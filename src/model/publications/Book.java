package model.publications;

import model.CsvConvertible;
import model.JsonConvertible;

public record Book (
    String title,
    String author,
    int year,
    BookStatus status
) implements CsvConvertible, JsonConvertible {
    @Override
    public String toCsv() {
        return title + ";" + author + ";" + year + ";" + status;
    }

    @Override
    public String toString() {
        return  title + ", " + author + ", " + year + ", status: " + status;
    }

    @Override
    public String toJson() {
       return "{ \"title\": \"" + title + "\", \"author\": \"" + author + "\", \"year\": " + year + ", \"status\": \"" + status + "\" }";
    }

    public Book withStatus(BookStatus newStatus) {
        return new Book(title, author, year, newStatus);
    }
}
