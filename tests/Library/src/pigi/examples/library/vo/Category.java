package pigi.examples.library.vo;

import static pigi.examples.library.vo.Names.LIBRARY_ID;
import static pigi.examples.library.vo.Names.NAME;

import java.util.Map;

import pigi.framework.general.vo.DataObject;
import pigi.framework.general.vo.DataObject;



/**
 * this class represents a single category
 * @author acure
 *
 */
public class Category extends DataObject {

    /**
     * 
     */
    private static final long serialVersionUID = 4932797594467053028L;


    public Category() {
        this((String) null);
    }

    public Category(String id) {
		super(id);
	}

	public Category(DataObject objectVO){
		super(objectVO.getId(), objectVO.getFields());
	}
	
	public Category(String id, Map<String, String> fields) {
		super(id, fields);
	}

	/**
	 * returns name of category
	 * @return
	 */
	public String getName(){
		return getField(NAME);
	}
	
	public void setName(String name){
		addField(NAME, name);
	}
	
    public static Category newCategory(String name) {
        Category c = new Category();
        c.setName(name);
        return c;
    }
	
	
	// TODO: drop the two methods below
    /**
     * returns libraryId
     * @return
     */
    public String getLibraryId(){
        return getField(LIBRARY_ID);
    }
        
	
	public void setLibraryId(String libraryId){
		addField(LIBRARY_ID, libraryId);
	}
	
}
