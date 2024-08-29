package src.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import src.model.Book;
import src.service.UserService;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;

@RestController
@RequestMapping("/books")
@CrossOrigin
public class BookController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<Book> getAllBooks() {
        return userService.getAllBooks();
    }

    @PostMapping("/add")
    public Book addBook(@RequestBody Book book) {
        userService.addBook(book);
        return book;
    }

    @PutMapping("/{isbn}")
    public Book updateBook(@PathVariable String isbn, @RequestBody Book book) {
        userService.updateBook(book);
        return book;
    }

    @DeleteMapping("/{isbn}")
    public void deleteBook(@PathVariable String isbn) {
        userService.deleteBook(isbn);
    }

    @PostMapping("/borrow/{isbn}")
    public ResponseEntity<?> borrowBook(@PathVariable String isbn, @RequestBody Map<String, Integer> payload, @RequestHeader("Authorization") String token) {
        try {
            String username = userService.getUsernameFromToken(token.replace("Bearer ", ""));
            if (username == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("无效的 token");
            }
            int days = payload.get("days");
            userService.borrowBook(username, isbn, days);
            return ResponseEntity.ok().body("借阅成功");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/return/{isbn}")
    public ResponseEntity<?> returnBook(@PathVariable String isbn, @RequestHeader("Authorization") String token) {
        try {
            String username = userService.getUsernameFromToken(token.replace("Bearer ", ""));
            if (username == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("无效的 token");
            }
            userService.returnBook(username, isbn);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/borrowed")
    public ResponseEntity<?> getBorrowedBooks(@RequestHeader("Authorization") String token) {
        try {
            String username = userService.getUsernameFromToken(token.replace("Bearer ", ""));
            if (username == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("无效的 token");
            }
            List<Book> borrowedBooks = userService.getBorrowedBooks(username);
            return ResponseEntity.ok(borrowedBooks);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 可以添加一个新的方法来获取所有可借阅的书籍
    @GetMapping("/available")
    public ResponseEntity<?> getAvailableBooks() {
        try {
            List<Book> availableBooks = userService.getAllBooks().stream()
                .filter(book -> !book.isBorrowed())
                .collect(Collectors.toList());
            return ResponseEntity.ok(availableBooks);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/all-with-status")
    public ResponseEntity<?> getAllBooksWithStatus(@RequestHeader("Authorization") String token) {
        System.out.println("Received request for all books with status");
        System.out.println("Token: " + token);
        try {
            String jwtToken = token.replace("Bearer ", "");
            String username = userService.getUsernameFromToken(jwtToken);
            System.out.println("Username from token: " + username);
            if (username == null) {
                System.out.println("Invalid token: username is null");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("无效的 token");
            }
            List<Map<String, Object>> booksWithStatus = userService.getAllBooksWithStatus();
            System.out.println("Books with status: " + booksWithStatus);
            return ResponseEntity.ok(booksWithStatus);
        } catch (Exception e) {
            System.err.println("Error in getAllBooksWithStatus: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token 验证失败: " + e.getMessage());
        }
    }
}
