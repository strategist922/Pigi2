package pigi.framework.general.idgenerators;

import java.util.List;
import java.util.Map;

import pigi.framework.general.vo.DataObject;

/**
 * id generator for counters table
 */
public interface IdGeneratorForCounters {
	public List<String> getIds(DataObject object);

	public String getId(Map<String, String> properties);
}
