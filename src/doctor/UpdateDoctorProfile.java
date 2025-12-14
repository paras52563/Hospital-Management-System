/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package doctor;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;
import java.io.*;

public class UpdateDoctorProfile extends JFrame {
    private String doctorId;
    private Connection conn;

    private JTextField tfName, tfEmail, tfQualification, tfSpeciality;
    private JPasswordField pfPassword;
    private JComboBox<String> cbGender;
    private JLabel lblImage;
    private JButton btnUploadImage, btnSave, btnBack;
    private byte[] imageBytes;
    private File selectedImageFile;

    public UpdateDoctorProfile(String doctorId) {
        this.doctorId = doctorId;

        setTitle("Update Doctor Profile");
        setSize(500, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initComponents();
        connectDB();
        loadDoctorData();

        setVisible(true);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Title
        JLabel lblTitle = new JLabel("Update Profile", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(41, 128, 185));
        panel.add(lblTitle, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                new EmptyBorder(15, 15, 15, 15)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        int row = 0;

        // Name (readonly)
        JLabel lblName = new JLabel("Name:");
        lblName.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(lblName, gbc);

        tfName = new JTextField();
        tfName.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        tfName.setEditable(false);
        gbc.gridx = 1; gbc.gridy = row++; formPanel.add(tfName, gbc);

        // Password
        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(lblPassword, gbc);

        pfPassword = new JPasswordField();
        pfPassword.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridx = 1; gbc.gridy = row++; formPanel.add(pfPassword, gbc);

        // Gender (readonly)
        JLabel lblGender = new JLabel("Gender:");
        lblGender.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(lblGender, gbc);

        cbGender = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        cbGender.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        cbGender.setEnabled(false);
        gbc.gridx = 1; gbc.gridy = row++; formPanel.add(cbGender, gbc);

        // Qualification
        JLabel lblQualification = new JLabel("Qualification:");
        lblQualification.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(lblQualification, gbc);

        tfQualification = new JTextField();
        tfQualification.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        tfQualification.setEditable(false);
        gbc.gridx = 1; gbc.gridy = row++; formPanel.add(tfQualification, gbc);

        // Email (readonly)
        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(lblEmail, gbc);

        tfEmail = new JTextField();
        tfEmail.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        tfEmail.setEditable(false); // make email readonly
        gbc.gridx = 1; gbc.gridy = row++; formPanel.add(tfEmail, gbc);

        // Speciality
        JLabel lblSpeciality = new JLabel("Speciality:");
        lblSpeciality.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(lblSpeciality, gbc);

        tfSpeciality = new JTextField();
        tfSpeciality.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridx = 1; gbc.gridy = row++; formPanel.add(tfSpeciality, gbc);

        // Profile Image
        lblImage = new JLabel("No Image", SwingConstants.CENTER);
        lblImage.setPreferredSize(new Dimension(140, 140));
        lblImage.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        lblImage.setOpaque(true);
        lblImage.setBackground(new Color(230, 230, 230));
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(lblImage, gbc);

        // Upload button
        btnUploadImage = new JButton("Upload Image");
        btnUploadImage.setBackground(new Color(41, 128, 185));
        btnUploadImage.setForeground(Color.WHITE);
        btnUploadImage.setFocusPainted(false);
        btnUploadImage.addActionListener(e -> chooseImage());
        gbc.gridx = 0; gbc.gridy = ++row; gbc.gridwidth = 2; formPanel.add(btnUploadImage, gbc);

        panel.add(formPanel, BorderLayout.CENTER);

        // Bottom Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        btnPanel.setBackground(new Color(245, 245, 245));

        btnBack = new JButton("Back");
        btnBack.setBackground(new Color(192, 57, 43));
        btnBack.setForeground(Color.WHITE);
        btnBack.setFocusPainted(false);
        btnBack.addActionListener(e -> {
            dispose();
            new DoctorDashboard(doctorId).setVisible(true);
        });
        btnPanel.add(btnBack);

        btnSave = new JButton("Save Changes");
        btnSave.setBackground(new Color(52, 152, 219));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);
        btnSave.addActionListener(e -> saveProfile());
        btnPanel.add(btnSave);

        panel.add(btnPanel, BorderLayout.SOUTH);

        add(panel);
    }

    private void connectDB() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital_db", "root", "");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database connection failed: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadDoctorData() {
        String sql = "SELECT * FROM doctor WHERE doctor_id = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, doctorId);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                tfName.setText(rs.getString("name"));
                pfPassword.setText(rs.getString("password"));
                cbGender.setSelectedItem(rs.getString("gender"));
                tfQualification.setText(rs.getString("qualification"));
                tfEmail.setText(rs.getString("email"));
                tfSpeciality.setText(rs.getString("speciality"));

                Blob blob = rs.getBlob("image");
                if (blob != null) {
                    imageBytes = blob.getBytes(1, (int) blob.length());
                    ImageIcon icon = new ImageIcon(imageBytes);
                    Image img = icon.getImage().getScaledInstance(140, 140, Image.SCALE_SMOOTH);
                    lblImage.setIcon(new ImageIcon(img));
                    lblImage.setText("");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load doctor data: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void chooseImage() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Image files", "jpg", "jpeg", "png", "gif"));
        int option = chooser.showOpenDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            selectedImageFile = chooser.getSelectedFile();
            String fileName = selectedImageFile.getName().toLowerCase();
            if (!(fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") ||
                  fileName.endsWith(".png") || fileName.endsWith(".gif"))) {
                JOptionPane.showMessageDialog(this, "Invalid file type. Please select a valid image.",
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                ImageIcon icon = new ImageIcon(selectedImageFile.getAbsolutePath());
                Image img = icon.getImage().getScaledInstance(140, 140, Image.SCALE_SMOOTH);
                lblImage.setIcon(new ImageIcon(img));
                lblImage.setText("");

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                FileInputStream fis = new FileInputStream(selectedImageFile);
                byte[] buffer = new byte[1024];
                int read;
                while ((read = fis.read(buffer)) != -1) baos.write(buffer, 0, read);
                fis.close();
                imageBytes = baos.toByteArray();

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Failed to load image: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveProfile() {
        String password = new String(pfPassword.getPassword()).trim();
        String qualification = tfQualification.getText().trim();
        String speciality = tfSpeciality.getText().trim();

        // Password validation
        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Password cannot be empty.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this, "Password must be at least 6 characters long.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Qualification validation
        if (qualification.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Qualification cannot be empty.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!qualification.matches("^[a-zA-Z0-9.,\\- ]+$")) {
            JOptionPane.showMessageDialog(this, "Qualification contains invalid characters.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Speciality validation
        if (speciality.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Speciality cannot be empty.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!speciality.matches("^[a-zA-Z ]+$")) {
            JOptionPane.showMessageDialog(this, "Speciality should only contain alphabets and spaces.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Image validation (if selected)
        if (selectedImageFile != null) {
            String fileName = selectedImageFile.getName().toLowerCase();
            if (!(fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")
                    || fileName.endsWith(".png") || fileName.endsWith(".gif"))) {
                JOptionPane.showMessageDialog(this, "Please upload a valid image (jpg, jpeg, png, gif).",
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        // If all validations pass → Update database
        try {
            String sql = "UPDATE doctor SET password = ?, qualification = ?, speciality = ?, image = ? WHERE doctor_id = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, password);
            pst.setString(2, qualification);
            pst.setString(3, speciality);

            if (imageBytes != null) {
                pst.setBytes(4, imageBytes);
            } else {
                pst.setNull(4, Types.BLOB);
            }

            pst.setString(5, doctorId);

            int updated = pst.executeUpdate();
            if (updated > 0) {
                JOptionPane.showMessageDialog(this, "Profile updated successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "No changes made or update failed.",
                        "Update Failed", JOptionPane.WARNING_MESSAGE);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error saving profile: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UpdateDoctorProfile("DOC2420")); // replace with valid doctor_id
    }
}
