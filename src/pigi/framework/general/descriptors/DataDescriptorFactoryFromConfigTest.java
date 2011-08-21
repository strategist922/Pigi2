//package pigi.framework.general.descriptors;
//
//import static org.junit.Assert.*;
//
//import java.util.List;
//import java.util.Set;
//
//import junit.framework.Assert;
//
//import org.junit.After;
//import org.junit.AfterClass;
//import org.junit.Before;
//import org.junit.BeforeClass;
//import org.junit.Test;
//
//import pigi.framework.general.descriptors.configuration.DataDescriptorFactory;
//import pigi.framework.general.descriptors.configuration.DataDescriptorProvider;
//import pigi.framework.general.descriptors.configuration.Index;
//import pigi.framework.general.desriptors.fields.FieldOrder;
//import pigi.framework.general.vo.DataObject;
//
//// TODO(jasmin): maybe move this to test directory tree
//public class DataDescriptorFactoryFromConfigTest {
//
//	String jsonConfigString = "{\"indexconfigurations\":[{"
//			+ "\"datatablename\":\"table1\","
//			+ "\"dataclass\":\"pigi.framework.general.vo.DataObject\","
//			+ "\"fields\":[\"field1\",\"field2\",\"field3\",\"field4\",\"field5\"],"
//			+ "\"indexes\":[{"
//			+ "\"orderfields\":{\"field1\":\"asc\",\"field2\":\"asc\"},"
//			+ "\"pagesize\":10},"
//			+ "{\"indexproperties\":[\"field3\",\"field4\",\"field5\"],"
//			+ "\"orderfields\":{\"field3\":\"desc\",\"field4\":\"desc\",\"field5\":\"desc\"},"
//			+ "\"pagesize\":10" + "}" + "]}]}";
//
//	private MockHbaseIndexConfigurationReader mockConfigReader;
//
//	DataDescriptorProvider dataDescriptorProvider;
//
//	@BeforeClass
//	public static void setUpBeforeClass() throws Exception {
//
//	}
//
//	@AfterClass
//	public static void tearDownAfterClass() throws Exception {
//
//	}
//
//	@Before
//	public void setUp() throws Exception {
//		mockConfigReader = new MockHbaseIndexConfigurationReader(
//				jsonConfigString);
//		dataDescriptorProvider = DataDescriptorFactory
//				.fromConfig(mockConfigReader);
//	}
//
//	@After
//	public void tearDown() throws Exception {
//	}
//
//	@Test
//	public void testBuild() throws Exception {
//
//		String dataTableName = "table1";
//
//		GeneralDescriptor.dataTableName = dataTableName;
//
//		DataDescriptor<DataObject> dataDescriptor = dataDescriptorProvider
//				.build(dataTableName, DataObject.class);
//		// checking fieldnames
//		assertFieldNamesEqual(
//				mockConfigReader.getAllFieldsNames(dataTableName),
//				dataDescriptor.fieldDescriptors.fieldDescriptors().keySet());
//		// checking strategies
//		// TODO : mockConfigreader needs to be modified to return strategynames
//		// in the format used by the pigi lib
//
//		Assert.assertEquals(dataDescriptor.allIndexNames(),
//				mockConfigReader.getIndexNamesList(dataTableName));
//		for (String indexName : mockConfigReader
//				.getIndexNamesList(dataTableName)) {
//			assertNotNull(dataDescriptor.getIndexDescriptor(indexName));
//
//			assertStrategyEquals(dataDescriptor.getIndexDescriptor(indexName),
//					mockConfigReader.getIndexByName(dataTableName, indexName));
//		}
//
//	}
//
//	private void assertFieldNamesEqual(List<String> expected, Set<String> actual) {
//		Assert.assertEquals(expected.size(), actual.size());
//		for (String s : expected) {
//			if (!actual.contains(s))
//				Assert.fail();
//		}
//	}
//
//	private void assertStrategyEquals(
//			IndexDescriptor<DataObject> strategyDescriptor, Index index) {
//		assertEquals(strategyDescriptor.getIndexProperties().size(), index
//				.getIndexproperties().size());
//		for (String fieldname : index.getIndexproperties()) {
//			assertTrue(strategyDescriptor.hasField(fieldname));
//		}
//
//		assertEquals(strategyDescriptor.getOrderFields().size(), index
//				.getOrderfields().size());
//		for (FieldOrder orderField : strategyDescriptor.getOrderFields()) {
//			String fieldName = index.getOrderfields().get(orderField.getName());
//			assertNotNull(fieldName);
//			assertEquals(fieldName.substring(0, 1), orderField.direction()
//					.toString());
//		}
//
//	}
//
//	// @Test
//	public void testGetAllConfigurations() {
//		fail("Not yet implemented");
//	}
//
//}
