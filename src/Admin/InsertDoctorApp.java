/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Admin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.*;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class InsertDoctorApp extends JFrame {

    private JTextField tfDoctorId, tfName, tfEmail, tfQualification;
    private JPasswordField pfPass, pfConfirm;
    private JComboBox<String> cbGender, cbSpeciality;
    private JLabel lblImage;
    private File selectedFile;
    private JButton btnUpload, btnSubmit, btnBack;
    private String generatedOTP;

    public InsertDoctorApp() {
        setTitle("Doctor Registration");
        setSize(950, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel header = new JLabel("Doctor Registration Form", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 32));
        header.setForeground(Color.WHITE);
        header.setOpaque(true);
        header.setBackground(new Color(40, 60, 90));
        header.setPreferredSize(new Dimension(getWidth(), 80));
        add(header, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("Segoe UI", Font.PLAIN, 16);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 15);
        Dimension fieldDim = new Dimension(320, 35);
        int y = 0;

        // Doctor ID
        gbc.gridx = 0; gbc.gridy = y;
        formPanel.add(new JLabel("Doctor ID:", JLabel.LEFT), gbc);
        gbc.gridx = 1;
        tfDoctorId = createTextField(fieldFont, fieldDim);
        tfDoctorId.setEditable(false);
        formPanel.add(tfDoctorId, gbc);

        // Name
        gbc.gridx = 0; gbc.gridy = ++y;
        formPanel.add(new JLabel("Name:", JLabel.LEFT), gbc);
        gbc.gridx = 1;
        tfName = createTextField(fieldFont, fieldDim);
        formPanel.add(tfName, gbc);

        // Email
        gbc.gridx = 0; gbc.gridy = ++y;
        formPanel.add(new JLabel("Email:", JLabel.LEFT), gbc);
        gbc.gridx = 1;
        tfEmail = createTextField(fieldFont, fieldDim);
        formPanel.add(tfEmail, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = ++y;
        formPanel.add(new JLabel("Password:", JLabel.LEFT), gbc);
        gbc.gridx = 1;
        pfPass = new JPasswordField();
        pfPass.setFont(fieldFont);
        pfPass.setPreferredSize(fieldDim);
        formPanel.add(pfPass, gbc);

        // Confirm Password
        gbc.gridx = 0; gbc.gridy = ++y;
        formPanel.add(new JLabel("Confirm Password:", JLabel.LEFT), gbc);
        gbc.gridx = 1;
        pfConfirm = new JPasswordField();
        pfConfirm.setFont(fieldFont);
        pfConfirm.setPreferredSize(fieldDim);
        formPanel.add(pfConfirm, gbc);

        // Gender
        gbc.gridx = 0; gbc.gridy = ++y;
        formPanel.add(new JLabel("Gender:", JLabel.LEFT), gbc);
        gbc.gridx = 1;
        cbGender = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        cbGender.setFont(fieldFont);
        cbGender.setPreferredSize(fieldDim);
        formPanel.add(cbGender, gbc);

        // Qualification
        gbc.gridx = 0; gbc.gridy = ++y;
        formPanel.add(new JLabel("Qualification:", JLabel.LEFT), gbc);
        gbc.gridx = 1;
        tfQualification = createTextField(fieldFont, fieldDim);
        formPanel.add(tfQualification, gbc);

        // Speciality
        gbc.gridx = 0; gbc.gridy = ++y;
        formPanel.add(new JLabel("Speciality:", JLabel.LEFT), gbc);
        gbc.gridx = 1;
        String[] specialities = {
            "Cardiologist", "Dermatologist", "Neurologist", "Pediatrician",
            "Psychiatrist", "Radiologist", "Surgeon", "General Physician",
            "Orthopedic", "ENT Specialist"
        };
        cbSpeciality = new JComboBox<>(specialities);
        cbSpeciality.setFont(fieldFont);
        cbSpeciality.setPreferredSize(fieldDim);
        formPanel.add(cbSpeciality, gbc);

        // Image Upload
        gbc.gridx = 0; gbc.gridy = ++y;
        formPanel.add(new JLabel("Image:", JLabel.LEFT), gbc);
        gbc.gridx = 1;
        JPanel imgPanel = new JPanel(new BorderLayout());
        imgPanel.setPreferredSize(fieldDim);
        lblImage = new JLabel("No file selected", JLabel.LEFT);
        lblImage.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        btnUpload = new JButton("Upload");
        styleButton(btnUpload, new Color(100, 118, 255));
        imgPanel.add(lblImage, BorderLayout.CENTER);
        imgPanel.add(btnUpload, BorderLayout.EAST);
        formPanel.add(imgPanel, gbc);

        // Submit Button
        gbc.gridx = 0; gbc.gridy = ++y;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        btnSubmit = new JButton("Register Doctor");
        btnSubmit.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnSubmit.setPreferredSize(new Dimension(300, 45));
        styleButton(btnSubmit, new Color(45, 180, 90));
        formPanel.add(btnSubmit, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Back Button
        btnBack = new JButton("← Back");
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 14));
        styleButton(btnBack, new Color(230, 70, 70));
        btnBack.setPreferredSize(new Dimension(100, 35));
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setBackground(new Color(250, 250, 250));
        bottomPanel.add(btnBack);
        add(bottomPanel, BorderLayout.SOUTH);

        generateDoctorId();

        btnUpload.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                selectedFile = fc.getSelectedFile();
                lblImage.setText(selectedFile.getName());
            }
        });

        btnSubmit.addActionListener(e -> {
            if (validateForm()) {
                generateAndShowOTP();
                String enteredOTP = JOptionPane.showInputDialog(this, "Enter the OTP shown:");

                if (enteredOTP != null && enteredOTP.equals(generatedOTP)) {
                    registerDoctor();
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid OTP. Registration cancelled.");
                }
            }
        });

        btnBack.addActionListener(e -> {
            dispose();
            new DoctorMainDashboard(); // Ensure this class exists
        });

        setVisible(true);
    }

    private JTextField createTextField(Font font, Dimension size) {
        JTextField tf = new JTextField();
        tf.setFont(font);
        tf.setPreferredSize(size);
        return tf;
    }

    private void styleButton(JButton button, Color bgColor) {
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { button.setBackground(bgColor.darker()); }
            public void mouseExited(MouseEvent e) { button.setBackground(bgColor); }
        });
    }

    private void generateDoctorId() {
        String id = "DOC" + (int) (Math.random() * 9000 + 1000);
        tfDoctorId.setText(id);
    }

    private void generateAndShowOTP() {
        int otp = 100000 + (int)(Math.random() * 900000);
        generatedOTP = String.valueOf(otp);
        JOptionPane.showMessageDialog(this,
            "Your OTP for registration is: " + generatedOTP,
            "OTP Verification", JOptionPane.INFORMATION_MESSAGE);
    }

    private boolean validateForm() {
    String name = tfName.getText().trim();
    String email = tfEmail.getText().trim();
    String password = new String(pfPass.getPassword());
    String confirm = new String(pfConfirm.getPassword());
    String qualification = tfQualification.getText().trim();

    if (name.isEmpty() || email.isEmpty() || password.isEmpty() ||
        confirm.isEmpty() || qualification.isEmpty() || selectedFile == null) {
        JOptionPane.showMessageDialog(this, "Please fill in all fields and upload an image.", "Validation Error", JOptionPane.WARNING_MESSAGE);
        return false;
    }

    // Name validation: alphabets and spaces only
    if (!name.matches("^[a-zA-Z\\s]+$")) {
        JOptionPane.showMessageDialog(this, "Name should only contain alphabets and spaces.", "Validation Error", JOptionPane.WARNING_MESSAGE);
        return false;
    }

    // Email validation
    if (!email.matches("^[\\w-.]+@[\\w-]+\\.[a-zA-Z]{2,}$")) {
        JOptionPane.showMessageDialog(this, "Invalid email format.", "Validation Error", JOptionPane.WARNING_MESSAGE);
        return false;
    }

    // Password minimum 8 characters + 1 uppercase + 1 lowercase + 1 number + 1 special character
    if (!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$")) {
        JOptionPane.showMessageDialog(this,
            "Password must be at least 8 characters long and include:\n• One uppercase letter\n• One lowercase letter\n• One number\n• One special character (e.g., @, #, $, !)",
            "Password Format Error", JOptionPane.WARNING_MESSAGE);
        return false;
    }

    if (!password.equals(confirm)) {
        JOptionPane.showMessageDialog(this, "Passwords do not match.", "Validation Error", JOptionPane.WARNING_MESSAGE);
        return false;
    }

    return true;
}
    private String formatNameProper(String name) {
    if (name == null || name.isEmpty()) return name;
    String[] words = name.trim().toLowerCase().split("\\s+");
    StringBuilder formatted = new StringBuilder();
    for (String word : words) {
        if (word.length() > 0) {
            formatted.append(Character.toUpperCase(word.charAt(0)))
                     .append(word.substring(1))
                     .append(" ");
        }
    }
    return formatted.toString().trim();
}


    private void registerDoctor() {
        try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/hospital_db", "root", "")) {

            String doctorId = tfDoctorId.getText().trim();
            String name = formatNameProper(tfName.getText().trim());
            String email = tfEmail.getText().trim();
            String password = new String(pfPass.getPassword());
            String gender = (String) cbGender.getSelectedItem();
            String qualification = tfQualification.getText().trim();
            String speciality = (String) cbSpeciality.getSelectedItem();

            String sql = "INSERT INTO doctor "
                       + "(doctor_id, name, email, password, gender, qualification, speciality, image) "
                       + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, doctorId);
            pst.setString(2, name);
            pst.setString(3, email);
            pst.setString(4, password);
            pst.setString(5, gender);
            pst.setString(6, qualification);
            pst.setString(7, speciality);
            pst.setBinaryStream(8,
                new FileInputStream(selectedFile),
                (int) selectedFile.length());

            int result = pst.executeUpdate();
            if (result > 0) {
                JOptionPane.showMessageDialog(this, "Doctor registered successfully!");
                clearForm();
            }

        } catch (SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(this, "Doctor with this email already exists.");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void clearForm() {
        tfName.setText("");
        tfEmail.setText("");
        pfPass.setText("");
        pfConfirm.setText("");
        tfQualification.setText("");
        cbSpeciality.setSelectedIndex(0);
        lblImage.setText("No file selected");
        cbGender.setSelectedIndex(0);
        selectedFile = null;
        generateDoctorId();
    }

    public static void main(String[] args) {
        try { Class.forName("com.mysql.cj.jdbc.Driver"); } catch (Exception e) { /* ignore */ }
        SwingUtilities.invokeLater(InsertDoctorApp::new);
    }
}


