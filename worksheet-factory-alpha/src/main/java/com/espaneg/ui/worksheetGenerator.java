package com.espaneg.ui;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class worksheetGenerator {

    public static void main(String[] args) {

        JFrame frame = new JFrame();
        frame.setTitle("EduCreate – Worksheet Generator");
        frame.setSize(1400, 900);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        ImageIcon logo = new ImageIcon("LOGO.png"); //textbox logo
        frame.setIconImage(logo.getImage());

        GradientPanel background = new GradientPanel();
        background.setLayout(null);
        frame.setContentPane(background);


        RoundedPanel leftPanel = new RoundedPanel(0);
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBounds(0, 0, 270, 900);
        leftPanel.setLayout(null);
        background.add(leftPanel);


        JPanel leftContent = new JPanel();
        leftContent.setLayout(new BoxLayout(leftContent, BoxLayout.Y_AXIS));
        leftContent.setBackground(Color.WHITE);


        JScrollPane leftScroll = new JScrollPane(leftContent);
        leftScroll.setBounds(0, 80, 270, 820);
        leftScroll.setBorder(null);
        leftPanel.add(leftScroll);


        JLabel menuIcon = new JLabel(new ImageIcon("USERICON.png"));
        menuIcon.setBounds(20, 20, 30, 30);
        leftPanel.add(menuIcon);

        JLabel menuCollapseIcon = new JLabel(new ImageIcon("LOgo2.png"));
        menuCollapseIcon.setBounds(220, 20, 30, 30);
        leftPanel.add(menuCollapseIcon);


        RoundedPanel searchBar = new RoundedPanel(40);
        searchBar.setBackground(new Color(255, 255, 255, 120));
        searchBar.setBounds(320, 30, 500, 50);
        searchBar.setLayout(null);
        background.add(searchBar);

        JTextField searchField = new JTextField("Search...");
        searchField.setBorder(null);
        searchField.setOpaque(false);
        searchField.setForeground(new Color(60, 60, 60));
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 18));
        searchField.setBounds(20, 10, 430, 30);
        searchBar.add(searchField);

        JLabel searchIcon = new JLabel(new ImageIcon("search.png"));
        searchIcon.setBounds(450, 10, 30, 30);
        searchBar.add(searchIcon);


        RoundedButton exportButton = new RoundedButton("Export & Share");
        exportButton.setBounds(1000, 30, 160, 45);
        background.add(exportButton);

        RoundedButton moreButton = new RoundedButton("⋮");
        moreButton.setFont(new Font("SansSerif", Font.BOLD, 24));
        moreButton.setBounds(1180, 30, 60, 45);
        background.add(moreButton);

        JPanel canvas = new JPanel();
        canvas.setBackground(Color.WHITE);
        canvas.setBorder(new LineBorder(new Color(170, 170, 255), 2, true));
        canvas.setBounds(430, 140, 550, 650);
        background.add(canvas);


        RoundedPanel toolbar = new RoundedPanel(40);
        toolbar.setBackground(new Color(255, 255, 255, 140));
        toolbar.setBounds(350, 820, 700, 55);
        toolbar.setLayout(null);
        background.add(toolbar);

        JLabel icon1 = toolbarIcon("undo.png", 50);
        JLabel icon2 = toolbarIcon("redo.png", 120);
        JLabel icon3 = toolbarIcon("align-left.png", 190);
        JLabel icon4 = toolbarIcon("align-center.png", 260);
        JLabel icon5 = toolbarIcon("align-right.png", 330);
        JLabel icon6 = toolbarIcon("refresh.png", 400);

        toolbar.add(icon1);
        toolbar.add(icon2);
        toolbar.add(icon3);
        toolbar.add(icon4);
        toolbar.add(icon5);
        toolbar.add(icon6);

        RoundedPanel chatbotBubble = new RoundedPanel(15);
        chatbotBubble.setBackground(Color.WHITE);
        chatbotBubble.setBounds(1100, 700, 180, 90);
        chatbotBubble.setLayout(null);

        JLabel chatLabel = new JLabel("<html>Click to<br>interact with<br>EduCreate<br>chatbot</html>");
        chatLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        chatLabel.setBounds(20, 10, 160, 70);
        chatbotBubble.add(chatLabel);

        background.add(chatbotBubble);

        frame.setVisible(true);
    }


    public static JPanel section(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setMaximumSize(new Dimension(250, 90));
        panel.setBackground(Color.WHITE);

        JLabel label = new JLabel(title);
        label.setFont(new Font("SansSerif", Font.BOLD, 16));
        label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(label, BorderLayout.NORTH);
        return panel;
    }



    static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;

            GradientPaint gp = new GradientPaint(
                    0, 0, new Color(180, 210, 230),
                    0, getHeight(), new Color(60, 90, 120)
            );

            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());
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

    static class RoundedButton extends JButton {
        RoundedButton(String text) {
            super(text);
            setFont(new Font("SansSerif", Font.PLAIN, 16));
            setForeground(new Color(50, 60, 80));
            setFocusPainted(false);
            setContentAreaFilled(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(new Color(255, 255, 255, 200));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);

            super.paintComponent(g);
        }
    }

    public static JLabel toolbarIcon(String filename, int x) {
        JLabel icon = new JLabel(new ImageIcon(filename));
        icon.setBounds(x, 12, 32, 32);
        return icon;
    }
}
