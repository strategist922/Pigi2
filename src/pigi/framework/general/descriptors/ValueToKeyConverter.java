package pigi.framework.general.descriptors;

import java.util.Map;

public interface ValueToKeyConverter {
    
    public abstract String valueToKey(Map<String, String> properties);

}
