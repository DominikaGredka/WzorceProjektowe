package model.users;

public class Teacher extends User {
    public Teacher(String id, String firstName, String lastName, String email) {
        super(id, firstName, lastName, email);
    }

    @Override
    public int getMaxBooks() {
        return 10;
    }

}