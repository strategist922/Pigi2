package pigi.framework.general.vo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

@SuppressWarnings("serial")
public class DataObject implements Serializable {
	protected String id;
	protected long timestamp;
	private Map<String, String> fields;

	//FIXME: trzeba pewnie zmodyfikowac konstruktory po dodaniu timestamp'a
	public DataObject() {
	    this((String) null);
	}
			
	public DataObject(String id) {
		this(id, new HashMap<String, String>());
	}

	public DataObject(String id, Map<String, String> fields) {
		this.id = id;
		this.fields = fields;
	}

	public DataObject(DataObject source) {
		this(source.getId(), source.getFields());
	}

	public String setId(String id) {
		return this.id = id;
	}

	public Map<String, String> getFields() {
		return fields;
	}

	public String getId() {
		return id;
	}

	public String getField(String name) {
	    return fields.containsKey(name) ? fields.get(name) : fields.get(name + "");
	}
	
	public int getInt(String name) {
	    return Integer.parseInt(getField(name));
	}

	public void addField(String name, String value) {
		fields.put(name, value);
	}

	public void setFields(Map<String, String> fields) {
		this.fields = fields;
	}
	
	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder("id: " + getId());
		for (Entry<String, String> entry : getFields().entrySet()) {
			result.append("\n ").append(entry.getKey()).append("=").append(entry.getValue());
		}

		return result.toString();
	}

    public <T extends DataObject> T turnTo(Class<T> targetClass) {
        return Factory.build(targetClass, this);
    }
}
