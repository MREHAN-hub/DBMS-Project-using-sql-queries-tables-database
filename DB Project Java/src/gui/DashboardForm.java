package gui;

import db.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

@SuppressWarnings("serial")
public class DashboardForm extends JFrame {
    JLabel donorCount, recipientCount, requestCount, inventoryCount;

    public DashboardForm() {
        setTitle("Blood Bank Dashboard");
        setSize(800, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        Image icon = Toolkit.getDefaultToolkit().getImage("C:\\Users\\Lenovo\\Desktop\\Project DB\\icons8-blood-drop-96.png");
        setIconImage(icon);

        JPanel mainPanel = new JPanel(new BorderLayout(20, 20)) {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint(new GradientPaint(0, 0, new Color(250, 250, 255),
                                               0, getHeight(), new Color(220, 230, 250)));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setContentPane(mainPanel);

        // Header
        JLabel title = new JLabel("Blood Bank Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(45, 45, 90));
        mainPanel.add(title, BorderLayout.NORTH);

        // Center split: Navigation Left | Stats Right
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 30, 0));
        centerPanel.setOpaque(false);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // LEFT: Navigation Buttons (ladder style)
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setOpaque(false);
        navPanel.setBorder(BorderFactory.createTitledBorder("Navigation"));

        JButton donorBtn = styledButton("Manage Donors");
        JButton recipientBtn = styledButton("Manage Recipients");
        JButton inventoryBtn = styledButton("Manage Inventory");
        JButton requestBtn = styledButton("Manage Requests");
        JButton logoutBtn = styledButton("Logout");
        JButton exitBtn = styledButton("Exit");

        navPanel.add(donorBtn);
        navPanel.add(Box.createVerticalStrut(15));
        navPanel.add(recipientBtn);
        navPanel.add(Box.createVerticalStrut(15));
        navPanel.add(inventoryBtn);
        navPanel.add(Box.createVerticalStrut(15));
        navPanel.add(requestBtn);
        navPanel.add(Box.createVerticalStrut(15));
        navPanel.add(logoutBtn);
        navPanel.add(Box.createVerticalStrut(15));
        navPanel.add(exitBtn);

        // RIGHT: Centered Stats
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(BorderFactory.createTitledBorder("Statistics"));

        donorCount = styledStatLabel("Donors: 0");
        recipientCount = styledStatLabel("Recipients: 0");
        requestCount = styledStatLabel("Requests: 0");
        inventoryCount = styledStatLabel("Inventory Records: 0");

        statsPanel.add(Box.createVerticalGlue());
        statsPanel.add(centered(donorCount));
        statsPanel.add(Box.createVerticalStrut(20));
        statsPanel.add(centered(recipientCount));
        statsPanel.add(Box.createVerticalStrut(20));
        statsPanel.add(centered(requestCount));
        statsPanel.add(Box.createVerticalStrut(20));
        statsPanel.add(centered(inventoryCount));
        statsPanel.add(Box.createVerticalGlue());

        centerPanel.add(navPanel);
        centerPanel.add(statsPanel);

        // Button actions
        donorBtn.addActionListener(e -> new DonorForm());
        recipientBtn.addActionListener(e -> new RecipientForm());
        inventoryBtn.addActionListener(e -> new BloodInventoryForm());
        requestBtn.addActionListener(e -> new BloodRequestForm());
        logoutBtn.addActionListener(e -> {
            this.dispose();
            new LoginForm();
        });
        exitBtn.addActionListener(e -> System.exit(0));

        loadStats();
        setResizable(false);
        setVisible(true);
    }

    private JPanel centered(JComponent comp) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setOpaque(false);
        panel.add(comp);
        return panel;
    }

    private JLabel styledStatLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setOpaque(true);
        label.setFont(new Font("Segoe UI", Font.BOLD, 20));
        label.setForeground(new Color(25, 25, 112));
        label.setBackground(new Color(240, 248, 255));
        label.setPreferredSize(new Dimension(300, 45));
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        return label;
    }

    private JButton styledButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(200, 45));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(50, 110, 160));
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(70, 130, 180));
            }
        });
        return button;
    }

    private void loadStats() {
        try (Connection conn = DBConnection.getConnection()) {
            donorCount.setText("Donors: " + getCount(conn, "SELECT COUNT(*) FROM Donor"));
            recipientCount.setText("Recipients: " + getCount(conn, "SELECT COUNT(*) FROM Recipient"));
            requestCount.setText("Requests: " + getCount(conn, "SELECT COUNT(*) FROM BloodRequest"));
            inventoryCount.setText("Inventory Records: " + getCount(conn, "SELECT COUNT(*) FROM BloodInventory"));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading dashboard: " + e.getMessage());
        }
    }

    private int getCount(Connection conn, String query) throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            rs.next();
            return rs.getInt(1);
        }
    }
}
