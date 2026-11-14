package com.java.libray;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Reservation {

    public enum Status { PENDING, ACTIVE, REJECTED, RETURNED, RETURN_PENDING }

    private static int counter = 1;
    private int    reservationId;
    private final String username;
    private final Book   book;
    private final LocalDate reservationDate;
    private final LocalDate dueDate;
    private Status status;
    private int copyNumber; // 0 = not yet assigned

    /** Constructor used when loading an existing reservation from the database. */
    Reservation(int id, String username, Book book, Status status, LocalDate reservationDate, LocalDate dueDate, int copyNumber) {
        this.reservationId   = id;
        this.username        = username;
        this.book            = book;
        this.reservationDate = reservationDate;
        this.dueDate         = dueDate;
        this.status          = status;
        this.copyNumber      = copyNumber;
    }

    /** Creates a PENDING request — book copies are NOT decremented yet. */
    public Reservation(String username, Book book) {
        this.reservationId   = counter++;
        this.username        = username;
        this.book            = book;
        this.reservationDate = LocalDate.now();
        this.dueDate         = reservationDate.plusDays(14);
        this.status          = Status.PENDING;
    }

    public int    getReservationId() { return reservationId; }
    public String getUsername()      { return username; }
    public Book   getBook()          { return book; }
    public Status getStatus()        { return status; }
    public int    getCopyNumber()    { return copyNumber; }

    public boolean isPending()       { return status == Status.PENDING;        }
    public boolean isActive()        { return status == Status.ACTIVE;         }
    public boolean isReturned()      { return status == Status.RETURNED;       }
    public boolean isReturnPending() { return status == Status.RETURN_PENDING; }

    /**
     * Admin approves: reserves a copy. Returns false if no copies available.
     */
    public boolean approve() {
        if (status != Status.PENDING) return false;
        if (!book.reserve()) return false;
        status = Status.ACTIVE;
        return true;
    }

    /** Admin rejects the pending request. */
    public void reject() {
        if (status == Status.PENDING) status = Status.REJECTED;
    }

    /** User / admin marks the book as returned. */
    public void markReturned() {
        if (status == Status.ACTIVE) {
            status = Status.RETURNED;
            book.returnBook();
        }
    }

    public String getStatusText() {
        switch (status) {
            case PENDING:        return "Pending";
            case ACTIVE:         return "Active";
            case REJECTED:       return "Rejected";
            case RETURNED:       return "Returned";
            case RETURN_PENDING: return "Return Pending";
            default:             return "";
        }
    }

    public String getReservationDate() {
        return reservationDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
    }

    public String getDueDate() {
        return dueDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
    }
}
