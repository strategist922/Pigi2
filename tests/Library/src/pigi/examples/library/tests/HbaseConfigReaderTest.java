package pigi.examples.library.tests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

import pigi.framework.general.descriptors.configuration.HbaseIndexConfigReader;
import pigi.framework.general.descriptors.configuration.IndexConfiguration;
import pigi.framework.general.descriptors.configuration.IndexConfigurationList;
import pigi.framework.general.descriptors.configuration.Index;

// TODO(jasmin): please rename "strategy" to "index"; this is what we'll use in choochee dao
public class HbaseConfigReaderTest {
	HbaseIndexConfigReader configReader;
	IndexConfigurationList expected;
	String configurations = "{\"indexconfigurations\":[{"+
				"\"datatablename\":\"table1\","+
				"\"dataclass\":\"pigi.framework.general.vo.DataObject\","+
				"\"fields\":[\"field1\",\"field2\",\"field3\",\"field4\",\"field5\"],"+
				"\"indexes\":[{"+
						"\"orderfields\":{\"field1\":\"asc\",\"field2\":\"asc\"},"+							
						"\"pagesize\":10},"+
					"{\"indexproperties\":[\"field3\",\"field4\",\"field5\"],"+
						"\"orderfields\":{\"field3\":\"desc\",\"field4\":\"desc\",\"field5\":\"desc\"},"+
						"\"pagesize\":10"+											 
					"}"+
					"]}]}";

	
	@Before
	public void setUp() throws Exception {
		this.configReader = new HbaseIndexConfigReader();
		
		}

	
	@Test
	public void testGetConfigrationList() {
		System.out.println(configurations);
		IndexConfigurationList actual = configReader.getConfigrationList(configurations);
		assertIfEqual(expected(), actual);
	}
	private void assertIfEqual(IndexConfigurationList expected, IndexConfigurationList actual)
	{
		
		IndexConfiguration expectedConfiguration = expected.getConfigurations().get("table1");		
		IndexConfiguration actualConfiguration = actual.getConfigurations().get("table1");
		
		Assert.assertEquals(expectedConfiguration.getDatatablename(),actualConfiguration.getDatatablename());
		Assert.assertEquals(expectedConfiguration.getDataclass(),actualConfiguration.getDataclass());
		Assert.assertEquals(expectedConfiguration.getFields(),actualConfiguration.getFields());
		Assert.assertEquals(expectedConfiguration.getIndexes().get(0).getPagesize(),
				actualConfiguration.getIndexes().get(0).getPagesize());
		Assert.assertEquals(expectedConfiguration.getIndexes().get(0).getIndexproperties(),
				actualConfiguration.getIndexes().get(0).getIndexproperties());
		Assert.assertEquals(expectedConfiguration.getIndexes().get(0).getOrderfields(),
				actualConfiguration.getIndexes().get(0).getOrderfields());
	}
	private IndexConfigurationList expected()
	{
		IndexConfigurationList configList = new IndexConfigurationList();
		IndexConfiguration config = new IndexConfiguration();
		config.setDatatablename("table1");
		config.setDataclass("pigi.framework.general.vo.DataObject");
		config.setFields(Lists.newArrayList(new String("field1,field2,field3,field4,field5").split(",")));
		
		List<Index> strategies = new ArrayList<Index>();
		Index s = new Index();
		Map<String,String> sortFields = new HashMap<String, String>();
		sortFields.put("field1", "asc");
		sortFields.put("field2", "asc");
		s.setOrderfields(sortFields);
		s.setPagesize("10");
		strategies.add(s);
		
		s = new Index();
		sortFields = new HashMap<String, String>();
		sortFields.put("field3", "desc");
		sortFields.put("field4", "desc");
		sortFields.put("field5", "desc");
		s.setOrderfields(sortFields);		
		s.setIndexproperties(Lists.newArrayList(new String("field3,field4,field5").split(",")));
		s.setPagesize("10");
		strategies.add(s);
		
		config.setIndexes(strategies);
		
		configList.setConfigurations(Lists.newArrayList(config));
		
		return configList;
	}
}
