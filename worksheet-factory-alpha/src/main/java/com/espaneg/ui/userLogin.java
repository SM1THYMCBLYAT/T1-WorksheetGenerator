package com.espaneg.ui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class userLogin {

    public static void main(String[] args) {

        JFrame frame1 = new JFrame();
        frame1.setTitle("Educreate");                  // window title
        frame1.setSize(1000, 600);                     // size (fits design)
        frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame1.setLocationRelativeTo(null);            // center on screen

        try {
            ImageIcon logo = new ImageIcon("LOGO.png");
            frame1.setIconImage(logo.getImage());
        } catch (Exception ignored) {}

        GradientPanel background = new GradientPanel();
        background.setLayout(new BoxLayout(background, BoxLayout.Y_AXIS));
        frame1.setContentPane(background);

        JLabel title = new JLabel("Login"); //added title
        title.setFont(new Font("SansSerif", Font.BOLD, 40));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setBorder(BorderFactory.createEmptyBorder(40, 0, 6, 0));

        JLabel subtitle = new JLabel("Continue your journey by logging into your account"); //added subtitle
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 16));
        subtitle.setForeground(new Color(235, 240, 245));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        Component spacer = Box.createVerticalStrut(36);

        RoundedPanel card = new RoundedPanel(28);
        card.setBackground(new Color(255, 255, 255, 225));        // soft white
        card.setLayout(new GridLayout(3, 1, 14, 14));
        card.setMaximumSize(new Dimension(640, 210));
        card.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField emailField = new RoundedTextField("Email:");
        JPasswordField passwordField = new RoundedPasswordField("Password:");
        JPasswordField confirmField = new RoundedPasswordField("Confirm Password:");

        card.add(emailField);
        card.add(passwordField);
        card.add(confirmField);

        background.add(title);
        background.add(subtitle);
        background.add(spacer);
        background.add(card);

        frame1.setVisible(true);
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

    /** Rounded card panel */
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
            Shape r = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius);
            g2.setColor(getBackground());
            g2.fill(r);
            super.paintComponent(g);
        }
    }

    static class RoundedTextField extends JTextField {
        private final String placeholder;
        RoundedTextField(String placeholder) {
            this.placeholder = placeholder;
            setOpaque(false);
            setFont(new Font("SansSerif", Font.PLAIN, 16));
            setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
            super.paintComponent(g);
            if (getText().isEmpty() && !isFocusOwner()) {
                g2.setColor(new Color(150, 155, 160));
                g2.setFont(getFont());
                g2.drawString(placeholder, 16, getHeight() / 2 + getFont().getSize() / 2 - 3);
            }
        }
        @Override
        public void setBorder(Border border) {
            // keep internal padding only
            super.setBorder(border);
        }
    }

    static class RoundedPasswordField extends JPasswordField {
        private final String placeholder;
        RoundedPasswordField(String placeholder) {
            this.placeholder = placeholder;
            setOpaque(false);
            setFont(new Font("SansSerif", Font.PLAIN, 16));
            setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
            super.paintComponent(g);
            if (getPassword().length == 0 && !isFocusOwner()) {
                g2.setColor(new Color(150, 155, 160));
                g2.setFont(getFont());
                g2.drawString(placeholder, 16, getHeight() / 2 + getFont().getSize() / 2 - 3);
            }
        }
        @Override
        public void setBorder(Border border) {
            super.setBorder(border);
        }
    }
}
