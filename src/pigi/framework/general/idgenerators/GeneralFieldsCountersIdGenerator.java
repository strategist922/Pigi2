package pigi.framework.general.idgenerators;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pigi.framework.general.GeneralConstants;
import pigi.framework.general.descriptors.IndexDescriptor;
import pigi.framework.general.vo.DataObject;

/**
 *  Simple identifiers generator for counters table 
 */
public class GeneralFieldsCountersIdGenerator <T extends DataObject> implements IdGeneratorForCounters {
	private Keys idTool;
	private IndexDescriptor<T> indexDescriptor;

	public GeneralFieldsCountersIdGenerator(IndexDescriptor<T> indexDescriptor) {
		this.indexDescriptor = indexDescriptor;
		this.idTool = new Keys();
	}

	public List<String> getIds(DataObject object) {
		List<String> res = new ArrayList<String>();
		String deforder = GeneralConstants.ID_PART_SEPARATOR;
		for (String valuesPart : idTool.generateIdValuesParts(indexDescriptor, object)) {
			res.add(deforder + valuesPart);
		}
		return res;
	}

	public String getId(Map<String, String> propertyValues) {
		return GeneralConstants.ID_PART_SEPARATOR
		     + idTool.generateIdValuesPart(indexDescriptor, propertyValues);
	}

}
