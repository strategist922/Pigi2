package pigi.examples.library.tests;

import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.hadoop.conf.Configuration;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pigi.examples.library.dao.AutomaticLibraryDAO;
import pigi.examples.library.vo.Author;
import pigi.examples.library.vo.Book;
import pigi.examples.library.vo.Category;
import pigi.framework.general.Direction;
import pigi.framework.general.PigiException;
import pigi.framework.general.descriptors.DataDescriptor;
import pigi.framework.general.descriptors.IndexDescriptor;
import pigi.framework.general.descriptors.IndexedField;
import pigi.framework.general.descriptors.configuration.DataDescriptorFactory;
import pigi.framework.general.descriptors.configuration.DataDescriptorProvider;
import pigi.framework.general.descriptors.configuration.FromHBase;
import pigi.framework.general.desriptors.fields.FieldOrder;
import pigi.framework.general.idgenerators.Keys;
import pigi.framework.general.views.results.PageQueryResult;

public class QueriesWithDiscoveryTest {

    private static final Category COMEDY = Category.newCategory("comedy");
    private static final Category DRAMA = Category.newCategory("drama");
    private static final List<String> CATEGORIES = Arrays.asList(COMEDY.getName(), DRAMA.getName());
    private static final List<String> AUTHORS = Arrays.asList(new String[]{"Poquelin", "Shakespear", "Shakespeare"});
    private static final List<String> AUTHORS_REVERSE = Arrays.asList(new String[]{"Shakespeare", "Shakespear", "Poquelin"});
    private Configuration config;

    @Before
    public void setup() throws Exception {
        config = LibraryConfiguration.configure();
    }
    
    @Test
    public void testListIndexes() throws Exception {
        FromHBase fhb = new FromHBase(config);
        assertEquals(Arrays.asList(
                "categories....name.ASC..10..counters", 
                "categories....name.ASC..10..index", 
                "categories....name.ASC..10..pages"), 
                fhb.listIndexes("categories"));
    }
    
    @Test
    public void testFromHBase() throws Exception {
        DataDescriptorProvider factory = DataDescriptorFactory.fromHBase(config);
        DataDescriptor<Author> categories = factory.build("authors", Author.class);
        assertNotNull(categories);
        IndexDescriptor<Author> indexDescriptor = categories.getIndexDescriptor("authors..name-surname..surname.ASC-name.ASC");
        assertNotNull(indexDescriptor);
    }
    
    @Test
    public void testParseFields() throws Exception {
        List<IndexedField> fields = Keys.parseFields("author_id-category_id");
        assertEquals(2, fields.size());
        IndexedField f0 = fields.get(0);
        assertEquals("author_id", f0.getName());
        assertEquals("author_id", f0.getFields().iterator().next().getName());
    }
    
    @Test
    public void testParseFields_none_there() throws Exception {
        List<IndexedField> fields = Keys.parseFields("");
        assertEquals(0, fields.size());
    }
    
    @Test
    public void testParseSordOrder() throws Exception {
        List<FieldOrder> fields = Keys.parseSortOrder("title.ASC");
        assertEquals(1, fields.size());
        FieldOrder f0 = fields.get(0);
        assertEquals("title", f0.getName());
        assertEquals(Direction.ASC, f0.direction());
    }
    
    @Test
    public void testParseSordOrder_none_there() throws Exception {
        List<FieldOrder> fields = Keys.parseSortOrder("");
        assertEquals(0, fields.size());
    }
    
    @Test
    public void getAllCategories() throws Exception {
        AutomaticLibraryDAO dao = new AutomaticLibraryDAO(config);
        PageQueryResult<Category> res = dao.getAllCategories(0);
        List<String> expected = CATEGORIES;
        List<String> actual = new ArrayList<String>();
        for (Category category : res.getObjects()) {
            actual.add(category.getName());
        }
        assertEquals(expected, actual);
    }

