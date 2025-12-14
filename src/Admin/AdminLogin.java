/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Admin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Date;
import java.util.Random;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class AdminLogin extends JFrame {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/hospital_db";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";

    private JTextField emailField, captchaField, otpField;
    private JPasswordField passwordField;
    private JButton loginBtn, verifyOtpBtn, refreshCaptchaBtn, resendOtpBtn, forgotPassBtn, backBtn;
    private JLabel lblOtp, captchaImage;

    private String generatedOtp;
    private String currentUserEmail;
    private String generatedCaptcha;
    private boolean otpSent = false;

    public AdminLogin() {
        setTitle("Admin Login - Hospital Management System");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        JPanel outerPanel = new JPanel(new GridBagLayout());
        outerPanel.setBackground(new Color(240, 240, 240));  // light gray background
        setContentPane(outerPanel);

        // The "box" panel that will contain your login form
        JPanel contentBox = new JPanel(new GridBagLayout());
        contentBox.setBackground(Color.WHITE);
        contentBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2), // black border for the box
                BorderFactory.createEmptyBorder(30, 50, 30, 50) // padding inside the box
        ));

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 250, 255));
        add(panel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 20, 15, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Admin Login");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setForeground(new Color(0, 102, 204));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(titleLabel, gbc);

        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridwidth = 1;

        panel.add(new JLabel("Email:"), getConstraints(gbc, 0, 1));
        emailField = new JTextField(20);
        panel.add(emailField, getConstraints(gbc, 1, 1));

        panel.add(new JLabel("Password:"), getConstraints(gbc, 0, 2));
        passwordField = new JPasswordField(20);
        panel.add(passwordField, getConstraints(gbc, 1, 2));

        panel.add(new JLabel("CAPTCHA:"), getConstraints(gbc, 0, 3));
        captchaField = new JTextField(10);
        panel.add(captchaField, getConstraints(gbc, 1, 3));

        captchaImage = new JLabel();
        refreshCaptcha();
        panel.add(captchaImage, getConstraints(gbc, 0, 4));

        refreshCaptchaBtn = new JButton("↻ Refresh CAPTCHA");
        refreshCaptchaBtn.addActionListener(e -> refreshCaptcha());
        panel.add(refreshCaptchaBtn, getConstraints(gbc, 1, 4));

        loginBtn = new JButton("Login");
        styleButton(loginBtn, new Color(0, 123, 255));
        panel.add(loginBtn, getConstraints(gbc, 0, 5));

        forgotPassBtn = new JButton("Forgot Password?");
        forgotPassBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        forgotPassBtn.setForeground(new Color(0, 102, 204));
        forgotPassBtn.setContentAreaFilled(false);
        forgotPassBtn.setBorderPainted(false);
        forgotPassBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panel.add(forgotPassBtn, getConstraints(gbc, 1, 5));

        lblOtp = new JLabel("Enter OTP:");
        lblOtp.setVisible(false);
        panel.add(lblOtp, getConstraints(gbc, 0, 6));

        otpField = new JTextField(20);
        otpField.setVisible(false);
        panel.add(otpField, getConstraints(gbc, 1, 6));

        verifyOtpBtn = new JButton("Verify OTP");
        styleButton(verifyOtpBtn, new Color(40, 167, 69));
        verifyOtpBtn.setVisible(false);
        panel.add(verifyOtpBtn, getConstraints(gbc, 0, 7));

        backBtn = new JButton("← Back");
        styleButton(backBtn, new Color(108, 117, 125)); // Bootstrap gray
        panel.add(backBtn, getConstraints(gbc, 0, 8));

        resendOtpBtn = new JButton("Resend OTP");
        styleButton(resendOtpBtn, new Color(255, 193, 7));
        resendOtpBtn.setVisible(false);
        panel.add(resendOtpBtn, getConstraints(gbc, 1, 7));

        loginBtn.addActionListener(e -> handleLogin());
        verifyOtpBtn.addActionListener(e -> verifyOtp());
        resendOtpBtn.addActionListener(e -> resendOtp());
        forgotPassBtn.addActionListener(e -> forgotPassword());
        backBtn.addActionListener(e -> dispose());

        setVisible(true);
    }

    private GridBagConstraints getConstraints(GridBagConstraints gbc, int x, int y) {
        gbc.gridx = x;
        gbc.gridy = y;
        return gbc;
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void refreshCaptcha() {
        generatedCaptcha = generateCaptchaText(6);
        captchaImage.setIcon(generateCaptchaImage(generatedCaptcha));
    }

    private void handleLogin() {
        if (otpSent) {
            JOptionPane.showMessageDialog(this, "Please verify the OTP or click Resend OTP.");
            return;
        }

        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String inputCaptcha = captchaField.getText().trim();

        if (email.isEmpty() || password.isEmpty() || inputCaptcha.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.");
            return;
        }

        if (!inputCaptcha.equalsIgnoreCase(generatedCaptcha)) {
            JOptionPane.showMessageDialog(this, "Invalid CAPTCHA.");
            refreshCaptcha();
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM admin_users WHERE email = ?");
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                JOptionPane.showMessageDialog(this, "Admin not found.");
                return;
            }

            int failedAttempts = rs.getInt("failed_attempts");
            Timestamp lockTime = rs.getTimestamp("lock_time");

            if (lockTime != null) {
                long elapsed = System.currentTimeMillis() - lockTime.getTime();
                if (elapsed < 3600000) {
                    long remaining = 3600000 - elapsed;
                    long minutes = (remaining / 1000) / 60;
                    long seconds = (remaining / 1000) % 60;
                    JOptionPane.showMessageDialog(this, "Account is locked. Try again in " + minutes + "m " + seconds + "s.");
                    return;
                }
            }

            String dbPassHash = rs.getString("password_hash");
            if (!sha256(password).equals(dbPassHash)) {
                failedAttempts++;
                if (failedAttempts >= 3) {
                    PreparedStatement update = conn.prepareStatement("UPDATE admin_users SET failed_attempts = 0, lock_time = ? WHERE email = ?");
                    update.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
                    update.setString(2, email);
                    update.executeUpdate();
                    JOptionPane.showMessageDialog(this, "3 incorrect attempts! Account locked for 1 hour.");
                } else {
                    PreparedStatement update = conn.prepareStatement("UPDATE admin_users SET failed_attempts = ? WHERE email = ?");
                    update.setInt(1, failedAttempts);
                    update.setString(2, email);
                    update.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Incorrect password. Attempt " + failedAttempts + " of 3.");
                }
                return;
            }

            // Successful login
            PreparedStatement reset = conn.prepareStatement("UPDATE admin_users SET failed_attempts = 0, lock_time = NULL WHERE email = ?");
            reset.setString(1, email);
            reset.executeUpdate();

            generateAndShowOtp(email);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Login Error: " + ex.getMessage());
        }
    }

    private void generateAndShowOtp(String email) {
        generatedOtp = String.valueOf(new Random().nextInt(900000) + 100000);
        currentUserEmail = email;
        otpField.setVisible(true);
        lblOtp.setVisible(true);
        verifyOtpBtn.setVisible(true);
        resendOtpBtn.setVisible(true);
        otpSent = true;
        loginBtn.setEnabled(false);
        JOptionPane.showMessageDialog(this, "Your OTP is: " + generatedOtp, "OTP", JOptionPane.INFORMATION_MESSAGE);
    }

    private void resendOtp() {
        if (currentUserEmail != null) {
            generateAndShowOtp(currentUserEmail);
        }
    }

    private void verifyOtp() {
        String inputOtp = otpField.getText().trim();
        if (inputOtp.equals(generatedOtp)) {
            SessionManager.setLoggedInEmail(currentUserEmail);
            JOptionPane.showMessageDialog(this, "Login Successful!");
            dispose();
            new AdminDashboardModernUI().setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid OTP.");
        }
    }

    private void forgotPassword() {
        String email = JOptionPane.showInputDialog(this, "Enter registered email:");
        if (email == null || email.trim().isEmpty()) {
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM admin_users WHERE email = ?");
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                JOptionPane.showMessageDialog(this, "Email not registered.");
                return;
            }

            String otp = String.valueOf(new Random().nextInt(900000) + 100000);
            String input = JOptionPane.showInputDialog(this, "Enter OTP: " + otp);
            if (input == null || !input.equals(otp)) {
                JOptionPane.showMessageDialog(this, "Incorrect OTP.");
                return;
            }

            String newPass = JOptionPane.showInputDialog(this, "Enter new password:");
            if (newPass == null || newPass.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Password cannot be empty.");
                return;
            }

            PreparedStatement update = conn.prepareStatement("UPDATE admin_users SET password_hash = ? WHERE email = ?");
            update.setString(1, sha256(newPass));
            update.setString(2, email);
            update.executeUpdate();

            JOptionPane.showMessageDialog(this, "Password reset successfully.");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Reset Error: " + ex.getMessage());
        }
    }

    private String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 Error");
        }
    }

    private String generateCaptchaText(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder captcha = new StringBuilder();
        Random rand = new Random();
        for (int i = 0; i < length; i++) {
            captcha.append(chars.charAt(rand.nextInt(chars.length())));
        }
        return captcha.toString();
    }

    private ImageIcon generateCaptchaImage(String text) {
        BufferedImage img = new BufferedImage(160, 50, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = img.createGraphics();
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, 160, 50);
        g2.setFont(new Font("Arial", Font.BOLD, 30));
        g2.setColor(Color.BLUE);
        g2.drawString(text, 20, 35);
        g2.dispose();
        return new ImageIcon(img);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminLogin());
    }
}
