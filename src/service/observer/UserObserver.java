package service.observer;

import model.publications.Book;
import model.users.User;

public class UserObserver implements Observer {
    private final User user;

    public UserObserver(User user) {
        this.user = user;
    }

    @Override
    public void update(Book book) {
        System.out.println("User " + user.getFirstName() + " " + user.getLastName() +
                " notified about book: " + book.title());
    }

    public String getId() {
        return user.getId();
    }
}