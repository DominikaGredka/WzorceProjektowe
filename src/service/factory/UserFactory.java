package service.factory;

import model.users.Librarian;
import model.users.Student;
import model.users.Teacher;
import model.users.User;

public class UserFactory {
    public static User createUser(String userType, String id, String firstName, String lastName, String email) {
        return switch (userType.toLowerCase()) {
            case "student" -> new Student(id, firstName, lastName, email);
            case "teacher" -> new Teacher(id, firstName, lastName, email);
            case "librarian" -> new Librarian(id, firstName, lastName, email);
            default -> throw new IllegalArgumentException("Unknown user type: " + userType);
        };
    }
}
