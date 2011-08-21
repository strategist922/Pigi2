package pigi.examples.onetomany.vo;

import java.util.Map;

import pigi.examples.onetomany.descriptors.CityDescriptor;
import pigi.framework.general.vo.DataObject;
import pigi.framework.general.vo.DataObject;

public class City extends DataObject {

	public City() { 
		super();
	}
	
	public City(DataObject objectVO) {
		super(objectVO);
	}

	public City(String id, Map<String, String> fields) {
		super(id, fields);
	}

	public City(String id) {
		super(id);
	}

	public String getCountryId() { 
		return getField(CityDescriptor.COUNTRY_ID);
	}
	
	public void setCountryId(String countryId) {
		addField(CityDescriptor.COUNTRY_ID, countryId);
	}
	
	public String getName() { 
		return getField(CityDescriptor.NAME);
	}
	
	public void setName(String name) {
		addField(CityDescriptor.NAME, name);
	}

	public String getSize() { 
		return getField(CityDescriptor.SIZE);
	}
	
	public void setSize(String size) {
		addField(CityDescriptor.SIZE, size);
	}
}
