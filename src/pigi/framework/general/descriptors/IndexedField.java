package pigi.framework.general.descriptors;


import java.util.Collection;
import java.util.Map;

import pigi.framework.general.desriptors.fields.Field;
import pigi.framework.general.idgenerators.Keys;
import pigi.framework.general.indexes.properties.IndexProperty;
import pigi.framework.general.vo.DataObject;

/**
 * index property descriptor - for index descriptor
 * 
 * @author akozielewski, vpatryshev
 * 
 */
public abstract class IndexedField implements ValueToKeyConverter, Comparable<IndexedField> {
	private String name;
	
	protected IndexedField(String name) {
		this.name = name;
	}

	abstract public Collection<Field> getFields();

	public String getName() {
		return name;
	}
	
	public abstract IndexProperty getProperty(DataObject object);

	public int compareTo(IndexedField o) {
		return name.compareTo(o.getName());
	}

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof IndexedField && name.equals(((IndexedField) obj).name);
    }

    public String valueToKey(Map<String, String> properties) {
        return Keys.normalize(properties.get(getName()));
    }
}
