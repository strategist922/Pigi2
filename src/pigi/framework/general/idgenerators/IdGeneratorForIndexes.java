package pigi.framework.general.idgenerators;

import java.util.List;
import java.util.Map;

import pigi.framework.general.vo.DataObject;


/**
 * generates id for index tables - please use first understand how it works and than use it carefully.
 * 
 * @author acure
 * 
 */
public interface IdGeneratorForIndexes {
	/**
	 * returns first possible id for object with given properties
	 * 
	 * @param properties
	 * @return
	 */
	public String getFirstPossibleId(Map<String, String> propertyValues);

	/**
	 * returns last possible id for object with given properties
	 * 
	 * @param properties
	 * @return
	 */
	public String getLastPossibleId(Map<String, String> propertyValues);

	/**
	 * returns a list of index ids for a given DataObject
	 * 
	 * @param object
	 * @return
	 */
	public List<String> getIds(DataObject object);

}
