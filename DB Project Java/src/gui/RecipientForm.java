package gui;

import db.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

@SuppressWarnings("serial")
public class RecipientForm extends JFrame {
    private JTextField nameField, ageField, hospitalField;
    private JComboBox<String> genderBox, bloodGroupBox;
    private JButton addButton, updateButton, deleteButton;
    private JTable recipientTable;
    private DefaultTableModel tableModel;
    private int selectedRecipientId = -1;

    public RecipientForm() {
        setTitle("Recipient Management");
        setSize(900, 500);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        Image icon = Toolkit.getDefaultToolkit().getImage("C:\\Users\\Lenovo\\Desktop\\Project DB\\icons8-blood-drop-96.png");
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

        JLabel title = new JLabel("Recipient Management", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(60, 60, 120));
        mainPanel.add(title, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setPreferredSize(new Dimension(330, 400));
        formPanel.setBorder(BorderFactory.createTitledBorder("Recipient Details"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        nameField = createInputField();
        ageField = createInputField();
        hospitalField = createInputField();
        genderBox = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        bloodGroupBox = new JComboBox<>(getBloodGroups());

        String[] labels = {"Full Name:", "Age:", "Gender:", "Blood Group:", "Hospital:"};
        JComponent[] fields = {nameField, ageField, genderBox, bloodGroupBox, hospitalField};

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

        // Table
        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Age", "Gender", "Blood Group", "Hospital"}, 0);
        recipientTable = new JTable(tableModel);
        recipientTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        recipientTable.setRowHeight(24);
        JScrollPane scrollPane = new JScrollPane(recipientTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Registered Recipients"));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        loadRecipients();

        // Table selection
        recipientTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && recipientTable.getSelectedRow() != -1) {
                selectedRecipientId = Integer.parseInt(tableModel.getValueAt(recipientTable.getSelectedRow(), 0).toString());
                nameField.setText(tableModel.getValueAt(recipientTable.getSelectedRow(), 1).toString());
                ageField.setText(tableModel.getValueAt(recipientTable.getSelectedRow(), 2).toString());
                genderBox.setSelectedItem(tableModel.getValueAt(recipientTable.getSelectedRow(), 3).toString());
                bloodGroupBox.setSelectedItem(tableModel.getValueAt(recipientTable.getSelectedRow(), 4).toString());
                hospitalField.setText(tableModel.getValueAt(recipientTable.getSelectedRow(), 5).toString());
            }
        });

        // Actions
        addButton.addActionListener(e -> addRecipient());
        updateButton.addActionListener(e -> updateRecipient());
        deleteButton.addActionListener(e -> deleteRecipient());

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

    private void loadRecipients() {
        tableModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT RecipientID, FullName, Age, Gender, BloodGroup, Hospital FROM Recipient")) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("RecipientID"),
                        rs.getString("FullName"),
                        rs.getInt("Age"),
                        rs.getString("Gender"),
                        rs.getString("BloodGroup"),
                        rs.getString("Hospital")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load recipients: " + e.getMessage());
        }
    }

    private void addRecipient() {
        String name = nameField.getText().trim();
        String ageText = ageField.getText().trim();
        String hospital = hospitalField.getText().trim();

        if (name.isEmpty() || ageText.isEmpty() || hospital.isEmpty()) {
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
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Age must be a valid number.");
            return;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO Recipient (FullName, Age, Gender, BloodGroup, Hospital, RequestDate) VALUES (?, ?, ?, ?, ?, NOW())")) {
            stmt.setString(1, name);
            stmt.setInt(2, age);
            stmt.setString(3, genderBox.getSelectedItem().toString());
            stmt.setString(4, bloodGroupBox.getSelectedItem().toString());
            stmt.setString(5, hospital);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Recipient added successfully.");
            loadRecipients();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void updateRecipient() {
        if (selectedRecipientId == -1) return;

        String name = nameField.getText().trim();
        String ageText = ageField.getText().trim();
        String hospital = hospitalField.getText().trim();

        if (name.isEmpty() || ageText.isEmpty() || hospital.isEmpty()) {
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
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Age must be a valid number.");
            return;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE Recipient SET FullName=?, Age=?, Gender=?, BloodGroup=?, Hospital=? WHERE RecipientID=?")) {
            stmt.setString(1, name);
            stmt.setInt(2, age);
            stmt.setString(3, genderBox.getSelectedItem().toString());
            stmt.setString(4, bloodGroupBox.getSelectedItem().toString());
            stmt.setString(5, hospital);
            stmt.setInt(6, selectedRecipientId);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Recipient updated.");
            loadRecipients();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void deleteRecipient() {
        if (selectedRecipientId == -1) return;
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this recipient?");
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM Recipient WHERE RecipientID=?")) {
                stmt.setInt(1, selectedRecipientId);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Recipient deleted.");
                loadRecipients();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }
}
