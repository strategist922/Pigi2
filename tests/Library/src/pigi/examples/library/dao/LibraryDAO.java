package pigi.examples.library.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;

import pigi.examples.library.descriptors.AuthorDescriptor;
import pigi.examples.library.descriptors.BookDescriptor;
import pigi.examples.library.descriptors.CategoryDescriptor;
import pigi.examples.library.descriptors.LibraryDescriptor;
import pigi.examples.library.vo.Author;
import pigi.examples.library.vo.Book;
import pigi.examples.library.vo.Category;
import pigi.examples.library.vo.Library;
import pigi.framework.general.Direction;
import pigi.framework.general.PigiException;
import pigi.framework.general.descriptors.DataDescriptor;
import pigi.framework.general.desriptors.fields.Field;
import pigi.framework.general.desriptors.fields.FieldOrder;
import pigi.framework.general.indexes.IndexException;
import pigi.framework.general.logic.DbLogic;
import pigi.framework.general.logic.DbLogicException;
import pigi.framework.general.views.results.PageQueryResult;
import pigi.framework.general.vo.CRUDException;
import pigi.framework.general.vo.DataObject;
import pigi.framework.tools.HTableFactory;
import pigi.framework.tools.HTableFactoryException;
import pigi.framework.tools.RowToolException;

public class LibraryDAO {

    private static DataDescriptor<Library> LIBRARY_DESCRIPTOR = new LibraryDescriptor();
    private static DataDescriptor<Book> BOOK_DESCRIPTOR = new BookDescriptor();
    private static DataDescriptor<Category> CATEGORY_DESCRIPTOR = new CategoryDescriptor();
    private static DataDescriptor<Author> AUTHOR_DESCRIPTOR = new AuthorDescriptor();
    private static HashMap<String, String> NO_PROPERTIES = new HashMap<String,String>();
    
    private static DataDescriptor<?>[] DESCRIPTORS = 
        {AUTHOR_DESCRIPTOR, BOOK_DESCRIPTOR, CATEGORY_DESCRIPTOR, LIBRARY_DESCRIPTOR};
    // using this flag to test the behavior in the case where we do not add indexes (so we can reindex later)
    public boolean doNotIndex = false;
    
    public LibraryDAO(Configuration config, boolean doNotIndex) {
        this.doNotIndex = doNotIndex;
    }
    
    public LibraryDAO(Configuration config) {
        HTableFactory.setHBaseConfiguration(config);     
    }

    public void discoverDescriptors() {
//        LibraryDescriptor = 
    }
    
    public static List<FieldOrder> orderBy(Object... params) {
        List<FieldOrder> order = new ArrayList<FieldOrder>();
        String current = null;
        for (int i = 0; i < params.length; i++) {
            Object param = params[i];
            if (param instanceof Direction) {
                if (current == null) throw new IllegalArgumentException(
                        "Bad order parameters at " + i + ", no attribute for " + param + ", previous parameter " +
                        (i == 0 ? " is missing " : params[i - 1].toString()) + "... " +
                        Arrays.asList(params));
                order.add(new FieldOrder(new Field(current), (Direction) param));
                current = null;
            } else if (param instanceof String) {
                if (current != null) {
                    order.add(new FieldOrder(new Field(current), Direction.ASC));
                }
                current = (String) param;
            }
        }
        if (current != null) {
            order.add(new FieldOrder(new Field(current), Direction.ASC));
        }
        return order;
    }
    
    private PageQueryResult<Author> getAuthorsList(Map<String, String> properties, int pageNo, List<FieldOrder> orderedBy) throws PigiException, IndexException {
        return AUTHOR_DESCRIPTOR.find(properties, pageNo, orderedBy);
    }
    
	private PageQueryResult<Category> getCategoriesList(Map<String, String> properties, int pageNo, List<FieldOrder> orderedBy) throws PigiException {
        return CATEGORY_DESCRIPTOR.find(properties, pageNo, orderedBy);
    }

    private PageQueryResult<Book> getBooksList(Map<String, String> properties, int pageNo, List<FieldOrder> orderedBy) throws PigiException {
		return BOOK_DESCRIPTOR.find(properties, pageNo, orderedBy);
	}
	
	public PageQueryResult<Author> findAuthors(Author likeAuthor, int pageNo) throws PigiException {
		return getAuthorsList(likeAuthor.getFields(), pageNo, orderBy("surname", "name"));		
	}
	
	public PageQueryResult<Author> getAllAuthorsASC(int pageNo) throws PigiException {
		return getAuthorsList(NO_PROPERTIES, pageNo, orderBy("surname", "name"));									
	}
			
