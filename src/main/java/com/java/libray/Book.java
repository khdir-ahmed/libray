package com.java.libray;
public class Book {
    private final int id;
    private String title;
    private String author;
    private String genre;
    private String isbn;
    private int availableCopies;
    private int totalCopies;

    public Book(int id, String title, String author, String genre, String isbn, int totalCopies) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.isbn = isbn;
        this.totalCopies = totalCopies;
        this.availableCopies = totalCopies;
    }

    /** Constructor used when loading from database (availableCopies may differ from totalCopies). */
    Book(int id, String title, String author, String genre, String isbn, int totalCopies, int availableCopies) {
        this(id, title, author, genre, isbn, totalCopies);
        this.availableCopies = availableCopies;
    }

    public int getId()               { return id; }
    public String getTitle()         { return title; }
    public String getAuthor()        { return author; }
    public String getGenre()         { return genre; }
    public String getIsbn()          { return isbn; }
    public int getAvailableCopies()  { return availableCopies; }
    public int getTotalCopies()      { return totalCopies; }
    public boolean isAvailable()     { return availableCopies > 0; }

    public void setTitle(String title)   { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setGenre(String genre)   { this.genre = genre; }
    public void setIsbn(String isbn)     { this.isbn = isbn; }
    public void setTotalCopies(int newTotal) {
        int reserved = totalCopies - availableCopies;
        this.totalCopies = Math.max(newTotal, reserved);
        this.availableCopies = this.totalCopies - reserved;
    }

    public boolean reserve() {
        if (availableCopies > 0) { availableCopies--; return true; }
        return false;
    }

    public void returnBook() {
        if (availableCopies < totalCopies) availableCopies++;
    }

    public String getAvailabilityText() {
        if (availableCopies == 0)       return "Not Available";
        if (availableCopies == 1)       return "Last Copy!";
        return availableCopies + " Copies Available";
    }
}
