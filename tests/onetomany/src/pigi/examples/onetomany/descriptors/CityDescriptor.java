package pigi.examples.onetomany.descriptors;

import java.util.Arrays;
import java.util.List;

import pigi.examples.onetomany.vo.City;
import pigi.framework.general.PigiException;
import pigi.framework.general.descriptors.DataDescriptor;
import pigi.framework.general.descriptors.IndexedField;
import pigi.framework.general.descriptors.SimpleIndexDescriptor;
import pigi.framework.general.descriptors.ValueField;
import pigi.framework.general.desriptors.fields.Field;
import pigi.framework.general.desriptors.fields.FieldOrder;

public class CityDescriptor  extends DataDescriptor<City> {
    public static final String SIZE = "size";
    public static final String NAME = "name";
    public static final String COUNTRY_ID = "countryId";

    public CityDescriptor() {
		super("cities", City.class);
		Field countryId = addField(new Field(COUNTRY_ID));
		Field name = addField(new Field(NAME));
		Field size = addField(new Field(SIZE));
		

		//cities by country
		List<IndexedField> indexedPropertiesC = listOf(new ValueField(countryId));		
		List<FieldOrder> orderFieldsC = listOf(name.ascending());
		addSimpleIndex(10, indexedPropertiesC, orderFieldsC);
		
		//cities by size
		List<IndexedField> indexedPropertiesS = listOf(new ValueField(size));		
		addSimpleIndex(10, indexedPropertiesS, listOf(name.ascending()));

		addSimpleIndex(10, 
		        listOf(
      		        new ValueField(countryId),
		            new ValueField(size)), 
		        listOf(name.ascending(), size.ascending()));

	}
}
