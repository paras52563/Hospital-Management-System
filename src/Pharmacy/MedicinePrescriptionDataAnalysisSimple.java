/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import org.jfree.chart.*;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.awt.Font;
import java.awt.geom.Ellipse2D;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;
import org.jfree.chart.axis.NumberAxis;

public class MedicinePrescriptionDataAnalysisSimple extends JFrame {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/hospital_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    private JComboBox<String> graphSelector;
    private ChartPanel chartPanel;
    private JPanel kpiPanel;

    // store KPIs in memory for PDF export
    private final Map<String, String> kpiData = new LinkedHashMap<>();

    public MedicinePrescriptionDataAnalysisSimple() {
        setTitle("📊 Pharmacy Analytics Dashboard");
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        initUI();
        loadChart("Prescription by Brand Share");
        loadKPIs();
    }

    private void initUI() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        topPanel.setBackground(new Color(30, 39, 46));
        topPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel graphLabel = new JLabel("Select Graph:");
        graphLabel.setForeground(Color.WHITE);
        topPanel.add(graphLabel);

        graphSelector = new JComboBox<>(new String[]{
            "Prescription by Brand Share",
            "Stock vs Prescribed Quantity",
            "Prescription Status Trend"
        });
        topPanel.add(graphSelector);

        JButton exportBtn = new JButton("⬇ Export PNG");
        styleButton(exportBtn, new Color(155, 89, 182));
        topPanel.add(exportBtn);

        JButton pdfBtn = new JButton("📄 Export PDF");
        styleButton(pdfBtn, new Color(231, 76, 60));
        topPanel.add(pdfBtn);

        add(topPanel, BorderLayout.NORTH);

        kpiPanel = new JPanel(new GridLayout(2, 4, 15, 10)); // more KPIs → 2 rows
        kpiPanel.setBackground(new Color(44, 62, 80));
        kpiPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        add(kpiPanel, BorderLayout.SOUTH);

        chartPanel = new ChartPanel(null);
        chartPanel.setPreferredSize(new Dimension(1000, 500));
        add(chartPanel, BorderLayout.CENTER);

