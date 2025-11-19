package com.espaneg.ui;

import com.espaneg.model.WorksheetSettings;
import com.espaneg.utils.ResourceLoader;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.font.TextAttribute;

public class WorksheetGenerator {
    // Page state
    static boolean fitToWidth = false;
    static boolean showMargins = true;

    static int marginTop = 50;
    static int marginBottom = 50;
    static int marginLeft = 50;
    static int marginRight = 50;

    // Orientation: true = portrait, false = landscape
    static boolean portrait = true;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(WorksheetGenerator::createAndShowUI);
    }

    private static void createAndShowUI() {
        JFrame frame = new JFrame();
        frame.setTitle("EduCreate – Worksheet Generator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setResizable(true);

        // --- ICON FIX: Logo loading ---
        ImageIcon logo = ResourceLoader.loadIcon("LOGO2.png");
        if (logo != null) {
            frame.setIconImage(logo.getImage());
        }

        // Root gradient background using BorderLayout
        GradientPanel background = new GradientPanel();
        background.setLayout(new BorderLayout(12, 12));
        frame.setContentPane(background);

        // ---------------------------
        // LEFT SIDEBAR (dock WEST)
        // ---------------------------
        RoundedPanel leftPanel = new RoundedPanel(0);
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setLayout(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        leftPanel.setPreferredSize(new Dimension(270, 0)); // preferred width; height flexible
        background.add(leftPanel, BorderLayout.WEST);

        // TOP BAR inside sidebar
        JPanel topBar = new JPanel(new BorderLayout(8, 0));
        topBar.setOpaque(false);
        topBar.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        leftPanel.add(topBar, BorderLayout.NORTH);

        // Home Icon
        JLabel homeBtn = new JLabel(ResourceLoader.loadIcon("HOME.png"));
        homeBtn.setHorizontalAlignment(SwingConstants.CENTER);
        homeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        homeBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                frame.dispose();
                UserLogin.main(null);
            }
        });
        topBar.add(homeBtn, BorderLayout.WEST);

        JLabel centeredHeader = new JLabel("Customization Panel", SwingConstants.CENTER);
        centeredHeader.setFont(new Font("SansSerif", Font.BOLD, 17));
        centeredHeader.setForeground(new Color(40, 50, 70));
        topBar.add(centeredHeader, BorderLayout.CENTER);

        JLabel collapseBtn = new JLabel(ResourceLoader.loadIcon("EXIT.png"));
        collapseBtn.setHorizontalAlignment(SwingConstants.CENTER);
        collapseBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        topBar.add(collapseBtn, BorderLayout.EAST);

        // Left content (scrollable)
        JPanel leftContent = new JPanel();
        leftContent.setLayout(new BoxLayout(leftContent, BoxLayout.Y_AXIS));
        leftContent.setOpaque(false);

        JScrollPane leftScroll = new JScrollPane(leftContent);
        leftScroll.setBorder(null);
        leftScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        leftScroll.getVerticalScrollBar().setUnitIncrement(12);
        leftPanel.add(leftScroll, BorderLayout.CENTER);

        // Collapse handling
        final boolean[] collapsed = {false};
        collapseBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (!collapsed[0]) {
                    leftPanel.setPreferredSize(new Dimension(70, 0));
                    centeredHeader.setVisible(false);
                    leftScroll.setVisible(false);
                    homeBtn.setVisible(false);

                    topBar.removeAll();
                    topBar.add(collapseBtn, BorderLayout.CENTER);
                } else {
                    leftPanel.setPreferredSize(new Dimension(270, 0));
                    centeredHeader.setVisible(true);
                    leftScroll.setVisible(true);
                    homeBtn.setVisible(true);

                    topBar.removeAll();
                    topBar.add(homeBtn, BorderLayout.WEST);
                    topBar.add(centeredHeader, BorderLayout.CENTER);
                    topBar.add(collapseBtn, BorderLayout.EAST);
                }

                collapsed[0] = !collapsed[0];
                leftPanel.revalidate();
                leftPanel.repaint();
            }
        });

        // ============================================================
        // TOP BAR (global - search + export + more)
        // ============================================================
        JPanel globalTopBar = new JPanel(new BorderLayout(8, 8));
        globalTopBar.setOpaque(false);
        globalTopBar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        background.add(globalTopBar, BorderLayout.NORTH);

        // Search bar (left side of top)
        RoundedPanel searchBar = new RoundedPanel(40);
        searchBar.setBackground(new Color(255, 255, 255, 150));
        searchBar.setLayout(new BorderLayout(8, 8));
        searchBar.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        searchBar.setPreferredSize(new Dimension(600, 48));

        JTextField searchField = new JTextField("Search...");
        searchField.setBorder(null);
        searchField.setOpaque(false);
        searchField.setForeground(new Color(60, 60, 60));
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 18));
        searchBar.add(searchField, BorderLayout.CENTER);

        JLabel searchIcon = new JLabel(ResourceLoader.loadIcon("SEARCH.png"));
        searchBar.add(searchIcon, BorderLayout.EAST);

        globalTopBar.add(searchBar, BorderLayout.WEST);

        // Top-right buttons (export & more)
        JPanel topRightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        topRightButtons.setOpaque(false);

        RoundedButton exportButton = new RoundedButton("Export & Share");
        exportButton.setPreferredSize(new Dimension(160, 40));
        RoundedButton moreButton = new RoundedButton("⋮");
        moreButton.setFont(new Font("SansSerif", Font.BOLD, 20));
        moreButton.setPreferredSize(new Dimension(48, 40));

        topRightButtons.add(exportButton);
        topRightButtons.add(moreButton);

        globalTopBar.add(topRightButtons, BorderLayout.EAST);

        // More menu (three dots)
        JPopupMenu moreMenu = new JPopupMenu() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 240));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
            }
        };
        moreMenu.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        moreMenu.setOpaque(false);

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

        exitItem.addActionListener(e -> System.exit(0));
        moreButton.addActionListener(e -> moreMenu.show(moreButton, 0, moreButton.getHeight()));

        // ============================================================
        // CENTER CANVAS (scrollable)
        // ============================================================
        // Canvas container (the actual "worksheet" area)
        JPanel canvasContainer = new JPanel(new BorderLayout());
        canvasContainer.setOpaque(false);

        // A "page-like" panel that will hold worksheet content; it can be large and will be scrollable
        // Wrapper that RESIZES but does not draw
        JPanel pagePanel = new JPanel(new GridBagLayout());
        pagePanel.setOpaque(false);

