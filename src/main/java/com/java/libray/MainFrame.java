package com.java.libray;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class MainFrame extends JFrame {
    private final String username;
    // Regular-user tabs
    private BooksPanel booksPanel;
    private MyReservationsPanel reservationsPanel;
    // Admin tabs
    private ManageBooksPanel manageBooksPanel;
    private AllReservationsPanel allReservationsPanel;
    private ManageUsersPanel manageUsersPanel;
    private JTabbedPane tabbedPane;

    private static final Color DARK    = new Color(30, 20, 10);
    private static final Color ACCENT  = new Color(140, 90, 40);
    private static final Color BG      = new Color(245, 242, 235);
    private static final Color TOPBAR  = new Color(30, 20, 10);

    public MainFrame(String username) {
        this.username = username;
        setTitle("Library Book Reservation — " + username);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(960, 660);
        setMinimumSize(new Dimension(800, 580));
        setLocationRelativeTo(null);
        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);

        // Top bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(TOPBAR);
        topBar.setBorder(new EmptyBorder(12, 20, 12, 20));

        JLabel logo = new JLabel("📚  Library Reservation System");
        logo.setFont(new Font("Georgia", Font.BOLD, 16));
        logo.setForeground(new Color(230, 200, 150));

        JPanel rightBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightBar.setOpaque(false);

        JLabel userLabel = new JLabel("👤  " + username.toUpperCase());
        userLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        userLabel.setForeground(new Color(200, 170, 120));

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        logoutBtn.setBackground(new Color(80, 40, 20));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        rightBar.add(userLabel);
        rightBar.add(logoutBtn);
        topBar.add(logo, BorderLayout.WEST);
        topBar.add(rightBar, BorderLayout.EAST);

        // Tabs
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("SansSerif", Font.BOLD, 13));
        tabbedPane.setBackground(BG);

        if (DataManager.getInstance().isAdmin(username)) {
            manageBooksPanel      = new ManageBooksPanel();
            allReservationsPanel  = new AllReservationsPanel();
            manageUsersPanel      = new ManageUsersPanel();

            tabbedPane.addTab("  Manage Books  ",       manageBooksPanel);
            tabbedPane.addTab("  All Requests  ",   allReservationsPanel);
            tabbedPane.addTab("  Manage Users  ",       manageUsersPanel);

            tabbedPane.addChangeListener(e -> {
                int idx = tabbedPane.getSelectedIndex();
                if (idx == 0) manageBooksPanel.refresh();
                if (idx == 1) allReservationsPanel.refresh();
                if (idx == 2) manageUsersPanel.refresh();
            });
        } else {
            booksPanel        = new BooksPanel(username, this);
            reservationsPanel = new MyReservationsPanel(username);

            tabbedPane.addTab("  Browse Books  ",     booksPanel);
            tabbedPane.addTab("  My History  ",  reservationsPanel);

            tabbedPane.addChangeListener(e -> {
                if (tabbedPane.getSelectedIndex() == 1) reservationsPanel.refresh();
            });
        }

        root.add(topBar, BorderLayout.NORTH);
        root.add(tabbedPane, BorderLayout.CENTER);
        setContentPane(root);
    }

    public void switchToReservations() {
        if (reservationsPanel != null) {
            reservationsPanel.refresh();
            tabbedPane.setSelectedIndex(1);
        }
    }
}
