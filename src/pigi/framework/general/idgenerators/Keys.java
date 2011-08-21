package pigi.framework.general.idgenerators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import pigi.framework.general.Direction;
import pigi.framework.general.GeneralConstants;
import pigi.framework.general.descriptors.IndexDescriptor;
import pigi.framework.general.descriptors.IndexedField;
import pigi.framework.general.descriptors.ValueField;
import pigi.framework.general.descriptors.ValueToKeyConverter;
import pigi.framework.general.desriptors.fields.Field;
import pigi.framework.general.desriptors.fields.FieldOrder;
import pigi.framework.general.vo.DataObject;
import pigi.framework.tools.GUIDGenerator;
import pigi.framework.tools.Strings;

/**
 * id generation tool
 * 
 * @author acure, vpatryshev
 * 
 */
public class Keys {
	public static final String INDEX_NAME_SEPARATOR = "..";
    public static final String FIRST_KEY = "" + GeneralConstants.minChar;
    public static final String LAST_KEY = "" + GeneralConstants.maxChar;
    public static final String ESCAPED_NAME_SEPARATOR = INDEX_NAME_SEPARATOR.replaceAll("\\.", "\\\\.");
    
    /**
	 * creates new Id
	 * 
	 * @return
	 */
	public String newId() {
		return GUIDGenerator.getNext();
	}

	/**
	 * returns max value of vo id
	 * 
	 * @return
	 */
	public static String maxId() {
		return GUIDGenerator.getMaxGUID();
	}

	/**
	 * returns min value of vo id
	 * 
	 * @return
	 */
	public static String minId() {
		return GUIDGenerator.getMinGUID();
	}

	/**
	 * minimum page nr
	 * 
	 * @return
	 */
	public int minPageNo() {
		return Integer.MIN_VALUE;
	}

	/**
	 * maximum page nr
	 * 
	 * @return
	 */
	public int maxPageNo() {
		return Integer.MAX_VALUE;
	}

	public static String normalize(String inputText) {
        if (inputText == null) inputText = "";
        return inputText + GeneralConstants.minChar;
    }

	public String generateIdOrderValuesPart(IndexDescriptor<?> indexDescriptor, Map<String, String> fields) {
		return buildKeyPartFromValues(fields, indexDescriptor.getOrderFields());
	}

	public String generateIdValuesPart(IndexDescriptor<?> indexDescriptor, Map<String, String> properties) {
        return buildKeyPartFromValues(properties, indexDescriptor.getIndexProperties());
	}

    private String buildKeyPartFromValues(Map<String, String> properties, Iterable<? extends ValueToKeyConverter> indexes) {
        List<String> elements = new ArrayList<String>();

        for (ValueToKeyConverter d : indexes) {			
			elements.add(d.valueToKey(properties));
		}
		return Strings.join(GeneralConstants.ID_SUBPART_SEPARATOR, elements);
    }

    public <T extends DataObject> List<String> generateIdValuesParts(IndexDescriptor<T> indexDescriptor, DataObject object) {
		List<String> resultList = new ArrayList<String>();
		for (Map<String, String> elemsMap : objectToPropertiesValues(indexDescriptor, object)) {
			resultList.add(generateIdValuesPart(indexDescriptor, elemsMap));
		}
		return resultList;
	}

	private <T extends DataObject> List<Map<String, String>> objectToPropertiesValues(IndexDescriptor<T> indexDescriptor, DataObject object) {
		Map<String, List<String>> propertiesValues = new HashMap<String, List<String>>();
		for (IndexedField propDescr : indexDescriptor.getIndexProperties()) {
    		List<String> selectedPropertyValues = new ArrayList<String>();
			for (String propertyValue : propDescr.getProperty(object).values()) {
				selectedPropertyValues.add(propertyValue);
			}
			propertiesValues.put(propDescr.getName(), selectedPropertyValues);
		}
		return Strings.mix(propertiesValues);

	}

	/**
	 * return max field values for index
	 * 
	 * @param indexDescriptor
	 * @return
	 */
	public <T extends DataObject> Map<String, String> generateMaxOrderFieldsValues(IndexDescriptor<T> indexDescriptor) {
		Map<String, String> res = new HashMap<String, String>();
		for (FieldOrder field : indexDescriptor.getOrderFields()) {
			res.put(field.getName(), field.maxValue());
		}
		return res;
	}

    /**
	 * returns min fields values for index
	 * 
	 * @param indexDescriptor
	 * @return
	 */
	public <T extends DataObject> Map<String, String> generateMinOrderFieldsValues(IndexDescriptor<T> indexDescriptor) {
		Map<String, String> res = new HashMap<String, String>();
		for (FieldOrder field : indexDescriptor.getOrderFields()) {
            res.put(field.getName(), field.minValue());
		}
		return res;
	}

	/**
	 * returns max set of values for index
	 * 
	 * 
	 * @param indexDescriptor
	 * @return
	 */
	public <T extends DataObject> Map<String, String> generateMaxIndexedPropertiesValues(IndexDescriptor<T> indexDescriptor) {
		Map<String, String> res = new HashMap<String, String>();
		for (IndexedField property : indexDescriptor.getIndexProperties()) {
			res.put(property.getName(), LAST_KEY);
		}
		return res;
	}

	/**
	 * returns min values for index properites
	 * 
	 * @param indexDescriptor
	 * @return
	 */
	public <T extends DataObject> Map<String, String> generateMinIndexedPropertiesValues(IndexDescriptor<T> indexDescriptor) {
		Map<String, String> res = new HashMap<String, String>();
		for (IndexedField property : indexDescriptor.getIndexProperties()) {
			res.put(property.getName(), FIRST_KEY);
		}
		return res;
	}

    public static String indexName(String dataTableName, String filter, String order) {
        return Strings.join(INDEX_NAME_SEPARATOR, dataTableName, filter,  order);
    }

    public static String indexName(String dataTableName, Map<String, String> properties, List<FieldOrder> orderedBy) {
        return indexName(dataTableName, filterName(properties), orderFieldNamesJoined(orderedBy));
    }

    public static String filterName(Map<String, String> properties) {
        Set<String> names = new TreeSet<String>(properties.keySet());
        return Strings.join("-", names);
    }

    public static String orderFieldNamesJoined(Iterable<FieldOrder> fields) {
        StringBuilder buf = new StringBuilder();
        for (FieldOrder f : fields) {
            if (buf.length() > 0) buf.append("-");
            buf.append(f.getName()).append(".").append(f.direction().name());
        }
        return buf.toString();
    }

    public static String fieldsNamesJoined(Iterable<? extends IndexedField> indexProperties) {
        SortedSet<String> names = new TreeSet<String>();
        
        for (IndexedField descriptor : indexProperties) {
            for (Field f : descriptor.getFields()) {
                names.add(f.getName());
            }
        }
        return Strings.join("-", names);
    }

    public static List<IndexedField> parseFields(String fields) {
        String[] fieldStrings = fields.split("-");
        List<IndexedField> result = new ArrayList<IndexedField>();
        for (String field : fieldStrings) {
            if (!field.isEmpty()) result.add(new ValueField(new Field(field)));
        }
        return result;
        
    }

    public static List<FieldOrder> parseSortOrder(String s) {
        List<FieldOrder> result = new ArrayList<FieldOrder>();
        if (s.length() > 0) {
            String[] strings = s.split("-");
            for (String orderString : strings) {
                String[] split = orderString.split("\\.");
                if (split.length == 2) {
                    FieldOrder order = new FieldOrder(new Field(split[0]), Direction.valueOf(split[1]));
                    result.add(order);
                }
            }
        }
        return result;
    }

}
