/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package doctor;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

public class ViewDoctorAppointmentsModern extends JFrame {
    private JTable appointmentTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> statusFilter;
    private String doctorId;

    public ViewDoctorAppointmentsModern(String doctorId) {
        this.doctorId = doctorId;

        setTitle("Doctor - View Appointments");
        setSize(1100, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        initComponents();
        cancelPastAndTodayAppointments();
        fetchAppointments(null, null);

        setVisible(true);
    }

    private void initComponents() {
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        topPanel.setBackground(new Color(236, 240, 241));

        // Left panel for Back button and heading
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        leftPanel.setBackground(new Color(236, 240, 241));

        JButton backButton = new JButton("← Back");
        backButton.setFocusPainted(false);
        backButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        backButton.setBackground(new Color(52, 73, 94));
        backButton.setForeground(Color.WHITE);
        backButton.setPreferredSize(new Dimension(100, 30));

        backButton.addActionListener(e -> {
            dispose(); // Close this window
            new DoctorDashboard(doctorId).setVisible(true);
        });

        JLabel heading = new JLabel("My Appointments");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 24));

        leftPanel.add(backButton);
        leftPanel.add(heading);
        topPanel.add(leftPanel, BorderLayout.WEST);

        // Right panel for search
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setBackground(new Color(236, 240, 241));

        searchField = new JTextField("Search Patient ID or Name...", 20);
        searchField.setForeground(Color.GRAY);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Search Patient ID or Name...")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }

            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setForeground(Color.GRAY);
                    searchField.setText("Search Patient ID or Name...");
                }
            }
        });

        JButton btnSearch = new JButton("Search");
        btnSearch.addActionListener(e ->
                fetchAppointments(searchField.getText().trim(), (String) statusFilter.getSelectedItem()));

        searchPanel.add(searchField);
        searchPanel.add(btnSearch);
        topPanel.add(searchPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Table setup
        tableModel = new DefaultTableModel(new String[]{
                "Appointment ID", "Patient ID", "Date", "Time", "Reason", "Status", "Booked On"
        }, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        appointmentTable = new JTable(tableModel);
        appointmentTable.setRowHeight(30);
        appointmentTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        appointmentTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        appointmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        appointmentTable.setAutoCreateRowSorter(true);

        appointmentTable.getColumn("Status").setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String status = value.toString();
                switch (status) {
                    case "Confirmed":
                        c.setForeground(new Color(39, 174, 96));
                        break;
                    case "Cancelled":
                        c.setForeground(new Color(192, 57, 43));
                        break;
                    case "Scheduled":
                        c.setForeground(new Color(243, 156, 18));
                        break;
                    case "Rescheduled":
                        c.setForeground(new Color(41, 128, 185));
                        break;
                    default:
                        c.setForeground(Color.BLACK);
                }
                return c;
            }
        });

        appointmentTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = appointmentTable.getSelectedRow();
                    if (row != -1) {
                        String appointmentId = appointmentTable.getValueAt(row, 0).toString();
                        showAppointmentDetailsDialog(appointmentId);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(appointmentTable);
        add(scrollPane, BorderLayout.CENTER);

        // Bottom filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(new Color(236, 240, 241));
        filterPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        statusFilter = new JComboBox<>(new String[]{"All", "Scheduled", "Rescheduled", "Confirmed", "Cancelled"});
        statusFilter.addActionListener(e ->
                fetchAppointments(searchField.getText().trim(), (String) statusFilter.getSelectedItem()));

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> {
            cancelPastAndTodayAppointments(); // re-check cancel on refresh
            fetchAppointments(null, null);
        });

        filterPanel.add(new JLabel("Filter by Status: "));
        filterPanel.add(statusFilter);
        filterPanel.add(btnRefresh);

        add(filterPanel, BorderLayout.SOUTH);
    }

    private void fetchAppointments(String searchTerm, String status) {
        tableModel.setRowCount(0);
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital_db", "root", "");
             PreparedStatement ps = con.prepareStatement(buildQuery(searchTerm, status))) {

            int paramIndex = 1;
            if (searchTerm != null && !searchTerm.isEmpty() && !searchTerm.equals("Search Patient ID or Name...")) {
                ps.setString(paramIndex++, "%" + searchTerm + "%");
            }
            if (status != null && !status.equals("All")) {
                ps.setString(paramIndex++, status);
            }
            ps.setString(paramIndex, doctorId);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("appointment_id"));
                row.add(rs.getString("patient_id"));
                row.add(rs.getDate("appointment_date"));
                row.add(rs.getTime("appointment_time"));
                row.add(rs.getString("reason"));
                row.add(rs.getString("status"));
                row.add(rs.getTimestamp("booking_timestamp"));
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String buildQuery(String search, String status) {
        if (search != null && !search.isEmpty() && !search.equals("Search Patient ID or Name...")) {
            if (status != null && !status.equals("All")) {
                return "SELECT * FROM appointments WHERE patient_id LIKE ? AND status = ? AND doctor_id = ? ORDER BY appointment_date, appointment_time";
            } else {
                return "SELECT * FROM appointments WHERE patient_id LIKE ? AND doctor_id = ? ORDER BY appointment_date, appointment_time";
            }
        } else {
            if (status != null && !status.equals("All")) {
                return "SELECT * FROM appointments WHERE status = ? AND doctor_id = ? ORDER BY appointment_date, appointment_time";
            } else {
                return "SELECT * FROM appointments WHERE doctor_id = ? ORDER BY appointment_date, appointment_time";
            }
        }
    }

    private void showAppointmentDetailsDialog(String appointmentId) {
        JOptionPane.showMessageDialog(this,
                "Details for Appointment ID: " + appointmentId + "\n(More info can be added here)",
                "Appointment Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private void cancelPastAndTodayAppointments() {
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital_db", "root", "");
             PreparedStatement ps = con.prepareStatement(
                     "UPDATE appointments SET status = 'Cancelled' " +
                             "WHERE doctor_id = ? AND appointment_date < CURDATE() AND status IN ('Scheduled', 'Rescheduled')"
             )) {
            ps.setString(1, doctorId);
            int rows = ps.executeUpdate();
            System.out.println("Cancelled " + rows + " past appointments.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ViewDoctorAppointmentsModern("DOC2157"));
    }
}




