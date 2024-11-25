package service.observer;

import model.publications.Book;
import model.users.User;

import java.util.List;
import java.util.stream.Collectors;

public class Notifier {

    public static void sendInfo(Book book, List<User> users) {
        List<User> usersToNotify = users.stream()
                                        .filter(user -> user instanceof Observer)
                                        .toList();

        if (usersToNotify.isEmpty()) {
            System.out.println("No users are observing this book.");
        }

        for (User user : usersToNotify) {
            user.update(book);
        }

        String emails = usersToNotify.stream()
                                     .map(User::getEmail)
                                     .collect(Collectors.joining(", "));

        System.out.println("To: " + emails);
        System.out.println("Topic: New book status");
        System.out.println("Content: Book '" + book.title() + "' has a new status: " + book.status());
    }
}