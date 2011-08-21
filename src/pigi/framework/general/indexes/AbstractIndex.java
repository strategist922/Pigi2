package pigi.framework.general.indexes;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.client.HTable;

import pigi.framework.general.descriptors.DataDescriptor;
import pigi.framework.general.descriptors.IndexDescriptor;
import pigi.framework.general.idgenerators.GeneralFieldsCountersIdGenerator;
import pigi.framework.general.idgenerators.GeneralFieldsIndexIdGenerator;
import pigi.framework.general.idgenerators.IdGeneratorForIndexes;
import pigi.framework.general.views.query.Query;
import pigi.framework.general.views.results.IterableResult;
import pigi.framework.general.vo.DataObject;
import pigi.framework.tools.HTableFactory;
import pigi.framework.tools.HTableFactoryException;
import pigi.framework.tools.RowTool;
import pigi.framework.tools.RowToolException;

/**
 * Abstract class for index
 * @author kgalecki, vpatryshev
 *
 */
public abstract class AbstractIndex<T extends DataObject> implements Index<T> {
    protected transient Log logger = LogFactory.getLog(getClass());
	protected IndexDescriptor<T> indexDescriptor;
	protected DataDescriptor<T> dataDescriptor;
    protected HTableFactory tableFactory;
    protected HTable indexTable;
    protected HTable pagesTable;
    protected HTable countersTable;
    protected RowTool tableTool;

	public AbstractIndex(IndexDescriptor<T> indexDescriptor, DataDescriptor <T> dataDescriptor) throws IndexException {
		this.indexDescriptor = indexDescriptor;
		this.dataDescriptor = dataDescriptor;
        tableFactory = new HTableFactory();
        try {
            this.indexTable = tableFactory.getHTable(indexDescriptor.indexTableName());
            this.pagesTable = tableFactory.getHTable(indexDescriptor.pagesTableName());
            this.countersTable = tableFactory.getHTable(indexDescriptor.countersTableName());
        } catch (HTableFactoryException e) {
            throw new IndexException(e);
        }
        this.tableTool = new RowTool();
	}

	abstract Map<String, String> prepareIndexFields(DataObject object) throws IndexException;

    protected List<String> counterIds(T object) {
        return new GeneralFieldsCountersIdGenerator<T>(dataDescriptor.getIndexDescriptor(getName())).getIds(object);
    }

    protected List<String> indexIds(T object) {
        return new GeneralFieldsIndexIdGenerator<T>(dataDescriptor.getIndexDescriptor(getName())).getIds(object);
    }

    public String getName() {
    	return indexDescriptor.getName();
    }

    protected void insertIndexes(T object) throws RowToolException, IndexException {
        for (String indexId : indexIds(object)) {
        	// insert index
        	logger.debug(" insert : " + getName() + " : id = " + object.getId() + " - indexId = " + indexId);
        	tableTool.insert(indexTable, indexId, prepareIndexFields(object));
        }
    }

    protected void increaseCounts(T object) throws RowToolException {
        for (String countersId : counterIds(object)) {
            int count = tableTool.getInt(countersTable, countersId, "childrenCount", 0) + 1;
            Map<String, String> map = Collections.singletonMap("childrenCount", count + "");
        	tableTool.insert(countersTable, countersId, map);
        }
    }

    public void insert(T object) throws IndexException {
    	try {
            insertIndexes(object);
    		increaseCounts(object);
    	} catch (RowToolException e) {
    		throw new IndexException(e);
    	}
    }

    protected void deleteIndexes(T object) throws RowToolException {
        for (String indexId : indexIds(object)) {
        	logger.debug(" element remove : index : " + getName() + " - id : " + indexId);
        	tableTool.delete(indexTable, indexId);
        }
    }

    protected void decreaseCounts(T object) throws RowToolException {
        for (String countersId : counterIds(object)) {
        	logger.debug(" counters decrease : " + countersId);
        	int count = tableTool.getInt(countersTable, countersId, "childrenCount", 0) - 1;
        	Map<String, String> map = Collections.singletonMap("childrenCount", count + "");
        	tableTool.insert(countersTable, countersId, map);
        }
    }

    public void delete(T object) throws IndexException {
    	try {
    	    deleteIndexes(object);
            decreaseCounts(object);
    	} catch (RowToolException e) {
    		throw new IndexException(e);
    	}
    }
    
    protected abstract Iterator<T> resultIterator(String firstId, String lastId) throws RowToolException;

    public IterableResult<T> getAll(Query<T> query) throws IndexException {
    	try {
    		IdGeneratorForIndexes indexIdGenerator = new GeneralFieldsIndexIdGenerator<T>(indexDescriptor);		
    		String firstId = indexIdGenerator.getFirstPossibleId(query.getProperties());
    		String lastId = indexIdGenerator.getLastPossibleId(query.getProperties());	
    		return new IterableResult<T>(resultIterator(firstId, lastId));
    	} catch (RowToolException e) {
    		throw new IndexException(e);
    	}
    }

    public int pageSize() {
        return indexDescriptor.pageSize();
    }
}