    @Test
    public void getAllAuthorsASC() throws Exception {
        AutomaticLibraryDAO dao = new AutomaticLibraryDAO(config);
        PageQueryResult<Author> res = dao.getAllAuthorsASC(0);
        List<String> expected = AUTHORS;
        List<String> actual = new ArrayList<String>();
        for (Author Author : res.getObjects()) {
            actual.add(Author.getSurname());
        }
        assertEquals(expected, actual);
    }
    
    @Test
    public void update() throws Exception {
        AutomaticLibraryDAO dao = new AutomaticLibraryDAO(config);
        Author author = findBySurname(dao, "Shakespear");
        author.setName("Willem");
        dao.updateAuthor(author);
        Author newAuthor = findBySurname(dao, "Shakespear");
        assertNotNull(newAuthor);
        assertEquals("Willem", newAuthor.getName());
        assertEquals("Shakespear", newAuthor.getSurname());
    }

    @Test
    public void getAllAuthorsDESC() throws Exception {
        AutomaticLibraryDAO dao = new AutomaticLibraryDAO(config);
        PageQueryResult<Author> res = dao.getAllAuthorsDESC(0);
        List<String> expected = AUTHORS_REVERSE;
        List<String> actual = new ArrayList<String>();
        for (Author Author : res.getObjects()) {
            actual.add(Author.getSurname());
        }
        assertEquals(expected, actual);
    }

    @Test
    public void findAuthorByNameSurname() throws Exception {
        AutomaticLibraryDAO dao = new AutomaticLibraryDAO(config);
        Author likeAuthor = new Author((String) null);
        likeAuthor.setName("William");
        likeAuthor.setSurname("Shakespeare");
        PageQueryResult<Author> res = dao.findAuthors(likeAuthor, 0);
        List<Author> actual = res.getObjects();
        assertEquals(1, actual.size());
        Author author = actual.get(0);
        assertEquals("William", author.getName());
        assertEquals("Shakespeare", author.getSurname());
    }

    @Test
    public void findAuthorBySurname() throws Exception {
        String surname = "Shakespeare";
        AutomaticLibraryDAO dao = new AutomaticLibraryDAO(config);
        Author author = findBySurname(dao, surname);
        assertEquals("William", author.getName());
        assertEquals(surname, author.getSurname());
    }

    private Author findBySurname(AutomaticLibraryDAO dao, String surname) throws Exception {
        Author likeAuthor = new Author((String) null);
        likeAuthor.setSurname(surname);
        PageQueryResult<Author> res = dao.findAuthors(likeAuthor, 0);
        List<Author> actual = res.getObjects();
        assertEquals(1, actual.size());
        return actual.get(0);
    }

    private boolean containsTitle(Iterable<Book> collection, String title) {
        for (Book book : collection) {
            if (title.equals(book.getTitle())) return true;
        }
        return false;
    }
        
    @Test
    public void findBooksInCategory_ascending() throws Exception {
        AutomaticLibraryDAO dao = new AutomaticLibraryDAO(config);
        Category comedy = findCategory(dao, COMEDY);
        Book likeBook = new Book((String) null);
        likeBook.setCategoryId(comedy.getId());
        PageQueryResult<Book> res = dao.findBooks(likeBook, 0);
        List<Book> books = res.getObjects();
        assertTrue(books.size() > 0);
        assertContains(books, "Dom Garcie de Navarre ou le Prince jaloux", "George Dandin ou le Mari confondu");

        for (int i = 0; i < books.size() - 1; i++) {
            String first = books.get(i).getTitle();
            String second = books.get(i+1).getTitle();
            assertTrue(first + " vs " + second, first.compareTo(second) < 0);
        }
    }
    
    @Test
    public void findBooksInCategory_ascending_with_offset_and_count() throws Exception {
        AutomaticLibraryDAO dao = new AutomaticLibraryDAO(config);
        Category comedy = findCategory(dao, DRAMA);
        Book likeBook = new Book((String) null);
        likeBook.setCategoryId(comedy.getId());
        List<String> actual = new ArrayList();
        for (Book book : dao.findBooks(likeBook, 6, 6)) {
            actual.add(book.getTitle());
        }
        List<String> expected = Arrays.asList("Othello", "Romeo and Juliet", "The Tragedy of Julius Caesar", "Timon of Athens", "Titus Andronicus", "Troilus and Cressida");
        assertEquals(expected, actual);
    }

