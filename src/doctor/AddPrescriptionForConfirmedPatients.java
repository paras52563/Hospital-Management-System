

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package doctor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class AddPrescriptionForConfirmedPatients extends JFrame {

    private JComboBox<String> cbPatients, cbMedicines;
    private JTextField tfDosage, tfQuantity;
    private JButton btnPrescribe, btnBack;
    private JLabel lblStatus;

    private String doctorId;
    private Map<String, String> patientMap = new HashMap<>();
    private Map<String, Integer> appointmentMap = new HashMap<>();
    private Map<String, Integer> medicineMap = new HashMap<>();

    public AddPrescriptionForConfirmedPatients(String doctorId) {
    this.doctorId = doctorId;

    setTitle("Add Prescription for Confirmed Patients");
    setSize(750, 520);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    // Outer background
    JPanel outer = new JPanel(new GridBagLayout());
    outer.setBackground(new Color(240, 242, 245)); // softer background

    // Inner card/box
    JPanel panel = new JPanel(new GridBagLayout()) {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20); // rounded corners
        }
    };
    panel.setBorder(new EmptyBorder(20, 20, 20, 20));
    panel.setOpaque(false);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(14, 14, 14, 14);
    gbc.fill = GridBagConstraints.HORIZONTAL;

    Font labelFont = new Font("Segoe UI", Font.PLAIN, 16);
    Font fieldFont = new Font("Segoe UI", Font.PLAIN, 16);

    // Title bar
    JPanel titleBar = new JPanel();
    titleBar.setBackground(new Color(30, 144, 255)); // Dodger blue
    JLabel lblTitle = new JLabel("🩺 Prescribe Medicine", SwingConstants.CENTER);
    lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
    lblTitle.setForeground(Color.WHITE);
    titleBar.add(lblTitle);

    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 2;
    gbc.fill = GridBagConstraints.BOTH;
    panel.add(titleBar, gbc);
    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;

    // Patient dropdown
    JLabel lblPatient = new JLabel("Confirmed Patient:");
    lblPatient.setFont(labelFont);
    gbc.gridx = 0;
    gbc.gridy = 1;
    panel.add(lblPatient, gbc);

    cbPatients = new JComboBox<>();
    cbPatients.setFont(fieldFont);
    gbc.gridx = 1;
    panel.add(cbPatients, gbc);

    // Medicine dropdown
    JLabel lblMedicine = new JLabel("Select Medicine:");
    lblMedicine.setFont(labelFont);
    gbc.gridx = 0;
    gbc.gridy = 2;
    panel.add(lblMedicine, gbc);

    cbMedicines = new JComboBox<>();
    cbMedicines.setFont(fieldFont);
    gbc.gridx = 1;
    panel.add(cbMedicines, gbc);

    // Dosage
    JLabel lblDosage = new JLabel("Dosage (e.g., 1-0-1):");
    lblDosage.setFont(labelFont);
    gbc.gridx = 0;
    gbc.gridy = 3;
    panel.add(lblDosage, gbc);

    tfDosage = new JTextField();
    tfDosage.setFont(fieldFont);
    gbc.gridx = 1;
    panel.add(tfDosage, gbc);

    // Quantity
    JLabel lblQuantity = new JLabel("Quantity:");
    lblQuantity.setFont(labelFont);
    gbc.gridx = 0;
    gbc.gridy = 4;
    panel.add(lblQuantity, gbc);

    tfQuantity = new JTextField();
    tfQuantity.setFont(fieldFont);
    gbc.gridx = 1;
    panel.add(tfQuantity, gbc);

    // Buttons panel
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
    buttonPanel.setOpaque(false);

    btnPrescribe = new JButton("Prescribe");
    styleButton(btnPrescribe, new Color(0, 128, 0), new Color(34, 160, 34));

    btnBack = new JButton("Back");
    styleButton(btnBack, new Color(178, 34, 34), new Color(200, 55, 55));

    buttonPanel.add(btnPrescribe);
    buttonPanel.add(btnBack);

    gbc.gridx = 0;
    gbc.gridy = 5;
    gbc.gridwidth = 2;
    panel.add(buttonPanel, gbc);

    // Status label
    lblStatus = new JLabel("", SwingConstants.CENTER);
    lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 16));
    gbc.gridy = 6;
    panel.add(lblStatus, gbc);

    outer.add(panel);
    add(outer);

    // Listeners
    btnPrescribe.addActionListener(e -> prescribe());
    btnBack.addActionListener(e -> {
        dispose();
        new DoctorDashboard(doctorId).setVisible(true);
    });

    loadConfirmedPatients();
    loadMedicines();
    setVisible(true);
}

