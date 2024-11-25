package service.observer;

import model.publications.Book;

public interface Observer {
    void update(Book book);

    String getId();
}