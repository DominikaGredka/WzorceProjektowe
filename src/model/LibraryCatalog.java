package model;

import model.publications.Book;
import model.publications.BookStatus;
import model.users.User;
import service.iterator.BookIterator;
import service.iterator.UserIterator;
import service.observer.Observer;

import java.util.*;
import java.util.stream.Collectors;

public class LibraryCatalog implements Iterable<Book>, CsvConvertible {

    private List<Book> books = new ArrayList<>();
    private List<User> users = new ArrayList<>();
    private Map<Book, List<User>> bookObservers = new HashMap<>();

    public void addBook(Book book) {
        books.add(book);
    }

    public void addUser(User user) {
        users.add(user);
    }

    public List<Book> getBooks() {
        return books;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setBookObservers(Map<Book, List<User>> bookObservers) {
        this.bookObservers = bookObservers;
    }

    public void addObserverToBook(Book book, User observer) {
        bookObservers.computeIfAbsent(book, k -> new ArrayList<>()).add(observer);
    }

    public void removeObserverFromBook(Book book, User observer) {
        List<User> observers = bookObservers.get(book);
        if (observers != null) {
            observers.remove(observer);
            if (observers.isEmpty()) {
                bookObservers.remove(book);
            }
        }
    }

    public void notifyObservers(Book book) {
        List<User> observers = bookObservers.get(book);
        if (observers != null) {
            for (User user : observers) {
                System.out.println("User " + user.getFirstName() + " " + user.getLastName() +
                        " notified about book: " + book.title());
            }
        }
    }

    public void changeBookStatus(Book book, BookStatus newStatus) {
        Book updatedBook = book.withStatus(newStatus);
        books.remove(book);
        books.add(updatedBook);
        notifyObservers(updatedBook);
    }

    public Iterator<Book> iterator() {
        return new BookIterator(books);
    }

    public Iterator<User> userIterator() {
        return new UserIterator(users);
    }


    public List<Book> searchBooks(String title) {
        return books.stream()
                    .filter(book -> book.title().toLowerCase().contains(title.toLowerCase()))
                    .collect(Collectors.toList());
    }

    public Book findBook(String title) {
        return books.stream()
                    .filter(book -> book.title().equalsIgnoreCase(title))
                    .findFirst()
                    .orElse(null);
    }

    public void updateBook(Book book) {
        for (int i = 0; i < books.size(); i++) {
            if (books.get(i).title().equals(book.title())) {
                books.set(i, book);
                break;
            }
        }
    }

    public List<Book> getBooksByStatus(BookStatus bookStatus) {
        return books.stream().filter(book -> book.status().equals(BookStatus.AVAILABLE)).toList();
    }

    @Override
    public String toCsv() {
        StringBuilder csvBuilder = new StringBuilder();

        for (Map.Entry<Book, List<User>> entry : bookObservers.entrySet()) {
            Book book = entry.getKey();
            List<User> observers = entry.getValue();

            List<String> observerIds = observers.stream()
                                                 .map(Observer::getId)
                                                 .toList();

            String csvLine = book.title() + ";" + observerIds + "\n";

            csvBuilder.append(csvLine);
        }

        return csvBuilder.toString();
    }

    public Map<Book, List<User>> getBookObservers() {
        return bookObservers;
    }

    public List<User> getObserversForBook(Book book) {
        Set<Book> books = bookObservers.keySet();
        List<Book> list = books.stream().filter(book1 -> book1.title().equals(book.title())).toList();
        List<User> users = new ArrayList<>();
        for (Book book1 : list){
           List<User> usersList =  bookObservers.get(book1) ;
           usersList.stream().spliterator().forEachRemaining(users::add);
        }
        return users;
    }

    private static LibraryCatalog instance;
    public LibraryCatalog() {}
    public static synchronized LibraryCatalog getInstance() {
        if (instance == null) {
            instance = new LibraryCatalog();
        }
        return instance;
    }

}