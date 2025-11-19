package com.espaneg.ui;

import com.espaneg.model.WorksheetSettings;
import com.espaneg.utils.ResourceLoader;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.File;

public class
WorksheetGenerator {

    public static void main(String[] args) {

        JFrame frame = new JFrame();
        frame.setTitle("EduCreate – Worksheet Generator");
        frame.setSize(1400, 900);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        // --- ICON FIX 1: Logo loading ---
        ImageIcon logo = ResourceLoader.loadIcon("LOGO2.png");
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
        leftContent.setOpaque(false);


        JScrollPane leftScroll = new JScrollPane(leftContent);
        leftScroll.setBounds(0, 120, 270, 780); // moved down to make space for header
        leftScroll.setBorder(null);
        leftPanel.add(leftScroll);
        leftScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

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
        com.espaneg.model.WorksheetSettings settings =
                new com.espaneg.model.WorksheetSettings("Default", "Tracing Letters", 20);

        // ============================================================
        // ADD SIDEBAR SECTIONS
        // ============================================================
        leftContent.add(createStudentDetailsSection(canvas));
        leftContent.add(gridSection(canvas, settings));
        leftContent.add(fontSection());
        leftContent.add(importContentSection());
        leftContent.add(colorPaletteSection());
        leftContent.add(calculationsSection());
        leftContent.add(quickFillSection());
        leftContent.add(templateSection());

//        leftContent.add(section(""));

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

        // ===== THREE DOTS DROPDOWN MENU =====
        JPopupMenu moreMenu = new JPopupMenu() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Soft white/grey rounded menu background
                g2.setColor(new Color(255, 255, 255, 240));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
            }
        };

        moreMenu.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        moreMenu.setOpaque(false); // allows custom rounded BG

        JMenuItem settingsItem = new JMenuItem("Leave a review");
        JMenuItem helpItem = new JMenuItem("Contact us");
        JMenuItem aboutItem = new JMenuItem("FAQs");
        JMenuItem exitItem = new JMenuItem("Exit");

        Font menuFont = new Font("SansSerif", Font.PLAIN, 14);
        settingsItem.setFont(menuFont);
        helpItem.setFont(menuFont);
        aboutItem.setFont(menuFont);
        exitItem.setFont(menuFont);

        moreMenu.add(settingsItem);
        moreMenu.add(helpItem);
        moreMenu.add(aboutItem);
        moreMenu.addSeparator();
        moreMenu.add(exitItem);

// EXIT button closes the app
        exitItem.addActionListener(e -> System.exit(0));

// Show dropdown under the button
        moreButton.addActionListener(e -> {
            moreMenu.show(moreButton, 0, moreButton.getHeight());
        });


        // ============================================================
        // TOOLBAR (BOTTOM)
        // ============================================================
        RoundedPanel toolbar = new RoundedPanel(40);
        toolbar.setBackground(new Color(255, 255, 255, 140));
        toolbar.setBounds(350, 820, 700, 60);
        toolbar.setLayout(new BorderLayout());

        background.add(toolbar);

        // --- ICON FIX 5: Toolbar Icons (Corrected usage) ---
        // Note: Filenames must not include "images/" since ResourceLoader adds that path.
// LEFT ICON GROUP
        JPanel iconGroup = new JPanel();
        iconGroup.setOpaque(false);
        iconGroup.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 10)); // spacing and alignment

        iconGroup.add(toolbarIcon("UNDO.png", 0));
        iconGroup.add(toolbarIcon("ARROWLEFT.png", 0));
        iconGroup.add(toolbarIcon("ALIGNLEFT.png", 0));
        iconGroup.add(toolbarIcon("ALIGNCENTER.png", 0));
        iconGroup.add(toolbarIcon("ALIGNRIGHT.png", 0));
        iconGroup.add(toolbarIcon("ARROWRIGHT.png", 0));
        iconGroup.add(toolbarIcon("REDO.png", 0));
        toolbar.add(iconGroup, BorderLayout.WEST);

        // RIGHT BUTTON GROUP
        JPanel rightButtons = new JPanel();
        rightButtons.setOpaque(false);
        rightButtons.setLayout(new FlowLayout(FlowLayout.RIGHT, 15, 10));

        RoundedButton autosaveBtn = new RoundedButton("AutoSave");
        RoundedButton saveBtn = new RoundedButton(" Zoom ");

        autosaveBtn.setPreferredSize(new Dimension(150, 34));
        saveBtn.setPreferredSize(new Dimension(100, 34));

        rightButtons.add(autosaveBtn);
        rightButtons.add(saveBtn);

        toolbar.add(rightButtons, BorderLayout.EAST);

//        // ZOOM BUTTON (opens zoom menu)
//        toolbar.add(createZoomButton(540, canvas));

        // ============================================================
        // CHATBOT LAUNCHER ICON
        // ============================================================
        JLabel chatLauncher = new JLabel(ResourceLoader.loadIcon("CHAT.png"));
        chatLauncher.setBounds(1100, 700, 60, 60);
        chatLauncher.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        background.add(chatLauncher);
        // POPUP CHAT WINDOW
        JPanel chatPopup = createChatPopup();
        background.add(chatPopup);
        background.setComponentZOrder(chatPopup, 0);

