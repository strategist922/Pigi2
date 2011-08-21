package pigi.framework.general.views.query;

import java.util.Map;

import pigi.framework.general.indexes.Index;
import pigi.framework.general.vo.DataObject;

/**
 * Basic query 
 */
public class Query<T extends DataObject> {
	protected Map<String, String> properties;
    protected Index<T> index;
	
	public Query(Index<T> index, Map<String,String> properties) {
	    this.index = index;
		this.properties = properties;
	}
	
	public Map<String, String> getProperties() {
		return properties;
	}	
}
