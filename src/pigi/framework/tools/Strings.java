package pigi.framework.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Strings {

    public static String invert(String s) {
    	StringBuilder result = new StringBuilder();
    	int size = s.codePointCount(0, s.length());
        for (int i = 0; i < size; i++) {
            int pos = s.offsetByCodePoints(0, i);
    		result.append((char) (Character.MAX_CODE_POINT - s.codePointAt(pos)));
    	}
    	return result.toString();
    }

    /**
     * String mixer tool - generates all combinations of passed grouped strings
     * for example for elements:
     * A: a1, a2
     * B: b1, b2, b3
     * result will be:
     * (a1, b1), (a1, b2), (a1, b3), (a2,b1), (a2, b2), (a2, b3)
     * This class is used to prepare index entries in case of multivalue properties.
     * Contains main method with simple example.
     */
    public static List<String> mix(Map<String, List<String>> strings, String separator) {
    	List<Map<String, String>> normalOutput = Strings.mix(strings);
    	List<String> output = new ArrayList<String>();
    
    	for (Map<String, String> list : normalOutput) {
    		StringBuilder result = new StringBuilder();
    		for (String s : list.values()) {
    			if (result.length() > 0) result.append(separator);
    			result.append(s);
    		}
    		output.add(result.toString());
    	}
    	return output;
    }

    public static List<Map<String, String>> mix(Map<String, List<String>> strings) {
    	int actualValue = 0;
    	int maxValue = 0;
    
    	List<Map<String, String>> result = new ArrayList<Map<String, String>>();
    
    	Map<String, Integer> actualIndexes = new HashMap<String, Integer>();
    	for (Entry<String, List<String>> inputMapEntry : strings.entrySet()) {
    		actualIndexes.put(inputMapEntry.getKey(), 0);
    		maxValue += inputMapEntry.getValue().size() - 1;
    	}
    
    	boolean incremented = true;
    	boolean initialIncremented = true;
    	while (incremented) {
    		if (!initialIncremented)
    			incremented = false;
    
    		Map<String, String> actualRow = new HashMap<String, String>();
    		for (Entry<String, List<String>> inputMapEntry : strings.entrySet()) {
    			List<String> list = inputMapEntry.getValue();
    			String iteratorKey = inputMapEntry.getKey();
    			int actualIndex = actualIndexes.get(iteratorKey);
    
    			actualRow.put(iteratorKey, list.get(actualIndex));
    
    			if (!incremented) {
    				if (list.size() - 1 > actualIndex) {
    					actualIndexes.put(iteratorKey, actualIndex + 1);
    					actualValue++;
    					incremented = true;
    					continue;
    				} else {
    					if (list.size() - 1 == actualIndexes.get(iteratorKey)) {
    						if (actualValue < maxValue)
    							actualValue -= actualIndexes.get(iteratorKey);
    						actualIndexes.put(iteratorKey, 0);
    					}
    				}
    			}
    		}
    
    		if (!initialIncremented)
    			result.add(actualRow);
    
    		initialIncremented = false;
    	}
    
    	return result;
    }
    
    public static String join(String separator, Iterable<?> items) {
        StringBuilder sb = new StringBuilder();
        for (Object item : items) {
            if (sb.length() > 0) sb.append(separator);
            sb.append(item);
        }
        
        return sb.toString();
    }
    
    public static String join(String separator, Object... items) {
        return join(separator, Arrays.asList(items));
    }

}