// Toggle when clicked
        chatLauncher.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                chatPopup.setVisible(!chatPopup.isVisible());
            }
        });

        frame.setVisible(true);
    }

    // ============================================================
    // STUDENT DETAILS SECTION (FULL FUNCTIONAL)
    // ============================================================
    public static JPanel createStudentDetailsSection(JPanel canvas) {

        // Outer card with shadow
        RoundedPanel sectionPanel = new RoundedPanel(25) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Shadow
                g2.setColor(new Color(0, 0, 0, 55));
                g2.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 25, 25);

                // Card background
                g2.setColor(new Color(255, 255, 255, 230));
                g2.fillRoundRect(0, 0, getWidth() - 6, getHeight() - 6, 25, 25);

                super.paintComponent(g2);
            }
        };

        sectionPanel.setLayout(new BorderLayout());
        sectionPanel.setOpaque(false);
        sectionPanel.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        sectionPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));

        // --------------------------
        // Header gradient bar
        // --------------------------
        JPanel headerBar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(120, 140, 170),
                        getWidth(), getHeight(), new Color(90, 110, 140)
                );
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);

                super.paintComponent(g2);
            }
        };
        headerBar.setOpaque(false);
        headerBar.setLayout(new BorderLayout());
        headerBar.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        JLabel headerLabel = new JLabel("Student Details");
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        headerLabel.setForeground(Color.WHITE);

        headerBar.add(headerLabel, BorderLayout.WEST);
        sectionPanel.add(headerBar, BorderLayout.NORTH);

        // Divider
        JPanel divider = new JPanel();
        divider.setPreferredSize(new Dimension(200, 1));
        divider.setBackground(new Color(180, 180, 180));

        // --------------------------
        // Content area
        // --------------------------
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        content.add(Box.createVerticalStrut(10));
        content.add(divider);
        content.add(Box.createVerticalStrut(12));

        // Labels
        JLabel nameLabel = new JLabel("Name:");
        JLabel instructionsLabel = new JLabel("Instructions:");

        // --------------------------
        // Name field (rounded + centered)
        // --------------------------
        JPanel nameWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        nameWrapper.setOpaque(false);

        JTextField nameField = new JTextField();
        nameField.setPreferredSize(new Dimension(180, 32));
        nameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1, true),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        nameField.setBackground(Color.WHITE);

        nameWrapper.add(nameField);

        // --------------------------
        // Instructions field (rounded + centered)
        // --------------------------
        JPanel instructionsWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        instructionsWrapper.setOpaque(false);

        JTextArea instructionsArea = new JTextArea(4, 20);
        instructionsArea.setLineWrap(true);
        instructionsArea.setWrapStyleWord(true);

        JScrollPane scroll = new JScrollPane(instructionsArea);
        scroll.setPreferredSize(new Dimension(180, 80));
        scroll.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1, true),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        instructionsArea.setBackground(Color.WHITE);

        instructionsWrapper.add(scroll);

        // Add to content
        content.add(nameLabel);
        content.add(Box.createVerticalStrut(4));
        content.add(nameWrapper);

        content.add(Box.createVerticalStrut(12));
        content.add(instructionsLabel);
        content.add(Box.createVerticalStrut(4));
        content.add(instructionsWrapper);

        sectionPanel.add(content, BorderLayout.CENTER);

        // --------------------------
        // Canvas live update
        // --------------------------
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
// GRID SECTION (NOW FUNCTIONAL – CONNECTED TO WORKSHEETSETTINGS)
// ============================================================
    public static JPanel gridSection(JPanel canvas, WorksheetSettings settings) {

        // ---------- OUTER CARD ----------
        RoundedPanel outer = new RoundedPanel(25) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Shadow
                g2.setColor(new Color(0, 0, 0, 55));
                g2.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 25, 25);

                // Card background
                g2.setColor(new Color(255, 255, 255, 230));
                g2.fillRoundRect(0, 0, getWidth() - 6, getHeight() - 6, 25, 25);

                super.paintComponent(g2);
            }
        };

        outer.setOpaque(false);
        outer.setLayout(new BorderLayout());
        outer.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        outer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 330));

        // ---------- HEADER BAR ----------
        JPanel headerBar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(120, 140, 170),
                        getWidth(), getHeight(), new Color(90, 110, 140)
                );
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                super.paintComponent(g2);
            }
        };

        headerBar.setOpaque(false);
        headerBar.setLayout(new BorderLayout());
        headerBar.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        JLabel headerLabel = new JLabel("Grid");
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        headerLabel.setForeground(Color.WHITE);

        JLabel arrow = new JLabel("▼");
        arrow.setForeground(Color.WHITE);
        arrow.setFont(new Font("SansSerif", Font.BOLD, 16));

        headerBar.add(headerLabel, BorderLayout.WEST);
        headerBar.add(arrow, BorderLayout.EAST);

        outer.add(headerBar, BorderLayout.NORTH);

        // ---------- CONTENT PANEL ----------
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setVisible(false);
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Helper for full width
        java.util.function.Consumer<JComponent> fullWidth = c -> {
            c.setAlignmentX(Component.LEFT_ALIGNMENT);
            c.setMaximumSize(new Dimension(Integer.MAX_VALUE, c.getPreferredSize().height));
        };

        // ---------- COMPONENTS ----------
        JCheckBox showGrid = new JCheckBox("Show Grid");
        showGrid.setOpaque(false);
        fullWidth.accept(showGrid);

        JLabel orientationLabel = new JLabel("Grid Orientation:");
        fullWidth.accept(orientationLabel);

        JCheckBox verticalGrid = new JCheckBox("Vertical");
        JCheckBox horizontalGrid = new JCheckBox("Horizontal");
        JCheckBox noneGrid = new JCheckBox("None");

        verticalGrid.setOpaque(false);
        horizontalGrid.setOpaque(false);
        noneGrid.setOpaque(false);

        fullWidth.accept(verticalGrid);
        fullWidth.accept(horizontalGrid);
        fullWidth.accept(noneGrid);

        JLabel sizeLabel = new JLabel("Grid Size:");
        fullWidth.accept(sizeLabel);

        JSpinner sizeSpinner = new JSpinner(new SpinnerNumberModel(20, 5, 100, 1));
        fullWidth.accept(sizeSpinner);

        JButton colorButton = new JButton("Choose Grid Color");
        fullWidth.accept(colorButton);

        JLabel opacityLabel = new JLabel("Grid Opacity: " + (int) settings.getGridOpacity() + "%");
        fullWidth.accept(opacityLabel);

        JSlider opacitySlider = new JSlider(0, 100, (int) settings.getGridOpacity());
        fullWidth.accept(opacitySlider);

        JButton applyButton = new JButton("Apply Grid to Canvas");
        fullWidth.accept(applyButton);

        // ---------- LOGIC ----------
        noneGrid.addActionListener(e -> {
            if (noneGrid.isSelected()) {
                verticalGrid.setSelected(false);
                horizontalGrid.setSelected(false);
            }
        });

        verticalGrid.addActionListener(e -> {
            if (verticalGrid.isSelected()) noneGrid.setSelected(false);
        });

        horizontalGrid.addActionListener(e -> {
            if (horizontalGrid.isSelected()) noneGrid.setSelected(false);
        });

        opacitySlider.addChangeListener(e ->
                opacityLabel.setText("Grid Opacity: " + opacitySlider.getValue() + "%")
        );

        colorButton.addActionListener(e -> {
            Color chosen = JColorChooser.showDialog(null, "Choose Grid Color", settings.getGridColor());
            if (chosen != null) {
                settings.updateGridSettings(
                        settings.isShowGrid(),
                        settings.isGridVertical(),
                        settings.isGridHorizontal(),
                        settings.getGridSize(),
                        chosen,
                        settings.getGridOpacity()
                );
            }
        });

        applyButton.addActionListener(e -> {
            settings.updateGridSettings(
                    showGrid.isSelected(),
                    verticalGrid.isSelected(),
                    horizontalGrid.isSelected(),
                    (int) sizeSpinner.getValue(),
                    settings.getGridColor(),
                    opacitySlider.getValue()
            );

            drawGridOnCanvas(canvas, settings);
        });

        // ---------- ADD COMPONENTS ----------
        content.add(showGrid);
        content.add(Box.createVerticalStrut(8));

        content.add(orientationLabel);
        content.add(verticalGrid);
        content.add(horizontalGrid);
        content.add(noneGrid);
        content.add(Box.createVerticalStrut(10));

        content.add(sizeLabel);
        content.add(sizeSpinner);
        content.add(Box.createVerticalStrut(10));

        content.add(colorButton);
        content.add(Box.createVerticalStrut(10));

        content.add(opacityLabel);
        content.add(opacitySlider);
        content.add(Box.createVerticalStrut(10));

        content.add(applyButton);

        outer.add(content, BorderLayout.CENTER);

        // ---------- EXPAND / COLLAPSE ----------
        headerBar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        headerBar.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                boolean visible = content.isVisible();
                content.setVisible(!visible);
                arrow.setText(visible ? "▼" : "▲");
                outer.revalidate();
            }
        });

        return outer;
    }
    private static void drawGridOnCanvas(JPanel canvas, WorksheetSettings settings) {

        canvas.removeAll();

        JPanel gridPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                if (!settings.isShowGrid()) return;

                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int size = settings.getGridSize();
                int width = getWidth();
                int height = getHeight();

                // Apply opacity
                Color base = settings.getGridColor();
                int alpha = (int) (settings.getGridOpacity() * 2.55); // % → 0–255
                Color c = new Color(base.getRed(), base.getGreen(), base.getBlue(), alpha);

                g2.setColor(c);

                // Vertical lines
                if (settings.isGridVertical()) {
                    for (int x = size; x < width; x += size) {
                        g2.drawLine(x, 0, x, height);
                    }
                }

                // Horizontal lines
                if (settings.isGridHorizontal()) {
                    for (int y = size; y < height; y += size) {
                        g2.drawLine(0, y, width, y);
                    }
                }
            }
        };

        gridPanel.setBackground(Color.WHITE);
        gridPanel.setLayout(new BorderLayout());

        canvas.setLayout(new BorderLayout());
        canvas.add(gridPanel, BorderLayout.CENTER);

        canvas.revalidate();
        canvas.repaint();
    }

    // ============================================================
