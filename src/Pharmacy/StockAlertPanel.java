/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Pharmacy;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class StockAlertPanel extends JFrame {
    private JTextArea alertArea;
    private JButton btnRefresh, btnBack;
    private Connection con;
    

    public StockAlertPanel() {
        setTitle("Medicine Stock Alerts");
        /*setSize(950, 600);
        setLocationRelativeTo(null);*/
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Main panel with padding and white background
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        // Title label with clean font & color
        JLabel titleLabel = new JLabel("Low Stock Medicines", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 28));
        titleLabel.setForeground(new Color(40, 55, 71));
        titleLabel.setBorder(new EmptyBorder(10, 0, 10, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Text area for alerts
        alertArea = new JTextArea();
        alertArea.setEditable(false);
        alertArea.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        alertArea.setForeground(new Color(65, 65, 65));
        alertArea.setBackground(new Color(245, 245, 245));  // Light gray
        alertArea.setBorder(BorderFactory.createLineBorder(new Color(210, 210, 210)));
        alertArea.setLineWrap(true);
        alertArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(alertArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(12);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Buttons panel: Back on left, Refresh on right
        JPanel btnPanel = new JPanel(new BorderLayout(10, 0));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        btnBack = createButton("Back", new Color(220, 53, 69));        // Bootstrap Danger red
        btnRefresh = createButton("Refresh", new Color(40, 167, 69));  // Bootstrap Success green

        // Panel for back button (left aligned)
        JPanel leftBtnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftBtnPanel.setBackground(Color.WHITE);
        leftBtnPanel.add(btnBack);

        // Panel for refresh button (right aligned)
        JPanel rightBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightBtnPanel.setBackground(Color.WHITE);
        rightBtnPanel.add(btnRefresh);

        btnPanel.add(leftBtnPanel, BorderLayout.WEST);
        btnPanel.add(rightBtnPanel, BorderLayout.EAST);

        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        add(mainPanel);

        connect();
        loadLowStockMedicines();

        btnRefresh.addActionListener(e -> loadLowStockMedicines());
        btnBack.addActionListener(e -> {
            dispose();
            new PharmacyDashboardModern().setVisible(true);
        });

        setVisible(true);
    }

    private JButton createButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 16));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void connect() {
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital_db", "root", "");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database connection error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

  private void loadLowStockMedicines() {
    if (con == null) return;

    try {
        String sql = "SELECT brand, name, quantity FROM medicines WHERE quantity <= 10 ORDER BY quantity ASC";
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        StringBuilder sb = new StringBuilder();

        // Header row with fixed width columns
        String header = String.format("%-20s %-30s %10s%n", "Brand", "Medicine Name", "Quantity");
        sb.append(header);
        sb.append("--------------------------------------------------------------\n");

        boolean hasData = false;
        while (rs.next()) {
            hasData = true;
            String brandName = rs.getString("brand");
            String medName = rs.getString("name");
            int qty = rs.getInt("quantity");

            // Format each row with fixed widths:
            String row = String.format("%-20s %-30s %10d%n", brandName, medName, qty);
            sb.append(row);
        }

        if (!hasData) {
            alertArea.setText("✅ All medicines are sufficiently stocked.");
        } else {
            alertArea.setText(sb.toString());
        }

        // Set monospaced font so columns align
        alertArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 16));

        // Disable line wrap to keep table formatting intact
        alertArea.setLineWrap(false);
        alertArea.setWrapStyleWord(false);

        // Remove text area margins so text fills entire area nicely
        alertArea.setMargin(new Insets(10, 10, 10, 10));

    } catch (SQLException e) {
        alertArea.setText("⚠️ Error loading stock data.");
        e.printStackTrace();
    }
}


    

    private void onBackClicked() {
        // Implement back action here
        // For example, dispose this window or navigate to previous screen
        JOptionPane.showMessageDialog(this, "Back button clicked");
        dispose();
        // or open previous window here
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(StockAlertPanel::new);
    }
}
