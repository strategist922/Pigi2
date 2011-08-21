package pigi.framework.general.descriptors.configuration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IndexConfigurationList {
	Map<String, IndexConfiguration> indexconfigurations;

	public void setConfigurations(List<IndexConfiguration> indexConfigurations) {
		indexconfigurations = new HashMap<String, IndexConfiguration>();

	    for (IndexConfiguration c : indexConfigurations) {
		    indexconfigurations.put(c.getDatatablename(), c);
		}
	}

	public Map<String, IndexConfiguration> getConfigurations() {
		return indexconfigurations;
	}

	public List<IndexConfiguration> forTableName(String tableName) {
		IndexConfiguration c = indexconfigurations.get(tableName);
		if (c == null) {
		    return Arrays.asList();
		} else {
	        return Arrays.asList(c);
		}
	}

	public void setIndexconfigurations(List<IndexConfiguration> indexConfigurations) {
	    indexconfigurations = new HashMap<String, IndexConfiguration>();

	    for (IndexConfiguration c : indexConfigurations) {
		    indexconfigurations.put(c.getDatatablename(), c);
		}
	}
}
 