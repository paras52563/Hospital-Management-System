/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package doctor;

import homepage.HospitalHomePage;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Random;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class DoctorLoginSystem extends JFrame {
    private JTextField tfEmail, tfCaptcha;
    private JPasswordField pfPassword;
    private JLabel lblCaptcha, lblStatus;
    private JButton btnLogin, btnForgot, btnBack, btnRefresh;
    private String captchaText = "";

    public DoctorLoginSystem() {
        setTitle("Doctor Login - Hospital Management System");
        setSize(520, 460);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
       
        initUI();
        initListeners();
    }

    private void initUI() {
        // Gradient background panel
        JPanel bgPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(25, 118, 210),
                        0, getHeight(), new Color(13, 71, 161));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // White card-like panel in center
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel lblTitle = new JLabel("Doctor Login", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(new Color(25, 118, 210));

        tfEmail = new JTextField(20);
        styleField(tfEmail);
        pfPassword = new JPasswordField(20);
        styleField(pfPassword);

        lblCaptcha = new JLabel();
        lblCaptcha.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblCaptcha.setForeground(new Color(198, 40, 40));

        tfCaptcha = new JTextField(10);
        styleField(tfCaptcha);
        btnRefresh = styledButton("Refresh", new Color(117, 117, 117));

        btnLogin = styledButton("Login", new Color(56, 142, 60));
        btnForgot = styledButton("Forgot Password?", new Color(25, 118, 210));
        btnBack = styledButton("Back", new Color(211, 47, 47));
        lblStatus = new JLabel(" ", SwingConstants.CENTER);
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblStatus.setForeground(new Color(198, 40, 40));

        // Layout for card
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        card.add(lblTitle, gbc);

        gbc.gridwidth = 1; gbc.gridy++; gbc.anchor = GridBagConstraints.EAST;
        card.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        card.add(tfEmail, gbc);

        gbc.gridx = 0; gbc.gridy++; gbc.anchor = GridBagConstraints.EAST;
        card.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        card.add(pfPassword, gbc);

        // Captcha Row
        gbc.gridx = 0; gbc.gridy++; gbc.anchor = GridBagConstraints.EAST;
        card.add(new JLabel("Captcha:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        JPanel captchaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        captchaPanel.setBackground(Color.WHITE);
        captchaPanel.add(lblCaptcha);
        captchaPanel.add(tfCaptcha);
        captchaPanel.add(btnRefresh);
        card.add(captchaPanel, gbc);

        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        card.add(btnLogin, gbc);
        gbc.gridy++;
        card.add(btnForgot, gbc);
        gbc.gridy++;
        card.add(btnBack, gbc);
        gbc.gridy++;
        card.add(lblStatus, gbc);

        bgPanel.add(card);
        add(bgPanel);

        generateCaptcha();
        setVisible(true);
    }

    private void initListeners() {
        btnLogin.addActionListener(e -> login());
        btnForgot.addActionListener(e -> forgotPassword());
        btnBack.addActionListener(e -> {
            dispose();
            new HospitalHomePage().setVisible(true);
        });
        btnRefresh.addActionListener(e -> generateCaptcha());
    }
     private boolean isValidEmail(String email) {
        String regex = "^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,}$";
        return email != null && email.matches(regex);
    }

    private void styleField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
    }

    private JButton styledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btn.setOpaque(true);

        // Hover effect
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

    private void generateCaptcha() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder sb = new StringBuilder();
        Random r = new Random();
        for (int i = 0; i < 5; i++) {
            sb.append(chars.charAt(r.nextInt(chars.length())));
        }
        captchaText = sb.toString();
        lblCaptcha.setText(captchaText);
        tfCaptcha.setText("");
    }

    private void login() {
        lblStatus.setText(" ");
        String email = tfEmail.getText().trim();
        String password = new String(pfPassword.getPassword()).trim();
        String enteredCaptcha = tfCaptcha.getText().trim();

        if (email.isEmpty() || password.isEmpty() || enteredCaptcha.isEmpty()) {
            lblStatus.setText("Please fill all fields.");
            return;
        }
        if (!enteredCaptcha.equalsIgnoreCase(captchaText)) {
            lblStatus.setText("Invalid Captcha!");
            generateCaptcha();
            return;
        }
        if (!isValidEmail(email)) {
            lblStatus.setText("Enter a valid email (e.g., doctor@hospital.com).");
            return;
        }

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital_db", "root", "");
             PreparedStatement pst = con.prepareStatement("SELECT * FROM doctor WHERE email=? AND BINARY  password=?")) {
            pst.setString(1, email);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                String doctorId = rs.getString("doctor_id");

                // OTP Validation
                String otp = generateOTP();
                String inputOtp = JOptionPane.showInputDialog(this, "Your OTP is: " + otp + "\nEnter OTP:");
                if (inputOtp != null && inputOtp.equals(otp)) {
                    dispose();
                    new DoctorDashboard(doctorId).setVisible(true);
                } else {
                    lblStatus.setText("Incorrect OTP.");
                }
            } else {
                lblStatus.setText("Invalid email or password.");
            }
        } catch (Exception ex) {
            lblStatus.setText("Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void forgotPassword() {
        lblStatus.setText(" ");
        String email = tfEmail.getText().trim();
        if (email.isEmpty()||!isValidEmail(email)) {
            lblStatus.setText("Enter your registered email.");
            return;
        }

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital_db", "root", "");
             PreparedStatement pst = con.prepareStatement("SELECT * FROM doctor WHERE email=?")) {
            pst.setString(1, email);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                String otp = generateOTP();
                String inputOtp = JOptionPane.showInputDialog(this, "Your OTP is: " + otp + "\nEnter OTP to reset password:");
                if (inputOtp != null && inputOtp.equals(otp)) {
                    String newPass = JOptionPane.showInputDialog(this, "Enter new password:");
                    if (newPass != null && !newPass.trim().isEmpty()) {
                        PreparedStatement update = con.prepareStatement("UPDATE doctor SET password=? WHERE email=?");
                        update.setString(1, newPass.trim());
                        update.setString(2, email);
                        update.executeUpdate();
                        JOptionPane.showMessageDialog(this, "Password reset successful.");
                    } else {
                        lblStatus.setText("Password cannot be empty.");
                    }
                } else {
                    lblStatus.setText("Incorrect OTP.");
                }
            } else {
                lblStatus.setText("Email not registered.");
            }
        } catch (Exception ex) {
            lblStatus.setText("Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private String generateOTP() {
        return String.valueOf(new Random().nextInt(900000) + 100000);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DoctorLoginSystem::new);
    }
}



