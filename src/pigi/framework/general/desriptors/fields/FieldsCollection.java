package pigi.framework.general.desriptors.fields;

import java.util.HashMap;
import java.util.Map;

/**
 * this class represents collection of FieldDescriptors
 * @author acure, vpatryshev
 *
 * @param <T>
 */
public class FieldsCollection<T extends Field> {
	private Map<String, T> descriptors = new HashMap<String, T>(); // descriptors by name
    private final static FieldsCollection<Field> EMPTY = 
        new FieldsCollection<Field>();
    
    @SuppressWarnings("unchecked")
    public final static <T extends Field> FieldsCollection<T> empty() {
        return (FieldsCollection<T>) EMPTY;
    }
	
	public FieldsCollection() {}

	public FieldsCollection(T... descriptors) {
        for (T descriptor : descriptors) {
            add(descriptor);
        }    
	}

    public void add(T fieldDescriptor) {
		descriptors.put(fieldDescriptor.getName(), fieldDescriptor);
	}

    public Map<String, T> fieldDescriptors() {
        return descriptors;
    }

}
