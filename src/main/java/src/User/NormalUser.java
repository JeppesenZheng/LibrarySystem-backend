package src.User;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import src.model.Book;
import src.model.User;

@Entity
@DiscriminatorValue("NORMAL")
public class NormalUser extends User {

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_borrowed_books",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "book_id")
    )
    private List<Book> borrowedBooks = new ArrayList<>();

    public NormalUser() { }
    public NormalUser(String name, String password) {
        super(name, password);
    }

    public void borrowBook(Book book) {
        if (!borrowedBooks.contains(book)) {
            borrowedBooks.add(book);
            System.out.println("Book added to user's borrowed list: " + book.getTitle());
        }
    }

    public void returnBook(Book book) {
        borrowedBooks.remove(book);
    }

    public List<Book> getBorrowedBooks() {
        return borrowedBooks;
    }
}
