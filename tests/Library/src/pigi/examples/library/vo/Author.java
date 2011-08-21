package pigi.examples.library.vo;

import pigi.framework.general.vo.DataObject;


/**
 * this class represents Author
 *    - all VO objects have to extends GeneralObjectVO 
 * @author acure
 *
 */
public class Author extends DataObject {
	
	/**
     * 
     */
    private static final long serialVersionUID = 7808919240564090162L;

    public Author(String id) {
		super(id);
	}
	
	public Author(String first, String last) {
	    this((String) null);
	    setName(first);
	    setSurname(last);
	}
	
	public Author(DataObject object) {
		super(object.getId(), object.getFields());
	}

	/**
	 * @return the name of author
	 */
	public String getName() {
		return getField(Names.NAME);
	}
	
	/** 
	 * @return the author's surname
	 */
	public String getSurname() {
		return getField(Names.SURNAME);
	}
	
	public void setName(String name){
		addField(Names.NAME, name);		
	}
	
	public void setSurname(String surname){
		addField(Names.SURNAME, surname);
	}
	
}
