package src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import src.Book.Book;
import src.User.BookAdmin;
import src.User.NormalUser;
import src.User.SystemAdmin;
import src.User.User;

@Service
public class UserService {
    private Map<String, User> users = new HashMap<>();
    private Map<String, User> tokens = new HashMap<>();
    private Map<String, Book> books = new HashMap<>();

    public SystemAdmin createSystemAdmin(String name, String password) {
        checkUserExists(name, password);
        SystemAdmin admin = new SystemAdmin(name, password);
        users.put(admin.getName(), admin);
        return admin;
    }

    public BookAdmin createBookAdmin(String name, String password) {
        checkUserExists(name, password);
        BookAdmin admin = new BookAdmin(name, password);
        users.put(admin.getName(), admin);
        return admin;
    }

    public NormalUser createNormalUser(String name, String password) {
        checkUserExists(name, password);
        NormalUser normalUser = new NormalUser(name, password);
        users.put(normalUser.getName(), normalUser);
        return normalUser;
    }

    private void checkUserExists(String name, String email) {
        if (users.containsKey(name)) {
            throw new IllegalArgumentException("User with the same name already exists.");
        }
    }

    public String authenticate(String name, String password) {
        System.out.println("users are " + users);
        User user = users.get(name);
        if (user != null && user.getPassword().equals(password)) {
            String sessionId = UUID.randomUUID().toString();
            tokens.put(sessionId, user);
            return sessionId;
        }
        return null;
    }

    // add new book
    public void addBook(Book book) {
        books.put(book.getISBN(), book);
    }

    public List<Book> getAllBooks() {
        System.out.println("books are " + books);
        return new ArrayList<>(books.values());
    }

    public Book getBookByISBN(String isbn) {
        return books.get(isbn);
    }
    
    public void updateBook(Book book) {
        books.put(book.getISBN(), book);
    }

    public void deleteBook(String isbn) {
        books.remove(isbn);
    }
}
