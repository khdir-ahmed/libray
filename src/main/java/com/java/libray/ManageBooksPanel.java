package com.java.libray;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class ManageBooksPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;

    private static final Color BG       = new Color(245, 242, 235);
    private static final Color DARK     = new Color(30, 20, 10);
    private static final Color ACCENT   = new Color(140, 90, 40);
    private static final Color ROW_ODD  = new Color(255, 253, 248);
    private static final Color ROW_EVEN = new Color(240, 236, 225);

    private static final String[] COLS = {
        "ID", "Title", "Author", "Genre", "ISBN", "Available", "Total"
    };

    public ManageBooksPanel() {
        setBackground(BG);
        setLayout(new BorderLayout(0, 0));
        buildUI();
        refresh();
    }

    private void buildUI() {
        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(220, 210, 190));
        header.setBorder(new EmptyBorder(12, 16, 12, 16));

        JLabel title = new JLabel("Manage Books  (Admin)");
        title.setFont(new Font("Georgia", Font.BOLD, 16));
        title.setForeground(DARK);
        header.add(title, BorderLayout.WEST);

        // Table
        tableModel = new DefaultTableModel(COLS, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel) {
            public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row))
                    c.setBackground(row % 2 == 0 ? ROW_ODD : ROW_EVEN);
                c.setForeground(DARK);
                return c;
            }
        };
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.setRowHeight(28);
        table.setSelectionBackground(new Color(200, 170, 110, 120));
        table.setGridColor(new Color(210, 195, 170));
        table.setShowVerticalLines(false);
        table.setFillsViewportHeight(true);

        JTableHeader th = table.getTableHeader();
        th.setFont(new Font("SansSerif", Font.BOLD, 12));
        th.setBackground(new Color(50, 30, 15));
        th.setForeground(new Color(230, 200, 150));
        th.setPreferredSize(new Dimension(0, 32));
        ((DefaultTableCellRenderer) th.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.LEFT);

        int[] widths = {35, 230, 150, 90, 130, 70, 50};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(ROW_ODD);

        // Bottom action bar
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        bottom.setBackground(new Color(220, 210, 190));
        bottom.setBorder(new EmptyBorder(4, 8, 4, 8));

        JButton addBtn    = makeBtn("+ Add Book",    new Color(30, 100, 40),  Color.WHITE);
        JButton editBtn   = makeBtn("✎ Edit Book",   new Color(80, 50, 20),   Color.WHITE);
        JButton deleteBtn = makeBtn("✕ Delete Book", new Color(140, 30, 30),  Color.WHITE);
        JButton refreshBtn = makeBtn("↺ Refresh",    new Color(160, 140, 110), DARK);

        addBtn.addActionListener(e -> showAddDialog());
        editBtn.addActionListener(e -> showEditDialog());
        deleteBtn.addActionListener(e -> deleteSelected());
        refreshBtn.addActionListener(e -> refresh());

        bottom.add(refreshBtn);
        bottom.add(addBtn);
        bottom.add(editBtn);
        bottom.add(deleteBtn);

        add(header, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    private JButton makeBtn(String text, Color bg, Color fg) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.BOLD, 13));
        b.setBackground(bg);
        b.setForeground(fg);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(new EmptyBorder(7, 18, 7, 18));
        return b;
    }

    public void refresh() {
        tableModel.setRowCount(0);
        List<Book> books = DataManager.getInstance().getBooks();
        for (Book b : books) {
            tableModel.addRow(new Object[]{
                b.getId(), b.getTitle(), b.getAuthor(), b.getGenre(),
                b.getIsbn(), b.getAvailableCopies(), b.getTotalCopies()
            });
        }
    }

    // ── Add ─────────────────────────────────────────────────────────────────

    private void showAddDialog() {
        JTextField titleF  = new JTextField(24);
        JTextField authorF = new JTextField(24);
        JTextField genreF  = new JTextField(16);
        JTextField isbnF   = new JTextField(18);
        JSpinner   copiesS = new JSpinner(new SpinnerNumberModel(1, 0, 9999, 1));

        JPanel form = buildForm(
            new String[]{"Title:", "Author:", "Genre:", "ISBN:", "Total Copies:"},
            new JComponent[]{titleF, authorF, genreF, isbnF, copiesS}
        );

        int result = JOptionPane.showConfirmDialog(
            this, form, "Add New Book", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION) return;

        String title  = titleF.getText().trim();
        String author = authorF.getText().trim();
        String genre  = genreF.getText().trim();
        String isbn   = isbnF.getText().trim();
        int    copies = (int) copiesS.getValue();

        if (title.isEmpty() || author.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Title and Author are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        DataManager.getInstance().addBook(title, author, genre, isbn, copies);
        refresh();
        JOptionPane.showMessageDialog(this, "Book \"" + title + "\" added successfully.", "Added", JOptionPane.INFORMATION_MESSAGE);
    }

    // ── Edit ────────────────────────────────────────────────────────────────

    private void showEditDialog() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a book to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int    id     = (int)    tableModel.getValueAt(row, 0);
        String curTitle  = (String) tableModel.getValueAt(row, 1);
        String curAuthor = (String) tableModel.getValueAt(row, 2);
        String curGenre  = (String) tableModel.getValueAt(row, 3);
        String curIsbn   = (String) tableModel.getValueAt(row, 4);
        int    curTotal  = (int)    tableModel.getValueAt(row, 6);

        JTextField titleF  = new JTextField(curTitle,  24);
        JTextField authorF = new JTextField(curAuthor, 24);
        JTextField genreF  = new JTextField(curGenre,  16);
        JTextField isbnF   = new JTextField(curIsbn,   18);
        JSpinner   copiesS = new JSpinner(new SpinnerNumberModel(curTotal, 0, 9999, 1));

        JPanel form = buildForm(
            new String[]{"Title:", "Author:", "Genre:", "ISBN:", "Total Copies:"},
            new JComponent[]{titleF, authorF, genreF, isbnF, copiesS}
        );

        int result = JOptionPane.showConfirmDialog(
            this, form, "Edit Book  (ID: " + id + ")", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION) return;

        String title  = titleF.getText().trim();
        String author = authorF.getText().trim();
        String genre  = genreF.getText().trim();
        String isbn   = isbnF.getText().trim();
        int    copies = (int) copiesS.getValue();

        if (title.isEmpty() || author.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Title and Author are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        DataManager.getInstance().updateBook(id, title, author, genre, isbn, copies);
        refresh();
        JOptionPane.showMessageDialog(this, "Book updated successfully.", "Updated", JOptionPane.INFORMATION_MESSAGE);
    }

    // ── Delete ──────────────────────────────────────────────────────────────

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a book to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int    id    = (int)    tableModel.getValueAt(row, 0);
        String title = (String) tableModel.getValueAt(row, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
            "<html><b>Delete this book?</b><br><br>\"" + title + "\"<br><br>" +
            "<font color='red'>This action cannot be undone.</font></html>",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) return;

        DataManager.getInstance().deleteBook(id);
        refresh();
        JOptionPane.showMessageDialog(this, "Book \"" + title + "\" deleted.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
    }

    // ── Helpers ─────────────────────────────────────────────────────────────

    private JPanel buildForm(String[] labels, JComponent[] fields) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(8, 4, 4, 4));
        GridBagConstraints lc = new GridBagConstraints();
        lc.anchor = GridBagConstraints.WEST;
        lc.insets = new Insets(6, 4, 2, 8);
        GridBagConstraints fc = new GridBagConstraints();
        fc.fill = GridBagConstraints.HORIZONTAL;
        fc.weightx = 1.0;
        fc.insets = new Insets(6, 0, 2, 4);

        for (int i = 0; i < labels.length; i++) {
            lc.gridy = fc.gridy = i;
            lc.gridx = 0; fc.gridx = 1;
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
            panel.add(lbl, lc);
            panel.add(fields[i], fc);
        }
        return panel;
    }
}
