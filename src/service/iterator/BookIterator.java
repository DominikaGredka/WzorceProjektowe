package service.iterator;

import model.publications.Book;

import java.util.Iterator;
import java.util.List;

public class BookIterator implements Iterator<Book> {
    private final List<Book> books;
    private int currentIndex = 0;

    public BookIterator(List<Book> books) {
        this.books = books;
    }

    @Override
    public boolean hasNext() {
        return currentIndex < books.size();
    }

    @Override
    public Book next() {
        if (!hasNext()) {
            throw new java.util.NoSuchElementException();
        }
        return books.get(currentIndex++);
    }
}
