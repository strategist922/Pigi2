package pigi.examples.library.vo;

import java.util.Map;

import pigi.framework.general.vo.DataObject;
import pigi.framework.general.vo.DataObject;

/**
 * this class represents single book
 *        book has attributes : 
 *            - title
 *            - authorId
 *            - categoryId  (book is in only one category)
 *            - isbn (unique international book number)
 * @author acure
 *
 */
public class Book extends DataObject {

	/**
     * 
     */
    private static final transient long serialVersionUID = -3385276233518623643L;

    @Override
    public String toString() {
        return "\"" + getTitle() + "\"";
    }

    public Book(String id) {
		super(id);
	}
	
	public Book(DataObject objectVO){
		super(objectVO.getId(), objectVO.getFields());
	}
	
	public Book(String id, Map<String, String> fields) {
		super(id, fields);
	}

	/**
	 * returns author id 
	 * @return
	 */
	public String getAuthorId(){
		return getField(Names.AUTHOR_ID);
	}
	
	/**
	 * returns a title of book
	 * @return
	 */
	public String getTitle(){
		return getField(Names.TITLE);
	}

	/**
	 * returns book's categoryId
	 * @return
	 */
	public String getCategoryId(){
		return getField(Names.CATEGORY_ID);
	}
	
	/**
	 * returns isbn number 
	 * @return
	 */
	public String getISBN(){
		return getField(Names.ISBN);
	}
	
	
	public void setAuthorId(String authorId){
		addField(Names.AUTHOR_ID, authorId);
	}
	
	public void setTitle(String title){
		addField(Names.TITLE, title);
	}
	
	public Book setCategoryId(String categoryId){
		addField(Names.CATEGORY_ID, categoryId);
		return this;
	}	
	
	public void setISBN(String isbn){
		addField(Names.ISBN, isbn);
	}	
	
    public static Book newBook(Category category, Author author, String title) {
        long hash = title.hashCode();
        hash *= hash *= hash &= 0x7FFFFFFFFFFFFFFFL;
        String isbn = Long.toString(hash * hash * hash, 36);
        Book book = new Book((String) null);
        book.setCategoryId(category.getId());
        book.setAuthorId(author.getId());
        book.setTitle(title);
        try {
            book.setISBN(isbn.substring(0, Math.min(13, isbn.length())));
        } catch (Exception e) {
            System.out.println("omg");
        }
        return book;
    }
}
