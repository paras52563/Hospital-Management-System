/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Patient;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.data.category.*;
import com.itextpdf.text.Font;
import org.jfree.data.general.*;

public class PatientReports extends JFrame {

    private final String patientId;

    public PatientReports(String patientId) {
        this.patientId = patientId;

        setTitle("Patient Reports & Statistics - " + patientId);
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // Header
        JLabel header = new JLabel("Reports & Statistics", JLabel.CENTER);
        //header.setFont(new Font("SansSerif", Font.BOLD, 24));
        header.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(header, BorderLayout.NORTH);

        // Buttons Panel
        JPanel btnPanel = new JPanel(new GridLayout(2,2,20,20));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(20,50,20,50));

        JButton presBtn = new JButton("Download Prescription Report");
        JButton medBtn = new JButton("Download Medical History Report");
        JButton chartAppBtn = new JButton("Monthly Appointments Chart");
        JButton chartMedBtn = new JButton("Recurring Medicines Chart");

        JButton[] buttons = {presBtn, medBtn, chartAppBtn, chartMedBtn};
        Color[] colors = {Color.GREEN.darker(), Color.CYAN.darker(), Color.ORANGE, Color.RED.darker()};
        for(int i=0;i<4;i++){
            buttons[i].setForeground(Color.WHITE);
            buttons[i].setBackground(colors[i]);
            buttons[i].setFocusPainted(false);
            btnPanel.add(buttons[i]);
        }
        add(btnPanel, BorderLayout.CENTER);

        // Back Button
        JButton back = new JButton("Back to Dashboard");
        back.addActionListener(e -> { dispose(); new PatientDashboard(patientId); });
        JPanel foot = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        foot.add(back);
        add(foot, BorderLayout.SOUTH);

        // Button Actions
        presBtn.addActionListener(e -> exportPrescriptionReport());
        medBtn.addActionListener(e -> exportMedicalHistoryReport());
        chartAppBtn.addActionListener(e -> showMonthlyAppointmentsChart());
        chartMedBtn.addActionListener(e -> showRecurringMedicinesChart());

