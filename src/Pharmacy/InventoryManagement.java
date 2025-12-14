/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Pharmacy;

import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class InventoryManagement extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private JTextField tfSearch;
    private JButton btnAdd, btnEdit, btnDelete, btnBack;
    private JButton btnExportExcel;

    public InventoryManagement() {
        setTitle("Pharmacy Inventory Management");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(33, 47, 61));
        titlePanel.setPreferredSize(new Dimension(getWidth(), 80));
        titlePanel.setLayout(new BorderLayout());

        JLabel title = new JLabel("Pharmacy Inventory Management", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 36));
        title.setForeground(Color.WHITE);
        titlePanel.add(title, BorderLayout.CENTER);

        // Search TextField with placeholder behavior
        tfSearch = new JTextField("Search by name, brand, or expiry date...");
        tfSearch.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        tfSearch.setForeground(Color.GRAY);
        tfSearch.setPreferredSize(new Dimension(400, 40));
        tfSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(33, 47, 61), 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        tfSearch.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (tfSearch.getText().equals("Search by name, brand, or expiry date...")) {
                    tfSearch.setText("");
                    tfSearch.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (tfSearch.getText().trim().isEmpty()) {
                    tfSearch.setText("Search by name, brand, or expiry date...");
                    tfSearch.setForeground(Color.GRAY);
                }
            }
        });

        tfSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (!tfSearch.getText().equals("Search by name, brand, or expiry date...")) {
                    loadMedicines(getSearchKeyword());
                }
            }
        });

        // Table Model and JTable
        model = new DefaultTableModel(new String[]{
            "ID", "Name", "Brand", "Qty", "Price", "Expiry"
        }, 0);
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
                        Date expiryDate = new SimpleDateFormat("yyyy-MM-dd").parse(expiryDateStr);
                        if (expiryDate.before(new Date())) {
                            c.setForeground(Color.RED); // expired: red text
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return c;
            }
        };

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(33, 47, 61), 2));

        // Buttons with style & hover effect
        btnAdd = createButton("Add", new Color(46, 204, 113));
        btnEdit = createButton("Edit", new Color(241, 196, 15));
        btnDelete = createButton("Delete", new Color(231, 76, 60));
        btnBack = createButton("Back", new Color(149, 165, 166));
        btnExportExcel = createButton("Export & Analyze", new Color(52, 152, 219));

        btnAdd.addActionListener(e -> new AddMedicineModern().setVisible(true));
        btnEdit.addActionListener(e -> editSelected());
        btnDelete.addActionListener(e -> deleteSelected());
        btnBack.addActionListener(e -> {
            dispose();
            new PharmacyDashboardModern().setVisible(true);
        });
        btnExportExcel.addActionListener(e -> exportToExcelAndAnalyze());

        // Main panel with GroupLayout
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);

        GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(titlePanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(tfSearch, GroupLayout.PREFERRED_SIZE, 400, GroupLayout.PREFERRED_SIZE)
                .addComponent(scroll)
                .addGroup(layout.createSequentialGroup()
                        .addComponent(btnAdd, 150, 150, 150)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnEdit, 150, 150, 150)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnDelete, 150, 150, 150)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnBack, 150, 150, 150)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnExportExcel, 180, 180, 180)
                )
        );

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(titlePanel, 80, 80, 80)
                .addGap(20)
                .addComponent(tfSearch, 40, 40, 40)
                .addGap(15)
                .addComponent(scroll)
                .addGap(20)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(btnAdd, 45, 45, 45)
                        .addComponent(btnEdit, 45, 45, 45)
                        .addComponent(btnDelete, 45, 45, 45)
                        .addComponent(btnBack, 45, 45, 45)
                        .addComponent(btnExportExcel, 45, 45, 45)
                )
        );

        setContentPane(panel);

        loadMedicines("");
        setVisible(true);
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.darker());
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });
        return button;
    }

    private String getSearchKeyword() {
        String text = tfSearch.getText().trim();
        if (text.equals("Search by name, brand, or expiry date...")) {
            return "";
        }
        return text;
    }

    private void loadMedicines(String keyword) {
        model.setRowCount(0);
        String query = "SELECT * FROM medicines WHERE name LIKE ? OR brand LIKE ? OR expiry_date LIKE ? ORDER BY medicine_id asc";
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital_db", "root", ""); PreparedStatement pst = con.prepareStatement(query)) {

            String likeKeyword = "%" + keyword + "%";
            pst.setString(1, likeKeyword);
            pst.setString(2, likeKeyword);
            pst.setString(3, likeKeyword);

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("medicine_id"));
                row.add(rs.getString("name"));
                row.add(rs.getString("brand"));
                row.add(rs.getInt("quantity"));
                row.add(rs.getDouble("price"));
                java.sql.Date expirySqlDate = rs.getDate("expiry_date");
                String expiryStr = expirySqlDate != null ? new SimpleDateFormat("yyyy-MM-dd").format(expirySqlDate) : "";
                row.add(expiryStr);
                model.addRow(row);
            }

            if (model.getRowCount() == 0) {
                Vector<Object> noDataRow = new Vector<>();
                noDataRow.add("No medicine found");
                int colCount = model.getColumnCount();
                for (int i = 1; i < colCount; i++) {
                    noDataRow.add("");
                }
                model.addRow(noDataRow);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading data.");
            e.printStackTrace();
        }
    }

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a medicine to edit.");
            return;
        }

        int id = (int) model.getValueAt(row, 0);
        int quantity = Integer.parseInt(model.getValueAt(row, 3).toString());
        double price = Double.parseDouble(model.getValueAt(row, 4).toString());

        String oldDateStr = model.getValueAt(row, 5).toString();
        Date oldDate = null;
        try {
            oldDate = new SimpleDateFormat("yyyy-MM-dd").parse(oldDateStr);
        } catch (Exception ex) {
            oldDate = new Date();
        }

        JTextField tfQty = new JTextField(String.valueOf(quantity));
        JTextField tfPrice = new JTextField(String.valueOf(price));
        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setDate(oldDate);
        dateChooser.setDateFormatString("yyyy-MM-dd");
        dateChooser.setMinSelectableDate(new Date());

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.add(new JLabel("Quantity:"));
        panel.add(tfQty);
        panel.add(new JLabel("Price:"));
        panel.add(tfPrice);
        panel.add(new JLabel("Expiry Date:"));
        panel.add(dateChooser);

        int result = JOptionPane.showConfirmDialog(this, panel, "Edit Medicine", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital_db", "root", ""); PreparedStatement pst = con.prepareStatement(
                    "UPDATE medicines SET quantity=?, price=?, expiry_date=? WHERE medicine_id=?")) {

                pst.setInt(1, Integer.parseInt(tfQty.getText()));
                pst.setDouble(2, Double.parseDouble(tfPrice.getText()));
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                pst.setString(3, sdf.format(dateChooser.getDate()));
                pst.setInt(4, id);
                pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "Updated successfully.");
                loadMedicines(getSearchKeyword());  // Refresh after edit
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error updating record.");
                e.printStackTrace();
            }
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a medicine to delete.");
            return;
        }

        int id = (int) model.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure to delete this medicine?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital_db", "root", ""); PreparedStatement pst = con.prepareStatement("DELETE FROM medicines WHERE medicine_id=?")) {

                pst.setInt(1, id);
                pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "Deleted successfully.");
                loadMedicines(getSearchKeyword());  // Refresh after delete
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error deleting record.");
                e.printStackTrace();
            }
        }
    }

    private void exportToExcelAndAnalyze() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("InventoryData.xlsx"));
        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Inventory");

                // Create header
                Row header = sheet.createRow(0);
                for (int i = 0; i < model.getColumnCount(); i++) {
                    Cell cell = header.createCell(i);
                    cell.setCellValue(model.getColumnName(i));
                    // Style header cell bold
                    CellStyle style = workbook.createCellStyle();
                    /*Font font = workbook.createFont();
                    font.setBold(true);
                    style.setFont(font);*/
                    cell.setCellStyle(style);
                }

                // Fill data
                for (int i = 0; i < model.getRowCount(); i++) {
                    Row row = sheet.createRow(i + 1);
                    for (int j = 0; j < model.getColumnCount(); j++) {
                        Object value = model.getValueAt(i, j);
                        Cell cell = row.createCell(j);

                        if (value == null) {
                            cell.setCellValue("");
                        } else {
                            // Write numbers as numeric cells
                            if (j == 0) { // ID integer
                                cell.setCellValue(Integer.parseInt(value.toString()));
                            } else if (j == 3) { // Quantity integer
                                cell.setCellValue(Integer.parseInt(value.toString()));
                            } else if (j == 4) { // Price double
                                cell.setCellValue(Double.parseDouble(value.toString()));
                            } else if (j == 5) { // Expiry date string
                                // Try parse date and write as date cell
                                try {
                                    Date date = new SimpleDateFormat("yyyy-MM-dd").parse(value.toString());
                                    CellStyle dateStyle = workbook.createCellStyle();
                                    CreationHelper createHelper = workbook.getCreationHelper();
                                    dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-MM-dd"));
                                    cell.setCellValue(date);
                                    cell.setCellStyle(dateStyle);
                                } catch (Exception ex) {
                                    cell.setCellValue(value.toString());
                                }
                            } else {
                                cell.setCellValue(value.toString());
                            }
                        }
                    }
                }

                // Autosize columns for better appearance
                for (int i = 0; i < model.getColumnCount(); i++) {
                    sheet.autoSizeColumn(i);
                }

                // Add Analysis Sheet
                Sheet analysisSheet = workbook.createSheet("Analysis");

                // Title cells
                Row row0 = analysisSheet.createRow(0);
                Cell cellA0 = row0.createCell(0);
                cellA0.setCellValue("Total Quantity:");

                Row row1 = analysisSheet.createRow(1);
                Cell cellA1 = row1.createCell(0);
                cellA1.setCellValue("Average Price:");

                // Add formulas for total quantity and average price referencing Inventory sheet
                int dataRowCount = model.getRowCount();
                if (dataRowCount > 0) {
                    // Sum quantity column D (index 3) from Inventory sheet, rows 2 to (rowCount+1)
                    Cell cellB0 = row0.createCell(1);
                    String qtyFormula = "SUM(Inventory!D2:D" + (dataRowCount + 1) + ")";
                    cellB0.setCellFormula(qtyFormula);

                    // Average price column E (index 4)
                    Cell cellB1 = row1.createCell(1);
                    String priceFormula = "AVERAGE(Inventory!E2:E" + (dataRowCount + 1) + ")";
                    cellB1.setCellFormula(priceFormula);
                } else {
                    // No data case
                    analysisSheet.createRow(2).createCell(0).setCellValue("No data to analyze.");
                }

                // Autosize analysis sheet columns
                analysisSheet.autoSizeColumn(0);
                analysisSheet.autoSizeColumn(1);

                // Write to file
                try (FileOutputStream out = new FileOutputStream(fileToSave)) {
                    workbook.write(out);
                }

                JOptionPane.showMessageDialog(this, "Exported and Analyzed successfully!\nOpen the Excel file and press F9 to refresh formulas if needed.");

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error exporting to Excel.");
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(InventoryManagement::new);
    }
}