// Actual render surface (the page)
        JPanel renderPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Background
                g2.setColor(Color.WHITE);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // === MARGIN GUIDE DRAWING ===
                if (showMargins) {
                    g2.setColor(new Color(0, 0, 0, 60));
                    g2.setStroke(new BasicStroke(1.2f));

                    int left = marginLeft;
                    int top = marginTop;
                    int right = getWidth() - marginRight;
                    int bottom = getHeight() - marginBottom;

                    g2.drawRect(left, top, right - left, bottom - top);
                }

                // === ORIENTATION & SCALING SUPPORTED ===
                // Grid and content repaint happen naturally here
            }
        };
        renderPanel.setLayout(new BorderLayout());
        renderPanel.setBorder(new LineBorder(new Color(170, 170, 255), 2, true));
        renderPanel.setPreferredSize(new Dimension(1000, 1400));

// Render panel in center of pagePanel
        pagePanel.add(renderPanel);

        // Keep a reference to worksheet settings
        WorksheetSettings settings = new WorksheetSettings("Default", "Tracing Letters", 20);

        // Add default blank area or grid area
        drawGridOnCanvas(pagePanel, settings);

        // Live display label at top of page (updated by Student Details)
        JLabel pageTopDisplay = new JLabel();
        pageTopDisplay.setVerticalAlignment(SwingConstants.TOP);
        pageTopDisplay.setFont(new Font("SansSerif", Font.PLAIN, 16));
        pageTopDisplay.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        pagePanel.add(pageTopDisplay, BorderLayout.NORTH);

        // Scroll pane that holds the pagePanel
        // Center wrapper for responsive canvas
        JPanel canvasWrapper = new JPanel(new GridBagLayout());
        canvasWrapper.setOpaque(false);
        canvasWrapper.add(pagePanel);

