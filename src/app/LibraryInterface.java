package app;

import model.LibraryCatalog;
import model.publications.Book;
import model.publications.BookStatus;
import model.users.User;

import java.util.List;
import java.util.Optional;

public class LibraryInterface {

    private final LibraryCatalog libraryCatalog;

    public LibraryInterface(LibraryCatalog libraryCatalog) {
        this.libraryCatalog = libraryCatalog;
    }

    public List<Book> searchBooks(String title) {
        return libraryCatalog.searchBooks(title);
    }

    public void borrowBook(User user, String title) {
        Book book = libraryCatalog.findBook(title);
        if (book == null) {
            System.out.println("Book not found.");
        }
        if (book.status() == BookStatus.BORROWED) {
            System.out.println("Book is already borrowed.");
        }

        book = new Book(book.title(), book.author(), book.year(), BookStatus.BORROWED);
        libraryCatalog.updateBook(book);

        user.addBook(book);

        List<User> observers = libraryCatalog.getObserversForBook(book);
        for (User observer : observers) {
            if (!observer.equals(user)) {
                System.out.println("Notification sent to " + observer.getFirstName() + " " + observer.getLastName() + ": " +
                        "The book \"" + book.title() + "\" has been borrowed by another user.");
            }
        }


        System.out.println("Book borrowed successfully!");
    }

    public void returnBook(User user, String title) {
        Book book = libraryCatalog.findBook(title);
        if (book == null) {
             System.out.println("Book not found.");
        }

        if (user.hasBook(book)) {
            Book bookNew = new Book(book.title(), book.author(), book.year(), BookStatus.AVAILABLE);
            libraryCatalog.updateBook(bookNew);
            user.removeBook(book);
            List<User> observers = libraryCatalog.getObserversForBook(bookNew);
            for (User observer : observers) {
                System.out.println("Notification sent to " + observer.getFirstName() + " " + observer.getLastName() + ": " +
                        "The book \"" + bookNew.title() + "\" is now available in the library.");
            }
            System.out.println("Book returned successfully!");
        } else {
            System.out.println("This user did not borrow this book.");
        }
    }

    public void addBookToCatalog(Book book) {
        libraryCatalog.addBook(book);
    }

    public void addUserToSystem(User user) {
        libraryCatalog.addUser(user);
    }

    public Optional<User> findUserById(String id){
       return libraryCatalog.getUsers().stream().filter(user -> user.getId().equals(id) ).findFirst();
    }

    public String getNextId(){
        return String.valueOf(Integer.parseInt(libraryCatalog.getUsers().getLast().getId()) + 1);
    }

    public LibraryCatalog getLibraryCatalog() {
        return libraryCatalog;
    }
}