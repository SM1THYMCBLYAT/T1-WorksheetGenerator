package com.espaneg.ui;

import com.espaneg.logic.MathGen;
import com.espaneg.model.WorksheetSettings;
import com.espaneg.services.PdfService;
import com.espaneg.utils.ResourceLoader;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.awt.print.Printable;
import java.awt.print.PrinterJob;
import java.util.ArrayList;

public class WorksheetGenerator {
    // Page state
    static boolean fitToWidth = false;
    static boolean showMargins = true;

    static int marginTop = 50;
    static int marginBottom = 50;
    static int marginLeft = 50;
    static int marginRight = 50;
    // header & center content references (for cross-method updates)
    static JPanel headerPanel;
    static JPanel centerContentPanel; // holds grid / editor / templates etc.
    static JLabel headerLogoLabel;
    static JLabel headerNameLabel;
    static JLabel headerInstructionsLabel;
    static JPanel pagePanel;

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
              //  UserLogin.main(null);
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
        RoundedButton printButton = new RoundedButton("Print");
        printButton.setPreferredSize(new Dimension(120, 40));

        exportButton.addActionListener(e -> {
            try {
                // 1. Choose save path
                JFileChooser chooser = new JFileChooser();
                chooser.setDialogTitle("Save Worksheet as PDF");
                chooser.setSelectedFile(new java.io.File("worksheet.pdf"));

                if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {

                    java.io.File file = chooser.getSelectedFile();

                    // 2. Convert the page panel (renderPanel) into a PDF
                    PdfService.exportPanelAsPDF(file.getAbsolutePath(), pagePanel);

                    JOptionPane.showMessageDialog(null,
                            "PDF exported successfully!",
                            "Export Complete",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Failed to export PDF.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        printButton.addActionListener(e -> {
            try {
                PrinterJob job = PrinterJob.getPrinterJob();

                job.setJobName("EduCreate Worksheet");

                job.setPrintable((graphics, pageFormat, pageIndex) -> {
                    if (pageIndex > 0) return Printable.NO_SUCH_PAGE;

                    Graphics2D g2 = (Graphics2D) graphics;

                    // Scale worksheet to fit printable area
                    double scaleX = pageFormat.getImageableWidth() / pagePanel.getWidth();
                    double scaleY = pageFormat.getImageableHeight() / pagePanel.getHeight();
                    double scale = Math.min(scaleX, scaleY);

                    g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
                    g2.scale(scale, scale);

                    pagePanel.print(g2);

                    return Printable.PAGE_EXISTS;
                });

                if (job.printDialog()) {
                    job.print();
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Failed to print the worksheet.",
                        "Print Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        topRightButtons.add(exportButton);
        topRightButtons.add(printButton);
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
        // ---------- PAGE PANEL (with header + center content) ----------
        pagePanel = new JPanel(new BorderLayout());
        pagePanel.setOpaque(false);

// The actual white "paper" area renderer
        JPanel renderPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(Color.WHITE);
                g2.fillRect(0, 0, getWidth(), getHeight());

                if (showMargins) {
                    g2.setColor(new Color(0, 0, 0, 60));
                    g2.setStroke(new BasicStroke(1.2f));
                    int left = marginLeft;
                    int top = marginTop;
                    int right = getWidth() - marginRight;
                    int bottom = getHeight() - marginBottom;
                    g2.drawRect(left, top, right - left, bottom - top);
                }
            }
        };
        renderPanel.setLayout(new BorderLayout());
        renderPanel.setBorder(new LineBorder(new Color(170, 170, 255), 2, true));
        renderPanel.setPreferredSize(new Dimension(1000, 1400));
        renderPanel.setBackground(Color.WHITE);
        renderPanel.setOpaque(true);

// Center area holds the editable/grid content
        centerContentPanel = new JPanel(new BorderLayout());
        centerContentPanel.setBackground(Color.WHITE);
        centerContentPanel.setOpaque(true);


// Keep a reference to worksheet settings
        WorksheetSettings settings = new WorksheetSettings("Default", "Tracing Letters", 20);

// Build fixed header INSIDE the white render panel
        headerPanel = buildHeaderPanel(pagePanel, settings);
        renderPanel.add(headerPanel, BorderLayout.NORTH);

// Put center content ALSO inside renderPanel
        renderPanel.add(centerContentPanel, BorderLayout.CENTER);
// white paper to the page panel (THIS WAS MISSING)
        pagePanel.add(renderPanel, BorderLayout.CENTER);


        // Add default blank area or grid area
        drawGridOnCanvas(pagePanel, settings);

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
        JLabel undoBtn = toolbarIcon("UNDO.png", 0);
        JLabel leftArrowBtn = toolbarIcon("ARROWLEFT.png", 0);
        JLabel alignLeftBtn = toolbarIcon("ALIGNLEFT.png", 0);
        JLabel alignCenterBtn = toolbarIcon("ALIGNCENTER.png", 0);
        JLabel alignRightBtn = toolbarIcon("ALIGNRIGHT.png", 0);
        JLabel rightArrowBtn = toolbarIcon("ARROWRIGHT.png", 0);
        JLabel redoBtn = toolbarIcon("REDO.png", 0);

        iconGroup.add(undoBtn);
        iconGroup.add(leftArrowBtn);
        iconGroup.add(alignLeftBtn);
        iconGroup.add(alignCenterBtn);
        iconGroup.add(alignRightBtn);
        iconGroup.add(rightArrowBtn);
        iconGroup.add(redoBtn);
// ---------------------------------------------------------
// TOOLBAR ACTIONS
// ---------------------------------------------------------

        final String[] lastContent = {""};
        final String[] redoContent = {""};

// UNDO
        undoBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (centerContentPanel.getComponentCount() > 0 &&
                        centerContentPanel.getComponent(0) instanceof JScrollPane scroll) {

                    JTextPane editor = (JTextPane) scroll.getViewport().getView();
                    redoContent[0] = editor.getText();
                    editor.setText(lastContent[0]);
                }
            }
        });

// REDO
        redoBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (centerContentPanel.getComponentCount() > 0 &&
                        centerContentPanel.getComponent(0) instanceof JScrollPane scroll) {

                    JTextPane editor = (JTextPane) scroll.getViewport().getView();
                    editor.setText(redoContent[0]);
                }
            }
        });

// Save editor text whenever new content is added
        centerContentPanel.addContainerListener(new java.awt.event.ContainerAdapter() {
            @Override public void componentAdded(java.awt.event.ContainerEvent e) {
                if (e.getChild() instanceof JScrollPane scroll) {
                    JTextPane editor = (JTextPane) scroll.getViewport().getView();
                    lastContent[0] = editor.getText();
                }
            }
        });

// ALIGN LEFT
        alignLeftBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                applyAlignmentToEditor(StyleConstants.ALIGN_LEFT);
            }
        });

