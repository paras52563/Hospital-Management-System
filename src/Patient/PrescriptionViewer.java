/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Patient;
import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.sql.*;
import java.util.*;
import java.util.List;
import java.text.SimpleDateFormat;

public class PrescriptionViewer extends JFrame {
    private final String patientId;
    private JPanel prescriptionPanel;
    private JDateChooser dateChooser; // Calendar picker

    public PrescriptionViewer(String patientId) {
        this.patientId = patientId;

        setTitle("💊 Prescription Viewer - " + patientId);
        setSize(1100, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(245, 248, 255));

        // ---------- HEADER ----------
        JPanel header = new JPanel();
        header.setBackground(new Color(245, 248, 255));
        header.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("💊 Prescription History", JLabel.CENTER);
        titleLabel.setForeground(new Color(30, 60, 90));
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel idLabel = new JLabel("Patient ID: " + patientId, JLabel.CENTER);
        idLabel.setForeground(new Color(100, 100, 100));
        idLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        idLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(titleLabel);
        header.add(Box.createVerticalStrut(5));
        header.add(idLabel);

        // ---------- DATE FILTER ----------
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        filterPanel.setBackground(new Color(245, 248, 255));

        JLabel filterLabel = new JLabel("📅 Filter by Date: ");
        filterLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("yyyy-MM-dd");

        JButton filterBtn = new JButton("🔍 Apply Filter");
        filterBtn.setBackground(new Color(40, 167, 69));
        filterBtn.setForeground(Color.WHITE);
        filterBtn.setFocusPainted(false);
        filterBtn.setFont(new Font("SansSerif", Font.BOLD, 13));

        JButton resetBtn = new JButton("⟳ Reset");
        resetBtn.setBackground(new Color(23, 162, 184));
        resetBtn.setForeground(Color.WHITE);
        resetBtn.setFocusPainted(false);
        resetBtn.setFont(new Font("SansSerif", Font.BOLD, 13));

        filterPanel.add(filterLabel);
        filterPanel.add(dateChooser);
        filterPanel.add(filterBtn);
        filterPanel.add(resetBtn);

        header.add(Box.createVerticalStrut(10));
        header.add(filterPanel);

        add(header, BorderLayout.NORTH);

        // ---------- PRESCRIPTION PANEL ----------
        prescriptionPanel = new JPanel();
        prescriptionPanel.setLayout(new BoxLayout(prescriptionPanel, BoxLayout.Y_AXIS));
        prescriptionPanel.setBackground(new Color(245, 248, 255));

        JScrollPane scroll = new JScrollPane(prescriptionPanel);
        scroll.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        // ---------- BACK BUTTON ----------
        JButton back = new JButton("🔙 Back to Dashboard");
        back.setBackground(new Color(220, 53, 69));
        back.setForeground(Color.WHITE);
        back.setFocusPainted(false);
        back.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        back.setFont(new Font("SansSerif", Font.BOLD, 14));
        back.addActionListener(e -> {
            dispose();
            new PatientDashboard(patientId).setVisible(true);
        });

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setBackground(new Color(245, 248, 255));
        bottom.add(back);
        add(bottom, BorderLayout.SOUTH);

        // Load all initially
        loadPrescriptions(null);

        // Filter button action
        filterBtn.addActionListener(e -> {
            if (dateChooser.getDate() == null) {
                JOptionPane.showMessageDialog(this, "Please select a date.", 
                        "Invalid Date", JOptionPane.WARNING_MESSAGE);
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String selectedDate = sdf.format(dateChooser.getDate());
                loadPrescriptions(selectedDate);
            }
        });

        // Reset filter
        resetBtn.addActionListener(e -> {
            dateChooser.setDate(null);
            loadPrescriptions(null);
        });

        setVisible(true);
    }

    // ---------- LOAD DATA ----------
    private void loadPrescriptions(String filterDate) {
        prescriptionPanel.removeAll();

        String sql = "SELECT p.prescription_id, p.medicine_id, p.dosage, p.doctor_id, p.prescription_date " +
                     "FROM prescriptions p WHERE p.patient_id = ? ";
        if (filterDate != null) sql += "AND p.prescription_date = ? ";
        sql += "ORDER BY p.prescription_date DESC";

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital_db", "root", "");
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, patientId);
            if (filterDate != null) ps.setString(2, filterDate);