// FONT SECTION (UI ONLY – FULL CONTROLS AND PREVIEW)
// ============================================================
    public static JPanel fontSection() {

        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(Color.WHITE);
        outer.setMaximumSize(new Dimension(250, 350));

        JButton header = new JButton("▼  Font");
        header.setFont(new Font("SansSerif", Font.BOLD, 14));
        header.setFocusPainted(false);
        header.setContentAreaFilled(false);
        header.setBorderPainted(false);
        header.setHorizontalAlignment(SwingConstants.LEFT);

        // ===== CONTENT PANEL =====
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Color.WHITE);
        content.setVisible(false);

        // ------------------------
        // FONT FAMILY DROPDOWN
        // ------------------------
        JLabel familyLabel = new JLabel("Font Family:");
        familyLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));

        String[] families = {"SansSerif", "Serif", "Monospaced", "Dialog", "Arial"};
        JComboBox<String> familyBox = new JComboBox<>(families);
        familyBox.setMaximumSize(new Dimension(200, 30));

        // ------------------------
        // FONT SIZE SPINNER
        // ------------------------
        JLabel sizeLabel = new JLabel("Font Size:");
        sizeLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));

        SpinnerNumberModel sizeModel =
                new SpinnerNumberModel(16, 8, 72, 1);
        JSpinner sizeSpinner = new JSpinner(sizeModel);
        sizeSpinner.setMaximumSize(new Dimension(200, 30));

        // ------------------------
        // STYLE TOGGLES
        // ------------------------
        JCheckBox bold = new JCheckBox("Bold");
        JCheckBox italic = new JCheckBox("Italic");
        JCheckBox underline = new JCheckBox("Underline");

        bold.setFont(new Font("SansSerif", Font.PLAIN, 12));
        italic.setFont(new Font("SansSerif", Font.PLAIN, 12));
        underline.setFont(new Font("SansSerif", Font.PLAIN, 12));

        bold.setBackground(Color.WHITE);
        italic.setBackground(Color.WHITE);
        underline.setBackground(Color.WHITE);

        // ------------------------
        // ALIGNMENT BUTTONS
        // ------------------------
        JLabel alignLabel = new JLabel("Alignment:");
        alignLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JButton alignLeft = new JButton("Left");
        JButton alignCenter = new JButton("Center");
        JButton alignRight = new JButton("Right");

        alignLeft.setFocusPainted(false);
        alignCenter.setFocusPainted(false);
        alignRight.setFocusPainted(false);

        alignLeft.setMaximumSize(new Dimension(200, 28));
        alignCenter.setMaximumSize(new Dimension(200, 28));
        alignRight.setMaximumSize(new Dimension(200, 28));

        // ------------------------
        // LIVE PREVIEW LABEL
        // ------------------------
        JLabel previewLabel = new JLabel("Preview Text");
        previewLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        previewLabel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        previewLabel.setOpaque(true);
        previewLabel.setBackground(new Color(245, 245, 245));
        previewLabel.setMaximumSize(new Dimension(200, 60));

        // ------------------------
        // LIVE UPDATE LOGIC
        // ------------------------
        Runnable updatePreview = () -> {
            int size = (int) sizeSpinner.getValue();
            String family = (String) familyBox.getSelectedItem();

            int style = Font.PLAIN;
            if (bold.isSelected()) style |= Font.BOLD;
            if (italic.isSelected()) style |= Font.ITALIC;

            Font f = new Font(family, style, size);
            previewLabel.setFont(f);
        };

        familyBox.addActionListener(e -> updatePreview.run());
        sizeSpinner.addChangeListener(e -> updatePreview.run());
        bold.addActionListener(e -> updatePreview.run());
        italic.addActionListener(e -> updatePreview.run());
        underline.addActionListener(e -> updatePreview.run());

        // ===== ADD COMPONENTS =====
        content.add(familyLabel);
        content.add(familyBox);
        content.add(Box.createVerticalStrut(10));

        content.add(sizeLabel);
        content.add(sizeSpinner);
        content.add(Box.createVerticalStrut(10));

        content.add(bold);
        content.add(italic);
        content.add(underline);
        content.add(Box.createVerticalStrut(10));

        content.add(alignLabel);
        content.add(alignLeft);
        content.add(alignCenter);
        content.add(alignRight);
        content.add(Box.createVerticalStrut(10));

        content.add(previewLabel);
        content.add(Box.createVerticalStrut(10));

        // ===== Expand/Collapse Toggle =====
        header.addActionListener(e -> {
            boolean visible = content.isVisible();
            content.setVisible(!visible);
            header.setText((visible ? "▼  " : "▲  ") + "Font");
            outer.revalidate();
            outer.repaint();
        });

        outer.add(header, BorderLayout.NORTH);
        outer.add(content, BorderLayout.CENTER);

        return outer;
    }

    // ============================================================
