package pigi.framework.general.descriptors;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import pigi.framework.general.desriptors.fields.Field;
import pigi.framework.general.desriptors.fields.FieldOrder;
import pigi.framework.general.idgenerators.Keys;
import pigi.framework.general.indexes.Index;
import pigi.framework.general.indexes.IndexException;
import pigi.framework.general.vo.DataObject;


/**
 * describes single index - object of this class contains all data for creating and managing single index 
 * @author acure, vpatryshev
 *
 */
public abstract class IndexDescriptor<T extends DataObject> {
	private String name;
	private String indexTableName;
	private String pagesTableName;
	private String countersTableName;
	private int pageSize;
	private HashMap<String, Field> relatedFields;
	private Collection<? extends IndexedField> indexProperties;
	private List<FieldOrder> orderFields;

    protected IndexDescriptor(String dataTableName, int pageSize, Collection<? extends IndexedField> indexProperties, List<FieldOrder> orderFields) {
        String filter = Keys.fieldsNamesJoined(indexProperties);
        String order = Keys.orderFieldNamesJoined(orderFields);
        this.name = Keys.indexName(dataTableName, filter, order);
        String tablenamePrefix = name + Keys.INDEX_NAME_SEPARATOR + pageSize + Keys.INDEX_NAME_SEPARATOR; 
        this.indexTableName    = tablenamePrefix + "index";
        this.pagesTableName    = tablenamePrefix + "pages";
        this.countersTableName = tablenamePrefix + "counters";
		this.pageSize = pageSize;
		this.indexProperties   = indexProperties;
		this.orderFields = orderFields;
		this.relatedFields = new HashMap<String,Field>();
		for(IndexedField iField : indexProperties){
			for(Field f : iField.getFields()){
				relatedFields.put(f.getName(), f);
			}
		}
		for(int i = 0; i < orderFields.size(); i ++){
			relatedFields.put(orderFields.get(i).getName(), orderFields.get(i));
		}		
	}

    public String countersTableName() {
		return countersTableName;
	}

	public String indexTableName() {
		return indexTableName;
	}

	public String getName() {
		return name;
	}

	public String pagesTableName() {
		return pagesTableName;
	}

	public Collection<? extends IndexedField> getIndexProperties() {
		return indexProperties;
	}

	public List<FieldOrder> getOrderFields() {
		return orderFields;
	}

	public int pageSize() {
		return pageSize;
	}

	public abstract Index<T> getIndex(DataDescriptor<T> dataDescriptor) throws IndexException;

    public boolean hasField(String fieldName) {
        return relatedFields.containsKey(fieldName);
    }

}
