package gui;

import db.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

@SuppressWarnings("serial")
public class RegisterForm extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JCheckBox showPasswordCheck;
    private JButton registerButton;
    private JButton cancelButton;
    private LoginForm loginForm;

    public RegisterForm(LoginForm loginForm) {
        this.loginForm = loginForm;

        setTitle("Blood Bank - Register Admin");
        setSize(500, 420);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        Image icon = Toolkit.getDefaultToolkit().getImage("C:\\Users\\Lenovo\\Desktop\\Project DB\\Blood.png");
        setIconImage(icon);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15)) {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint(new GradientPaint(0, 0, new Color(255, 255, 255),
                                               0, getHeight(), new Color(225, 235, 245)));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        setContentPane(mainPanel);

        // Title
        JLabel title = new JLabel("Register New Admin", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(45, 45, 90));
        mainPanel.add(title, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);

        usernameField = createInputField();
        passwordField = createPasswordField();
        confirmPasswordField = createPasswordField();

        formPanel.add(createLabeledField("Username:", usernameField));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createLabeledField("Password:", passwordField));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createLabeledField("Confirm Password:", confirmPasswordField));

        showPasswordCheck = new JCheckBox("Show Passwords");
        showPasswordCheck.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        showPasswordCheck.setOpaque(false);
        showPasswordCheck.addActionListener(e -> togglePasswordVisibility());
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(showPasswordCheck);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setOpaque(false);

        registerButton = styledButton("Register");
        cancelButton = styledButton("Cancel");

        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Actions
        registerButton.addActionListener(e -> registerUser());
        cancelButton.addActionListener(e -> closeRegistration());

        setResizable(false);
        setVisible(true);
    }

    private JTextField createInputField() {
        JTextField field = new JTextField(20);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        return field;
    }

    private JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField(20);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        return field;
    }

    private JPanel createLabeledField(String labelText, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setOpaque(false);
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 15));
        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private JButton styledButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(130, 40));
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

    private void togglePasswordVisibility() {
        boolean visible = showPasswordCheck.isSelected();
        passwordField.setEchoChar(visible ? (char) 0 : '•');
        confirmPasswordField.setEchoChar(visible ? (char) 0 : '•');
    }

    private void registerUser() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.");
            return;
        }

        if (username.length() < 4) {
            JOptionPane.showMessageDialog(this, "Username must be at least 4 characters long.");
            return;
        }

        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this, "Password must be at least 6 characters long.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement checkStmt = conn.prepareStatement("SELECT * FROM Staff WHERE Username = ?");
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Username already exists. Choose another.");
                return;
            }

            PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO Staff (Username, Password) VALUES (?, ?)");
            insertStmt.setString(1, username);
            insertStmt.setString(2, password); // In production, use hashed password

            int rows = insertStmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Registration successful! You can now login.");
                closeRegistration();
            } else {
                JOptionPane.showMessageDialog(this, "Registration failed. Try again.");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void closeRegistration() {
        this.dispose();
        loginForm.showLogin();
    }
}
