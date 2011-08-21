package pigi.examples.library.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;

import pigi.examples.library.vo.Author;
import pigi.examples.library.vo.Book;
import pigi.examples.library.vo.Category;
import pigi.examples.library.vo.Library;
import pigi.framework.general.Direction;
import pigi.framework.general.PigiException;
import pigi.framework.general.descriptors.DataDescriptor;
import pigi.framework.general.descriptors.configuration.DataDescriptorFactory;
import pigi.framework.general.descriptors.configuration.DataDescriptorProvider;
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

public class AutomaticLibraryDAO {

    private DataDescriptor<Library> libraries;
    private DataDescriptor<Book> books;
    private DataDescriptor<Category> categories;
    private DataDescriptor<Author> authors;
    private static HashMap<String, String> NO_PROPERTIES = new HashMap<String,String>();
    
    private DataDescriptor<?>[] descriptors;
    // using this flag to test the behavior in the case where we do not add indexes (so we can reindex later)
    public boolean doNotIndex = false;
    private Configuration config;
	
    public AutomaticLibraryDAO(Configuration config) throws PigiException {
        this.config = config;
        HTableFactory.setHBaseConfiguration(config);
        discoverDescriptors();
    }
    
    public AutomaticLibraryDAO(Configuration config, boolean doNotIndex) throws PigiException {
        this(config);
        this.doNotIndex = doNotIndex;
    }
    
    public void discoverDescriptors() throws PigiException {
        DataDescriptorProvider factory = DataDescriptorFactory.fromHBase(config);
        authors = factory.build("authors", Author.class);
        books = factory.build("books", Book.class);
        categories = factory.build("categories", Category.class);
        libraries = factory.build("libraries", Library.class);

        descriptors = new DataDescriptor<?>[]{authors, books, categories, libraries};
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
        return authors.find(properties, pageNo, orderedBy);
    }
    
	private PageQueryResult<Category> getCategoriesList(Map<String, String> properties, int pageNo, List<FieldOrder> orderedBy) throws PigiException {
        return categories.find(properties, pageNo, orderedBy);
    }

    private PageQueryResult<Book> getBooksList(Map<String, String> properties, int pageNo, List<FieldOrder> orderedBy) throws PigiException {
		return books.find(properties, pageNo, orderedBy);
	}
    
    private List<Book> getBooksList(Map<String, String> fields,
            int offset, int count, List<FieldOrder> orderBy) throws PigiException {
        return books.findRange(fields, offset, count, orderBy);
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
    
    public List<Book> findBooks(Book likeBook, int offset, int count) throws PigiException {
        return getBooksList(likeBook.getFields(), offset, count, orderBy("title"));
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
		return insert(libraries.getLogic(), library);
	}
	
	/**
	 * returns id of added author
	 * @param author
	 * @return
	 */
	public String addAuthor(Author author) throws PigiException {
		return insert(authors.getLogic(), author);		
	}
	
	public String addCategory(Category category) throws PigiException {
		return insert(categories.getLogic(), category);
	}
	
	public String addBook(Book book) throws PigiException {
		return insert(books.getLogic(), book);
	}
	
	//---------------------------------------------------------------------------
	// UPDATE OBJECTS
	//---------------------------------------------------------------------------	

	public void updateLibrary(Library library) throws PigiException {
		libraries.getLogic().update(library);
	}
	
	public void updateAuthor(Author author) throws PigiException {
		authors.getLogic().update(author);
	}
	
	public void updateCategory(Category category) throws PigiException {
		categories.getLogic().update(category);
	}
	
	public void updateBook(Book book) throws PigiException {	
		books.getLogic().update(book);
	}
	
	
	//---------------------------------------------------------------------------
	// REMOVE OBJECTS
	//---------------------------------------------------------------------------	

	public void deleteLibrary(Library library) throws PigiException {
		libraries.getLogic().delete(library);
	}
	
	public void deleteAuthor(Author author) throws PigiException {
		authors.getLogic().delete(author);		
	}
	
	public void deleteCategory(Category category) throws PigiException {
		categories.getLogic().delete(category);
	}
	
	public void deleteBook(Book book) throws PigiException {	
		books.getLogic().delete(book);
	}

    public void rebuildIndexes() throws HTableFactoryException, IOException, DbLogicException, RowToolException, IndexException {
        for (DataDescriptor<? extends DataObject> d : descriptors) {
            d.rebuildIndexes();
        }
        
    }

	
	
}
