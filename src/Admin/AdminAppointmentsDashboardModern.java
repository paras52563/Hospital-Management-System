/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Admin;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class AdminAppointmentsDashboardModern extends JFrame {

    private JLabel lblConfirmed, lblScheduled, lblRescheduled;
    private JTextField txtSearch;
    private JTable table;
    private DefaultTableModel model;
    private TableRowSorter<DefaultTableModel> rowSorter;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/hospital_db";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";

    public AdminAppointmentsDashboardModern() {
        setTitle("Admin Appointments Dashboard");
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(245, 247, 250));

        // Top Panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(26, 35, 50));
        topPanel.setPreferredSize(new Dimension(0, 70));
        topPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        JButton btnBack = new JButton("\u2190 Back");
        btnBack.setFocusPainted(false);
        btnBack.setBackground(Color.WHITE);
        btnBack.setForeground(Color.BLACK);
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBack.setBorder(new EmptyBorder(5, 15, 5, 15));
        btnBack.addActionListener(e -> { 
            dispose();
            new AdminDashboardModernUI().setVisible(true);
        });
        topPanel.add(btnBack, BorderLayout.WEST);

        JLabel heading = new JLabel("Appointments Dashboard", SwingConstants.CENTER);
        heading.setForeground(Color.WHITE);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 28));
        topPanel.add(heading, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // Summary Panel
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 25));
        summaryPanel.setBackground(new Color(245, 247, 250));

        lblConfirmed = createSummaryCard(summaryPanel, "\u2714", "Confirmed", new Color(46, 204, 113));
        lblScheduled = createSummaryCard(summaryPanel, "\u23F3", "Scheduled", new Color(52, 152, 219));
        lblRescheduled = createSummaryCard(summaryPanel, "\u21bb", "Rescheduled", new Color(241, 196, 15));

        add(summaryPanel, BorderLayout.CENTER);

        // Search + Table
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBorder(new EmptyBorder(10, 20, 20, 20));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setOpaque(false);

        JLabel searchLabel = new JLabel("Search: ");
        txtSearch = new JTextField(25);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(180, 180, 180), 1, true),
                new EmptyBorder(5, 10, 5, 10)));

        searchPanel.add(searchLabel);
        searchPanel.add(txtSearch);
        centerPanel.add(searchPanel, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"ID", "Patient", "Doctor", "Date", "Time", "Status"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        table.getTableHeader().setBackground(new Color(30, 41, 59));
        table.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true));
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.SOUTH);

        rowSorter = new TableRowSorter<>(model);
        table.setRowSorter(rowSorter);

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filter(); }
            public void removeUpdate(DocumentEvent e) { filter(); }
            public void changedUpdate(DocumentEvent e) { filter(); }

            private void filter() {
                String text = txtSearch.getText().trim();
                rowSorter.setRowFilter(text.isEmpty() ? null : RowFilter.regexFilter("(?i)" + text));
            }
        });

        loadAppointments();
        setVisible(true);
    }

    private JLabel createSummaryCard(JPanel container, String icon, String title, Color bgColor) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(240, 120));
        card.setBackground(bgColor);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.LIGHT_GRAY, 1, true), new EmptyBorder(15, 20, 15, 20)));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

       /* JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        iconLabel.setForeground(Color.WHITE);
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);*/

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel countLabel = new JLabel("0");
        countLabel.setFont(new Font("Segoe UI Black", Font.BOLD, 32));
        countLabel.setForeground(Color.WHITE);
        countLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        //card.add(iconLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(countLabel);

        card.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                rowSorter.setRowFilter(RowFilter.regexFilter("^" + title + "$", 5));
            }
        });

        card.setComponentPopupMenu(createResetPopup());
        container.add(card);
        return countLabel;
    }

    private JPopupMenu createResetPopup() {
        JPopupMenu popup = new JPopupMenu();
        JMenuItem reset = new JMenuItem("Show All");
        reset.addActionListener(e -> rowSorter.setRowFilter(null));
        popup.add(reset);
        return popup;
    }

    private void loadAppointments() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            int confirmed = 0, scheduled = 0, rescheduled = 0;

            protected Void doInBackground() {
                try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                    String sql = """
                        SELECT a.appointment_id, p.name AS patient_name, d.name AS doctor_name,
                               a.appointment_date, a.appointment_time, a.status
                        FROM appointments a
                        JOIN patients p ON a.patient_id = p.patient_id
                        JOIN doctor d ON a.doctor_id = d.doctor_id
                        ORDER BY a.appointment_date DESC
                        """;
                    try (PreparedStatement ps = conn.prepareStatement(sql);
                         ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            String status = rs.getString("status").toLowerCase();
                            switch (status) {
                                case "confirmed" -> confirmed++;
                                case "scheduled" -> scheduled++;
                                case "rescheduled" -> rescheduled++;
                            }
                            model.addRow(new Object[]{
                                    rs.getInt("appointment_id"),
                                    rs.getString("patient_name"),
                                    rs.getString("doctor_name"),
                                    rs.getDate("appointment_date").toString(),
                                    rs.getTime("appointment_time").toString(),
                                    status.substring(0, 1).toUpperCase() + status.substring(1)
                            });
                        }
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(AdminAppointmentsDashboardModern.this,
                            "Error loading data: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                }
                return null;
            }

            protected void done() {
                lblConfirmed.setText(String.valueOf(confirmed));
                lblScheduled.setText(String.valueOf(scheduled));
                lblRescheduled.setText(String.valueOf(rescheduled));
            }
        };
        worker.execute();
    }

    public static void main(String[] args) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        SwingUtilities.invokeLater(AdminAppointmentsDashboardModern::new);
    }
}

