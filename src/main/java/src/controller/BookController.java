package src.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import src.model.Book;
import src.service.UserService;

import java.util.List;

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
}
