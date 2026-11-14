package com.java.libray;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class MyReservationsPanel extends JPanel {
    private final String username;
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel summaryLabel;
    private JPanel holdsPanel;
    private JLabel holdsLabel;

    private static final Color BG       = new Color(245, 242, 235);
    private static final Color DARK     = new Color(30, 20, 10);
    private static final Color ACCENT   = new Color(140, 90, 40);
    private static final Color ROW_ODD  = new Color(255, 253, 248);
    private static final Color ROW_EVN  = new Color(240, 236, 225);
    private static final Color PENDING        = new Color(180, 110, 0);
    private static final Color ACTIVE         = new Color(20, 100, 40);
    private static final Color REJECTED       = new Color(150, 30, 30);
    private static final Color DONE          = new Color(120, 120, 120);
    private static final Color RETURN_PEND   = new Color(0, 90, 160);

    private static final String[] COLS = {
        "Res. ID", "Book Title", "Author", "Genre", "Reserved On", "Due Date", "Copy #", "Status"
    };

    public MyReservationsPanel(String username) {
        this.username = username;
        setBackground(BG);
        setLayout(new BorderLayout());
        buildUI();
    }

    private void buildUI() {
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(220, 210, 190));
        headerPanel.setBorder(new EmptyBorder(12, 16, 12, 16));

        JLabel title = new JLabel("My History");
        title.setFont(new Font("Georgia", Font.BOLD, 16));
        title.setForeground(DARK);

        summaryLabel = new JLabel();
        summaryLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        summaryLabel.setForeground(ACCENT);

        headerPanel.add(title, BorderLayout.WEST);
        headerPanel.add(summaryLabel, BorderLayout.EAST);

        // Holds banner — appears between header and table when the user has
        // active book reservations waiting for the next available copy.
        holdsPanel = new JPanel(new BorderLayout());
        holdsPanel.setBackground(new Color(255, 248, 225));
        holdsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(210, 180, 120)),
            new EmptyBorder(10, 16, 10, 16)
        ));
        holdsLabel = new JLabel();
        holdsLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        holdsLabel.setForeground(new Color(80, 50, 20));
        holdsLabel.setVerticalAlignment(SwingConstants.TOP);
        holdsPanel.add(holdsLabel, BorderLayout.CENTER);
        holdsPanel.setVisible(false);

        JPanel topSection = new JPanel(new BorderLayout());
        topSection.add(headerPanel, BorderLayout.NORTH);
        topSection.add(holdsPanel,  BorderLayout.SOUTH);

        // Table
        tableModel = new DefaultTableModel(COLS, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel) {
            public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row))
                    c.setBackground(row % 2 == 0 ? ROW_ODD : ROW_EVN);
                if (col == 7) {
                    String val = (String) getValueAt(row, col);
                    switch (val == null ? "" : val) {
                        case "Pending":        c.setForeground(PENDING);      break;
                        case "Active":         c.setForeground(ACTIVE);       break;
                        case "Rejected":       c.setForeground(REJECTED);     break;
                        case "Return Pending": c.setForeground(RETURN_PEND);  break;
                        default:               c.setForeground(DONE);         break;
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

        int[] widths = {60, 210, 130, 80, 100, 100, 60, 90};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(ROW_ODD);

        // Bottom bar
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        bottom.setBackground(new Color(220, 210, 190));
        bottom.setBorder(new EmptyBorder(4, 8, 4, 8));

        JLabel hint = new JLabel("Select an active reservation to request a return");
        hint.setFont(new Font("SansSerif", Font.ITALIC, 12));
        hint.setForeground(ACCENT);

        JButton returnBtn = new JButton("Request Return");
        returnBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        returnBtn.setBackground(new Color(0, 90, 160));
        returnBtn.setForeground(Color.WHITE);
        returnBtn.setBorderPainted(false);
        returnBtn.setFocusPainted(false);
        returnBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        returnBtn.setBorder(new EmptyBorder(7, 20, 7, 20));
        returnBtn.addActionListener(e -> requestReturn());

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        refreshBtn.setBackground(new Color(160, 140, 110));
        refreshBtn.setForeground(DARK);
        refreshBtn.setBorderPainted(false);
        refreshBtn.setFocusPainted(false);
        refreshBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        refreshBtn.setBorder(new EmptyBorder(7, 16, 7, 16));
        refreshBtn.addActionListener(e -> refresh());

        bottom.add(hint);
        bottom.add(refreshBtn);
        bottom.add(returnBtn);

        add(topSection, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        refresh();
    }

    public void refresh() {
        DataManager.getInstance().applyOverduePenalties();
        tableModel.setRowCount(0);
        List<Reservation> list = DataManager.getInstance().getUserReservations(username);
        int active = 0;
        int pending = 0;
        for (Reservation r : list) {
            tableModel.addRow(new Object[]{
                "#" + r.getReservationId(),
                r.getBook().getTitle(),
                r.getBook().getAuthor(),
                r.getBook().getGenre(),
                r.getReservationDate(),
                r.getDueDate(),
                r.getCopyNumber() > 0 ? "#" + r.getCopyNumber() : "-",
                r.getStatusText()
            });
            if (!r.isReturned() && r.getStatus() != Reservation.Status.REJECTED) active++;
            if (r.isPending()) pending++;
        }
        int penalty = DataManager.getInstance().getPenaltyPoints(username);
        boolean blocked = DataManager.getInstance().isBlocked(username);
        String penaltyHtml = "<font color='" + (blocked ? "#aa0000" : penalty > 0 ? "#aa6600" : "#206030") + "'>"
            + "Penalty: " + penalty + "/2" + (blocked ? " — BLOCKED" : "") + "</font>";
        summaryLabel.setText("<html>Total: " + list.size() + "  |  Pending: " + pending +
            "  |  Active: " + (active - pending) + "  |  Returned: " + (list.size() - active) +
            "  &nbsp;&nbsp; " + penaltyHtml + "</html>");

        refreshHolds();
    }

    private void refreshHolds() {
        List<BookHold> holds = DataManager.getInstance().getUserHolds(username);
        if (holds.isEmpty()) {
            holdsPanel.setVisible(false);
            return;
        }
        StringBuilder sb = new StringBuilder("<html><b>📌 My Book Reservations</b><br>");
        for (BookHold h : holds) {
            sb.append("&nbsp;&nbsp;• \"").append(h.getBook().getTitle()).append("\" — ")
              .append("Copy <b>#").append(h.getCopyNumber()).append("</b> reserved, ")
              .append("expected available <b>").append(h.getExpectedDateText()).append("</b><br>");
        }
        sb.append("</html>");
        holdsLabel.setText(sb.toString());
        holdsPanel.setVisible(true);
    }

    private void requestReturn() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a reservation to return.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String status = (String) tableModel.getValueAt(row, 7);
        if ("Pending".equals(status)) {
            JOptionPane.showMessageDialog(this, "Your borrow request is still pending admin approval.", "Not Yet Active", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if ("Rejected".equals(status)) {
            JOptionPane.showMessageDialog(this, "This request was rejected by the admin.", "Request Rejected", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if ("Return Pending".equals(status)) {
            JOptionPane.showMessageDialog(this, "Your return request is already waiting for admin approval.", "Already Requested", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (!"Active".equals(status)) {
            JOptionPane.showMessageDialog(this, "This book has already been returned.", "Already Returned", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int id = Integer.parseInt(((String) tableModel.getValueAt(row, 0)).replace("#", ""));

        int confirm = JOptionPane.showConfirmDialog(this,
            "<html><b>Request to return this book?</b><br><br>" +
            "Book: " + tableModel.getValueAt(row, 1) + "<br><br>" +
            "The admin will need to approve your return.</html>",
            "Confirm Return Request", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (DataManager.getInstance().requestReturn(id)) {
                refresh();
                JOptionPane.showMessageDialog(this, "Return request submitted! Waiting for admin approval.", "Request Sent", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
}
