/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package admin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;

public class AdminContactMessagesModern extends JFrame {

    private JTable messageTable;
    private DefaultTableModel model;
    private JTextField txtSearch;
    private JButton btnRead, btnBack, btnRefresh, btnAll, btnUnread, btnReadFilter;
    private JLabel lblCount;
    private Connection conn;
    private String currentFilter = "ALL";

    public AdminContactMessagesModern() {
        setTitle("Admin - Contact Messages");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(245, 247, 250));

        // === HEADER ===
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0,
                        new Color(52, 152, 219),
                        getWidth(), getHeight(),
                        new Color(41, 128, 185));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        headerPanel.setPreferredSize(new Dimension(950, 80));
        headerPanel.setLayout(new BorderLayout());
        JLabel lblTitle = new JLabel("📨 Contact Messages", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        // === TOP BAR (Search + Filters) ===
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setBackground(new Color(245, 247, 250));

        txtSearch = new JTextField(25);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
                new EmptyBorder(5, 10, 5, 10)
        ));
        btnRefresh = createButton("🔄 Refresh", new Color(52, 152, 219));
        btnAll = createButton("📋 All", new Color(52, 73, 94));
        btnUnread = createButton("📥 Unread", new Color(241, 196, 15));
        btnReadFilter = createButton("✅ Read", new Color(46, 204, 113));

        lblCount = new JLabel("Messages: 0");
        lblCount.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblCount.setForeground(new Color(100, 100, 100));

        topPanel.add(new JLabel("Search: "));
        topPanel.add(txtSearch);
        topPanel.add(btnRefresh);
        topPanel.add(btnAll);
        topPanel.add(btnUnread);
        topPanel.add(btnReadFilter);
        topPanel.add(lblCount);
        add(topPanel, BorderLayout.BEFORE_FIRST_LINE);

        // === TABLE ===
        model = new DefaultTableModel(new Object[]{"ID", "Name", "Email", "Message", "Submitted At", "Status"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };

        messageTable = new JTable(model);
        messageTable.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        messageTable.setRowHeight(35);
        messageTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        messageTable.getTableHeader().setBackground(new Color(52, 152, 219));
        messageTable.getTableHeader().setForeground(Color.WHITE);
        messageTable.setSelectionBackground(new Color(46, 204, 113));
        messageTable.setSelectionForeground(Color.WHITE);
        messageTable.setGridColor(new Color(230, 230, 230));
        messageTable.setShowHorizontalLines(true);

        // Custom cell renderer for status badges
        messageTable.getColumnModel().getColumn(5).setCellRenderer(new StatusRenderer());

        JScrollPane scrollPane = new JScrollPane(messageTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // === BOTTOM BUTTONS ===
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 15));
        bottomPanel.setBackground(Color.WHITE);
        btnRead = createButton("📖 Read Message", new Color(46, 204, 113));
        btnBack = createButton("⬅ Back", new Color(231, 76, 60));
        bottomPanel.add(btnRead);
        bottomPanel.add(btnBack);
        add(bottomPanel, BorderLayout.SOUTH);

        // === DATABASE CONNECTION ===
        connectDB();
        loadMessages();

        // === ACTIONS ===
        btnRead.addActionListener(e -> showSelectedMessage());
        btnBack.addActionListener(e -> dispose());
        btnRefresh.addActionListener(e -> loadMessages());
        btnAll.addActionListener(e -> { currentFilter = "ALL"; loadMessages(); });
        btnUnread.addActionListener(e -> { currentFilter = "UNREAD"; loadMessages(); });
        btnReadFilter.addActionListener(e -> { currentFilter = "READ"; loadMessages(); });

        txtSearch.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                searchMessages(txtSearch.getText());
            }
        });
    }

    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { button.setBackground(bgColor.darker()); }
            public void mouseExited(MouseEvent e) { button.setBackground(bgColor); }
        });
        return button;
    }

    private void connectDB() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital_db", "root", "");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database Connection Failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadMessages() {
        model.setRowCount(0);
        try {
            String sql = "SELECT * FROM contact_messages";
            if (currentFilter.equals("READ"))
                sql += " WHERE status='Read'";
            else if (currentFilter.equals("UNREAD"))
                sql += " WHERE status='Unread'";
            sql += " ORDER BY submitted_at DESC";

            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a");
            int count = 0;

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("message"),
                        sdf.format(rs.getTimestamp("submitted_at")),
                        rs.getString("status")
                });
                count++;
            }
            lblCount.setText("Messages: " + count);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading messages: " + e.getMessage());
        }
    }

    private void searchMessages(String keyword) {
        model.setRowCount(0);
        try {
            String sql = "SELECT * FROM contact_messages WHERE (name LIKE ? OR email LIKE ? OR message LIKE ?)";
            if (currentFilter.equals("READ"))
                sql += " AND status='Read'";
            else if (currentFilter.equals("UNREAD"))
                sql += " AND status='Unread'";
            sql += " ORDER BY submitted_at DESC";

            PreparedStatement pst = conn.prepareStatement(sql);
            String like = "%" + keyword + "%";
            pst.setString(1, like);
            pst.setString(2, like);
            pst.setString(3, like);
            ResultSet rs = pst.executeQuery();
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a");
            int count = 0;
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("message"),
                        sdf.format(rs.getTimestamp("submitted_at")),
                        rs.getString("status")
                });
                count++;
            }
            lblCount.setText("Messages: " + count);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Search Error: " + e.getMessage());
        }
    }

    private void showSelectedMessage() {
        int row = messageTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a message to read.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) model.getValueAt(row, 0);
        String name = (String) model.getValueAt(row, 1);
        String email = (String) model.getValueAt(row, 2);
        String message = (String) model.getValueAt(row, 3);
        String date = (String) model.getValueAt(row, 4);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setPreferredSize(new Dimension(500, 300));
        panel.add(new JLabel("<html><b>From:</b> " + name +
                "<br><b>Email:</b> " + email +
                "<br><b>Date:</b> " + date + "</html>"), BorderLayout.NORTH);

        JTextArea txtArea = new JTextArea(message);
        txtArea.setWrapStyleWord(true);
        txtArea.setLineWrap(true);
        txtArea.setEditable(false);
        txtArea.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txtArea.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        panel.add(new JScrollPane(txtArea), BorderLayout.CENTER);

        JOptionPane.showMessageDialog(this, panel, "📬 Message Details", JOptionPane.PLAIN_MESSAGE);

        markAsRead(id);
        loadMessages();
    }

    private void markAsRead(int id) {
        try {
            PreparedStatement pst = conn.prepareStatement("UPDATE contact_messages SET status='Read' WHERE id=?");
            pst.setInt(1, id);
            pst.executeUpdate();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error updating message status: " + e.getMessage());
        }
    }

    // === CUSTOM RENDERER FOR STATUS COLUMN ===
    static class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int col) {
            JLabel label = new JLabel(value != null ? value.toString() : "", SwingConstants.CENTER);
            label.setFont(new Font("Segoe UI", Font.BOLD, 13));
            label.setOpaque(true);
            label.setForeground(Color.WHITE);
            if ("Unread".equalsIgnoreCase(value.toString()))
                label.setBackground(new Color(231, 76, 60));
            else
                label.setBackground(new Color(46, 204, 113));
            if (isSelected) label.setBackground(label.getBackground().darker());
            return label;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminContactMessagesModern().setVisible(true));
    }
}


