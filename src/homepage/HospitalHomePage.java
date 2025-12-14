/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package homepage;

import patient.*;
import doctor.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import Admin.*; // Make sure InsertDoctorApp, AboutPage, ContactPage are here or imported
import Patient.PatientProgressBar;

public class HospitalHomePage extends JFrame {

    JPanel headerPanel, contentPanel;
    JLabel homeLabel, aboutLabel, contactLabel;

    public HospitalHomePage() {
        setTitle("Hospital Management System");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initHeader();
        initCards();

        setVisible(true);
    }

    private void initHeader() {
        headerPanel = new JPanel();
        headerPanel.setBackground(new Color(10, 66, 117));
        headerPanel.setPreferredSize(new Dimension(1000, 80));
        headerPanel.setLayout(new BorderLayout());

        JLabel title = new JLabel("  Hospital Management System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        headerPanel.add(title, BorderLayout.WEST);

        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 25));
        navPanel.setOpaque(false);

        homeLabel = navLabel("Home");
        aboutLabel = navLabel("About");
        contactLabel = navLabel("Contact");

        navPanel.add(homeLabel);
        navPanel.add(aboutLabel);
        navPanel.add(contactLabel);
        headerPanel.add(navPanel, BorderLayout.EAST);

        homeLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
                new HospitalHomePage();
            }
        });

        aboutLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
                new AboutPage(); // Replace with your real About page class
            }
        });

        contactLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
                new ContactPage(); // Replace with your real Contact page class
            }
        });

        add(headerPanel, BorderLayout.NORTH);
    }

    private JLabel navLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        label.setForeground(Color.WHITE);
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));
        label.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                label.setForeground(Color.YELLOW);
            }

            public void mouseExited(MouseEvent e) {
                label.setForeground(Color.WHITE);
            }
        });
        return label;
    }

    private void initCards() {
        contentPanel = new JPanel();
        contentPanel.setBackground(new Color(242, 242, 242));
        contentPanel.setLayout(new GridBagLayout());
        add(contentPanel, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 30, 20, 30);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.5;
        gbc.weighty = 0.5;

        gbc.gridx = 0;
        gbc.gridy = 0;
        contentPanel.add(new CardPanel("Admin Panel", "Manage admins and system settings", "admin.png", () -> {
           dispose();
           new AdminProgressBar().setVisible(true);
            
        }), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        contentPanel.add(new CardPanel("Doctor Panel", "View appointments and patient history", "doctor.png", () -> {
            dispose();
            new DoctorProgressBar().setVisible(true);
        }), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        contentPanel.add(new CardPanel("Patient Panel", "Manage patient data and reports", "patient.png", () -> {
           dispose();
           new PatientProgressBar().setVisible(true);
        }), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        contentPanel.add(new CardPanel("Pharmacy Panel", "Handle medicines and stock", "pharmacy.png", () -> {
            dispose();
            new Pharmacy.PharmacyProgressBar().setVisible(true);
        }), gbc);
    }

    class CardPanel extends JPanel {
        public CardPanel(String title, String description, String iconPath, Runnable action) {
            setLayout(new BorderLayout());
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            JPanel textPanel = new JPanel();
            textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
            textPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
            textPanel.setOpaque(false);

            JLabel heading = new JLabel(title);
            heading.setFont(new Font("Segoe UI", Font.BOLD, 20));
            textPanel.add(heading);

            JLabel desc = new JLabel("<html><p style='width:280px'>" + description + "</p></html>");
            desc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            textPanel.add(Box.createVerticalStrut(10));
            textPanel.add(desc);

            JButton openButton = new JButton("Open");
            openButton.setBackground(new Color(10, 66, 117));
            openButton.setForeground(Color.WHITE);
            openButton.setFocusPainted(false);
            openButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            openButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            openButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

            openButton.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    openButton.setBackground(new Color(6, 51, 92));
                }

                public void mouseExited(MouseEvent e) {
                    openButton.setBackground(new Color(10, 66, 117));
                }
            });

            openButton.addActionListener(e -> action.run());

            JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            bottomPanel.setOpaque(false);
            bottomPanel.add(openButton);

            add(textPanel, BorderLayout.CENTER);
            add(bottomPanel, BorderLayout.SOUTH);
        }
    }

    public static void main(String[] args) {
        new HospitalHomePage();
    }
}
