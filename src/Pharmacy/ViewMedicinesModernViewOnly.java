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
import java.text.SimpleDateFormat;
import java.util.Vector;

public class ViewMedicinesModernViewOnly extends JFrame {

    private JTable table;
    private JTextField tfSearch;
    private JButton btnSearch, btnBack;
    private DefaultTableModel model;

    public ViewMedicinesModernViewOnly() {
        setTitle("Pharmacy Inventory - View Only");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // setSize(950, 600); // no longer needed with fullscreen
        setExtendedState(JFrame.MAXIMIZED_BOTH);  // <-- Fullscreen
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(245, 245, 245));

        // Title
        JLabel title = new JLabel("View Medicine Inventory", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(40, 55, 71));

        // Search field styled like InventoryManagement
        tfSearch = new JTextField("Search by medicine or brand...");
        tfSearch.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        tfSearch.setForeground(Color.GRAY);
        tfSearch.setPreferredSize(new Dimension(400, 40));
        tfSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(33, 47, 61), 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        tfSearch.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (tfSearch.getText().equals("Search by medicine or brand...")) {
                    tfSearch.setText("");
                    tfSearch.setForeground(Color.BLACK);
                }
            }

            public void focusLost(FocusEvent e) {
                if (tfSearch.getText().trim().isEmpty()) {
                    tfSearch.setText("Search by medicine or brand...");
                    tfSearch.setForeground(Color.GRAY);
                }
            }
        });

        // Search button styled like InventoryManagement "Add" button
        btnSearch = createButton("Search", new Color(46, 204, 113)); // green like Add button
        btnSearch.setPreferredSize(new Dimension(120, 40));
        btnSearch.addActionListener(e -> {
            String keyword = tfSearch.getText().trim();
            if (keyword.equals("Search by medicine or brand...")) keyword = "";
            loadMedicines(keyword);
        });

        // Table and model
        model = new DefaultTableModel(new String[]{"ID", "Name", "Brand", "Quantity", "Price", "Expiry Date"}, 0);
        table = new JTable(model) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    comp.setBackground(row % 2 == 0 ? new Color(245, 245, 245) : Color.WHITE);
                } else {
                    comp.setBackground(new Color(204, 229, 255));
                }
                return comp;
            }
        };
        customizeTable();

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(33, 47, 61), 2)); // border color like InventoryManagement

        // Back button styled like InventoryManagement Back button
        btnBack = createButton("Back", new Color(149, 165, 166));
        btnBack.setPreferredSize(new Dimension(150, 45));
        btnBack.addActionListener(e -> {
            dispose();
            new PharmacyDashboardModern().setVisible(true);
        });

        // Layout with GroupLayout for responsiveness
        JPanel panel = new JPanel();
        panel.setBackground(new Color(245, 245, 245));
        GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        // Horizontal grouping
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(title, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(tfSearch, GroupLayout.PREFERRED_SIZE, 400, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(btnSearch, GroupLayout.PREFERRED_SIZE, 120, GroupLayout.PREFERRED_SIZE))
                .addComponent(scroll, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE, Short.MAX_VALUE)
                .addComponent(btnBack, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE)
        );

        // Vertical grouping
        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addGap(20)
                .addComponent(title, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                .addGap(20)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(tfSearch, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSearch, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE))
                .addGap(20)
                .addComponent(scroll, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE, Short.MAX_VALUE)
                .addGap(20)
                .addComponent(btnBack, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
                .addGap(20)
        );

        setContentPane(panel);

        loadMedicines("");
        setVisible(true);
    }

    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        return button;
    }

    private void customizeTable() {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        table.setRowHeight(32);
        table.setSelectionBackground(new Color(204, 229, 255));
        table.setGridColor(new Color(220, 220, 220));
        table.setShowVerticalLines(false);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 16));
        header.setBackground(new Color(33, 47, 61));  // dark border color for header bg
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
   table = new JTable(model) {
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    comp.setBackground(row % 2 == 0 ? new Color(245, 245, 245) : Color.WHITE);
                } else {
                    comp.setBackground(new Color(204, 229, 255));
                }
                return comp;
            }
        };

        table = new JTable(model) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);

                // Keep default font and size
                table.setRowHeight(32);
                table.setRowHeight(32);
                table.setFont(new Font("Segoe UI", Font.PLAIN, 15));
                table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 16));
                table.setGridColor(new Color(220, 220, 220));
                table.setSelectionForeground(Color.BLACK);
                table.setSelectionBackground(new Color(204, 229, 255));
                table.setFillsViewportHeight(true);
                c.setFont(new Font("Segoe UI", Font.PLAIN, 15)); // ensures font size stays same as table's font
                c.setForeground(Color.BLACK); // default text color

                String expiryDateStr = getValueAt(row, 5).toString(); // expiry_date column
                try {
                    if (!expiryDateStr.isEmpty()) {
                        java.util.Date expiryDate = new SimpleDateFormat("yyyy-MM-dd").parse(expiryDateStr);
                        if (expiryDate.before(new java.util.Date())) {
                            c.setForeground(Color.RED); // expired: red text
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return c;
            }
        };

       
        
    }

   private void loadMedicines(String keyword) {
    model.setRowCount(0);
    try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital_db", "root", "");
         PreparedStatement pst = con.prepareStatement(
                 "SELECT * FROM medicines WHERE name LIKE ? OR brand LIKE ? ORDER BY medicine_id ASC")) {

        String searchPattern = "%" + keyword + "%";
        pst.setString(1, searchPattern);
        pst.setString(2, searchPattern);

        ResultSet rs = pst.executeQuery();
        boolean hasResults = false;

        while (rs.next()) {
            hasResults = true;
            model.addRow(new Object[]{
                rs.getInt("medicine_id"),
                rs.getString("name"),
                rs.getString("brand"),
                rs.getInt("quantity"),
                rs.getDouble("price"),
                rs.getDate("expiry_date")
            });
        }

        if (!hasResults) {
            Vector<Object> noDataRow = new Vector<>();
            noDataRow.add("No medicine found");
            for (int i = 1; i < model.getColumnCount(); i++) {
                noDataRow.add("");  // empty cells
            }
            model.addRow(noDataRow);
        }

    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Error loading data: " + ex.getMessage());
    }
}




    public static void main(String[] args) {
        SwingUtilities.invokeLater(ViewMedicinesModernViewOnly::new);
    }
}

