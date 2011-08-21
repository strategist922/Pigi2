package pigi.framework.general.vo;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;

public class Factory {

    public static <T extends DataObject> T build(
            Class<T> targetClass, 
            String key,
            Map<String, String> properties) {
        DataObject object = new DataObject(key);
        object.setFields(properties);
        return build(targetClass, object);
    }

    public static <T extends DataObject> T build(Class<T> targetClass, DataObject source) {
        try {
            for (Constructor<T> c: Factory.forDataObject(targetClass)) {
                return c.newInstance(source);
            }
            for (Constructor<T> c: Factory.forIdAndMap(targetClass)) {
                return c.newInstance(source.getId(), source.getFields());
            }
            for (Constructor<T> c: Factory.forId(targetClass)) {
                T x = c.newInstance(source.getId());
                x.setFields(source.getFields());
                return x;
            }
    		T x = targetClass.newInstance();
    		x.setFields(source.getFields());
    		x.setId(source.getId());
    		return x;
    	} catch (InstantiationException e) {
    		throw new RuntimeException(e);			
    	} catch (IllegalAccessException e) {
    		throw new RuntimeException(e);
    	} catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends DataObject> Iterable<Constructor<T>> forId(Class<T> dataClass) {
        try {
            Constructor<T> c = dataClass.getConstructor(String.class);
            if (c != null) return Arrays.asList(c);
        } catch (Exception e) {
            
        }
        return Arrays.asList();
    }

    @SuppressWarnings("unchecked")
    private static <T extends DataObject> Iterable<Constructor<T>> forIdAndMap(Class<T> dataClass) {
        try {
            Constructor<T> c = dataClass.getConstructor(String.class, Map.class);
            if (c != null) return Arrays.asList(c);
        } catch (Exception e) {
            
        }
        return Arrays.asList();
    }

    @SuppressWarnings("unchecked")
    private static <T extends DataObject> Iterable<Constructor<T>> forDataObject(Class<T> dataClass) {
        try {
            Constructor<T> c = dataClass.getConstructor(DataObject.class);
            if (c != null) return Arrays.asList(c);
        } catch (Exception e) {
            
        }
        return Arrays.asList();
    }

}