// IMPORT CONTENT SECTION (UI ONLY – Logo, Images, Text, B/W-Color)
// ============================================================
    public static JPanel importContentSection() {

        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(Color.WHITE);
        outer.setMaximumSize(new Dimension(250, 380));

        JButton header = new JButton("▼  Import Content");
        header.setFont(new Font("SansSerif", Font.BOLD, 14));
        header.setFocusPainted(false);
        header.setContentAreaFilled(false);
        header.setBorderPainted(false);
        header.setHorizontalAlignment(SwingConstants.LEFT);

        // ===== CONTENT PANEL =====
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Color.WHITE);
        content.setVisible(false);

        // ------------------------
        // ADD LOGO BUTTON
        // ------------------------
        JButton addLogo = new JButton("Add Logo");
        addLogo.setFocusPainted(false);
        addLogo.setMaximumSize(new Dimension(200, 30));

        // ------------------------
        // ADD MULTIPLE IMAGES BUTTON
        // ------------------------
        JButton addImages = new JButton("Add Images");
        addImages.setFocusPainted(false);
        addImages.setMaximumSize(new Dimension(200, 30));

        // ------------------------
        // IMPORT IMAGE (single)
        // ------------------------
        JButton importImage = new JButton("Upload Image");
        importImage.setFocusPainted(false);
        importImage.setMaximumSize(new Dimension(200, 30));

        // ------------------------
        // IMPORT TEXT FILE
        // ------------------------
        JButton importTextFile = new JButton("Upload Text File");
        importTextFile.setFocusPainted(false);
        importTextFile.setMaximumSize(new Dimension(200, 30));

        // ------------------------
        // B/W OR COLOR RADIO BUTTONS
        // ------------------------
        JLabel modeLabel = new JLabel("Colour Mode:");
        modeLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JRadioButton colorMode = new JRadioButton("Color");
        JRadioButton bwMode = new JRadioButton("Black & White");

        ButtonGroup modeGroup = new ButtonGroup();
        modeGroup.add(colorMode);
        modeGroup.add(bwMode);

        colorMode.setBackground(Color.WHITE);
        bwMode.setBackground(Color.WHITE);

        colorMode.setFont(new Font("SansSerif", Font.PLAIN, 12));
        bwMode.setFont(new Font("SansSerif", Font.PLAIN, 12));

        // Default = Color
        colorMode.setSelected(true);

        // ------------------------
        // PASTE TEXT AREA
        // ------------------------
        JLabel pasteLabel = new JLabel("Paste Text:");
        pasteLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JTextArea pasteArea = new JTextArea(4, 20);
        pasteArea.setLineWrap(true);
        pasteArea.setWrapStyleWord(true);
        JScrollPane pasteScroll = new JScrollPane(pasteArea);
        pasteScroll.setMaximumSize(new Dimension(220, 70));

        // ------------------------
        // PREVIEW AREA
        // ------------------------
        JLabel previewLabel = new JLabel("No content imported");
        previewLabel.setOpaque(true);
        previewLabel.setBackground(new Color(245, 245, 245));
        previewLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        previewLabel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        previewLabel.setMaximumSize(new Dimension(220, 60));

        // ------------------------
        // REMOVE CONTENT BUTTON
        // ------------------------
        JButton removeButton = new JButton("Remove Content");
        removeButton.setFocusPainted(false);
        removeButton.setMaximumSize(new Dimension(200, 30));

        // ===== ACTION LOGIC =====

        addLogo.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            int result = chooser.showOpenDialog(null);

            if (result == JFileChooser.APPROVE_OPTION) {
                previewLabel.setText("Logo Added: " + chooser.getSelectedFile().getName());
            }
        });

        addImages.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setMultiSelectionEnabled(true);
            int result = chooser.showOpenDialog(null);

            if (result == JFileChooser.APPROVE_OPTION) {
                File[] files = chooser.getSelectedFiles();
                previewLabel.setText("Images Added: " + files.length);
            }
        });

        importImage.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            int result = chooser.showOpenDialog(null);

            if (result == JFileChooser.APPROVE_OPTION) {
                previewLabel.setText("Imported Image: " + chooser.getSelectedFile().getName());
            }
        });

        importTextFile.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            int result = chooser.showOpenDialog(null);

            if (result == JFileChooser.APPROVE_OPTION) {
                previewLabel.setText("Imported Text File: " + chooser.getSelectedFile().getName());
            }
        });

        pasteArea.getDocument().addDocumentListener(simpleListener(() -> {
            if (!pasteArea.getText().trim().isEmpty()) {
                previewLabel.setText("<html><b>Pasted Text:</b><br>" +
                        pasteArea.getText().replace("\n", "<br>") + "</html>");
            }
        }));

        removeButton.addActionListener(e -> {
            pasteArea.setText("");
            previewLabel.setText("No content imported");
        });

        // ===== ADD COMPONENTS =====
        content.add(addLogo);
        content.add(Box.createVerticalStrut(8));

        content.add(addImages);
        content.add(Box.createVerticalStrut(8));

        content.add(importImage);
        content.add(Box.createVerticalStrut(8));

        content.add(importTextFile);
        content.add(Box.createVerticalStrut(12));

        content.add(modeLabel);
        content.add(colorMode);
        content.add(bwMode);
        content.add(Box.createVerticalStrut(10));

        content.add(pasteLabel);
        content.add(pasteScroll);
        content.add(Box.createVerticalStrut(8));

        content.add(previewLabel);
        content.add(Box.createVerticalStrut(10));

        content.add(removeButton);
        content.add(Box.createVerticalStrut(10));

        // ===== EXPAND/COLLAPSE =====
        header.addActionListener(e -> {
            boolean visible = content.isVisible();
            content.setVisible(!visible);
            header.setText((visible ? "▼  " : "▲  ") + "Import Content");
            outer.revalidate();
            outer.repaint();
        });

        outer.add(header, BorderLayout.NORTH);
        outer.add(content, BorderLayout.CENTER);

        return outer;
    }

    // ============================================================
