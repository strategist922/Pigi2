package pigi.framework.general.descriptors;

import java.util.List;

import pigi.framework.general.desriptors.fields.FieldOrder;
import pigi.framework.general.indexes.ExtendedIndex;
import pigi.framework.general.indexes.Index;
import pigi.framework.general.indexes.IndexException;
import pigi.framework.general.vo.DataObject;

public class ExtendedIndexDescriptor<T extends DataObject> extends IndexDescriptor<T> {

    public ExtendedIndexDescriptor(String dataTableName,
            int pageSize, List<IndexedField> indexProperties, List<FieldOrder> orderFields) {
        super(dataTableName, pageSize, indexProperties, orderFields);
    }

    @Override
    public Index<T> getIndex(DataDescriptor<T> objectDescriptor) throws IndexException{
        return new ExtendedIndex<T>(this, objectDescriptor);
    }

}
