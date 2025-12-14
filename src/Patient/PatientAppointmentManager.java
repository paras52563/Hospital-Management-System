/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Patient;

import com.toedter.calendar.JDateChooser;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.awt.Font;

public class PatientAppointmentManager extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private String loggedInPatientId;
    private String loggedInPatientEmail;
    private JButton btnReschedule, btnCancel;

    public PatientAppointmentManager(String patientId) {
        this.loggedInPatientId = patientId;
        this.loggedInPatientEmail = fetchPatientEmail(patientId);

        setTitle("🗓️ My Appointments");
        setSize(1100, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 🔹 Gradient background panel
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0,
                        new Color(240, 248, 255),
                        0, getHeight(),
                        new Color(220, 235, 245));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(mainPanel);

        // 🔹 Top bar with back button + title
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JButton btnBack = new JButton("← Back");
        styleButton(btnBack, new Color(200, 200, 200), Color.BLACK);
        btnBack.setPreferredSize(new Dimension(100, 35));
        btnBack.addActionListener(e -> {
            dispose();
            new PatientDashboard(loggedInPatientId).setVisible(true);
        });

        JLabel titleLabel = new JLabel("📅 My Appointments", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(40, 55, 71));

        topPanel.add(btnBack, BorderLayout.WEST);
        topPanel.add(titleLabel, BorderLayout.CENTER);

        // 🔹 Table
        model = new DefaultTableModel(new String[]{
            "Appointment ID", "Doctor ID", "Doctor Name", "Speciality",
            "Date", "Time", "Reason", "Status"
        }, 0);

        table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(28);
        table.setGridColor(new Color(220, 220, 220));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(100, 149, 237));
        header.setForeground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new LineBorder(new Color(200, 200, 200), 1, true));

        // 🔹 Buttons panel
        btnReschedule = new JButton("⏳ Reschedule");
        btnCancel = new JButton("❌ Cancel");

        styleButton(btnReschedule, new Color(0, 123, 255), Color.WHITE);
        styleButton(btnCancel, new Color(220, 53, 69), Color.WHITE);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.add(btnReschedule);
        buttonPanel.add(btnCancel);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scroll, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Load Data
        checkAndCancelPastAppointments();
        loadAppointments();

        // Actions
        btnCancel.addActionListener(e -> cancelAppointment());
        btnReschedule.addActionListener(e -> rescheduleAppointment());

        setVisible(true);
    }

    // 🔹 Reusable button style
    private void styleButton(JButton button, Color bg, Color fg) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bg);
        button.setForeground(fg);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(8, 15, 8, 15));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bg.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bg);
            }
        });
    }

    // ------------------------ EXISTING METHODS ------------------------
    private String fetchPatientEmail(String patientId) {
        String email = "";
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital_db", "root", "");
             PreparedStatement ps = con.prepareStatement("SELECT email FROM patients WHERE patient_id = ?")) {
            ps.setString(1, patientId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                email = rs.getString("email");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return email;
    }

    private void checkAndCancelPastAppointments() {
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital_db", "root", "");
             PreparedStatement ps = con.prepareStatement(
                     "UPDATE appointments SET status = 'Cancelled' WHERE patient_id = ? AND appointment_date < CURDATE() AND status = 'Scheduled'")) {
            ps.setString(1, loggedInPatientId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadAppointments() {
        model.setRowCount(0);
        String query = "SELECT a.appointment_id, a.doctor_id, d.name AS doctor_name, d.speciality, "
                + "a.appointment_date, a.appointment_time, a.reason, a.status "
                + "FROM appointments a JOIN doctor d ON a.doctor_id = d.doctor_id "
                + "WHERE a.patient_id = ?";
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital_db", "root", "");
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, loggedInPatientId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("appointment_id"),
                        rs.getString("doctor_id"),
                        rs.getString("doctor_name"),
                        rs.getString("speciality"),
                        rs.getDate("appointment_date"),
                        rs.getTime("appointment_time"),
                        rs.getString("reason"),
                        rs.getString("status")
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load appointments.");
        }
    }

    private void cancelAppointment() {
    int row = table.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(this, "Select an appointment to cancel.");
        return;
    }
    
    String status = model.getValueAt(row, 7).toString();
    if (status.equalsIgnoreCase("Cancelled")) {
        JOptionPane.showMessageDialog(this, "Appointment is already cancelled.");
        return;
    }
    
    int id = (int) model.getValueAt(row, 0);
    String doctorId = model.getValueAt(row, 1).toString();
    String date = model.getValueAt(row, 4).toString();
    String time = model.getValueAt(row, 5).toString();
    
    int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to cancel this appointment?", "Confirm", JOptionPane.YES_NO_OPTION);
    if (confirm != JOptionPane.YES_OPTION) {
        return;
    }
    
    try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital_db", "root", ""); 
         PreparedStatement ps = con.prepareStatement("UPDATE appointments SET status = 'Cancelled' WHERE appointment_id = ?")) {
        ps.setInt(1, id);
        ps.executeUpdate();
        generatePDF(id, doctorId, date, time, "Cancelled");
        loadAppointments();
        JOptionPane.showMessageDialog(this, "Appointment cancelled. PDF generated.");
    } catch (Exception ex) {
        ex.printStackTrace();
    }
}

