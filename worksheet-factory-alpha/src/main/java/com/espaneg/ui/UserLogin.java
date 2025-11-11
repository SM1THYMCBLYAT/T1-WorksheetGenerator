package com.espaneg.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class UserLogin extends JFrame {


    public UserLogin() {

        setTitle("EduCreate - Login");
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);


        JPanel background = new GradientPanel();
        background.setLayout(new BoxLayout(background, BoxLayout.Y_AXIS));
        add(background);


        JLabel title = new JLabel("Login");
        title.setFont(new Font("SansSerif", Font.BOLD, 36));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setBorder(BorderFactory.createEmptyBorder(40, 0, 10, 0));


        JLabel subtitle = new JLabel("Continue your journey by logging into your account");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 16));
        subtitle.setForeground(new Color(240, 240, 240));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);


        RoundedPanel formPanel = getRoundedPanel();


        RoundedButton continueBtn = new RoundedButton("Continue");
        continueBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        continueBtn.setMaximumSize(new Dimension(200, 50));
        continueBtn.setBorder(BorderFactory.createEmptyBorder(15, 0, 20, 0));

        RoundedButton loginReturnToHome = new RoundedButton("Return");
        loginReturnToHome.setAlignmentX(Component.CENTER_ALIGNMENT);

        loginReturnToHome.setMaximumSize(new Dimension(200,50));

        loginReturnToHome.setBorder(BorderFactory.createEmptyBorder(15,0,20,0));

        loginReturnToHome.addActionListener(e -> {
            new HomePage();
            dispose();
        });

        background.add(title);
        background.add(subtitle);
        background.add(Box.createVerticalStrut(40));
        background.add(formPanel);
        background.add(Box.createVerticalStrut(30));
        background.add(continueBtn);
        background.add(loginReturnToHome);

        setVisible(true);
    }

    private static RoundedPanel getRoundedPanel() {
        RoundedPanel formPanel = new RoundedPanel(35);
        formPanel.setBackground(new Color(255, 255, 255, 200));
        formPanel.setLayout(new GridLayout(3, 1, 10, 10));
        formPanel.setMaximumSize(new Dimension(600, 200));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));


        JTextField email = new RoundedTextField("Email:");
        JPasswordField password = new RoundedPasswordField("Password:");
        JPasswordField confirmPassword = new RoundedPasswordField("Confirm Password:");

        formPanel.add(email);
        formPanel.add(password);
        formPanel.add(confirmPassword);
        return formPanel;
    }


    static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            GradientPaint gp = new GradientPaint(
                    0, 0, new Color(167, 195, 220),
                    0, getHeight(), new Color(110, 140, 170)
            );
            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
    }


    static class RoundedPanel extends JPanel {
        private final int cornerRadius;

        public RoundedPanel(int radius) {
            this.cornerRadius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Shape round = new RoundRectangle2D.Float(
                    0, 0, getWidth(), getHeight(),
                    cornerRadius, cornerRadius
            );

            g2.setColor(getBackground());
            g2.fill(round);
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


    static class RoundedButton extends JButton {
        public RoundedButton(String text) {
            super(text);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFont(new Font("SansSerif", Font.BOLD, 18));
            setForeground(new Color(60, 70, 85));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(new Color(255, 255, 255, 220));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

            super.paintComponent(g);
        }
    }
    //    zachary pojo implemt
    public static class LoginCredentials {
        private final String email;
        private final String password;

        public LoginCredentials(String email, String password) {
            this.email = email;
            this.password = password;
        }


        public String getEmail() {
            return email;
        }

        public String getPassword() {
            return password;
        }

    }

    public static void main(String[] args) {
        new UserLogin();
    }

}
