package pigi.framework.general.descriptors;

import java.util.Arrays;
import java.util.Collection;

import pigi.framework.general.desriptors.fields.Field;
import pigi.framework.general.indexes.properties.IndexProperty;
import pigi.framework.general.indexes.properties.SingleFieldIndexProperty;
import pigi.framework.general.vo.DataObject;

public class ValueField extends IndexedField {

    private Field field;

    private ValueField(String name, Field field) {
        super(name);
        this.field = field;
    }

    public ValueField(Field field) {
        this(field.getName(), field);
    }
    
    @Override
    public IndexProperty getProperty(DataObject object) {
        return new SingleFieldIndexProperty(this, object);
    }

    @Override
    public Collection<Field> getFields() {
        return Arrays.asList(field);
    }

}