            ResultSet rs = ps.executeQuery();
            Map<String, Map<String, List<Prescription>>> grouped = new LinkedHashMap<>();

            while (rs.next()) {
                String date = rs.getDate("prescription_date").toString();
                String doctorName = getDoctorName(con, rs.getString("doctor_id"));
                String medicineName = getMedicineName(con, rs.getString("medicine_id"));

                Prescription pres = new Prescription(
                        rs.getInt("prescription_id"),
                        doctorName,
                        rs.getString("medicine_id") + " - " + medicineName,
                        rs.getString("dosage"),
                        date
                );

                grouped.computeIfAbsent(date, k -> new LinkedHashMap<>())
                       .computeIfAbsent(doctorName, k -> new ArrayList<>())
                       .add(pres);
            }

            if (grouped.isEmpty()) {
                JLabel no = new JLabel("No prescriptions found.", JLabel.CENTER);
                no.setForeground(Color.GRAY);
                no.setAlignmentX(Component.CENTER_ALIGNMENT);
                no.setFont(new Font("SansSerif", Font.ITALIC, 16));
                prescriptionPanel.add(no);
            } else {
                for (String date : grouped.keySet()) {
                    JLabel dateLabel = new JLabel("📅 " + date);
                    dateLabel.setForeground(new Color(25, 118, 210));
                    dateLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
                    dateLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
                    prescriptionPanel.add(dateLabel);

                    Map<String, List<Prescription>> doctorsMap = grouped.get(date);
                    for (String doctor : doctorsMap.keySet()) {
                        prescriptionPanel.add(createDoctorCard(doctor, doctorsMap.get(doctor)));
                        prescriptionPanel.add(Box.createVerticalStrut(15));
                    }
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        prescriptionPanel.revalidate();
        prescriptionPanel.repaint();
    }

    // ---------- CREATE DOCTOR CARD ----------
    private JPanel createDoctorCard(String doctor, List<Prescription> prescriptions) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                new EmptyBorder(15, 20, 15, 20)
        ));

        JLabel doctorLabel = new JLabel("👨‍⚕️ " + doctor);
        doctorLabel.setForeground(new Color(25, 118, 210));
        doctorLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        doctorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(doctorLabel);
        card.add(Box.createVerticalStrut(10));

        for (Prescription pres : prescriptions) {
            JPanel presPanel = new JPanel(new GridLayout(1, 2, 20, 10));
            presPanel.setBackground(Color.WHITE);
            presPanel.add(createRow("💊 Medicine", pres.medicine));
            presPanel.add(createRow("🕒 Dosage", pres.dosage));
            presPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
            card.add(presPanel);
            card.add(Box.createVerticalStrut(5));
        }

        return card;
    }

    private JPanel createRow(String label, String value) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        JLabel l = new JLabel(label + ": ");
        l.setForeground(new Color(60, 60, 60));
        l.setFont(new Font("SansSerif", Font.BOLD, 14));
        JLabel v = new JLabel(value != null && !value.isEmpty() ? value : "—");
        v.setFont(new Font("SansSerif", Font.PLAIN, 14));
        p.add(l, BorderLayout.WEST);
        p.add(v, BorderLayout.CENTER);
        return p;
    }

    private String getDoctorName(Connection con, String doctorId) {
        try (PreparedStatement ps = con.prepareStatement("SELECT name, speciality FROM doctor WHERE doctor_id=?")) {
            ps.setString(1, doctorId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("name") + " (" + rs.getString("speciality") + ")";
        } catch (SQLException ignored) {}
        return doctorId;
    }

    private String getMedicineName(Connection con, String medicineId) {
        try (PreparedStatement ps = con.prepareStatement("SELECT name FROM medicines WHERE medicine_id=?")) {
            ps.setString(1, medicineId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("name");
        } catch (SQLException ignored) {}
        return medicineId;
    }

    private static class Prescription {
        int id;
        String doctor;
        String medicine;
        String dosage;
        String date;

        Prescription(int id, String doctor, String medicine, String dosage, String date) {
            this.id = id;
            this.doctor = doctor;
            this.medicine = medicine;
            this.dosage = dosage;
            this.date = date;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PrescriptionViewer("PAT97125"));
    }
}









