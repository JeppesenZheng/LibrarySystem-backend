package src.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import src.model.Book;
import src.repository.BookRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Optional<Book> getBookByISBN(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }

    public Book addBook(Book book) {
        return bookRepository.save(book);
    }

    public void deleteBook(String isbn) {
        bookRepository.findByIsbn(isbn).ifPresent(book -> bookRepository.delete(book));
    }

    public Book updateBook(Book book) {
        return bookRepository.save(book);
    }

    public Map<String, Object> getBookStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        List<Book> allBooks = bookRepository.findAll();

        long totalBooks = allBooks.size();
        long borrowedBooks = allBooks.stream().filter(Book::isBorrowed).count();
        long availableBooks = totalBooks - borrowedBooks;
        double borrowRate = totalBooks > 0 ? (double) borrowedBooks / totalBooks * 100 : 0;

        statistics.put("totalBooks", totalBooks);
        statistics.put("borrowedBooks", borrowedBooks);
        statistics.put("availableBooks", availableBooks);
        statistics.put("borrowRate", String.format("%.2f%%", borrowRate));

        List<Map<String, Object>> topBorrowedBooks = allBooks.stream()
            .sorted((b1, b2) -> Integer.compare(b2.getBorrowCount(), b1.getBorrowCount()))
            .limit(10)
            .map(book -> {
                Map<String, Object> bookInfo = new HashMap<>();
                bookInfo.put("title", book.getTitle());
                bookInfo.put("borrowCount", book.getBorrowCount());
                return bookInfo;
            })
            .collect(Collectors.toList());
        statistics.put("topBorrowedBooks", topBorrowedBooks);

        return statistics;
    }
}