        setVisible(true);
    }

    private String chooseFilePath(String defaultFileName){
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File(defaultFileName));
        if(chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
            return chooser.getSelectedFile().getAbsolutePath();
        return null;
    }

    private Date[] getDateRange(){
        JPanel panel = new JPanel(new GridLayout(2,2,5,5));
        panel.add(new JLabel("Start Date:"));
        JSpinner start = new JSpinner(new SpinnerDateModel());
        start.setEditor(new JSpinner.DateEditor(start,"yyyy-MM-dd"));
        panel.add(start);
        panel.add(new JLabel("End Date:"));
        JSpinner end = new JSpinner(new SpinnerDateModel());
        end.setEditor(new JSpinner.DateEditor(end,"yyyy-MM-dd"));
        panel.add(end);

        if(JOptionPane.showConfirmDialog(this,panel,"Select Date Range",JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION){
            return new Date[]{(Date)start.getValue(), (Date)end.getValue()};
        }
        return null;
    }

    // -------------------- PDF EXPORT --------------------
    private void exportPrescriptionReport() {
        Date[] dates = getDateRange();
        if(dates==null) return;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String from = sdf.format(dates[0]);
        String to = sdf.format(dates[1]);

        try(Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital_db","root","")) {
            PreparedStatement ps = con.prepareStatement(
                    "SELECT p.prescription_date, d.name AS doctor, m.name AS medicine, p.dosage " +
                    "FROM prescriptions p JOIN doctor d ON p.doctor_id=d.doctor_id " +
                    "JOIN medicines m ON p.medicine_id=m.medicine_id " +
                    "WHERE p.patient_id=? AND p.prescription_date BETWEEN ? AND ? ORDER BY p.prescription_date DESC");
            ps.setString(1, patientId);
            ps.setString(2, from);
            ps.setString(3, to);

            ResultSet rs = ps.executeQuery();
            String path = chooseFilePath("Prescription_Report_"+patientId+".pdf");
            if(path==null) return;

            Document doc = new Document();
            PdfWriter.getInstance(doc,new FileOutputStream(path));
            doc.open();
            doc.add(new Paragraph("Prescription Report - "+patientId,new Font(Font.FontFamily.HELVETICA,18,Font.BOLD)));
            doc.add(new Paragraph("Date Range: "+from+" to "+to));
            doc.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(4);
            table.addCell("Date"); table.addCell("Doctor"); table.addCell("Medicine"); table.addCell("Dosage");

            boolean hasData=false;
            while(rs.next()){
                hasData=true;
                table.addCell(rs.getDate("prescription_date").toString());
                table.addCell(rs.getString("doctor"));
                table.addCell(rs.getString("medicine"));
                table.addCell(rs.getString("dosage"));
            }
            if(!hasData){ JOptionPane.showMessageDialog(this,"No data found."); doc.close(); return; }

            doc.add(table); doc.close();
            JOptionPane.showMessageDialog(this,"PDF saved successfully!");
        } catch(Exception e){ e.printStackTrace(); JOptionPane.showMessageDialog(this,"Error: "+e.getMessage()); }
    }

    private void exportMedicalHistoryReport() {
        Date[] dates = getDateRange();
        if(dates==null) return;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String from = sdf.format(dates[0]);
        String to = sdf.format(dates[1]);

        try(Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital_db","root","")) {
            PreparedStatement ps = con.prepareStatement(
                    "SELECT mr.record_date, d.name AS doctor, mr.diagnosis, mr.treatment, mr.notes " +
                    "FROM medical_records mr JOIN doctor d ON mr.doctor_id=d.doctor_id " +
                    "WHERE mr.patient_id=? AND mr.record_date BETWEEN ? AND ? ORDER BY mr.record_date DESC");
            ps.setString(1, patientId);
            ps.setString(2, from);
            ps.setString(3, to);

            ResultSet rs = ps.executeQuery();
            String path = chooseFilePath("MedicalHistory_Report_"+patientId+".pdf");
            if(path==null) return;

            Document doc = new Document();
            PdfWriter.getInstance(doc,new FileOutputStream(path));
            doc.open();
            doc.add(new Paragraph("Medical History - "+patientId,new Font(Font.FontFamily.HELVETICA,18,Font.BOLD)));
            doc.add(new Paragraph("Date Range: "+from+" to "+to));
            doc.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(5);
            table.addCell("Date"); table.addCell("Doctor"); table.addCell("Diagnosis"); table.addCell("Treatment"); table.addCell("Notes");

            boolean hasData=false;
            while(rs.next()){
                hasData=true;
                table.addCell(rs.getDate("record_date").toString());
                table.addCell(rs.getString("doctor"));
                table.addCell(rs.getString("diagnosis"));
                table.addCell(rs.getString("treatment"));
                table.addCell(rs.getString("notes"));
            }
            if(!hasData){ JOptionPane.showMessageDialog(this,"No data found."); doc.close(); return; }

            doc.add(table); doc.close();
            JOptionPane.showMessageDialog(this,"PDF saved successfully!");
        } catch(Exception e){ e.printStackTrace(); JOptionPane.showMessageDialog(this,"Error: "+e.getMessage()); }
    }

    // -------------------- CHARTS --------------------
    private void showMonthlyAppointmentsChart() {
        try(Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital_db","root","")) {
            PreparedStatement ps = con.prepareStatement(
                    "SELECT MONTH(appointment_date) AS month, COUNT(*) AS total FROM appointments " +
                    "WHERE patient_id=? GROUP BY MONTH(appointment_date)");
            ps.setString(1, patientId);
            ResultSet rs = ps.executeQuery();

            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            while(rs.next()){
                int month = rs.getInt("month");
                dataset.addValue(rs.getInt("total"), "Appointments", new java.text.DateFormatSymbols().getMonths()[month-1]);
            }

            JFreeChart chart = ChartFactory.createBarChart("Monthly Appointments","Month","Total",dataset,PlotOrientation.VERTICAL,false,true,false);
            ChartFrame frame = new ChartFrame("Monthly Appointments", chart);
            frame.pack(); frame.setVisible(true);
        } catch(Exception e){ e.printStackTrace(); JOptionPane.showMessageDialog(this,"Error: "+e.getMessage()); }
    }

    private void showRecurringMedicinesChart() {
        try(Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital_db","root","")) {
            PreparedStatement ps = con.prepareStatement(
                    "SELECT m.name, COUNT(*) AS total FROM prescriptions p JOIN medicines m ON p.medicine_id=m.medicine_id " +
                    "WHERE p.patient_id=? GROUP BY m.name");
            ps.setString(1, patientId);
            ResultSet rs = ps.executeQuery();

            DefaultPieDataset dataset = new DefaultPieDataset();
            while(rs.next()){ dataset.setValue(rs.getString("name"), rs.getInt("total")); }

            JFreeChart chart = ChartFactory.createPieChart("Recurring Medicines", dataset, true,true,false);
            ChartFrame frame = new ChartFrame("Recurring Medicines", chart);
            frame.pack(); frame.setVisible(true);
        } catch(Exception e){ e.printStackTrace(); JOptionPane.showMessageDialog(this,"Error: "+e.getMessage()); }
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> new PatientReports("PAT97125"));
    }
}