private void rescheduleAppointment() {
    int row = table.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(this, "Select an appointment to reschedule.");
        return;
    }

    String status = model.getValueAt(row, 7).toString();
    if (status.equalsIgnoreCase("Cancelled")) {
        JOptionPane.showMessageDialog(this, "Cancelled appointment cannot be rescheduled.");
        return;
    }

    int id = (int) model.getValueAt(row, 0);
    String doctorId = model.getValueAt(row, 1).toString();

    // 🔹 Create a panel for date & time selection
    JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));

    // Date chooser
    JDateChooser dateChooser = new JDateChooser();
    dateChooser.setDateFormatString("yyyy-MM-dd");
    panel.add(new JLabel("Select Date:"));
    panel.add(dateChooser);

    // Time chooser (hour + minute combo)
    JComboBox<String> hourCombo = new JComboBox<>();
    for (int i = 9; i <= 18; i++) { // Doctor available 9 AM to 6 PM
        hourCombo.addItem(String.format("%02d", i));
    }

    JComboBox<String> minuteCombo = new JComboBox<>(new String[]{"00", "15", "30", "45"});

    JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
    timePanel.add(hourCombo);
    timePanel.add(new JLabel(":"));
    timePanel.add(minuteCombo);

    panel.add(new JLabel("Select Time:"));
    panel.add(timePanel);

    int result = JOptionPane.showConfirmDialog(this, panel, "Reschedule Appointment",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

    if (result != JOptionPane.OK_OPTION) {
        return;
    }

    try {
        // Get selected date
        java.util.Date utilDate = dateChooser.getDate();
        if (utilDate == null) {
            JOptionPane.showMessageDialog(this, "Please select a valid date.");
            return;
        }
        LocalDate newDate = LocalDate.parse(new SimpleDateFormat("yyyy-MM-dd").format(utilDate));

        // Get selected time
        String newTime = hourCombo.getSelectedItem() + ":" + minuteCombo.getSelectedItem() + ":00";
        LocalTime inputTime = LocalTime.parse(newTime);

        // Validations
        if (newDate.isBefore(LocalDate.now())) {
            JOptionPane.showMessageDialog(this, "Cannot reschedule to a past date.");
            return;
        }
        if (inputTime.isBefore(LocalTime.of(9, 0)) || inputTime.isAfter(LocalTime.of(18, 0))) {
            JOptionPane.showMessageDialog(this, "Time must be between 09:00 and 18:00.");
            return;
        }
        if (!isTimeSlotAvailable(doctorId, newDate.toString(), newTime)) {
            JOptionPane.showMessageDialog(this, "Time slot not available.");
            return;
        }

        // 🔹 Update database
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital_db", "root", "");
             PreparedStatement ps = con.prepareStatement(
                     "UPDATE appointments SET appointment_date = ?, appointment_time = ?, status = 'Rescheduled' WHERE appointment_id = ?")) {
            ps.setString(1, newDate.toString());
            ps.setString(2, newTime);
            ps.setInt(3, id);
            ps.executeUpdate();

            generatePDF(id, doctorId, newDate.toString(), newTime, "Rescheduled");
            loadAppointments();
            JOptionPane.showMessageDialog(this, "Appointment rescheduled. PDF generated.");
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Invalid date or time.");
        e.printStackTrace();
    }
}


private boolean isTimeSlotAvailable(String doctorId, String date, String time) {
    try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital_db", "root", ""); 
         PreparedStatement ps = con.prepareStatement(
            "SELECT * FROM appointments WHERE doctor_id = ? AND appointment_date = ? AND appointment_time = ? AND status != 'Cancelled'")) {
        ps.setString(1, doctorId);
        ps.setString(2, date);
        ps.setString(3, time);
        ResultSet rs = ps.executeQuery();
        return !rs.next();
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}

private void generatePDF(int appointmentId, String doctorId, String date, String time, String status) {
    try {
        String fileName = "Appointment_" + appointmentId + "_" + status + ".pdf";
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(fileName));
        document.open();
        document.add(new Paragraph("Appointment Details"));
        document.add(new Paragraph("Generated On: " + new java.util.Date()));
        document.add(new Paragraph("Patient ID: " + loggedInPatientId));
        document.add(new Paragraph("Appointment ID: " + appointmentId));
        document.add(new Paragraph("Doctor ID: " + doctorId));
        document.add(new Paragraph("Date: " + date));
        document.add(new Paragraph("Time: " + time));
        document.add(new Paragraph("Status: " + status));
        document.close();
    } catch (Exception e) {
        e.printStackTrace();
    }
}


    public static void main(String[] args) {
        new PatientAppointmentManager("PAT97125");
    }
}


