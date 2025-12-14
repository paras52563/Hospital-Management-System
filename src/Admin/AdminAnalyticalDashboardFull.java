/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Admin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;
import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.category.*;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

public class AdminAnalyticalDashboardFull extends JFrame {

    private JPanel contentPane;
    private JTabbedPane tabbedPane;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                AdminAnalyticalDashboardFull frame = new AdminAnalyticalDashboardFull();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public AdminAnalyticalDashboardFull() {
        setTitle("Admin Analytical Dashboard");
        setSize(1400, 850);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // --- Main container ---
        contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(new Color(245, 247, 250));
        setContentPane(contentPane);

        // --- Header Bar ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(40, 55, 85));
        headerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel headerTitle = new JLabel("📊 Admin Analytical Dashboard");
        headerTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        headerTitle.setForeground(Color.WHITE);
        headerPanel.add(headerTitle, BorderLayout.WEST);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(new Color(220, 53, 69));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        headerPanel.add(logoutBtn, BorderLayout.EAST);

        contentPane.add(headerPanel, BorderLayout.NORTH);

        // --- Tabbed Charts ---
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        tabbedPane.setBackground(Color.WHITE);

        // Arrange charts in grid layout for each tab
        tabbedPane.addTab("Overview", createGridPanel(
                wrapChart("Doctors by Speciality", createBarChart(fetchDoctorsBySpeciality())),
                wrapChart("Appointments (Last 7 Days) - Bar", createBarChart(fetchAppointmentsPerDay())),
                wrapChart("Patients by Gender", createPieChart(fetchPatientsByGender())),
                wrapChart("Patient Age Distribution", createBarChart(fetchPatientAgeDistribution()))
        ));

        tabbedPane.addTab("Appointments Trend",
                wrapChart("Appointments Last 7 Days (Line)", createLineChart(fetchAppointmentsPerDay())));

        tabbedPane.addTab("Medical Records",
                wrapChart("Medical Records Last 5 Days", createAreaChart(fetchMedicalRecordsPerDay())));

        contentPane.add(tabbedPane, BorderLayout.CENTER);
    }

    // ===== Database Connection =====
    private Connection getConnection() throws Exception {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital_db", "root", "");
    }

    // ====== Fetch Data Methods ======
    private DefaultCategoryDataset fetchDoctorsBySpeciality() {
        DefaultCategoryDataset ds = new DefaultCategoryDataset();
        String sql = "SELECT speciality, COUNT(*) FROM doctor GROUP BY speciality";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) ds.addValue(rs.getInt(2), "Doctors", rs.getString(1));
        } catch (Exception e) { e.printStackTrace(); }
        return ds;
    }

    private DefaultCategoryDataset fetchAppointmentsPerDay() {
        DefaultCategoryDataset ds = new DefaultCategoryDataset();
        String sql = "SELECT DATE(appointment_date), COUNT(*) FROM appointments " +
                "WHERE appointment_date >= CURDATE() - INTERVAL 40 DAY GROUP BY DATE(appointment_date)";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) ds.addValue(rs.getInt(2), "Appointments", rs.getDate(1).toString());
        } catch (Exception e) { e.printStackTrace(); }
        return ds;
    }

    private DefaultPieDataset fetchPatientsByGender() {
        DefaultPieDataset ds = new DefaultPieDataset();
        String sql = "SELECT gender, COUNT(*) FROM patients GROUP BY gender";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) ds.setValue(rs.getString(1), rs.getInt(2));
        } catch (Exception e) { e.printStackTrace(); }
        return ds;
    }

    private DefaultCategoryDataset fetchPatientAgeDistribution() {
        DefaultCategoryDataset ds = new DefaultCategoryDataset();
        String sql = "SELECT " +
                "CASE " +
                " WHEN TIMESTAMPDIFF(YEAR, dob, CURDATE()) < 18 THEN '<18' " +
                " WHEN TIMESTAMPDIFF(YEAR, dob, CURDATE()) BETWEEN 18 AND 30 THEN '18-30' " +
                " WHEN TIMESTAMPDIFF(YEAR, dob, CURDATE()) BETWEEN 31 AND 45 THEN '31-45' " +
                " WHEN TIMESTAMPDIFF(YEAR, dob, CURDATE()) BETWEEN 46 AND 60 THEN '46-60' " +
                " ELSE '60+' END AS age_group, COUNT(*) " +
                "FROM patients GROUP BY age_group";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) ds.addValue(rs.getInt(2), "Patients", rs.getString(1));
        } catch (Exception e) { e.printStackTrace(); }
        return ds;
    }

    private DefaultCategoryDataset fetchMedicalRecordsPerDay() {
        DefaultCategoryDataset ds = new DefaultCategoryDataset();
        String sql = "SELECT DATE(record_date), COUNT(*) FROM medical_records " +
                "WHERE record_date >= CURDATE() - INTERVAL 40 DAY GROUP BY DATE(record_date)";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) ds.addValue(rs.getInt(2), "Records", rs.getDate(1).toString());
        } catch (Exception e) { e.printStackTrace(); }
        return ds;
    }

    // ====== Chart Wrappers ======
    private JPanel wrapChart(String title, JFreeChart chart) {
        ChartPanel cp = new ChartPanel(chart);
        cp.setBackground(Color.WHITE);
        cp.setPreferredSize(new Dimension(600, 400));

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel lbl = new JLabel(title, JLabel.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lbl.setForeground(new Color(60, 70, 90));
        lbl.setBorder(new EmptyBorder(0, 0, 10, 0));

        card.add(lbl, BorderLayout.NORTH);
        card.add(cp, BorderLayout.CENTER);
        return card;
    }

    private JPanel createGridPanel(JPanel... panels) {
        JPanel grid = new JPanel(new GridLayout(2, 2, 20, 20));
        grid.setBackground(new Color(245, 247, 250));
        for (JPanel p : panels) grid.add(p);
        return grid;
    }

    // ====== Chart Creators ======
    private JFreeChart createBarChart(DefaultCategoryDataset ds) {
        JFreeChart chart = ChartFactory.createBarChart("", "", "", ds);
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(new Color(200, 200, 200));
        plot.setOutlineVisible(false);

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setMaximumBarWidth(0.08);
        renderer.setSeriesPaint(0, new Color(79, 129, 189));
        return chart;
    }

    private JFreeChart createLineChart(DefaultCategoryDataset ds) {
        JFreeChart chart = ChartFactory.createLineChart("", "", "", ds);
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(new Color(200, 200, 200));

        LineAndShapeRenderer renderer = new LineAndShapeRenderer();
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesPaint(0, new Color(220, 20, 60));
        renderer.setSeriesStroke(0, new BasicStroke(2.0f));
        plot.setRenderer(renderer);
        return chart;
    }

    private JFreeChart createPieChart(DefaultPieDataset ds) {
        JFreeChart chart = ChartFactory.createPieChart("", ds, true, true, false);
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setLabelFont(new Font("Segoe UI", Font.PLAIN, 12));
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}: {1} ({2})"));

        // custom colors
        plot.setSectionPaint("Male", new Color(54, 162, 235));
        plot.setSectionPaint("Female", new Color(255, 99, 132));
        plot.setSectionPaint("Other", new Color(255, 206, 86));

        return chart;
    }

    private JFreeChart createAreaChart(DefaultCategoryDataset ds) {
        JFreeChart chart = ChartFactory.createAreaChart("", "", "", ds);
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(new Color(200, 200, 200));
        return chart;
    }
}















