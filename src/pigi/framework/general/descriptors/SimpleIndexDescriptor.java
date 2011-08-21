package pigi.framework.general.descriptors;

import java.util.Collection;
import java.util.List;

import pigi.framework.general.desriptors.fields.FieldOrder;
import pigi.framework.general.indexes.Index;
import pigi.framework.general.indexes.IndexException;
import pigi.framework.general.indexes.SimpleIndex;
import pigi.framework.general.vo.DataObject;

public class SimpleIndexDescriptor<T extends DataObject> extends IndexDescriptor<T> {

    public SimpleIndexDescriptor(String dataTableName,
            int pageSize, Collection<? extends IndexedField> indexProperties, List<FieldOrder> orderFields) {
        super(dataTableName, pageSize, indexProperties, orderFields);
    }

    @Override
    public Index<T> getIndex(DataDescriptor<T> dataDescriptor)
            throws IndexException {
        return new SimpleIndex<T>(this, dataDescriptor);
    }
}