	public PageQueryResult<Author> getAllAuthorsDESC(int pageNo) throws PigiException {
        return getAuthorsList(NO_PROPERTIES, pageNo, orderBy("surname", Direction.DESC, "name", Direction.DESC));                                   
	}
	
	public PageQueryResult<Category> getAllCategories(int pageNo) throws PigiException {
		return getCategoriesList(NO_PROPERTIES, pageNo, orderBy("name"));		
	}
	
	public PageQueryResult<Book> findBooks(Book likeBook, int pageNo) throws PigiException {
		return getBooksList(likeBook.getFields(), pageNo, orderBy("title"));
	}
	
	public PageQueryResult<Book> findBooksDESC(Book likeBook, int pageNo) throws PigiException {
		return getBooksList(likeBook.getFields(), pageNo, orderBy("title", Direction.DESC));
	}
	
	public PageQueryResult<Book> findBooksNoOrder(Book likeBook, int pageNo) throws PigiException {
		return getBooksList(likeBook.getFields(), pageNo, orderBy());
	}
	
	//---------------------------------------------------------------------------
	// INSERTS OBJECTS
	//---------------------------------------------------------------------------

	<T extends DataObject> String insert(DbLogic<T> logic, T object) throws CRUDException, DbLogicException {
	    return doNotIndex ? logic.addToDB_for_testing_only(object) : logic.insert(object); 
	}
	
	/**
	 * returns id of new Library
	 * @param newLibrary
	 * @return
	 * @throws GeneralFieldObjectDescriptorException 
	 * @throws GeneralFieldDescriptorsCollectionException 
	 * @throws DbLogicException 
	 */
	public String createLibrary(Library library) throws PigiException {
		return insert(LIBRARY_DESCRIPTOR.getLogic(), library);
	}
	
	/**
	 * returns id of added author
	 * @param author
	 * @return
	 */
	public String addAuthor(Author author) throws PigiException {
		return insert(AUTHOR_DESCRIPTOR.getLogic(), author);		
	}
	
	/**
	 * returns new category id
	 * @param category
	 * @return
	 */
	public String addCategory(Category category) throws PigiException {
		return insert(CATEGORY_DESCRIPTOR.getLogic(), category);
	}
	
	/**
	 * returns new book's id 
	 * @param book
	 * @return
	 * @throws PigiException 
	 */
	public String addBook(Book book) throws PigiException {
		return insert(BOOK_DESCRIPTOR.getLogic(), book);
	}
	
	//---------------------------------------------------------------------------
	// UPDATE OBJECTS
	//---------------------------------------------------------------------------	

	/**
	 * 
	 * @param library
	 * @return
	 * @throws GeneralFieldObjectDescriptorException 
	 * @throws GeneralFieldDescriptorsCollectionException 
	 * @throws DbLogicException 
	 */
	public void updateLibrary(Library library) throws PigiException {
		LIBRARY_DESCRIPTOR.getLogic().update(library);
	}
	
	public void updateAuthor(Author author) throws PigiException {
		AUTHOR_DESCRIPTOR.getLogic().update(author);
	}
	
	public void updateCategory(Category category) throws PigiException {
		CATEGORY_DESCRIPTOR.getLogic().update(category);
	}
	
	public void updateBook(Book book) throws PigiException {	
		BOOK_DESCRIPTOR.getLogic().update(book);
	}
	
	
	//---------------------------------------------------------------------------
	// REMOVE OBJECTS
	//---------------------------------------------------------------------------	

	/**
	 * 
	 * @param library
	 * @return
	 * @throws GeneralFieldObjectDescriptorException 
	 * @throws GeneralFieldDescriptorsCollectionException 
	 * @throws DbLogicException 
	 */
	public void deleteLibrary(Library library) throws PigiException {
		LIBRARY_DESCRIPTOR.getLogic().delete(library);
	}
	
	public void deleteAuthor(Author author) throws PigiException {
		AUTHOR_DESCRIPTOR.getLogic().delete(author);		
	}
	
	public void deleteCategory(Category category) throws PigiException {
		CATEGORY_DESCRIPTOR.getLogic().delete(category);
	}
	
	public void deleteBook(Book book) throws PigiException {	
		BOOK_DESCRIPTOR.getLogic().delete(book);
	}

    public void rebuildIndexes() throws HTableFactoryException, IOException, DbLogicException, RowToolException, IndexException {
        for (DataDescriptor<? extends DataObject> d : DESCRIPTORS) {
            d.rebuildIndexes();
        }
        
    }
	
}
