package model.users;

import model.CsvConvertible;
import model.JsonConvertible;
import model.publications.Book;
import service.observer.Observer;

import java.util.ArrayList;
import java.util.List;

public abstract class User implements CsvConvertible, JsonConvertible, Observer {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private List<Book> borrowedBooks;

    public User(String id, String firstName, String lastName, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.borrowedBooks = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public List<Book> getBorrowedBooks() {
        return borrowedBooks;
    }

    public boolean hasBook(Book book) {
        return borrowedBooks.contains(book);
    }

    public void addBook(Book book) {
        if (borrowedBooks.size() < getMaxBooks()) {
            borrowedBooks.add(book);
        } else {
            System.out.println("You have already borrowed the maximum number of books.");
        }
    }

    public void removeBook(Book book) {
        borrowedBooks.remove(book);
    }

    public abstract int getMaxBooks();

    @Override
    public String toCsv() {
        return getClass().getSimpleName().toUpperCase() + ";" + id + ";" + firstName + ";" + lastName + ";" + email + ";" + borrowedBooks.stream().map(Book::title).toList();
    }

    @Override
    public String toJson() {
        return "{ \"id\": \"" + id + "\", \"firstName\": \"" + firstName + "\", \"lastName\": \"" + lastName + "\", \"email\": \"" + email + "\" }";
    }

    @Override
    public void update(Book book) {
        System.out.println("Dear " + firstName + ", " + "The book '" + book.title() + "' you wanted is now available!");
        System.out.println("You will receive an email notification at: " + email);
    }

    @Override
    public String toString() {
        return  id + " " + firstName + " " + lastName + " " + email + " " + borrowedBooks ;
    }
}