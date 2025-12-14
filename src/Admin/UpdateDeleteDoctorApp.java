/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package admin;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Files;
import java.sql.*;
import java.util.Random;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class UpdateDeleteDoctorApp extends JFrame {

    private JTextField tfDoctorIdSearch, tfName, tfEmail, tfQualification;
    private JPasswordField pfPass, pfConfirm;
    private JComboBox<String> cbGender, cbSpeciality;
    private JLabel lblImage, lblDoctorId;
    private JButton btnSearch, btnUpload, btnUpdate, btnDelete, btnBack;
    private File selectedFile = null;
    private byte[] imageBytes = null;
    private String currentDoctorId = null;

    // OTP storage
    private String currentOTP = null;

    public UpdateDeleteDoctorApp() {
        setTitle("Update/Delete Doctor");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(900, 700));

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(25, 40, 25, 40));
        mainPanel.setBackground(new Color(245, 245, 250));
        setContentPane(mainPanel);

        JLabel title = new JLabel("Doctor Update/Delete Module", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));
        title.setForeground(new Color(45, 85, 160));
        title.setBorder(new EmptyBorder(0, 0, 20, 0));
        mainPanel.add(title, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(25, 25));
        centerPanel.setBackground(mainPanel.getBackground());
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(45, 85, 160), 2),
                "Doctor Details",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 18),
                new Color(45, 85, 160)
        ));
        centerPanel.add(formPanel, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 12, 10, 12);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Search by Doctor ID
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Search by Doctor ID:"), gbc);

        tfDoctorIdSearch = new JTextField(22);
        gbc.gridx = 1;
        formPanel.add(tfDoctorIdSearch, gbc);

        btnSearch = createButton("Search", new Color(0, 123, 255));
        btnSearch.setPreferredSize(new Dimension(100, 32));
        gbc.gridx = 2;
        formPanel.add(btnSearch, gbc);

        // Doctor ID Label
        gbc.gridx = 0; gbc.gridy++;
        gbc.gridwidth = 3;
        lblDoctorId = new JLabel("Doctor ID: ");
        lblDoctorId.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblDoctorId.setForeground(new Color(0, 120, 215));
        lblDoctorId.setBorder(new EmptyBorder(0, 0, 15, 0));
        formPanel.add(lblDoctorId, gbc);
        gbc.gridwidth = 1;

        // Fields
        String[] labels = {"Name:", "Email:", "Password:", "Confirm Password:", "Gender:", "Qualification:", "Speciality:"};
        Component[] inputs = new Component[7];

        tfName = new JTextField(22);
        tfEmail = new JTextField(22);
        pfPass = new JPasswordField(22);
        pfConfirm = new JPasswordField(22);
        cbGender = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        tfQualification = new JTextField(22);
        cbSpeciality = new JComboBox<>(new String[]{
                "Cardiology", "Dermatology", "Neurology", "Pediatrics", "Psychiatry",
                "General Surgery", "Orthopedics", "Radiology", "Oncology", "Gynecology"
        });

        inputs[0] = tfName;
        inputs[1] = tfEmail;
        inputs[2] = pfPass;
        inputs[3] = pfConfirm;
        inputs[4] = cbGender;
        inputs[5] = tfQualification;
        inputs[6] = cbSpeciality;

        for (int i = 0; i < labels.length; i++) {
            gbc.gridy++;
            gbc.gridx = 0;
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            formPanel.add(lbl, gbc);

            gbc.gridx = 1;
            gbc.gridwidth = 2;
            Component input = inputs[i];
            if (input instanceof JComboBox) {
                ((JComboBox<?>) input).setFont(new Font("Segoe UI", Font.PLAIN, 15));
            } else if (input instanceof JTextField || input instanceof JPasswordField) {
                input.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            }
            formPanel.add(input, gbc);
            gbc.gridwidth = 1;
        }

        // Right panel for image + upload button
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(mainPanel.getBackground());
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        centerPanel.add(rightPanel, BorderLayout.EAST);

        lblImage = new JLabel("No image selected", SwingConstants.CENTER);
        lblImage.setPreferredSize(new Dimension(180, 180));
        lblImage.setMaximumSize(new Dimension(180, 180));
        lblImage.setMinimumSize(new Dimension(180, 180));  // Consistent sizing
        lblImage.setBorder(BorderFactory.createLineBorder(new Color(160, 160, 160), 2));
        lblImage.setOpaque(true);
        lblImage.setBackground(Color.WHITE);
        lblImage.setAlignmentX(Component.CENTER_ALIGNMENT);
        rightPanel.add(lblImage);

        rightPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        btnUpload = createButton("Upload Image", new Color(0, 123, 255));
        btnUpload.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnUpload.setMaximumSize(new Dimension(180, 40));
        rightPanel.add(btnUpload);

        // Bottom panel buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 15));
        btnPanel.setBackground(mainPanel.getBackground());
        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        btnUpdate = createButton("Update", new Color(40, 167, 69));
        btnDelete = createButton("Delete", new Color(220, 53, 69));
        btnBack = createButton("Back", new Color(108, 117, 125));

        Dimension btnSize = new Dimension(140, 42);
        btnUpdate.setPreferredSize(btnSize);
        btnDelete.setPreferredSize(btnSize);
        btnBack.setPreferredSize(btnSize);

        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnBack);

        // Action listeners
        btnSearch.addActionListener(e -> searchDoctor());
        btnUpload.addActionListener(e -> uploadImage());

        btnUpdate.addActionListener(e -> {
            if (!validateForm()) return;
            if (showOtpDialog()) {
                updateDoctor();
            } else {
                JOptionPane.showMessageDialog(this, "Incorrect OTP. Update cancelled.");
            }
        });

        btnDelete.addActionListener(e -> {
            if (currentDoctorId == null) {
                JOptionPane.showMessageDialog(this, "Please search and select a doctor first.");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this doctor?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (showOtpDialog()) {
                    deleteDoctor();
                } else {
                    JOptionPane.showMessageDialog(this, "Incorrect OTP. Deletion cancelled.");
                }
            }
        });

        btnBack.addActionListener(e -> {
            dispose();
            // new DoctorMainDashboard().setVisible(true);
        });

        setVisible(true);
    }

    private JButton createButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(bgColor.darker());
            }

            public void mouseExited(MouseEvent e) {
                btn.setBackground(bgColor);
            }
        });
        return btn;
    }

    private void searchDoctor() {
        String searchId = tfDoctorIdSearch.getText().trim();
        if (searchId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter Doctor ID to search.");
            return;
        }

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital_db", "root", "")) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM doctor WHERE doctor_id = ?");
            ps.setString(1, searchId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                currentDoctorId = rs.getString("doctor_id");
                lblDoctorId.setText("Doctor ID: " + currentDoctorId);
                tfName.setText(rs.getString("name"));
                tfEmail.setText(rs.getString("email"));
                pfPass.setText(rs.getString("password"));
                pfConfirm.setText(rs.getString("password"));
                cbGender.setSelectedItem(rs.getString("gender"));
                tfQualification.setText(rs.getString("qualification"));
                cbSpeciality.setSelectedItem(rs.getString("speciality"));

                imageBytes = rs.getBytes("image");
                if (imageBytes != null) {
                    ImageIcon icon = new ImageIcon(imageBytes);
                    Image scaled = icon.getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH);
                    lblImage.setIcon(new ImageIcon(scaled));
                    lblImage.setText("");
                } else {
                    lblImage.setIcon(null);
                    lblImage.setText("No image selected");
                }
            } else {
                JOptionPane.showMessageDialog(this, "No doctor found with the given ID.");
                clearForm();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error while searching doctor.");
        }
    }

    private void uploadImage() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedFile = chooser.getSelectedFile();
            String fileName = selectedFile.getName().toLowerCase();
            if (!(fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png"))) {
            JOptionPane.showMessageDialog(this, "Invalid image format. Only JPG, JPEG, or PNG allowed.");
            selectedFile = null;
            return;
        }
            try {
                imageBytes = Files.readAllBytes(selectedFile.toPath());
                ImageIcon icon = new ImageIcon(imageBytes);
                Image scaled = icon.getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH);
                lblImage.setIcon(new ImageIcon(scaled));
                lblImage.setText("");
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to load image.");
            }
        }
    }

    private void updateDoctor() {
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital_db", "root", "")) {
            String sql = "UPDATE doctor SET name=?, email=?, password=?, gender=?, qualification=?, speciality=?, image=? WHERE doctor_id=?";
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, tfName.getText().trim());
            ps.setString(2, tfEmail.getText().trim());
            ps.setString(3, new String(pfPass.getPassword()));
            ps.setString(4, cbGender.getSelectedItem().toString());
            ps.setString(5, tfQualification.getText().trim());
            ps.setString(6, cbSpeciality.getSelectedItem().toString());
            ps.setBytes(7, imageBytes);
            ps.setString(8, currentDoctorId);

            int updated = ps.executeUpdate();
            if (updated > 0) {
                JOptionPane.showMessageDialog(this, "Doctor updated successfully.");
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Update failed.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error while updating doctor.");
        }
    }

    private void deleteDoctor() {
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital_db", "root", "")) {
            PreparedStatement ps = con.prepareStatement("DELETE FROM doctor WHERE doctor_id = ?");
            ps.setString(1, currentDoctorId);
            int deleted = ps.executeUpdate();
            if (deleted > 0) {
                JOptionPane.showMessageDialog(this, "Doctor deleted successfully.");
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Deletion failed.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Doctor details exist with patient(s), can't delete.");
        }
    }

    // Updated validation method with more checks
     private boolean validateForm() {
    String name = tfName.getText().trim();
    String email = tfEmail.getText().trim();
    String password = new String(pfPass.getPassword());
    String confirm = new String(pfConfirm.getPassword());
    String qualification = tfQualification.getText().trim();

    

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

    private void clearForm() {
        tfDoctorIdSearch.setText("");
        tfName.setText("");
        tfEmail.setText("");
        pfPass.setText("");
        pfConfirm.setText("");
        tfQualification.setText("");
        cbGender.setSelectedIndex(0);
        cbSpeciality.setSelectedIndex(0);
        lblImage.setIcon(null);
        lblImage.setText("No image selected");
        lblDoctorId.setText("Doctor ID: ");
        imageBytes = null;
        currentDoctorId = null;
        currentOTP = null;
    }

    // Show OTP dialog and verify OTP
    private boolean showOtpDialog() {
        generateOTP();
        // In real app, send OTP via email/SMS securely. Here we show it directly.
        JOptionPane.showMessageDialog(this, "Your OTP is: " + currentOTP, "OTP Verification", JOptionPane.INFORMATION_MESSAGE);

        String inputOtp = JOptionPane.showInputDialog(this, "Enter OTP to confirm:");
        if (inputOtp == null) return false; // user cancelled

        return currentOTP.equals(inputOtp.trim());
    }

    private void generateOTP() {
        Random rnd = new Random();
        int number = 100000 + rnd.nextInt(900000); // 6 digit OTP
        currentOTP = String.valueOf(number);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(UpdateDeleteDoctorApp::new);
    }
}








