/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */package Pharmacy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class PharmacyDashboardModern extends JFrame {

    public PharmacyDashboardModern() {
        setTitle("Pharmacy Dashboard - Hospital Management System");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(34, 49, 63)); // dark navy
        headerPanel.setPreferredSize(new Dimension(getWidth(), 70));

        JLabel headerTitle = new JLabel("Pharmacy Dashboard");
        headerTitle.setForeground(Color.WHITE);
        headerTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        headerTitle.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 10));
        headerPanel.add(headerTitle, BorderLayout.WEST);

        JButton btnLogout = createLogoutButton("Logout");
        headerPanel.add(btnLogout, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Content panel for main buttons
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(new Color(245, 247, 250)); // light background
        contentPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(25, 25, 25, 25);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;

        String[] buttonLabels = {
            "View Medicines",
            "Add Medicine",
            "Inventory Management",
            "View Prescriptions",
            "Generate Bills",
            "Stock Alerts",
            "Expired Medicines"
        };

        JButton[] buttons = new JButton[buttonLabels.length];

        for (int i = 0; i < buttonLabels.length; i++) {
            buttons[i] = createMainButton(buttonLabels[i]);
            gbc.gridx = i % 3;
            gbc.gridy = i / 3;
            contentPanel.add(buttons[i], gbc);
        }

        // Button action placeholders - replace with your actual windows
        buttons[0].addActionListener(e -> {
            dispose();
            new ViewMedicinesModernViewOnly().setVisible(true);
        });
        buttons[1].addActionListener(e -> {
            dispose();
            new AddMedicineModern().setVisible(true);
        });
        buttons[2].addActionListener(e -> {
            dispose();
            new InventoryManagement().setVisible(true);
        });
        buttons[3].addActionListener(e -> {
            dispose();
            new ViewPrescriptionsPharmacyPanel().setVisible(true);
        });
        buttons[4].addActionListener(e -> {
            dispose();
            new PharmacyBillingSystem().setVisible(true);
        });
        buttons[5].addActionListener(e -> {
            dispose();
            new StockAlertPanel().setVisible(true);
        });
        buttons[6].addActionListener(e -> {
            dispose();
            new ExpiredMedicinesModule().setVisible(true);
        });

        btnLogout.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this, "Do you want to logout?", "Logout",
                    JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                dispose();
                new homepage.HospitalHomePage().setVisible(true);
            }
        });

        add(contentPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    private JButton createMainButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(280, 100));
        button.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 2, true));
        button.setOpaque(true);

        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(230, 240, 250));
                button.setBorder(BorderFactory.createLineBorder(new Color(100, 140, 190), 2, true));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(Color.WHITE);
                button.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 2, true));
            }
        });

        return button;
    }

    private JButton createLogoutButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(192, 57, 43));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(110, 40));
        button.setBorder(BorderFactory.createLineBorder(new Color(170, 50, 40), 2, true));
        button.setOpaque(true);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(231, 76, 60));
                button.setBorder(BorderFactory.createLineBorder(new Color(210, 80, 65), 2, true));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(192, 57, 43));
                button.setBorder(BorderFactory.createLineBorder(new Color(170, 50, 40), 2, true));
            }
        });

        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PharmacyDashboardModern::new);
    }
}
