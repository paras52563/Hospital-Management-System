/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package doctor;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.sql.*;
import javax.swing.border.EmptyBorder;

public class DoctorPatientHistoryBySelection extends JFrame {

    private JComboBox<String> patientComboBox;
    private JButton backBtn;
    private JPanel cardPanel;
    private JScrollPane scrollPane;
    private String doctorId;

    public DoctorPatientHistoryBySelection(String doctorId) {
        this.doctorId = doctorId;

        setTitle("Patient History by Doctor");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        initUI();
        loadPatients();

        setVisible(true);
    }

    private void initUI() {
        // Top Panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(52, 152, 219));
        topPanel.setPreferredSize(new Dimension(getWidth(), 100));

        // Back Button
        backBtn = new JButton("← Back");
        backBtn.setFocusPainted(false);
        backBtn.setBackground(new Color(41, 128, 185));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backBtn.setPreferredSize(new Dimension(90, 35));
        backBtn.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        backBtn.addActionListener(e -> {
            dispose();
            new DoctorDashboard(doctorId).setVisible(true);
        });
        topPanel.add(backBtn, BorderLayout.WEST);

        // Title
        JLabel titleLabel = new JLabel("Select Patient to View Medical History", JLabel.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        topPanel.add(titleLabel, BorderLayout.CENTER);

        // Combo Box Panel
        JPanel comboPanel = new JPanel();
        comboPanel.setBackground(new Color(52, 152, 219));
        comboPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));

        JLabel patientLabel = new JLabel("Patient ID: ");
        patientLabel.setForeground(Color.WHITE);
        patientLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        comboPanel.add(patientLabel);

        patientComboBox = new JComboBox<>();
        patientComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        patientComboBox.setPreferredSize(new Dimension(250, 35));
        patientComboBox.addActionListener(e -> {
            if (patientComboBox.getSelectedIndex() > 0) {
                String patientId = (String) patientComboBox.getSelectedItem();
                fetchMedicalHistory(patientId);
            } else {
                cardPanel.removeAll();
                cardPanel.revalidate();
                cardPanel.repaint();
            }
        });
        comboPanel.add(patientComboBox);

        topPanel.add(comboPanel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        // Card Panel inside scroll pane
        cardPanel = new JPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBackground(new Color(236, 240, 241));
        cardPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        scrollPane = new JScrollPane(cardPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // smoother scrolling
        scrollPane.setBorder(null);

        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadPatients() {
        patientComboBox.removeAllItems();
        patientComboBox.addItem("-- Select Patient --");

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital_db", "root", "");
             PreparedStatement ps = con.prepareStatement(
                     "SELECT DISTINCT patient_id FROM medical_records WHERE doctor_id = ? ORDER BY patient_id"
             )) {
            ps.setString(1, doctorId);
            ResultSet rs = ps.executeQuery();

            boolean hasPatients = false;
            while (rs.next()) {
                String patientId = rs.getString("patient_id");
                patientComboBox.addItem(patientId);
                hasPatients = true;
            }

            if (!hasPatients) {
                JOptionPane.showMessageDialog(this, "No patients found for this doctor.");
                patientComboBox.setEnabled(false);
            } else {
                patientComboBox.setEnabled(true);
                patientComboBox.setSelectedIndex(0);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading patients: " + ex.getMessage());
        }
    }

    private void fetchMedicalHistory(String patientId) {
        cardPanel.removeAll();

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital_db", "root", "");
             PreparedStatement ps = con.prepareStatement(
                     "SELECT record_id, diagnosis, treatment, notes, record_date, created_at " +
                             "FROM medical_records WHERE doctor_id = ? AND patient_id = ? ORDER BY record_date DESC, created_at DESC"
             )) {
            ps.setString(1, doctorId);
            ps.setString(2, patientId);

            ResultSet rs = ps.executeQuery();

            boolean hasRecords = false;
            while (rs.next()) {
                hasRecords = true;
                JPanel card = createRecordCard(
                        rs.getInt("record_id"),
                        rs.getString("diagnosis"),
                        rs.getString("treatment"),
                        rs.getString("notes"),
                        rs.getDate("record_date").toString(),
                        rs.getTimestamp("created_at").toString()
                );
                cardPanel.add(card);
                cardPanel.add(Box.createRigidArea(new Dimension(0, 10))); // spacing between cards
            }

            if (!hasRecords) {
                JOptionPane.showMessageDialog(this, "No medical records found for patient: " + patientId);
            }

            cardPanel.revalidate();
            cardPanel.repaint();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching medical history: " + ex.getMessage());
        }
    }

    private JPanel createRecordCard(int recordId, String diagnosis, String treatment, String notes, String recordDate, String createdAt) {
        JPanel card = new JPanel();
        card.setLayout(new GridLayout(0, 1));
        card.setBackground(Color.WHITE);
        card.setBorder(new LineBorder(new Color(189, 195, 199), 1, true));
        card.setPreferredSize(new Dimension(scrollPane.getWidth() - 50, 120));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel lblId = new JLabel("Record ID: " + recordId);
        JLabel lblDiagnosis = new JLabel("Diagnosis: " + diagnosis);
        JLabel lblTreatment = new JLabel("Treatment: " + treatment);
        JLabel lblNotes = new JLabel("Notes: " + notes);
        JLabel lblDate = new JLabel("Record Date: " + recordDate + " | Created At: " + createdAt);

        for (JLabel lbl : new JLabel[]{lblId, lblDiagnosis, lblTreatment, lblNotes, lblDate}) {
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        }

        card.add(lblId);
        card.add(lblDiagnosis);
        card.add(lblTreatment);
        card.add(lblNotes);
        card.add(lblDate);

        return card;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DoctorPatientHistoryBySelection("DOC2420"));
    }
}
