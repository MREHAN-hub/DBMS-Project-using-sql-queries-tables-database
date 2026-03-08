package gui;

import db.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

@SuppressWarnings("serial")
public class BloodRequestForm extends JFrame {
    private JComboBox<String> recipientBox, bloodGroupBox, statusBox;
    private JTextField quantityField;
    private JButton addButton, updateStatusButton;
    private JTable requestTable;
    private DefaultTableModel tableModel;
    private int selectedRequestId = -1;

    public BloodRequestForm() {
        setTitle("Blood Requests");
        setSize(900, 520);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        Image icon = Toolkit.getDefaultToolkit().getImage("C:\\Users\\Lenovo\\Desktop\\Project DB\\icons8-blood-drop-96.png");
        setIconImage(icon);

        // Gradient background panel
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15)) {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setPaint(new GradientPaint(0, 0, Color.WHITE, 0, getHeight(), new Color(235, 240, 255)));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setContentPane(mainPanel);

        // Title
        JLabel title = new JLabel("Blood Request Management", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(60, 63, 65));
        mainPanel.add(title, BorderLayout.NORTH);

        // Form panel on the left
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setPreferredSize(new Dimension(330, 420));
        formPanel.setBorder(BorderFactory.createTitledBorder("Request Form"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        recipientBox = new JComboBox<>(getRecipients());
        bloodGroupBox = new JComboBox<>(getBloodGroups());
        quantityField = createInputField();
        statusBox = new JComboBox<>(new String[]{"Pending", "Fulfilled", "Rejected"});
        statusBox.setEnabled(false);

        String[] labels = {"Recipient:", "Blood Group:", "Quantity:", "Status:"};
        JComponent[] fields = {recipientBox, bloodGroupBox, quantityField, statusBox};

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            formPanel.add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1;
            formPanel.add(fields[i], gbc);
        }

        // Button panel with corrected FlowLayout
        addButton = styledButton("Add Request");
        updateStatusButton = styledButton("Update Status");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)); // Fixed here
        buttonPanel.setOpaque(false);
        buttonPanel.add(addButton);
        buttonPanel.add(updateStatusButton);

        gbc.gridx = 0;
        gbc.gridy = labels.length;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        mainPanel.add(formPanel, BorderLayout.WEST);

        // Table panel
        tableModel = new DefaultTableModel(new String[]{"ID", "Recipient", "Group", "Quantity", "Status", "Date"}, 0);
        requestTable = new JTable(tableModel);
        requestTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        requestTable.setRowHeight(24);
        JScrollPane scrollPane = new JScrollPane(requestTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Request Records"));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        loadRequests();

        requestTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && requestTable.getSelectedRow() != -1) {
                selectedRequestId = Integer.parseInt(tableModel.getValueAt(requestTable.getSelectedRow(), 0).toString());
                statusBox.setSelectedItem(tableModel.getValueAt(requestTable.getSelectedRow(), 4).toString());
                statusBox.setEnabled(true);
            }
        });

        addButton.addActionListener(e -> addRequest());
        updateStatusButton.addActionListener(e -> updateStatus());

        setResizable(false);
        setVisible(true);
    }

    private JTextField createInputField() {
        JTextField field = new JTextField(15);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        return field;
    }

    private JButton styledButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(new Color(100, 149, 237));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(160, 0, 0));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(178, 34, 34));
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
            String[] result = new String[model.getSize()];
            for (int i = 0; i < model.getSize(); i++) result[i] = model.getElementAt(i);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return new String[]{};
        }
    }

    private String[] getRecipients() {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT RecipientID, FullName FROM Recipient")) {
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
            while (rs.next()) {
                model.addElement(rs.getInt("RecipientID") + " - " + rs.getString("FullName"));
            }
            String[] result = new String[model.getSize()];
            for (int i = 0; i < model.getSize(); i++) result[i] = model.getElementAt(i);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return new String[]{};
        }
    }

    private void loadRequests() {
        tableModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT R.RequestID, C.FullName, R.BloodGroup, R.QuantityRequested, R.Status, R.RequestDate " +
                             "FROM BloodRequest R JOIN Recipient C ON R.RecipientID = C.RecipientID")) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("RequestID"),
                        rs.getString("FullName"),
                        rs.getString("BloodGroup"),
                        rs.getInt("QuantityRequested"),
                        rs.getString("Status"),
                        rs.getDate("RequestDate")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading requests: " + e.getMessage());
        }
    }

    private void addRequest() {
        String quantityText = quantityField.getText().trim();
        String selectedRecipient = (String) recipientBox.getSelectedItem();
        String selectedGroup = (String) bloodGroupBox.getSelectedItem();

        if (selectedRecipient == null || selectedGroup == null || quantityText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityText);
            if (quantity <= 0) {
                JOptionPane.showMessageDialog(this, "Quantity must be a positive number.");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Quantity must be a valid number.");
            return;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO BloodRequest (RecipientID, BloodGroup, QuantityRequested, Status, RequestDate) VALUES (?, ?, ?, 'Pending', NOW())")) {
            int recipientId = Integer.parseInt(selectedRecipient.split(" - ")[0]);

            stmt.setInt(1, recipientId);
            stmt.setString(2, selectedGroup);
            stmt.setInt(3, quantity);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Request added.");
            quantityField.setText("");
            statusBox.setSelectedItem("Pending");
            statusBox.setEnabled(false);
            loadRequests();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void updateStatus() {
        if (selectedRequestId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a request first.");
            return;
        }

        String newStatus = (String) statusBox.getSelectedItem();

        if (newStatus == null || newStatus.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a valid status.");
            return;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE BloodRequest SET Status = ? WHERE RequestID = ?")) {
            stmt.setString(1, newStatus);
            stmt.setInt(2, selectedRequestId);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Request status updated.");
            loadRequests();
            statusBox.setEnabled(false);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error updating status: " + e.getMessage());
        }
    }
}
