/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
// Same package
package Admin;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class AdminDashboardModernUI extends JFrame {

    private JPanel cardsPanel;
    private JLabel lblDoctorCount, lblPatientCount, lblMedicalRecordCount, lblAppointmentCount;
    private JLabel statusLabel;

    public AdminDashboardModernUI() {
        setTitle("🏥 Admin Dashboard - Hospital Management System");
        setSize(1200, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setMinimumSize(new Dimension(1000, 650));

        // Top Bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(30, 41, 59));
        topBar.setPreferredSize(new Dimension(0, 60));
        topBar.setBorder(new MatteBorder(0, 0, 2, 0, new Color(80, 120, 210)));

        // Heading
        JLabel heading = new JLabel("Admin Dashboard");
        heading.setForeground(Color.WHITE);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 26));
        heading.setBorder(new EmptyBorder(0, 25, 0, 0));
        topBar.add(heading, BorderLayout.WEST);

        // Logged-in email display from SessionManager
        String loggedInEmail = SessionManager.getLoggedInEmail();
        JLabel userEmailLabel = new JLabel("Logged in as: " + loggedInEmail);
        userEmailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userEmailLabel.setForeground(Color.WHITE);
        userEmailLabel.setBorder(new EmptyBorder(0, 0, 0, 20));
        topBar.add(userEmailLabel, BorderLayout.EAST);

        add(topBar, BorderLayout.NORTH);

        // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(15, 23, 42));
        sidebar.setPreferredSize(new Dimension(260, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(25, 15, 25, 15));

        String[] navItems = {"Dashboard", "Doctors", "Patients", "Appointments", "Medical Records", "Settings", "Logout"};
        for (String item : navItems) {
            JButton btn = new JButton("  " + item);
            btn.setForeground(Color.WHITE);
            btn.setBackground(new Color(15, 23, 42));
            btn.setFocusPainted(false);
            btn.setAlignmentX(Component.LEFT_ALIGNMENT);
            btn.setBorder(new EmptyBorder(15, 20, 15, 10));
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.setHorizontalAlignment(SwingConstants.LEFT);

            btn.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent evt) {
                    btn.setBackground(new Color(42, 68, 123));
                }

                public void mouseExited(MouseEvent evt) {
                    btn.setBackground(new Color(15, 23, 42));
                }
            });

            btn.addActionListener(e -> {
                String cmd = btn.getText().trim();
                if (cmd.equals("Logout")) {
                    dispose();
                    System.exit(0);
                } else if (cmd.equals("Dashboard")) {
                    ((CardLayout) cardsPanel.getLayout()).show(cardsPanel, "Dashboard");
                } else if(cmd.equals("Doctors")){
                    dispose();
                    new DoctorMainDashboard().setVisible(true);
                } else if(cmd.equals("Patients")){
                    dispose();
                    new PatientMainDashboard().setVisible(true);
                } else if(cmd.equals("Settings")){
                    dispose();
                    new AdminSetting().setVisible(true);
                } else if(cmd.equals("Appointments")){
                    dispose();
                    new AdminAppointmentsDashboardModern().setVisible(true);
                }else if(cmd.equals("Medical Records")){
                    dispose();
                    new AdminMedicalRecordsDashboard().setVisible(true);
                }else if(cmd.equals("Logout")){
                    dispose();
                    new AdminLogin().setVisible(true);
                }
                else {
                    JOptionPane.showMessageDialog(this, cmd + " module is under development.");
                }
            });

            sidebar.add(btn);
            sidebar.add(Box.createVerticalStrut(10));
        }

        add(sidebar, BorderLayout.WEST);

        // Main Dashboard Cards
        cardsPanel = new JPanel(new CardLayout());

        JPanel dashboardPanel = new JPanel(new GridLayout(2, 2, 30, 30));
        dashboardPanel.setBackground(new Color(245, 247, 250));
        dashboardPanel.setBorder(new EmptyBorder(50, 50, 50, 50));
        Font emojiFont = new Font("Segoe UI Emoji", Font.PLAIN, 20); //
        
        String doctorIcon = "\uD83D\uDC68\u200D\u2695\uFE0F"; // 👨‍⚕️
        String patientIcon = "\uD83E\uDDD1\u200D\u2695\uFE0F"; // 🧑‍⚕️
        String recordsIcon = "\uD83D\uDCCB"; // 📋
        String appointmentsIcon = "\uD83D\uDCC5"; // 📅


        lblDoctorCount = createGlassCard(doctorIcon + " Doctors", "Loading...", new Color(27, 156, 252));
        lblPatientCount = createGlassCard(patientIcon + " Patients", "Loading...", new Color(72, 195, 163));
        lblMedicalRecordCount = createGlassCard(recordsIcon + " Medical Records", "Loading...", new Color(255, 185, 60));
        lblAppointmentCount = createGlassCard(appointmentsIcon + " Appointments", "Loading...", new Color(239, 83, 80));


        dashboardPanel.add(lblDoctorCount.getParent());
        dashboardPanel.add(lblPatientCount.getParent());
        dashboardPanel.add(lblMedicalRecordCount.getParent());
        dashboardPanel.add(lblAppointmentCount.getParent());

        cardsPanel.add(dashboardPanel, "Dashboard");
        add(cardsPanel, BorderLayout.CENTER);

        // Status Bar
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.setBackground(new Color(235, 235, 235));
        statusBar.setBorder(new MatteBorder(1, 0, 0, 0, new Color(200, 200, 200)));
        statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusLabel.setForeground(new Color(70, 70, 70));
        statusBar.add(statusLabel);
        add(statusBar, BorderLayout.SOUTH);

        // Load data
        loadCountsFromDatabase();
        setVisible(true);
    }

    private JLabel createGlassCard(String title, String count, Color bgColor) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(10, 10));
        card.setBackground(bgColor);
        card.setBorder(new CompoundBorder(new EmptyBorder(25, 30, 25, 30), new RoundedShadowBorder()));
        card.setPreferredSize(new Dimension(280, 160));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.setOpaque(true);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);

        JLabel countLabel = new JLabel(count);
        countLabel.setFont(new Font("Segoe UI Black", Font.BOLD, 50));
        countLabel.setForeground(Color.WHITE);
        countLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(countLabel, BorderLayout.CENTER);

        card.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                card.setBorder(new CompoundBorder(new EmptyBorder(25, 30, 25, 30),
                        new RoundedShadowBorder(14, new Color(0, 0, 0, 140))));
            }

            public void mouseExited(MouseEvent e) {
                card.setBorder(new CompoundBorder(new EmptyBorder(25, 30, 25, 30),
                        new RoundedShadowBorder()));
            }
        });

        return countLabel;
    }

    private void loadCountsFromDatabase() {
        String url = "jdbc:mysql://localhost:3306/hospital_db";
        String username = "root";
        String password = "";

        String queryDoctors = "SELECT COUNT(*) FROM doctor";
        String queryPatients = "SELECT COUNT(*) FROM patients";
        String queryMedicalRecords = "SELECT COUNT(*) FROM medical_records";
        String queryAppointments = "SELECT COUNT(*) FROM appointments";

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                try (Connection conn = DriverManager.getConnection(url, username, password)) {
                    try (PreparedStatement ps = conn.prepareStatement(queryDoctors);
                         ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) lblDoctorCount.setText(String.valueOf(rs.getInt(1)));
                    }
                    try (PreparedStatement ps = conn.prepareStatement(queryPatients);
                         ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) lblPatientCount.setText(String.valueOf(rs.getInt(1)));
                    }
                    try (PreparedStatement ps = conn.prepareStatement(queryMedicalRecords);
                         ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) lblMedicalRecordCount.setText(String.valueOf(rs.getInt(1)));
                    }
                    try (PreparedStatement ps = conn.prepareStatement(queryAppointments);
                         ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) lblAppointmentCount.setText(String.valueOf(rs.getInt(1)));
                    }
                    SwingUtilities.invokeLater(() -> statusLabel.setText("Data loaded successfully"));
                } catch (SQLException e) {
                    SwingUtilities.invokeLater(() -> {
                        statusLabel.setText("Error loading data");
                        JOptionPane.showMessageDialog(AdminDashboardModernUI.this,
                                "Failed to load data:\n" + e.getMessage(),
                                "DB Error", JOptionPane.ERROR_MESSAGE);
                    });
                }
                return null;
            }
        };

        worker.execute();
    }

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "MySQL JDBC Driver not found.", "Driver Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        SwingUtilities.invokeLater(AdminDashboardModernUI::new);
    }

    // Rounded border with shadow
    static class RoundedShadowBorder extends AbstractBorder {
        private final int arc = 20;
        private int shadowSize = 8;
        private Color shadowColor = new Color(0, 0, 0, 60);

        public RoundedShadowBorder() {}

        public RoundedShadowBorder(int shadowSize, Color shadowColor) {
            this.shadowSize = shadowSize;
            this.shadowColor = shadowColor;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(shadowColor);
            g2d.fillRoundRect(x + shadowSize / 2, y + shadowSize / 2, width - shadowSize, height - shadowSize, arc, arc);
            g2d.setColor(c.getBackground());
            g2d.fillRoundRect(x, y, width - shadowSize, height - shadowSize, arc, arc);
            g2d.setColor(new Color(200, 200, 200, 120));
            g2d.setStroke(new BasicStroke(1.2f));
            g2d.drawRoundRect(x, y, width - shadowSize, height - shadowSize, arc, arc);
            g2d.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(shadowSize, shadowSize, shadowSize, shadowSize);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.set(shadowSize, shadowSize, shadowSize, shadowSize);
            return insets;
        }
    }
}




