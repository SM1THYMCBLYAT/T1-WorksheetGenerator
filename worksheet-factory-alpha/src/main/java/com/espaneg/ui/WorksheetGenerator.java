package com.espaneg.ui;

import com.espaneg.utils.ResourceLoader;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class
WorksheetGenerator {

    public static void main(String[] args) {

        JFrame frame = new JFrame();
        frame.setTitle("EduCreate – Worksheet Generator");
        frame.setSize(1400, 900);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        // --- ICON FIX 1: Logo loading ---
        ImageIcon logo = ResourceLoader.loadIcon("LOGO2.png" );
        if (logo != null) {
            frame.setIconImage(logo.getImage());
        }

        GradientPanel background = new GradientPanel();
        background.setLayout(null);
        frame.setContentPane(background);

        // ============================================================
        // LEFT PANEL
        // ============================================================
        RoundedPanel leftPanel = new RoundedPanel(0);
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBounds(0, 0, 270, 900);
        leftPanel.setLayout(null);
        background.add(leftPanel);

        JPanel leftContent = new JPanel();
        leftContent.setLayout(new BoxLayout(leftContent, BoxLayout.Y_AXIS));
        leftContent.setBackground(Color.WHITE);

        JScrollPane leftScroll = new JScrollPane(leftContent);
        leftScroll.setBounds(0, 120, 270, 780); // moved down to make space for header
        leftScroll.setBorder(null);
        leftPanel.add(leftScroll);

        // --- ICON FIX 2 & 3: Menu Icons loading ---
        JLabel menuIcon = new JLabel(ResourceLoader.loadIcon("HOME.png"));
        menuIcon.setBounds(20, 20, 30, 30);
        leftPanel.add(menuIcon);

        // Assuming a collapsed menu icon file name
        JLabel menuCollapseIcon = new JLabel(ResourceLoader.loadIcon("EXIT.png"));
        menuCollapseIcon.setBounds(220, 20, 30, 30);
        leftPanel.add(menuCollapseIcon);

        // ===== ADDED HEADER ABOVE SIDEBAR =====
        JLabel sidebarHeader = new JLabel("Worksheet Generator");
        sidebarHeader.setFont(new Font("SansSerif", Font.BOLD, 20));
        sidebarHeader.setForeground(new Color(50, 60, 80));
        sidebarHeader.setBounds(20, 70, 250, 30);
        leftPanel.add(sidebarHeader);

        // ============================================================
        // CANVAS
        // ============================================================
        JPanel canvas = new JPanel();
        canvas.setBackground(Color.WHITE);
        canvas.setBorder(new LineBorder(new Color(170, 170, 255), 2, true));
        canvas.setBounds(430, 140, 550, 650);
        background.add(canvas);

        // ============================================================
        // ADD SIDEBAR SECTIONS
        // ============================================================
        leftContent.add(createStudentDetailsSection(canvas));
        leftContent.add(section("Grid"));
        leftContent.add(section("Font"));
        leftContent.add(section("Import Content"));
        leftContent.add(section("Template Layouts"));
        leftContent.add(section("Colour Palette"));
        leftContent.add(section("Calculations"));
        leftContent.add(section(""));
        leftContent.add(section("Translate"));

        // ============================================================
        // SEARCH BAR
        // ============================================================
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

        // --- ICON FIX 4: Search Icon loading ---
        JLabel searchIcon = new JLabel(ResourceLoader.loadIcon("SEARCH.png"));
        searchIcon.setBounds(450, 10, 30, 30);
        searchBar.add(searchIcon);

        // ============================================================
        // TOP RIGHT BUTTONS
        // ============================================================
        RoundedButton exportButton = new RoundedButton("Export & Share");
        exportButton.setBounds(1000, 30, 160, 45);
        background.add(exportButton);

        RoundedButton moreButton = new RoundedButton("⋮");
        moreButton.setFont(new Font("SansSerif", Font.BOLD, 24));
        moreButton.setBounds(1180, 30, 60, 45);
        background.add(moreButton);

        // ============================================================
        // TOOLBAR (BOTTOM)
        // ============================================================
        RoundedPanel toolbar = new RoundedPanel(40);
        toolbar.setBackground(new Color(255, 255, 255, 140));
        toolbar.setBounds(350, 820, 700, 55);
        toolbar.setLayout(null);
        background.add(toolbar);

        // --- ICON FIX 5: Toolbar Icons (Corrected usage) ---
        // Note: Filenames must not include "images/" since ResourceLoader adds that path.
        toolbar.add(toolbarIcon("UNDO.png", 50));
        toolbar.add(toolbarIcon("ARROWLEFT.png", 120));
        toolbar.add(toolbarIcon("ALIGNLEFT.png", 190));
        toolbar.add(toolbarIcon("ALIGNCENTER.png", 260));
        toolbar.add(toolbarIcon("ALIGNRIGHT.png", 330));
        toolbar.add(toolbarIcon("ARROWRIGHT.png", 400));

        // Removed the redundant/unused ResourceLoader call here:
        //ImageIcon undoIcon = ResourceLoader.loadIcon("UNDO.png");

        // ============================================================
        // CHATBOT
        // ============================================================
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

    // ============================================================
    // STUDENT DETAILS SECTION (FULL FUNCTIONAL)
    // ============================================================
    public static JPanel createStudentDetailsSection(JPanel canvas) {

        JPanel sectionPanel = new JPanel(new BorderLayout());
        sectionPanel.setMaximumSize(new Dimension(250, 200));
        sectionPanel.setBackground(Color.WHITE);
        sectionPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JButton headerButton = new JButton("▼  Student Details");
        headerButton.setFocusPainted(false);
        headerButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        headerButton.setForeground(new Color(50, 60, 80));
        headerButton.setContentAreaFilled(false);
        headerButton.setBorderPainted(false);
        headerButton.setHorizontalAlignment(SwingConstants.LEFT);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Color.WHITE);
        content.setVisible(false);

        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField();
        nameField.setMaximumSize(new Dimension(230, 30));

        JLabel instructionsLabel = new JLabel("Instructions:");
        JTextArea instructionsArea = new JTextArea(4, 20);
        instructionsArea.setLineWrap(true);
        instructionsArea.setWrapStyleWord(true);
        JScrollPane scroll = new JScrollPane(instructionsArea);
        scroll.setMaximumSize(new Dimension(230, 80));

        content.add(nameLabel);
        content.add(nameField);
        content.add(Box.createVerticalStrut(5));
        content.add(instructionsLabel);
        content.add(scroll);

        headerButton.addActionListener(e -> {
            boolean visible = content.isVisible();
            content.setVisible(!visible);
            headerButton.setText((visible ? "▼  " : "▲  ") + "Student Details");
            sectionPanel.revalidate();
        });

        sectionPanel.add(headerButton, BorderLayout.NORTH);
        sectionPanel.add(content, BorderLayout.CENTER);

        // Canvas display
        JLabel display = new JLabel();
        display.setVerticalAlignment(SwingConstants.TOP);
        display.setFont(new Font("SansSerif", Font.PLAIN, 16));

        canvas.setLayout(new BorderLayout());
        canvas.add(display, BorderLayout.NORTH);

        Runnable refresh = () -> {
            String name = nameField.getText();
            String ins = instructionsArea.getText().replace("\n", "<br>");

            display.setText("<html><b>Name:</b> " + name +
                    "<br><br><b>Instructions:</b><br>" + ins + "</html>");

            canvas.revalidate();
            canvas.repaint();
        };

        nameField.getDocument().addDocumentListener(simpleListener(refresh));
        instructionsArea.getDocument().addDocumentListener(simpleListener(refresh));

        return sectionPanel;
    }

    // ============================================================
    // GENERIC COLLAPSIBLE SECTION
    // ============================================================
    public static JPanel section(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setMaximumSize(new Dimension(250, 50));
        panel.setBackground(Color.WHITE);

        JLabel label = new JLabel("▼  " + title);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(label, BorderLayout.NORTH);
        return panel;
    }

    // ============================================================
    // SUPPORT UTILITIES
    // ============================================================
    public static DocumentListener simpleListener(Runnable run) {
        return new DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { run.run(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { run.run(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { run.run(); }
        };
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

    /**
     * **FIXED:** Now uses ResourceLoader to correctly load the icon from the classpath.
     * @param filename The name of the icon file (e.g., "UNDO.png").
     * @param x The x-coordinate for setting bounds.
     * @return A JLabel containing the icon (or a text placeholder if missing).
     */
    public static JLabel toolbarIcon(String filename, int x) {

        ImageIcon imageIcon = ResourceLoader.loadIcon(filename); // <-- THE CRITICAL FIX
        JLabel icon;

        if (imageIcon != null) {
            icon = new JLabel(imageIcon);
        } else {
            // Fallback if the icon is missing
            System.err.println("Toolbar icon not found: " + filename);
            icon = new JLabel(filename.replace(".png", ""));
            icon.setForeground(Color.RED);
            icon.setFont(new Font("SansSerif", Font.BOLD, 10));
        }

        icon.setBounds(x, 12, 32, 32);
        icon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Normal, hover, and click background colours
        Color normal = new Color(255, 255, 255, 0);
        Color hover = new Color(255, 255, 255, 80);
        Color pressed = new Color(255, 255, 255, 140);

        icon.setOpaque(false);
        icon.setBackground(null);

        icon.addMouseListener(new java.awt.event.MouseAdapter() {

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                icon.setBackground(hover);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                icon.setBackground(normal);
            }

            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                icon.setBackground(pressed);
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                icon.setBackground(hover); // returns to hover state
            }
        });

        return icon;
    }
}