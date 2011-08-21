package pigi.examples.onetomany.dao;

import pigi.examples.onetomany.descriptors.CityDescriptor;
import pigi.examples.onetomany.descriptors.CountryDescriptor;
import pigi.examples.onetomany.vo.City;
import pigi.examples.onetomany.vo.Country;
import pigi.framework.tools.TableAdmin;

public class OneToManyTableCreator {

	public static void main(String[] args) throws Exception {
		new CountryDescriptor().createTables();
		new CityDescriptor().createTables();
	}

}
