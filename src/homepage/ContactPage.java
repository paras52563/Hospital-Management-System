/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package homepage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Properties;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class ContactPage extends JFrame {

    JLabel homeLabel, aboutLabel, contactLabel;
    private JTextField nameField, emailField;
    private JTextArea messageArea;

    public ContactPage() {
        setTitle("Contact - Hospital Management System");
        setSize(1000, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
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

        contactLabel.setForeground(Color.YELLOW); // Highlight current page

        homeLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                dispose();
                new HospitalHomePage(); // Make sure this class exists
            }
        });

        aboutLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                dispose();
                new AboutPage();
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
                if (!label.getText().equals("Contact")) {
                    label.setForeground(Color.WHITE);
                }
            }
        });

        return label;
    }

    private void initContent() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(245, 245, 245));
        add(mainPanel, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 30, 20, 30);
        gbc.fill = GridBagConstraints.BOTH;

        // Left panel: Contact Information Card
        JPanel infoCard = new JPanel();
        infoCard.setLayout(new BoxLayout(infoCard, BoxLayout.Y_AXIS));
        infoCard.setBackground(Color.WHITE);
        infoCard.setPreferredSize(new Dimension(400, 450));
        infoCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 210, 210)),
                BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));

        JLabel infoTitle = new JLabel("Contact Information");
        infoTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        infoTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoCard.add(infoTitle);

        infoCard.add(Box.createRigidArea(new Dimension(0, 25)));

        infoCard.add(makeInfoLabel("🏥  MediCare Multi-specialty Hospital"));
        infoCard.add(makeInfoLabel("📍  123 Health Ave, Healing City, India"));
        infoCard.add(makeInfoLabel("📞  +91 98765 43210"));
        infoCard.add(makeInfoLabel("✉️  support@medicarehospital.com"));
        infoCard.add(makeInfoLabel("⏰  Mon - Sat: 8:00 AM - 8:00 PM"));

        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(infoCard, gbc);

        // Right panel: Contact Form Card
        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBackground(Color.WHITE);
        formCard.setPreferredSize(new Dimension(500, 450));
        formCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 210, 210)),
                BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));

        GridBagConstraints fgbc = new GridBagConstraints();
        fgbc.insets = new Insets(12, 12, 12, 12);
        fgbc.anchor = GridBagConstraints.WEST;
        fgbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel formTitle = new JLabel("Send Us a Message");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        formTitle.setForeground(new Color(10, 66, 117));
        fgbc.gridx = 0;
        fgbc.gridy = 0;
        fgbc.gridwidth = 2;
        formCard.add(formTitle, fgbc);

        fgbc.gridwidth = 1;
        fgbc.gridy++;

        formCard.add(new JLabel("Your Name:"), fgbc);
        nameField = new JTextField(25);
        fgbc.gridx = 1;
        formCard.add(nameField, fgbc);

        fgbc.gridx = 0;
        fgbc.gridy++;
        formCard.add(new JLabel("Your Email:"), fgbc);
        emailField = new JTextField(25);
        fgbc.gridx = 1;
        formCard.add(emailField, fgbc);

        fgbc.gridx = 0;
        fgbc.gridy++;
        formCard.add(new JLabel("Message:"), fgbc);
        messageArea = new JTextArea(6, 25);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(messageArea);
        fgbc.gridx = 1;
        formCard.add(scrollPane, fgbc);

        fgbc.gridx = 1;
        fgbc.gridy++;
        JButton sendBtn = new JButton("Send Message");
        sendBtn.setBackground(new Color(10, 66, 117));
        sendBtn.setForeground(Color.WHITE);
        sendBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        sendBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        sendBtn.addActionListener(e -> sendEmail());
        formCard.add(sendBtn, fgbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        mainPanel.add(formCard, gbc);
    }

    private JLabel makeInfoLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        label.setForeground(new Color(50, 50, 50));
        label.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private void sendEmail() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String message = messageArea.getText().trim();

        if (name.isEmpty() || email.isEmpty() || message.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String to = "aroramayank488@gmail.com";  // Your receiving email
        String from = email;

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        String user = "aroramayank488@gmail.com"; // Your Gmail address
        String pass = "zojw kkfv hdyl etih";       // App password

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, pass);
            }
        });

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(from));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            msg.setSubject("Query from " + name);
            msg.setText("Name: " + name + "\nEmail: " + email + "\n\nMessage:\n" + message);
            Transport.send(msg);

            JOptionPane.showMessageDialog(this, "Message sent successfully!");
            nameField.setText("");
            emailField.setText("");
            messageArea.setText("");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to send message.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ContactPage::new);
    }
}





