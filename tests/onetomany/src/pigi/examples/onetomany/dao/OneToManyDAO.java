package pigi.examples.onetomany.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pigi.examples.onetomany.descriptors.CityDescriptor;
import pigi.examples.onetomany.descriptors.CountryDescriptor;
import pigi.examples.onetomany.vo.City;
import pigi.examples.onetomany.vo.Country;
import pigi.framework.general.PigiException;
import pigi.framework.general.descriptors.DataDescriptor;
import pigi.framework.general.logic.DbLogic;
import pigi.framework.general.views.NativeIndexIterator;
import pigi.framework.general.views.query.PageQuery;
import pigi.framework.general.views.query.IndexViewQuery;
import pigi.framework.general.views.results.PageQueryResult;
import pigi.framework.tools.Iterables;

public class OneToManyDAO {
	private static final CityDescriptor CITY_DESCRIPTOR = new CityDescriptor();
    private int pageSize = 5;
	
	public String addCountry(Country country)  throws PigiException {
		return new CountryDescriptor().getLogic().insert(country);
	}
	
	public String addCity(City city) throws PigiException {
		return CITY_DESCRIPTOR.getLogic().insert(city);
	}
	
	public void updateCountry(Country country) throws PigiException {
		new CountryDescriptor().getLogic().update(country);
	}
	
	public void updateCity(City city) throws PigiException {
		CITY_DESCRIPTOR.getLogic().update(city);
	}
	
	public void deleteCountry(Country country) throws PigiException {
		DbLogic<City> cityLogic = CITY_DESCRIPTOR.getLogic();
		for (City city : allCitiesByCountry(country.getId())) {
			cityLogic.delete(city);
		}
		
		new CountryDescriptor().getLogic().delete(country);
	}
	
	public void deleteCity(City city) throws PigiException {
		CITY_DESCRIPTOR.getLogic().delete(city);
	}
	
	public PageQueryResult<City>  citiesByCountry(String countryId) throws PigiException {
		DataDescriptor<City> desc = CITY_DESCRIPTOR;
		Map<String, String> properties = new HashMap<String, String>();
		properties.put("countryId", countryId);
		PageQuery<City> query = new PageQuery<City>(desc.getIndex("citiesByCountry"), properties, pageSize);
		return query.getPage();
	}
	
	public Iterable<City> allCitiesByCountry(String countryId) throws PigiException {
		Map<String, String> properties = new HashMap<String, String>();
		properties.put("countryId", countryId);
		IndexViewQuery<City> query = new IndexViewQuery<City>(CITY_DESCRIPTOR.getIndex("citiesByCountry"), properties);
        Iterable<City> rest = query.getAll();
		return Iterables.iterable(new NativeIndexIterator<City>(City.class, rest.iterator()));
	}
	
	public PageQueryResult<City> citiesBySize(String size) throws PigiException {
		Map<String, String> properties = new HashMap<String, String>();
		properties.put("size", size);
		PageQuery<City> query = new PageQuery<City>(CITY_DESCRIPTOR.getIndex("citiesBySize"), properties, pageSize);
        List<City> list = query.getPage().getObjects();
		return new PageQueryResult<City>((query.getPage()).getPageNo(), query.getPage().getMaxPage(), list);
	}
	
	public PageQueryResult<City>  citiesByCountryAndSize(String countryId, String size) throws PigiException {
		Map<String, String> properties = new HashMap<String, String>();
		properties.put("countryId", countryId);
		properties.put("size", size);
		PageQuery<City> query = new PageQuery<City>(CITY_DESCRIPTOR.getIndex("citiesByCountryAndSize"), properties, pageSize);
		return query.getPage();
	}
}