    private void assertContains(List<Book> books, String... titles) {
        for (String title : titles) {
            assertTrue("\"" + title + "\" must be in " + books, containsTitle(books, title));
        }
    }

    private List<Book> readBooklistDescendingPage(int page) throws Exception {
        AutomaticLibraryDAO dao = new AutomaticLibraryDAO(config);
        Category comedy = findCategory(dao, COMEDY);
        Book likeBook = new Book((String) null);
        likeBook.setCategoryId(comedy.getId());
        
        return dao.findBooksDESC(likeBook, page).getObjects();
    }

    @Test
    public void findBooksInCategory_descending() throws Exception {
        for (int page = 0; page < 4; page++) {
            List<Book> books = readBooklistDescendingPage(page);
            assertTrue(books.size() > 0);
            assertIsDescending(books);
            assertFalse("Must not be in " + books, containsTitle(books, "Dom Garcie de Navarre ou le Prince jaloux"));
            assertFalse("Must not be in " + books, containsTitle(books, "George Dandin ou le Mari confondu"));
        }
    }

    @Test
    public void exampleFindBooksInCategory_descending_last_page() throws Exception {
        List<Book> books = readBooklistDescendingPage(4);
        assertTrue(books.size() > 0);
        assertContains(books, "Dom Garcie de Navarre ou le Prince jaloux", "George Dandin ou le Mari confondu");

        assertIsDescending(books);
    }

    private void assertIsDescending(List<Book> books) {
        for (int i = 0; i < books.size() - 1; i++) {
            String first = books.get(i).getTitle();
            String second = books.get(i+1).getTitle();
            assertTrue(first + " vs " + second, first.compareTo(second) > 0);
        }
    }

    private Category findCategory(AutomaticLibraryDAO dao, Category c)
            throws PigiException {
        for (Category category : dao.getAllCategories(0).getObjects()) {
            assertNotNull("Seriously, dao cannot return a null", category);
            if (category.getName().equals(c.getName())) return category;
        }
        
        throw new RuntimeException("Category not found: " + c);
    }

    @Test
    public void findBooksByAuthorId() throws Exception {
        AutomaticLibraryDAO dao = new AutomaticLibraryDAO(config);
        String authorId = findBySurname(dao, "Shakespeare").getId();
        Book likeBook = new Book((String) null);
        likeBook.setAuthorId(authorId);
        List<Book> books = dao.findBooks(likeBook, 0).getObjects();
        
        assertContains(books, "King Lear", "Macbeth", "Antony and Cleopatra");
    }

    @Test
    public void findBooksByISBN() throws Exception {
        AutomaticLibraryDAO dao = new AutomaticLibraryDAO(config);
        Book likeBook = new Book((String) null);
        likeBook.setISBN("100f3kxk0frqv");
        List<Book> books = dao.findBooksNoOrder(likeBook, 0).getObjects();
        assertTrue(books.size() == 1);
        assertEquals("Julius Caesar", books.get(0).getTitle());
    }
    
    /*
     * see http://pubs.doc.ic.ac.uk/JavaTypesSound/JavaTypesSound.pdf
     * 
    class A {}
    class A1 extends A {}
    class B {
          char f(A x) { return 'f'; }
          int f(A1 y) { return 12345; }
          void g(A1 z) { System.out.println("Called g"); System.out.println(f(z)); System.out.println("out of g"); }
          void h(A u, A1 v) { System.out.println("Called h"); System.out.println(f(u)); System.out.println("out of h"); }
    }

    @Test
    public void testJavaSubsumption() {
        new B().g(new A1());
        new B().h(new A(), new A1());
    }
    /**/
}
