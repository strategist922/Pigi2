package pigi.framework.general.descriptors;

import java.util.Arrays;
import java.util.Collection;

import pigi.framework.general.desriptors.fields.Field;
import pigi.framework.general.indexes.properties.IndexProperty;
import pigi.framework.general.indexes.properties.RangeValueIndexProperty;
import pigi.framework.general.vo.DataObject;

public class RangeField extends IndexedField {

    private Field firstField;
    private Field lastField;

    public RangeField(String name, Field firstField, Field lastField) {
        super(name);
        this.firstField = firstField;
        this.lastField = lastField;
    }
    
    @Override
    public IndexProperty getProperty(DataObject object) {
        return new RangeValueIndexProperty(this, object);
    }

    public Field firstField() {
        return firstField;
    }

    public Field lastField() {
        return lastField;
    }
    
    @Override
    public Collection<Field> getFields() {
        return Arrays.asList(firstField, lastField);
    }

}
