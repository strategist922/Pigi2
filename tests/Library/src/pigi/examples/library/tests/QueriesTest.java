package pigi.examples.library.tests;

import static org.junit.Assert.*;
import static pigi.examples.library.vo.Book.newBook;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pigi.examples.library.dao.LibraryDAO;
import pigi.examples.library.descriptors.AuthorDescriptor;
import pigi.examples.library.descriptors.BookDescriptor;
import pigi.examples.library.descriptors.CategoryDescriptor;
import pigi.examples.library.descriptors.LibraryDescriptor;
import pigi.examples.library.vo.Author;
import pigi.examples.library.vo.Book;
import pigi.examples.library.vo.Category;
import pigi.examples.library.vo.Library;
import pigi.framework.general.PigiException;
import pigi.framework.general.idgenerators.Keys;
import pigi.framework.general.views.results.PageQueryResult;
import pigi.framework.tools.TableAdmin;
import pigi.framework.tools.TableTool;

public class QueriesTest {

    private static final Category COMEDY = Category.newCategory("comedy");
    private static final Category DRAMA = Category.newCategory("drama");
    private static final List<String> CATEGORIES = Arrays.asList(COMEDY.getName(), DRAMA.getName());
    private static final List<String> AUTHORS = Arrays.asList(new String[]{"Poquelin", "Shakespear", "Shakespeare"});
    private static final List<String> AUTHORS_REVERSE = Arrays.asList(new String[]{"Shakespeare", "Shakespear", "Poquelin"});
    private static Configuration config;

    @BeforeClass
    public static void setup() throws Exception {
        config = LibraryConfiguration.configure();
        dropTables();
        createTables(true);
        fillData(false);
    }

    @After
    public void after() throws Exception {
        List<Category> tragedies = new ArrayList<Category>();
        LibraryDAO dao = new LibraryDAO(config);
        for (Category category : dao.getAllCategories(0).getObjects()) {
            if ("tragedy".equals(category.getName())) {
                tragedies.add(category);
            }
        }
        for (Category tragedy : tragedies) {
            dao.deleteCategory(tragedy);
        }
        PageQueryResult<Category> res = dao.getAllCategories(0);
        List<String> actual = new ArrayList<String>();
        for (Category category : res.getObjects()) {
            actual.add(category.getName());
        }
        assertEquals(CATEGORIES, actual);
    }

    @Test
    public void nameMatching() {
        String pattern = "authors" + "..".replaceAll("\\.", "\\\\.") + ".*";
        assertEquals("authors\\.\\..*", pattern);
        assertTrue("authors..name..sort".matches(pattern));
        assertTrue("authors....sort".matches(pattern));
        assertFalse("authorses....sort".matches(pattern));
        assertFalse(" authors..name..sort".matches(pattern));
        assertFalse("uthors..name..sort".matches(pattern));
    }
    
    @Test
    public void getAllCategories() throws Exception {
        LibraryDAO dao = new LibraryDAO(config);
        PageQueryResult<Category> res = dao.getAllCategories(0);
        List<String> expected = CATEGORIES;
        List<String> actual = new ArrayList<String>();
        for (Category category : res.getObjects()) {
            actual.add(category.getName());
        }
        assertEquals(expected, actual);
    }

    @Test
    public void getAllCategories_afterManualAddition() throws Exception {
        Category tragedy = Category.newCategory("tragedy");
        LibraryDAO dao = new LibraryDAO(config, true);
        dao.addCategory(tragedy);
        PageQueryResult<Category> res = dao.getAllCategories(0);
        List<String> actual = new ArrayList<String>();
        for (Category category : res.getObjects()) {
            actual.add(category.getName());
        }
        assertEquals(CATEGORIES, actual);
        new LibraryDAO(config).rebuildIndexes();
        List<String> afterReindexing = new ArrayList<String>();
        List<String> expected = new ArrayList<String>(CATEGORIES);
        expected.add("tragedy");
        for (Category category : dao.getAllCategories(0).getObjects()) {
            afterReindexing.add(category.getName());
        }
        assertEquals(expected, afterReindexing);
    }

    @Test
    public void getAllAuthorsASC() throws Exception {
        LibraryDAO dao = new LibraryDAO(config);
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
        LibraryDAO dao = new LibraryDAO(config);
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
        LibraryDAO dao = new LibraryDAO(config);
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
        LibraryDAO dao = new LibraryDAO(config);
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
        LibraryDAO dao = new LibraryDAO(config);
        Author author = findBySurname(dao, surname);
        assertEquals("William", author.getName());
        assertEquals(surname, author.getSurname());
    }

    private Author findBySurname(LibraryDAO dao, String surname) throws Exception {
        Author likeAuthor = new Author((String) null);
        likeAuthor.setSurname(surname);
        PageQueryResult<Author> res = dao.findAuthors(likeAuthor, 0);
        List<Author> actual = res.getObjects();
        assertEquals(1, actual.size());
        return actual.get(0);
    }

