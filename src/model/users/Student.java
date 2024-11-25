package model.users;

public class Student extends User {
    public Student(String id, String firstName, String lastName, String email) {
        super(id, firstName, lastName, email);
    }

    @Override
    public int getMaxBooks() {
        return 5;
    }
}