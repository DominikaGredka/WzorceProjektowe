package io.file;

import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.DataExportException;
import exceptions.DataImportException;
import model.JsonConvertible;
import model.LibraryCatalog;
import model.publications.Book;
import model.users.User;

import java.io.*;
import java.util.Collection;

public class JsonFileManager implements FileManager {
    private static final String PUBLICATIONS_FILE_NAME = "Library.json";
    private static final String USERS_FILE_NAME = "Library_users.json";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public LibraryCatalog importData() {
        LibraryCatalog libraryCatalog = new LibraryCatalog();
        importBooks(libraryCatalog);
        importUsers(libraryCatalog);
        return libraryCatalog;
    }

    @Override
    public void exportData(LibraryCatalog libraryCatalog) {
        exportBooks(libraryCatalog);
        exportUsers(libraryCatalog);
    }

    private void exportBooks(LibraryCatalog libraryCatalog) {
        Collection<Book> books = libraryCatalog.getBooks();
        exportToJson(books, PUBLICATIONS_FILE_NAME);
    }

    private void exportUsers(LibraryCatalog libraryCatalog) {
        Collection<User> users = libraryCatalog.getUsers();
        exportToJson(users, USERS_FILE_NAME);
    }

    private <T extends JsonConvertible> void exportToJson(Collection<T> collection, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(writer, collection);
        } catch (IOException e) {
            throw new DataExportException("Błąd zapisu danych do pliku " + fileName + " " + e);
        }
    }

    private void importBooks(LibraryCatalog libraryCatalog) {
        try (BufferedReader reader = new BufferedReader(new FileReader(PUBLICATIONS_FILE_NAME))) {
            Book[] books = objectMapper.readValue(reader, Book[].class);
            for (Book book : books) {
                libraryCatalog.addBook(book);
            }
        } catch (IOException e) {
            throw new DataImportException("Błąd odczytu pliku " + PUBLICATIONS_FILE_NAME + " " + e);
        }
    }

    private void importUsers(LibraryCatalog libraryCatalog) {
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE_NAME))) {
            User[] users = objectMapper.readValue(reader, User[].class);
            for (User user : users) {
                libraryCatalog.addUser(user);
            }
        } catch (IOException e) {
            throw new DataImportException("Błąd odczytu pliku " + USERS_FILE_NAME + " " + e);
        }
    }
}