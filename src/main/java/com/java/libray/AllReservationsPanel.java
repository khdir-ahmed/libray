package com.java.libray;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class AllReservationsPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel summaryLabel;
    private JComboBox<String> userFilter;
    private JComboBox<String> statusFilter;

    private static final Color BG       = new Color(245, 242, 235);
    private static final Color DARK     = new Color(30, 20, 10);
    private static final Color ACCENT   = new Color(140, 90, 40);
    private static final Color ROW_ODD  = new Color(255, 253, 248);
    private static final Color ROW_EVN  = new Color(240, 236, 225);
    private static final Color PENDING      = new Color(180, 110, 0);
    private static final Color ACTIVE       = new Color(20, 100, 40);
    private static final Color REJECTED     = new Color(150, 30, 30);
    private static final Color DONE         = new Color(120, 120, 120);
    private static final Color RETURN_PEND  = new Color(0, 90, 160);

    private static final String[] COLS = {
        "Res. ID", "Username", "Book Title", "Author", "Genre", "Requested On", "Due Date", "Copy #", "Status"
    };

    public AllReservationsPanel() {
        setBackground(BG);
        setLayout(new BorderLayout());
        buildUI();
    }

    private void buildUI() {
        // ── Header ──────────────────────────────────────────────────────────
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(220, 210, 190));
        headerPanel.setBorder(new EmptyBorder(12, 16, 12, 16));

        JLabel title = new JLabel("All Borrow Requests");
        title.setFont(new Font("Georgia", Font.BOLD, 16));
        title.setForeground(DARK);

        summaryLabel = new JLabel();
        summaryLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        summaryLabel.setForeground(ACCENT);

        headerPanel.add(title, BorderLayout.WEST);
        headerPanel.add(summaryLabel, BorderLayout.EAST);

        // ── Filter bar ──────────────────────────────────────────────────────
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        filterBar.setBackground(new Color(230, 220, 200));
        filterBar.setBorder(new EmptyBorder(2, 8, 2, 8));

        userFilter   = new JComboBox<>();
        userFilter.setFont(new Font("SansSerif", Font.PLAIN, 13));
        statusFilter = new JComboBox<>(new String[]{"All Status", "Pending", "Active", "Return Pending", "Rejected", "Returned"});
        statusFilter.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JButton applyBtn = makeBtn("Apply Filter", new Color(80, 50, 20), Color.WHITE);
        JButton clearBtn = makeBtn("Clear",        new Color(160, 140, 110), DARK);

        applyBtn.addActionListener(e -> refresh());
        clearBtn.addActionListener(e -> {
            userFilter.setSelectedIndex(0);
            statusFilter.setSelectedIndex(0);
            refresh();
        });

        filterBar.add(new JLabel("User:"));
        filterBar.add(userFilter);
        filterBar.add(new JLabel("Status:"));
        filterBar.add(statusFilter);
        filterBar.add(applyBtn);
        filterBar.add(clearBtn);

        JPanel topSection = new JPanel(new BorderLayout());
        topSection.add(headerPanel, BorderLayout.NORTH);
        topSection.add(filterBar,   BorderLayout.SOUTH);

        // ── Table ────────────────────────────────────────────────────────────
        tableModel = new DefaultTableModel(COLS, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel) {
            public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row))
                    c.setBackground(row % 2 == 0 ? ROW_ODD : ROW_EVN);
                if (col == 8) {
                    String val = (String) getValueAt(row, col);
                    switch (val == null ? "" : val) {
                        case "Pending":        c.setForeground(PENDING);     break;
                        case "Active":         c.setForeground(ACTIVE);      break;
                        case "Rejected":       c.setForeground(REJECTED);    break;
                        case "Return Pending": c.setForeground(RETURN_PEND); break;
                        default:               c.setForeground(DONE);        break;
                    }
                    ((JComponent) c).setFont(c.getFont().deriveFont(
                        "Pending".equals(val) || "Active".equals(val) || "Return Pending".equals(val) ? Font.BOLD : Font.PLAIN));
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
        table.setShowVerticalLines(false);
        table.setFillsViewportHeight(true);

        JTableHeader th = table.getTableHeader();
        th.setFont(new Font("SansSerif", Font.BOLD, 12));
        th.setBackground(new Color(50, 30, 15));
        th.setForeground(new Color(230, 200, 150));
        th.setPreferredSize(new Dimension(0, 32));
        ((DefaultTableCellRenderer) th.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.LEFT);

        int[] widths = {60, 100, 170, 120, 70, 100, 100, 60, 90};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(ROW_ODD);

        // ── Bottom action bar ────────────────────────────────────────────────
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        bottom.setBackground(new Color(220, 210, 190));
        bottom.setBorder(new EmptyBorder(4, 8, 4, 8));

        JButton approveBtn = makeBtn("Approve",   new Color(30, 100, 40),   Color.WHITE);
        JButton rejectBtn  = makeBtn("Reject",    new Color(140, 30, 30),   Color.WHITE);
        JButton refreshBtn = makeBtn("↺ Refresh", new Color(160, 140, 110), DARK);

        approveBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        rejectBtn.setFont(new Font("SansSerif", Font.BOLD, 13));

        approveBtn.addActionListener(e -> approveSelected());
        rejectBtn.addActionListener(e  -> rejectSelected());
        refreshBtn.addActionListener(e -> refresh());

        JLabel hint = new JLabel("Pending → Approve/Reject borrow  |  Return Pending → Approve return");
        hint.setFont(new Font("SansSerif", Font.ITALIC, 11));
        hint.setForeground(ACCENT);

        bottom.add(hint);
        bottom.add(refreshBtn);
        bottom.add(rejectBtn);
        bottom.add(approveBtn);

        add(topSection, BorderLayout.NORTH);
        add(scroll,     BorderLayout.CENTER);
        add(bottom,     BorderLayout.SOUTH);

        refresh();
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
        String prevUser   = (String) userFilter.getSelectedItem();
        String prevStatus = (String) statusFilter.getSelectedItem();

        userFilter.removeAllItems();
        userFilter.addItem("All Users");
        for (String u : DataManager.getInstance().getUsernames())
            userFilter.addItem(u);

        if (prevUser   != null) userFilter.setSelectedItem(prevUser);
        if (prevStatus != null) statusFilter.setSelectedItem(prevStatus);

        String filterUser   = (String) userFilter.getSelectedItem();
        String filterStatus = (String) statusFilter.getSelectedItem();
        if (filterUser   == null) filterUser   = "All Users";
        if (filterStatus == null) filterStatus = "All Status";

        tableModel.setRowCount(0);
        List<Reservation> all = DataManager.getInstance().getAllReservations();
        int shown = 0, pending = 0, active = 0;

        for (Reservation r : all) {
            String status = r.getStatusText();
            boolean matchUser   = "All Users".equals(filterUser)   || r.getUsername().equals(filterUser);
            boolean matchStatus = "All Status".equals(filterStatus) || status.equals(filterStatus);
            if (matchUser && matchStatus) {
                tableModel.addRow(new Object[]{
                    "#" + r.getReservationId(),
                    r.getUsername(),
                    r.getBook().getTitle(),
                    r.getBook().getAuthor(),
                    r.getBook().getGenre(),
                    r.getReservationDate(),
                    r.getDueDate(),
                    r.getCopyNumber() > 0 ? "#" + r.getCopyNumber() : "-",
                    status
                });
                shown++;
                if (r.isPending()) pending++;
                if (r.isActive())  active++;
            }
        }
        summaryLabel.setText(
            "Showing: " + shown + "  |  Pending: " + pending +
            "  |  Active: " + active + "  |  Other: " + (shown - pending - active));
    }

    /** Pre-filter by a specific user (called from ManageUsersPanel). */
    public void filterByUser(String username) {
        userFilter.setSelectedItem(username);
        statusFilter.setSelectedIndex(0);
        refresh();
    }

    // ── Approve ──────────────────────────────────────────────────────────────

    private void approveSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a request.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String status    = (String) tableModel.getValueAt(row, 8);
        int    id        = Integer.parseInt(((String) tableModel.getValueAt(row, 0)).replace("#", ""));
        String bookTitle = (String) tableModel.getValueAt(row, 2);
        String user      = (String) tableModel.getValueAt(row, 1);

        if ("Pending".equals(status)) {
            int confirm = JOptionPane.showConfirmDialog(this,
                "<html><b>Approve this borrow request?</b><br><br>" +
                "User: " + user + "<br>Book: " + bookTitle + "<br><br>" +
                "The book copy will be reserved for this user.</html>",
                "Confirm Borrow Approval", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                boolean ok = DataManager.getInstance().approveRequest(id);
                if (ok) {
                    refresh();
                    JOptionPane.showMessageDialog(this,
                        "<html><b>Borrow Approved!</b><br>" +
                        user + " can now borrow \"" + bookTitle + "\".</html>",
                        "Approved", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                        "<html><b>Cannot approve — no copies available.</b><br>" +
                        "Wait for a return before approving this request.</html>",
                        "No Copies Available", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else if ("Return Pending".equals(status)) {
            int confirm = JOptionPane.showConfirmDialog(this,
                "<html><b>Approve this return?</b><br><br>" +
                "User: " + user + "<br>Book: " + bookTitle + "<br><br>" +
                "The book copy will be marked as returned and become available.</html>",
                "Confirm Return Approval", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (DataManager.getInstance().approveReturn(id)) {
                    refresh();
                    JOptionPane.showMessageDialog(this,
                        "<html><b>Return Approved!</b><br>" +
                        "\"" + bookTitle + "\" has been returned by " + user + ".</html>",
                        "Return Approved", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this,
                "Only Pending or Return Pending requests can be approved.", "Cannot Approve", JOptionPane.WARNING_MESSAGE);
        }
    }

    // ── Reject ───────────────────────────────────────────────────────────────

    private void rejectSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a request.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String status = (String) tableModel.getValueAt(row, 8);
        if (!"Pending".equals(status)) {
            JOptionPane.showMessageDialog(this, "Only Pending requests can be rejected.", "Not Pending", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int    id        = Integer.parseInt(((String) tableModel.getValueAt(row, 0)).replace("#", ""));
        String bookTitle = (String) tableModel.getValueAt(row, 2);
        String user      = (String) tableModel.getValueAt(row, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
            "<html><b>Reject this borrow request?</b><br><br>" +
            "User: " + user + "<br>Book: " + bookTitle + "</html>",
            "Confirm Rejection", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION && DataManager.getInstance().rejectRequest(id)) {
            refresh();
            JOptionPane.showMessageDialog(this, "Request rejected.", "Rejected", JOptionPane.INFORMATION_MESSAGE);
        }
    }

}
