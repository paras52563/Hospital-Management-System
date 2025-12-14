/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Admin;



import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.*;
import java.awt.Font;
//import java.awt.Image;
import java.util.*;
import java.sql.Date;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;


import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class PatientRegistrationApp extends JFrame {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/hospital_db";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";

    private JTextField patientIdField, nameField, ageField, dobField, contactField, emailField, aadhaarField, guardianField, insuranceField;
    private JComboBox<String> genderCombo, bloodGroupCombo;
    private JTextArea addressArea;
    private JLabel imageLabel;
    private File selectedImageFile;
    private String generatedOtp;
    private boolean otpVerified = false;
    private String patientPassword; // stores password internally


    public PatientRegistrationApp() {
        setTitle("Patient Registration");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(new Dimension(900, 820));
        setLocationRelativeTo(null);
        setLightThemeUI();

        initComponents();
        generatePatientId();
        setVisible(true);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(245, 248, 250));

        JLabel title = new JLabel("Patient Registration Form", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI Semibold", Font.BOLD, 32));
        title.setForeground(new Color(33, 37, 41));
        title.setBorder(new EmptyBorder(10, 0, 25, 0));
        mainPanel.add(title, BorderLayout.NORTH);

        JPanel splitPanel = new JPanel(new GridBagLayout());
        splitPanel.setBackground(new Color(245, 248, 250));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        JPanel leftPanel = createLeftPanel();
        gbc.gridx = 0;
        gbc.weightx = 0.6;
        splitPanel.add(leftPanel, gbc);

        JPanel rightPanel = createRightPanel();
        gbc.gridx = 1;
        gbc.weightx = 0.4;
        splitPanel.add(rightPanel, gbc);

        mainPanel.add(splitPanel, BorderLayout.CENTER);
        setContentPane(mainPanel);
        pack();
    }

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new RoundedBorder(12, new Color(200, 210, 218)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 30, 12, 30);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        row = addField(panel, gbc, row, "Patient ID:", patientIdField = createDisabledField());
        row = addField(panel, gbc, row, "Full Name:", nameField = new JTextField());
        row = addField(panel, gbc, row, "Gender:", genderCombo = new JComboBox<>(new String[]{"Male", "Female", "Other"}));
        row = addField(panel, gbc, row, "Age:", ageField = new JTextField());
        row = addField(panel, gbc, row, "DOB (YYYY-MM-DD):", dobField = new JTextField());
        row = addField(panel, gbc, row, "Blood Group:", bloodGroupCombo = new JComboBox<>(new String[]{"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"}));
        row = addField(panel, gbc, row, "Contact:", contactField = new JTextField());
        row = addField(panel, gbc, row, "Email:", emailField = new JTextField());

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel addressLabel = new JLabel("Address:");
        addressLabel.setForeground(new Color(70, 70, 70));
        addressLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        panel.add(addressLabel, gbc);

        gbc.gridy = row++;
        gbc.weighty = 0.3;
        gbc.fill = GridBagConstraints.BOTH;
        addressArea = new JTextArea(4, 20);
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        addressArea.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        addressArea.setBackground(Color.WHITE);
        addressArea.setForeground(new Color(40, 40, 40));
        addressArea.setCaretColor(new Color(40, 40, 40));
        JScrollPane scroll = new JScrollPane(addressArea);
        scroll.setBorder(new RoundedBorder(8, new Color(200, 210, 218)));
        panel.add(scroll, gbc);

        return panel;
    }

    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new RoundedBorder(12, new Color(200, 210, 218)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 30, 15, 30);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        row = addField(panel, gbc, row, "Aadhaar No:", aadhaarField = new JTextField());
        row = addField(panel, gbc, row, "Guardian Name:", guardianField = new JTextField());
        row = addField(panel, gbc, row, "Insurance Info:", insuranceField = new JTextField());

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        JLabel imageLabelTitle = new JLabel("Patient Photo:");
        imageLabelTitle.setForeground(new Color(70, 70, 70));
        imageLabelTitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        panel.add(imageLabelTitle, gbc);

        imageLabel = new JLabel();
        imageLabel.setPreferredSize(new Dimension(180, 180));
        imageLabel.setBorder(new RoundedBorder(8, new Color(180, 190, 210)));
        imageLabel.setOpaque(true);
        imageLabel.setBackground(Color.WHITE);
        gbc.gridx = 1;
        panel.add(imageLabel, gbc);

        gbc.gridy = ++row;
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.NONE;
        JButton browseBtn = new JButton("Upload Image");
        styleButton(browseBtn, new Color(0, 123, 255));
        browseBtn.setFont(new Font("Segoe UI Semibold", Font.BOLD, 15));
        browseBtn.setPreferredSize(new Dimension(160, 42));
        browseBtn.addActionListener(e -> chooseImage());
        panel.add(browseBtn, gbc);

        gbc.gridy = ++row;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(30, 30, 10, 30);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 35, 0));
        buttonPanel.setBackground(Color.WHITE);

        JButton registerBtn = new JButton("Register Patient");
        styleButton(registerBtn, new Color(40, 167, 69));
        registerBtn.setPreferredSize(new Dimension(200, 52));
        registerBtn.addActionListener(e -> registerPatientOffline());

        JButton backBtn = new JButton("Back");
        styleButton(backBtn, new Color(220, 53, 69));
        backBtn.setPreferredSize(new Dimension(120, 52));
        backBtn.addActionListener(e -> dispose());

        buttonPanel.add(registerBtn);
        buttonPanel.add(backBtn);
        panel.add(buttonPanel, gbc);

        return panel;
    }
    

    private int addField(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent input) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0;
        JLabel lbl = new JLabel(label);
        lbl.setForeground(new Color(70, 70, 70));
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        input.setPreferredSize(new Dimension(240, 32));
        input.setBackground(Color.WHITE);
        input.setForeground(new Color(50, 50, 50));
        input.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        if (input instanceof JTextField) {
            ((JTextField) input).setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 210, 218)),
                    BorderFactory.createEmptyBorder(6, 8, 6, 8)
            ));
        } else if (input instanceof JComboBox) {
            input.setBorder(BorderFactory.createLineBorder(new Color(200, 210, 218)));
        }
        panel.add(input, gbc);

        return row + 1;
    }

    private void styleButton(JButton btn, Color color) {
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new RoundedBorder(12, color.darker()));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(color.brighter());
            }

            public void mouseExited(MouseEvent e) {
                btn.setBackground(color);
            }
        });
    }

    private JTextField createDisabledField() {
        JTextField field = new JTextField();
        field.setEditable(false);
        field.setBackground(new Color(240, 240, 240));
        field.setForeground(new Color(130, 130, 130));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        field.setPreferredSize(new Dimension(240, 32));
        field.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        return field;
    }

    private void chooseImage() {
    JFileChooser chooser = new JFileChooser();
    if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
        selectedImageFile = chooser.getSelectedFile();
        java.awt.Image img = new ImageIcon(selectedImageFile.getAbsolutePath())
                .getImage()
                .getScaledInstance(180, 180, java.awt.Image.SCALE_SMOOTH);
        imageLabel.setIcon(new ImageIcon(img));
        imageLabel.setPreferredSize(new Dimension(180, 180));
    }
}

    private void generatePatientId() {
        String id = "PAT" + (10000 + new Random().nextInt(90000));
        patientIdField.setText(id);
    }

    private void registerPatientOffline() {
        if(!validateFields()){
            return;
        }
       if (!otpVerified) {
        generatedOtp = String.format("%06d", new Random().nextInt(999999));
        JOptionPane.showMessageDialog(this,"Generated OTP (for demo, send via email/SMS in real app): " + generatedOtp);

        // Ask user to enter OTP
        String enteredOtp = JOptionPane.showInputDialog(this, "Enter the OTP sent to your email/phone:");
        if (enteredOtp == null || !enteredOtp.equals(generatedOtp)) {
            JOptionPane.showMessageDialog(this, "OTP verification failed!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        otpVerified = true; // OTP verified
    }

    // Step 2: OTP verified → ask user to set password
    String userPassword = JOptionPane.showInputDialog(this, "Enter a password for your account:");
    if (userPassword == null || userPassword.trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Password cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Step 3: Insert patient into DB
    if (insertPatientData(userPassword.trim())) {
        JOptionPane.showMessageDialog(this, "Patient registered successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        generatePDFSlip(); // include password in PDF if needed
        clearForm();
        generatePatientId();
        otpVerified = false; // reset for next patient
    } else {
        JOptionPane.showMessageDialog(this, "Failed to register patient!", "Error", JOptionPane.ERROR_MESSAGE);
    }

        
    }

    private void generatePDFSlip() {
        String pdfName = "Patient_" + patientIdField.getText() + ".pdf";
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(pdfName));
            document.open();

            // Title
           com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(
    com.itextpdf.text.Font.FontFamily.HELVETICA, 20, com.itextpdf.text.Font.BOLD
);


            Paragraph title = new Paragraph("Patient Registration Slip\n\n", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            // Patient details table
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);

            addRow(table, "Patient ID", patientIdField.getText());
            addRow(table, "Name", nameField.getText());
            addRow(table, "Gender", (String) genderCombo.getSelectedItem());
            addRow(table, "Age", ageField.getText());
            addRow(table, "DOB", dobField.getText());
            addRow(table, "Blood Group", (String) bloodGroupCombo.getSelectedItem());
            addRow(table, "Contact", contactField.getText());
            addRow(table, "Email", emailField.getText());
            addRow(table, "Aadhaar", aadhaarField.getText());
            addRow(table, "Guardian", guardianField.getText());
            addRow(table, "Insurance", insuranceField.getText());
            addRow(table, "Address", addressArea.getText());

            document.add(table);

            // Add photo if available
            if (selectedImageFile != null) {
                document.add(new Paragraph("\nPatient Photo:"));
                Image img = Image.getInstance(selectedImageFile.getAbsolutePath());
                img.scaleToFit(150, 150);
                document.add(img);
            }

            document.close();
            JOptionPane.showMessageDialog(this, "PDF Slip generated: " + pdfName, "PDF Created", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "PDF Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
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

    private boolean insertPatientData(String password) {
    String patientId = patientIdField.getText();
    String name = formatNameProper(nameField.getText());
    String gender = (String) genderCombo.getSelectedItem();
    String age = ageField.getText();
    String dob = dobField.getText();
    String bloodGroup = (String) bloodGroupCombo.getSelectedItem();
    String contact = contactField.getText();
    String email = emailField.getText().toLowerCase();
    String aadhaar = aadhaarField.getText();
    String guardian = guardianField.getText();
    String insurance = insuranceField.getText();
    String address = addressArea.getText();
    String imagePath = selectedImageFile != null ? selectedImageFile.getAbsolutePath() : null;

    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
        String sql = "INSERT INTO patients "
                   + "(patient_id, name, gender, age, dob, blood_group, contact, email, aadhaar, guardian, insurance, address, password, image_path) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, patientId);
        ps.setString(2, name);
        ps.setString(3, gender);
        ps.setString(4, age);
        ps.setString(5, dob);
        ps.setString(6, bloodGroup);
        ps.setString(7, contact);
        ps.setString(8, email);
        ps.setString(9, aadhaar);
        ps.setString(10, guardian);
        ps.setString(11, insurance);
        ps.setString(12, address);
        ps.setString(13, password);
        ps.setString(14, imagePath); // store image path

        int inserted = ps.executeUpdate();
        return inserted > 0;
    } catch (SQLException ex) {
        ex.printStackTrace();
        return false;
    }
}


    private void addRow(PdfPTable table, String key, String value) {
        table.addCell(new PdfPCell(new Phrase(key)));
        table.addCell(new PdfPCell(new Phrase(value)));
    }

   

    private void clearForm() {
        nameField.setText("");
        genderCombo.setSelectedIndex(0);
        ageField.setText("");
        dobField.setText("");
        bloodGroupCombo.setSelectedIndex(0);
        contactField.setText("");
        emailField.setText("");
        aadhaarField.setText("");
        guardianField.setText("");
        insuranceField.setText("");
        addressArea.setText("");
        imageLabel.setIcon(null);
        selectedImageFile = null;
    }

    private void setLightThemeUI() {}

    private static class RoundedBorder extends AbstractBorder {
        private final int radius;
        private final Color borderColor;
        RoundedBorder(int radius, Color borderColor) {
            this.radius = radius; this.borderColor = borderColor;
        }
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setColor(borderColor);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRoundRect(x+1, y+1, w-3, h-3, radius, radius);
            g2d.dispose();
        }
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
        JOptionPane.showMessageDialog(this, "Age must be a number", "Validation Error", JOptionPane.ERROR_MESSAGE);
        ageField.requestFocus();
        return false;
    }
    int age = Integer.parseInt(ageText);
    if (age <= 0 || age > 120) {
        JOptionPane.showMessageDialog(this, "Age must be between 1 and 120", "Validation Error", JOptionPane.ERROR_MESSAGE);
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
        JOptionPane.showMessageDialog(this, "Contact must be a 10-digit number", "Validation Error", JOptionPane.ERROR_MESSAGE);
        contactField.requestFocus();
        return false;
    }

    // Email: basic pattern
    String email = emailField.getText().trim();
    if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
        JOptionPane.showMessageDialog(this, "Enter a valid Email address", "Validation Error", JOptionPane.ERROR_MESSAGE);
        emailField.requestFocus();
        return false;
    }

    // Aadhaar: 12 digits
    String aadhaar = aadhaarField.getText().trim();
    if (!aadhaar.matches("\\d{12}")) {
        JOptionPane.showMessageDialog(this, "Aadhaar must be a 12-digit number", "Validation Error", JOptionPane.ERROR_MESSAGE);
        aadhaarField.requestFocus();
        return false;
    }

    // Guardian Name: not empty
    if (guardianField.getText().trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Guardian Name is required", "Validation Error", JOptionPane.ERROR_MESSAGE);
        guardianField.requestFocus();
        return false;
    }

    // Insurance Info: optional but max 50 chars
    String insurance = insuranceField.getText().trim();
    if (!insurance.isEmpty() && insurance.length() > 50) {
        JOptionPane.showMessageDialog(this, "Insurance info must be less than 50 characters", "Validation Error", JOptionPane.ERROR_MESSAGE);
        insuranceField.requestFocus();
        return false;
    }

    // Address: at least 5 chars
    if (addressArea.getText().trim().length() < 5) {
        JOptionPane.showMessageDialog(this, "Address must be at least 5 characters long", "Validation Error", JOptionPane.ERROR_MESSAGE);
        addressArea.requestFocus();
        return false;
    }
     if (selectedImageFile == null) {
        JOptionPane.showMessageDialog(this, "Please upload a patient photo!", "Validation Error", JOptionPane.ERROR_MESSAGE);
        return false;
    }
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
    return true;

    // Photo: must be uploaded
  
   
}


    public static void main(String[] args) {
        SwingUtilities.invokeLater(PatientRegistrationApp::new);
    }
}


