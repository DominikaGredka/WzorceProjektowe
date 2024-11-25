package model.users;

public class Librarian extends User {
    public Librarian(String id, String firstName, String lastName, String email) {
        super(id, firstName, lastName, email);
    }

    @Override
    public int getMaxBooks() {
        return Integer.MAX_VALUE;
    }

}
