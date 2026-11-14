
package com.java.libray;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

public class DataManager {

    private static DataManager instance;
    private final Connection conn;

    private static final String DB_URL  =
            "jdbc:mysql://localhost:3306/bookstore?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";

    private DataManager() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect to database: " + e.getMessage(), e);
        }
    }

    public static DataManager getInstance() {
        if (instance == null) instance = new DataManager();
        return instance;
    }

    // ── Authentication ───────────────────────────────────────────────────────

    public boolean authenticate(String username, String password) {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT 1 FROM users WHERE username=? AND password=?")) {
            ps.setString(1, username);
            ps.setString(2, password);
            boolean ok = ps.executeQuery().next();
            if (ok) applyOverduePenalties();
            return ok;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    // ── Penalty system ───────────────────────────────────────────────────────

    /** Scans every ACTIVE / RETURN_PENDING reservation past its due date that
     *  hasn't been counted yet, and adds 1 penalty point per overdue book.
     *  Users reaching 3 penalty points are automatically blocked. */
    public void applyOverduePenalties() {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT id, username FROM reservations " +
                "WHERE status IN ('ACTIVE','RETURN_PENDING') " +
                "AND due_date < CURDATE() AND penalty_counted = 0")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int resId = rs.getInt("id");
                String user = rs.getString("username");
                try (PreparedStatement up = conn.prepareStatement(
                        "UPDATE users SET penalty_points = penalty_points + 1, " +
                        "is_blocked = CASE WHEN penalty_points + 1 >= 2 THEN 1 ELSE is_blocked END " +
                        "WHERE username=?")) {
                    up.setString(1, user);
                    up.executeUpdate();
                }
                try (PreparedStatement up = conn.prepareStatement(
                        "UPDATE reservations SET penalty_counted=1 WHERE id=?")) {
                    up.setInt(1, resId);
                    up.executeUpdate();
                }
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public int getPenaltyPoints(String username) {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT penalty_points FROM users WHERE username=?")) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public boolean isBlocked(String username) {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT is_blocked FROM users WHERE username=?")) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) == 1;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    /** Admin action: clear penalties and unblock the user. */
    public boolean resetPenalty(String username) {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE users SET penalty_points=0, is_blocked=0 WHERE username=?")) {
            ps.setString(1, username);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public boolean isAdmin(String username) {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT is_admin FROM users WHERE username=?")) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt("is_admin") == 1;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    // ── Books ─────────────────────────────────────────────────────────────────

    public List<Book> getBooks() {
        List<Book> books = new ArrayList<>();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM books ORDER BY id")) {
            while (rs.next()) books.add(rowToBook(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return books;
    }

    public List<Book> searchBooks(String query, String genre) {
        String q = "%" + query.toLowerCase() + "%";
        String sql = genre.equals("All")
            ? "SELECT * FROM books WHERE LOWER(title) LIKE ? OR LOWER(author) LIKE ? OR isbn LIKE ?"
            : "SELECT * FROM books WHERE (LOWER(title) LIKE ? OR LOWER(author) LIKE ? OR isbn LIKE ?) AND genre=?";
        List<Book> result = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, q);
            ps.setString(2, q);
            ps.setString(3, q);
            if (!genre.equals("All")) ps.setString(4, genre);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) result.add(rowToBook(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return result;
    }

    public List<String> getGenres() {
        List<String> genres = new ArrayList<>();
        genres.add("All");
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT DISTINCT genre FROM books ORDER BY genre")) {
            while (rs.next()) genres.add(rs.getString("genre"));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return genres;
    }

    public Book addBook(String title, String author, String genre, String isbn, int totalCopies) {
        int bookId;
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO books (title, author, genre, isbn, total_copies, available_copies) VALUES (?,?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, title);
            ps.setString(2, author);
            ps.setString(3, genre);
            ps.setString(4, isbn);
            ps.setInt(5, totalCopies);
            ps.setInt(6, totalCopies);
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (!keys.next()) return null;
            bookId = keys.getInt(1);
        } catch (SQLException e) { throw new RuntimeException(e); }
        insertCopies(bookId, 1, totalCopies);
        return getBookById(bookId);
    }

    private void insertCopies(int bookId, int from, int to) {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT IGNORE INTO book_copies (book_id, copy_number, is_available) VALUES (?,?,1)")) {
            for (int i = from; i <= to; i++) {
                ps.setInt(1, bookId);
                ps.setInt(2, i);
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public boolean updateBook(int id, String title, String author, String genre, String isbn, int totalCopies) {
        Book existing = getBookById(id);
        if (existing == null) return false;
        int oldTotal     = existing.getTotalCopies();
        int reserved     = oldTotal - existing.getAvailableCopies();
        int newAvailable = Math.max(totalCopies - reserved, 0);
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE books SET title=?, author=?, genre=?, isbn=?, total_copies=?, available_copies=? WHERE id=?")) {
            ps.setString(1, title);
            ps.setString(2, author);
            ps.setString(3, genre);
            ps.setString(4, isbn);
            ps.setInt(5, totalCopies);
            ps.setInt(6, newAvailable);
            ps.setInt(7, id);
            if (ps.executeUpdate() == 0) return false;
        } catch (SQLException e) { throw new RuntimeException(e); }
        if (totalCopies > oldTotal) {
            // Add new copy rows
            insertCopies(id, oldTotal + 1, totalCopies);
        } else if (totalCopies < oldTotal) {
            // Remove only available (not borrowed) copies from the high end
            try (PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM book_copies WHERE book_id=? AND copy_number>? AND is_available=1")) {
                ps.setInt(1, id);
                ps.setInt(2, totalCopies);
                ps.executeUpdate();
            } catch (SQLException e) { throw new RuntimeException(e); }
        }
        return true;
    }

    public boolean deleteBook(int id) {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM books WHERE id=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    // ── Reservations ─────────────────────────────────────────────────────────

    public Reservation makeRequest(String username, Book book) {
        applyOverduePenalties();
        if (isBlocked(username)) return null;
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT 1 FROM reservations WHERE username=? AND book_id=? AND status IN ('PENDING','ACTIVE')")) {
            ps.setString(1, username);
            ps.setInt(2, book.getId());
            if (ps.executeQuery().next()) return null; // duplicate
        } catch (SQLException e) { throw new RuntimeException(e); }

        LocalDate today = LocalDate.now();
        LocalDate due   = today.plusDays(14);
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO reservations (username, book_id, status, reservation_date, due_date) VALUES (?,?,'PENDING',?,?)",
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, username);
            ps.setInt(2, book.getId());
            ps.setDate(3, Date.valueOf(today));
            ps.setDate(4, Date.valueOf(due));
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return getReservationById(keys.getInt(1));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return null;
    }

    public boolean approveRequest(int reservationId) {
        Reservation r = getReservationById(reservationId);
        if (r == null || !r.isPending()) return false;
        Book book = getBookById(r.getBook().getId());
        if (book == null || !book.isAvailable()) return false;

        // Find lowest available copy number
        int copyNumber;
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT MIN(copy_number) FROM book_copies WHERE book_id=? AND is_available=1")) {
            ps.setInt(1, book.getId());
            ResultSet rs = ps.executeQuery();
            if (!rs.next() || rs.getObject(1) == null) return false;
            copyNumber = rs.getInt(1);
        } catch (SQLException e) { throw new RuntimeException(e); }

        // Assign copy to reservation and activate it
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE reservations SET status='ACTIVE', copy_number=? WHERE id=? AND status='PENDING'")) {
            ps.setInt(1, copyNumber);
            ps.setInt(2, reservationId);
            if (ps.executeUpdate() == 0) return false;
        } catch (SQLException e) { throw new RuntimeException(e); }

        // Mark copy as unavailable
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE book_copies SET is_available=0 WHERE book_id=? AND copy_number=?")) {
            ps.setInt(1, book.getId());
            ps.setInt(2, copyNumber);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }

        // Keep books.available_copies in sync
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE books SET available_copies = available_copies - 1 WHERE id=? AND available_copies > 0")) {
            ps.setInt(1, book.getId());
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }

        return true;
    }

    public boolean rejectRequest(int reservationId) {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE reservations SET status='REJECTED' WHERE id=? AND status='PENDING'")) {
            ps.setInt(1, reservationId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public boolean requestReturn(int reservationId) {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE reservations SET status='RETURN_PENDING' WHERE id=? AND status='ACTIVE'")) {
            ps.setInt(1, reservationId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public boolean approveReturn(int reservationId) {
        Reservation r = getReservationById(reservationId);
        if (r == null || !r.isReturnPending()) return false;
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE reservations SET status='RETURNED' WHERE id=? AND status='RETURN_PENDING'")) {
            ps.setInt(1, reservationId);
            if (ps.executeUpdate() == 0) return false;
        } catch (SQLException e) { throw new RuntimeException(e); }

        // Mark copy as available again
        if (r.getCopyNumber() > 0) {
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE book_copies SET is_available=1 WHERE book_id=? AND copy_number=?")) {
                ps.setInt(1, r.getBook().getId());
                ps.setInt(2, r.getCopyNumber());
                ps.executeUpdate();
            } catch (SQLException e) { throw new RuntimeException(e); }
        }

        // Keep books.available_copies in sync
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE books SET available_copies = available_copies + 1 WHERE id=?")) {
            ps.setInt(1, r.getBook().getId());
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
        return true;
    }

    public boolean returnBook(int reservationId) {
        Reservation r = getReservationById(reservationId);
        if (r == null || !r.isActive()) return false;
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE reservations SET status='RETURNED' WHERE id=? AND status='ACTIVE'")) {
            ps.setInt(1, reservationId);
            if (ps.executeUpdate() == 0) return false;
        } catch (SQLException e) { throw new RuntimeException(e); }
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE books SET available_copies = available_copies + 1 WHERE id=?")) {
            ps.setInt(1, r.getBook().getId());
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
        return true;
    }

    public List<Reservation> getUserReservations(String username) {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM reservations WHERE username=? ORDER BY id")) {
            ps.setString(1, username);
            return loadReservations(ps);
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    /** Returns the earliest due date among ACTIVE reservations for the given book, or null if none. */
    public LocalDate getEarliestDueDate(int bookId) {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT MIN(due_date) FROM reservations WHERE book_id=? AND status='ACTIVE'")) {
            ps.setInt(1, bookId);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getDate(1) != null) return rs.getDate(1).toLocalDate();
        } catch (SQLException e) { throw new RuntimeException(e); }
        return null;
    }

    public List<Reservation> getAllReservations() {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM reservations ORDER BY id")) {
            return loadReservations(ps);
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    // ── Book holds (advance reservations) ────────────────────────────────────

    /** True if the user already has an unfulfilled hold for this book. */
    public boolean hasActiveHold(String username, int bookId) {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT 1 FROM book_holds WHERE username=? AND book_id=? AND fulfilled=0")) {
            ps.setString(1, username);
            ps.setInt(2, bookId);
            return ps.executeQuery().next();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    /** Reserves the soonest-returning copy of a fully-borrowed book for the user.
     *  Returns null if the user is blocked, already holds the book, or no copy
     *  can be assigned (book has no borrowed copies, or every copy is already held). */
    public BookHold createHold(String username, Book book) {
        applyOverduePenalties();
        if (isBlocked(username))             return null;
        if (book.isAvailable())              return null;
        if (hasActiveHold(username, book.getId())) return null;

        // Pick the borrowed copy with the soonest due_date that isn't already held.
        int       copyNumber;
        LocalDate expectedDate;
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT r.copy_number, r.due_date FROM reservations r " +
                "WHERE r.book_id=? AND r.status IN ('ACTIVE','RETURN_PENDING') " +
                "AND r.copy_number IS NOT NULL " +
                "AND r.copy_number NOT IN (" +
                "    SELECT copy_number FROM book_holds WHERE book_id=? AND fulfilled=0" +
                ") ORDER BY r.due_date ASC LIMIT 1")) {
            ps.setInt(1, book.getId());
            ps.setInt(2, book.getId());
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) return null;
            copyNumber   = rs.getInt("copy_number");
            expectedDate = rs.getDate("due_date").toLocalDate();
        } catch (SQLException e) { throw new RuntimeException(e); }

        LocalDate today = LocalDate.now();
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO book_holds (username, book_id, copy_number, expected_date, created_date) " +
                "VALUES (?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, username);
            ps.setInt(2, book.getId());
            ps.setInt(3, copyNumber);
            ps.setDate(4, Date.valueOf(expectedDate));
            ps.setDate(5, Date.valueOf(today));
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (!keys.next()) return null;
            return new BookHold(keys.getInt(1), username, book, copyNumber, expectedDate, today);
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public List<BookHold> getUserHolds(String username) {
        List<BookHold> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM book_holds WHERE username=? AND fulfilled=0 ORDER BY id")) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Book b = getBookById(rs.getInt("book_id"));
                if (b == null) continue;
                list.add(new BookHold(
                    rs.getInt("id"),
                    rs.getString("username"),
                    b,
                    rs.getInt("copy_number"),
                    rs.getDate("expected_date").toLocalDate(),
                    rs.getDate("created_date").toLocalDate()
                ));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    // ── User management ─────────────────────────────────────────────────────

    public String getUserPhone(String username) {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT phone FROM users WHERE username=?")) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("phone");
        } catch (SQLException e) { throw new RuntimeException(e); }
        return "";
    }

    public List<String> getUsernames() {
        List<String> list = new ArrayList<>();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT username FROM users ORDER BY username")) {
            while (rs.next()) list.add(rs.getString("username"));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    public boolean addUser(String username, String password, String phone) {
        return addUser(username, password, phone, false);
    }

    public boolean addUser(String username, String password, String phone, boolean isAdmin) {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO users (username, password, is_admin, phone) VALUES (?,?,?,?)")) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setInt(3, isAdmin ? 1 : 0);
            ps.setString(4, phone);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false; // duplicate username
        }
    }

    public boolean deleteUser(String username) {
        if ("admin".equals(username)) return false;
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM users WHERE username=?")) {
            ps.setString(1, username);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    /** Returns true if the username + phone combination exists in the database. */
    public boolean verifyUsernameAndPhone(String username, String phone) {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT 1 FROM users WHERE username=? AND phone=?")) {
            ps.setString(1, username);
            ps.setString(2, phone);
            return ps.executeQuery().next();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public boolean updatePassword(String username, String newPassword) {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE users SET password=? WHERE username=?")) {
            ps.setString(1, newPassword);
            ps.setString(2, username);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Book rowToBook(ResultSet rs) throws SQLException {
        return new Book(
            rs.getInt("id"),
            rs.getString("title"),
            rs.getString("author"),
            rs.getString("genre"),
            rs.getString("isbn"),
            rs.getInt("total_copies"),
            rs.getInt("available_copies")
        );
    }

    private Book getBookById(int id) {
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM books WHERE id=?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rowToBook(rs);
        } catch (SQLException e) { throw new RuntimeException(e); }
        return null;
    }

    private Reservation getReservationById(int id) {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM reservations WHERE id=?")) {
            ps.setInt(1, id);
            List<Reservation> list = loadReservations(ps);
            return list.isEmpty() ? null : list.get(0);
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    private List<Reservation> loadReservations(PreparedStatement ps) throws SQLException {
        List<Reservation> list = new ArrayList<>();
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Book book = getBookById(rs.getInt("book_id"));
            if (book == null) continue;
            int copyNumber = rs.getObject("copy_number") != null ? rs.getInt("copy_number") : 0;
            list.add(new Reservation(
                rs.getInt("id"),
                rs.getString("username"),
                book,
                Reservation.Status.valueOf(rs.getString("status")),
                rs.getDate("reservation_date").toLocalDate(),
                rs.getDate("due_date").toLocalDate(),
                copyNumber
            ));
        }
        return list;
    }
}
