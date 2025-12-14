/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package homepage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AboutPage extends JFrame {

    JLabel homeLabel, aboutLabel, contactLabel;

    public AboutPage() {
        setTitle("About - Hospital Management System");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initHeader();
        initContent();

        setVisible(true);
    }

    private void initHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(10, 66, 117));
        headerPanel.setPreferredSize(new Dimension(1000, 80));
        add(headerPanel, BorderLayout.NORTH);

        JLabel title = new JLabel("  Hospital Management System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        headerPanel.add(title, BorderLayout.WEST);

        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 30, 25));
        navPanel.setOpaque(false);

        homeLabel = createNavLabel("Home");
        aboutLabel = createNavLabel("About");
        contactLabel = createNavLabel("Contact");

        aboutLabel.setForeground(Color.YELLOW); // Highlight current page

        homeLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                dispose();
                new HospitalHomePage();
            }
        });

        contactLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                dispose();
                new ContactPage();
            }
        });

        navPanel.add(homeLabel);
        navPanel.add(aboutLabel);
        navPanel.add(contactLabel);

        headerPanel.add(navPanel, BorderLayout.EAST);
    }

    private JLabel createNavLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        label.setForeground(Color.WHITE);
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));

        label.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                label.setForeground(Color.YELLOW);
            }

            public void mouseExited(MouseEvent e) {
                if (!label.getText().equals("About")) {
                    label.setForeground(Color.WHITE);
                }
            }
        });

        return label;
    }

    private void initContent() {
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(new Color(245, 245, 245));
        add(contentPanel, BorderLayout.CENTER);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(750, 450));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 210, 210)),
                BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));

        JLabel heading = new JLabel("Welcome to Our Hospital Management System");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 22));
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(heading);

        card.add(Box.createRigidArea(new Dimension(0, 20)));

        JTextArea bodyText = new JTextArea();
        bodyText.setText(
                "Our Hospital Management System is a modern and secure solution designed to simplify the complex processes of hospital administration.\n\n"
                        + "It provides a centralized platform to manage appointments, patient records, doctor schedules, pharmacy inventory, billing, and more. "
                        + "The system offers real-time access, automated workflows, and an intuitive interface to enhance efficiency, reduce paperwork, and improve the overall patient experience.\n\n"
                        + "Whether you're managing a large hospital or a small clinic, our system ensures that healthcare delivery is more organized, accurate, and effective."
        );
        bodyText.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        bodyText.setEditable(false);
        bodyText.setLineWrap(true);
        bodyText.setWrapStyleWord(true);
        bodyText.setBackground(Color.WHITE);
        bodyText.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(bodyText);

        card.add(Box.createVerticalGlue());
        card.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel version = new JLabel("Version: 1.0   |   Developed by Mayank Duapanjabi");
        version.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        version.setForeground(Color.GRAY);
        version.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(version);

        contentPanel.add(card);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AboutPage::new);
    }
}


