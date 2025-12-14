/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package doctor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.Vector;

public class DoctorConfirmAppointment extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private JButton confirmBtn, refreshBtn, backBtn;
    private String doctorId;

    public DoctorConfirmAppointment(String doctorId) {
        this.doctorId = doctorId;
        setTitle("Confirm Today's Appointments");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        initUI();
        fetchTodayAppointments();

        setVisible(true);
    }

    private void initUI() {
        // Header panel with back button and title
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(52, 152, 219));
        topPanel.setPreferredSize(new Dimension(getWidth(), 60));

        backBtn = new JButton("← Back");
        backBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        backBtn.setBackground(new Color(41, 128, 185));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);
        backBtn.setBorderPainted(false);
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> {
            dispose();
            new DoctorDashboard(doctorId).setVisible(true);
                
        });

        JLabel header = new JLabel("Today's Appointments (Scheduled / Rescheduled)", JLabel.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 20));
        header.setForeground(Color.WHITE);

        topPanel.add(backBtn, BorderLayout.WEST);
        topPanel.add(header, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{
                "Appointment ID", "Patient ID", "Date", "Time", "Reason", "Status"
        }, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        confirmBtn = new JButton("Confirm Selected Appointment");
        refreshBtn = new JButton("Refresh");

        confirmBtn.setBackground(new Color(46, 204, 113));
        confirmBtn.setForeground(Color.WHITE);
        confirmBtn.setFocusPainted(false);

        refreshBtn.setBackground(new Color(52, 73, 94));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFocusPainted(false);

        confirmBtn.addActionListener(e -> confirmAppointment());
        refreshBtn.addActionListener(e -> fetchTodayAppointments());

        bottomPanel.add(refreshBtn);
        bottomPanel.add(confirmBtn);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void fetchTodayAppointments() {
        model.setRowCount(0);
        LocalDate today = LocalDate.now();
        String todayStr = today.toString();

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital_db", "root", "");
             PreparedStatement ps = con.prepareStatement(
                     "SELECT * FROM appointments WHERE doctor_id = ? AND appointment_date = ? AND status IN ('Scheduled', 'Rescheduled') ORDER BY appointment_time"
             )) {

            ps.setString(1, doctorId);
            ps.setString(2, todayStr);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("appointment_id"));
                row.add(rs.getString("patient_id"));
                row.add(rs.getDate("appointment_date"));
                row.add(rs.getTime("appointment_time"));
                row.add(rs.getString("reason"));
                row.add(rs.getString("status"));
                model.addRow(row);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching appointments: " + ex.getMessage());
        }
    }

    private void confirmAppointment() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment to confirm.");
            return;
        }

        int appointmentId = (int) model.getValueAt(selectedRow, 0);
        String patientId = (String) model.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this, "Confirm appointment ID " + appointmentId + "?", "Confirm Appointment", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital_db", "root", "");
                 PreparedStatement ps = con.prepareStatement("UPDATE appointments SET status = 'Confirmed' WHERE appointment_id = ?")) {

                ps.setInt(1, appointmentId);
                int updated = ps.executeUpdate();

                if (updated > 0) {
                    JOptionPane.showMessageDialog(this, "Appointment confirmed successfully.");
                    fetchTodayAppointments();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to confirm appointment.");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error confirming appointment: " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DoctorConfirmAppointment("DOC2157"));
    }
}

