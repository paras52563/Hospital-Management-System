/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Pharmacy;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.sql.*;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Vector;
import javax.print.PrintService;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class ExpiredMedicinesModule extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private JButton btnRefresh, btnBack, btnPrint, btnExportPDF;
    private Connection con;

    public ExpiredMedicinesModule() {
        setTitle("Expired Medicines");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // setSize(950, 600); // no longer needed with fullscreen
        setExtendedState(JFrame.MAXIMIZED_BOTH);  // <-- Fullscreen
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(Color.WHITE);

        // Title label
        JLabel titleLabel = new JLabel("List of Expired Medicines", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 28));
        titleLabel.setForeground(new Color(33, 47, 61));
        titleLabel.setBorder(new EmptyBorder(10, 0, 20, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Table setup
        model = new DefaultTableModel(new String[]{"Medicine ID", "Name", "Quantity", "Price", "Expiry Date"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false; // Non-editable
            }
        };
        table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        table.setRowHeight(30);
        table.setSelectionBackground(new Color(174, 214, 241));
        table.getTableHeader().setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
        table.getTableHeader().setBackground(new Color(44, 62, 80));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Buttons panel at bottom
        JPanel btnPanel = new JPanel(new BorderLayout());
        btnPanel.setBackground(Color.WHITE);

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setBackground(Color.WHITE);
        btnBack = new JButton("Back");
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnBack.setBackground(new Color(231, 76, 60));
        btnBack.setForeground(Color.WHITE);
        btnBack.setFocusPainted(false);
        btnBack.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        leftPanel.add(btnBack);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBackground(Color.WHITE);
        btnRefresh = new JButton("Refresh");
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnRefresh.setBackground(new Color(40, 167, 69));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFocusPainted(false);
        btnRefresh.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // In rightPanel (where btnRefresh is)
        btnPrint = new JButton("Print");
        btnPrint.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnPrint.setBackground(new Color(52, 152, 219)); // nice blue color
        btnPrint.setForeground(Color.WHITE);
        btnPrint.setFocusPainted(false);
        btnPrint.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnExportPDF = new JButton("Export PDF");
        btnExportPDF.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnExportPDF.setBackground(new Color(155, 89, 182)); // purple
        btnExportPDF.setForeground(Color.WHITE);
        btnExportPDF.setFocusPainted(false);
        btnExportPDF.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        rightPanel.add(btnExportPDF);

        rightPanel.add(btnPrint);

        rightPanel.add(btnRefresh);

        btnPanel.add(leftPanel, BorderLayout.WEST);
        btnPanel.add(rightPanel, BorderLayout.EAST);

        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        add(mainPanel);

        connect();
        loadExpiredMedicines();

        btnRefresh.addActionListener(e -> loadExpiredMedicines());
        btnBack.addActionListener(e -> {
            // For now, just close the window
            dispose();
            new PharmacyDashboardModern().setVisible(true);
        });
        btnExportPDF.addActionListener(e -> exportTableToPDF());
        btnPrint.addActionListener(e -> {
            try {
                PrintService[] printers = PrinterJob.lookupPrintServices();
                if (printers.length == 0) {
                    JOptionPane.showMessageDialog(this, "No printers are installed on this system!", "Print Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Always show print dialog to let user pick printer
                MessageFormat header = new MessageFormat("Expired Medicines List");
                MessageFormat footer = new MessageFormat("Page {0}");
                boolean complete = table.print(JTable.PrintMode.FIT_WIDTH, header, footer);

                if (complete) {
                    JOptionPane.showMessageDialog(this, "Printing Complete", "Print", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Printing Cancelled", "Print", JOptionPane.WARNING_MESSAGE);
                }

            } catch (PrinterException pe) {
                JOptionPane.showMessageDialog(this, "Printing Failed: " + pe.getMessage(), "Print Error", JOptionPane.ERROR_MESSAGE);
                pe.printStackTrace();
            }
        });

        setVisible(true);
    }

    private void connect() {
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital_db", "root", "");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "DB Connection Error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadExpiredMedicines() {
        if (con == null) {
            return;
        }

        model.setRowCount(0); // Clear existing data
        String sql = "SELECT medicine_id, name, quantity, price, expiry_date FROM medicines WHERE expiry_date < CURDATE() ORDER BY expiry_date ASC";

        try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("medicine_id"));
                row.add(rs.getString("name"));
                row.add(rs.getInt("quantity"));
                row.add(rs.getDouble("price"));

                Date expiry = rs.getDate("expiry_date");
                row.add(expiry != null ? sdf.format(expiry) : "N/A");

                model.addRow(row);
            }

            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No expired medicines found.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching expired medicines: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void exportTableToPDF() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save PDF");
        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getName().toLowerCase().endsWith(".pdf")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".pdf");
            }

            try {
                com.itextpdf.text.Document document = new com.itextpdf.text.Document();
                com.itextpdf.text.pdf.PdfWriter.getInstance(document, new java.io.FileOutputStream(fileToSave));
                document.open();

                // Title
                com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(
                        com.itextpdf.text.Font.FontFamily.HELVETICA, 18, com.itextpdf.text.Font.BOLD, com.itextpdf.text.BaseColor.DARK_GRAY);
                com.itextpdf.text.Paragraph title = new com.itextpdf.text.Paragraph("Expired Medicines List", titleFont);
                title.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                title.setSpacingAfter(20f);
                document.add(title);

                // PDF table setup
                com.itextpdf.text.pdf.PdfPTable pdfTable = new com.itextpdf.text.pdf.PdfPTable(table.getColumnCount());
                pdfTable.setWidthPercentage(100);

                // Headers
                for (int i = 0; i < table.getColumnCount(); i++) {
                    com.itextpdf.text.pdf.PdfPCell headerCell = new com.itextpdf.text.pdf.PdfPCell(
                            new com.itextpdf.text.Phrase(table.getColumnName(i)));
                    headerCell.setBackgroundColor(com.itextpdf.text.BaseColor.LIGHT_GRAY);
                    headerCell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                    pdfTable.addCell(headerCell);
                }

                // Data
                for (int row = 0; row < table.getRowCount(); row++) {
                    for (int col = 0; col < table.getColumnCount(); col++) {
                        Object value = table.getValueAt(row, col);
                        pdfTable.addCell(value != null ? value.toString() : "");
                    }
                }

                document.add(pdfTable);
                document.close();

                JOptionPane.showMessageDialog(this, "PDF exported successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to export PDF: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ExpiredMedicinesModule::new);
    }
}
