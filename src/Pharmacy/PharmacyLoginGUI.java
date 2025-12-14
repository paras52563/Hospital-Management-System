/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Pharmacy;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Random;
import java.util.regex.Pattern;

public class PharmacyLoginGUI extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JCheckBox showPasswordCheckbox;
    private JButton loginButton, forgotButton, resendOtpButton;
    private JPanel cardPanel;
    private CardLayout cardLayout;

    private String generatedOTP = null;
    private String currentEmail = null;

    private JLabel captchaLabel;
    private JTextField captchaInput;
    private String captchaCode;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/hospital_db";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";

    public PharmacyLoginGUI() {
        setTitle("Pharmacy Secure Portal");
     
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // setSize(950, 600); // no longer needed with fullscreen
        setExtendedState(JFrame.MAXIMIZED_BOTH);  // <-- Fullscreen
        setLocationRelativeTo(null);
        setResizable(false);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        cardPanel.add(createLoginPanel(), "login");

        add(cardPanel);
        setVisible(true);
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(new EmptyBorder(40, 40, 40, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(14, 14, 14, 14);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;

        // Heading Label
        JLabel heading = new JLabel("Pharmacy Login Portal");
        heading.setFont(new Font("Segoe UI Semibold", Font.BOLD, 28));
        heading.setForeground(new Color(34, 49, 63));
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setBorder(new EmptyBorder(0, 0, 25, 0));
        panel.add(heading, gbc);

        // Email Field
        gbc.gridy++;
        emailField = new JTextField();
        styleTextField(emailField);
        setPlaceholder(emailField, "Enter Email");
        panel.add(emailField, gbc);

        // Password Field
        gbc.gridy++;
        passwordField = new JPasswordField();
        styleTextField(passwordField);
        setPlaceholder(passwordField, "Enter Password");
        panel.add(passwordField, gbc);

        // Show Password Checkbox
        gbc.gridy++;
        showPasswordCheckbox = new JCheckBox("Show Password");
        showPasswordCheckbox.setBackground(panel.getBackground());
        showPasswordCheckbox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        showPasswordCheckbox.setForeground(new Color(90, 90, 90));
        showPasswordCheckbox.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        showPasswordCheckbox.addActionListener(e -> {
            passwordField.setEchoChar(showPasswordCheckbox.isSelected() ? (char) 0 : '•');
        });
        panel.add(showPasswordCheckbox, gbc);

        // Hide showPasswordCheckbox when passwordField focused
        passwordField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                showPasswordCheckbox.setVisible(false);
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (passwordField.getPassword().length > 0) {
                    showPasswordCheckbox.setVisible(true);
                }
            }
        });

        // CAPTCHA Label & Input
        gbc.gridy++;
        gbc.gridwidth = 1;
        captchaCode = generateCaptchaCode(5);
        captchaLabel = new JLabel(captchaCode);
        captchaLabel.setFont(new Font("Consolas", Font.BOLD, 24));
        captchaLabel.setForeground(new Color(23, 162, 184)); // nice cyan
        captchaLabel.setBorder(new CompoundBorder(new LineBorder(new Color(23, 162, 184), 2, true), new EmptyBorder(7, 25, 7, 25)));
        captchaLabel.setHorizontalAlignment(SwingConstants.CENTER);
        captchaLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        captchaLabel.setToolTipText("Click to refresh CAPTCHA");
        captchaLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                captchaCode = generateCaptchaCode(5);
                captchaLabel.setText(captchaCode);
            }
        });
        panel.add(captchaLabel, gbc);

        gbc.gridx = 1;
        captchaInput = new JTextField();
        styleTextField(captchaInput);
        setPlaceholder(captchaInput, "Enter CAPTCHA");
        panel.add(captchaInput, gbc);

        // Login Button
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1;
        loginButton = new JButton("Login");
        styleButton(loginButton, new Color(0, 123, 255), Color.WHITE);
        panel.add(loginButton, gbc);

        // Forgot Password Button
        gbc.gridx = 1;
        forgotButton = new JButton("Forgot Password?");
        forgotButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        forgotButton.setForeground(new Color(0, 102, 204));
        forgotButton.setContentAreaFilled(false);
        forgotButton.setBorderPainted(false);
        forgotButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        forgotButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { forgotButton.setForeground(new Color(0, 80, 160)); }
            public void mouseExited(MouseEvent e) { forgotButton.setForeground(new Color(0, 102, 204)); }
        });
        panel.add(forgotButton, gbc);

        // Resend OTP Button (hidden initially)
        gbc.gridx = 0;
        gbc.gridy++;
        resendOtpButton = new JButton("Resend OTP");
        styleButton(resendOtpButton, new Color(255, 193, 7), Color.BLACK);
        resendOtpButton.setVisible(false);
        panel.add(resendOtpButton, gbc);

        // Action listeners
        loginButton.addActionListener(e -> login());
        forgotButton.addActionListener(e -> forgotPassword());
        resendOtpButton.addActionListener(e -> {
            if (currentEmail != null) {
                generatedOTP = generateOTP();
                JOptionPane.showMessageDialog(this, "Your OTP is: " + generatedOTP);
            }
        });

        return panel;
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        field.setBackground(Color.WHITE);
        field.setBorder(new CompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1, true),
            new EmptyBorder(10, 14, 10, 14)
        ));
        field.setForeground(new Color(50, 50, 50));
        field.setCaretColor(new Color(0, 123, 255));
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(new CompoundBorder(
                    new LineBorder(new Color(0, 123, 255), 2, true),
                    new EmptyBorder(10, 14, 10, 14)
                ));
            }
            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(new CompoundBorder(
                    new LineBorder(new Color(200, 200, 200), 1, true),
                    new EmptyBorder(10, 14, 10, 14)
                ));
            }
        });
    }

    private void styleButton(JButton button, Color bg, Color fg) {
        button.setBackground(bg);
        button.setForeground(fg);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI Semibold", Font.BOLD, 18));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(new RoundedBorder(12));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bg.darker());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bg);
            }
        });
    }

    private void setPlaceholder(JTextField field, String placeholder) {
        field.setForeground(Color.GRAY);
        field.setText(placeholder);
        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(new Color(50, 50, 50));
                }
            }
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setForeground(Color.GRAY);
                    field.setText(placeholder);
                }
            }
        });
    }

    private void login() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String captchaText = captchaInput.getText().trim();

        if (email.isEmpty() || password.isEmpty() || captchaText.isEmpty() ||
            email.equals("Enter Email") || password.equals("Enter Password") || captchaText.equals("Enter CAPTCHA")) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields including CAPTCHA.");
            return;
        }

        if (!isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address.");
            return;
        }

        if (!captchaText.equalsIgnoreCase(captchaCode)) {
            JOptionPane.showMessageDialog(this, "CAPTCHA does not match. Please try again.");
            captchaCode = generateCaptchaCode(5);
            captchaLabel.setText(captchaCode);
            captchaInput.setText("");
            return;
        }

        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            PreparedStatement ps = con.prepareStatement("SELECT password FROM pharmacy_users WHERE email = ?");
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString(1);
                if (!storedHash.equals(hashSHA256(password))) {
                    JOptionPane.showMessageDialog(this, "Invalid credentials.");
                    return;
                }
                currentEmail = email;
                generatedOTP = generateOTP();

                JOptionPane.showMessageDialog(this, "Your OTP is: " + generatedOTP);
                String userOtp = showOtpDialog();

                if (userOtp != null && userOtp.equals(generatedOTP)) {
                    JOptionPane.showMessageDialog(this, "Login successful!");
                    dispose();
                    new PharmacyDashboardModern().setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Incorrect OTP.");
                    resendOtpButton.setVisible(true);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Email not registered.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private String showOtpDialog() {
        JPanel panel = new JPanel(new BorderLayout(5,5));
        JLabel label = new JLabel("Enter the OTP sent to your email:");
        JTextField otpField = new JTextField();
        panel.add(label, BorderLayout.NORTH);
        panel.add(otpField, BorderLayout.CENTER);
        int result = JOptionPane.showConfirmDialog(this, panel, "OTP Verification",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            return otpField.getText().trim();
        }
        return null;
    }

    private void forgotPassword() {
        String email = JOptionPane.showInputDialog(this, "Enter your registered email:");
        if (email == null || email.trim().isEmpty()) return;

        if (!isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email.");
            return;
        }

        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM pharmacy_users WHERE email = ?");
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                generatedOTP = generateOTP();
                JOptionPane.showMessageDialog(this, "Your OTP is: " + generatedOTP);

                String inputOtp = showOtpDialog();
                if (inputOtp != null && inputOtp.equals(generatedOTP)) {
                    JPanel panel = new JPanel(new BorderLayout(5,5));
                    JLabel label = new JLabel("Enter new password:");
                    JPasswordField newPasswordField = new JPasswordField();
                    panel.add(label, BorderLayout.NORTH);
                    panel.add(newPasswordField, BorderLayout.CENTER);

                    int result = JOptionPane.showConfirmDialog(this, panel, "Reset Password",
                            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                    if (result == JOptionPane.OK_OPTION) {
                        String newPass = new String(newPasswordField.getPassword());
                        if (newPass.isEmpty()) {
                            JOptionPane.showMessageDialog(this, "Password cannot be empty.");
                            return;
                        }

                        PreparedStatement update = con.prepareStatement("UPDATE pharmacy_users SET password = ? WHERE email = ?");
                        update.setString(1, hashSHA256(newPass));
                        update.setString(2, email);
                        update.executeUpdate();
                        JOptionPane.showMessageDialog(this, "Password updated successfully!");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Incorrect OTP.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Email not registered.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private String generateOTP() {
        return String.valueOf(100000 + new Random().nextInt(900000));
    }

    private String generateCaptchaCode(int length) {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i=0; i<length; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private String hashSHA256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException("SHA-256 algorithm not found");
        }
    }

    private boolean isValidEmail(String email) {
        String regex = "^[\\w-.]+@[\\w-]+\\.[a-z]{2,}$";
        return Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(email).matches();
    }

    // Rounded border for buttons
    static class RoundedBorder extends AbstractBorder {
        private final int radius;
        public RoundedBorder(int radius) { this.radius = radius; }
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            g.setColor(new Color(0, 123, 255));
            g.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(radius/2, radius/2, radius/2, radius/2);
        }
        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.top = insets.right = insets.bottom = radius/2;
            return insets;
        }
    }

    public static void main(String[] args) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        SwingUtilities.invokeLater(PharmacyLoginGUI::new);
    }
}


