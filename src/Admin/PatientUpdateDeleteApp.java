/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package admin;

import Admin.PatientMainDashboard;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class PatientUpdateDeleteApp extends JFrame {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/hospital_db";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";

    private JTextField patientIdField, nameField, ageField, dobField, contactField, emailField, aadhaarField, guardianField, insuranceField;
    private JComboBox<String> genderCombo, bloodGroupCombo;
    private JTextArea addressArea;
    private JLabel imageLabel;
    private File selectedImageFile;
    private String currentImagePath;

    public PatientUpdateDeleteApp() {
        setTitle("Patient Update / Delete");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        setLocationRelativeTo(null);
        initComponents();
        setVisible(true);
    }
    private void initComponents() {
    Font headerFont = new Font("Segoe UI", Font.BOLD, 26);
    Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
    Font fieldFont = new Font("Segoe UI", Font.PLAIN, 14);

    JPanel mainPanel = new JPanel(new BorderLayout());
    mainPanel.setBackground(new Color(245, 247, 250));
    mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    // Header
    JLabel headerLabel = new JLabel("Patient Management", SwingConstants.CENTER);
    headerLabel.setFont(headerFont);
    headerLabel.setForeground(new Color(33, 150, 243));
    headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
    mainPanel.add(headerLabel, BorderLayout.NORTH);

    // Form panel
    JPanel formPanel = new JPanel(new GridBagLayout());
    formPanel.setBackground(mainPanel.getBackground());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(8, 10, 8, 10);
    gbc.fill = GridBagConstraints.HORIZONTAL;

    // --- Fields ---
    patientIdField = new JTextField(12); patientIdField.setFont(fieldFont);
    nameField = new JTextField(20); nameField.setFont(fieldFont);
    genderCombo = new JComboBox<>(new String[]{"Male","Female","Other"}); genderCombo.setFont(fieldFont);
    ageField = new JTextField(5); ageField.setFont(fieldFont);
    dobField = new JTextField(10); dobField.setFont(fieldFont);
    bloodGroupCombo = new JComboBox<>(new String[]{"A+","A-","B+","B-","AB+","AB-","O+","O-"}); bloodGroupCombo.setFont(fieldFont);
    contactField = new JTextField(12); contactField.setFont(fieldFont);
    emailField = new JTextField(20); emailField.setFont(fieldFont);
    addressArea = new JTextArea(4, 20); addressArea.setFont(fieldFont); addressArea.setLineWrap(true); addressArea.setWrapStyleWord(true);
    JScrollPane addressScroll = new JScrollPane(addressArea);
    aadhaarField = new JTextField(15); aadhaarField.setFont(fieldFont);
    guardianField = new JTextField(15); guardianField.setFont(fieldFont);
    insuranceField = new JTextField(20); insuranceField.setFont(fieldFont);

    imageLabel = new JLabel("No Image", SwingConstants.CENTER);
    imageLabel.setPreferredSize(new Dimension(150, 150));
    imageLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
    imageLabel.setOpaque(true);
    imageLabel.setBackground(new Color(230, 230, 230));
    imageLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
    imageLabel.setForeground(Color.DARK_GRAY);

    // Buttons
    JButton searchBtn = createStyledButton("Search", new Color(33, 150, 243));
    JButton browseBtn = createStyledButton("Browse", new Color(100, 181, 246));
    JButton updateBtn = createStyledButton("Update", new Color(76, 175, 80));
    JButton deleteBtn = createStyledButton("Delete", new Color(244, 67, 54));
    JButton backBtn = createStyledButton("Back", new Color(117, 117, 117));

    int row = 0;

    // Patient ID + Search
    gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
    formPanel.add(new JLabel("Patient ID:"), gbc);
    gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
    formPanel.add(patientIdField, gbc);
    gbc.gridx = 2; gbc.anchor = GridBagConstraints.WEST;
    formPanel.add(searchBtn, gbc);
    row++;

    // Name
    gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
    formPanel.add(new JLabel("Name:"), gbc);
    gbc.gridx = 1; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.WEST;
    formPanel.add(nameField, gbc); gbc.gridwidth = 1; row++;

    // Gender + Age
    gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
    formPanel.add(new JLabel("Gender:"), gbc);
    gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
    formPanel.add(genderCombo, gbc);
    gbc.gridx = 2;
    formPanel.add(new JLabel("Age:"), gbc);
    gbc.gridx = 3; gbc.anchor = GridBagConstraints.WEST;
    formPanel.add(ageField, gbc);
    row++;

    // DOB + Blood Group
    gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
    formPanel.add(new JLabel("DOB:"), gbc);
    gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
    formPanel.add(dobField, gbc);
    gbc.gridx = 2; gbc.anchor = GridBagConstraints.EAST;
    formPanel.add(new JLabel("Blood Group:"), gbc);
    gbc.gridx = 3; gbc.anchor = GridBagConstraints.WEST;
    formPanel.add(bloodGroupCombo, gbc);
    row++;

    // Contact + Email
    gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
    formPanel.add(new JLabel("Contact:"), gbc);
    gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
    formPanel.add(contactField, gbc);
    gbc.gridx = 2; gbc.anchor = GridBagConstraints.EAST;
    formPanel.add(new JLabel("Email:"), gbc);
    gbc.gridx = 3; gbc.anchor = GridBagConstraints.WEST;
    formPanel.add(emailField, gbc);
    row++;

    // Address
    gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.NORTHEAST;
    formPanel.add(new JLabel("Address:"), gbc);
    gbc.gridx = 1; gbc.gridwidth = 3; gbc.anchor = GridBagConstraints.WEST;
    formPanel.add(addressScroll, gbc); gbc.gridwidth = 1; row++;

    // Aadhaar + Insurance
    gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
    formPanel.add(new JLabel("Aadhaar:"), gbc);
    gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
    formPanel.add(aadhaarField, gbc);
    gbc.gridx = 2; gbc.anchor = GridBagConstraints.EAST;
    formPanel.add(new JLabel("Insurance:"), gbc);
    gbc.gridx = 3; gbc.anchor = GridBagConstraints.WEST;
    formPanel.add(insuranceField, gbc);
    row++;
    

// Guardian
gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
formPanel.add(new JLabel("Guardian:"), gbc);
gbc.gridx = 1; gbc.gridwidth = 3; gbc.anchor = GridBagConstraints.WEST;
formPanel.add(guardianField, gbc);
gbc.gridwidth = 1; row++;


    // Image + Browse
    gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.NORTHEAST;
    formPanel.add(new JLabel("Image:"), gbc);
    gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
    formPanel.add(imageLabel, gbc);
    gbc.gridx = 2; gbc.anchor = GridBagConstraints.WEST;
    formPanel.add(browseBtn, gbc);
    row++;

    // Buttons panel
    gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 4; gbc.anchor = GridBagConstraints.CENTER;
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
    buttonPanel.setBackground(formPanel.getBackground());
    buttonPanel.add(updateBtn);
    buttonPanel.add(deleteBtn);
    buttonPanel.add(backBtn);
    formPanel.add(buttonPanel, gbc);

    mainPanel.add(formPanel, BorderLayout.CENTER);
    add(mainPanel);

    // Actions
    browseBtn.addActionListener(e -> browseImage());
    searchBtn.addActionListener(e -> searchPatient());
    updateBtn.addActionListener(e -> updatePatient());
    deleteBtn.addActionListener(e -> deletePatient());
    backBtn.addActionListener(e -> { dispose(); new PatientMainDashboard().setVisible(true); });
}
    private JButton createStyledButton(String text, Color bgColor) {
    JButton button = new JButton(text);
    button.setFocusPainted(false);
    button.setBackground(bgColor);
    button.setForeground(Color.WHITE);
    button.setFont(new Font("Segoe UI", Font.BOLD, 14));
    button.setPreferredSize(new Dimension(120, 40));
    button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
    return button;
}

    




   




    

    private void browseImage() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedImageFile = chooser.getSelectedFile();
            ImageIcon icon = new ImageIcon(new ImageIcon(selectedImageFile.getAbsolutePath())
                    .getImage().getScaledInstance(imageLabel.getWidth(), imageLabel.getHeight(), Image.SCALE_SMOOTH));
            imageLabel.setIcon(icon);
            imageLabel.setText(null);
        }
    }

    private void searchPatient() {
        String pid = patientIdField.getText().trim();
        if (pid.isEmpty()) { JOptionPane.showMessageDialog(this, "Enter Patient ID."); return; }
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM patients WHERE patient_id=?");
            ps.setString(1, pid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                nameField.setText(rs.getString("name"));
                genderCombo.setSelectedItem(rs.getString("gender"));
                ageField.setText(rs.getString("age"));
                dobField.setText(rs.getString("dob"));
                bloodGroupCombo.setSelectedItem(rs.getString("blood_group"));
                contactField.setText(rs.getString("contact"));
                emailField.setText(rs.getString("email"));
                addressArea.setText(rs.getString("address"));
                aadhaarField.setText(rs.getString("aadhaar"));
                guardianField.setText(rs.getString("guardian"));
                insuranceField.setText(rs.getString("insurance"));
                currentImagePath = rs.getString("image_path");
                if (currentImagePath != null && !currentImagePath.isEmpty()) {
                    ImageIcon icon = new ImageIcon(new ImageIcon(currentImagePath)
                            .getImage().getScaledInstance(imageLabel.getWidth(), imageLabel.getHeight(), Image.SCALE_SMOOTH));
                    imageLabel.setIcon(icon); imageLabel.setText(null);
                } else { imageLabel.setIcon(null); imageLabel.setText("No Image"); }
                selectedImageFile = null;
            } else {
                JOptionPane.showMessageDialog(this, "Patient not found.");
                clearFields();
            }
        } catch (Exception ex) { ex.printStackTrace(); JOptionPane.showMessageDialog(this, "Error: "+ex.getMessage()); }
    }

    private void updatePatient() {
        if(!validateFields()){ return;}
        String pid = patientIdField.getText().trim();
        if (pid.isEmpty()) { JOptionPane.showMessageDialog(this, "Patient ID required."); return; }
        try { int age = Integer.parseInt(ageField.getText().trim()); if(age<0) throw new NumberFormatException(); }
        catch (NumberFormatException e) { JOptionPane.showMessageDialog(this, "Enter valid age."); return; }

        String imagePathToSave = currentImagePath;
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            if (selectedImageFile != null) {
                String imgDir = "patient_images"; new File(imgDir).mkdir();
                String ext = selectedImageFile.getName().substring(selectedImageFile.getName().lastIndexOf("."));
                String dest = imgDir + "/" + pid + ext;
                Files.copy(selectedImageFile.toPath(), Paths.get(dest), StandardCopyOption.REPLACE_EXISTING);
                imagePathToSave = dest;
            }

            PreparedStatement ps = conn.prepareStatement(
                "UPDATE patients SET name=?, gender=?, age=?, dob=?, blood_group=?, contact=?, email=?, address=?, aadhaar=?, guardian=?, insurance=?, image_path=? WHERE patient_id=?"
            );
            ps.setString(1, nameField.getText().trim());
            ps.setString(2, (String) genderCombo.getSelectedItem());
            ps.setInt(3, Integer.parseInt(ageField.getText().trim()));
            ps.setString(4, dobField.getText().trim());
            ps.setString(5, (String) bloodGroupCombo.getSelectedItem());
            ps.setString(6, contactField.getText().trim());
            ps.setString(7, emailField.getText().trim());
            ps.setString(8, addressArea.getText().trim());
            ps.setString(9, aadhaarField.getText().trim());
            ps.setString(10, guardianField.getText().trim());
            ps.setString(11, insuranceField.getText().trim());
            ps.setString(12, imagePathToSave);
            ps.setString(13, pid);

            if (ps.executeUpdate() > 0) JOptionPane.showMessageDialog(this, "Patient updated successfully.");
            else JOptionPane.showMessageDialog(this, "Update failed.");
        } catch (Exception e) { e.printStackTrace(); JOptionPane.showMessageDialog(this, "Error: "+e.getMessage()); }
    }

    private void deletePatient() {
        String pid = patientIdField.getText().trim();
        if (pid.isEmpty()) { JOptionPane.showMessageDialog(this, "Patient ID required."); return; }
        if (JOptionPane.showConfirmDialog(this, "Delete patient "+pid+"?", "Confirm Delete", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM patients WHERE patient_id=?");
            ps.setString(1, pid);
            if(ps.executeUpdate()>0) { JOptionPane.showMessageDialog(this, "Patient deleted."); clearFields(); }
            else JOptionPane.showMessageDialog(this, "Delete failed.");
        } catch (Exception e) { e.printStackTrace(); JOptionPane.showMessageDialog(this, "Error: "+e.getMessage()); }
    }
    private boolean validateFields() { 
    // Full Name: not empty, at least 3 chars 
    String name = nameField.getText().trim(); 
    if (name.isEmpty() || name.length() < 3) { 
        JOptionPane.showMessageDialog(this, "Full Name must be at least 3 characters", "Validation Error", JOptionPane.ERROR_MESSAGE); 
        nameField.requestFocus(); 
        return false; 
    } 
 
    // Age: numeric, 1-120 
    String ageText = ageField.getText().trim(); 
    if (!ageText.matches("\\d+")) { 
        JOptionPane.showMessageDialog(this, "Age must be a number", "Validation Error", 
JOptionPane.ERROR_MESSAGE); 
        ageField.requestFocus(); 
        return false; 
    } 
    int age = Integer.parseInt(ageText); 
    if (age <= 0 || age > 120) { 
        JOptionPane.showMessageDialog(this, "Age must be between 1 and 120", "Validation Error", 
JOptionPane.ERROR_MESSAGE); 
        ageField.requestFocus(); 
        return false; 
    } 
 
    // DOB: simple format check 
    String dob = dobField.getText().trim(); 
    if (!dob.matches("\\d{4}-\\d{2}-\\d{2}")) { 
        JOptionPane.showMessageDialog(this, "DOB must be in YYYY-MM-DD format", "Validation Error", JOptionPane.ERROR_MESSAGE); 
        dobField.requestFocus(); 
        return false; 
    } 
 
    // Contact: 10 digits 
    String contact = contactField.getText().trim(); 
    if (!contact.matches("\\d{10}")) { 
        JOptionPane.showMessageDialog(this, "Contact must be a 10-digit number", "Validation Error", 
JOptionPane.ERROR_MESSAGE); 
        contactField.requestFocus(); 
        return false; 
    } 
 
    // Email: basic pattern 
    String email = emailField.getText().trim(); 
    if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) { 
        JOptionPane.showMessageDialog(this, "Enter a valid Email address", "Validation Error", 
JOptionPane.ERROR_MESSAGE); 
        emailField.requestFocus(); 
        return false; 
    } 
 
    // Aadhaar: 12 digits 
    String aadhaar = aadhaarField.getText().trim(); 
    if (!aadhaar.matches("\\d{12}")) { 
        JOptionPane.showMessageDialog(this, "Aadhaar must be a 12-digit number", "Validation Error", 
JOptionPane.ERROR_MESSAGE); 
        aadhaarField.requestFocus(); 
        return false; 
    } 
 
    // Guardian Name: not empty 
    if (guardianField.getText().trim().isEmpty()) { 
        JOptionPane.showMessageDialog(this, "Guardian Name is required", "Validation Error", 
JOptionPane.ERROR_MESSAGE); 
        guardianField.requestFocus(); 
        return false; 
    } 
 
    // Insurance Info: optional but max 50 chars 
    String insurance = insuranceField.getText().trim(); 
    if (!insurance.isEmpty() && insurance.length() > 50) { 
        JOptionPane.showMessageDialog(this, "Insurance info must be less than 50 characters", 
"Validation Error", JOptionPane.ERROR_MESSAGE); 
        insuranceField.requestFocus(); 
        return false; 
    } 
 
    // Address: at least 5 chars 
    if (addressArea.getText().trim().length() < 5) { 
        JOptionPane.showMessageDialog(this, "Address must be at least 5 characters long", "Validation Error", JOptionPane.ERROR_MESSAGE); 
        addressArea.requestFocus(); 
        return false; 
    } 
   if (selectedImageFile != null) {
        String fileName = selectedImageFile.getName().toLowerCase();
        if (!(fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png"))) {
            JOptionPane.showMessageDialog(this, "Only JPG, JPEG, or PNG images are allowed!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        long maxSize = 2 * 1024 * 1024; // 2MB
        if (selectedImageFile.length() > maxSize) {
            JOptionPane.showMessageDialog(this, "Image size must be less than 2MB!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    } else if (currentImagePath == null || currentImagePath.isEmpty()) {
        // only required if no image exists at all
        JOptionPane.showMessageDialog(this, "Please upload a patient photo!", "Validation Error", JOptionPane.ERROR_MESSAGE);
        return false;
    }

    return true;
   

}

    private void clearFields() {
        nameField.setText(""); genderCombo.setSelectedIndex(0); ageField.setText("");
        dobField.setText(""); bloodGroupCombo.setSelectedIndex(0); contactField.setText("");
        emailField.setText(""); addressArea.setText(""); aadhaarField.setText("");
        guardianField.setText(""); insuranceField.setText("");
        imageLabel.setIcon(null); imageLabel.setText("No Image");
        selectedImageFile = null; currentImagePath = null;
    }

    public static void main(String[] args) {
        try { Class.forName("com.mysql.cj.jdbc.Driver"); } 
        catch (ClassNotFoundException e) { System.err.println("MySQL JDBC driver not found."); }
        SwingUtilities.invokeLater(PatientUpdateDeleteApp::new);
    }
}