// 🔹 Button styling method with hover effect
private void styleButton(JButton button, Color normal, Color hover) {
    button.setFont(new Font("Segoe UI", Font.BOLD, 16));
    button.setBackground(normal);
    button.setForeground(Color.WHITE);
    button.setFocusPainted(false);
    button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
    button.setCursor(new Cursor(Cursor.HAND_CURSOR));

    button.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseEntered(java.awt.event.MouseEvent evt) {
            button.setBackground(hover);
        }
        public void mouseExited(java.awt.event.MouseEvent evt) {
            button.setBackground(normal);
        }
    });
}



    private void loadConfirmedPatients() {
        try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/hospital_db", "root", "")) {

            String query = "SELECT a.appointment_id, p.patient_id, p.name FROM appointments a " +
                    "JOIN patients p ON a.patient_id = p.patient_id " +
                    "WHERE a.doctor_id = ? AND a.status = 'Confirmed'";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, doctorId);
            ResultSet rs = pst.executeQuery();

            cbPatients.removeAllItems();
            patientMap.clear();
            appointmentMap.clear();

            while (rs.next()) {
                String patientName = rs.getString("name");
                String patientId = rs.getString("patient_id");
                int appointmentId = rs.getInt("appointment_id");

                String label = patientName + " (ID: " + patientId + ")";
                cbPatients.addItem(label);
                patientMap.put(label, patientId);
                appointmentMap.put(label, appointmentId);
            }

            if (cbPatients.getItemCount() == 0) {
                lblStatus.setText("No confirmed patients found.");
                btnPrescribe.setEnabled(false);
            } else {
                lblStatus.setText("");
                btnPrescribe.setEnabled(true);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            lblStatus.setText("Error loading patients.");
        }
    }

    private void loadMedicines() {
        try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/hospital_db", "root", "")) {

            String query = "SELECT medicine_id, name, brand, expiry_date FROM medicines " +
                    "WHERE expiry_date >= CURDATE() ORDER BY name";
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(query);

            cbMedicines.removeAllItems();
            medicineMap.clear();

            while (rs.next()) {
                int medicineId = rs.getInt("medicine_id");
                String name = rs.getString("name");
                String brand = rs.getString("brand");

                String label = name + " - " + brand;
                cbMedicines.addItem(label);
                medicineMap.put(label, medicineId);
            }

            if (cbMedicines.getItemCount() == 0) {
                lblStatus.setText("No medicines available.");
                btnPrescribe.setEnabled(false);
            } else {
                lblStatus.setText("");
                btnPrescribe.setEnabled(true);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            lblStatus.setText("Error loading medicines.");
        }
    }

    private void prescribe() {
        lblStatus.setForeground(Color.RED);

        String selectedPatient = (String) cbPatients.getSelectedItem();
        String selectedMedicine = (String) cbMedicines.getSelectedItem();
        String dosage = tfDosage.getText().trim();
        String quantityStr = tfQuantity.getText().trim();

        if (selectedPatient == null || selectedMedicine == null || dosage.isEmpty() || quantityStr.isEmpty()) {
            lblStatus.setText("All fields are required.");
            return;
        }

        if (!dosage.matches("[0-3](\\.[0-9])?-[0-3](\\.[0-9])?-[0-3](\\.[0-9])?")) {
            lblStatus.setText("Dosage must be in format 'X-Y-Z' (0-3).");
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityStr);
            if (quantity <= 0) {
                lblStatus.setText("Quantity must be positive.");
                return;
            }
        } catch (NumberFormatException e) {
            lblStatus.setText("Quantity must be a valid number.");
            return;
        }

        try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/hospital_db", "root", "")) {

            String patientId = patientMap.get(selectedPatient);
            int appointmentId = appointmentMap.get(selectedPatient);
            int medicineId = medicineMap.get(selectedMedicine);
            LocalDate today = LocalDate.now();

            // Check duplicate prescription
            PreparedStatement dupStmt = con.prepareStatement(
                    "SELECT COUNT(*) FROM prescriptions WHERE appointment_id=? AND medicine_id=? AND prescription_date=?");
            dupStmt.setInt(1, appointmentId);
            dupStmt.setInt(2, medicineId);
            dupStmt.setDate(3, Date.valueOf(today));
            ResultSet dupRs = dupStmt.executeQuery();
            if (dupRs.next() && dupRs.getInt(1) > 0) {
                lblStatus.setText("Prescription for this medicine already exists today.");
                return;
            }

            // Insert prescription
            PreparedStatement insertStmt = con.prepareStatement(
                    "INSERT INTO prescriptions (appointment_id, patient_id, doctor_id, medicine_id, prescription_date, dosage, quantity, status) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, 'Pending')");
            insertStmt.setInt(1, appointmentId);
            insertStmt.setString(2, patientId);
            insertStmt.setString(3, doctorId);
            insertStmt.setInt(4, medicineId);
            insertStmt.setDate(5, Date.valueOf(today));
            insertStmt.setString(6, dosage);
            insertStmt.setInt(7, quantity);

            int result = insertStmt.executeUpdate();
            if (result > 0) {
                lblStatus.setForeground(new Color(0, 128, 0));
                lblStatus.setText("Prescription saved successfully!");
                JOptionPane.showMessageDialog(this,
                        "Prescription saved successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                tfDosage.setText("");
                tfQuantity.setText("");
            } else {
                lblStatus.setText("Failed to save prescription.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            lblStatus.setText("Error saving prescription.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AddPrescriptionForConfirmedPatients("DOC2157"));
    }
}
