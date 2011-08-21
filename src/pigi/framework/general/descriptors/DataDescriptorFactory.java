package pigi.framework.general.descriptors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.annotations.VisibleForTesting;

import pigi.framework.general.Direction;
import pigi.framework.general.descriptors.configuration.IndexConfiguration;
import pigi.framework.general.descriptors.configuration.IndexConfigurationList;
import pigi.framework.general.descriptors.configuration.HbaseIndexConfigReader;
import pigi.framework.general.descriptors.configuration.Index;
import pigi.framework.general.descriptors.IndexedField;
import pigi.framework.general.desriptors.fields.Field;
import pigi.framework.general.desriptors.fields.FieldsCollection;
import pigi.framework.general.desriptors.fields.FieldOrder;
import pigi.framework.general.vo.DataObject;

public class DataDescriptorFactory <T extends DataObject>{

	@VisibleForTesting
	HbaseIndexConfigReader configReader = new HbaseIndexConfigReader();

	public IndexConfigurationList getAllConfigurations() {
		return configReader.getIndexConfigurations();
	}

	private void getIndexesFromConfig(DataDescriptor<T> descriptor, List<Index> indexes, Map<String, Field> fieldMap, String dataTableName) {

		for (Index index : indexes) {
			List<IndexedField> indexProperties = getIndexPropertiesFromConfig(index
					.getIndexproperties(), fieldMap);
			List<FieldOrder> orderFields = getOrderFieldsFromConfig(index
					.getOrderfields(), fieldMap);

			int pageSize = index.getPagesize();
			descriptor.addSimpleIndex(pageSize, indexProperties, orderFields);
		}

	}

	// TOOD: Jasmine, since field descriptors are not needed in pigi, could you figure out if you can do without them too?
	private List<IndexedField> getIndexPropertiesFromConfig(
			List<String> indexProperties,
			Map<String, Field> fieldMap) {
		List<IndexedField> indexPropertyDescriptors = new ArrayList<IndexedField>();
		for (String indexProperty : indexProperties) {
			Field field = fieldMap.get(indexProperty);
			indexPropertyDescriptors.add(new ValueField(field));
		}
		return indexPropertyDescriptors;
	}

	private List<FieldOrder> getOrderFieldsFromConfig(
			Map<String, String> source, Map<String, Field> fieldMap) {
		List<FieldOrder> orderFieldDescriptors = new ArrayList<FieldOrder>();
		for (Entry<String, String> entry : source.entrySet()) {
			Field field = fieldMap.get(entry.getKey());
			orderFieldDescriptors.add(new FieldOrder(field,
					Direction.valueOf(entry.getValue().toUpperCase())));
		}
		return orderFieldDescriptors;
	}

	// TODO: Jasmine, I believe we can just build these fields when they are being used; no need to create a map at all
	private FieldsCollection<Field> getFieldDescriptiorsfromConfig(Iterable<String> names) throws NumberFormatException {
		FieldsCollection<Field> fieldDescriptors = new FieldsCollection<Field>();
		for (String name : names) {
			fieldDescriptors.add(new Field(name));
		}

		return fieldDescriptors;
	}

    public DataDescriptor<T> build(String dataTableName, Class<T> dataClass) {
        DataDescriptor<T> descriptor = new DataDescriptor<T>(dataTableName, dataClass);
        List<IndexConfiguration> configurations = configReader.getIndexConfigurations().forTableName(dataTableName);
        for (IndexConfiguration configuration : configurations) {
            FieldsCollection<Field> fieldDescriptors = getFieldDescriptiorsfromConfig(configuration.getFields());
            Map<String, Field> fieldMap = fieldDescriptors.fieldDescriptors();
            getIndexesFromConfig(descriptor, configuration.getIndexes(), fieldMap, dataTableName);
        }
        return descriptor;
    }

}
