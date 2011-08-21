package pigi.framework.general.desriptors.fields;

import pigi.framework.general.Direction;

/**
 * single field descriptor 
 * @author acure
 *
 */
public class Field {
	private String name;		// name of field

	public Field(String name) {
		this.name = name;
	}

    public String getName() {
		return name;
	}

	public FieldOrder withOrder(Direction order) {
	    return new FieldOrder(this, order);
	}

    public FieldOrder ascending() {
        return withOrder(Direction.ASC);
    }

    public FieldOrder descending() {
        return withOrder(Direction.DESC);
    }
	
}