// Scroll pane
        JScrollPane canvasScroll = new JScrollPane(canvasWrapper);
        canvasScroll.setBorder(null);
        canvasScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        canvasScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        canvasScroll.getVerticalScrollBar().setUnitIncrement(12);

        canvasScroll.setBorder(null);
        canvasScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        canvasScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        canvasScroll.getVerticalScrollBar().setUnitIncrement(12);
        canvasContainer.add(canvasScroll, BorderLayout.CENTER);

        background.add(canvasContainer, BorderLayout.CENTER);


        // ============================================================
        // BOTTOM TOOLBAR + chat launcher (docked)
        // ============================================================
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        background.add(bottomPanel, BorderLayout.SOUTH);

        // Toolbar (center)
        RoundedPanel toolbar = new RoundedPanel(40);
        toolbar.setBackground(new Color(255, 255, 255, 140));
        toolbar.setLayout(new BorderLayout());
        toolbar.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        toolbar.setPreferredSize(new Dimension(0, 72)); // height fixed

        // Left icon group
        JPanel iconGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 10));
        iconGroup.setOpaque(false);
        iconGroup.add(toolbarIcon("UNDO.png", 0));
        iconGroup.add(toolbarIcon("ARROWLEFT.png", 0));
        iconGroup.add(toolbarIcon("ALIGNLEFT.png", 0));
        iconGroup.add(toolbarIcon("ALIGNCENTER.png", 0));
        iconGroup.add(toolbarIcon("ALIGNRIGHT.png", 0));
        iconGroup.add(toolbarIcon("ARROWRIGHT.png", 0));
        iconGroup.add(toolbarIcon("REDO.png", 0));
        toolbar.add(iconGroup, BorderLayout.WEST);

        // Right button group
        JPanel rightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        rightButtons.setOpaque(false);
        RoundedButton autosaveBtn = new RoundedButton("AutoSave");
        RoundedButton saveBtn = new RoundedButton(" Zoom ");
        autosaveBtn.setPreferredSize(new Dimension(140, 36));
        saveBtn.setPreferredSize(new Dimension(100, 36));
        rightButtons.add(autosaveBtn);
        rightButtons.add(saveBtn);
        toolbar.add(rightButtons, BorderLayout.EAST);

        bottomPanel.add(toolbar, BorderLayout.CENTER);

        // Chat launcher sits to the right of the toolbar
        JLabel chatLauncher = new JLabel(ResourceLoader.loadIcon("CHAT.png"));
        chatLauncher.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        chatLauncher.setToolTipText("Open Chat");
        JPanel chatLauncherWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        chatLauncherWrapper.setOpaque(false);
        chatLauncherWrapper.add(chatLauncher);
        bottomPanel.add(chatLauncherWrapper, BorderLayout.EAST);

        // Chat popup implemented as a lightweight JDialog
        JDialog chatDialog = new JDialog(frame, false);
        chatDialog.setUndecorated(true);
        chatDialog.setResizable(false);
        JPanel chatPopup = createChatPopupPanel(chatDialog);
        chatDialog.getContentPane().add(chatPopup);
        chatDialog.pack(); // initial size from panel's preferred size

        // Positioning when showing: show at bottom-right corner above toolbar
        chatLauncher.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (chatDialog.isVisible()) {
                    chatDialog.setVisible(false);
                } else {
                    // compute location relative to frame
                    Dimension d = chatDialog.getSize();
                    int x = frame.getX() + frame.getWidth() - d.width - 24;
                    int y = frame.getY() + frame.getHeight() - d.height - toolbar.getHeight() - 48;
                    chatDialog.setLocation(Math.max(0, x), Math.max(0, y));
                    chatDialog.setVisible(true);
                }
            }
        });

        // ============================================================
        // BUILD SIDEBAR SECTIONS (these methods will update pageTopDisplay / pagePanel as needed)
        // ============================================================
        // Create student details section with live preview writer
        leftContent.add(createStudentDetailsSection(pagePanel, pageTopDisplay));
        leftContent.add(Box.createVerticalStrut(8));
        leftContent.add(gridSection(pagePanel, settings));
        leftContent.add(pageSizeSection(pagePanel, canvasScroll));

        leftContent.add(Box.createVerticalStrut(8));
        leftContent.add(fontSection());
        leftContent.add(Box.createVerticalStrut(8));
        leftContent.add(importContentSection());
        leftContent.add(Box.createVerticalStrut(8));
        leftContent.add(colorPaletteSection());
        leftContent.add(Box.createVerticalStrut(8));
        leftContent.add(calculationsSection());
        leftContent.add(Box.createVerticalStrut(8));
        leftContent.add(quickFillSection());
        leftContent.add(Box.createVerticalStrut(8));
        leftContent.add(templateSection());
        leftContent.add(Box.createVerticalGlue());

        // Final frame packing and show
        frame.setSize(1400, 900);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Ensure chatDialog packs correctly relative to frame after show
        chatDialog.pack();
    }

    // ===========================
    // STUDENT DETAILS (updates pageTopDisplay)
    // ===========================
    public static JPanel createStudentDetailsSection(JPanel pagePanel, JLabel display) {

        RoundedPanel sectionPanel = new RoundedPanel(25);
        sectionPanel.setLayout(new BorderLayout());
        sectionPanel.setOpaque(false);
        sectionPanel.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        sectionPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 280));

        JPanel headerBar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(120, 140, 170), getWidth(), getHeight(), new Color(90, 110, 140));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                super.paintComponent(g);
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

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        content.add(Box.createVerticalStrut(10));
        JPanel divider = new JPanel();
        divider.setPreferredSize(new Dimension(200, 1));
        divider.setBackground(new Color(180, 180, 180));
        content.add(divider);
        content.add(Box.createVerticalStrut(12));

        JLabel nameLabel = new JLabel("Name:");
        JLabel instructionsLabel = new JLabel("Instructions:");

        JPanel nameWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        nameWrapper.setOpaque(false);
        JTextField nameField = new JTextField();
        nameField.setPreferredSize(new Dimension(220, 32));
        nameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1, true),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        nameField.setBackground(Color.WHITE);
        nameWrapper.add(nameField);

        JPanel instructionsWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        instructionsWrapper.setOpaque(false);
        JTextArea instructionsArea = new JTextArea(4, 24);
        instructionsArea.setLineWrap(true);
        instructionsArea.setWrapStyleWord(true);
        JScrollPane scroll = new JScrollPane(instructionsArea);
        scroll.setPreferredSize(new Dimension(220, 80));
        scroll.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1, true),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        instructionsWrapper.add(scroll);

        content.add(nameLabel);
        content.add(Box.createVerticalStrut(4));
        content.add(nameWrapper);
        content.add(Box.createVerticalStrut(12));
        content.add(instructionsLabel);
        content.add(Box.createVerticalStrut(4));
        content.add(instructionsWrapper);

        sectionPanel.add(content, BorderLayout.CENTER);

        // Live update runnable
        Runnable refresh = () -> {
            String name = nameField.getText();
            String ins = instructionsArea.getText().replace("\n", "<br>");
            display.setText("<html><b>Name:</b> " + name + "<br><br><b>Instructions:</b><br>" + ins + "</html>");
            pagePanel.revalidate();
            pagePanel.repaint();
        };

        nameField.getDocument().addDocumentListener(simpleListener(refresh));
        instructionsArea.getDocument().addDocumentListener(simpleListener(refresh));

        return sectionPanel;
    }
    // ============================================================
