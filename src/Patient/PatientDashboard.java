/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Patient;

import javax.swing.*;
import java.awt.*;

public class PatientDashboard extends JFrame {

    private final String patientId;

    public PatientDashboard(String patientId) {
        this.patientId = patientId;

        setTitle("Patient Dashboard - " + patientId);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ---------- HEADER ----------
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(33, 150, 243));
        header.setPreferredSize(new Dimension(0, 80));

        JLabel welcomeLabel = new JLabel("Welcome, Patient", SwingConstants.LEFT);
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 0));

        JLabel idLabel = new JLabel("Patient ID: " + patientId, SwingConstants.RIGHT);
        idLabel.setForeground(Color.WHITE);
        idLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        idLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 30));

        header.add(welcomeLabel, BorderLayout.WEST);
        header.add(idLabel, BorderLayout.EAST);

        // ---------- SIDEBAR ----------
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBackground(new Color(245, 245, 245));
        sidebar.setLayout(new BorderLayout());

        JPanel topSidebar = new JPanel();
        topSidebar.setLayout(new BoxLayout(topSidebar, BoxLayout.Y_AXIS));
        topSidebar.setBackground(new Color(245, 245, 245));

        JLabel navTitle = new JLabel("Dashboard");
        navTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        navTitle.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 10));
        topSidebar.add(navTitle);

        sidebar.add(topSidebar, BorderLayout.NORTH);

        // ---------- LOGOUT BUTTON AT BOTTOM ----------
        JButton logoutButton = new JButton("🔒  Logout");
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        logoutButton.setBackground(new Color(244, 67, 54));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(211, 47, 47), 2),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        logoutButton.setMaximumSize(new Dimension(180, 45));
        logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to logout?",
                    "Logout Confirmation",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );
            if (confirm == JOptionPane.YES_OPTION) {
                dispose(); // Close current window
                new homepage.HospitalHomePage().setVisible(true);
            }
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(245, 245, 245));
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.add(Box.createVerticalStrut(30));
        bottomPanel.add(logoutButton);
        bottomPanel.add(Box.createVerticalStrut(30));

        sidebar.add(bottomPanel, BorderLayout.SOUTH);

        // ---------- CENTER PANEL ----------
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 10, 20, 10);
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        JButton btnProfile = createButton("👤  View Profile");
        JButton btnBook = createButton("📅  Book Appointment");
        JButton btnAppointments = createButton("🗓️  View Appointments");
        JButton btnMedicalHistory = createButton("🩺  Medical History");  // New button
        JButton btnPrescriptionHistory = createButton("🩺  Prescription History");  // New button

        gbc.gridy = 0;
        centerPanel.add(btnProfile, gbc);

        gbc.gridy = 1;
        centerPanel.add(btnBook, gbc);

        gbc.gridy = 2;
        centerPanel.add(btnAppointments, gbc);

        gbc.gridy = 3;
        centerPanel.add(btnMedicalHistory, gbc);  // Add new button
        
        gbc.gridy = 4;
        centerPanel.add(btnPrescriptionHistory, gbc);  // Add new button

        // ---------- FOOTER ----------
        JPanel footer = new JPanel();
        footer.setBackground(new Color(240, 240, 240));
        footer.setPreferredSize(new Dimension(0, 40));

        JLabel footerLabel = new JLabel("© 2025 HealthCare System");
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        footer.add(footerLabel);

        // ---------- ACTIONS ----------
        btnProfile.addActionListener(e -> {
            setVisible(false);
            ViewPatientProfile vprofile=new ViewPatientProfile(patientId);
            vprofile.setVisible(true);
           

        });
        btnBook.addActionListener(e -> {
            setVisible(false);
            BookPatientAppointment newWindow=new BookPatientAppointment(patientId);
            newWindow.setVisible(true);
            
        });

        btnAppointments.addActionListener(e ->{
            setVisible(false);
            PatientAppointmentManager pmanager=new PatientAppointmentManager(patientId);
            pmanager.setVisible(true);
            
           
        });
        btnMedicalHistory.addActionListener(e -> {
            setVisible(false);
            new MedicalHistoryNotes(patientId);
        }); 
        // Action for new button
        // ---------- ADD COMPONENTS TO FRAME ----------
        add(header, BorderLayout.NORTH);
        add(sidebar, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);

        setVisible(true);
    }

    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btn.setBackground(new Color(30, 136, 229));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(21, 101, 192), 2),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        return btn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PatientDashboard("PAT88073"));
    }
}
