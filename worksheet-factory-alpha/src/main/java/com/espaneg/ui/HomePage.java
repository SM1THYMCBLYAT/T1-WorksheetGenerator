package com.espaneg.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class HomePage {

    private JFrame frame;  // Global frame reference

    public HomePage() {
        initializeUI();
    }

    private void initializeUI() {
        frame = new JFrame("EduCreate");
        frame.setSize(1200, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        GradientPanel background = new GradientPanel();
        background.setLayout(null);
        frame.setContentPane(background);

        addLogo(background);
        addTitle(background);
        addSubtitle(background);
        addButtons(background);
        addTestimonial(background);

        frame.setVisible(true);
    }

    private void addLogo(JPanel background) {
        JLabel logo = new JLabel(new ImageIcon("logoWhite.png"));
        logo.setBounds(380, 80, 80, 80);
        background.add(logo);
    }

    private void addTitle(JPanel background) {
        JLabel title = new JLabel("EduCreate");
        title.setFont(new Font("SansSerif", Font.BOLD, 48));
        title.setForeground(Color.WHITE);
        title.setBounds(470, 80, 500, 80);
        background.add(title);
    }

    private void addSubtitle(JPanel background) {
        JLabel subtitle = new JLabel("ALL-IN-ONE WORKSHEET DESIGN PLATFORM");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 18));
        subtitle.setForeground(new Color(235, 240, 245));
        subtitle.setBounds(330, 160, 600, 40);
        background.add(subtitle);
    }

    private void addButtons(JPanel background) {

        // LOGIN BUTTON
        RoundedButton loginButton = new RoundedButton("Login to existing account");
        loginButton.setBounds(380, 250, 420, 55);
        loginButton.addActionListener(this::openLogin);
        background.add(loginButton);

        // SIGNUP BUTTON
        RoundedButton signupButton = new RoundedButton("Create a new account");
        signupButton.setBounds(380, 320, 420, 55);
        signupButton.addActionListener(this::openSignup);
        background.add(signupButton);
    }


    private void openLogin(ActionEvent e) {
        frame.dispose();
        new UserLogin();
    }

    private void openSignup(ActionEvent e) {
        frame.dispose();
        new AccountCreation();
    }

    private void addTestimonial(JPanel background) {
        RoundedPanel testimonial = new RoundedPanel(20);
        testimonial.setBackground(Color.WHITE);
        testimonial.setBounds(860, 250, 250, 180);
        testimonial.setLayout(null);

        JLabel stars = new JLabel("★★★★★");
        stars.setFont(new Font("SansSerif", Font.PLAIN, 22));
        stars.setBounds(20, 10, 200, 30);
        testimonial.add(stars);

        JLabel title = new JLabel("PERFECTION");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setBounds(20, 40, 200, 30);
        testimonial.add(title);

        JLabel body = new JLabel("<html>This is the best worksheet generator<br>I've ever worked with.</html>");
        body.setFont(new Font("SansSerif", Font.PLAIN, 14));
        body.setBounds(20, 70, 220, 60);
        testimonial.add(body);

        JLabel username = new JLabel("Madeline   03/18/2024");
        username.setFont(new Font("SansSerif", Font.PLAIN, 12));
        username.setForeground(new Color(70, 70, 70));
        username.setBounds(20, 130, 200, 20);
        testimonial.add(username);

        background.add(testimonial);
    }

    static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;

            GradientPaint gp = new GradientPaint(0, 0, new Color(220, 235, 245), 0, getHeight(), new Color(95, 130, 160)
            );

            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    static class RoundedButton extends JButton {
        RoundedButton(String text) {
            super(text);
            setFont(new Font("SansSerif", Font.PLAIN, 18));
            setForeground(new Color(50, 60, 80));
            setFocusPainted(false);
            setContentAreaFilled(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(255, 255, 255, 180));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
            super.paintComponent(g);
        }
    }

    static class RoundedPanel extends JPanel {
        private final int radius;

        RoundedPanel(int radius) {
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
}