// ALIGN CENTER
        alignCenterBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                applyAlignmentToEditor(StyleConstants.ALIGN_CENTER);
            }
        });

// ALIGN RIGHT
        alignRightBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                applyAlignmentToEditor(StyleConstants.ALIGN_RIGHT);
            }
        });

// ARROWS – scroll canvas left/right
        leftArrowBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                JScrollPane scroll = (JScrollPane) canvasContainer.getParent()
                        .getParent();
                scroll.getHorizontalScrollBar().setValue(
                        scroll.getHorizontalScrollBar().getValue() - 200
                );
            }
        });

        rightArrowBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                JScrollPane scroll = (JScrollPane) canvasContainer.getParent()
                        .getParent();
                scroll.getHorizontalScrollBar().setValue(
                        scroll.getHorizontalScrollBar().getValue() + 200
                );
            }
        });

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
        leftContent.add(createStudentDetailsSection(pagePanel));

        leftContent.add(Box.createVerticalStrut(8));
        leftContent.add(gridSection(pagePanel, settings));
        leftContent.add(pageSizeSection(pagePanel, canvasScroll));

        leftContent.add(Box.createVerticalStrut(8));
        leftContent.add(fontSection(pagePanel, settings));
        leftContent.add(Box.createVerticalStrut(8));
        leftContent.add(importContentSection(pagePanel));

        leftContent.add(Box.createVerticalStrut(8));
