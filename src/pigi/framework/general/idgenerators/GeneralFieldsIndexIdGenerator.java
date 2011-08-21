package pigi.framework.general.idgenerators;

import static pigi.framework.general.GeneralConstants.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pigi.framework.general.descriptors.IndexDescriptor;
import pigi.framework.general.vo.DataObject;


/*
 *   Id construnction :
 *   
 *                                         Property values              Order values
 *                                    __________/\____________  ___________/\____________
 *                                   /                        \/                         \                                    
 *                                   |                         |                          |  
 *    IndexName_P1,P2,P3_O1,O2,O3_Pv1.....,Pv2.....,Pv3....._Ov1.....,Ov2.....,Ov3....._ItemId
 *   |                              |                                                     |      |    
 *    \___________  _______________/ \_________________________  ________________________/ \_  _/     
 *                \/                                           \/                            \/    
 *         Definition Part                                Values Part                        Item Part
 *         
 *         
 *         P1, P2, P3  - Property short names
 *         O1, O2, O3  - Order property short names
 *         Ov1         - Order property value
 *         Pv1         - Property value
 *         ......	   - "space" fill the value to the full size of the property
 *         , and _ separators are defined in GeneralConstants
 *         
 *         
 */

public class GeneralFieldsIndexIdGenerator <T extends DataObject> implements IdGeneratorForIndexes {
	private Keys idTool;
	private IndexDescriptor<T> indexDescriptor;

	public GeneralFieldsIndexIdGenerator(IndexDescriptor<T> indexDescriptor) {
		this.indexDescriptor = indexDescriptor;
		this.idTool = new Keys();
	}

	public String getFirstPossibleId(Map<String, String> propertyValues) {
		return ID_PART_SEPARATOR
		     + idTool.generateIdValuesPart(indexDescriptor, propertyValues) 
		     + ID_PART_SEPARATOR
		     + idTool.generateIdOrderValuesPart(indexDescriptor, idTool.generateMinOrderFieldsValues(indexDescriptor)) 
		     + ID_PART_SEPARATOR
		     + Keys.minId();
	}

	public List<String> getIds(DataObject object) {
		List<String> res = new ArrayList<String>();
		String defStr = ID_PART_SEPARATOR;
		String orderStr = idTool.generateIdOrderValuesPart(indexDescriptor, object.getFields()) + ID_PART_SEPARATOR;
		for (String valuesPart : idTool.generateIdValuesParts(indexDescriptor, object)) {
			res.add(defStr + valuesPart + ID_PART_SEPARATOR + orderStr + object.getId());
		}
		return res;
	}

	public String getLastPossibleId(Map<String, String> propertyValues) {
		return ID_PART_SEPARATOR
		     + idTool.generateIdValuesPart(indexDescriptor, propertyValues) 
		     + ID_PART_SEPARATOR
		     + idTool.generateIdOrderValuesPart(indexDescriptor, idTool.generateMaxOrderFieldsValues(indexDescriptor)) 
		     + ID_PART_SEPARATOR
		     + Keys.maxId();
	}

}
