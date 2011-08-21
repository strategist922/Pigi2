package pigi.examples.library.vo;

import static pigi.examples.library.vo.Names.ADDRESS;
import static pigi.examples.library.vo.Names.NAME;

import java.util.Map;

import pigi.framework.general.vo.DataObject;


/**
 * this class represents single library
 * @author acure
 *
 */
public class Library extends DataObject {

	/**
     * 
     */
    private static final long serialVersionUID = 4036635059417099830L;

    public Library(String id, Map<String, String> fields) {
		super(id, fields);
	}

	public Library(String id) {
		super(id);
	}
	
	
	/**
	 * returns a name of library
	 * @return
	 */
	public String getName(){
		return getField(NAME);
	}
	
	/**
	 * returns address
	 * @return
	 */
	public String getAddress(){
		return getField(ADDRESS);
	}
	
	
	public void setName(String name){
		addField(NAME, name);		
	}
	
	public void setAddress(String address){
		addField(ADDRESS, address);
	}
}
