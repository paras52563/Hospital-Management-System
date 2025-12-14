/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Pharmacy;

import javax.swing.*;
import com.toedter.calendar.JDateChooser;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddMedicineModern extends JFrame {
    private JTextField tfName, tfBrand, tfQuantity, tfPrice;
    private JDateChooser expiryChooser;

    public AddMedicineModern() {
        setTitle("Add New Medicine");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header
        JLabel title = new JLabel("Add Medicine to Pharmacy", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(40, 55, 71));
        title.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        add(title, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("Segoe UI", Font.PLAIN, 16);

        tfName = new JTextField(20);
        tfBrand = new JTextField(20);
        tfQuantity = new JTextField(20);
        tfPrice = new JTextField(20);

        expiryChooser = new JDateChooser();
        expiryChooser.setDateFormatString("yyyy-MM-dd");
        expiryChooser.setPreferredSize(new Dimension(200, 28));
        expiryChooser.setMinSelectableDate(new Date()); // Prevent selecting past dates

        addField(formPanel, gbc, 0, "Medicine Name*", tfName, labelFont);
        addField(formPanel, gbc, 1, "Brand", tfBrand, labelFont);
        addField(formPanel, gbc, 2, "Quantity*", tfQuantity, labelFont);
        addField(formPanel, gbc, 3, "Price (Rs)*", tfPrice, labelFont);
        addField(formPanel, gbc, 4, "Expiry Date*", expiryChooser, labelFont);

        add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        btnPanel.setBackground(new Color(245, 245, 245));

        JButton btnAdd = createButton("Add Medicine", new Color(39, 174, 96));
        JButton btnClear = createButton("Clear", new Color(231, 76, 60));
        JButton btnBack = createButton("Back", new Color(52, 152, 219));

        btnPanel.add(btnAdd);
        btnPanel.add(btnClear);
        btnPanel.add(btnBack);
        add(btnPanel, BorderLayout.SOUTH);

        // Events
        btnAdd.addActionListener(e -> addMedicine());
        btnClear.addActionListener(e -> clearForm());
        btnBack.addActionListener(e -> {
            dispose();
            // Open your dashboard here, or just exit
            // new PharmacyDashboardModern().setVisible(true);
        });

        setVisible(true);
    }

    private void addField(JPanel panel, GridBagConstraints gbc, int y, String label, JComponent field, Font font) {
        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel lbl = new JLabel(label + ":");
        lbl.setFont(font);
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(field, gbc);
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(140, 40));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.darker());
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });
        return button;
    }

    private void addMedicine() {
        String name = tfName.getText().trim();
        String brand = tfBrand.getText().trim();
        String quantityStr = tfQuantity.getText().trim();
        String priceStr = tfPrice.getText().trim();
        Date expiryDate = expiryChooser.getDate();

        if (name.isEmpty() || quantityStr.isEmpty() || priceStr.isEmpty() || expiryDate == null) {
            JOptionPane.showMessageDialog(this, "Please fill all required (*) fields!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int quantity = Integer.parseInt(quantityStr);
            double price = Double.parseDouble(priceStr);
            String expiry = new SimpleDateFormat("yyyy-MM-dd").format(expiryDate);

            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital_db", "root", "");

            // Check if medicine with same name and brand exists
            String checkSql = "SELECT COUNT(*) FROM medicines WHERE name = ? AND brand = ?";
            PreparedStatement checkStmt = con.prepareStatement(checkSql);
            checkStmt.setString(1, name);
            checkStmt.setString(2, brand);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                if (count > 0) {
                    JOptionPane.showMessageDialog(this, "This medicine with the same brand already exists!", "Duplicate Entry", JOptionPane.ERROR_MESSAGE);
                    rs.close();
                    checkStmt.close();
                    con.close();
                    return; // Stop insertion
                }
            }
            rs.close();
            checkStmt.close();

            // Insert new medicine
            String sql = "INSERT INTO medicines(name, brand, quantity, price, expiry_date) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, name);
            pst.setString(2, brand);
            pst.setInt(3, quantity);
            pst.setDouble(4, price);
            pst.setString(5, expiry);

            int rows = pst.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Medicine added successfully!");
                clearForm();
            }

            pst.close();
            con.close();
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Quantity and Price must be numeric.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace(); // Important for debugging DB issues
            JOptionPane.showMessageDialog(this, "Database error! See console for details.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        tfName.setText("");
        tfBrand.setText("");
        tfQuantity.setText("");
        tfPrice.setText("");
        expiryChooser.setDate(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AddMedicineModern::new);
    }
}