        graphSelector.addActionListener(e -> reloadChart());
        exportBtn.addActionListener(e -> exportChartPNG());
        pdfBtn.addActionListener(e -> exportPDF());
    }

    private void reloadChart() {
        String graph = (String) graphSelector.getSelectedItem();
        loadChart(graph);
    }

    private void loadChart(String chartType) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            JFreeChart chart = null;

            switch (chartType) {
                case "Prescription by Brand Share" -> {
                    DefaultPieDataset dataset = getPrescriptionByBrandDataset(conn);
                    chart = ChartFactory.createPieChart("Prescription Share by Brand", dataset, true, true, false);
                    PiePlot plot = (PiePlot) chart.getPlot();
                    plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}: {1} ({2})",
                            new DecimalFormat("0"), new DecimalFormat("0%")));
                }
                case "Stock vs Prescribed Quantity" -> {
                    DefaultCategoryDataset dataset = getStockVsPrescribedDataset(conn);
                    chart = ChartFactory.createBarChart("Stock vs Prescribed Quantity", "Medicine", "Quantity",
                            dataset, PlotOrientation.VERTICAL, true, true, false);
                    BarRenderer renderer = (BarRenderer) ((CategoryPlot) chart.getPlot()).getRenderer();
                    renderer.setSeriesPaint(0, new Color(52, 152, 219));
                    renderer.setSeriesPaint(1, new Color(39, 174, 96));
                }
                case "Prescription Status Trend" ->
                    chart = getPrescriptionStatusTrendChart(conn);
            }

            if (chart != null) {
                chartPanel.setChart(chart);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading chart: " + e.getMessage());
        }
    }

    private void loadKPIs() {
        kpiPanel.removeAll();
        kpiData.clear();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD); Statement stmt = conn.createStatement()) {

            addKPI("Total Prescriptions", getSingleValue(stmt, "SELECT COUNT(*) FROM prescriptions"));
            addKPI("Unique Medicines", getSingleValue(stmt, "SELECT COUNT(DISTINCT medicine_id) FROM prescriptions"));
            addKPI("Out of Stock", getSingleValue(stmt, "SELECT COUNT(*) FROM medicines WHERE quantity=0"));
            addKPI("Most Prescribed", getSingleText(stmt,
                    "SELECT m.name FROM prescriptions p JOIN medicines m ON p.medicine_id=m.medicine_id "
                    + "GROUP BY m.name ORDER BY SUM(p.quantity) DESC LIMIT 1"));
            addKPI("Top Brand", getSingleText(stmt,
                    "SELECT m.brand FROM prescriptions p JOIN medicines m ON p.medicine_id=m.medicine_id "
                    + "GROUP BY m.brand ORDER BY COUNT(*) DESC LIMIT 1"));

            // === EXTRA KPIs ===
            addKPI("Total Patients", getSingleValue(stmt, "SELECT COUNT(DISTINCT patient_id) FROM prescriptions"));
            addKPI("Avg Prescriptions/Day", getSingleValue(stmt,
                    "SELECT ROUND(COUNT(*)/COUNT(DISTINCT prescription_date),2) FROM prescriptions"));
            addKPI("Most Common Status", getSingleText(stmt,
                    "SELECT status FROM prescriptions GROUP BY status ORDER BY COUNT(*) DESC LIMIT 1"));

        } catch (SQLException e) {
            addKPI("Error", e.getMessage());
        }
        kpiPanel.revalidate();
        kpiPanel.repaint();
    }

    private String getSingleValue(Statement stmt, String sql) throws SQLException {
        ResultSet rs = stmt.executeQuery(sql);
        return rs.next() ? rs.getInt(1) + "" : "0";
    }

    private String getSingleText(Statement stmt, String sql) throws SQLException {
        ResultSet rs = stmt.executeQuery(sql);
        return rs.next() ? rs.getString(1) : "N/A";
    }

    private void addKPI(String title, String value) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(52, 73, 94));
        card.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel lblVal = new JLabel(value, SwingConstants.CENTER);
        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblVal.setForeground(Color.WHITE);

        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblTitle.setForeground(Color.LIGHT_GRAY);

        card.add(lblVal, BorderLayout.CENTER);
        card.add(lblTitle, BorderLayout.SOUTH);
        kpiPanel.add(card);

        kpiData.put(title, value); // store for PDF export
    }

    private void styleButton(JButton btn, Color color) {
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    }

    private void exportChartPNG() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("chart.png"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                ChartUtilities.saveChartAsPNG(chooser.getSelectedFile(), chartPanel.getChart(), 900, 500);
                JOptionPane.showMessageDialog(this, "Chart saved!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed: " + ex.getMessage());
            }
        }
    }

    private void exportPDF() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("Pharmacy_Report.pdf"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                Document doc = new Document(PageSize.A4, 50, 50, 50, 50);
                PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(chooser.getSelectedFile()));
                doc.open();

                // Title
                com.itextpdf.text.Font titleFont
                        = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 18, com.itextpdf.text.Font.BOLD);

                Paragraph title = new Paragraph("Pharmacy Analytics Report\n\n", titleFont);

                title.setAlignment(Element.ALIGN_CENTER);
                doc.add(title);

                // KPI Table
                PdfPTable table = new PdfPTable(2);
                table.setWidthPercentage(100);
                for (Map.Entry<String, String> entry : kpiData.entrySet()) {
                    table.addCell(new PdfPCell(new Phrase(entry.getKey())));
                    table.addCell(new PdfPCell(new Phrase(entry.getValue())));
                }
                doc.add(table);
                doc.add(new Paragraph("\n"));

                // Chart
                File temp = File.createTempFile("chart", ".png");
                ChartUtilities.saveChartAsPNG(temp, chartPanel.getChart(), 500, 300);
                com.itextpdf.text.Image chartImg = com.itextpdf.text.Image.getInstance(temp.getAbsolutePath());
                chartImg.setAlignment(com.itextpdf.text.Image.ALIGN_CENTER);
                doc.add(chartImg);
                doc.add(chartImg);

                doc.close();
                writer.close();
                JOptionPane.showMessageDialog(this, "PDF Exported!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "PDF Export failed: " + ex.getMessage());
            }
        }
    }

    // === SQL Data ===
    private DefaultPieDataset getPrescriptionByBrandDataset(Connection conn) throws SQLException {
        DefaultPieDataset dataset = new DefaultPieDataset();
        String sql = "SELECT IFNULL(m.brand,'Unknown') brand, SUM(p.quantity) qty "
                + "FROM prescriptions p JOIN medicines m ON p.medicine_id=m.medicine_id GROUP BY m.brand";
        ResultSet rs = conn.createStatement().executeQuery(sql);
        while (rs.next()) {
            dataset.setValue(rs.getString("brand"), rs.getInt("qty"));
        }
        return dataset;
    }

    private DefaultCategoryDataset getStockVsPrescribedDataset(Connection conn) throws SQLException {
        DefaultCategoryDataset ds = new DefaultCategoryDataset();
        String sql = "SELECT m.name, m.quantity stock_qty, COALESCE(SUM(p.quantity),0) prescribed "
                + "FROM medicines m LEFT JOIN prescriptions p ON m.medicine_id=p.medicine_id "
                + "GROUP BY m.medicine_id ORDER BY m.quantity DESC LIMIT 8";
        ResultSet rs = conn.createStatement().executeQuery(sql);
        while (rs.next()) {
            ds.addValue(rs.getInt("stock_qty"), "Stock", rs.getString("name"));
            ds.addValue(rs.getInt("prescribed"), "Prescribed", rs.getString("name"));
        }
        return ds;
    }
    private JFreeChart getPrescriptionStatusTrendChart(Connection conn) throws SQLException {
    DefaultCategoryDataset dataset = new DefaultCategoryDataset();

    String sql = "SELECT p.prescription_date, p.status, COUNT(*) AS cnt "
               + "FROM prescriptions p "
               + "WHERE p.prescription_date IS NOT NULL "
               + "GROUP BY p.prescription_date, p.status "
               + "ORDER BY p.prescription_date";

    try (Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {
        while (rs.next()) {
            String date = rs.getDate("prescription_date").toString();
            String status = rs.getString("status");
            int count = rs.getInt("cnt");
            dataset.addValue(count, status, date);
        }
    }

    JFreeChart chart = ChartFactory.createStackedAreaChart(
            "Prescription Status Trend",
            "Date",
            "Prescription Count",
            dataset,
            PlotOrientation.VERTICAL,
            true, true, false
    );

    CategoryPlot plot = chart.getCategoryPlot();
    plot.setBackgroundPaint(Color.WHITE);
    plot.setDomainGridlinePaint(Color.GRAY);
    plot.setRangeGridlinePaint(Color.GRAY);

    return chart;
}


    

    


   
    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception ignored) {
        }
        SwingUtilities.invokeLater(() -> new MedicinePrescriptionDataAnalysisSimple().setVisible(true));
    }
}
