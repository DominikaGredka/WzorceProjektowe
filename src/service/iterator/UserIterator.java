package service.iterator;

import model.users.User;

import java.util.Iterator;
import java.util.List;

public class UserIterator implements Iterator<User> {
    private final List<User> users;
    private int currentIndex = 0;

    public UserIterator(List<User> users) {
        this.users = users;
    }

    @Override
    public boolean hasNext() {
        return currentIndex < users.size();
    }

    @Override
    public User next() {
        if (!hasNext()) {
            throw new java.util.NoSuchElementException();
        }
        return users.get(currentIndex++);
    }
}