//        leftContent.add(colorPaletteSection(pagePanel, renderPanel, null, settings));
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
    public static JPanel createStudentDetailsSection(JPanel pagePanel) {

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

        // LIVE UPDATE (updates headerPanel ONLY)
        Runnable refresh = () -> {
            String name = nameField.getText();
            String ins = instructionsArea.getText();
            updateHeaderText(name, ins);
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
    // Draw grid into centerContentPanel (preserve header)
    private static void drawGridOnCanvas(JPanel pagePanel, WorksheetSettings settings) {
        if (centerContentPanel == null) return;

        // Clear center content only, keep headerPanel intact
        centerContentPanel.removeAll();

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

        // Match grid size to page size
        Dimension pageSize = pagePanel.getPreferredSize();
        if (pageSize != null && pageSize.width > 0 && pageSize.height > 0) {
            gridPanel.setPreferredSize(pageSize);
        }

        centerContentPanel.add(gridPanel, BorderLayout.CENTER);

        centerContentPanel.revalidate();
        centerContentPanel.repaint();
    }


    // ===========================
    // FONT SECTION
    // ===========================
    public static JPanel fontSection(JPanel pagePanel, WorksheetSettings settings) {

        RoundedPanel outer = new RoundedPanel(25);
        outer.setOpaque(false);
        outer.setLayout(new BorderLayout());
        outer.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        outer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 500));

        // ---------- HEADER ----------
        JPanel headerBar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0,
                        new Color(120, 140, 170),
                        getWidth(), getHeight(),
                        new Color(90, 110, 140));
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

        // ---------- CONTENT ----------
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        content.setVisible(false);

        java.util.function.Consumer<JComponent> fullWidth =
                c -> {
                    c.setAlignmentX(Component.LEFT_ALIGNMENT);
                    c.setMaximumSize(new Dimension(Integer.MAX_VALUE, c.getPreferredSize().height));
                };

        // --- FONT FAMILY ---
        JLabel familyLabel = new JLabel("Font Family:");
        familyLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        fullWidth.accept(familyLabel);

        JComboBox<String> familyBox = new JComboBox<>(
                new String[]{"SansSerif", "Serif", "Monospaced", "Dialog", "Arial"}
        );
        familyBox.setFont(new Font("SansSerif", Font.PLAIN, 13));
        fullWidth.accept(familyBox);

        // --- SIZE ---
        JLabel sizeLabel = new JLabel("Font Size:");
        sizeLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        fullWidth.accept(sizeLabel);

        JSpinner sizeSpinner = new JSpinner(new SpinnerNumberModel(24, 8, 200, 1));
        sizeSpinner.setFont(new Font("SansSerif", Font.PLAIN, 13));
        fullWidth.accept(sizeSpinner);

        // --- STYLE CHECKBOXES ---
        JCheckBox bold = new JCheckBox("Bold");
        JCheckBox italic = new JCheckBox("Italic");
        JCheckBox underline = new JCheckBox("Underline");
        for (JCheckBox c : new JCheckBox[]{bold, italic, underline}) {
            c.setOpaque(false);
            c.setFont(new Font("SansSerif", Font.PLAIN, 12));
            fullWidth.accept(c);
        }

        // --- ALIGNMENT BUTTONS (preview + apply) ---
        JLabel alignLabel = new JLabel("Alignment:");
        alignLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        fullWidth.accept(alignLabel);

        JPanel alignRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        alignRow.setOpaque(false);

        JButton alignLeft = styleButton(new JButton("Left"), new Color(150,165,190));
        JButton alignCenter = styleButton(new JButton("Center"), new Color(150,165,190));
        JButton alignRight = styleButton(new JButton("Right"), new Color(150,165,190));
        alignLeft.setPreferredSize(new Dimension(76, 28));
        alignCenter.setPreferredSize(new Dimension(76, 28));
        alignRight.setPreferredSize(new Dimension(76, 28));

        alignRow.add(alignLeft);
        alignRow.add(alignCenter);
        alignRow.add(alignRight);
        fullWidth.accept(alignRow);

        final int[] selectedAlignment = {StyleConstants.ALIGN_LEFT}; // default

        alignLeft.addActionListener(e -> {
            selectedAlignment[0] = StyleConstants.ALIGN_LEFT;
            alignLeft.setEnabled(false);
            alignCenter.setEnabled(true);
            alignRight.setEnabled(true);
        });

        alignCenter.addActionListener(e -> {
            selectedAlignment[0] = StyleConstants.ALIGN_CENTER;
            alignLeft.setEnabled(true);
            alignCenter.setEnabled(false);
            alignRight.setEnabled(true);
        });

        alignRight.addActionListener(e -> {
            selectedAlignment[0] = StyleConstants.ALIGN_RIGHT;
            alignLeft.setEnabled(true);
            alignCenter.setEnabled(true);
            alignRight.setEnabled(false);
        });

        // --- TEXT INPUT (the side panel input you type into) ---
        JLabel inputLabel = new JLabel("Type text for the page (this will replace page content):");
        inputLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        fullWidth.accept(inputLabel);

        JTextArea sideInput = new JTextArea(4, 24);
        sideInput.setLineWrap(true);
        sideInput.setWrapStyleWord(true);
        JScrollPane sideScroll = new JScrollPane(sideInput);
        sideScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        fullWidth.accept(sideScroll);

        // --- PREVIEW LABEL ---
        JLabel previewLabel = new JLabel("<html>Preview: <br><i>Text will appear full-page after Apply</i></html>");
        previewLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        previewLabel.setOpaque(true);
        previewLabel.setBackground(new Color(245, 245, 245));
        previewLabel.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        fullWidth.accept(previewLabel);

        // update preview runnable
        Runnable updatePreview = () -> {
            int size = (int) sizeSpinner.getValue();
            String fam = (String) familyBox.getSelectedItem();
            int style = Font.PLAIN;
            if (bold.isSelected()) style |= Font.BOLD;
            if (italic.isSelected()) style |= Font.ITALIC;

            Font pFont = new Font(fam, style, size);
            previewLabel.setFont(pFont);
            // underline:
            if (underline.isSelected()) {
                Font f = previewLabel.getFont();
                java.util.Map<TextAttribute, Object> map = new java.util.HashMap<>(f.getAttributes());
                map.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
                previewLabel.setFont(f.deriveFont(map));
            } else {
                // remove underline by rebuilding font
                previewLabel.setFont(pFont);
            }
        };

        familyBox.addActionListener(e -> updatePreview.run());
        sizeSpinner.addChangeListener(e -> updatePreview.run());
        bold.addActionListener(e -> updatePreview.run());
        italic.addActionListener(e -> updatePreview.run());
        underline.addActionListener(e -> updatePreview.run());

        // ==========================================
        // TEXT COLOR PICKER + APPLY + RESET
        // ==========================================
        JLabel colorHeader = new JLabel("Text Color:");
        colorHeader.setFont(new Font("SansSerif", Font.PLAIN, 12));
        fullWidth.accept(colorHeader);

        JButton chooseColorBtn = styleButton(new JButton("Choose Text Color"), new Color(150, 165, 190));
        chooseColorBtn.setForeground(Color.WHITE);
        chooseColorBtn.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        fullWidth.accept(chooseColorBtn);

        final Color[] selectedTextColor = {Color.BLACK};
        chooseColorBtn.addActionListener(e -> {
            Color chosen = JColorChooser.showDialog(null, "Select Text Color", selectedTextColor[0]);
            if (chosen != null) {
                selectedTextColor[0] = chosen;
                previewLabel.setForeground(chosen);
            }
        });

        // APPLY button: creates a full-page JTextPane inside pagePanel
        JButton applyBtn = styleButton(new JButton("Apply to Page"), new Color(186, 210, 241));
        applyBtn.setForeground(Color.WHITE);
        applyBtn.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        fullWidth.accept(applyBtn);

        applyBtn.addActionListener(e -> {
            // Gather font settings
            String fam = (String) familyBox.getSelectedItem();
            int sz = (int) sizeSpinner.getValue();
            boolean isBold = bold.isSelected();
            boolean isItalic = italic.isSelected();
            boolean isUnderline = underline.isSelected();
            Color color = selectedTextColor[0];
            int align = selectedAlignment[0];
            String text = sideInput.getText();

            // Use pagePanel preferred size
            Dimension pageSize = pagePanel.getPreferredSize();
            if (pageSize == null || pageSize.width == 0 || pageSize.height == 0) {
                pageSize = new Dimension(1000, 1400);
            }

            // Clear ONLY the center content (header stays!)
            centerContentPanel.removeAll();

            // Create editor
            JTextPane editor = new JTextPane();
            editor.setEditable(true);
            editor.setBackground(Color.WHITE);
            editor.setPreferredSize(pageSize);
            editor.setMinimumSize(pageSize);
            editor.setMaximumSize(pageSize);

            // Apply text styling
            StyledDocument doc = editor.getStyledDocument();
            SimpleAttributeSet attrs = new SimpleAttributeSet();
            StyleConstants.setFontFamily(attrs, fam);
            StyleConstants.setFontSize(attrs, sz);
            StyleConstants.setBold(attrs, isBold);
            StyleConstants.setItalic(attrs, isItalic);
            StyleConstants.setForeground(attrs, color);
            StyleConstants.setUnderline(attrs, isUnderline);

            try {
                doc.remove(0, doc.getLength());
                doc.insertString(0, text, attrs);
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }

            // Set alignment
            SimpleAttributeSet para = new SimpleAttributeSet();
            StyleConstants.setAlignment(para, align);
            doc.setParagraphAttributes(0, doc.getLength(), para, false);

            // Put editor in scroll pane
            JScrollPane editorScroll = new JScrollPane(editor);
            editorScroll.setBorder(new LineBorder(new Color(141, 157, 177), 2, true));
            editorScroll.setPreferredSize(pageSize);
            editorScroll.setMinimumSize(pageSize);
            editorScroll.setMaximumSize(pageSize);

            // Add editor to CENTER CONTENT ONLY
            centerContentPanel.add(editorScroll, BorderLayout.CENTER);

            // Refresh
            centerContentPanel.revalidate();
            centerContentPanel.repaint();
            pagePanel.revalidate();
            pagePanel.repaint();

            JOptionPane.showMessageDialog(null, "Page updated. You can edit text directly on the page.");
        });


        // RESET button: restore defaults and redraw page (blank/grid)
        JButton resetBtn = styleButton(new JButton("Reset Font & Clear Page"), new Color(186, 210, 241));
        resetBtn.setForeground(Color.WHITE);
        resetBtn.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        fullWidth.accept(resetBtn);

        resetBtn.addActionListener(e -> {
            // reset UI controls to defaults
            familyBox.setSelectedItem("SansSerif");
            sizeSpinner.setValue(24);
            bold.setSelected(false);
            italic.setSelected(false);
            underline.setSelected(false);
            selectedTextColor[0] = Color.BLACK;
            previewLabel.setForeground(Color.BLACK);
            sideInput.setText("");

            // Restore page content WITHOUT removing the header
            centerContentPanel.removeAll();
            drawGridOnCanvas(pagePanel, settings);

            // Ensure header stays at the top
            if (headerPanel.getParent() == null) {
                pagePanel.add(headerPanel, BorderLayout.NORTH);
            }

            pagePanel.revalidate();
            pagePanel.repaint();
        });


        // layout: add components into content panel
        content.add(familyLabel);
        content.add(familyBox);
        content.add(Box.createVerticalStrut(8));
        content.add(sizeLabel);
        content.add(sizeSpinner);
        content.add(Box.createVerticalStrut(10));

        content.add(bold);
        content.add(italic);
        content.add(underline);
        content.add(Box.createVerticalStrut(8));

        content.add(alignLabel);
        content.add(alignRow);
        content.add(Box.createVerticalStrut(8));

        content.add(inputLabel);
        content.add(sideScroll);
        content.add(Box.createVerticalStrut(10));

        content.add(colorHeader);
        content.add(chooseColorBtn);
        content.add(Box.createVerticalStrut(8));

        content.add(applyBtn);
        content.add(Box.createVerticalStrut(8));
        content.add(resetBtn);
        content.add(Box.createVerticalStrut(12));

        content.add(previewLabel);
        content.add(Box.createVerticalStrut(6));

        outer.add(content, BorderLayout.CENTER);

        // COLLAPSE ACTION
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
    public static JPanel importContentSection(JPanel pagePanel) {
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
                java.io.File f = chooser.getSelectedFile();
                previewLabel.setText("Logo Added: " + f.getName());

                try {
                    ImageIcon icon = new ImageIcon(f.getAbsolutePath());

                    // Determine header height so the logo fits
                    int targetH = 64;
                    if (headerPanel != null && headerPanel.getHeight() > 0) {
                        targetH = Math.max(48, headerPanel.getHeight() - 16);
                    }

                    ImageIcon scaled = resizeToFitHeight(icon.getImage(), targetH);

                    if (scaled != null && headerLogoLabel != null) {
                        headerLogoLabel.setIcon(scaled);
                        headerPanel.revalidate();
                        headerPanel.repaint();
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
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
// APPLY IMPORTED CONTENT TO PAGE
        JButton applyImportBtn =
                styleButton(new JButton("Apply to Page"), new Color(150,165,190));
        applyImportBtn.setForeground(Color.WHITE);
        applyImportBtn.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        fullWidth.accept(applyImportBtn);

// Add button to the layout
        content.add(Box.createVerticalStrut(10));
        content.add(applyImportBtn);
        applyImportBtn.addActionListener(e -> {
            // Clear page center area
            centerContentPanel.removeAll();

            // If pasted text exists → apply text block
            String pasted = pasteArea.getText().trim();
            if (!pasted.isEmpty()) {
                JTextArea textBlock = new JTextArea(pasted);
                textBlock.setLineWrap(true);
                textBlock.setWrapStyleWord(true);
                textBlock.setFont(new Font("SansSerif", Font.PLAIN, 18));
                textBlock.setBorder(new LineBorder(new Color(141,157,177), 2, true));
                textBlock.setBackground(Color.WHITE);

                JScrollPane textScroll = new JScrollPane(textBlock);
                centerContentPanel.add(textScroll, BorderLayout.CENTER);
                refresh(pagePanel);
                return;
            }

            // If ONLY a logo was selected, apply it at top of center area
            if (headerLogoLabel.getIcon() != null) {
                JLabel logoPreview = new JLabel(headerLogoLabel.getIcon());
                logoPreview.setHorizontalAlignment(SwingConstants.CENTER);
                centerContentPanel.add(logoPreview, BorderLayout.NORTH);
                refresh(pagePanel);
                return;
            }

            JOptionPane.showMessageDialog(null, "Nothing to apply yet!");
        });

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

        generateButton.addActionListener(e -> {
            MathGen.Range selectedRange = null;
            if (range20.isSelected()) selectedRange = MathGen.Range.SMALL;
            if (range50.isSelected()) selectedRange = MathGen.Range.MEDIUM;
            if (range100.isSelected()) selectedRange = MathGen.Range.LARGE;

            if (selectedRange == null) {
                JOptionPane.showMessageDialog(null, "Please select a number range");
                return;
            }

            java.util.List<String> opsList = new ArrayList<>();
            if (addOp.isSelected()) opsList.add("+");
            if (subOp.isSelected()) opsList.add("-");
            if (mulOp.isSelected()) opsList.add("*");
            if (divOp.isSelected()) opsList.add("/");

            if (opsList.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please select an operation");
                return;
            }

            int count = (Integer) problemSpinner.getValue();

            // ===============================
            // BUILD THE PAGE PANEL CONTENT
            // ===============================
            JPanel mathPanel = new JPanel();
            mathPanel.setLayout(new BoxLayout(mathPanel, BoxLayout.Y_AXIS));
            mathPanel.setBackground(Color.WHITE);
            mathPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

            for (String op : opsList) {

                JLabel header = new JLabel(op + " Problems");
                header.setFont(new Font("SansSerif", Font.BOLD, 20));
                header.setBorder(BorderFactory.createEmptyBorder(10, 0, 6, 0));
                mathPanel.add(header);

                java.util.List<String> problems =
                        MathGen.generate(op, selectedRange, count);

                for (String p : problems) {
                    JLabel line = new JLabel(p);
                    line.setFont(new Font("Monospaced", Font.PLAIN, 18));
                    line.setBorder(BorderFactory.createEmptyBorder(2, 12, 2, 0));
                    mathPanel.add(line);
                }

                mathPanel.add(Box.createVerticalStrut(20));
            }

            // ===============================
            // APPLY TO THE PAGE (NOT SIDEBAR)
            // ===============================

            centerContentPanel.removeAll();           // Clear old content
            centerContentPanel.add(mathPanel, BorderLayout.CENTER);
            centerContentPanel.revalidate();
            centerContentPanel.repaint();
        });


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
// RESET BUTTON (Calculations) - matches UI
        JButton resetCalcBtn = styleButton(new JButton("Reset Calculations"), new Color(150, 165, 190));
        resetCalcBtn.setForeground(Color.WHITE);
        resetCalcBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        resetCalcBtn.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        resetCalcBtn.setFocusPainted(false);
        resetCalcBtn.setContentAreaFilled(false);
        resetCalcBtn.setOpaque(false);
        fullWidth.accept(resetCalcBtn);

        resetCalcBtn.addActionListener(e -> {
            range20.setSelected(false);
            range50.setSelected(false);
            range100.setSelected(false);

            problemSpinner.setValue(10);

            addOp.setSelected(false);
            subOp.setSelected(false);
            mulOp.setSelected(false);
            divOp.setSelected(false);
        });

        content.add(resetCalcBtn);
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
// fullWidth helper (match other sections)
        java.util.function.Consumer<JComponent> fullWidth = c -> {
            c.setAlignmentX(Component.LEFT_ALIGNMENT);
            c.setMaximumSize(new Dimension(Integer.MAX_VALUE, c.getPreferredSize().height));
        };

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
// RESET BUTTON (Quick Fill) - matches UI
        JButton resetQuickBtn = styleButton(new JButton("Reset Quick Fill"), new Color(150, 165, 190));
        resetQuickBtn.setForeground(Color.WHITE);
        resetQuickBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        resetQuickBtn.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        resetQuickBtn.setFocusPainted(false);
        resetQuickBtn.setContentAreaFilled(false);
        resetQuickBtn.setOpaque(false);

        resetQuickBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        resetQuickBtn.setMaximumSize(new Dimension(180, 40));
        resetQuickBtn.setPreferredSize(new Dimension(180, 40));
        resetQuickBtn.setMinimumSize(new Dimension(180, 40));

        resetQuickBtn.addActionListener(e -> {
            // no persistent state for quick-fill buttons yet —
            // if you later add toggles, clear them here.
            // For now clear any preview or notify user:
            JOptionPane.showMessageDialog(null, "Quick Fill reset.");
        });

        content.add(resetQuickBtn);
        content.add(Box.createVerticalStrut(10));

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
// fullWidth helper (used by other sections)
        java.util.function.Consumer<JComponent> fullWidth = c -> {
            c.setAlignmentX(Component.LEFT_ALIGNMENT);
            c.setMaximumSize(new Dimension(Integer.MAX_VALUE, c.getPreferredSize().height));
        };

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
// RESET BUTTON (Templates) - matches UI
        JButton resetTemplateBtn = styleButton(new JButton("Reset Templates"), new Color(150, 165, 190));
        resetTemplateBtn.setForeground(Color.WHITE);
        resetTemplateBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        resetTemplateBtn.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        resetTemplateBtn.setFocusPainted(false);
        resetTemplateBtn.setContentAreaFilled(false);
        resetTemplateBtn.setOpaque(false);


        resetTemplateBtn.addActionListener(e -> {
            // clear any template selection state here when you add it
            JOptionPane.showMessageDialog(null, "Templates reset.");
        });
        resetTemplateBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        resetTemplateBtn.setMaximumSize(new Dimension(180, 40));
        resetTemplateBtn.setPreferredSize(new Dimension(180, 40));
        resetTemplateBtn.setMinimumSize(new Dimension(180, 40));

        content.add(resetTemplateBtn);
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
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { icon.setOpaque(false); icon.setBackground(hover); }
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
    public static void applyTextColorDeep(Container c, Color textColor) {
        for (Component comp : c.getComponents()) {

            if (comp instanceof JLabel lbl) {
                lbl.setForeground(textColor);

                // HTML support
                String t = lbl.getText();
                if (t != null && t.startsWith("<html>")) {
                    lbl.setText(
                            t.replaceAll("color: rgb\\([^)]*\\)",
                                    "color: rgb(" + textColor.getRed() + "," + textColor.getGreen() + "," + textColor.getBlue() + ")")
                    );
                }
            }

            if (comp instanceof JTextField tf) tf.setForeground(textColor);
            if (comp instanceof JTextArea ta) ta.setForeground(textColor);
            if (comp instanceof JButton btn) btn.setForeground(textColor);

            if (comp instanceof Container child) applyTextColorDeep(child, textColor);
        }
    }
    public static JButton styleButton(JButton btn, Color base) {
        JButton styled = new JButton(btn.getText()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(base);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                super.paintComponent(g);
            }
        };

        styled.setForeground(btn.getForeground());
        styled.setFont(btn.getFont());
        styled.setBorder(btn.getBorder());
        styled.setFocusPainted(false);
        styled.setContentAreaFilled(false);
        styled.setOpaque(false);

        return styled;
    }
// ============================================================
// STEP 4 — HEADER PANEL BUILDER + LOGO RESIZER + HEADER UPDATERS
// ============================================================

    // Build the top header (logo left, name+instructions right)
    public static JPanel buildHeaderPanel(JPanel pagePanel, WorksheetSettings settings) {

        int headerHeight = 90;
        Dimension pageSize = pagePanel.getPreferredSize();
        if (pageSize != null && pageSize.height > 0) {
            headerHeight = Math.max(60, Math.min(140, pageSize.height / 12));
        }

        // ---- HEADER PANEL WITH SOFT GREY BACKGROUND ----
        RoundedPanel header = new RoundedPanel(12) {
            @Override protected void paintComponent(Graphics g) {

                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Soft grey background (makes it stand out from page)
                g2.setColor(new Color(255, 255, 255));  // Slight contrast from white page
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);

                // Soft grey border
                g2.setColor(new Color(7, 7, 7));
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);


            }
        };

        header.setOpaque(true);
        header.setLayout(new BorderLayout(12, 8));
        header.setPreferredSize(new Dimension(0, headerHeight));
        header.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));

        // ------------------ LOGO AREA ------------------
        headerLogoLabel = new JLabel();
        headerLogoLabel.setHorizontalAlignment(SwingConstants.LEFT);
        headerLogoLabel.setVerticalAlignment(SwingConstants.CENTER);
        headerLogoLabel.setPreferredSize(new Dimension(headerHeight - 20, headerHeight - 20));

        JPanel logoWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        logoWrap.setOpaque(false);
        logoWrap.add(headerLogoLabel);
        header.add(logoWrap, BorderLayout.WEST);

        // ------------------ TEXT AREA ------------------
        JPanel textWrap = new JPanel();
        textWrap.setOpaque(false);
        textWrap.setLayout(new BoxLayout(textWrap, BoxLayout.Y_AXIS));

        headerNameLabel = new JLabel("Name:");
        headerNameLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        headerNameLabel.setForeground(Color.DARK_GRAY);
        headerNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        headerInstructionsLabel = new JLabel("<html><i>Instructions appear here</i></html>");
        headerInstructionsLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        headerInstructionsLabel.setForeground(Color.DARK_GRAY);
        headerInstructionsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        textWrap.add(headerNameLabel);
        textWrap.add(Box.createVerticalStrut(6));
        textWrap.add(headerInstructionsLabel);

        header.add(textWrap, BorderLayout.CENTER);

        return header;
    }


    // Resize an image to a specific target height (keeps aspect ratio)
    public static ImageIcon resizeToFitHeight(Image img, int targetHeight) {
        if (img == null) return null;
        int h = img.getHeight(null);
        int w = img.getWidth(null);
        if (h <= 0 || w <= 0) return null;
        double scale = (double) targetHeight / h;
        int newW = (int)(w * scale);
        int newH = (int)(h * scale);
        Image scaled = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    // Update header text when student details change
    public static void updateHeaderText(String name, String instructions) {
        if (headerNameLabel != null)
            headerNameLabel.setText("Name: " + (name == null ? "" : name));

        if (headerInstructionsLabel != null) {
            String html = instructions == null ? "" : instructions.replace("\n", "<br>");
            headerInstructionsLabel.setText("<html>" + html + "</html>");
        }
    }
    private static void refresh(JPanel pagePanel) {
        centerContentPanel.revalidate();
        centerContentPanel.repaint();
        pagePanel.revalidate();
        pagePanel.repaint();
    }
    public static void applyAlignmentToEditor(int align) {
        if (centerContentPanel.getComponentCount() == 0) return;

        Component comp = centerContentPanel.getComponent(0);
        if (!(comp instanceof JScrollPane scroll)) return;

        JTextPane editor = (JTextPane) scroll.getViewport().getView();
        StyledDocument doc = editor.getStyledDocument();

        SimpleAttributeSet attrs = new SimpleAttributeSet();
        StyleConstants.setAlignment(attrs, align);

        doc.setParagraphAttributes(
                editor.getSelectionStart(),
                editor.getSelectionEnd() - editor.getSelectionStart(),
                attrs,
                false
        );
    }

}
