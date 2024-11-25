package io.file;

import exceptions.DataExportException;
import exceptions.DataImportException;
import model.CsvConvertible;
import model.LibraryCatalog;
import model.publications.Book;
import model.publications.BookStatus;
import model.users.User;
import service.factory.UserFactory;

import java.io.*;
import java.util.*;

public class CsvFileManager implements FileManager {
    private static final String PUBLICATIONS_FILE_NAME = "Library.csv";
    private static final String USERS_FILE_NAME = "Library_users.csv";
    private static final String NOTIFICATIONS_FILE_NAME = "Notifications.csv";

    @Override
    public LibraryCatalog importData() {
        LibraryCatalog libraryCatalog = new LibraryCatalog();
        importBooks(libraryCatalog);
        importUsers(libraryCatalog);
        importObservers(libraryCatalog);
        return libraryCatalog;
    }

    @Override
    public void exportData(LibraryCatalog libraryCatalog) {
        exportBooks(libraryCatalog);
        exportUsers(libraryCatalog);
        exportNotifications(libraryCatalog);
    }

    private void exportBooks(LibraryCatalog libraryCatalog) {
        Collection<Book> books = libraryCatalog.getBooks();
        exportToCsv(books, PUBLICATIONS_FILE_NAME);
    }

    private void exportUsers(LibraryCatalog libraryCatalog) {
        Collection<User> users = libraryCatalog.getUsers();
        exportToCsv(users, USERS_FILE_NAME);
    }

    private void exportNotifications(LibraryCatalog libraryCatalog) {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(NOTIFICATIONS_FILE_NAME))) {
        for (Map.Entry<Book, List<User>> entry : libraryCatalog.getBookObservers().entrySet()) {
            Book book = entry.getKey();
            List<User> users = entry.getValue();

            String userIds = users.stream()
                                  .map(User::getId)
                                  .reduce((id1, id2) -> id1 + "," + id2)
                                  .orElse("");

            String csvLine = book.title() + ";[" + userIds + "]";
            writer.write(csvLine);
            writer.newLine();
        }
    } catch (IOException e) {
        throw new DataExportException("Błąd zapisu powiadomień do pliku " + NOTIFICATIONS_FILE_NAME);
    }
}

    private <T extends CsvConvertible> void exportToCsv(Collection<T> collection, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (T item : collection) {
                writer.write(item.toCsv());
                writer.newLine();
            }
        } catch (IOException e) {
            throw new DataExportException("Błąd zapisu danych do pliku " + fileName);
        }
    }

    private void importBooks(LibraryCatalog libraryCatalog) {
        try (BufferedReader reader = new BufferedReader(new FileReader(PUBLICATIONS_FILE_NAME))) {
            reader.lines()
                  .map(this::createBookFromString)
                  .forEach(libraryCatalog::addBook);
        } catch (IOException e) {
            throw new DataImportException("Błąd odczytu pliku " + PUBLICATIONS_FILE_NAME);
        }
    }

    private Book createBookFromString(String csvText) {
        String[] data = csvText.split(";");
        return new Book(data[0], data[1], Integer.parseInt(data[2]), BookStatus.valueOf(data[3]));
    }

    private void importUsers(LibraryCatalog libraryCatalog) {
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE_NAME))) {
            reader.lines()
                  .map((String csvText) -> createUserFromString(csvText, libraryCatalog))
                  .forEach(libraryCatalog::addUser);
        } catch (IOException e) {
            throw new DataImportException("Błąd odczytu pliku " + USERS_FILE_NAME);
        }
    }

    private User createUserFromString(String csvText, LibraryCatalog libraryCatalog) {
        String[] data = csvText.split(";");

        String userType = data[0];
        String id = data[1];
        String firstName = data[2];
        String lastName = data[3];
        String email = data[4];

        String booksData = data.length > 5 ? data[5] : "";
        List<Book> books = parseBooksList(booksData, libraryCatalog);

        User user = UserFactory.createUser(userType, id, firstName, lastName, email);

        for (Book book : books) {
            user.addBook(book);
        }

        return user;
    }

    private List<Book> parseBooksList(String booksData, LibraryCatalog libraryCatalog) {
        List<Book> books = new ArrayList<>();

        if (!booksData.isEmpty()) {
            String[] bookTitles = booksData.replace("[", "").replace("]", "").split(",");

            for (String title : bookTitles) {
                title = title.trim();

                Book book = findBookByTitle(title, libraryCatalog);
                if (book != null) {
                    books.add(book);
                }
            }
        }

        return books;
    }

    private Book findBookByTitle(String title, LibraryCatalog libraryCatalog) {
        return libraryCatalog.findBook(title);
    }

    private void importObservers(LibraryCatalog libraryCatalog) {
        File file = new File(NOTIFICATIONS_FILE_NAME);
        if (!file.exists() || file.length() == 0) {
            libraryCatalog.setBookObservers(new HashMap<>());
            System.out.println("Notifications file is empty or does not exist. Initializing with an empty observers map.");
        } else {
            try (BufferedReader reader = new BufferedReader(new FileReader(NOTIFICATIONS_FILE_NAME))) {
            reader.lines()
                  .map((String csvText) -> createObserverFromString(csvText, libraryCatalog))
                  .forEach(entry -> {
                      Book book = entry.getKey();
                      List<User> users = entry.getValue();
                      users.forEach(user -> libraryCatalog.addObserverToBook(book, user));
                  });
            } catch (IOException e) {
                throw new DataImportException("Błąd odczytu pliku " + NOTIFICATIONS_FILE_NAME);
            }
        }

    }

    private Map.Entry<Book, List<User>> createObserverFromString(String csvText, LibraryCatalog libraryCatalog) {
        String[] data = csvText.split(";");
        String bookTitle = data[0];
        String userIds = data[1];

        Book book = findBookByTitle(bookTitle, libraryCatalog);
        if (book == null) {
            throw new DataImportException("Książka o tytule " + bookTitle + " nie istnieje w katalogu.");
        }

        List<User> users = new ArrayList<>();
        String[] ids = userIds.replace("[", "").replace("]", "").split(",");
        for (String id : ids) {
            User user = findUserById(id.trim(), libraryCatalog);
            if (user != null) {
                users.add(user);
            }
        }

        return new AbstractMap.SimpleEntry<>(book, users);
    }
    private User findUserById(String id, LibraryCatalog libraryCatalog) {
        return libraryCatalog.getUsers().stream()
                                    .filter(user -> user.getId().equals(id))
                                    .findFirst()
                                    .orElse(null);
    }
}