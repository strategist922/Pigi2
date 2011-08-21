package pigi.framework.tools;

import java.util.Map;

/**
 * Representation of single row 
 */
public class DataRow {
	private String id;
	private Map<String, String> cols;

	public DataRow(String id, Map<String, String> cols) {
		this.id = id;
		this.cols = cols;
	}

	public Map<String, String> getCols() {
		return cols;
	}

	public String getId() {
		return id;
	}

}