// COLOUR PALETTE SECTION (UI ONLY – Pickers, Swatches, Reset)
// ============================================================
    public static JPanel colorPaletteSection() {

        // ==== OUTER CARD WITH SHADOW ====
        RoundedPanel outer = new RoundedPanel(25) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(0, 0, 0, 55));
                g2.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 25, 25);

                g2.setColor(new Color(255, 255, 255, 230));
                g2.fillRoundRect(0, 0, getWidth() - 6, getHeight() - 6, 25, 25);

                super.paintComponent(g2);
            }
        };

        outer.setOpaque(false);
        outer.setLayout(new BorderLayout());
        outer.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        outer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 450));

        // ==== HEADER GRADIENT BAR ====
        JPanel headerBar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(120, 140, 170),
                        getWidth(), getHeight(), new Color(90, 110, 140)
                );

                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                super.paintComponent(g);
            }
        };
        headerBar.setOpaque(false);
        headerBar.setLayout(new BorderLayout());
        headerBar.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        JLabel title = new JLabel("Colour Palette");
        title.setFont(new Font("SansSerif", Font.BOLD, 15));
        title.setForeground(Color.WHITE);

        JLabel arrow = new JLabel("▼");
        arrow.setFont(new Font("SansSerif", Font.BOLD, 16));
        arrow.setForeground(Color.WHITE);

        headerBar.add(title, BorderLayout.WEST);
        headerBar.add(arrow, BorderLayout.EAST);

        outer.add(headerBar, BorderLayout.NORTH);

        // ==== CONTENT PANEL ====
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        content.setVisible(false);

        // Helper for alignment
        java.util.function.Consumer<JComponent> fullWidth = c -> {
            c.setAlignmentX(Component.LEFT_ALIGNMENT);
            c.setMaximumSize(new Dimension(Integer.MAX_VALUE, c.getPreferredSize().height));
        };

        // ===========================================================
        // TEXT COLOUR BUTTON (FIXED)
        // ===========================================================
        JButton textColorButton = new JButton("Choose Text Colour") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(150, 165, 190),
                        getWidth(), getHeight(), new Color(110, 125, 155)
                );
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);

                super.paintComponent(g); // <-- CRITICAL FIX
            }
        };
        textColorButton.setOpaque(false);
        textColorButton.setContentAreaFilled(false);
        textColorButton.setBorderPainted(false);
        textColorButton.setFocusPainted(false);
        textColorButton.setForeground(Color.WHITE);
        textColorButton.setFont(new Font("SansSerif", Font.BOLD, 13));
        textColorButton.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        fullWidth.accept(textColorButton);

        JLabel textColorPreview = new JLabel("Text Colour Preview");
        textColorPreview.setOpaque(true);
        textColorPreview.setBackground(Color.BLACK);
        textColorPreview.setForeground(Color.WHITE);
        textColorPreview.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
        fullWidth.accept(textColorPreview);

        textColorButton.addActionListener(e -> {
            Color chosen = JColorChooser.showDialog(null, "Choose Text Colour", textColorPreview.getBackground());
            if (chosen != null) {
                textColorPreview.setBackground(chosen);
                textColorPreview.setForeground(chosen.equals(Color.BLACK) ? Color.WHITE : Color.BLACK);
            }
        });

        // ===========================================================
        // BACKGROUND COLOUR BUTTON (FIXED)
        // ===========================================================
        JButton backgroundColorButton = new JButton("Choose Background Colour") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(150, 165, 190),
                        getWidth(), getHeight(), new Color(110, 125, 155)
                );
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);

                super.paintComponent(g);
            }
        };
        backgroundColorButton.setOpaque(false);
        backgroundColorButton.setContentAreaFilled(false);
        backgroundColorButton.setBorderPainted(false);
        backgroundColorButton.setFocusPainted(false);
        backgroundColorButton.setForeground(Color.WHITE);
        backgroundColorButton.setFont(new Font("SansSerif", Font.BOLD, 13));
        backgroundColorButton.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        fullWidth.accept(backgroundColorButton);

        JLabel bgColorPreview = new JLabel("Background Preview");
        bgColorPreview.setOpaque(true);
        bgColorPreview.setBackground(Color.WHITE);
        bgColorPreview.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
        fullWidth.accept(bgColorPreview);

        backgroundColorButton.addActionListener(e -> {
            Color chosen = JColorChooser.showDialog(null, "Choose Background Colour", bgColorPreview.getBackground());
            if (chosen != null) {
                bgColorPreview.setBackground(chosen);
            }
        });

        // ===========================================================
        // PRESET SWATCHES
        // ===========================================================
        JLabel swatchLabel = new JLabel("Preset Colours:");
        swatchLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        fullWidth.accept(swatchLabel);

        JPanel swatchPanel = new JPanel(new GridLayout(2, 6, 6, 6));
        swatchPanel.setOpaque(false);
        fullWidth.accept(swatchPanel);

        Color[] swatches = {
                Color.BLACK, Color.WHITE,
                new Color(255, 0, 0), new Color(0, 128, 0),
                new Color(0, 0, 255), new Color(255, 165, 0),
                new Color(128, 0, 128), new Color(0, 139, 139),
                new Color(255, 20, 147), new Color(139, 69, 19),
                new Color(75, 0, 130), new Color(173, 255, 47)
        };

        for (Color c : swatches) {
            JLabel sw = new JLabel();
            sw.setOpaque(true);
            sw.setBackground(c);
            sw.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
            sw.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            sw.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    textColorPreview.setBackground(c);
                    textColorPreview.setForeground(c.equals(Color.BLACK) ? Color.WHITE : Color.BLACK);
                }
            });

            swatchPanel.add(sw);
        }

        // ===========================================================
        // APPLY BUTTON
        // ===========================================================
        JButton applyButton = new JButton("Apply Colours") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(110, 125, 155),
                        getWidth(), getHeight(), new Color(80, 90, 120)
                );
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);

                super.paintComponent(g);
            }
        };

        applyButton.setOpaque(false);
        applyButton.setContentAreaFilled(false);
        applyButton.setBorderPainted(false);
        applyButton.setFocusPainted(false);
        applyButton.setForeground(Color.WHITE);
        applyButton.setFont(new Font("SansSerif", Font.BOLD, 13));
        applyButton.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        fullWidth.accept(applyButton);

        applyButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(null, "Colours applied successfully!");
        });

        // ===========================================================
        // RESET BUTTON
        // ===========================================================
        JButton resetButton = new JButton("Reset Colours") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(150, 165, 190),
                        getWidth(), getHeight(), new Color(110, 125, 155)
                );
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);

                super.paintComponent(g);
            }
        };

        resetButton.setOpaque(false);
        resetButton.setContentAreaFilled(false);
        resetButton.setBorderPainted(false);
        resetButton.setFocusPainted(false);
        resetButton.setForeground(Color.WHITE);
        resetButton.setFont(new Font("SansSerif", Font.BOLD, 13));
        resetButton.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        fullWidth.accept(resetButton);

        resetButton.addActionListener(e -> {
            textColorPreview.setBackground(Color.BLACK);
            textColorPreview.setForeground(Color.WHITE);
            bgColorPreview.setBackground(Color.WHITE);
        });

        // ===========================================================
        // ADD EVERYTHING
        // ===========================================================
        content.add(textColorButton);
        content.add(Box.createVerticalStrut(6));
        content.add(textColorPreview);
        content.add(Box.createVerticalStrut(15));

        content.add(backgroundColorButton);
        content.add(Box.createVerticalStrut(6));
        content.add(bgColorPreview);
        content.add(Box.createVerticalStrut(18));

        content.add(swatchLabel);
        content.add(Box.createVerticalStrut(5));
        content.add(swatchPanel);
        content.add(Box.createVerticalStrut(15));

        content.add(applyButton);
        content.add(Box.createVerticalStrut(10));
        content.add(resetButton);

        outer.add(content, BorderLayout.CENTER);

        // ===========================================================
        // EXPAND / COLLAPSE
        // ===========================================================
        headerBar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        headerBar.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                boolean visible = content.isVisible();
                content.setVisible(!visible);
                arrow.setText(visible ? "▼" : "▲");
                outer.revalidate();
            }
        });

        return outer;
    }


    // ============================================================
