/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Admin;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.List;

public class DoctorDisplayModern extends JFrame {
    private JTextField searchField;
    private JTable table;
    private DefaultTableModel model;
    private JComboBox<String> sortCombo;
    private JButton prevBtn, nextBtn, refreshBtn, backBtn;
    private JLabel pageLabel;
    private List<Object[]> doctorData = new ArrayList<>();
    private List<Object[]> originalData = new ArrayList<>();
    private int page = 1;
    private final int rowsPerPage = 5;

    public DoctorDisplayModern() {
        
        setTitle("Doctor Directory - Modern UI");
        setSize(1100, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.setBackground(new Color(245, 247, 250));

        // Top Bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(30, 41, 59));
        topBar.setPreferredSize(new Dimension(0, 60));
        topBar.setBorder(new MatteBorder(0, 0, 2, 0, new Color(80, 120, 210)));

        JLabel heading = new JLabel("Doctor Directory");
        heading.setForeground(Color.WHITE);
        heading.setFont(new Font("Segoe UI Semibold", Font.BOLD, 26));
        heading.setBorder(new EmptyBorder(0, 25, 0, 0));
        topBar.add(heading, BorderLayout.WEST);

        contentPane.add(topBar, BorderLayout.NORTH);

        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        mainPanel.setBackground(new Color(245, 247, 250));

        // Top Controls
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        controlPanel.setBackground(new Color(245, 247, 250));

        backBtn = new JButton("Back");
        styleButton(backBtn, new Color(220, 53, 69));
        controlPanel.add(backBtn);

        searchField = new JTextField(20);
        searchField.setToolTipText("Search by ID, Name, Gender, Email, Specialist...");
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setBorder(new CompoundBorder(new LineBorder(new Color(200, 200, 200), 1, true), new EmptyBorder(5, 10, 5, 10)));
        controlPanel.add(searchField);

        refreshBtn = new JButton("Refresh");
        styleButton(refreshBtn, new Color(0, 123, 255));
        controlPanel.add(refreshBtn);

        sortCombo = new JComboBox<>(new String[]{
                "Sort by ID ↑", "Sort by ID ↓", "Sort by Name ↑", "Sort by Name ↓", "Sort by Specialist ↑", "Sort by Specialist ↓"});
        sortCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        controlPanel.add(sortCombo);

        mainPanel.add(controlPanel, BorderLayout.NORTH);

        // Table
        model = new DefaultTableModel(new Object[]{"ID", "Name", "Gender", "Qualification", "Specialist", "Email", "Image"}, 0) {
            public Class<?> getColumnClass(int column) {
                return column == 6 ? ImageIcon.class : String.class;
            }

            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(80);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(52, 58, 64));
        table.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        bottomPanel.setBackground(new Color(245, 247, 250));

        prevBtn = new JButton("<< Previous");
        nextBtn = new JButton("Next >>");
        styleButton(prevBtn, new Color(108, 117, 125));
        styleButton(nextBtn, new Color(108, 117, 125));

        pageLabel = new JLabel("Page 1");
        pageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        bottomPanel.add(prevBtn);
        bottomPanel.add(pageLabel);
        bottomPanel.add(nextBtn);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);

        // Data
        loadDataFromDatabase();
        displayPage();

        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                filterAndSort();
            }
        });

        sortCombo.addActionListener(e -> filterAndSort());

        refreshBtn.addActionListener(e -> {
            searchField.setText("");
            loadDataFromDatabase();
            displayPage();
        });

        prevBtn.addActionListener(e -> {
            if (page > 1) {
                page--;
                displayPage();
            }
        });

        nextBtn.addActionListener(e -> {
            if (page * rowsPerPage < doctorData.size()) {
                page++;
                displayPage();
            }
        });

        backBtn.addActionListener(e -> {
            dispose();
            new Admin.DoctorMainDashboard().setVisible(true);
        });
    }

    private void loadDataFromDatabase() {
        doctorData.clear();
        originalData.clear();
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital_db", "root", "");
             PreparedStatement pst = con.prepareStatement("SELECT doctor_id, name, gender, qualification, speciality, email, image FROM doctor");
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                String id = rs.getString("doctor_id");
                String name = rs.getString("name");
                String gender = rs.getString("gender");
                String qualification = rs.getString("qualification");
                String specialist = rs.getString("speciality");
                String email = rs.getString("email");

                ImageIcon imageIcon;
                Blob blob = rs.getBlob("image");
                if (blob != null) {
                    byte[] imgBytes = blob.getBytes(1, (int) blob.length());
                    BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imgBytes));
                    Image scaled = bufferedImage.getScaledInstance(80, 80, Image.SCALE_SMOOTH);
                    imageIcon = new ImageIcon(scaled);
                } else {
                    imageIcon = new ImageIcon(new BufferedImage(80, 80, BufferedImage.TYPE_INT_ARGB));
                }

                Object[] row = {id, name, gender, qualification, specialist, email, imageIcon};
                doctorData.add(row);
                originalData.add(row.clone());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayPage() {
        model.setRowCount(0);
        int start = (page - 1) * rowsPerPage;
        int end = Math.min(start + rowsPerPage, doctorData.size());
        for (int i = start; i < end; i++) {
            model.addRow(doctorData.get(i));
        }
        pageLabel.setText("Page " + page);
        prevBtn.setEnabled(page > 1);
        nextBtn.setEnabled(page * rowsPerPage < doctorData.size());
    }

    private void filterAndSort() {
        String keyword = searchField.getText().toLowerCase(Locale.ROOT).trim();
        
        
        String sort = (String) sortCombo.getSelectedItem();

        List<Object[]> filtered = new ArrayList<>();
        for (Object[] row : originalData) {
            String combined = (row[0] + " " + row[1] + " " + row[2] + " " + row[3] + " " + row[4] + " " + row[5]).toLowerCase();
            if (combined.contains(keyword)) {
                filtered.add(row);
            }
        }

        Comparator<Object[]> comparator = switch (sort) {
            case "Sort by ID ↓" -> Comparator.comparing(o -> o[0].toString(), Comparator.reverseOrder());
            case "Sort by Name ↑" -> Comparator.comparing(o -> o[1].toString());
            case "Sort by Name ↓" -> Comparator.comparing(o -> o[1].toString(), Comparator.reverseOrder());
            case "Sort by Specialist ↑" -> Comparator.comparing(o -> o[4].toString());
            case "Sort by Specialist ↓" -> Comparator.comparing(o -> o[4].toString(), Comparator.reverseOrder());
            default -> Comparator.comparing(o -> o[0].toString());
        };

        filtered.sort(comparator);
        doctorData = filtered;
        page = 1;
        displayPage();
    }

    private void styleButton(JButton button, Color bgColor) {
        button.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(bgColor.darker(), 1, true),
                new EmptyBorder(6, 16, 6, 16)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);

        // Hover effects
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
    }

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "MySQL JDBC Driver not found.", "Driver Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

       SwingUtilities.invokeLater(() -> new DoctorDisplayModern().setVisible(true));
    }
}