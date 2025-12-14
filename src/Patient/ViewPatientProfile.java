/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Patient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.regex.Pattern;

public class ViewPatientProfile extends JFrame {

    private final String patientId;
    private Connection conn;

    // Fields for editable details
    private JTextField txtContact, txtEmail, txtAddress, txtInsurance;

    public ViewPatientProfile(String patientId) {
        this.patientId = patientId;
        setTitle("Patient Profile - " + patientId);
        setSize(950, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(245, 247, 250));

        // ===== Header =====
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(30, 136, 229));
        header.setPreferredSize(new Dimension(0, 70));

        JButton backButton = new JButton("← Back");
        backButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        backButton.setBackground(new Color(240, 240, 240));
        backButton.setForeground(Color.BLACK);
        backButton.setFocusPainted(false);
        backButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> {
            dispose();
            new PatientDashboard(patientId).setVisible(true);
        });

        JLabel titleLabel = new JLabel("👤 Patient Profile");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));

        JPanel leftHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        leftHeader.setOpaque(false);
        leftHeader.add(backButton);
        leftHeader.add(titleLabel);

        header.add(leftHeader, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // ===== Center Panel =====
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 20, 12, 20);
        gbc.anchor = GridBagConstraints.WEST;

        // Profile Image
        JLabel profileImage = new JLabel();
        profileImage.setPreferredSize(new Dimension(200, 200));
        profileImage.setHorizontalAlignment(JLabel.CENTER);
        profileImage.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 6;
        contentPanel.add(profileImage, gbc);
        gbc.gridheight = 1;

        // Labels + Fields
        JLabel lblId = createLabel("Patient ID:");
        JLabel lblName = createLabel("Name:");
        JLabel lblGender = createLabel("Gender:");
        JLabel lblDob = createLabel("DOB:");
        JLabel lblAadhaar = createLabel("Aadhaar:");

        JTextField txtId = createField(false);
        JTextField txtName = createField(false);
        JTextField txtGender = createField(false);
        JTextField txtDob = createField(false);
        JTextField txtAadhaar = createField(false);

        txtContact = createField(true);
        txtEmail = createField(true);
        txtAddress = createField(true);
        txtInsurance = createField(true);

        // Arrange in Grid
        String[] fieldNames = {"Patient ID", "Name", "Gender", "DOB", "Aadhaar",
                               "Contact", "Email", "Address", "Insurance"};
        JComponent[] fieldComponents = {txtId, txtName, txtGender, txtDob, txtAadhaar,
                                        txtContact, txtEmail, txtAddress, txtInsurance};
        JLabel[] labels = {lblId, lblName, lblGender, lblDob, lblAadhaar,
                           createLabel("Contact:"), createLabel("Email:"),
                           createLabel("Address:"), createLabel("Insurance:")};

        gbc.gridx = 1;
        gbc.gridy = 0;
        for (int i = 0; i < fieldNames.length; i++) {
            contentPanel.add(labels[i], gbc);
            gbc.gridx = 2;
            contentPanel.add(fieldComponents[i], gbc);
            gbc.gridy++;
            gbc.gridx = 1;
        }

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        // ===== Footer with Update Button =====
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setBackground(new Color(240, 240, 240));

        JButton btnUpdate = new JButton("Update");
        btnUpdate.setBackground(new Color(46, 204, 113));
        btnUpdate.setForeground(Color.WHITE);
        btnUpdate.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnUpdate.setFocusPainted(false);
        btnUpdate.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btnUpdate.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnUpdate.addActionListener(e -> updateDetails());

        footer.add(btnUpdate);
        add(footer, BorderLayout.SOUTH);

        // ===== Load Data =====
        try {
            connectDB();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM patients WHERE patient_id=?");
            stmt.setString(1, patientId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                txtId.setText(rs.getString("patient_id"));
                txtName.setText(rs.getString("name"));
                txtGender.setText(rs.getString("gender"));
                txtDob.setText(rs.getString("dob"));
                txtAadhaar.setText(rs.getString("aadhaar"));
                txtContact.setText(rs.getString("contact"));
                txtEmail.setText(rs.getString("email"));
                txtAddress.setText(rs.getString("address"));
                txtInsurance.setText(rs.getString("insurance"));

                String imagePath = rs.getString("image_path");
                if (imagePath != null && !imagePath.isEmpty()) {
                    ImageIcon icon = new ImageIcon(imagePath);
                    Image img = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                    profileImage.setIcon(new ImageIcon(img));
                } else {
                    profileImage.setIcon(new ImageIcon(new ImageIcon("default-profile.png")
                            .getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH)));
                }
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading patient data: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        setVisible(true);
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        return lbl;
    }

    private JTextField createField(boolean editable) {
        JTextField txt = new JTextField(25);
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txt.setEditable(editable);
        return txt;
    }

    private void connectDB() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital_db", "root", "");
    }

    private void updateDetails() {
        // ===== Validations =====
        String contact = txtContact.getText().trim();
        String email = txtEmail.getText().trim();
        String address = txtAddress.getText().trim();
        String insurance = txtInsurance.getText().trim();

        // Contact validation
        if (!contact.matches("\\d{10}")) {
            JOptionPane.showMessageDialog(this, "Contact number must be 10 digits.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Email validation
        if (!Pattern.matches("^[A-Za-z0-9+_.-]+@(.+)$", email)) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Address validation
        if (address.isEmpty() || address.length() < 5) {
            JOptionPane.showMessageDialog(this, "Address must be at least 5 characters long.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Insurance validation (optional)
        if (!insurance.isEmpty() && !insurance.matches("[A-Za-z0-9\\- ]+")) {
            JOptionPane.showMessageDialog(this, "Insurance should be alphanumeric only.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // ===== Update Database =====
        try {
            connectDB();
            PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE patients SET contact=?, email=?, address=?, insurance=? WHERE patient_id=?");
            stmt.setString(1, contact);
            stmt.setString(2, email);
            stmt.setString(3, address);
            stmt.setString(4, insurance);
            stmt.setString(5, patientId);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Profile updated successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            }
            stmt.close();
            conn.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error updating details: " + ex.getMessage(),
                    "Update Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ViewPatientProfile("PAT97125"));
    }
}