// CALCULATIONS SECTION (UI ONLY – ranges, ops, problem count)
// ============================================================
    public static JPanel calculationsSection() {

        // CARD WITH SHADOW
        RoundedPanel outer = new RoundedPanel(25) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Soft shadow
                g2.setColor(new Color(0, 0, 0, 55));
                g2.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 25, 25);

                // Card background
                g2.setColor(new Color(255, 255, 255, 230));
                g2.fillRoundRect(0, 0, getWidth() - 6, getHeight() - 6, 25, 25);

                super.paintComponent(g2);
            }
        };

        outer.setOpaque(false);
        outer.setLayout(new BorderLayout());
        outer.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        outer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 420));

        // HEADER BAR
        JPanel headerBar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(120, 140, 170),
                        getWidth(), getHeight(), new Color(90, 110, 140)
                );

                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);

                super.paintComponent(g2);
            }
        };

        headerBar.setOpaque(false);
        headerBar.setLayout(new BorderLayout());
        headerBar.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        JLabel title = new JLabel("Calculations");
        title.setFont(new Font("SansSerif", Font.BOLD, 15));
        title.setForeground(Color.WHITE);

        JLabel arrow = new JLabel("▼");
        arrow.setFont(new Font("SansSerif", Font.BOLD, 16));
        arrow.setForeground(Color.WHITE);

        headerBar.add(title, BorderLayout.WEST);
        headerBar.add(arrow, BorderLayout.EAST);

        outer.add(headerBar, BorderLayout.NORTH);

        // CONTENT PANEL
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        content.setVisible(false);

        // Helper to align full width
        java.util.function.Consumer<JComponent> fullWidth = c -> {
            c.setAlignmentX(Component.LEFT_ALIGNMENT);
            c.setMaximumSize(new Dimension(Integer.MAX_VALUE, c.getPreferredSize().height));
        };

        // -------------------
        // NUMBER RANGE
        // -------------------
        JLabel rangeLabel = new JLabel("Number Range:");
        rangeLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        fullWidth.accept(rangeLabel);

        JCheckBox range20 = new JCheckBox("1 - 20");
        JCheckBox range50 = new JCheckBox("1 - 50");
        JCheckBox range100 = new JCheckBox("1 - 100");

        JCheckBox[] ranges = {range20, range50, range100};
        for (JCheckBox box : ranges) {
            box.setOpaque(false);
            box.setFont(new Font("SansSerif", Font.PLAIN, 12));
            fullWidth.accept(box);
        }

        // -------------------
        // PROBLEM COUNT
        // -------------------
        JLabel problemLabel = new JLabel("How many problems?");
        problemLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        fullWidth.accept(problemLabel);

        SpinnerNumberModel problemModel = new SpinnerNumberModel(10, 1, 50, 1);
        JSpinner problemSpinner = new JSpinner(problemModel);
        fullWidth.accept(problemSpinner);

        // -------------------
        // OPERATIONS
        // -------------------
        JLabel opsLabel = new JLabel("Operations:");
        opsLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        fullWidth.accept(opsLabel);

        JCheckBox addOp = new JCheckBox("Addition (+)");
        JCheckBox subOp = new JCheckBox("Subtraction (−)");
        JCheckBox mulOp = new JCheckBox("Multiplication (×)");
        JCheckBox divOp = new JCheckBox("Division (÷)");

        JCheckBox[] ops = {addOp, subOp, mulOp, divOp};
        for (JCheckBox box : ops) {
            box.setOpaque(false);
            box.setFont(new Font("SansSerif", Font.PLAIN, 12));
            fullWidth.accept(box);
        }

        // -------------------
        // GENERATE BUTTON
        // -------------------
        JButton generateButton = new JButton("Generate Problems") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(150, 165, 190),
                        getWidth(), getHeight(), new Color(110, 125, 155)
                );

                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                super.paintComponent(g);
            }
        };

        generateButton.setOpaque(false);
        generateButton.setContentAreaFilled(false);
        generateButton.setFocusPainted(false);
        generateButton.setForeground(Color.WHITE);
        generateButton.setFont(new Font("SansSerif", Font.BOLD, 13));
        generateButton.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        fullWidth.accept(generateButton);

        generateButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(null,
                    "Math problem generation feature coming soon!",
                    "Coming Soon",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        // -------------------
        // ADD COMPONENTS
        // -------------------
        content.add(rangeLabel);
        content.add(range20);
        content.add(range50);
        content.add(range100);
        content.add(Box.createVerticalStrut(10));

        content.add(problemLabel);
        content.add(problemSpinner);
        content.add(Box.createVerticalStrut(10));

        content.add(opsLabel);
        content.add(addOp);
        content.add(subOp);
        content.add(mulOp);
        content.add(divOp);
        content.add(Box.createVerticalStrut(15));

        content.add(generateButton);
        content.add(Box.createVerticalStrut(10));

        outer.add(content, BorderLayout.CENTER);

        // EXPAND / COLLAPSE
        headerBar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        headerBar.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                boolean visible = content.isVisible();
                content.setVisible(!visible);
                arrow.setText(visible ? "▼" : "▲");
                outer.revalidate();
            }
        });

        return outer;
    }

    // ============================================================
