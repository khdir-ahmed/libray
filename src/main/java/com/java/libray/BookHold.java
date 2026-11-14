package com.java.libray;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class BookHold {
    private final int       id;
    private final String    username;
    private final Book      book;
    private final int       copyNumber;
    private final LocalDate expectedDate;
    private final LocalDate createdDate;

    public BookHold(int id, String username, Book book, int copyNumber,
                    LocalDate expectedDate, LocalDate createdDate) {
        this.id           = id;
        this.username     = username;
        this.book         = book;
        this.copyNumber   = copyNumber;
        this.expectedDate = expectedDate;
        this.createdDate  = createdDate;
    }

    public int       getId()           { return id; }
    public String    getUsername()     { return username; }
    public Book      getBook()         { return book; }
    public int       getCopyNumber()   { return copyNumber; }
    public LocalDate getExpectedDate() { return expectedDate; }
    public LocalDate getCreatedDate()  { return createdDate; }

    public String getExpectedDateText() {
        return expectedDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
    }
    public String getCreatedDateText() {
        return createdDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
    }
}
