/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Admin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class PatientLogin extends JFrame {
    private JTextField emailField, otpField;
    private JPasswordField passwordField, newPassField, confirmPassField;
    private JButton sendOtpBtn, loginBtn, forgotPassBtn, verifyOtpBtn, changePassBtn;
    private String generatedOTP, currentEmail;
    private boolean isLoginOTPVerified = false;

    public PatientLogin() {
        setTitle("Patient Login Portal");
        setSize(600, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(240, 248, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(panel, gbc);

        GridBagConstraints p = new GridBagConstraints();
        p.insets = new Insets(10, 10, 10, 10);
        p.gridx = 0; p.gridy = 0; p.gridwidth = 2;

        JLabel title = new JLabel("Patient Login");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        panel.add(title, p);

        // Email
        p.gridy++;
        p.gridwidth = 1;
        panel.add(new JLabel("Email:"), p);

        emailField = new JTextField(20);
        p.gridx = 1;
        panel.add(emailField, p);

        // Send OTP
        p.gridy++; p.gridx = 1;
        sendOtpBtn = new JButton("Send OTP");
        styleButton(sendOtpBtn, new Color(30, 144, 255));
        sendOtpBtn.addActionListener(e -> sendLoginOTP(emailField.getText()));
        panel.add(sendOtpBtn, p);

        // OTP
        p.gridy++; p.gridx = 0;
        panel.add(new JLabel("OTP:"), p);

        otpField = new JTextField(20);
        p.gridx = 1;
        panel.add(otpField, p);

        // Verify OTP
        p.gridy++; p.gridx = 1;
        verifyOtpBtn = new JButton("Verify OTP");
        styleButton(verifyOtpBtn, new Color(46, 139, 87));
        verifyOtpBtn.addActionListener(e -> verifyLoginOTP());
        panel.add(verifyOtpBtn, p);

        // Password
        p.gridy++; p.gridx = 0;
        panel.add(new JLabel("Password:"), p);

        passwordField = new JPasswordField(20);
        p.gridx = 1;
        panel.add(passwordField, p);

        // Login
        p.gridy++; p.gridx = 1;
        loginBtn = new JButton("Login");
        styleButton(loginBtn, new Color(70, 130, 180));
        loginBtn.addActionListener(e -> loginUser());
        panel.add(loginBtn, p);

        // Forgot password
        p.gridy++;
        forgotPassBtn = new JButton("Forgot Password?");
        forgotPassBtn.setForeground(Color.BLUE);
        forgotPassBtn.setContentAreaFilled(false);
        forgotPassBtn.setBorderPainted(false);
        forgotPassBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotPassBtn.addActionListener(e -> showOtpForReset());
        panel.add(forgotPassBtn, p);

        // New password
        p.gridy++;
        newPassField = new JPasswordField(20);
        newPassField.setVisible(false);
        panel.add(newPassField, p);

        // Confirm password
        p.gridy++;
        confirmPassField = new JPasswordField(20);
        confirmPassField.setVisible(false);
        panel.add(confirmPassField, p);

        // Change Password Button
        p.gridy++;
        changePassBtn = new JButton("Change Password");
        styleButton(changePassBtn, new Color(255, 69, 0));
        changePassBtn.setVisible(false);
        changePassBtn.addActionListener(e -> changePassword());
        panel.add(changePassBtn, p);

        add(mainPanel);
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
    }

    // OTP Email logic
    private void sendLoginOTP(String email) {
        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter your email first.");
            return;
        }
        generatedOTP = String.valueOf(new Random().nextInt(899999) + 100000);
        currentEmail = email;

        try (Connection con = getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM patients WHERE email=?");
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                sendEmail(email, generatedOTP, "Login OTP");
                JOptionPane.showMessageDialog(this, "OTP sent to your email.");
            } else {
                JOptionPane.showMessageDialog(this, "Email not registered.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void verifyLoginOTP() {
        if (otpField.getText().trim().equals(generatedOTP)) {
            isLoginOTPVerified = true;
            JOptionPane.showMessageDialog(this, "OTP verified. Proceed to login.");
        } else {
            JOptionPane.showMessageDialog(this, "Incorrect OTP.");
        }
    }

    private void loginUser() {
        if (!isLoginOTPVerified) {
            JOptionPane.showMessageDialog(this, "Please verify OTP first.");
            return;
        }
        String email = emailField.getText().trim();
        String password = String.valueOf(passwordField.getPassword());

        try (Connection con = getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM patients WHERE email=? AND password=?");
            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Login successful!");
                dispose();
                
                
            } else {
                JOptionPane.showMessageDialog(this, "Invalid email or password.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void showOtpForReset() {
        String email = JOptionPane.showInputDialog(this, "Enter your registered email:");
        if (email == null || email.trim().isEmpty()) return;

        generatedOTP = String.valueOf(new Random().nextInt(899999) + 100000);
        currentEmail = email;

        try (Connection con = getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM patients WHERE email=?");
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                sendEmail(email, generatedOTP, "Password Reset OTP");
                String enteredOtp = JOptionPane.showInputDialog(this, "Enter OTP sent to your email:");
                if (enteredOtp != null && enteredOtp.equals(generatedOTP)) {
                    newPassField.setVisible(true);
                    confirmPassField.setVisible(true);
                    changePassBtn.setVisible(true);
                    revalidate();
                    repaint();
                } else {
                    JOptionPane.showMessageDialog(this, "Incorrect OTP.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Email not registered.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void changePassword() {
        String newPass = String.valueOf(newPassField.getPassword());
        String confirmPass = String.valueOf(confirmPassField.getPassword());

        if (!newPass.equals(confirmPass)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.");
            return;
        }

        try (Connection con = getConnection()) {
            PreparedStatement ps = con.prepareStatement("UPDATE patients SET password=? WHERE email=?");
            ps.setString(1, newPass);
            ps.setString(2, currentEmail);
            int updated = ps.executeUpdate();

            if (updated > 0) {
                JOptionPane.showMessageDialog(this, "Password updated. You can now login.");
                dispose();
                new PatientLogin().setVisible(true);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void sendEmail(String to, String otp, String subject) {
        final String from = "aroramayank488@gmail.com";
        final String pass = "zojw kkfv hdyl etih";

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, pass);
            }
        });

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(from));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            msg.setSubject(subject);
            msg.setText("Your OTP is: " + otp);
            Transport.send(msg);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private Connection getConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital_db", "root", "");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PatientLogin().setVisible(true));
    }
}

