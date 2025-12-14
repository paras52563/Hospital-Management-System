/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Pharmacy;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ViewPrescriptionsPharmacyPanel extends JFrame {

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField tfSearch;
    private JButton btnSearch, btnRefresh, btnBack;
    private JLabel lblRowCount;

    public ViewPrescriptionsPharmacyPanel() {
        setTitle("All Prescriptions - Pharmacy Panel");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(245, 245, 245));
        setLayout(new BorderLayout());

        // Title
        JLabel title = new JLabel("View All Prescriptions", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(40, 55, 71));
        title.setBorder(new EmptyBorder(20, 10, 10, 10));
        add(title, BorderLayout.NORTH);

        // Top Panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        topPanel.setBackground(new Color(245, 245, 245));

        tfSearch = new JTextField("Search patient, medicine or doctor...", 25);
        tfSearch.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        tfSearch.setForeground(Color.GRAY);
        tfSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(5, 10, 5, 10)));

        tfSearch.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (tfSearch.getText().equals("Search patient, medicine or doctor...")) {
                    tfSearch.setText("");
                    tfSearch.setForeground(Color.BLACK);
                }
            }

            public void focusLost(FocusEvent e) {
                if (tfSearch.getText().isEmpty()) {
                    tfSearch.setText("Search patient, medicine or doctor...");
                    tfSearch.setForeground(Color.GRAY);
                }
            }
        });

        btnSearch = createButton("Search", new Color(41, 128, 185));
        btnRefresh = createButton("Refresh", new Color(149, 165, 166));
        btnBack = createButton("Back", new Color(231, 76, 60));

        topPanel.add(tfSearch);
        topPanel.add(btnSearch);
        topPanel.add(btnRefresh);
        topPanel.add(btnBack);
        add(topPanel, BorderLayout.BEFORE_FIRST_LINE);

        // Table
        tableModel = new DefaultTableModel(new String[]{
                "Prescription ID", "Patient Name", "Doctor Name", "Medicine Name", "Dosage", "Quantity", "Date", "Status"
        }, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        customizeTable();

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        add(scroll, BorderLayout.CENTER);

        // Footer
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        bottomPanel.setBackground(new Color(245, 245, 245));
        lblRowCount = new JLabel("Total Prescriptions: 0");
        lblRowCount.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        bottomPanel.add(lblRowCount);
        add(bottomPanel, BorderLayout.SOUTH);

        // Events
        btnSearch.addActionListener(e -> {
            String keyword = tfSearch.getText().trim();
            if (keyword.equals("Search patient, medicine or doctor...")) keyword = "";
            loadPrescriptions(keyword);
        });

        btnRefresh.addActionListener(e -> {
            tfSearch.setText("Search patient, medicine or doctor...");
            tfSearch.setForeground(Color.GRAY);
            loadPrescriptions("");
        });

        btnBack.addActionListener(e -> {
            dispose();
           new PharmacyDashboardModern().setVisible(true);
        });

        loadPrescriptions("");
        setVisible(true);
    }

    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(color.darker());
            }

            public void mouseExited(MouseEvent e) {
                btn.setBackground(color);
            }
        });

        return btn;
    }

    private void customizeTable() {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        table.setRowHeight(28);
        table.setSelectionBackground(new Color(224, 242, 255));
        table.setGridColor(new Color(220, 220, 220));
        table.setShowVerticalLines(false);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 16));
        header.setBackground(new Color(52, 152, 219));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(100, 40));
    }

    private void loadPrescriptions(String keyword) {
        tableModel.setRowCount(0);
        int count = 0;

        String sql = "SELECT pr.prescription_id, p.name AS patient_name, d.name AS doctor_name, " +
                     "m.name AS medicine_name, pr.dosage, pr.quantity, pr.prescription_date, pr.status " +
                     "FROM prescriptions pr " +
                     "JOIN patients p ON pr.patient_id = p.patient_id " +
                     "JOIN medicines m ON pr.medicine_id = m.medicine_id " +
                     "JOIN doctor d ON pr.doctor_id = d.doctor_id ";

        if (!keyword.isEmpty()) {
            sql += "WHERE p.name LIKE ? OR m.name LIKE ? OR d.name LIKE ? ";
        }

        sql += "ORDER BY pr.prescription_date DESC";

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital_db", "root", "");
             PreparedStatement pst = con.prepareStatement(sql)) {

            if (!keyword.isEmpty()) {
                String kw = "%" + keyword + "%";
                pst.setString(1, kw);
                pst.setString(2, kw);
                pst.setString(3, kw);
            }

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("prescription_id"),
                        rs.getString("patient_name"),
                        rs.getString("doctor_name"),
                        rs.getString("medicine_name"),
                        rs.getString("dosage"),
                        rs.getInt("quantity"),
                        rs.getDate("prescription_date"),
                        rs.getString("status")
                });
                count++;
            }

            lblRowCount.setText("Total Prescriptions: " + count);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading prescriptions.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ViewPrescriptionsPharmacyPanel::new);
    }
}

