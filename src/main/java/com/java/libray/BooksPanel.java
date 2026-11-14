package com.java.libray;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BooksPanel extends JPanel {
    private final String username;
    private final MainFrame parent;
    private JTextField searchField;
    private JComboBox<String> genreBox;
    private JTable table;
    private DefaultTableModel tableModel;

    private static final Color BG       = new Color(245, 242, 235);
    private static final Color DARK     = new Color(30, 20, 10);
    private static final Color ACCENT   = new Color(140, 90, 40);
    private static final Color AVAIL    = new Color(20, 100, 40);
    private static final Color LAST     = new Color(160, 90, 0);
    private static final Color UNAVAIL  = new Color(150, 30, 30);
    private static final Color ROW_ODD  = new Color(255, 253, 248);
    private static final Color ROW_EVEN = new Color(240, 236, 225);

    private static final String[] COLS = {
        "ID", "Title", "Author", "Genre", "ISBN", "Available", "Total", "Status"
    };

    public BooksPanel(String username, MainFrame parent) {
        this.username = username;
        this.parent = parent;
        setBackground(BG);
        setLayout(new BorderLayout(0, 0));
        buildUI();
        loadBooks("", "All");
    }

    private void buildUI() {
        // Search bar
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        searchPanel.setBackground(new Color(220, 210, 190));
        searchPanel.setBorder(new EmptyBorder(6, 8, 6, 8));

        searchField = new JTextField(22);
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        searchField.putClientProperty("JTextField.placeholderText", "Search title, author or ISBN...");
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 150, 110), 1),
            new EmptyBorder(5, 10, 5, 10)
        ));

        List<String> genres = DataManager.getInstance().getGenres();
        genreBox = new JComboBox<>(genres.toArray(new String[0]));
        genreBox.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JButton searchBtn = makeBtn("Search", new Color(80, 50, 20), Color.WHITE);
        JButton clearBtn  = makeBtn("Clear",  new Color(160, 140, 110), DARK);

        searchBtn.addActionListener(e -> doSearch());
        clearBtn.addActionListener(e -> { searchField.setText(""); genreBox.setSelectedIndex(0); doSearch(); });
        searchField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) { if (e.getKeyCode() == KeyEvent.VK_ENTER) doSearch(); }
        });

        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(new JLabel("Genre:"));
        searchPanel.add(genreBox);
        searchPanel.add(searchBtn);
        searchPanel.add(clearBtn);

        // Table
        tableModel = new DefaultTableModel(COLS, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel) {
            public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? ROW_ODD : ROW_EVEN);
                }
                // Color the Status column
                if (col == 7) {
                    String val = (String) getValueAt(row, col);
                    if (val == null) { c.setForeground(DARK); return c; }
                    if (val.equals("Not Available"))  c.setForeground(UNAVAIL);
                    else if (val.equals("Last Copy!")) c.setForeground(LAST);
                    else                               c.setForeground(AVAIL);
                    ((JComponent) c).setFont(c.getFont().deriveFont(Font.BOLD));
                } else {
                    c.setForeground(DARK);
                }
                return c;
            }
        };

        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.setRowHeight(28);
        table.setSelectionBackground(new Color(200, 170, 110, 120));
        table.setGridColor(new Color(210, 195, 170));
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setShowVerticalLines(false);
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 12));
        header.setBackground(new Color(50, 30, 15));
        header.setForeground(new Color(230, 200, 150));
        header.setPreferredSize(new Dimension(0, 32));
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.LEFT);

        // Column widths
        int[] widths = {35, 220, 150, 90, 130, 70, 50, 130};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(ROW_ODD);

        // Bottom action bar
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        bottomBar.setBackground(new Color(220, 210, 190));
        bottomBar.setBorder(new EmptyBorder(4, 8, 4, 8));

        JLabel hint = new JLabel("Select a book and click Request to Borrow");
        hint.setFont(new Font("SansSerif", Font.ITALIC, 12));
        hint.setForeground(ACCENT);

        JButton reserveBtn = makeBtn("Request to Borrow", new Color(30, 20, 10), Color.WHITE);
        reserveBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        reserveBtn.addActionListener(e -> reserveSelected());

        bottomBar.add(hint);
        bottomBar.add(Box.createHorizontalStrut(20));
        bottomBar.add(reserveBtn);

        add(searchPanel, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(bottomBar, BorderLayout.SOUTH);
    }

    private JButton makeBtn(String text, Color bg, Color fg) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.PLAIN, 13));
        b.setBackground(bg);
        b.setForeground(fg);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(new EmptyBorder(6, 16, 6, 16));
        return b;
    }

    private void doSearch() {
        loadBooks(searchField.getText().trim(), (String) genreBox.getSelectedItem());
    }

    private void loadBooks(String query, String genre) {
        tableModel.setRowCount(0);
        List<Book> books = DataManager.getInstance().searchBooks(query, genre);
        for (Book b : books) {
            tableModel.addRow(new Object[]{
                b.getId(), b.getTitle(), b.getAuthor(), b.getGenre(),
                b.getIsbn(), b.getAvailableCopies(), b.getTotalCopies(),
                b.getAvailabilityText()
            });
        }
    }

    private void reserveSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a book first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        DataManager.getInstance().applyOverduePenalties();
        if (DataManager.getInstance().isBlocked(username)) {
            int pts = DataManager.getInstance().getPenaltyPoints(username);
            JOptionPane.showMessageDialog(this,
                "<html><b>You are blocked.</b><br><br>" +
                "You currently have <b>" + pts + " penalty points</b> (limit is 2).<br>" +
                "You cannot borrow any new books until an administrator resets your account.</html>",
                "Account Blocked", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int bookId = (int) tableModel.getValueAt(row, 0);
        Book book = DataManager.getInstance().getBooks().stream()
            .filter(b -> b.getId() == bookId).findFirst().orElse(null);
        if (book == null) return;

        if (book.isAvailable()) {
            confirmBorrowRequest(book);
        } else {
            offerReservation(book);
        }
    }

    private void confirmBorrowRequest(Book book) {
        int confirm = JOptionPane.showConfirmDialog(this,
            "<html><b>Send a borrow request for this book?</b><br><br>" +
            "Title:  " + book.getTitle() + "<br>" +
            "Author: " + book.getAuthor() + "<br>" +
            "Copies available: " + book.getAvailableCopies() + "<br><br>" +
            "Your request will be reviewed by an admin.</html>",
            "Confirm Borrow Request", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        Reservation r = DataManager.getInstance().makeRequest(username, book);
        if (r != null) {
            doSearch();
            JOptionPane.showMessageDialog(this,
                "<html><b>Request Sent!</b><br><br>" +
                "Request ID: #" + r.getReservationId() + "<br>" +
                "Book: " + book.getTitle() + "<br>" +
                "Status: <b>Pending</b> — waiting for admin approval.</html>",
                "Request Submitted", JOptionPane.INFORMATION_MESSAGE);
            parent.switchToReservations();
        } else {
            JOptionPane.showMessageDialog(this,
                "You already have a pending or active request for this book.",
                "Duplicate Request", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void offerReservation(Book book) {
        if (DataManager.getInstance().hasActiveHold(username, book.getId())) {
            JOptionPane.showMessageDialog(this,
                "You already have an active reservation on this book.",
                "Already Reserved", JOptionPane.WARNING_MESSAGE);
            return;
        }
        LocalDate earliest = DataManager.getInstance().getEarliestDueDate(book.getId());
        String dateStr = earliest != null
            ? "Earliest expected return: <b>" + earliest.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")) + "</b>"
            : "No return date available yet.";

        Object[] options = { "Reserve Next Copy", "Cancel" };
        int choice = JOptionPane.showOptionDialog(this,
            "<html><b>All copies of \"" + book.getTitle() + "\" are currently borrowed.</b><br><br>" +
            "Author: " + book.getAuthor() + "<br>" +
            "<font color='#cc0000'>No copies available right now.</font><br>" +
            dateStr + "<br><br>" +
            "Would you like to reserve the next copy that becomes available?</html>",
            "No Copies Available",
            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
            null, options, options[0]);
        if (choice != 0) return;

        BookHold hold = DataManager.getInstance().createHold(username, book);
        if (hold == null) {
            JOptionPane.showMessageDialog(this,
                "<html>Could not create a reservation for this book.<br>" +
                "Either every copy is already reserved by other members, " +
                "or the book has no borrowed copies to wait on.</html>",
                "Reservation Unavailable", JOptionPane.WARNING_MESSAGE);
            return;
        }
        doSearch();
        JOptionPane.showMessageDialog(this,
            "<html><b>Reservation Confirmed!</b><br><br>" +
            "Book: " + book.getTitle() + "<br>" +
            "Reserved Copy: <b>#" + hold.getCopyNumber() + "</b><br>" +
            "Expected available: <b>" + hold.getExpectedDateText() + "</b><br><br>" +
            "We'll hold this copy for you once it's returned.</html>",
            "Copy Reserved", JOptionPane.INFORMATION_MESSAGE);
        parent.switchToReservations();
    }
}
