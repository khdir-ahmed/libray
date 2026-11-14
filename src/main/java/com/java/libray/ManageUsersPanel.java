package com.java.libray;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class ManageUsersPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel summaryLabel;

    private static final Color BG       = new Color(245, 242, 235);
    private static final Color DARK     = new Color(30, 20, 10);
    private static final Color ACCENT   = new Color(140, 90, 40);
    private static final Color ROW_ODD  = new Color(255, 253, 248);
    private static final Color ROW_EVEN = new Color(240, 236, 225);
    private static final Color ADMIN_FG = new Color(140, 60, 20);

    private static final String[] COLS = {
        "Username", "Phone", "Role", "Total Borrows", "Active Borrows", "Returned", "Penalty", "Status"
    };

    public ManageUsersPanel() {
        setBackground(BG);
        setLayout(new BorderLayout(0, 0));
        buildUI();
        refresh();
    }

    private void buildUI() {
        // ── Header ──────────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(220, 210, 190));
        header.setBorder(new EmptyBorder(12, 16, 12, 16));

        JLabel title = new JLabel("Manage Users  (Admin)");
        title.setFont(new Font("Georgia", Font.BOLD, 16));
        title.setForeground(DARK);

        summaryLabel = new JLabel();
        summaryLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        summaryLabel.setForeground(ACCENT);

        header.add(title, BorderLayout.WEST);
        header.add(summaryLabel, BorderLayout.EAST);

        // ── Table ────────────────────────────────────────────────────────────
        tableModel = new DefaultTableModel(COLS, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel) {
            public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row))
                    c.setBackground(row % 2 == 0 ? ROW_ODD : ROW_EVEN);
                if (col == 2) {
                    String val = (String) getValueAt(row, col);
                    if ("Admin".equals(val)) {
                        c.setForeground(ADMIN_FG);
                        ((JComponent) c).setFont(c.getFont().deriveFont(Font.BOLD));
                    } else {
                        c.setForeground(DARK);
                    }
                } else if (col == 6) {
                    Object v = getValueAt(row, col);
                    String s = v == null ? "" : v.toString();
                    int p = 0;
                    try { p = Integer.parseInt(s.split("/")[0].trim()); } catch (NumberFormatException ignore) {}
                    c.setForeground(p >= 2 ? new Color(170, 30, 30)
                                  : p > 0  ? new Color(170, 100, 0)
                                           : DARK);
                    ((JComponent) c).setFont(c.getFont().deriveFont(p > 0 ? Font.BOLD : Font.PLAIN));
                } else if (col == 7) {
                    String val = (String) getValueAt(row, col);
                    if ("Blocked".equals(val)) {
                        c.setForeground(new Color(170, 30, 30));
                        ((JComponent) c).setFont(c.getFont().deriveFont(Font.BOLD));
                    } else {
                        c.setForeground(new Color(20, 100, 40));
                    }
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

        int[] widths = {130, 110, 70, 100, 100, 80, 70, 80};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(ROW_ODD);

        // ── Bottom bar ───────────────────────────────────────────────────────
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        bottom.setBackground(new Color(220, 210, 190));
        bottom.setBorder(new EmptyBorder(4, 8, 4, 8));

        JButton addBtn     = makeBtn("+ Add",               new Color(30, 100, 40),   Color.WHITE);
        JButton pwdBtn     = makeBtn("Change Password",    new Color(80, 50, 20),    Color.WHITE);
        JButton resetBtn   = makeBtn("Reset Penalty",      new Color(0, 90, 160),    Color.WHITE);
        JButton deleteBtn  = makeBtn("Delete User",        new Color(140, 30, 30),   Color.WHITE);
        JButton refreshBtn = makeBtn("↺ Refresh",          new Color(160, 140, 110), DARK);

        addBtn.addActionListener(e    -> showAddChoiceDialog());
        pwdBtn.addActionListener(e    -> showChangePasswordDialog());
        resetBtn.addActionListener(e  -> resetPenaltySelected());
        deleteBtn.addActionListener(e -> deleteSelected());
        refreshBtn.addActionListener(e -> refresh());

        bottom.add(refreshBtn);
        bottom.add(addBtn);
        bottom.add(pwdBtn);
        bottom.add(resetBtn);
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
        DataManager.getInstance().applyOverduePenalties();
        tableModel.setRowCount(0);
        List<String> usernames = DataManager.getInstance().getUsernames();
        int blockedCount = 0;
        for (String u : usernames) {
            List<Reservation> res = DataManager.getInstance().getUserReservations(u);
            int active = 0;
            for (Reservation r : res) if (!r.isReturned()) active++;
            int returned = res.size() - active;
            String role  = DataManager.getInstance().isAdmin(u) ? "Admin" : "User";
            String phone = DataManager.getInstance().getUserPhone(u);
            int penalty  = DataManager.getInstance().getPenaltyPoints(u);
            boolean blk  = DataManager.getInstance().isBlocked(u);
            if (blk) blockedCount++;
            tableModel.addRow(new Object[]{u, phone, role, res.size(), active, returned,
                penalty + "/2", blk ? "Blocked" : "Active"});
        }
        summaryLabel.setText("Total Users: " + usernames.size() + "   |   Blocked: " + blockedCount);
    }

    private void resetPenaltySelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a user.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String username = (String) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Reset penalty points and unblock \"" + username + "\"?",
            "Confirm Reset", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;
        DataManager.getInstance().resetPenalty(username);
        refresh();
        JOptionPane.showMessageDialog(this, "Penalties cleared for \"" + username + "\".",
            "Reset", JOptionPane.INFORMATION_MESSAGE);
    }

    // ── Add (choice) ─────────────────────────────────────────────────────────

    private void showAddChoiceDialog() {
        Object[] options = {"New User", "New Admin", "Cancel"};
        int choice = JOptionPane.showOptionDialog(
            this,
            "What would you like to add?",
            "Add Account",
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );
        if (choice == 0) showAddUserDialog(false);
        else if (choice == 1) showAddUserDialog(true);
    }

    private void showAddUserDialog(boolean isAdmin) {
        JTextField     usernameF = new JTextField(20);
        JPasswordField passwordF = new JPasswordField(20);
        JPasswordField confirmF  = new JPasswordField(20);
        JTextField     phoneF    = new JTextField(20);

        JPanel form = buildForm(
            new String[]{"Username:", "Password:", "Confirm Password:", "Phone Number:"},
            new JComponent[]{usernameF, passwordF, confirmF, phoneF}
        );

        String title = isAdmin ? "Add New Admin" : "Add New User";
        int result = JOptionPane.showConfirmDialog(
            this, form, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;

        String username = usernameF.getText().trim();
        String password = new String(passwordF.getPassword());
        String confirm  = new String(confirmF.getPassword());
        String phone    = phoneF.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username and password are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!password.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Phone number is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!phone.matches("\\d{8}")) {
            JOptionPane.showMessageDialog(this, "Phone number must be exactly 8 digits.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (DataManager.getInstance().addUser(username, password, phone, isAdmin)) {
            refresh();
            String roleLabel = isAdmin ? "Admin" : "User";
            JOptionPane.showMessageDialog(this, roleLabel + " \"" + username + "\" added successfully.", "Added", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Username \"" + username + "\" already exists.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Change Password ───────────────────────────────────────────────────────

    private void showChangePasswordDialog() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a user.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String username = (String) tableModel.getValueAt(row, 0);

        JPasswordField newPwdF     = new JPasswordField(20);
        JPasswordField confirmPwdF = new JPasswordField(20);

        JPanel form = buildForm(
            new String[]{"New Password:", "Confirm Password:"},
            new JComponent[]{newPwdF, confirmPwdF}
        );

        int result = JOptionPane.showConfirmDialog(
            this, form, "Change Password — " + username, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;

        String newPwd  = new String(newPwdF.getPassword());
        String confirm = new String(confirmPwdF.getPassword());

        if (newPwd.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Password cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!newPwd.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        DataManager.getInstance().updatePassword(username, newPwd);
        JOptionPane.showMessageDialog(this, "Password for \"" + username + "\" updated.", "Updated", JOptionPane.INFORMATION_MESSAGE);
    }

    // ── Delete User ───────────────────────────────────────────────────────────

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String username = (String) tableModel.getValueAt(row, 0);
        String role     = (String) tableModel.getValueAt(row, 2);

        if ("Admin".equals(role)) {
            JOptionPane.showMessageDialog(this, "The admin account cannot be deleted.", "Restricted", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int active = (int) tableModel.getValueAt(row, 4);
        String warning = active > 0
            ? "<br><font color='red'>Warning: this user still has " + active + " active borrow(s).</font>"
            : "";

        int confirm = JOptionPane.showConfirmDialog(this,
            "<html><b>Delete user \"" + username + "\"?</b>" + warning +
            "<br><br><font color='red'>This cannot be undone.</font></html>",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) return;

        DataManager.getInstance().deleteUser(username);
        refresh();
        JOptionPane.showMessageDialog(this, "User \"" + username + "\" deleted.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
    }

    // ── Form helper ───────────────────────────────────────────────────────────

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
