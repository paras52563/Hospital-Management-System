/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Admin;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.sql.*;
import java.util.*;
import java.util.List;
import javax.imageio.ImageIO;

public class PatientDisplayModern extends JFrame {

    JTextField searchField;
    JTable table;
    DefaultTableModel model;
    JComboBox<String> sortCombo;
    JButton prevBtn, nextBtn, refreshBtn, backBtn;
    JLabel pageLabel;
    List<Object[]> patientData = new ArrayList<>();
    List<Object[]> originalData = new ArrayList<>();
    int page = 1;
    int rowsPerPage = 5;

    public PatientDisplayModern() {
        setTitle("Patient Directory - Modern UI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(960, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(12, 12));
        getContentPane().setBackground(new Color(245, 248, 250));
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(10, 15, 10, 15));

        // Top Panel
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setBackground(new Color(245, 248, 250));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        backBtn = new JButton("Back");
        styleButton(backBtn, new Color(220, 53, 69));
        backBtn.setPreferredSize(new Dimension(90, 32));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        topPanel.add(backBtn, gbc);

        JLabel title = new JLabel("Patient Directory");
        title.setFont(new Font("Segoe UI Semibold", Font.BOLD, 24));
        title.setForeground(new Color(33, 37, 41));
        gbc.gridx = 1;
        gbc.weightx = 1;
        topPanel.add(title, gbc);

        searchField = new JTextField();
        searchField.setToolTipText("Search by ID, Name, Gender, Contact, Email...");
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setPreferredSize(new Dimension(220, 32));
        searchField.setBorder(new CompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                new EmptyBorder(5, 10, 5, 10)));
        gbc.gridx = 2;
        gbc.weightx = 1;
        topPanel.add(searchField, gbc);

