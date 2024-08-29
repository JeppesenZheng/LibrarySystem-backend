package src.service;

import java.util.ArrayList;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
// import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
// import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.transaction.Transactional;
import src.User.BookAdmin;
import src.User.NormalUser;
import src.User.SystemAdmin;
import src.model.Book;
import src.model.User;
import src.repository.BookRepository;
import src.repository.UserRepository;
import java.util.Optional;
// import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

import java.time.temporal.ChronoUnit;

@Service
public class UserService {
    // private Map<String, User> users = new HashMap<>();
    // private Map<String, User> tokens = new HashMap<>();
    // private Map<String, Book> books = new HashMap<>();
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    // private Map<String, String> tokenToUsername = new HashMap<>();

    @Value("${jwt.secret}")
    private String jwtSecret;

    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

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

    public String authenticate(String username, String password) {
        // 验证用户名和密码
        User user = getUserByName(username);
        if (user != null && user.getPassword().equals(password)) {
            return generateToken(username);
        }
        return null;
    }

    private String generateToken(String username) {
        long expirationTime = 1000 * 60 * 60 * 24; // 24小时
        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
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

    public void borrowBook(String username, String isbn, int days) throws Exception {
        User user = getUserByName(username);
        Book book = getBookByISBN(isbn);
        if (user instanceof NormalUser && book != null && !book.isBorrowed()) {
            ((NormalUser) user).borrowBook(book);
            book.setBorrowed(true);
            book.setBorrowDate(LocalDate.now());
            book.setBorrowDays(days);
            book.incrementBorrowCount();
            updateBook(book);
        } else {
            throw new Exception("无法借阅该书");
        }
    }

    public void returnBook(String username, String isbn) throws Exception {
        User user = getUserByName(username);
        Book book = getBookByISBN(isbn);
        if (user instanceof NormalUser && book != null && book.isBorrowed()) {
            ((NormalUser) user).returnBook(book);
            book.setBorrowed(false);
            updateBook(book);
        } else {
            throw new Exception("无法归还该书");
        }
    }

    public List<Book> getBorrowedBooks(String username) {
        User user = getUserByName(username);
        if (user instanceof NormalUser) {
            return ((NormalUser) user).getBorrowedBooks();
        }
        return new ArrayList<>();
    }

    public String getUsernameFromToken(String token) {
    try {
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
        return claims.getSubject();
    } catch (Exception e) {
        System.err.println("Could not get username from token: " + e.getMessage());
        return null;
    }
}

    public List<Map<String, Object>> getAllBooksWithStatus() {
        List<Book> allBooks = bookRepository.findAll();
        List<Map<String, Object>> booksWithStatus = new ArrayList<>();

        for (Book book : allBooks) {
            Map<String, Object> bookInfo = new HashMap<>();
            bookInfo.put("id", book.getId());
            bookInfo.put("title", book.getTitle());
            bookInfo.put("author", book.getAuthor());
            bookInfo.put("isbn", book.getISBN());
            bookInfo.put("borrowed", book.isBorrowed());

            if (book.isBorrowed()) {
                String borrowerName = findBorrowerName(book);
                bookInfo.put("borrowerName", borrowerName);
                bookInfo.put("borrowDate", book.getBorrowDate().toString());
                bookInfo.put("borrowDays", book.getBorrowDays());
                
                // 计算剩余天数
                LocalDate dueDate = book.getBorrowDate().plusDays(book.getBorrowDays());
                long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
                bookInfo.put("daysLeft", daysLeft);
            }

            booksWithStatus.add(bookInfo);
        }

        return booksWithStatus;
    }

    private String findBorrowerName(Book book) {
        List<NormalUser> normalUsers = userRepository.findAllNormalUsers();
        for (NormalUser user : normalUsers) {
            if (user.getBorrowedBooks().contains(book)) {
                return user.getName();
            }
        }
        return null;
    }

    public List<Map<String, Object>> getAllUsers() {
        List<User> allUsers = userRepository.findAll();
        List<Map<String, Object>> usersInfo = new ArrayList<>();

        for (User user : allUsers) {
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("name", user.getName());
            userInfo.put("userType", getUserType(user));
            userInfo.put("password", user.getPassword());
            usersInfo.add(userInfo);
        }

        return usersInfo;
    }

    private String getUserType(User user) {
        if (user instanceof NormalUser) return "normal";
        if (user instanceof BookAdmin) return "bookAdmin";
        if (user instanceof SystemAdmin) return "systemAdmin";
        return "unknown";
    }
}
