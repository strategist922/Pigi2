package pigi.examples.onetomany.vo;

import java.util.Map;

import pigi.framework.general.vo.DataObject;
import pigi.framework.general.vo.DataObject;


public class Country extends DataObject{
	
	public Country() { 
		super();
	}
	
	public Country(DataObject objectVO) {
		super(objectVO);
	}

	public Country(String id, Map<String, String> fields) {
		super(id, fields);
	}

	public Country(String id) {
		super(id);
	}

	public String getName() { 
		return getField("name");
	}
	
	public void setName(String name) {
		addField("name", name);
	}
}
