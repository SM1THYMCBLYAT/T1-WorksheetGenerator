package com.espaneg.ui;

import javax.swing.*;
import java.awt.*;

public class AccountCreation {

    private JFrame frame;

    public AccountCreation() {
        initializeUI();
    }

    private void initializeUI() {
        frame = new JFrame("EduCreate");
        frame.setSize(1200, 750);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        // Window icon
        frame.setIconImage(new ImageIcon("LOGO.png").getImage());

        GradientPanel background = new GradientPanel();
        background.setLayout(null);
        frame.setContentPane(background);

        addTitle(background);
        addProfilePictureSection(background);
        addForm(background);

        frame.setVisible(true);
    }

    private void addTitle(JPanel background) {
        JLabel title = new JLabel("Create your profile");
        title.setFont(new Font("SansSerif", Font.BOLD, 40));
        title.setForeground(Color.WHITE);
        title.setBounds(380, 40, 500, 60);
        background.add(title);
    }

    private void addProfilePictureSection(JPanel background) {

        JLabel userIcon = new JLabel(new ImageIcon("USERICON.png"));
        userIcon.setBounds(520, 140, 150, 150);
        background.add(userIcon);

        RoundedButton takePhoto = new RoundedButton("Take Photo");
        takePhoto.setBounds(470, 300, 150, 35);
        background.add(takePhoto);

        RoundedButton importPhoto = new RoundedButton("Import Photo");
        importPhoto.setBounds(630, 300, 160, 35);
        background.add(importPhoto);
    }

    private void addForm(JPanel background) {

        RoundedPanel form = new RoundedPanel(35);
        form.setBackground(new Color(255, 255, 255, 225));
        form.setLayout(new GridLayout(5, 1, 15, 15));
        form.setBounds(260, 360, 680, 280);
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        background.add(form);

        form.add(new RoundedTextField("Full name:"));
        form.add(new RoundedTextField("Email:"));
        form.add(new RoundedTextField("Mobile Number:"));
        form.add(new RoundedPasswordField("Password:"));
        form.add(new RoundedPasswordField("Confirm Password:"));
    }

    static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;

            g2.setPaint(new GradientPaint(
                    0, 0, new Color(180, 210, 230),
                    0, getHeight(), new Color(100, 140, 170)
            ));

            g2.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    static class RoundedButton extends JButton {
        RoundedButton(String text) {
            super(text);
            setFont(new Font("SansSerif", Font.PLAIN, 14));
            setForeground(new Color(70, 70, 70));
            setFocusPainted(false);
            setContentAreaFilled(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(new Color(255, 255, 255, 200));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

            super.paintComponent(g);
        }
    }

    static class RoundedPanel extends JPanel {
        private final int radius;

        public RoundedPanel(int radius) {
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

            super.paintComponent(g);
        }
    }

    static class RoundedTextField extends JTextField {
        private final String placeholder;

        public RoundedTextField(String placeholder) {
            this.placeholder = placeholder;
            setOpaque(false);
            setFont(new Font("SansSerif", Font.PLAIN, 16));
            setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);

            super.paintComponent(g);

            if (getText().isEmpty() && !isFocusOwner()) {
                g2.setColor(new Color(160, 160, 160));
                g2.drawString(placeholder, 15, getHeight() / 2 + 5);
            }
        }
    }

    static class RoundedPasswordField extends JPasswordField {
        private final String placeholder;

        public RoundedPasswordField(String placeholder) {
            this.placeholder = placeholder;
            setOpaque(false);
            setFont(new Font("SansSerif", Font.PLAIN, 16));
            setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);

            super.paintComponent(g);

            if (getPassword().length == 0 && !isFocusOwner()) {
                g2.setColor(new Color(160, 160, 160));
                g2.drawString(placeholder, 15, getHeight() / 2 + 5);
            }
        }
    }
}
