package pigi.framework.general.desriptors.fields;

import java.util.Map;

import pigi.framework.general.Direction;
import pigi.framework.general.descriptors.ValueToKeyConverter;
import pigi.framework.general.idgenerators.Keys;
import pigi.framework.tools.Strings;

/**
 * descriptor of ordered field - that is a field descriptor with order definition 
 * @author acure, vpatryshev
 *
 */
public class FieldOrder extends Field implements ValueToKeyConverter {
	private Direction direction;

	/**
	 * 
	 * @param field
	 * @param direction
	 */
	public FieldOrder(Field field, Direction direction) {
		super(field.getName());
		this.direction = direction;
	}

    public String valueToKey(Map<String, String> fields) {
        String valueToAdd = Keys.normalize(fields.get(getName()));
        return direction == Direction.DESC ? Strings.invert(valueToAdd) : valueToAdd;
    }

    public String toString() {
        return "OrderFieldDescriptor(" + getName() + "." + direction + ")";
    }

    public String maxValue() {
        return direction.last;
    }

    public String minValue() {
        return direction.first;
    }
    
    public Direction direction() {
        return direction;
    }
}
