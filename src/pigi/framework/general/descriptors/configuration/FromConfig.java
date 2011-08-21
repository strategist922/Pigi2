package pigi.framework.general.descriptors.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import pigi.framework.general.Direction;
import pigi.framework.general.PigiException;
import pigi.framework.general.descriptors.DataDescriptor;
import pigi.framework.general.descriptors.IndexDescriptor;
import pigi.framework.general.descriptors.IndexedField;
import pigi.framework.general.descriptors.SimpleIndexDescriptor;
import pigi.framework.general.descriptors.ValueField;
import pigi.framework.general.desriptors.fields.Field;
import pigi.framework.general.desriptors.fields.FieldOrder;
import pigi.framework.general.desriptors.fields.FieldsCollection;
import pigi.framework.general.vo.DataObject;

public class FromConfig implements DataDescriptorProvider {

    HbaseIndexConfigReader configReader;

    public FromConfig(HbaseIndexConfigReader configReader) {
        this.configReader = configReader;
    }

    public IndexConfigurationList getAllConfigurations() {
        return configReader.getIndexConfigurations();
    }

    private <T extends DataObject> Map<String, IndexDescriptor<T>> 
        getStrategiesFromConfig(List<Index> indexes, FieldsCollection<Field> fieldDescriptors, String dataTableName) {

        HashMap<String, IndexDescriptor<T>> indexStrategyDescriptors = new HashMap<String, IndexDescriptor<T>>();
        for (Index index : indexes) {

            List<IndexedField> indexProperties = getIndexFields(index
                    .getIndexproperties(), fieldDescriptors);
            List<FieldOrder> orderFields = getOrderFields(index
                    .getOrderfields(), fieldDescriptors);

            int pageSize = index.getPagesize();
            IndexDescriptor<T> indexDescriptor = new SimpleIndexDescriptor<T>(
                    dataTableName, pageSize, 
                    indexProperties, 
                    orderFields);
            indexStrategyDescriptors.put(indexDescriptor.getName(), indexDescriptor);
        }

        return indexStrategyDescriptors;
    }

    private List<IndexedField> getIndexFields(
            List<String> indexProperties,
            FieldsCollection<Field> fieldDescriptors) {
        List<IndexedField> indexFields = new ArrayList<IndexedField>();
        for (String indexProperty : indexProperties) {
            Field fieldDescriptor = fieldDescriptors.fieldDescriptors().get(indexProperty);
            // TODO : figure out what field types mean, now hard
            // coded to singleField
            indexFields.add(new ValueField(fieldDescriptor));
        }
        return indexFields;
    }

    private List<FieldOrder> getOrderFields(
            Map<String, String> source, FieldsCollection<Field> fieldDescriptors) {
        List<FieldOrder> orderFieldDescriptors = new ArrayList<FieldOrder>();
        for (Entry<String, String> entry : source.entrySet()) {
            Field fieldDescriptor = fieldDescriptors.fieldDescriptors().get(entry.getKey());
            orderFieldDescriptors.add(new FieldOrder(fieldDescriptor,
                    Direction.valueOf(entry.getValue().toUpperCase())));
        }
        return orderFieldDescriptors;
    }

    private FieldsCollection<Field> getFieldDescriptiorsfromConfig(Iterable<String> names) throws NumberFormatException {
        FieldsCollection<Field> fieldDescriptors = new FieldsCollection<Field>();
        for (String name : names) {
            fieldDescriptors.add(new Field(name));
        }

        return fieldDescriptors;
    }

    public <T extends DataObject> DataDescriptor<T> build(String dataTableName, Class<T> dataClass) 
            throws PigiException {
        List<IndexConfiguration> configurations = configReader.getIndexConfigurations().forTableName(dataTableName);
        for (IndexConfiguration configuration : configurations) {
            FieldsCollection<Field> fieldDescriptors = getFieldDescriptiorsfromConfig(configuration.getFields());
            Map<String, IndexDescriptor<T>> indexDescriptors = getStrategiesFromConfig(configuration.getIndexes(), fieldDescriptors, dataTableName);
            return new DataDescriptor<T>(dataTableName, dataClass, fieldDescriptors, indexDescriptors);
        }
        throw new PigiException("Failed bo build descriptor for " + dataTableName);
    }

}