        refreshBtn = new JButton("⟳ Refresh");
        styleButton(refreshBtn, new Color(0, 123, 255));
        refreshBtn.setPreferredSize(new Dimension(110, 32));
        refreshBtn.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 13));
        refreshBtn.setFocusPainted(false);
        refreshBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridx = 3;
        gbc.weightx = 0;
        topPanel.add(refreshBtn, gbc);

        sortCombo = new JComboBox<>(new String[]{
                "Sort by ID ↑", "Sort by ID ↓", "Sort by Name ↑", "Sort by Name ↓"});
        sortCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sortCombo.setPreferredSize(new Dimension(150, 32));
        gbc.gridx = 4;
        topPanel.add(sortCombo, gbc);

        add(topPanel, BorderLayout.NORTH);

        // Table
        model = new DefaultTableModel(new Object[]{"ID", "Name", "Gender", "Contact", "Email", "Image"}, 0) {
            public Class<?> getColumnClass(int column) {
                return column == 5 ? ImageIcon.class : String.class;
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
        table.setSelectionBackground(new Color(233, 236, 239));
        table.setGridColor(new Color(230, 230, 230));
        table.setShowGrid(true);

        // Image tooltip on hover
        table.addMouseMotionListener(new MouseAdapter() {
            public void mouseMoved(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (row >= 0 && col == 5) {
                    ImageIcon icon = (ImageIcon) table.getValueAt(row, col);
                    if (icon != null && icon.getImage() != null) {
                        Image scaled = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                        ImageIcon preview = new ImageIcon(scaled);
                        table.setToolTipText("<html><img src='"
                                + toDataURL(preview.getImage())
                                + "'/></html>");
                    } else {
                        table.setToolTipText(null);
                    }
                } else {
                    table.setToolTipText(null);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true));
        add(scrollPane, BorderLayout.CENTER);

        // Bottom pagination
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        bottomPanel.setBackground(new Color(245, 248, 250));

        prevBtn = new JButton("<< Previous");
        styleButton(prevBtn, new Color(108, 117, 125));
        bottomPanel.add(prevBtn);

        pageLabel = new JLabel("Page 1");
        pageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        bottomPanel.add(pageLabel);

        nextBtn = new JButton("Next >>");
        styleButton(nextBtn, new Color(108, 117, 125));
        bottomPanel.add(nextBtn);

        add(bottomPanel, BorderLayout.SOUTH);

        // Load data
        loadDataFromDatabase();
        displayPage();

        // Listeners
        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                filterAndSort();
            }
        });

        sortCombo.addActionListener(e -> filterAndSort());

        refreshBtn.addActionListener(e -> {
            searchField.setText("");
            loadDataFromDatabase();
            page = 1;
            displayPage();
        });

        prevBtn.addActionListener(e -> {
            if (page > 1) {
                page--;
                displayPage();
            }
        });

        nextBtn.addActionListener(e -> {
            if (page * rowsPerPage < patientData.size()) {
                page++;
                displayPage();
            }
        });

        backBtn.addActionListener(e -> {
            dispose();
            new PatientMainDashboard().setVisible(true);
        });
    }

    private void loadDataFromDatabase() {
        patientData.clear();
        originalData.clear();
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital_db", "root", "");
             PreparedStatement pst = con.prepareStatement("SELECT patient_id, name, gender, contact, email, image_path FROM patients");
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                String id = rs.getString("patient_id");
                String name = rs.getString("name");
                String gender = rs.getString("gender");
                String contact = rs.getString("contact");
                String email = rs.getString("email");

                ImageIcon imageIcon;
                String imgPath = rs.getString("image_path");
                if (imgPath != null && !imgPath.isEmpty()) {
                    ImageIcon tmpIcon = new ImageIcon(imgPath);
                    Image scaled = tmpIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
                    imageIcon = new ImageIcon(scaled);
                } else {
                    imageIcon = new ImageIcon(new BufferedImage(80, 80, BufferedImage.TYPE_INT_ARGB));
                }

                Object[] row = {id, name, gender, contact, email, imageIcon};
                patientData.add(row);
                originalData.add(row.clone());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayPage() {
        model.setRowCount(0);
        int start = (page - 1) * rowsPerPage;
        int end = Math.min(start + rowsPerPage, patientData.size());
        for (int i = start; i < end; i++) {
            model.addRow(patientData.get(i));
        }
        pageLabel.setText("Page " + page);
        prevBtn.setEnabled(page > 1);
        nextBtn.setEnabled(page * rowsPerPage < patientData.size());
    }

    private void filterAndSort() {
        String keyword = searchField.getText().toLowerCase().trim();
        String sort = (String) sortCombo.getSelectedItem();

        List<Object[]> filtered = new ArrayList<>();
        for (Object[] row : originalData) {
            String combined = (row[0] + " " + row[1] + " " + row[2] + " " + row[3] + " " + row[4]).toLowerCase();
            if (combined.contains(keyword)) filtered.add(row);
        }

        Comparator<Object[]> comparator = switch (sort) {
            case "Sort by ID ↓" -> Comparator.comparing(o -> o[0].toString(), Comparator.reverseOrder());
            case "Sort by Name ↑" -> Comparator.comparing(o -> o[1].toString().toLowerCase());
            case "Sort by Name ↓" -> Comparator.comparing(o -> o[1].toString().toLowerCase(), Comparator.reverseOrder());
            default -> Comparator.comparing(o -> o[0].toString());
        };

        filtered.sort(comparator);
        patientData = filtered;
        page = 1;
        displayPage();
    }

    private void styleButton(JButton button, Color bgColor) {
        button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(new LineBorder(bgColor.darker(), 1, true));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private String toDataURL(Image image) {
        try {
            BufferedImage buffered = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
            Graphics g = buffered.createGraphics();
            g.drawImage(image, 0, 0, null);
            g.dispose();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(buffered, "png", out);
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(out.toByteArray());
        } catch (Exception e) {
            return "";
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PatientDisplayModern().setVisible(true));
    }
}



