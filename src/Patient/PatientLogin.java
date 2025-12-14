/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Patient;

import homepage.HospitalHomePage;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.Random;

public class PatientLogin extends JFrame {
    private JTextField emailField, otpField;
    private JPasswordField passwordField;
    private JButton sendOtpBtn, loginBtn, forgotPassBtn, verifyOtpBtn, backBtn;
    private String generatedOTP, currentEmail;
    private boolean isLoginOTPVerified = false;

    public PatientLogin() {
        setTitle("Patient Login Portal");
        setSize(950, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        // Main panel with soft blue background
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(225, 245, 254));

        // Content panel white card with padding and border
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setPreferredSize(new Dimension(500, 600));
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
            BorderFactory.createEmptyBorder(20, 30, 20, 30)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 10, 12, 10);
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;

        JLabel title = new JLabel("Patient Login");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(33, 150, 243));
        contentPanel.add(title, gbc);

        // Email label and field
        gbc.gridy++; gbc.gridwidth = 1;
        contentPanel.add(new JLabel("Email:"), gbc);

        emailField = new JTextField(20);
        gbc.gridx = 1;
        contentPanel.add(emailField, gbc);

        // Send OTP button
        gbc.gridy++; gbc.gridx = 1;
        sendOtpBtn = createStyledButton("Send OTP", new Color(30, 136, 229));
        sendOtpBtn.addActionListener(e -> sendLoginOTP(emailField.getText()));
        contentPanel.add(sendOtpBtn, gbc);

        // OTP label and field
        gbc.gridy++; gbc.gridx = 0;
        contentPanel.add(new JLabel("OTP:"), gbc);

        otpField = new JTextField(20);
        gbc.gridx = 1;
        contentPanel.add(otpField, gbc);

        // Verify OTP button
        gbc.gridy++; gbc.gridx = 1;
        verifyOtpBtn = createStyledButton("Verify OTP", new Color(67, 160, 71));
        verifyOtpBtn.addActionListener(e -> verifyLoginOTP());
        contentPanel.add(verifyOtpBtn, gbc);

        // Password label and field
        gbc.gridy++; gbc.gridx = 0;
        contentPanel.add(new JLabel("Password:"), gbc);

        passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        contentPanel.add(passwordField, gbc);

        // Login button
        gbc.gridy++; gbc.gridx = 1;
        loginBtn = createStyledButton("Login", new Color(0, 172, 193));
        loginBtn.addActionListener(e -> loginUser());
        loginBtn.setEnabled(false);  // Disabled until OTP verified
        contentPanel.add(loginBtn, gbc);

        // Forgot password link styled as button
        gbc.gridy++;
        forgotPassBtn = new JButton("Forgot Password?");
        forgotPassBtn.setForeground(new Color(33, 150, 243));
        forgotPassBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        forgotPassBtn.setContentAreaFilled(false);
        forgotPassBtn.setBorderPainted(false);
        forgotPassBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        forgotPassBtn.addActionListener(e -> showOtpForReset());
        contentPanel.add(forgotPassBtn, gbc);

        // Back button
        gbc.gridy++;
        backBtn = createStyledButton("Back", new Color(120, 144, 156));
        backBtn.addActionListener(e -> {
            dispose();
            new HospitalHomePage().setVisible(true);
        });
        contentPanel.add(backBtn, gbc);

        mainPanel.add(contentPanel);
        add(mainPanel);
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bgColor.darker(), 1),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(bgColor.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(bgColor);
            }
        });

        return btn;
    }

    // Generate and show OTP in dialog (no email)
    private void sendLoginOTP(String email) {
        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter your email first.");
            return;
        }
        try (Connection con = getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM patients WHERE email=?");
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                generatedOTP = String.valueOf(new Random().nextInt(899999) + 100000);
                currentEmail = email;
                JOptionPane.showMessageDialog(this, "Your OTP is: " + generatedOTP);
            } else {
                JOptionPane.showMessageDialog(this, "Email not registered.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error. Please try again.");
        }
    }

    // Verify OTP entered for login
    private void verifyLoginOTP() {
        if (otpField.getText().trim().equals(generatedOTP)) {
            isLoginOTPVerified = true;
            JOptionPane.showMessageDialog(this, "OTP verified. You can now login.");
            loginBtn.setEnabled(true);
            sendOtpBtn.setEnabled(false);
            verifyOtpBtn.setEnabled(false);
            emailField.setEditable(false);
            otpField.setEditable(false);
        } else {
            JOptionPane.showMessageDialog(this, "Incorrect OTP.");
        }
    }

    // Perform login after OTP verified
    private void loginUser() {
        if (!isLoginOTPVerified) {
            JOptionPane.showMessageDialog(this, "Please verify OTP first.");
            return;
        }

        String email = emailField.getText().trim();
        String password = String.valueOf(passwordField.getPassword());

        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter password.");
            return;
        }

        try (Connection con = getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM patients WHERE email=? AND password=?");
            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String patientId = rs.getString("patient_id");
                JOptionPane.showMessageDialog(this, "Login successful! Welcome " + patientId);
                dispose();
                new PatientDashboard(patientId).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid email or password.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error. Please try again.");
        }
    }

    // Improved Forgot password flow using a custom modal dialog
    private void showOtpForReset() {
        String email = JOptionPane.showInputDialog(this, "Enter your registered email:");
        if (email == null || email.trim().isEmpty()) return;

        try (Connection con = getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM patients WHERE email=?");
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // Open the ForgotPasswordDialog instead of multiple input dialogs
                new ForgotPasswordDialog(this, email);
            } else {
                JOptionPane.showMessageDialog(this, "Email not registered.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error. Please try again.");
        }
    }

    // MySQL connection method
    private Connection getConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital_db", "root", "");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PatientLogin().setVisible(true));
    }

    // Inner dialog class for Forgot Password flow
    private class ForgotPasswordDialog extends JDialog {
        private JTextField otpField;
        private JPasswordField newPassField, confirmPassField;
        private JButton verifyOtpBtn, changePassBtn, cancelBtn;
        private String tempOTP;

        public ForgotPasswordDialog(JFrame parent, String email) {
            super(parent, "Forgot Password", true);
            setSize(400, 350);
            setLocationRelativeTo(parent);
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 15, 10, 15);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            // Generate OTP
            tempOTP = generateOTP();

            // Label: Info
            gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
            JLabel infoLabel = new JLabel("<html>OTP has been generated and shown below.<br>Enter OTP and set new password.</html>");
            add(infoLabel, gbc);

            // Show OTP in a non-editable field
            gbc.gridy++;
            JTextField otpDisplay = new JTextField(tempOTP);
            otpDisplay.setEditable(false);
            otpDisplay.setFont(new Font("Segoe UI", Font.BOLD, 18));
            otpDisplay.setHorizontalAlignment(JTextField.CENTER);
            add(otpDisplay, gbc);

            // OTP input
            gbc.gridy++; gbc.gridwidth = 1;
            add(new JLabel("Enter OTP:"), gbc);
            otpField = new JTextField();
            gbc.gridx = 1;
            add(otpField, gbc);

            // New password
            gbc.gridx = 0; gbc.gridy++;
            add(new JLabel("New Password:"), gbc);
            newPassField = new JPasswordField();
            gbc.gridx = 1;
            add(newPassField, gbc);

            // Confirm password
            gbc.gridx = 0; gbc.gridy++;
            add(new JLabel("Confirm Password:"), gbc);
            confirmPassField = new JPasswordField();
            gbc.gridx = 1;
            add(confirmPassField, gbc);

            // Buttons panel
            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
            verifyOtpBtn = new JButton("Verify OTP");
            changePassBtn = new JButton("Change Password");
            cancelBtn = new JButton("Cancel");
            changePassBtn.setEnabled(false); // Enabled after OTP verified

            btnPanel.add(verifyOtpBtn);
            btnPanel.add(changePassBtn);
            btnPanel.add(cancelBtn);

            gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2;
            add(btnPanel, gbc);

            // Button actions
            verifyOtpBtn.addActionListener(e -> {
                String enteredOtp = otpField.getText().trim();
                if (enteredOtp.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter the OTP.");
                    return;
                }
                if (enteredOtp.equals(tempOTP)) {
                    JOptionPane.showMessageDialog(this, "OTP verified. Now you can change your password.");
                    verifyOtpBtn.setEnabled(false);
                    otpField.setEditable(false);
                    changePassBtn.setEnabled(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Incorrect OTP.");
                }
            });

            changePassBtn.addActionListener(e -> {
                String newPass = String.valueOf(newPassField.getPassword());
                String confirmPass = String.valueOf(confirmPassField.getPassword());
                if (newPass.isEmpty() || confirmPass.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please fill both password fields.");
                    return;
                }
                if (!newPass.equals(confirmPass)) {
                    JOptionPane.showMessageDialog(this, "Passwords do not match.");
                    return;
                }

                try (Connection con = getConnection()) {
                    PreparedStatement ps = con.prepareStatement("UPDATE patients SET password=? WHERE email=?");
                    ps.setString(1, newPass);
                    ps.setString(2, email);
                    int updated = ps.executeUpdate();
                    if (updated > 0) {
                        JOptionPane.showMessageDialog(this, "Password updated successfully.");
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(this, "Password update failed.");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Database error. Please try again.");
                }
            });

            cancelBtn.addActionListener(e -> dispose());

            setVisible(true);
        }

        // Generate OTP helper
        private String generateOTP() {
            return String.valueOf(new Random().nextInt(899999) + 100000);
        }
    }
}
