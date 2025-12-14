/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Pharmacy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class PharmacyProgressBar extends JFrame {
    private JProgressBar progressBar;
    private Timer timer;
    private int progress = 0;

    public PharmacyProgressBar() {
        setTitle("Loading Pharmacy Module...");
        setSize(500, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(245, 249, 250));
        setLayout(new GridBagLayout());

        JLabel title = new JLabel("Pharmacy System - Loading");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(40, 40, 40));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        progressBar = new JProgressBar(0, 100) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int width = getWidth();
                int height = getHeight();
                int arc = 30;

                // Background
                g2.setColor(new Color(200, 210, 220));
                g2.fillRoundRect(0, 0, width, height, arc, arc);

                // Progress fill with pharmacy-blue gradient
                int fillWidth = (int) (width * getPercentComplete());
                GradientPaint gradient = new GradientPaint(0, 0, new Color(0, 172, 193), 
                                                           width, height, new Color(0, 96, 100));
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, fillWidth, height, arc, arc);

                // Progress text
                String text = getValue() + "%";
                g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
                FontMetrics fm = g2.getFontMetrics();
                int textWidth = fm.stringWidth(text);
                int textHeight = fm.getAscent();
                g2.setColor(Color.WHITE);
                g2.drawString(text, (width - textWidth) / 2, (height + textHeight) / 2 - 2);

                g2.dispose();
            }
        };

        progressBar.setPreferredSize(new Dimension(350, 35));
        progressBar.setBorderPainted(false);
        progressBar.setStringPainted(false);
        progressBar.setValue(0);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(title, gbc);

        gbc.gridy = 1;
        add(progressBar, gbc);

        // Simulate loading with Timer
        timer = new Timer(35, (ActionEvent e) -> {
            progress++;
            progressBar.setValue(progress);
            progressBar.repaint();
            if (progress >= 100) {
                timer.stop();
                dispose();
                new PharmacyLoginGUI().setVisible(true);
            }
        });

        timer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PharmacyProgressBar().setVisible(true));
    }
}