// PAGE SIZE SECTION (A4, Letter, Legal, A5, Custom)
// ============================================================
    public static JPanel pageSizeSection(JPanel pagePanel, JScrollPane canvasScroll) {

        RoundedPanel outer = new RoundedPanel(25);
        outer.setOpaque(false);
        outer.setLayout(new BorderLayout());
        outer.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        outer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 350));

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
                super.paintComponent(g);
            }
        };
        headerBar.setOpaque(false);
        headerBar.setLayout(new BorderLayout());
        headerBar.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        JLabel title = new JLabel("Page Size & Layout");
        title.setFont(new Font("SansSerif", Font.BOLD, 15));
        title.setForeground(Color.WHITE);

        JLabel arrow = new JLabel("▼");
        arrow.setFont(new Font("SansSerif", Font.BOLD, 16));
        arrow.setForeground(Color.WHITE);

        headerBar.add(title, BorderLayout.WEST);
        headerBar.add(arrow, BorderLayout.EAST);
        outer.add(headerBar, BorderLayout.NORTH);

        // ---------- CONTENT ----------
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(12, 10, 10, 10));
        content.setVisible(false);

        java.util.function.Consumer<JComponent> fullWidth = c -> {
            c.setAlignmentX(Component.LEFT_ALIGNMENT);
            c.setMaximumSize(new Dimension(Integer.MAX_VALUE, c.getPreferredSize().height));
        };

        // Page size dropdown
        JLabel sizeLabel = new JLabel("Select Page Size:");
        sizeLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        fullWidth.accept(sizeLabel);

        JComboBox<String> sizeBox = new JComboBox<>(new String[]{
                "A4 (210 × 297 mm)",
                "Letter (8.5 × 11 in)",
                "Legal (8.5 × 14 in)",
                "A5 (148 × 210 mm)",
                "Custom"
        });
        sizeBox.setFont(new Font("SansSerif", Font.PLAIN, 13));
        fullWidth.accept(sizeBox);

        // Custom size input
        JLabel customLabel = new JLabel("Custom (px):");
        customLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        fullWidth.accept(customLabel);
        customLabel.setVisible(false);

        JPanel customRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        customRow.setOpaque(false);

        JTextField customWidth = new JTextField("1000");
        customWidth.setPreferredSize(new Dimension(80, 28));

        JTextField customHeight = new JTextField("1400");
        customHeight.setPreferredSize(new Dimension(80, 28));

        customRow.add(new JLabel("W:"));
        customRow.add(customWidth);
        customRow.add(new JLabel("H:"));
        customRow.add(customHeight);

        customRow.setVisible(false);
        fullWidth.accept(customRow);

        // APPLY button
        JButton applyBtn = new JButton("Apply Page Size") {
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

        applyBtn.setOpaque(false);
        applyBtn.setBorderPainted(false);
        applyBtn.setForeground(Color.WHITE);
        applyBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        applyBtn.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        fullWidth.accept(applyBtn);

        // ---------- Action Logic ----------
        sizeBox.addActionListener(e -> {
            boolean isCustom = sizeBox.getSelectedItem().equals("Custom");
            customLabel.setVisible(isCustom);
            customRow.setVisible(isCustom);
        });

        applyBtn.addActionListener(e -> {

            int width = 1000, height = 1400;

            switch (sizeBox.getSelectedIndex()) {
                case 0: // A4
                    width = 1000;
                    height = 1414;
                    break;
                case 1: // Letter
                    width = 1000;
                    height = 1294;
                    break;
                case 2: // Legal
                    width = 1000;
                    height = 1647;
                    break;
                case 3: // A5
                    width = 700;
                    height = 990;
                    break;
                case 4: // Custom
                    try {
                        width = Integer.parseInt(customWidth.getText());
                        height = Integer.parseInt(customHeight.getText());
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Enter valid numbers.");
                        return;
                    }
                    break;
            }

            // Apply new size
            pagePanel.setPreferredSize(new Dimension(width, height));

            // Force layout refresh of:
            // - pagePanel (the page)
            // - canvasWrapper (the centering container)
            // - canvasScroll (scroll pane)
            JPanel wrapper = (JPanel) canvasScroll.getViewport().getView();
            updateCanvasLayout(pagePanel, wrapper, canvasScroll);
        });

        // Add widgets
        content.add(sizeLabel);
        content.add(sizeBox);
        content.add(Box.createVerticalStrut(10));
        content.add(customLabel);
        content.add(customRow);
        content.add(Box.createVerticalStrut(12));
        content.add(applyBtn);

        outer.add(content, BorderLayout.CENTER);

        // Collapse behaviour
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

    // ===========================
    // GRID SECTION (works with settings)
    // ===========================
    public static JPanel gridSection(JPanel pagePanel, WorksheetSettings settings) {

        RoundedPanel outer = new RoundedPanel(25);
        outer.setOpaque(false);
        outer.setLayout(new BorderLayout());
        outer.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        outer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 360));

        JPanel headerBar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(120, 140, 170), getWidth(), getHeight(), new Color(90, 110, 140));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                super.paintComponent(g);
            }
        };
        headerBar.setOpaque(false);
        headerBar.setLayout(new BorderLayout());
        headerBar.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        JLabel headerLabel = new JLabel("Grid");
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        headerLabel.setForeground(Color.WHITE);

        JLabel arrow = new JLabel("▼");
        arrow.setFont(new Font("SansSerif", Font.BOLD, 16));
        arrow.setForeground(Color.WHITE);

        headerBar.add(headerLabel, BorderLayout.WEST);
        headerBar.add(arrow, BorderLayout.EAST);
        outer.add(headerBar, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setVisible(false);
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        java.util.function.Consumer<JComponent> fullWidth = c -> {
            c.setAlignmentX(Component.LEFT_ALIGNMENT);
            c.setMaximumSize(new Dimension(Integer.MAX_VALUE, c.getPreferredSize().height));
        };

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

            drawGridOnCanvas(pagePanel, settings);
        });

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

        headerBar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        headerBar.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                boolean visible = content.isVisible();
                content.setVisible(!visible);
                arrow.setText(visible ? "▼" : "▲");
                outer.revalidate();
            }
        });

        return outer;
    }

    // Draw grid onto the provided pagePanel
    private static void drawGridOnCanvas(JPanel pagePanel, WorksheetSettings settings) {
        pagePanel.removeAll();

        JPanel gridPanel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                if (!settings.isShowGrid()) return;

                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int size = settings.getGridSize();
                int width = getWidth();
                int height = getHeight();

                Color base = settings.getGridColor();
                int alpha = (int) (settings.getGridOpacity() * 2.55);
                Color c = new Color(base.getRed(), base.getGreen(), base.getBlue(), alpha);

                g2.setColor(c);

                if (settings.isGridVertical()) {
                    for (int x = size; x < width; x += size) {
                        g2.drawLine(x, 0, x, height);
                    }
                }

                if (settings.isGridHorizontal()) {
                    for (int y = size; y < height; y += size) {
                        g2.drawLine(0, y, width, y);
                    }
                }
            }
        };

        gridPanel.setBackground(Color.WHITE);
        gridPanel.setLayout(new BorderLayout());

        pagePanel.setLayout(new BorderLayout());
        pagePanel.add(gridPanel, BorderLayout.CENTER);

        pagePanel.revalidate();
        pagePanel.repaint();
    }

    // ===========================
    // FONT SECTION
    // ===========================
    public static JPanel fontSection() {
        RoundedPanel outer = new RoundedPanel(25);
        outer.setOpaque(false);
        outer.setLayout(new BorderLayout());
        outer.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        outer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 450));

        JPanel headerBar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(120, 140, 170), getWidth(), getHeight(), new Color(90, 110, 140));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                super.paintComponent(g);
            }
        };
        headerBar.setOpaque(false);
        headerBar.setLayout(new BorderLayout());
        headerBar.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        JLabel title = new JLabel("Font");
        title.setFont(new Font("SansSerif", Font.BOLD, 15));
        title.setForeground(Color.WHITE);

        JLabel arrow = new JLabel("▼");
        arrow.setFont(new Font("SansSerif", Font.BOLD, 16));
        arrow.setForeground(Color.WHITE);

        headerBar.add(title, BorderLayout.WEST);
        headerBar.add(arrow, BorderLayout.EAST);
        outer.add(headerBar, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        content.setVisible(false);

        java.util.function.Consumer<JComponent> fullWidth = c -> {
            c.setAlignmentX(Component.LEFT_ALIGNMENT);
            c.setMaximumSize(new Dimension(Integer.MAX_VALUE, c.getPreferredSize().height));
        };

        JLabel familyLabel = new JLabel("Font Family:");
        familyLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        fullWidth.accept(familyLabel);

        JComboBox<String> familyBox = new JComboBox<>(new String[]{"SansSerif", "Serif", "Monospaced", "Dialog", "Arial"});
        familyBox.setFont(new Font("SansSerif", Font.PLAIN, 13));
        fullWidth.accept(familyBox);

        JLabel sizeLabel = new JLabel("Font Size:");
        sizeLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        fullWidth.accept(sizeLabel);

        JSpinner sizeSpinner = new JSpinner(new SpinnerNumberModel(16, 8, 72, 1));
        sizeSpinner.setFont(new Font("SansSerif", Font.PLAIN, 13));
        fullWidth.accept(sizeSpinner);

        JCheckBox bold = new JCheckBox("Bold");
        JCheckBox italic = new JCheckBox("Italic");
        JCheckBox underline = new JCheckBox("Underline");
        for (JCheckBox c : new JCheckBox[]{bold, italic, underline}) {
            c.setOpaque(false);
            c.setFont(new Font("SansSerif", Font.PLAIN, 12));
            fullWidth.accept(c);
        }

        JLabel alignLabel = new JLabel("Alignment:");
        alignLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        fullWidth.accept(alignLabel);

        Color grad1 = new Color(150, 165, 190);
        Color grad2 = new Color(110, 125, 155);

        java.util.function.Function<String, JButton> makeAlignBtn = (text) -> {
            JButton btn = new JButton(text) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    GradientPaint gp = new GradientPaint(0, 0, grad1, getWidth(), getHeight(), grad2);
                    g2.setPaint(gp);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                    super.paintComponent(g);
                }
            };
            btn.setOpaque(false);
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("SansSerif", Font.BOLD, 12));
            btn.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
            btn.setMaximumSize(new Dimension(200, 32));
            return btn;
        };

        JButton alignLeft = makeAlignBtn.apply("Left");
        JButton alignCenter = makeAlignBtn.apply("Center");
        JButton alignRight = makeAlignBtn.apply("Right");

        content.add(alignLeft);
        content.add(Box.createVerticalStrut(5));
        content.add(alignCenter);
        content.add(Box.createVerticalStrut(5));
        content.add(alignRight);
        content.add(Box.createVerticalStrut(15));

        JLabel previewLabel = new JLabel("Preview Text");
        previewLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        previewLabel.setOpaque(true);
        previewLabel.setBackground(new Color(245, 245, 245));
        previewLabel.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        fullWidth.accept(previewLabel);

        java.util.function.BiConsumer<JLabel, Boolean> setUnderline = (lbl, active) -> {
            Font font = lbl.getFont();
            java.util.Map<TextAttribute, Object> map = new java.util.HashMap<>(font.getAttributes());
            if (active) map.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
            else map.remove(TextAttribute.UNDERLINE);
            lbl.setFont(font.deriveFont(map));
        };

        Runnable updatePreview = () -> {
            int size = (int) sizeSpinner.getValue();
            String family = (String) familyBox.getSelectedItem();

            int style = Font.PLAIN;
            if (bold.isSelected()) style |= Font.BOLD;
            if (italic.isSelected()) style |= Font.ITALIC;

            Font base = new Font(family, style, size);
            previewLabel.setFont(base);
            setUnderline.accept(previewLabel, underline.isSelected());
        };

        familyBox.addActionListener(e -> updatePreview.run());
        sizeSpinner.addChangeListener(e -> updatePreview.run());
        bold.addActionListener(e -> updatePreview.run());
        italic.addActionListener(e -> updatePreview.run());
        underline.addActionListener(e -> updatePreview.run());

        content.add(familyLabel);
        content.add(familyBox);
        content.add(Box.createVerticalStrut(10));
        content.add(sizeLabel);
        content.add(sizeSpinner);
        content.add(Box.createVerticalStrut(12));
        content.add(bold);
        content.add(italic);
        content.add(underline);
        content.add(Box.createVerticalStrut(15));
        content.add(alignLabel);
        content.add(Box.createVerticalStrut(5));
        content.add(previewLabel);
        content.add(Box.createVerticalStrut(5));

        outer.add(content, BorderLayout.CENTER);

        headerBar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        headerBar.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                boolean visible = content.isVisible();
                content.setVisible(!visible);
                arrow.setText(visible ? "▼" : "▲");
                outer.revalidate();
            }
        });

        return outer;
    }

    // ===========================
    // IMPORT CONTENT SECTION
    // ===========================
    public static JPanel importContentSection() {
        RoundedPanel outer = new RoundedPanel(25);
        outer.setOpaque(false);
        outer.setLayout(new BorderLayout());
        outer.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        outer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 520));

        JPanel headerBar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(120, 140, 170), getWidth(), getHeight(), new Color(90, 110, 140));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                super.paintComponent(g);
            }
        };
        headerBar.setOpaque(false);
        headerBar.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        headerBar.setLayout(new BorderLayout());

        JLabel title = new JLabel("Import Content");
        title.setFont(new Font("SansSerif", Font.BOLD, 15));
        title.setForeground(Color.WHITE);

        JLabel arrow = new JLabel("▼");
        arrow.setFont(new Font("SansSerif", Font.BOLD, 16));
        arrow.setForeground(Color.WHITE);

        headerBar.add(title, BorderLayout.WEST);
        headerBar.add(arrow, BorderLayout.EAST);
        outer.add(headerBar, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        content.setVisible(false);

        java.util.function.Consumer<JComponent> fullWidth = c -> {
            c.setAlignmentX(Component.LEFT_ALIGNMENT);
            c.setMaximumSize(new Dimension(Integer.MAX_VALUE, c.getPreferredSize().height));
        };

        Color grad1 = new Color(150, 165, 190);
        Color grad2 = new Color(110, 125, 155);

        java.util.function.Function<String, JButton> styledBtn = (text) -> {
            JButton btn = new JButton(text) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    GradientPaint gp = new GradientPaint(0, 0, grad1, getWidth(), getHeight(), grad2);
                    g2.setPaint(gp);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                    super.paintComponent(g);
                }
            };
            btn.setOpaque(false);
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("SansSerif", Font.BOLD, 13));
            btn.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
            fullWidth.accept(btn);
            return btn;
        };

        JButton addLogo = styledBtn.apply("Add Logo");
        JButton addImages = styledBtn.apply("Add Images");
        JButton importImage = styledBtn.apply("Upload Image");
        JButton importTextFile = styledBtn.apply("Upload Text File");
        JButton removeButton = styledBtn.apply("Remove Content");

        JLabel modeLabel = new JLabel("Colour Mode:");
        modeLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        fullWidth.accept(modeLabel);

        JRadioButton colorMode = new JRadioButton("Color");
        JRadioButton bwMode = new JRadioButton("Black & White");

        ButtonGroup modeGroup = new ButtonGroup();
        modeGroup.add(colorMode);
        modeGroup.add(bwMode);
        colorMode.setSelected(true);

        for (JRadioButton r : new JRadioButton[]{colorMode, bwMode}) {
            r.setOpaque(false);
            r.setFont(new Font("SansSerif", Font.PLAIN, 12));
            fullWidth.accept(r);
        }

        JLabel pasteLabel = new JLabel("Paste Text:");
        pasteLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        fullWidth.accept(pasteLabel);

        JTextArea pasteArea = new JTextArea(4, 20);
        pasteArea.setLineWrap(true);
        pasteArea.setWrapStyleWord(true);
        JScrollPane pasteScroll = new JScrollPane(pasteArea);
        pasteScroll.setMaximumSize(new Dimension(220, 70));
        fullWidth.accept(pasteScroll);

        JLabel previewLabel = new JLabel("No content imported");
        previewLabel.setOpaque(true);
        previewLabel.setBackground(new Color(245, 245, 245));
        previewLabel.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        previewLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        fullWidth.accept(previewLabel);

        addLogo.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                previewLabel.setText("Logo Added: " + chooser.getSelectedFile().getName());
            }
        });

        addImages.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setMultiSelectionEnabled(true);
            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                previewLabel.setText("Images Added: " + chooser.getSelectedFiles().length);
            }
        });

        importImage.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                previewLabel.setText("Imported Image: " + chooser.getSelectedFile().getName());
            }
        });

        importTextFile.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                previewLabel.setText("Imported Text File: " + chooser.getSelectedFile().getName());
            }
        });

        pasteArea.getDocument().addDocumentListener(simpleListener(() -> {
            if (!pasteArea.getText().trim().isEmpty()) {
                previewLabel.setText("<html><b>Pasted Text:</b><br>" + pasteArea.getText().replace("\n", "<br>") + "</html>");
            }
        }));

        removeButton.addActionListener(e -> {
            pasteArea.setText("");
            previewLabel.setText("No content imported");
        });

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
        content.add(Box.createVerticalStrut(12));
        content.add(pasteLabel);
        content.add(pasteScroll);
        content.add(Box.createVerticalStrut(12));
        content.add(previewLabel);
        content.add(Box.createVerticalStrut(12));
        content.add(removeButton);

        outer.add(content, BorderLayout.CENTER);

        headerBar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        headerBar.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                boolean visible = content.isVisible();
                content.setVisible(!visible);
                arrow.setText(visible ? "▼" : "▲");
                outer.revalidate();
            }
        });

        return outer;
    }

    // ===========================
    // COLOUR PALETTE SECTION
    // ===========================
    public static JPanel colorPaletteSection() {
        RoundedPanel outer = new RoundedPanel(25);
        outer.setOpaque(false);
        outer.setLayout(new BorderLayout());
        outer.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        outer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 420));

        JPanel headerBar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(120, 140, 170), getWidth(), getHeight(), new Color(90, 110, 140));
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

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        content.setVisible(false);

        java.util.function.Consumer<JComponent> fullWidth = c -> {
            c.setAlignmentX(Component.LEFT_ALIGNMENT);
            c.setMaximumSize(new Dimension(Integer.MAX_VALUE, c.getPreferredSize().height));
        };

        JButton textColorButton = new JButton("Choose Text Colour") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(150, 165, 190), getWidth(), getHeight(), new Color(110, 125, 155));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                super.paintComponent(g);
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

        JButton backgroundColorButton = new JButton("Choose Background Colour") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(150, 165, 190), getWidth(), getHeight(), new Color(110, 125, 155));
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
                @Override public void mouseClicked(java.awt.event.MouseEvent evt) {
                    textColorPreview.setBackground(c);
                    textColorPreview.setForeground(c.equals(Color.BLACK) ? Color.WHITE : Color.BLACK);
                }
            });
            swatchPanel.add(sw);
        }

        JButton applyButton = new JButton("Apply Colours") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(110, 125, 155), getWidth(), getHeight(), new Color(80, 90, 120));
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

        applyButton.addActionListener(e -> JOptionPane.showMessageDialog(null, "Colours applied successfully!"));

        JButton resetButton = new JButton("Reset Colours") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(150, 165, 190), getWidth(), getHeight(), new Color(110, 125, 155));
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

        headerBar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        headerBar.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                boolean visible = content.isVisible();
                content.setVisible(!visible);
                arrow.setText(visible ? "▼" : "▲");
                outer.revalidate();
            }
        });

        return outer;
    }

    // ===========================
    // CALCULATIONS SECTION
    // ===========================
    public static JPanel calculationsSection() {
        RoundedPanel outer = new RoundedPanel(25);
        outer.setOpaque(false);
        outer.setLayout(new BorderLayout());
        outer.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        outer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 420));

        JPanel headerBar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(120, 140, 170), getWidth(), getHeight(), new Color(90, 110, 140));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                super.paintComponent(g);
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

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        content.setVisible(false);

        java.util.function.Consumer<JComponent> fullWidth = c -> {
            c.setAlignmentX(Component.LEFT_ALIGNMENT);
            c.setMaximumSize(new Dimension(Integer.MAX_VALUE, c.getPreferredSize().height));
        };

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

        JLabel problemLabel = new JLabel("How many problems?");
        problemLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        fullWidth.accept(problemLabel);

        SpinnerNumberModel problemModel = new SpinnerNumberModel(10, 1, 50, 1);
        JSpinner problemSpinner = new JSpinner(problemModel);
        fullWidth.accept(problemSpinner);

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

        JButton generateButton = new JButton("Generate Problems") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(150, 165, 190), getWidth(), getHeight(), new Color(110, 125, 155));
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

        generateButton.addActionListener(e -> JOptionPane.showMessageDialog(null, "Math problem generation feature coming soon!", "Coming Soon", JOptionPane.INFORMATION_MESSAGE));

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

        headerBar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        headerBar.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                boolean visible = content.isVisible();
                content.setVisible(!visible);
                arrow.setText(visible ? "▼" : "▲");
                outer.revalidate();
            }
        });

        return outer;
    }

    // ===========================
    // QUICK FILL SECTION
    // ===========================
    public static JPanel quickFillSection() {
        RoundedPanel outer = new RoundedPanel(25);
        outer.setOpaque(false);
        outer.setLayout(new BorderLayout());
        outer.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        outer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 420));

        JPanel headerBar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(120, 140, 170), getWidth(), getHeight(), new Color(90, 110, 140));
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

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setVisible(false);
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel grid = new JPanel(new GridLayout(0, 2, 12, 12));
        grid.setOpaque(false);

        Color grad1 = new Color(150, 165, 190);
        Color grad2 = new Color(110, 125, 155);

        java.util.function.Function<String, JButton> makeBtn = (text) -> {
            JButton btn = new JButton(text) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    GradientPaint gp = new GradientPaint(0, 0, grad1, getWidth(), getHeight(), grad2);
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

            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mouseEntered(java.awt.event.MouseEvent e) { btn.setForeground(new Color(230, 230, 255)); }
                @Override public void mouseExited(java.awt.event.MouseEvent e) { btn.setForeground(Color.WHITE); }
                @Override public void mousePressed(java.awt.event.MouseEvent e) { btn.setForeground(Color.LIGHT_GRAY); }
            });

            return btn;
        };

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
        grid.add(new JLabel());

        content.add(grid);
        content.add(Box.createVerticalStrut(10));

        outer.add(content, BorderLayout.CENTER);

        headerBar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        headerBar.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                boolean visible = content.isVisible();
                content.setVisible(!visible);
                arrow.setText(visible ? "▼" : "▲");
                outer.revalidate();
            }
        });

        return outer;
    }

    // ===========================
    // TEMPLATE SECTION
    // ===========================
    public static JPanel templateSection() {
        RoundedPanel outer = new RoundedPanel(25);
        outer.setOpaque(false);
        outer.setLayout(new BorderLayout());
        outer.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        outer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 420));

        JPanel headerBar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(120, 140, 170), getWidth(), getHeight(), new Color(90, 110, 140));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                super.paintComponent(g);
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

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        content.setVisible(false);

        JPanel grid = new JPanel(new GridLayout(0, 2, 12, 12));
        grid.setOpaque(false);

        Color grad1 = new Color(150, 165, 190);
        Color grad2 = new Color(110, 125, 155);

        java.util.function.Function<String, JButton> makeBtn = (text) -> {
            JButton btn = new JButton(text) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    GradientPaint gp = new GradientPaint(0, 0, grad1, getWidth(), getHeight(), grad2);
                    g2.setPaint(gp);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                    super.paintComponent(g);
                }
            };
            btn.setOpaque(false);
            btn.setContentAreaFilled(false);
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("SansSerif", Font.BOLD, 13));
            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mouseEntered(java.awt.event.MouseEvent e) { btn.setForeground(new Color(230, 230, 255)); }
                @Override public void mouseExited(java.awt.event.MouseEvent e) { btn.setForeground(Color.WHITE); }
                @Override public void mousePressed(java.awt.event.MouseEvent e) { btn.setForeground(Color.LIGHT_GRAY); }
            });
            return btn;
        };

        grid.add(makeBtn.apply("Basic"));
        grid.add(makeBtn.apply("Lined"));
        grid.add(makeBtn.apply("Graph"));
        grid.add(makeBtn.apply("Handwriting"));
        grid.add(makeBtn.apply("Math Grid"));
        grid.add(makeBtn.apply("Table"));
        grid.add(makeBtn.apply("Flashcards"));
        grid.add(makeBtn.apply("Blank"));

        content.add(grid);
        content.add(Box.createVerticalStrut(10));

        outer.add(content, BorderLayout.CENTER);

        headerBar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        headerBar.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                boolean visible = content.isVisible();
                content.setVisible(!visible);
                arrow.setText(visible ? "▼" : "▲");
                outer.revalidate();
            }
        });

        return outer;
    }

    // ===========================
    // SUPPORT UTILITIES
    // ===========================
    public static DocumentListener simpleListener(Runnable run) {
        return new DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { run.run(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { run.run(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { run.run(); }
        };
    }

    static class GradientPanel extends JPanel {
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            GradientPaint gp = new GradientPaint(0, 0, new Color(180, 210, 230), 0, getHeight(), new Color(60, 90, 120));
            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    static class RoundedPanel extends JPanel {
        private final int radius;
        public RoundedPanel(int radius) { this.radius = radius; setOpaque(false); }
        @Override protected void paintComponent(Graphics g) {
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
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(255, 255, 255, 200));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
            super.paintComponent(g);
        }
    }

    /**
     * Loads icon via ResourceLoader. x parameter retained for API compatibility with original method (unused here).
     */
    public static JLabel toolbarIcon(String filename, int x) {
        ImageIcon imageIcon = ResourceLoader.loadIcon(filename);
        JLabel icon;
        if (imageIcon != null) {
            icon = new JLabel(imageIcon);
        } else {
            System.err.println("Toolbar icon not found: " + filename);
            icon = new JLabel(filename.replace(".png", ""));
            icon.setForeground(Color.RED);
            icon.setFont(new Font("SansSerif", Font.BOLD, 10));
        }
        icon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        icon.setOpaque(false);
        // subtle hover effect
        icon.addMouseListener(new java.awt.event.MouseAdapter() {
            Color normal = new Color(255,255,255,0);
            Color hover = new Color(255,255,255,80);
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { icon.setOpaque(true); icon.setBackground(hover); }
            @Override public void mouseExited(java.awt.event.MouseEvent e) { icon.setOpaque(false); icon.setBackground(null); }
        });
        return icon;
    }

    // Creates the chat popup panel used inside a JDialog
    private static JPanel createChatPopupPanel(JDialog parent) {
        RoundedPanel popup = new RoundedPanel(30);
        popup.setLayout(new BorderLayout());
        popup.setBackground(Color.WHITE);
        popup.setBorder(BorderFactory.createEmptyBorder(0,0,12,0));
        popup.setPreferredSize(new Dimension(320, 480));

        RoundedPanel header = new RoundedPanel(30) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(80, 140, 255), 0, getHeight(), new Color(40, 100, 220));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                super.paintComponent(g);
            }
        };
        header.setPreferredSize(new Dimension(320, 70));
        header.setLayout(null);

        JButton closeBtn = new JButton("⟵");
        closeBtn.setFont(new Font("SansSerif", Font.BOLD, 20));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFocusPainted(false);
        closeBtn.setContentAreaFilled(false);
        closeBtn.setBorderPainted(false);
        closeBtn.setBounds(270, 15, 40, 40);
        closeBtn.addActionListener(e -> parent.setVisible(false));
        header.add(closeBtn);

        JLabel title = new JLabel("Chat with EduCreate");
        title.setFont(new Font("SansSerif", Font.BOLD, 14));
        title.setForeground(Color.WHITE);
        title.setBounds(20, 20, 260, 30);
        header.add(title);

        popup.add(header, BorderLayout.NORTH);

        JPanel chatArea = new JPanel();
        chatArea.setLayout(new BoxLayout(chatArea, BoxLayout.Y_AXIS));
        chatArea.setBackground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(chatArea);
        scroll.setBorder(null);
        scroll.setPreferredSize(new Dimension(300, 300));
        popup.add(scroll, BorderLayout.CENTER);

        JPanel inputRow = new JPanel(null);
        inputRow.setPreferredSize(new Dimension(320, 64));
        inputRow.setBackground(Color.WHITE);

        JTextField input = new JTextField();
        input.setBounds(15, 12, 230, 40);
        inputRow.add(input);

        JButton sendBtn = new JButton("➤");
        sendBtn.setFont(new Font("SansSerif", Font.BOLD, 18));
        sendBtn.setBounds(255, 12, 50, 40);
        sendBtn.setFocusPainted(false);
        inputRow.add(sendBtn);

        sendBtn.addActionListener(e -> {
            String text = input.getText().trim();
            if (text.isEmpty()) return;
            chatArea.add(createBubble(text, true));
            chatArea.revalidate();
            input.setText("");
            Timer t = new Timer(500, ev -> {
                chatArea.add(createBubble("I’m here to help! 😊", false));
                chatArea.revalidate();
            });
            t.setRepeats(false);
            t.start();
        });

        popup.add(inputRow, BorderLayout.SOUTH);
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
    public static void updateCanvasLayout(JPanel pagePanel, JPanel canvasWrapper, JScrollPane scroll) {
        pagePanel.revalidate();
        pagePanel.repaint();

        canvasWrapper.revalidate();
        canvasWrapper.repaint();

        scroll.revalidate();
        scroll.repaint();
    }

}