// QUICK FILL SECTION (UI ONLY – BUTTON GRID LIKE SAMPLE)
// ============================================================
    public static JPanel quickFillSection() {

        // OUTER CARD
        RoundedPanel outer = new RoundedPanel(25) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Soft shadow
                g2.setColor(new Color(0, 0, 0, 55));
                g2.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 25, 25);

                // Card background
                g2.setColor(new Color(255, 255, 255, 230));
                g2.fillRoundRect(0, 0, getWidth() - 6, getHeight() - 6, 25, 25);

                super.paintComponent(g2);
            }
        };

        outer.setOpaque(false);
        outer.setLayout(new BorderLayout());
        outer.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        outer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 500));

        // HEADER BAR
        JPanel headerBar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(120, 140, 170),   // top
                        getWidth(), getHeight(), new Color(90, 110, 140)  // bottom
                );

                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);

                super.paintComponent(g);
            }
        };
        headerBar.setOpaque(false);
        headerBar.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        headerBar.setLayout(new BorderLayout());

        JLabel headerLabel = new JLabel("Quick Fill");
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        headerLabel.setForeground(Color.WHITE);

        JLabel arrow = new JLabel("▼");
        arrow.setFont(new Font("SansSerif", Font.BOLD, 16));
        arrow.setForeground(Color.WHITE);

        headerBar.add(headerLabel, BorderLayout.WEST);
        headerBar.add(arrow, BorderLayout.EAST);

        outer.add(headerBar, BorderLayout.NORTH);

        // CONTENT PANEL
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setVisible(false);
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // GRID (2 COLUMNS)
        JPanel grid = new JPanel(new GridLayout(0, 2, 12, 12));
        grid.setOpaque(false);

        // Button gradient colors — OPTION A
        Color grad1 = new Color(150, 165, 190);
        Color grad2 = new Color(110, 125, 155);

        // FACTORY: Creates your new UI-matching buttons
        java.util.function.Function<String, JButton> makeBtn = (text) -> {

            JButton btn = new JButton(text) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    GradientPaint gp = new GradientPaint(
                            0, 0, grad1,
                            getWidth(), getHeight(), grad2
                    );

                    g2.setPaint(gp);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                    super.paintComponent(g);
                }
            };

            btn.setOpaque(false);
            btn.setContentAreaFilled(false);
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("SansSerif", Font.BOLD, 13));

            // Hover + Press effect
            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    btn.setForeground(new Color(230, 230, 255));
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    btn.setForeground(Color.WHITE);
                }

                @Override
                public void mousePressed(java.awt.event.MouseEvent e) {
                    btn.setForeground(Color.LIGHT_GRAY);
                }
            });

            return btn;
        };

        // ADD BUTTONS
        grid.add(makeBtn.apply("A-Z"));
        grid.add(makeBtn.apply("a-z"));

        grid.add(makeBtn.apply("1-20"));
        grid.add(makeBtn.apply("Sight Words"));

        grid.add(makeBtn.apply("Colors"));
        grid.add(makeBtn.apply("Animals"));

        grid.add(makeBtn.apply("CVC Words"));
        grid.add(makeBtn.apply("Shapes"));

        grid.add(makeBtn.apply("Addition"));
        grid.add(makeBtn.apply("Subtraction"));

        grid.add(makeBtn.apply("Count 1-10"));
        grid.add(new JLabel()); // alignment filler

        content.add(grid);
        content.add(Box.createVerticalStrut(10));

        outer.add(content, BorderLayout.CENTER);

        // EXPAND / COLLAPSE
        headerBar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        headerBar.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                boolean visible = content.isVisible();
                content.setVisible(!visible);
                arrow.setText(visible ? "▼" : "▲");
                outer.revalidate();
            }
        });

        return outer;
    }

    // ============================================================
// TEMPLATE LAYOUT SECTION (UI ONLY – 2-COLUMN TEMPLATE BUTTONS)
// ============================================================
    public static JPanel templateSection() {

        // OUTER CARD
        RoundedPanel outer = new RoundedPanel(25) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Shadow
                g2.setColor(new Color(0, 0, 0, 55));
                g2.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 25, 25);

                // Card background
                g2.setColor(new Color(255, 255, 255, 230));
                g2.fillRoundRect(0, 0, getWidth() - 6, getHeight() - 6, 25, 25);

                super.paintComponent(g2);
            }
        };

        outer.setOpaque(false);
        outer.setLayout(new BorderLayout());
        outer.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        outer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 460));

        // HEADER BAR ------------------------------------
        JPanel headerBar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(120, 140, 170),
                        getWidth(), getHeight(), new Color(90, 110, 140)
                );

                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);

                super.paintComponent(g2);
            }
        };
        headerBar.setOpaque(false);
        headerBar.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        headerBar.setLayout(new BorderLayout());

        JLabel headerLabel = new JLabel("Template Layouts");
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        headerLabel.setForeground(Color.WHITE);

        JLabel arrow = new JLabel("▼");
        arrow.setFont(new Font("SansSerif", Font.BOLD, 16));
        arrow.setForeground(Color.WHITE);

        headerBar.add(headerLabel, BorderLayout.WEST);
        headerBar.add(arrow, BorderLayout.EAST);

        outer.add(headerBar, BorderLayout.NORTH);

        // CONTENT ----------------------------------------
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        content.setVisible(false);

        // GRID
        JPanel grid = new JPanel(new GridLayout(0, 2, 12, 12));
        grid.setOpaque(false);

        // Gradient colors (same as Quick Fill)
        Color grad1 = new Color(150, 165, 190);
        Color grad2 = new Color(110, 125, 155);

        // BUTTON BUILDER (same as Quick Fill)
        java.util.function.Function<String, JButton> makeBtn = (text) -> {

            JButton btn = new JButton(text) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    GradientPaint gp = new GradientPaint(0, 0, grad1,
                            getWidth(), getHeight(), grad2);

                    g2.setPaint(gp);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                    super.paintComponent(g2);
                }
            };

            btn.setOpaque(false);
            btn.setContentAreaFilled(false);
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("SansSerif", Font.BOLD, 13));

            // Hover + Press effects
            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    btn.setForeground(new Color(230, 230, 255));
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    btn.setForeground(Color.WHITE);
                }

                @Override
                public void mousePressed(java.awt.event.MouseEvent e) {
                    btn.setForeground(Color.LIGHT_GRAY);
                }
            });

            return btn;
        };

        // ADD TEMPLATE BUTTONS
        grid.add(makeBtn.apply("Basic"));
        grid.add(makeBtn.apply("Lined"));

        grid.add(makeBtn.apply("Graph"));
        grid.add(makeBtn.apply("Handwriting"));

        grid.add(makeBtn.apply("Math Grid"));
        grid.add(makeBtn.apply("Table"));

        grid.add(makeBtn.apply("Flashcards"));
        grid.add(makeBtn.apply("Blank"));

        // Add grid to content
        content.add(grid);
        content.add(Box.createVerticalStrut(10));

        outer.add(content, BorderLayout.CENTER);

        // EXPAND / COLLAPSE
        headerBar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        headerBar.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                boolean visible = content.isVisible();
                content.setVisible(!visible);
                arrow.setText(visible ? "▼" : "▲");
                outer.revalidate();
            }
        });

        return outer;
    }

