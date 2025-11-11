package com.espaneg.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class HomePage {

    public static void main(String[] args) {

        JFrame frame3 = new JFrame();
        frame3.setTitle("Educreate");
        frame3.setSize(1200, 700);
        frame3.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame3.setLocationRelativeTo(null);

        GradientPanel background = new GradientPanel();
        background.setLayout(null);
        frame3.setContentPane(background);

        JLabel logoIcon = new JLabel(new ImageIcon("logoWhite.png"));
        logoIcon.setBounds(380, 80, 80, 80);
        background.add(logoIcon);

        JLabel title = new JLabel("EduCreate");
        title.setFont(new Font("SansSerif", Font.BOLD, 48));
        title.setForeground(Color.WHITE);
        title.setBounds(470, 80, 500, 80);
        background.add(title);


        JLabel subtitle = new JLabel("ALL-IN-ONE WORKSHEET DESIGN PLATFORM");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 18));
        subtitle.setForeground(new Color(235, 240, 245));
        subtitle.setBounds(330, 160, 600, 40);
        background.add(subtitle);


        RoundedButton loginButton = new RoundedButton("Login to existing account");
        loginButton.setBounds(380, 250, 420, 55);
        background.add(loginButton);
        loginButton.addActionListener(e -> {
            new UserLogin();
            dispose();        
        });

//zachary:Added a link betweet login and userlogin.java
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Hide the current window
                frame3.setVisible(false);
                // Open the new UserLogin window
                new UserLogin();
            }
        });

        RoundedButton signupButton = new RoundedButton("Create a new account");
        signupButton.setBounds(380, 320, 420, 55);
        background.add(signupButton);


        RoundedPanel testimonial = new RoundedPanel(20);
        testimonial.setBackground(Color.WHITE);
        testimonial.setBounds(860, 250, 250, 180);
        testimonial.setLayout(null);

        JLabel stars = new JLabel("★★★★★");
        stars.setFont(new Font("SansSerif", Font.PLAIN, 22));
        stars.setBounds(20, 10, 200, 30);
        testimonial.add(stars);

        JLabel titleTest = new JLabel("PERFECTION");
        titleTest.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleTest.setBounds(20, 40, 200, 30);
        testimonial.add(titleTest);

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

        frame3.setVisible(true);
    }

    private static void dispose() {
    }


    static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;

            GradientPaint gp = new GradientPaint(
                    0, 0, new Color(220, 235, 245),
                    0, getHeight(), new Color(95, 130, 160)
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
