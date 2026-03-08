package gui;

import db.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

@SuppressWarnings("serial")
public class BloodInventoryForm extends JFrame {
    private JComboBox<String> bloodGroupBox;
    private JTextField quantityField;
    private JButton addButton, updateButton, deleteButton;
    private JTable inventoryTable;
    private DefaultTableModel tableModel;
    private String selectedBloodGroup = null;

    public BloodInventoryForm() {
        setTitle("Blood Inventory Management");
        setSize(750, 500);
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

        JLabel title = new JLabel("Blood Inventory Management", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(60, 60, 120));
        mainPanel.add(title, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setPreferredSize(new Dimension(280, 400));
        formPanel.setBorder(BorderFactory.createTitledBorder("Inventory Entry"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        bloodGroupBox = new JComboBox<>(getBloodGroups());
        quantityField = createInputField();

        String[] labels = {"Blood Group:", "Quantity (ml):"};
        JComponent[] fields = {bloodGroupBox, quantityField};

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
        tableModel = new DefaultTableModel(new String[]{"Blood Group", "Quantity (ml)"}, 0);
        inventoryTable = new JTable(tableModel);
        inventoryTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        inventoryTable.setRowHeight(24);
        JScrollPane scrollPane = new JScrollPane(inventoryTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Inventory Records"));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        loadInventory();

        // Table Selection
        inventoryTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && inventoryTable.getSelectedRow() != -1) {
                selectedBloodGroup = tableModel.getValueAt(inventoryTable.getSelectedRow(), 0).toString();
                bloodGroupBox.setSelectedItem(selectedBloodGroup);
                quantityField.setText(tableModel.getValueAt(inventoryTable.getSelectedRow(), 1).toString());
            }
        });

        // Button actions
        addButton.addActionListener(e -> addRecord());
        updateButton.addActionListener(e -> updateRecord());
        deleteButton.addActionListener(e -> deleteRecord());

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

    private void loadInventory() {
        tableModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT BloodGroup, Quantity FROM BloodInventory")) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getString("BloodGroup"),
                        rs.getInt("Quantity")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load inventory: " + e.getMessage());
        }
    }

    private void addRecord() {
        String bloodGroup = bloodGroupBox.getSelectedItem().toString();
        String quantityText = quantityField.getText().trim();

        if (quantityText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Quantity is required.");
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityText);
            if (quantity <= 0) {
                JOptionPane.showMessageDialog(this, "Quantity must be positive.");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Quantity must be a number.");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            // Check if the blood group already exists
            PreparedStatement checkStmt = conn.prepareStatement("SELECT * FROM BloodInventory WHERE BloodGroup=?");
            checkStmt.setString(1, bloodGroup);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Blood group already exists. Use Update instead.");
                return;
            }

            PreparedStatement stmt = conn.prepareStatement("INSERT INTO BloodInventory (BloodGroup, Quantity) VALUES (?, ?)");
            stmt.setString(1, bloodGroup);
            stmt.setInt(2, quantity);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Record added successfully.");
            loadInventory();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void updateRecord() {
        if (selectedBloodGroup == null) return;

        String quantityText = quantityField.getText().trim();
        if (quantityText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Quantity is required.");
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityText);
            if (quantity <= 0) {
                JOptionPane.showMessageDialog(this, "Quantity must be positive.");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Quantity must be a number.");
            return;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE BloodInventory SET Quantity=? WHERE BloodGroup=?")) {
            stmt.setInt(1, quantity);
            stmt.setString(2, selectedBloodGroup);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Record updated.");
            loadInventory();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void deleteRecord() {
        if (selectedBloodGroup == null) return;
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this record?");
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM BloodInventory WHERE BloodGroup=?")) {
                stmt.setString(1, selectedBloodGroup);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Record deleted.");
                loadInventory();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }
}
