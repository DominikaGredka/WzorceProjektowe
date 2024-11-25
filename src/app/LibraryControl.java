package app;

import exceptions.DataExportException;
import io.file.CsvFileManager;
import io.file.FileManager;
import model.LibraryCatalog;
import model.publications.Book;
import model.publications.BookStatus;
import model.users.User;
import service.factory.UserFactory;

import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class LibraryControl {
    private final LibraryInterface libraryInterface;
    private final Scanner scanner;
    private final FileManager fileManager;


    public LibraryControl() {

        fileManager = new CsvFileManager();
        LibraryCatalog libraryCatalog = fileManager.importData();
        this.libraryInterface = new LibraryInterface(libraryCatalog);
        this.scanner = new Scanner(System.in);

    }

    public void controlLoop() {
        while (true) {
            showMenu();
            int option = getUserChoice();

            switch (option) {
                case 1:
                    searchBooks();
                    break;
                case 2:
                    borrowBook();
                    break;
                case 3:
                    returnBook();
                    break;
                case 4:
                    addBook();
                    break;
                case 5:
                    addUser();
                    break;
                case 6:
                    showUsers();
                    break;
                case 7:
                    showAvailableBooks();
                    break;
                case 8:
                    subscribeToNotifications();
                    break;
                case 9:
                    unsubscribeFromNotifications();
                    break;
                case 10:
                    exit();
                    System.out.println("Exiting the library system.");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void exit() {
        try {
            fileManager.exportData(libraryInterface.getLibraryCatalog());
            System.out.println("Export data to file completed successfully.");
        } catch (DataExportException e) {
            System.out.println("Error exporting data: " + e.getMessage());
        } finally {
            scanner.close();
            System.out.println("End of the program. Goodbye!");
        }
    }

    private void showMenu() {
        System.out.println("\nLibrary Menu:");
        System.out.println("1. Search Books");
        System.out.println("2. Borrow Book");
        System.out.println("3. Return Book");
        System.out.println("4. Add Book");
        System.out.println("5. Add User");
        System.out.println("6. Show Users");
        System.out.println("7. Show Available Books");
        System.out.println("8. Subscribe to Notifications");
        System.out.println("9. Unsubscribe from Notifications");
        System.out.println("10. Exit");
    }

    private int getUserChoice() {
        int choice = -1;
        while (choice < 1 || choice > 10) {
            System.out.print("Enter your choice (1-10): ");
            try {
                choice = scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number between 1 and 10.");
                scanner.next();
            }
        }
        return choice;
    }

    private void searchBooks() {
        scanner.nextLine();
        System.out.print("Enter book title to search: ");
        String title = scanner.nextLine();
        List<Book> books = libraryInterface.searchBooks(title);
        if (books.isEmpty()) {
            System.out.println("No books found.");
        } else {
            books.forEach(System.out::println);
        }
    }

    private void borrowBook() {
        scanner.nextLine();
        System.out.print("Enter user id: ");
        String id = scanner.nextLine();

        System.out.print("Enter book title to borrow: ");
        String title = scanner.nextLine();
        libraryInterface.findUserById(id).ifPresentOrElse(user -> libraryInterface.borrowBook(user, title), LibraryControl::noUserFound);
    }

    private static void noUserFound() {
        System.out.println("User not found");
    }

    private void returnBook() {
        scanner.nextLine();
        System.out.print("Enter user id: ");
        String id = scanner.nextLine();

        System.out.print("Enter book title to return: ");
        String title = scanner.nextLine();
        libraryInterface.findUserById(id).ifPresentOrElse(user -> libraryInterface.returnBook(user, title), LibraryControl::noUserFound);
    }

    private void addBook() {
        scanner.nextLine();
        System.out.print("Enter book title: ");
        String title = scanner.nextLine();

        System.out.print("Enter book author: ");
        String author = scanner.nextLine();

        System.out.print("Enter book year: ");
        int year = scanner.nextInt();

        Book book = new Book(title, author, year, BookStatus.AVAILABLE);
        libraryInterface.addBookToCatalog(book);
        System.out.println("Book added to catalog.");
    }

    private void addUser() {
        scanner.nextLine();
        System.out.print("Enter user first name: ");
        String firstName = scanner.nextLine();

        System.out.print("Enter user last name: ");
        String lastName = scanner.nextLine();

        System.out.print("Enter user email: ");
        String email = scanner.nextLine();

        System.out.print("Enter user type: ");
        String userType = scanner.nextLine();

        String id = libraryInterface.getNextId();

        User user = UserFactory.createUser(userType, id, firstName, lastName, email);

        libraryInterface.addUserToSystem(user);
        System.out.println("User added to system.");
    }

    private void showUsers() {
        Iterator<User> userIterator = libraryInterface.getLibraryCatalog().userIterator();
        userIterator.forEachRemaining(System.out::println);
    }

    private void showAvailableBooks() {
        List<Book> availableBooks = libraryInterface.getLibraryCatalog().getBooksByStatus(BookStatus.AVAILABLE);
        if (availableBooks.isEmpty()) {
            System.out.println("No available books in the catalog.");
        } else {
            System.out.println("List of available books:");
            availableBooks.forEach(System.out::println);
        }
    }

    private void subscribeToNotifications() {
        scanner.nextLine();
        System.out.print("Enter user id: ");
        String userId = scanner.nextLine();

        System.out.print("Enter book title to subscribe: ");
        String bookTitle = scanner.nextLine();

        libraryInterface.findUserById(userId).ifPresentOrElse(user -> {
            Book book = libraryInterface.getLibraryCatalog().findBook(bookTitle);
            if (book != null) {
                libraryInterface.getLibraryCatalog().addObserverToBook(book, user);
                System.out.println("Subscribed to notifications for book: " + bookTitle);
            } else {
                System.out.println("Book not found.");
            }
        }, LibraryControl::noUserFound);
    }

    private void unsubscribeFromNotifications() {
        scanner.nextLine();
        System.out.print("Enter user id: ");
        String userId = scanner.nextLine();

        System.out.print("Enter book title to unsubscribe: ");
        String bookTitle = scanner.nextLine();

        libraryInterface.findUserById(userId).ifPresentOrElse(user -> {
            Book book = libraryInterface.getLibraryCatalog().findBook(bookTitle);
            if (book != null) {
                libraryInterface.getLibraryCatalog().removeObserverFromBook(book, user);
                System.out.println("Unsubscribed from notifications for book: " + bookTitle);
            } else {
                System.out.println("Book not found.");
            }
        }, LibraryControl::noUserFound);
    }


}
