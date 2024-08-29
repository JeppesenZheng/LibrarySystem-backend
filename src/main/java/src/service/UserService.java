package src.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import src.User.BookAdmin;
import src.User.NormalUser;
import src.User.SystemAdmin;
import src.model.Book;
import src.model.User;
import src.repository.BookRepository;
import src.repository.UserRepository;
import java.util.Optional;

@Service
public class UserService {
    // private Map<String, User> users = new HashMap<>();
    // private Map<String, User> tokens = new HashMap<>();
    // private Map<String, Book> books = new HashMap<>();
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    public SystemAdmin createSystemAdmin(String name, String password) {
        checkUserExists(name, password);
        SystemAdmin admin = new SystemAdmin(name, password);
        // users.put(admin.getName(), admin);
        return userRepository.save(admin);
    }

    public BookAdmin createBookAdmin(String name, String password) {
        checkUserExists(name, password);
        BookAdmin admin = new BookAdmin(name, password);
        // users.put(admin.getName(), admin);
        return userRepository.save(admin);
    }

    @Transactional
    public NormalUser createNormalUser(String name, String password) {
        // checkUserExists(name, password);
        // try {
        //     if (userRepository.findByName(name).isPresent()) {
        //         throw new IllegalArgumentException("User with the same name already exists.");
        //     }
        //     NormalUser normalUser = new NormalUser(name, password);
        //     NormalUser savedUser = userRepository.save(normalUser);
        //     System.out.println("User created successfully: " + savedUser.getName());
        //     return savedUser;
        // } catch (Exception e) {
        //     System.err.println("Error creating user: " + e.getMessage());
        //     e.printStackTrace();
        //     throw e;
        // }
        if (userRepository.findByName(name).isPresent()) {
            throw new IllegalArgumentException("User with the same name already exists.");
        }
        NormalUser normalUser = new NormalUser(name, password);
        return (NormalUser) userRepository.save(normalUser);
    }

    private void checkUserExists(String name, String email) {
        Optional<User> existingUser = userRepository.findByName(name);
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("User with the same name already exists.");
        }
    }

    public String authenticate(String name, String password) {
        Optional<User> user = userRepository.findByName(name);
        return user.isPresent() && user.get().getPassword().equals(password) ? UUID.randomUUID().toString() : null;
    }

    // add new book
    public void addBook(Book book) {
        bookRepository.save(book);
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book getBookByISBN(String isbn) {
        return bookRepository.findByIsbn(isbn).orElse(null);
    }
    
    public void updateBook(Book book) {
        bookRepository.save(book);
    }

    public void deleteBook(String isbn) {
        bookRepository.findByIsbn(isbn).ifPresent(book -> bookRepository.delete(book));
    }

    public User getUserByName(String name) {
        return userRepository.findByName(name).orElse(null);
    }
}
