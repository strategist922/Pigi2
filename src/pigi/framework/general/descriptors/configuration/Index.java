package pigi.framework.general.descriptors.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Index {
	
	List<String> indexproperties=new ArrayList<String>();
	Map<String,String> orderfields = new HashMap<String, String>();
	public Map<String, String> getOrderfields() {
		return orderfields;
	}
	public void setOrderfields(Map<String, String> orderfields) {
		this.orderfields = orderfields;
	}
	String pagesize;
	
	public List<String> getIndexproperties() {
		return indexproperties;
	}
	public void setIndexproperties(List<String> indexproperties) {
		this.indexproperties = indexproperties;
	}
	
	public int getPagesize() {
		return Integer.parseInt(pagesize);
	}
	public void setPagesize(String pagesize) {
		this.pagesize = pagesize;
	}
	
}
