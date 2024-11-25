import app.LibraryInterface;
import io.file.CsvFileManager;
import io.file.FileManager;
import model.LibraryCatalog;
import model.publications.Book;
import model.publications.BookStatus;
import model.users.User;
import service.factory.UserFactory;

import java.util.Iterator;

public class LibrarySystemDemo {
    public static void main(String[] args) {

        System.out.println("=== Singleton ===");
        LibraryCatalog catalog1 = LibraryCatalog.getInstance();
        LibraryCatalog catalog2 = LibraryCatalog.getInstance();
        System.out.println("Are catalog1 and catalog2 the same instance? " + (catalog1.equals(catalog2)));

        System.out.println("\n=== Adapter ===");
        FileManager csvFileManager = new CsvFileManager();  
        catalog1 = csvFileManager.importData();
        System.out.println("Books imported from CSV: " + catalog1.getBooks());

        System.out.println("\n=== Factory ===");
        UserFactory factory = new UserFactory();
        LibraryInterface libraryInterface = new LibraryInterface(catalog1);
        User student = UserFactory.createUser("Student", libraryInterface.getNextId(), "John", "Doe", "john.doe@example.com");
        User teacher = UserFactory.createUser("Teacher", libraryInterface.getNextId(), "Alice", "Smith", "alice.smith@example.com");
        System.out.println("Created User: " + student);
        System.out.println("Created User: " + teacher);

        System.out.println("\n=== Observer ===");
        Book book = new Book("Design Patterns", "Gang of Four", 1994, BookStatus.AVAILABLE);
        catalog1.addBook(book);
        catalog1.addObserverToBook(book, student);
        catalog1.addObserverToBook(book, teacher);
        System.out.println("Notifying observers about borrowing the book...");
        libraryInterface.borrowBook(student, "Design Patterns");

        System.out.println("\n=== Facade ===");
        libraryInterface.addBookToCatalog(new Book("Clean Code", "Robert C. Martin", 2008, BookStatus.AVAILABLE));
        libraryInterface.addUserToSystem(student);
        libraryInterface.borrowBook(student, "Clean Code");
        libraryInterface.returnBook(student, "Clean Code");

        System.out.println("\n=== Iterator ===");
        Iterator<User> userIterator = catalog1.userIterator();
        System.out.println("Users in the system:");
        userIterator.forEachRemaining(System.out::println);

        Iterator<Book> bookIterator = catalog1.iterator();
        System.out.println("Books in the catalog:");
        bookIterator.forEachRemaining(System.out::println);

        System.out.println("\nDemo finished.");
    }
}
