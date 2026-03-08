package gui;

import db.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

@SuppressWarnings("serial")
public class DonorForm extends JFrame {
    private JTextField nameField, ageField, contactField;
    private JComboBox<String> genderBox, bloodGroupBox;
    private JButton addButton, updateButton, deleteButton;
    private JTable donorTable;
    private DefaultTableModel tableModel;
    private int selectedDonorId = -1;

    public DonorForm() {
        setTitle("Donor Management");
        setSize(900, 500);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        Image icon = Toolkit.getDefaultToolkit().getImage("C:\\Users\\Lenovo\\Desktop\\Project DB\\Blood.png");
        setIconImage(icon);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15)) {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint(new GradientPaint(0, 0, new Color(255, 255, 255),
                                               0, getHeight(), new Color(230, 240, 255)));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setContentPane(mainPanel);

        // Title
        JLabel title = new JLabel("Donor Management", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(60, 60, 120));
        mainPanel.add(title, BorderLayout.NORTH);

        // Form Panel (left)
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setPreferredSize(new Dimension(330, 400));
        formPanel.setBorder(BorderFactory.createTitledBorder("Donor Details"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        nameField = createInputField();
        ageField = createInputField();
        contactField = createInputField();
        genderBox = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        bloodGroupBox = new JComboBox<>(getBloodGroups());

        String[] labels = {"Full Name:", "Age:", "Gender:", "Blood Group:", "Contact:"};
        JComponent[] fields = {nameField, ageField, genderBox, bloodGroupBox, contactField};

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            formPanel.add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1;
            formPanel.add(fields[i], gbc);
        }

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setOpaque(false);
        addButton = styledButton("Add");
        updateButton = styledButton("Update");
        deleteButton = styledButton("Delete");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);

        gbc.gridx = 0;
        gbc.gridy = labels.length;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        mainPanel.add(formPanel, BorderLayout.WEST);

        // Table (right)
        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Age", "Gender", "Blood Group", "Contact"}, 0);
        donorTable = new JTable(tableModel);
        donorTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        donorTable.setRowHeight(24);
        JScrollPane scrollPane = new JScrollPane(donorTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Registered Donors"));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        loadDonors();

        // Table row selection
        donorTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && donorTable.getSelectedRow() != -1) {
                selectedDonorId = Integer.parseInt(tableModel.getValueAt(donorTable.getSelectedRow(), 0).toString());
                nameField.setText(tableModel.getValueAt(donorTable.getSelectedRow(), 1).toString());
                ageField.setText(tableModel.getValueAt(donorTable.getSelectedRow(), 2).toString());
                genderBox.setSelectedItem(tableModel.getValueAt(donorTable.getSelectedRow(), 3).toString());
                bloodGroupBox.setSelectedItem(tableModel.getValueAt(donorTable.getSelectedRow(), 4).toString());
                contactField.setText(tableModel.getValueAt(donorTable.getSelectedRow(), 5).toString());
            }
        });

        // Button actions
        addButton.addActionListener(e -> addDonor());
        updateButton.addActionListener(e -> updateDonor());
        deleteButton.addActionListener(e -> deleteDonor());

        setResizable(false);
        setVisible(true);
    }

    private JTextField createInputField() {
        JTextField field = new JTextField(20);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        return field;
    }

    private JButton styledButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(new Color(65, 105, 225));
        button.setForeground(Color.WHITE);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(100, 35));
        button.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(50, 90, 200));
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(65, 105, 225));
            }
        });

        return button;
    }

    private String[] getBloodGroups() {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT BloodGroup FROM BloodGroup")) {

            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
            while (rs.next()) model.addElement(rs.getString(1));

            String[] arr = new String[model.getSize()];
            for (int i = 0; i < model.getSize(); i++) arr[i] = model.getElementAt(i);
            return arr;

        } catch (Exception e) {
            e.printStackTrace();
            return new String[]{};
        }
    }

    private void loadDonors() {
        tableModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT DonorID, FullName, Age, Gender, BloodGroup, Contact FROM Donor")) {

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("DonorID"),
                        rs.getString("FullName"),
                        rs.getInt("Age"),
                        rs.getString("Gender"),
                        rs.getString("BloodGroup"),
                        rs.getString("Contact")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load donors: " + e.getMessage());
        }
    }

    private void addDonor() {
        String name = nameField.getText().trim();
        String ageText = ageField.getText().trim();
        String contact = contactField.getText().trim();

        if (name.isEmpty() || ageText.isEmpty() || contact.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled.");
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageText);
            if (age <= 0 || age > 120) {
                JOptionPane.showMessageDialog(this, "Age must be between 1 and 120.");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Age must be a valid number.");
            return;
        }

        if (!contact.matches("\\d{10,15}")) {
            JOptionPane.showMessageDialog(this, "Contact must be 10 to 15 digits.");
            return;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO Donor (FullName, Age, Gender, BloodGroup, Contact) VALUES (?, ?, ?, ?, ?)")) {
            stmt.setString(1, name);
            stmt.setInt(2, age);
            stmt.setString(3, genderBox.getSelectedItem().toString());
            stmt.setString(4, bloodGroupBox.getSelectedItem().toString());
            stmt.setString(5, contact);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Donor added successfully.");
            loadDonors();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void updateDonor() {
        if (selectedDonorId == -1) return;

        String name = nameField.getText().trim();
        String ageText = ageField.getText().trim();
        String contact = contactField.getText().trim();

        if (name.isEmpty() || ageText.isEmpty() || contact.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled.");
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageText);
            if (age <= 0 || age > 120) {
                JOptionPane.showMessageDialog(this, "Age must be between 1 and 120.");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Age must be a valid number.");
            return;
        }

        if (!contact.matches("\\d{10,15}")) {
            JOptionPane.showMessageDialog(this, "Contact must be 10 to 15 digits.");
            return;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE Donor SET FullName=?, Age=?, Gender=?, BloodGroup=?, Contact=? WHERE DonorID=?")) {
            stmt.setString(1, name);
            stmt.setInt(2, age);
            stmt.setString(3, genderBox.getSelectedItem().toString());
            stmt.setString(4, bloodGroupBox.getSelectedItem().toString());
            stmt.setString(5, contact);
            stmt.setInt(6, selectedDonorId);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Donor updated.");
            loadDonors();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void deleteDonor() {
        if (selectedDonorId == -1) return;
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this donor?");
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM Donor WHERE DonorID=?")) {
                stmt.setInt(1, selectedDonorId);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Donor deleted.");
                loadDonors();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }
}
