package pigi.framework.general.descriptors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;

import pigi.framework.general.desriptors.fields.Field;
import pigi.framework.general.desriptors.fields.FieldOrder;
import pigi.framework.general.desriptors.fields.FieldsCollection;
import pigi.framework.general.idgenerators.Keys;
import pigi.framework.general.indexes.Index;
import pigi.framework.general.indexes.IndexException;
import pigi.framework.general.logic.DbLogic;
import pigi.framework.general.logic.DbLogicException;
import pigi.framework.general.views.query.PageQuery;
import pigi.framework.general.views.results.PageQueryResult;
import pigi.framework.general.vo.DataObject;
import pigi.framework.general.vo.Factory;
import pigi.framework.tools.HTableFactory;
import pigi.framework.tools.HTableFactoryException;
import pigi.framework.tools.RowTool;
import pigi.framework.tools.RowToolException;
import pigi.framework.tools.TableAdmin;

/**
 * 
 * @author acure, vpatryshev
 * 
 */
public class DataDescriptor<T extends DataObject> {
	protected static final List<FieldOrder> NO_ORDER = DataDescriptor.<FieldOrder, FieldOrder>listOf();
    private String dataTableName;
    private String familyName;
	protected Map<String, IndexDescriptor<T>> indexDescriptors;
    private DbLogic<T> logic;
    protected Class<T> dataClass;
    private TableAdmin<T> admin;
    // TODO(vlad): add "validity filter" that would, while rescanning, kick out the invalid data, like marked "deleted" in our case

	/**
	 * create descriptor
	 * @param dataTableName
	 * @param dataClass TODO
	 * @param name
	 * @throws DbLogicException 
	 */
	public DataDescriptor(String dataTableName, String familyName, Class<T> dataClass) {
	    this.dataTableName = dataTableName;
        this.familyName = familyName;
        this.dataClass = dataClass;
		this.indexDescriptors = new HashMap<String, IndexDescriptor<T>>();
		dbLogic(dataTableName, familyName);
        admin = new TableAdmin<T>(this);
    }

    private DbLogic<T> dbLogic(String dataTableName, String familyName) {
        try {
            return new DbLogic<T>(this);
        } catch (DbLogicException e) {
            throw new IllegalArgumentException("Could not find table " + dataTableName + " with column " + familyName,
                    e);
        }
    }

	public DataDescriptor(String dataTableName, Class<T> dataClass) {
	    this(dataTableName, dataTableName + "family", dataClass);
	}

	public DataDescriptor(String dataTableName, Class<T> dataClass,
            FieldsCollection<Field> fieldDescriptors,
            Map<String, IndexDescriptor<T>> indexDescriptors) {
	    this(dataTableName, dataClass);
	    this.indexDescriptors = indexDescriptors;
    }

    public void addIndex(IndexDescriptor<T> indexDescriptor) {
		if (indexDescriptors.containsKey(indexDescriptor.getName())) {
			throw new IllegalArgumentException(" duplicate index \""
					+ indexDescriptor.getName() + "\" for table " + dataTableName 
					+ " columnFamily " + familyName);
		}
		this.indexDescriptors.put(indexDescriptor.getName(), indexDescriptor);
	}
    
    public void addSimpleIndex(int pageSize, Collection<? extends IndexedField> indexedFields, List<FieldOrder> orderFields) {
        if (indexedFields == null) throw new NullPointerException("index properties can't be null");
        if (orderFields == null) throw new NullPointerException("order fields can't be null");
        addIndex(new SimpleIndexDescriptor<T>(tableName(), pageSize, indexedFields, orderFields));
    }
    
    public void addExtendedIndex(int pageSize, List<IndexedField> indexedFields, List<FieldOrder> orderFields) {
        addIndex(new ExtendedIndexDescriptor<T>(tableName(), pageSize, indexedFields, orderFields));
    }

	public Iterable<IndexDescriptor<T>> allIndexDescriptors() {
		return Collections.<IndexDescriptor<T>>unmodifiableCollection(indexDescriptors.values());
	}

	public List<String> allIndexNames() {
		return new ArrayList<String>(indexDescriptors.keySet());
	}

	public String familyName() {
		return familyName;
	}

	public Field addField(Field field) {
		return field;
	}

    public Field addField(String name) {
        return addField(new Field(name));
    }

	public IndexDescriptor<T> getIndexDescriptor(String name) {
		return indexDescriptors.get(name);
	}

	public String tableName() {
		return dataTableName;
	}

	public Index<T> getIndex(String name)
			throws IndexException {
		IndexDescriptor<T> descriptor = getIndexDescriptor(name);
		if (descriptor == null) {
		    throw new IndexException("No index for " + name);
		}
        return descriptor.getIndex(this);
	}

	public DbLogic<T> getLogic() throws DbLogicException {
	    if (logic == null) {
	        logic = new DbLogic<T>(this);
	    }
		return logic;
	}

	public void rebuildIndexes() throws HTableFactoryException, IOException, RowToolException, DbLogicException, IndexException {
        admin.createIndexTables();
	    HTable table = new HTableFactory().getHTable(dataTableName);
	    RowTool rowTool = new RowTool();
	    ResultScanner scanner = table.getScanner(new Scan(Keys.minId().getBytes(), Keys.maxId().getBytes()));
        try {
            for (Result r : scanner) {
                Map<String, String> map = rowTool.resultToMap(r);
                getLogic().addToIndexes(Factory.build(dataClass, new String(r.getRow()), map));
            }
        } finally {
            scanner.close();
        }
    }

    public T buildData(DataObject source) {
        return Factory.build(dataClass, source);
    }

    public List<T> findRange(
            Map<String, String> properties, 
            long offset,
            int count,
            List<FieldOrder> orderedBy) throws IndexException {
        String indexName = Keys.indexName(dataTableName, properties, orderedBy);
        Index<T> index = getIndex(indexName);
        List<T> result = new ArrayList<T>(count);
        int pageSize = index.pageSize();
        int firstPage = (int) (offset / pageSize);
        int lastPage = (int) ((offset + count + pageSize - 1) / pageSize);
        for (int pageNo = firstPage; pageNo <= lastPage; pageNo++) {
            PageQuery<T> query = new PageQuery<T>(index, properties, pageNo);
            PageQueryResult<T> page = index.getPage(query);
            lastPage = Math.min(lastPage, page.getMaxPage());
            long current = (long) pageNo * pageSize;
            List<T> pageData = page.getObjects();
            int first = Math.max(0, (int)(offset - current));
            int last = Math.min(pageData.size(), (int) (offset + count - current));
            result.addAll(pageData.subList(first, last));
        }
        return result;
    }

    public PageQueryResult<T> find(
            Map<String, String> properties, 
            int pageNo, 
            List<FieldOrder> orderedBy) throws IndexException {
        String indexName = Keys.indexName(dataTableName, properties, orderedBy);
        Index<T> index = getIndex(indexName);
        PageQuery<T> query = new PageQuery<T>(index, properties, pageNo);
        return index.getPage(query);
    }

    public void createTables() {
        admin.createTables();
    }

    public void dropTables() {
        admin.dropTables();
    }
    
    @SuppressWarnings("unchecked")
    public static <T, S extends T> List<T> listOf(S... items) {
        return (List<T>) Arrays.asList(items);
    }
}
