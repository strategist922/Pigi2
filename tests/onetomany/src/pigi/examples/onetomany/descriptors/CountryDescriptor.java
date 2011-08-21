 package pigi.examples.onetomany.descriptors;

import pigi.examples.onetomany.vo.Country;
import pigi.framework.general.descriptors.DataDescriptor;
import pigi.framework.general.desriptors.fields.Field;

public class CountryDescriptor extends DataDescriptor<Country> {
    
	public static final String NAME = "name";

    public CountryDescriptor() {
		super("countries", Country.class);
		addField(new Field(NAME));
	}

}
