package com.java.libray;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.Cursor;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel errorLabel;

    private static final Color BG       = new Color(245, 242, 235);
    private static final Color DARK     = new Color(30, 20, 10);
    private static final Color ACCENT   = new Color(140, 90, 40);
    private static final Color PANEL_BG = new Color(255, 253, 248);
    private static final Color BORDER_C = new Color(200, 175, 140);
    private static final Color HINT_BG  = new Color(255, 248, 230);

    public LoginFrame() {
        setTitle("Library Book Reservation System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(440, 560);
        setLocationRelativeTo(null);
        setResizable(false);
        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(BG);
        root.setBorder(new EmptyBorder(0, 0, 0, 0));

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(PANEL_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_C, 1, true),
            new EmptyBorder(36, 40, 36, 40)
        ));
        card.setMaximumSize(new Dimension(340, 440));

        // Icon + title
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel bookIcon = new JLabel("📚");
        bookIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        bookIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("Library System");
        titleLabel.setFont(new Font("Georgia", Font.BOLD, 22));
        titleLabel.setForeground(DARK);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subLabel = new JLabel("Book Reservation Portal");
        subLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subLabel.setForeground(ACCENT);
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(bookIcon);
        header.add(Box.createVerticalStrut(10));
        header.add(titleLabel);
        header.add(Box.createVerticalStrut(4));
        header.add(subLabel);

        // Hint panel
        JPanel hintPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        hintPanel.setBackground(HINT_BG);
        hintPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(210, 180, 120), 1, true),
            new EmptyBorder(8, 12, 8, 12)
        ));
        JLabel hint = new JLabel("<html><b>Demo Accounts:</b><br>admin / admin123 &nbsp;|&nbsp; alice / alice123</html>");
        hint.setFont(new Font("SansSerif", Font.PLAIN, 11));
        hint.setForeground(new Color(120, 80, 20));
        hintPanel.add(hint);

        // Fields
        usernameField = makeField("Username");
        passwordField = new JPasswordField();
        styleField(passwordField);
        passwordField.putClientProperty("JPasswordField.cutCopyAllowed", true);

        // Error label
        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        errorLabel.setForeground(new Color(160, 30, 30));
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Forgot password link
        JLabel forgotLabel = new JLabel("<html><u>Forgot Password?</u></html>");
        forgotLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        forgotLabel.setForeground(ACCENT);
        forgotLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        forgotLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        forgotLabel.setHorizontalAlignment(SwingConstants.CENTER);
        forgotLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { showForgotPasswordDialog(); }
            public void mouseEntered(MouseEvent e) { forgotLabel.setForeground(DARK); }
            public void mouseExited(MouseEvent e)  { forgotLabel.setForeground(ACCENT); }
        });

        // Login button
        JButton loginBtn = new JButton("Sign In →");
        loginBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        loginBtn.setBackground(DARK);
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.setBorderPainted(false);
        loginBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        loginBtn.addActionListener(e -> attemptLogin());
        loginBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { loginBtn.setBackground(ACCENT); }
            public void mouseExited(MouseEvent e)  { loginBtn.setBackground(DARK); }
        });

        // Enter key
        KeyAdapter enterKey = new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) attemptLogin();
            }
        };
        usernameField.addKeyListener(enterKey);
        passwordField.addKeyListener(enterKey);

        // Assemble card
        card.add(header);
        card.add(Box.createVerticalStrut(24));
        card.add(makeLabel("Username"));
        card.add(Box.createVerticalStrut(4));
        card.add(usernameField);
        card.add(Box.createVerticalStrut(14));
        card.add(makeLabel("Password"));
        card.add(Box.createVerticalStrut(4));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(14));
        card.add(hintPanel);
        card.add(Box.createVerticalStrut(20));
        card.add(loginBtn);
        card.add(Box.createVerticalStrut(8));
        card.add(errorLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(forgotLabel);

        root.add(card);
        setContentPane(root);
    }

    private JLabel makeLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.BOLD, 11));
        l.setForeground(new Color(100, 70, 40));
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        l.setHorizontalAlignment(SwingConstants.CENTER);
        return l;
    }

    private JTextField makeField(String placeholder) {
        JTextField f = new JTextField();
        styleField(f);
        return f;
    }

    private void styleField(JTextField f) {
        f.setFont(new Font("SansSerif", Font.PLAIN, 14));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_C, 1, true),
            new EmptyBorder(8, 12, 8, 12)
        ));
        f.setBackground(Color.WHITE);
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        f.setAlignmentX(Component.CENTER_ALIGNMENT);
        f.setHorizontalAlignment(JTextField.CENTER);
    }

    private void showForgotPasswordDialog() {
        // Step 1: ask for username + phone
        JTextField usernameF = new JTextField(20);
        JTextField phoneF    = new JTextField(20);

        JPanel step1 = new JPanel(new GridBagLayout());
        step1.setBorder(new EmptyBorder(8, 4, 4, 4));
        GridBagConstraints lc = new GridBagConstraints();
        lc.anchor = GridBagConstraints.WEST; lc.insets = new Insets(6, 4, 2, 8);
        GridBagConstraints fc = new GridBagConstraints();
        fc.fill = GridBagConstraints.HORIZONTAL; fc.weightx = 1.0; fc.insets = new Insets(6, 0, 2, 4);

        lc.gridy = fc.gridy = 0; lc.gridx = 0; fc.gridx = 1;
        step1.add(boldLabel("Username:"), lc); step1.add(usernameF, fc);
        lc.gridy = fc.gridy = 1; lc.gridx = 0; fc.gridx = 1;
        step1.add(boldLabel("Phone Number:"), lc); step1.add(phoneF, fc);

        int r1 = JOptionPane.showConfirmDialog(this, step1,
            "Reset Password — Step 1: Verify Identity",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (r1 != JOptionPane.OK_OPTION) return;

        String username = usernameF.getText().trim();
        String phone    = phoneF.getText().trim();

        if (username.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!DataManager.getInstance().verifyUsernameAndPhone(username, phone)) {
            JOptionPane.showMessageDialog(this,
                "No account found with that username and phone number.",
                "Verification Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Step 2: enter new password
        JPasswordField newPwdF     = new JPasswordField(20);
        JPasswordField confirmPwdF = new JPasswordField(20);

        JPanel step2 = new JPanel(new GridBagLayout());
        step2.setBorder(new EmptyBorder(8, 4, 4, 4));
        lc.gridy = fc.gridy = 0; lc.gridx = 0; fc.gridx = 1;
        step2.add(boldLabel("New Password:"), lc); step2.add(newPwdF, fc);
        lc.gridy = fc.gridy = 1; lc.gridx = 0; fc.gridx = 1;
        step2.add(boldLabel("Confirm Password:"), lc); step2.add(confirmPwdF, fc);

        int r2 = JOptionPane.showConfirmDialog(this, step2,
            "Reset Password — Step 2: New Password",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (r2 != JOptionPane.OK_OPTION) return;

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
        JOptionPane.showMessageDialog(this,
            "Password for \"" + username + "\" has been reset successfully.\nYou can now sign in with your new password.",
            "Password Reset", JOptionPane.INFORMATION_MESSAGE);
    }

    private JLabel boldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.BOLD, 12));
        return l;
    }

    private void attemptLogin() {
        String user = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword());
        if (user.isEmpty() || pass.isEmpty()) {
            errorLabel.setText("Please enter username and password.");
            return;
        }
        if (DataManager.getInstance().authenticate(user, pass)) {
            dispose();
            new MainFrame(user).setVisible(true);
        } else {
            errorLabel.setText("Invalid username or password.");
            passwordField.setText("");
        }
    }
}
