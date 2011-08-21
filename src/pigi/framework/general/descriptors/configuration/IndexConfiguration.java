package pigi.framework.general.descriptors.configuration;

import java.util.List;

public class IndexConfiguration 
{
	String datatablename;
	String dataclass;
	List<String> fields;	
	List<Index> indexes;
	
	public String getDatatablename() {
		return datatablename;
	}
	public void setDatatablename(String datatablename) {
		this.datatablename = datatablename;
	}
	public String getDataclass() {
		return dataclass;
	}
	public void setDataclass(String dataclass) {
		this.dataclass = dataclass;
	}
	
	public List<Index> getIndexes() {
		return indexes;
	}
	public void setIndexes(List<Index> indexes) {
		this.indexes = indexes;
	}
	public List<String> getFields() {
		return fields;
	}
	public void setFields(List<String> fields) {
		this.fields = fields;
	}
}
