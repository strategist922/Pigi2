package pigi.examples.library.tests;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;

import pigi.framework.general.descriptors.configuration.HbaseIndexConfigReader;
import pigi.framework.general.descriptors.configuration.Index;
import pigi.framework.tools.Strings;

public class MockHbaseIndexConfigurationReader extends HbaseIndexConfigReader {

	public static final String INDEX_NAME_SEPARATOR = "..";
	private static List<Index> indexesList = new ArrayList<Index>();

	public MockHbaseIndexConfigurationReader(String jsonConfigString) {
		super(jsonConfigString);
	}

	public List<String> getIndexNamesList(String dataTableName) {
		List<String> strategyNames = new ArrayList<String>();

		for (Index index : this.getIndexConfigurations()
				.forTableName(dataTableName).get(0).getIndexes()) {
			String filter = fieldsNamesJoined(index.getIndexproperties());
			String order = orderFieldNamesJoined(index.getOrderfields());
			String indexName = indexName(dataTableName, filter, order);
			strategyNames.add(indexName);
		}

		return strategyNames;
	}

	public List<String> getAllFieldsNames(String dataTableName) {
		return this.getIndexConfigurations().forTableName(dataTableName).get(0)
				.getFields();

	}

	public Index getIndexByName(String dataTableName, String indexName) {
		for (Index strategy : this.getIndexConfigurations()
				.forTableName(dataTableName).get(0).getIndexes()) {
			String filter = fieldsNamesJoined(strategy.getIndexproperties());
			String order = orderFieldNamesJoined(strategy.getOrderfields());
			String name = indexName(dataTableName, filter, order);
			if (name.equals(indexName))
				return strategy;
		}
		return null;
	}

	private static String fieldsNamesJoined(List<String> indexProperties) {
		SortedSet<String> names = new TreeSet<String>();

		for (String fieldName : indexProperties) {

			names.add(fieldName);
		}
		return Strings.join("-", names);
	}

	private static String orderFieldNamesJoined(Map<String, String> orderFields) {
		StringBuilder buf = new StringBuilder();
		for (Entry<String, String> entry : orderFields.entrySet()) {
			if (buf.length() > 0)
				buf.append("-");
			buf.append(entry.getKey()).append(".").append(entry.getValue().toUpperCase());
		}
		return buf.toString();
	}

	private static String indexName(String dataTableName, String filter,
			String order) {
		return dataTableName + INDEX_NAME_SEPARATOR + filter
				+ INDEX_NAME_SEPARATOR + order;
	}
}
