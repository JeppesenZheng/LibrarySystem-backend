package src.model;

import java.time.LocalDate;

import jakarta.persistence.*;

@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String author;
    private String isbn;
    private boolean borrowed;
    private LocalDate borrowDate;
    private int borrowDays;
    @Column(nullable = false)
    private Integer borrowCount = 0;

    public Book() { }

    public Book(String title, String author, String isbn) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.borrowed = false;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getISBN() {
        return isbn;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setISBN(String isbn) {
        this.isbn = isbn;
    }

    public boolean isBorrowed() {
        return borrowed;
    }

    public void setBorrowed(boolean borrowed) {
        this.borrowed = borrowed;
    }

    public Long getId() {
        return id;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate;
    }

    public int getBorrowDays() {
        return borrowDays;
    }

    public void setBorrowDays(int borrowDays) {
        this.borrowDays = borrowDays;
    }

    public Integer getBorrowCount() {
        return borrowCount == null ? 0 : borrowCount;
    }

    public void setBorrowCount(Integer borrowCount) {
        this.borrowCount = borrowCount == null ? 0 : borrowCount;
    }

    public void incrementBorrowCount() {
        this.borrowCount = (this.borrowCount == null ? 0 : this.borrowCount) + 1;
    }
}