//    // ============================================================
//// GENERIC COLLAPSIBLE SECTION (NOW WORKING LIKE STUDENT DETAILS)
//// ============================================================
//    public static JPanel section(String title) {
//
//        JPanel outer = new JPanel(new BorderLayout());
//        outer.setBackground(Color.WHITE);
//        outer.setMaximumSize(new Dimension(250, 200));
//
//        JButton header = new JButton("▼  " + title);
//        header.setFont(new Font("SansSerif", Font.BOLD, 14));
//        header.setFocusPainted(false);
//        header.setContentAreaFilled(false);
//        header.setBorderPainted(false);
//        header.setHorizontalAlignment(SwingConstants.LEFT);
//
//        // CONTENT PANEL (collapsed by default)
//        JPanel content = new JPanel();
//        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
//        content.setBackground(Color.WHITE);
//        content.setVisible(false);
//
//        // Placeholder content so expansion works visually.
//        // You can replace this later with actual controls.
//        JLabel placeholder = new JLabel("Content for " + title);
//        placeholder.setFont(new Font("SansSerif", Font.PLAIN, 12));
//        placeholder.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 5));
//
//        content.add(placeholder);
//        content.add(Box.createVerticalStrut(10));
//
//        // Toggle behaviour
//        header.addActionListener(e -> {
//            boolean visible = content.isVisible();
//            content.setVisible(!visible);
//            header.setText((visible ? "▼  " : "▲  ") + title);
//            outer.revalidate();
//            outer.repaint();
//        });
//
//        outer.add(header, BorderLayout.NORTH);
//        outer.add(content, BorderLayout.CENTER);
//
//        return outer;
//    }
//    // ===========================================================
//    // Zoom button
//    //============================================================
//    public static JLabel createZoomButton(int x, JPanel canvas) {
//
//        JLabel zoomIcon = toolbarIcon("ZOOM.png", x); // or fallback text
//
//        zoomIcon.addMouseListener(new java.awt.event.MouseAdapter() {
//            @Override
//            public void mouseReleased(java.awt.event.MouseEvent e) {
//                JPopupMenu zoomMenu = new JPopupMenu();
//
//                JMenuItem zoomIn = new JMenuItem("Zoom In");
//                JMenuItem zoomOut = new JMenuItem("Zoom Out");
//
//                zoomIn.addActionListener(ev -> {
//                    scaleCanvas(canvas, 1.1f); // increase 10%
//                });
//
//                zoomOut.addActionListener(ev -> {
//                    scaleCanvas(canvas, 0.9f); // decrease 10%
//                });
//
//                zoomMenu.add(zoomIn);
//                zoomMenu.add(zoomOut);
//
//                zoomMenu.show(zoomIcon, 10, 30);
//            }
//        });
//
//        return zoomIcon;
//    }
//    private static void scaleCanvas(JPanel canvas, float scale) {
//        Dimension size = canvas.getSize();
//
//        int newW = Math.round(size.width * scale);
//        int newH = Math.round(size.height * scale);
//
//        canvas.setPreferredSize(new Dimension(newW, newH));
//        canvas.revalidate();
//        canvas.repaint();
//    }
//
//


    // ============================================================
    // SUPPORT UTILITIES
    // ============================================================
//    public static JLabel createToolbarButton(String filename, int x, Runnable action) {
//
//        ImageIcon imageIcon = ResourceLoader.loadIcon(filename);
//        JLabel icon;
//
//        if (imageIcon != null) {
//            icon = new JLabel(imageIcon);
//        } else {
//            icon = new JLabel(filename.replace(".png", ""));
//            icon.setForeground(Color.RED);
//            icon.setFont(new Font("SansSerif", Font.BOLD, 10));
//        }
//
//        icon.setBounds(x, 12, 32, 32);
//        icon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
//
//        Color normal = new Color(255, 255, 255, 0);
//        Color hover = new Color(255, 255, 255, 80);
//        Color pressed = new Color(255, 255, 255, 140);
//
//        icon.addMouseListener(new java.awt.event.MouseAdapter() {
//            @Override public void mouseEntered(java.awt.event.MouseEvent e) { icon.setBackground(hover); }
//            @Override public void mouseExited(java.awt.event.MouseEvent e) { icon.setBackground(normal); }
//            @Override public void mousePressed(java.awt.event.MouseEvent e) { icon.setBackground(pressed); }
//            @Override public void mouseReleased(java.awt.event.MouseEvent e) {
//                icon.setBackground(hover);
//                action.run();  // <--- ACTION TRIGGER
//            }
//        });
//
//        icon.setOpaque(true);
//        return icon;
//    }
//

    public static DocumentListener simpleListener(Runnable run) {
        return new DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                run.run();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                run.run();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                run.run();
            }
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
     *
     * @param filename The name of the icon file (e.g., "UNDO.png").
     * @param x        The x-coordinate for setting bounds.
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
    //chatbot method
    public static JPanel createChatPopup() {

        RoundedPanel popup = new RoundedPanel(30);
        popup.setLayout(null);
        popup.setBackground(Color.WHITE);
        popup.setBounds(850, 330, 320, 480);
        popup.setVisible(false);

        // HEADER WITH BLUE GRADIENT
        RoundedPanel header = new RoundedPanel(30) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(80, 140, 255),
                        0, getHeight(), new Color(40, 100, 220)
                );

                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                super.paintComponent(g);
            }
        };

        header.setBounds(0, 0, 320, 70);
        header.setLayout(null);
// CLOSE / RETURN BUTTON
        JButton closeBtn = new JButton("⟵");
        closeBtn.setFont(new Font("SansSerif", Font.BOLD, 20));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFocusPainted(false);
        closeBtn.setContentAreaFilled(false);
        closeBtn.setBorderPainted(false);
        closeBtn.setBounds(270, 15, 40, 40);

// Will hide the popup when clicked
        closeBtn.addActionListener(e -> {
            Component parent = header.getParent();
            if (parent != null) parent.setVisible(false);
        });

        header.add(closeBtn);

        JLabel title = new JLabel("Chat with EduCreate");
        title.setFont(new Font("SansSerif", Font.BOLD, 14));
        title.setForeground(Color.WHITE);
        title.setBounds(20, 20, 260, 30);
        header.add(title);

        popup.add(header);

        // CHAT DISPLAY AREA
        JPanel chatArea = new JPanel();
        chatArea.setLayout(new BoxLayout(chatArea, BoxLayout.Y_AXIS));
        chatArea.setBackground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(chatArea);
        scroll.setBounds(10, 80, 300, 300);
        scroll.setBorder(null);
        popup.add(scroll);

        // INPUT FIELD
        JTextField input = new JTextField();
        input.setBounds(15, 400, 230, 40);
        popup.add(input);

        JButton sendBtn = new JButton("➤");
        sendBtn.setFont(new Font("SansSerif", Font.BOLD, 18));
        sendBtn.setBounds(255, 400, 50, 40);
        sendBtn.setFocusPainted(false);
        popup.add(sendBtn);

        // SEND MESSAGE FUNCTIONALITY
        sendBtn.addActionListener(e -> {
            String text = input.getText().trim();
            if (text.isEmpty()) return;

            chatArea.add(createBubble(text, true));
            chatArea.revalidate();

            input.setText("");

            // BOT RESPONSE
            Timer t = new Timer(500, ev -> {
                chatArea.add(createBubble("I’m here to help! 😊", false));
                chatArea.revalidate();
            });
            t.setRepeats(false);
            t.start();
        });

        return popup;
    }
    public static JPanel createBubble(String text, boolean isUser) {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        RoundedPanel bubble = new RoundedPanel(18);
        bubble.setLayout(new BorderLayout());

        JLabel msg = new JLabel("<html>" + text + "</html>");
        msg.setFont(new Font("SansSerif", Font.PLAIN, 13));

        if (isUser) {
            bubble.setBackground(new Color(80, 140, 255));
            msg.setForeground(Color.WHITE);
            outer.add(bubble, BorderLayout.EAST);
        } else {
            bubble.setBackground(new Color(240, 240, 240));
            msg.setForeground(Color.BLACK);
            outer.add(bubble, BorderLayout.WEST);
        }

        bubble.add(msg, BorderLayout.CENTER);
        outer.setOpaque(false);
        return outer;
    }

}
//payge