/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Admin;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class AdminSetting extends JFrame {

    private JLabel lblName, lblEmail;
    private JPasswordField currentPassField, newPassField;
    private JButton updatePassBtn, logoutBtn, backBtn;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/hospital_db";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";

    public AdminSetting() {
        setTitle("Admin Settings");
        setSize(600, 450);
        setMinimumSize(new Dimension(580, 430));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        mainPanel.setBackground(Color.WHITE);
        setContentPane(mainPanel);

        // Heading panel with title and underline
        JPanel headingPanel = new JPanel(new BorderLayout());
        headingPanel.setBackground(Color.WHITE);
        JLabel heading = new JLabel("Admin Settings");
        heading.setFont(new Font("Segoe UI Semibold", Font.BOLD, 28));
        heading.setForeground(new Color(0, 102, 204));
        headingPanel.add(heading, BorderLayout.CENTER);
        headingPanel.add(createUnderlinePanel(new Color(0, 102, 204)), BorderLayout.SOUTH);
        mainPanel.add(headingPanel, BorderLayout.NORTH);

        // Form panel with GridBagLayout for labels and fields
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        mainPanel.add(formPanel, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        String loggedInEmail = SessionManager.getLoggedInEmail();

        // Fetch admin name from DB
        String name = "Unknown";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String query = "SELECT name FROM admin_users WHERE email = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, loggedInEmail);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                name = rs.getString("name");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // ===== Labels column (right aligned) =====
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.gridx = 0;

        // Email Label
        gbc.gridy = 0;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        formPanel.add(emailLabel, gbc);

        // Name Label
        gbc.gridy++;
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        formPanel.add(nameLabel, gbc);

        // Current Password Label
        gbc.gridy++;
        JLabel currentPassLabel = new JLabel("Current Password:");
        currentPassLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        formPanel.add(currentPassLabel, gbc);

        // New Password Label
        gbc.gridy++;
        JLabel newPassLabel = new JLabel("New Password:");
        newPassLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        formPanel.add(newPassLabel, gbc);

        // ===== Fields column (left aligned) =====
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.gridx = 1;
        gbc.gridy = 0;

        lblEmail = new JLabel(loggedInEmail);
        lblEmail.setFont(new Font("Segoe UI", Font.BOLD, 18));
        formPanel.add(lblEmail, gbc);

        gbc.gridy++;
        lblName = new JLabel(name);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 18));
        formPanel.add(lblName, gbc);

        gbc.gridy++;
        currentPassField = new JPasswordField();
        currentPassField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        currentPassField.setPreferredSize(new Dimension(250, 30));
        formPanel.add(currentPassField, gbc);

        gbc.gridy++;
        newPassField = new JPasswordField();
        newPassField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        newPassField.setPreferredSize(new Dimension(250, 30));
        formPanel.add(newPassField, gbc);

        // ===== Buttons panel =====
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnPanel.setBackground(Color.WHITE);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        updatePassBtn = new JButton("Update Password");
        styleButton(updatePassBtn, new Color(0, 153, 76));
        updatePassBtn.setPreferredSize(new Dimension(170, 40));
        btnPanel.add(updatePassBtn);

        logoutBtn = new JButton("Logout");
        styleButton(logoutBtn, new Color(220, 53, 69));
        logoutBtn.setPreferredSize(new Dimension(120, 40));
        btnPanel.add(logoutBtn);

        backBtn = new JButton("Back");
        styleButton(backBtn, new Color(105, 105, 105));
        backBtn.setPreferredSize(new Dimension(120, 40));
        btnPanel.add(backBtn);

        // ===== Events =====
        updatePassBtn.addActionListener(e -> updatePassword());
        logoutBtn.addActionListener(e -> {
            SessionManager.clearSession();
            dispose();
            new AdminLogin().setVisible(true);
        });
        backBtn.addActionListener(e -> {
            dispose();
            new AdminDashboardModernUI().setVisible(true);
        });

        setVisible(true);
    }

    // Utility method to create a thin underline panel below the heading
    private JPanel createUnderlinePanel(Color color) {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(0, 3));
        panel.setBackground(color);
        return panel;
    }

    private void styleButton(JButton btn, Color bgColor) {
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Hover effect
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(bgColor);
            }
        });
    }

    private void updatePassword() {
        String currentPass = new String(currentPassField.getPassword()).trim();
        String newPass = new String(newPassField.getPassword()).trim();

        if (currentPass.isEmpty() || newPass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Both fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String email = SessionManager.getLoggedInEmail();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String query = "SELECT password_hash FROM admin_users WHERE email = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String dbHash = rs.getString("password_hash");
                if (!dbHash.equals(sha256(currentPass))) {
                    JOptionPane.showMessageDialog(this, "Incorrect current password!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            String update = "UPDATE admin_users SET password_hash = ? WHERE email = ?";
            PreparedStatement updatePs = conn.prepareStatement(update);
            updatePs.setString(1, sha256(newPass));
            updatePs.setString(2, email);
            updatePs.executeUpdate();

            JOptionPane.showMessageDialog(this, "Password updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            currentPassField.setText("");
            newPassField.setText("");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating password!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String sha256(String base) throws Exception {
        java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(base.getBytes("UTF-8"));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash)
            hexString.append(String.format("%02x", b));
        return hexString.toString();
    }

    public static void main(String[] args) {
        try {
           // UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
        SwingUtilities.invokeLater(AdminSetting::new);
    }
}