    private Author findByName(LibraryDAO dao, String name) throws Exception {
        Author likeAuthor = new Author((String) null);
        likeAuthor.setName(name);
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
        LibraryDAO dao = new LibraryDAO(config);
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

    private void assertContains(List<Book> books, String... titles) {
        for (String title : titles) {
            assertTrue("\"" + title + "\" must be in " + books, containsTitle(books, title));
        }
    }

    private List<Book> readBooklistDescendingPage(int page) throws Exception {
        LibraryDAO dao = new LibraryDAO(config);
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

    private Category findCategory(LibraryDAO dao, Category c)
            throws PigiException {
        for (Category category : dao.getAllCategories(0).getObjects()) {
            assertNotNull("Seriously, dao cannot return a null", category);
            if (category.getName().equals(c.getName())) return category;
        }
        
        throw new RuntimeException("Category not found: " + c);
    }

    @Test
    public void findBooksByAuthorId() throws Exception {
        LibraryDAO dao = new LibraryDAO(config);
        String authorId = findBySurname(dao, "Shakespeare").getId();
        Book likeBook = new Book((String) null);
        likeBook.setAuthorId(authorId);
        List<Book> books = dao.findBooks(likeBook, 0).getObjects();
        
        assertContains(books, "King Lear", "Macbeth", "Antony and Cleopatra");
    }

    @Test
    public void findBooksByISBN() throws Exception {
        LibraryDAO dao = new LibraryDAO(config);
        Book likeBook = new Book((String) null);
        likeBook.setISBN("100f3kxk0frqv");
        List<Book> books = dao.findBooksNoOrder(likeBook, 0).getObjects();
        assertTrue(books.size() == 1);
        assertEquals("Julius Caesar", books.get(0).getTitle());
    }

    public static void dropTables() throws Exception {
        TableTool tool = new TableTool();
        for (String table : new String[] {
            }) {
            tool.deleteTable(table);
        }
        new LibraryDescriptor().dropTables();
        new CategoryDescriptor().dropTables();
        new AuthorDescriptor().dropTables();
        new BookDescriptor().dropTables();
    }

    public static void createTables(boolean withIndexes) throws Exception {
        TableAdmin.build(new LibraryDescriptor()).createTables_for_testing(withIndexes);
        TableAdmin.build(new CategoryDescriptor()).createTables_for_testing(withIndexes);
        TableAdmin.build(new AuthorDescriptor()).createTables_for_testing(withIndexes);
        TableAdmin.build(new BookDescriptor()).createTables_for_testing(withIndexes);
    }

    public static void addBooks(LibraryDAO dao, Category category, Author author, String... titles) throws Exception {
        for (String title : titles) {
            Book book = newBook(category, author, title);
            dao.addBook(book);
            System.out.println(" book : " + book.getTitle() + " added");
        }
    }

    public static void addComedies(LibraryDAO dao, Author author, String... titles) throws Exception {
        addBooks(dao, COMEDY, author, titles);
    }

    public static void addDramas(LibraryDAO dao, Author author, String... titles) throws Exception {
        addBooks(dao, DRAMA, author, titles);
    }

    private static void fillData(boolean doNotIndex) throws Exception {
        //LibraryDAO daoForDeletion = new LibraryDAO(config);
        LibraryDAO dao = new LibraryDAO(config, doNotIndex);
        
        Library Library = new Library(null);
        Library.setName("London library");
        Library.setAddress("some street in london");
        dao.createLibrary(Library);
        dao.addCategory(COMEDY);
        dao.addCategory(DRAMA);

        Author shakespeare = new Author("William", "Shakespeare");
        dao.addAuthor(shakespeare);

        Author shakespear = new Author("Guillaume", "Shakespear");
        dao.addAuthor(shakespear);

        addComedies(dao, shakespeare,
                "The Comedy of Errors",
                "Loves' Labour's Lost",
                "The Taming of The Shrew",
                "The Two Gentlemen of Verona",
                "A Midsummer Night's Dream",
                "The Merchant of Venice",
                "Much Ado about Nothing",
                "As You Like It",
                "Twelfth Night or What You Will",
                "The Merry Wives of Windsor",
                "Pericles, Prince of Tyre",
                "All's Well That Ends Well",
                "Measure for Measure");

        addDramas(dao, shakespeare, 
                "Titus Andronicus",
                "Romeo and Juliet",
                "The Tragedy of Julius Caesar",
                "Troilus and Cressida",
                "Hamlet, Prince of Denmark",
                "Othello",
                "King Lear",
                "Macbeth",
                "Antony and Cleopatra",
                "Coriolanus",
                "Timon of Athens",
                "Julius Caesar");

        // Molier - Jean-Baptiste Poquelin
        Author molier = new Author("Jean-Baptiste", "Poquelin");
        dao.addAuthor(molier);

        addComedies(dao, molier, 
                "Le Médecin volant",
                "La Jalousie du barbouillé",
                "L'Étourdi ou les Contretemps",
                "Le Dépit amoureux",
                "Le Docteur amoureux",
                "Les Précieuses ridicules",
                "Sganarelle ou le Cocu imaginaire",
                "Dom Garcie de Navarre ou le Prince jaloux",
                "L'École des maris",
                "Les Fâcheux",
                "L'École des femmes",
                "La Jalousie du Gros-René",
                "La Critique de l'école des femmes",
                "L'Impromptu de Versailles",
                "Le Mariage forcé",
                "Gros-René, petit enfant",
                "La Princesse d'Élide",
                "Tartuffe ou l'Imposteur",
                "Dom Juan ou le Festin de pierre",
                "L'Amour médecin",
                "Le Misanthrope ou l'Atrabilaire amoureux",
                "Le Médecin malgré lui",
                "Mélicerte",
                "Pastorale comique",
                "Le Sicilien ou l'Amour peintre",
                "Amphitryon",
                "George Dandin ou le Mari confondu",
                "L'Avare ou l'École du mensonge",
                "Monsieur de Pourceaugnac",
                "Les Amants magnifiques",
                "Le Bourgeois gentilhomme",
                "Psyché",
                "Les Fourberies de Scapin",
                "La Comtesse d'Escarbagnas",
                "Les Femmes Savantes",
                "Le Malade Imaginaire");
        System.out.println("Done.");
    }
    
    //@Test
    public void rebuildIndexes() throws Exception {
        dropTables();
        createTables(false);
        fillData(true);
        new LibraryDAO(config).rebuildIndexes();
        new LibraryDAO(config).rebuildIndexes();
        getAllCategories();
        getAllAuthorsASC();
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
