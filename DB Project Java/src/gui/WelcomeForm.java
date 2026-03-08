package gui;

import javax.swing.*;
import java.awt.*;

public class WelcomeForm extends JFrame {
    public WelcomeForm() {
        setTitle("Welcome to Blood Bank Management System");
        setSize(600, 350);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        Image icon = Toolkit.getDefaultToolkit().getImage("C:\\Users\\Lenovo\\Desktop\\Project DB\\icons8-blood-drop-96.png");
        setIconImage(icon);

        JPanel panel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setPaint(new GradientPaint(0, 0, new Color(255, 255, 255),
                                               0, getHeight(), new Color(220, 235, 255)));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 20, 20, 20));

        // Icon Label
        ImageIcon logo = new ImageIcon("C:\\Users\\Lenovo\\Desktop\\Project DB\\Blood2.png");
        JLabel iconLabel = new JLabel(logo);
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Welcome Message
        JLabel welcomeLabel = new JLabel("Welcome to the Blood Bank Management System", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        welcomeLabel.setForeground(new Color(50, 50, 100));
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 0, 10));

        panel.add(iconLabel);
        panel.add(welcomeLabel);
        add(panel);

        setResizable(false);
        setVisible(true);

        // Show login after delay
        Timer timer = new Timer(2500, e -> {
            dispose();
            new LoginForm();
        });
        timer.setRepeats(false);
        timer.start();
    }
}
