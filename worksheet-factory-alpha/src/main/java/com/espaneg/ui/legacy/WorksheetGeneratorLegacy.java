package com.espaneg.ui.legacy;

import com.espaneg.logic.MathGen;
import com.espaneg.logic.QuickFill;
import com.espaneg.model.WorksheetSettings;
import com.espaneg.services.PdfService;
import com.espaneg.ui.HomePage;
import com.espaneg.utils.ResourceLoader;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentListener;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.awt.print.Printable;
import java.awt.print.PrinterJob;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

//import static jdk.javadoc.internal.doclets.formats.html.markup.HtmlStyles.header;

public class WorksheetGeneratorLegacy {

    public WorksheetGeneratorLegacy() {
        // Ensures settings & page list initialized BEFORE UI build
        settings = new WorksheetSettings("Default", "", 20);
        pagePanels = new ArrayList<>();
    }
    public static void launch() {
        SwingUtilities.invokeLater(() -> {
            WorksheetGeneratorLegacy legacy = new WorksheetGeneratorLegacy();
            legacy.createAndShowUI();    // must be instance method
        });
    }

    // existing variables + constructor follow here…


    // Helper: reset pages to single blank first page before rendering new content
    private static void resetPagesForRender() {
        try {
            if (pagePanels == null || pagePanels.size() == 0) return;
            // remove all pages except first
            for (int i = pagePanels.size() - 1; i > 0; i--) {
                pageContainer.remove(pagePanels.get(i));
                pagePanels.remove(i);
            }
            // clear first page content
            JPanel firstPage = pagePanels.get(0);
            JPanel contentPanel = getContentPanelFromPage(firstPage);
            if (contentPanel != null) {
                contentPanel.removeAll();
                contentPanel.revalidate();
                contentPanel.repaint();
                centerContentPanel = contentPanel;
            }
            activePageIndex = 0;
            pagePanel = pagePanels.get(0);
            pageContainer.revalidate();
            pageContainer.repaint();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    // Page state
    static boolean fitToWidth = false;
    static boolean showMargins = true;

    static int marginTop = 50;
    static int marginBottom = 50;
    static int marginLeft = 50;
    static int marginRight = 50;
    static JPanel headerPanel;
    static JPanel centerContentPanel; // holds grid / editor / templates etc.
    static JLabel headerLogoLabel;
    static JLabel headerNameLabel;
    static JLabel headerInstructionsLabel;
    static JPanel pagePanel;
    static JComboBox<String> fontSectionComboFamily;
    static JSpinner fontSectionSizeSpinner;
    static JCheckBox fontSectionBold;
    static JCheckBox fontSectionItalic;
    static JCheckBox fontSectionUnderline;
    static Color selectedFontColor = Color.BLACK;
    static int selectedAlignment = StyleConstants.ALIGN_LEFT;
    static JTextArea fontSideInput;
    static JPanel pageContainer;
    static java.util.List<JPanel> pagePanels = new ArrayList<>();
    static int activePageIndex = 0;
    static WorksheetSettings settings = new WorksheetSettings("Default", "", 20);
    static double pageScale = 1.0;
    static JScrollPane canvasScroll;
    // GRID settings (persisted)
    static int gridRows = 5;
    static int gridCols = 2;
    static int boxesPerPage = gridRows * gridCols;
    static boolean gridAutoScaleText = true; // try to autoscale label font to fit cell
    // Page selection mode for grid re-apply
    enum PageApplyMode { CURRENT, ALL, RANGE }
    static PageApplyMode gridApplyMode = PageApplyMode.CURRENT;
    static int gridApplyFrom = 1;
    static int gridApplyTo = 1;
    static String[] lastRenderedItems;

    // -----------------------------
// THEME & UI HELPERS (paste once)
// -----------------------------
    private static final Color HEADER_TOP = new Color(120, 140, 170);
    private static final Color HEADER_BOTTOM = new Color(90, 110, 140);
    private static final Color BTN_TOP = new Color(150, 165, 190);
    private static final Color BTN_BOTTOM = new Color(110, 125, 155);


    // Make component full width in sidebar (consistently)
    private static void makeFullWidth(JComponent c) {
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, c.getPreferredSize().height));
    }

    // Create a consistent header bar for sections (returns header panel and arrow label in a small holder)
    private static JPanel createSectionHeader(String title, JLabel arrowOut) {
        JPanel headerBar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, HEADER_TOP, getWidth(), getHeight(), HEADER_BOTTOM);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                super.paintComponent(g);
            }
        };
        headerBar.setOpaque(false);
        headerBar.setLayout(new BorderLayout());
        headerBar.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("SansSerif", Font.BOLD, 15));
        titleLbl.setForeground(Color.WHITE);
        arrowOut.setFont(new Font("SansSerif", Font.BOLD, 16));
        arrowOut.setForeground(Color.WHITE);
        headerBar.add(titleLbl, BorderLayout.WEST);
        headerBar.add(arrowOut, BorderLayout.EAST);
        return headerBar;
    }

    // Universal apply executor: given a Runnable that applies the setting to a single page, it will run it for current/all/range
    private static void applyToPagesExecutor(Runnable applyToSinglePage, PageApplyMode mode, int from, int to) {
        if (pagePanels.isEmpty()) return;
        switch (mode) {
            case CURRENT -> {
                applyToSinglePage.run();
            }
            case ALL -> {
                for (int i = 0; i < pagePanels.size(); i++) {
                    activePageIndex = i;
                    applyToSinglePage.run();
                }
            }
            case RANGE -> {
                int start = Math.max(1, from);
                int end = Math.min(pagePanels.size(), to);
                for (int i = start - 1; i <= end - 1; i++) {
                    activePageIndex = i;
                    applyToSinglePage.run();
                }
            }
        }
    }

    // --------------------------------------------------
// UNIVERSAL STYLED BUTTON (matches Import Content UI)
// --------------------------------------------------
    private static JButton styledButton(String text) {

        Color grad1 = new Color(150, 165, 190);
        Color grad2 = new Color(110, 125, 155);

        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gp =
                        new GradientPaint(0, 0, grad1, getWidth(), getHeight(), grad2);

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

        return btn;
    }

    //-----------------------------------------------------------
// UNIVERSAL PAGE TARGET RESOLVER
//-----------------------------------------------------------
    public static java.util.List<JPanel> getTargetPages() {

        // CURRENT page only
        if (gridApplyMode == PageApplyMode.CURRENT) {
            return java.util.List.of(pagePanels.get(activePageIndex));
        }

        // ALL pages
        if (gridApplyMode == PageApplyMode.ALL) {
            return pagePanels;
        }

        // RANGE: from X to Y
        if (gridApplyMode == PageApplyMode.RANGE) {
            java.util.List<JPanel> rangePages = new ArrayList<>();
            for (int i = gridApplyFrom - 1; i <= gridApplyTo - 1; i++) {
                if (i >= 0 && i < pagePanels.size()) {
                    rangePages.add(pagePanels.get(i));
                }
            }
            return rangePages;
        }

        // fallback (should not happen)
        return java.util.List.of(pagePanels.get(activePageIndex));
    }

    public enum ViewMode {LIST, GRID, MATCH}

    static ViewMode selectedViewMode = ViewMode.LIST;
    static boolean portrait = true;
    // Global undo/redo stacks
    public static java.util.Stack<byte[]> undoStack = new java.util.Stack<>();
    public static java.util.Stack<byte[]> redoStack = new java.util.Stack<>();
    // Save full snapshot of centerContentPanel
    public static void pushUndoState() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(centerContentPanel);
            out.flush();

            byte[] snapshot = bos.toByteArray();
            undoStack.push(snapshot);

            // Clear redo whenever new action occurs
            redoStack.clear();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }



    private void createAndShowUI() {
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
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                frame.dispose();       // close WorksheetGenerator window
              new HomePage();        // open HomePage
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
        JPanel leftContent = new JPanel() {
            @Override
            public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();

                // 🔥 Fix scrollbar shrink — match viewport width
                Container parent = getParent();
                if (parent instanceof JViewport viewport) {
                    d.width = viewport.getWidth();
                }

                return d;
            }
        };
        leftContent.setLayout(new BoxLayout(leftContent, BoxLayout.Y_AXIS));
        leftContent.setOpaque(false);

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
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
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
        RoundedButton addPageBtn = new RoundedButton("New Page");
        addPageBtn.setPreferredSize(new Dimension(120, 40));

        addPageBtn.addActionListener(e -> addNewPage());

        topRightButtons.add(addPageBtn);

        RoundedButton exportButton = new RoundedButton("Export & Share");
        exportButton.setPreferredSize(new Dimension(160, 40));
        RoundedButton moreButton = new RoundedButton("⋮");
        moreButton.setFont(new Font("SansSerif", Font.BOLD, 20));
        moreButton.setPreferredSize(new Dimension(48, 40));
        RoundedButton printButton = new RoundedButton("Print");
        printButton.setPreferredSize(new Dimension(120, 40));

        exportButton.addActionListener(e -> {
            try {
                JFileChooser chooser = new JFileChooser();
                chooser.setDialogTitle("Save Worksheet as PDF");
                chooser.setSelectedFile(new java.io.File("worksheet.pdf"));

                if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {

                    String outPath = chooser.getSelectedFile().getAbsolutePath();
                    PdfService.startPDF(outPath);

                    for (JPanel page : pagePanels) {
                        PdfService.exportPanelPage(page);
                    }

                    PdfService.finishPDF(outPath);

                    JOptionPane.showMessageDialog(null,
                            "PDF exported successfully!",
                            "Export Complete",
                            JOptionPane.INFORMATION_MESSAGE);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        printButton.addActionListener(e -> {
            try {
                PrinterJob job = PrinterJob.getPrinterJob();
                job.setJobName("EduCreate Worksheet");

                job.setPrintable((graphics, pageFormat, pageIndex) -> {

                    // Stop after last page
                    if (pageIndex >= pagePanels.size())
                        return Printable.NO_SUCH_PAGE;

                    JPanel page = pagePanels.get(pageIndex);
                    Graphics2D g2 = (Graphics2D) graphics;

                    double scaleX = pageFormat.getImageableWidth() / page.getWidth();
                    double scaleY = pageFormat.getImageableHeight() / page.getHeight();
                    double scale = Math.min(scaleX, scaleY);

                    g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
                    g2.scale(scale, scale);

                    page.print(g2);

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
        // Scroll pane that holds the pagePanel
        // Center wrapper for responsive canvas
        JPanel canvasWrapper = new JPanel(new GridBagLayout());
        canvasWrapper.setOpaque(false);
        pageContainer = new JPanel();
        pageContainer.setLayout(new BoxLayout(pageContainer, BoxLayout.X_AXIS));
        pageContainer.setOpaque(false);

        canvasWrapper.add(pageContainer);


// Scroll pane
        canvasScroll = new JScrollPane(canvasWrapper);
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
// INITIALIZE FIRST PAGE  (STEP 4)
// ============================================================
        addNewPage();   // creates Page 1 and sets it active
        SwingUtilities.invokeLater(() ->
                autoScalePage(pagePanels.get(0), canvasScroll)
        );

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
// TOOLBAR ACTIONS - UNIVERSAL UNDO/REDO
// ---------------------------------------------------------

// Store snapshots of the entire centerContentPanel
//        final java.util.Stack<Component> undoStack = new java.util.Stack<>();
//        final java.util.Stack<Component> redoStack = new java.util.Stack<>();

// Helper method to clone a component
        java.util.function.Function<Component, Component> cloneComponent = (comp) -> {
            if (comp instanceof JScrollPane scroll) {
                Component view = scroll.getViewport().getView();
                if (view instanceof JTextPane pane) {
                    JTextPane newPane = new JTextPane();
                    newPane.setDocument(pane.getStyledDocument());
                    newPane.setText(pane.getText());
                    newPane.setEditable(pane.isEditable());
                    newPane.setBackground(pane.getBackground());
                    newPane.setForeground(pane.getForeground());
                    newPane.setFont(pane.getFont());

                    JScrollPane newScroll = new JScrollPane(newPane);
                    newScroll.setBorder(scroll.getBorder());
                    newScroll.setPreferredSize(scroll.getPreferredSize());
                    return newScroll;
                }
            }
            return null;
        };

// Save current state
        Runnable saveState = () -> {
            if (centerContentPanel.getComponentCount() > 0) {
                Component current = centerContentPanel.getComponent(0);
                Component clone = cloneComponent.apply(current);
                if (clone != null) {
//                    undoStack.push(clone);
                    redoStack.clear();
                    System.out.println("✓ State saved. Undo stack: " + undoStack.size());
                }
            }
        };

// UNDO
        undoBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (undoStack.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Nothing to undo!");
                    return;
                }

                try {
                    byte[] data = undoStack.pop();

                    // Save current state into redo
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    ObjectOutputStream out = new ObjectOutputStream(bos);
                    out.writeObject(centerContentPanel);
                    redoStack.push(bos.toByteArray());

                    ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(data));
                    JPanel restored = (JPanel) in.readObject();

                    centerContentPanel.removeAll();
                    centerContentPanel.add(restored);
                    centerContentPanel.revalidate();
                    centerContentPanel.repaint();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });


// REDO
        redoBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                System.out.println("REDO clicked. Stack size: " + redoStack.size());

                if (!redoStack.isEmpty()) {
                    // Save current state to undo
                    if (centerContentPanel.getComponentCount() > 0) {
                        Component current = centerContentPanel.getComponent(0);
                        Component clone = cloneComponent.apply(current);
                        if (clone != null) {
//                            undoStack.push(clone);
                        }
                    }

                    // Restore next state
//                    Component next = redoStack.pop();
//                    centerContentPanel.removeAll();
//                    centerContentPanel.add(next, BorderLayout.CENTER);
//                    centerContentPanel.revalidate();
//                    centerContentPanel.repaint();

                    System.out.println("✓ REDO successful");
                } else {
                    System.out.println("Nothing to redo");
                    JOptionPane.showMessageDialog(null, "Nothing to redo!");
                }
            }
        });

// Auto-save state when content changes
        centerContentPanel.addContainerListener(new java.awt.event.ContainerAdapter() {
            @Override
            public void componentAdded(java.awt.event.ContainerEvent e) {
                // Wait a bit then save state
                javax.swing.Timer timer = new javax.swing.Timer(500, ev -> saveState.run());
                timer.setRepeats(false);
                timer.start();
            }
        });
// ALIGN LEFT
        alignLeftBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                selectedAlignment = StyleConstants.ALIGN_LEFT;
                pushUndoState();

                // Re-render using new alignment
                if (lastRenderedItems != null) {
                    resetPagesForRender();
                    renderItemsToPages(lastRenderedItems);
                }
            }
        });


// ALIGN CENTER
        alignCenterBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                selectedAlignment = StyleConstants.ALIGN_CENTER;
                pushUndoState();

                if (lastRenderedItems != null) {
                    resetPagesForRender();
                    renderItemsToPages(lastRenderedItems);
                }
            }
        });


// ALIGN RIGHT
        alignRightBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                selectedAlignment = StyleConstants.ALIGN_RIGHT;
                pushUndoState();

                if (lastRenderedItems != null) {
                    resetPagesForRender();
                    renderItemsToPages(lastRenderedItems);
                }
            }
        });


// ARROWS – scroll canvas left/right
        // === PAGE SWITCHING WITH LEFT/RIGHT ARROWS ===
        leftArrowBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {

                if (pagePanels.isEmpty()) return;

                // go to previous page if exists
                int newIndex = activePageIndex - 1;
                if (newIndex >= 0) {
                    activatePage(newIndex);
                } else {
                    // OPTIONAL: wrap-around to last page
                    // activatePage(pagePanels.size() - 1);
                }
            }
        });

        rightArrowBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {

                if (pagePanels.isEmpty()) return;

                // go to next page if exists
                int newIndex = activePageIndex + 1;
                if (newIndex < pagePanels.size()) {
                    activatePage(newIndex);
                } else {
                    // OPTIONAL: wrap-around to first page
                    // activatePage(0);
                }
            }
        });


        toolbar.add(iconGroup, BorderLayout.WEST);

        JPanel rightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        rightButtons.setOpaque(false);
        RoundedButton autosaveBtn = new RoundedButton("AutoSave");
        RoundedButton zoomBtn = new RoundedButton(" Zoom ");
        autosaveBtn.setPreferredSize(new Dimension(140, 36));
        zoomBtn.setPreferredSize(new Dimension(100, 36));

// ZOOM FUNCTIONALITY - Only zooms the canvas
        zoomBtn.addActionListener(e -> {
            String[] zoomOptions = {"50%", "75%", "100%", "125%", "150%", "200%", "250%", "300%"};
            String selected = (String) JOptionPane.showInputDialog(
                    frame,
                    "Select zoom level for the canvas:",
                    "Zoom Canvas",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    zoomOptions,
                    "100%"
            );

            if (selected != null) {
                try {
                    double zoomFactor = Double.parseDouble(selected.replace("%", "")) / 100.0;

                    int baseWidth = 1000;
                    int baseHeight = 1400;

                    int newWidth = (int) (baseWidth * zoomFactor);
                    int newHeight = (int) (baseHeight * zoomFactor);

                    JPanel page = pagePanels.get(activePageIndex);

                    page.setPreferredSize(new Dimension(newWidth, newHeight));
                    page.revalidate();
                    page.repaint();

                    canvasScroll.revalidate();

                    JOptionPane.showMessageDialog(frame,
                            "Canvas zoomed to " + selected,
                            "Zoom Applied",
                            JOptionPane.INFORMATION_MESSAGE);

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame,
                            "Invalid zoom level",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }

        });

        rightButtons.add(autosaveBtn);
        rightButtons.add(zoomBtn);
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
        leftContent.add(createStudentDetailsSection());
        leftContent.add(Box.createVerticalStrut(8));

        leftContent.add(gridSection());
        leftContent.add(Box.createVerticalStrut(8));

        leftContent.add(pageSizeSection(canvasWrapper, canvasScroll));

        leftContent.add(Box.createVerticalStrut(8));

        leftContent.add(fontSection());
        leftContent.add(Box.createVerticalStrut(8));

        leftContent.add(importContentSection());
        leftContent.add(Box.createVerticalStrut(8));

        leftContent.add(Box.createVerticalStrut(8));
//        leftContent.add(colorPaletteSection(pagePanel, renderPanel, null, settings));
        leftContent.add(Box.createVerticalStrut(8));
        leftContent.add(calculationsSection());
        leftContent.add(Box.createVerticalStrut(8));
        leftContent.add(quickFillSection());
        leftContent.add(Box.createVerticalStrut(8));
        leftContent.add(templateSection());
        leftContent.add(Box.createVerticalGlue());
// =====================================================================
// UNIVERSAL APPLY SETTINGS SECTION (MATCHES UI STYLE)
// =====================================================================
        RoundedPanel applyUniversalSection = new RoundedPanel(25);
        applyUniversalSection.setOpaque(false);
        applyUniversalSection.setLayout(new BorderLayout());
        applyUniversalSection.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        applyUniversalSection.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));

// ---------- HEADER (same as other sections) ----------
        JPanel applyHeader = new JPanel() {
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
        applyHeader.setOpaque(false);
        applyHeader.setLayout(new BorderLayout());
        applyHeader.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        JLabel applyTitle = new JLabel("Apply Settings To");
        applyTitle.setFont(new Font("SansSerif", Font.BOLD, 15));
        applyTitle.setForeground(Color.WHITE);

        JLabel applyArrow = new JLabel("▼");
        applyArrow.setFont(new Font("SansSerif", Font.BOLD, 16));
        applyArrow.setForeground(Color.WHITE);

        applyHeader.add(applyTitle, BorderLayout.WEST);
        applyHeader.add(applyArrow, BorderLayout.EAST);

        applyUniversalSection.add(applyHeader, BorderLayout.NORTH);

// ---------- CONTENT ----------
        JPanel applyContent = new JPanel();
        applyContent.setOpaque(false);
        applyContent.setLayout(new BoxLayout(applyContent, BoxLayout.Y_AXIS));
        applyContent.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        applyContent.setVisible(false);

        java.util.function.Consumer<JComponent> fullWidthApply = c -> {
            c.setAlignmentX(Component.LEFT_ALIGNMENT);
            c.setMaximumSize(new Dimension(Integer.MAX_VALUE, c.getPreferredSize().height));
        };

// radio buttons
        JRadioButton applyCurrent = new JRadioButton("Current Page");
        JRadioButton applyAll = new JRadioButton("All Pages");
        JRadioButton applyRange = new JRadioButton("Page Range:");

        for (JRadioButton r : new JRadioButton[]{applyCurrent, applyAll, applyRange}) {
            r.setOpaque(false);
            r.setFont(new Font("SansSerif", Font.PLAIN, 12));
            fullWidthApply.accept(r);
        }

        applyCurrent.setSelected(true);

        ButtonGroup grp = new ButtonGroup();
        grp.add(applyCurrent);
        grp.add(applyAll);
        grp.add(applyRange);

// range fields
        JPanel rangePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        rangePanel.setOpaque(false);

        JSpinner fromSpin = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        fromSpin.setPreferredSize(new Dimension(60, 24));

        JSpinner toSpin = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        toSpin.setPreferredSize(new Dimension(60, 24));

        rangePanel.add(new JLabel("From:"));
        rangePanel.add(fromSpin);
        rangePanel.add(new JLabel("To:"));
        rangePanel.add(toSpin);

        fullWidthApply.accept(rangePanel);

// Apply button (styled)
        JButton btnApplyAll = new JButton("Apply Settings") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(150, 165, 190),
                        getWidth(), getHeight(), new Color(110, 125, 155)
                );
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                super.paintComponent(g);
            }
        };
        btnApplyAll.setOpaque(false);
        btnApplyAll.setContentAreaFilled(false);
        btnApplyAll.setBorderPainted(false);
        btnApplyAll.setForeground(Color.WHITE);
        btnApplyAll.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnApplyAll.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        fullWidthApply.accept(btnApplyAll);
// ---------- APPLY SETTINGS LOGIC ----------
        btnApplyAll.addActionListener(e -> {

            // Set global apply mode
            if (applyCurrent.isSelected()) {
                gridApplyMode = PageApplyMode.CURRENT;
            }
            else if (applyAll.isSelected()) {
                gridApplyMode = PageApplyMode.ALL;
            }
            else if (applyRange.isSelected()) {
                gridApplyMode = PageApplyMode.RANGE;

                try {
                    gridApplyFrom = (int) fromSpin.getValue();
                    gridApplyTo   = (int) toSpin.getValue();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame,
                            "Invalid page range.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            // Confirmation popup
            String msg =
                    applyCurrent.isSelected() ? "Settings will apply to the current page."
                            : applyAll.isSelected()   ? "Settings will apply to ALL pages."
                            : "Settings will apply to pages " + gridApplyFrom + " to " + gridApplyTo + ".";

            JOptionPane.showMessageDialog(frame, msg, "Apply Mode Updated", JOptionPane.INFORMATION_MESSAGE);
        });

// assemble
        applyContent.add(applyCurrent);
        applyContent.add(applyAll);
        applyContent.add(applyRange);
        applyContent.add(rangePanel);
        applyContent.add(Box.createVerticalStrut(10));
        applyContent.add(btnApplyAll);

        applyUniversalSection.add(applyContent, BorderLayout.CENTER);

// toggle collapse
        applyHeader.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        applyHeader.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                boolean vis = applyContent.isVisible();
                applyContent.setVisible(!vis);
                applyArrow.setText(vis ? "▼" : "▲");
                applyUniversalSection.revalidate();
            }
        });

// add to sidebar
        leftContent.add(Box.createVerticalStrut(12));
        leftContent.add(applyUniversalSection);

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
    public static JPanel createStudentDetailsSection() {


        RoundedPanel sectionPanel = new RoundedPanel(25);
        sectionPanel.setLayout(new BorderLayout());
        sectionPanel.setOpaque(false);
        sectionPanel.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        sectionPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 280));

        JPanel headerBar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
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
            pagePanels.get(activePageIndex).revalidate();
            pagePanels.get(activePageIndex).repaint();

        };

        nameField.getDocument().addDocumentListener(simpleListener(refresh));
        instructionsArea.getDocument().addDocumentListener(simpleListener(refresh));

        return sectionPanel;
    }

    // ============================================================
// PAGE SIZE SECTION (A4, Letter, Legal, A5, Custom)
// ============================================================
    public static JPanel pageSizeSection(JPanel canvasWrapper, JScrollPane canvasScroll) {

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
        Color grad1 = new Color(150, 165, 190);
        Color grad2 = new Color(110, 125, 155);

        JButton applyBtn = new JButton("Apply Page Size") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, grad1, getWidth(), getHeight(), grad2);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                super.paintComponent(g);
            }
        };
        applyBtn.setOpaque(false);
        applyBtn.setContentAreaFilled(false);
        applyBtn.setBorderPainted(false);
        applyBtn.setFocusPainted(false);
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
            JPanel page = pagePanels.get(activePageIndex);
            page.setPreferredSize(new Dimension(width, height));
            updateCanvasLayout(page, canvasWrapper, canvasScroll);
// Auto-scale so the full page fits the view again
            autoScalePage(page, canvasScroll);


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

                boolean open = content.isVisible();
                content.setVisible(!open);
                arrow.setText(open ? "▼" : "▲");

                // 🔥 CRITICAL FIX — LOCK WIDTH, ADJUST ONLY HEIGHT
                int headerHeight = headerBar.getPreferredSize().height;
                int contentHeight = content.getPreferredSize().height;

                int newHeight = headerHeight + (open ? 0 : contentHeight);

                outer.setMaximumSize(new Dimension(Integer.MAX_VALUE, newHeight));
                outer.revalidate();
                outer.repaint();
            }
        });


        return outer;
    }

    // ===========================
// GRID SECTION (View Mode + Grid Settings)
// ===========================
    public static JPanel gridSection() {

        RoundedPanel outer = new RoundedPanel(25);
        outer.setOpaque(false);
        outer.setLayout(new BorderLayout());
        outer.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        outer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 360));

        // ---------- HEADER ----------
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

        JLabel headerLabel = new JLabel("View Mode");
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        headerLabel.setForeground(Color.WHITE);

        JLabel arrow = new JLabel("▼");
        arrow.setFont(new Font("SansSerif", Font.BOLD, 16));
        arrow.setForeground(Color.WHITE);

        headerBar.add(headerLabel, BorderLayout.WEST);
        headerBar.add(arrow, BorderLayout.EAST);
        outer.add(headerBar, BorderLayout.NORTH);

        // ---------- CONTENT PANEL ----------
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setVisible(false);
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        java.util.function.Consumer<JComponent> fullWidth = c -> {
            c.setAlignmentX(Component.LEFT_ALIGNMENT);
            c.setMaximumSize(new Dimension(Integer.MAX_VALUE, c.getPreferredSize().height));
        };

        // ---------- VIEW MODE ----------
        JLabel viewLabel = new JLabel("Choose display mode for Calculations & QuickFill:");
        viewLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        fullWidth.accept(viewLabel);

        JRadioButton listView = new JRadioButton("List View (Dividing Lines)");
        JRadioButton gridView = new JRadioButton("Grid View (Boxes)");
        JRadioButton matchView = new JRadioButton("Match View (Left Words / Right Images)");

        for (JRadioButton rb : new JRadioButton[]{listView, gridView, matchView}) {
            rb.setOpaque(false);
            rb.setFont(new Font("SansSerif", Font.PLAIN, 12));
            fullWidth.accept(rb);
        }

        ButtonGroup group = new ButtonGroup();
        group.add(listView);
        group.add(gridView);
        group.add(matchView);

        listView.setSelected(true);

        listView.addActionListener(e -> selectedViewMode = ViewMode.LIST);
        gridView.addActionListener(e -> selectedViewMode = ViewMode.GRID);
        matchView.addActionListener(e -> selectedViewMode = ViewMode.MATCH);

        // ---------- GRID SETTINGS ----------
        JPanel gridControls = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        gridControls.setOpaque(false);
        gridControls.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        JLabel rowsLabel = new JLabel("Rows:");
        JSpinner rowsSpinner = new JSpinner(new SpinnerNumberModel(gridRows, 1, 10, 1));
        rowsSpinner.setPreferredSize(new Dimension(60, 24));

        JLabel colsLabel = new JLabel("Cols:");
        JSpinner colsSpinner = new JSpinner(new SpinnerNumberModel(gridCols, 1, 8, 1));
        colsSpinner.setPreferredSize(new Dimension(60, 24));

        JLabel perPageLabel = new JLabel("Boxes / page:");
        JSpinner perPageSpinner = new JSpinner(new SpinnerNumberModel(boxesPerPage, 1, 100, 1));
        perPageSpinner.setPreferredSize(new Dimension(80, 24));

        gridControls.add(rowsLabel);
        gridControls.add(rowsSpinner);
        gridControls.add(colsLabel);
        gridControls.add(colsSpinner);
        gridControls.add(perPageLabel);
        gridControls.add(perPageSpinner);

        fullWidth.accept(gridControls);

        // ---------- APPLY BUTTON ----------
        JButton applyBtn = styledButton("Apply View Mode");
        fullWidth.accept(applyBtn);

        applyBtn.addActionListener(e -> {
            gridRows = (Integer) rowsSpinner.getValue();
            gridCols = (Integer) colsSpinner.getValue();
            boxesPerPage = (Integer) perPageSpinner.getValue();

            JOptionPane.showMessageDialog(null,
                    "View mode set to: " + selectedViewMode +
                            "\nGrid: " + gridRows + " x " + gridCols +
                            "\nBoxes per page: " + boxesPerPage);

            if (pagePanels.size() > 0) {
                JPanel p = pagePanels.get(activePageIndex);
                p.revalidate();
                p.repaint();
            }
        });

        // ---------- LAYOUT ORDER ----------
        content.add(viewLabel);
        content.add(Box.createVerticalStrut(8));
        content.add(listView);
        content.add(gridView);
        content.add(matchView);
        content.add(Box.createVerticalStrut(12));
        content.add(gridControls);
        content.add(Box.createVerticalStrut(12));
        content.add(applyBtn);

        outer.add(content, BorderLayout.CENTER);

        // ---------- COLLAPSE LOGIC ----------
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
    // FONT SECTION
    // ===========================
    public static JPanel fontSection() {

        RoundedPanel outer = new RoundedPanel(25);
        outer.setOpaque(false);
        outer.setLayout(new BorderLayout());
        outer.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        outer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 500));

        // ---------- HEADER ----------
        JPanel headerBar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
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
        // =========================================
// SAVE FONT CONTROLS GLOBALLY (STEP 2)
// =========================================
        fontSectionComboFamily = familyBox;
        fontSectionSizeSpinner = sizeSpinner;
        fontSectionBold = bold;
        fontSectionItalic = italic;
        fontSectionUnderline = underline;
        selectedFontColor = Color.BLACK;


        // --- ALIGNMENT BUTTONS (preview + apply) ---
        JLabel alignLabel = new JLabel("Alignment:");
        alignLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        fullWidth.accept(alignLabel);

        JPanel alignRow = new JPanel();
        alignRow.setLayout(new BoxLayout(alignRow, BoxLayout.X_AXIS));
        alignRow.setOpaque(false);

        alignRow.setOpaque(false);

        JButton alignLeft = styleButton(new JButton("Left"), new Color(150, 165, 190));
        JButton alignCenter = styleButton(new JButton("Center"), new Color(150, 165, 190));
        JButton alignRight = styleButton(new JButton("Right"), new Color(150, 165, 190));
        alignLeft.setPreferredSize(new Dimension(76, 28));
        alignCenter.setPreferredSize(new Dimension(76, 28));
        alignRight.setPreferredSize(new Dimension(76, 28));

        alignRow.add(alignLeft);
        alignRow.add(alignCenter);
        alignRow.add(alignRight);
        fullWidth.accept(alignRow);

        alignLeft.addActionListener(e -> {
            selectedAlignment = StyleConstants.ALIGN_LEFT;
            alignLeft.setEnabled(false);
            alignCenter.setEnabled(true);
            alignRight.setEnabled(true);
            applyAlignmentToEditor(selectedAlignment); // <--- ensure current page updates
        });


        alignCenter.addActionListener(e -> {
            selectedAlignment = StyleConstants.ALIGN_CENTER;
            alignLeft.setEnabled(false);
            alignCenter.setEnabled(true);
            alignRight.setEnabled(true);
            applyAlignmentToEditor(selectedAlignment); // <--- ensure current page updates
        });


        alignRight.addActionListener(e -> {
            selectedAlignment = StyleConstants.ALIGN_RIGHT;
            alignLeft.setEnabled(false);
            alignCenter.setEnabled(true);
            alignRight.setEnabled(true);
            applyAlignmentToEditor(selectedAlignment); // <--- ensure current page updates
        });


        // --- TEXT INPUT (the side panel input you type into) ---
        JLabel inputLabel = new JLabel("Type text for the page (this will replace page content):");
        inputLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        fullWidth.accept(inputLabel);

        fontSideInput = new JTextArea(4, 24);
        JTextArea sideInput = fontSideInput; // keep your original name usable
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
                selectedFontColor = chosen;
            }
        });

        // APPLY button: creates a full-page JTextPane inside pagePanel
        JButton applyBtn = styleButton(new JButton("Apply to Page"), new Color(186, 210, 241));
        applyBtn.setForeground(Color.WHITE);
        applyBtn.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        fullWidth.accept(applyBtn);

        applyBtn.addActionListener(e -> {
            pushUndoState();

            String text = sideInput.getText();
            if (text == null || text.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter text first.");
                return;
            }

            // Clean + split lines
            String[] lines = text.split("\n");
            java.util.List<String> cleaned = new ArrayList<>();
            for (String s : lines) if (!s.trim().isEmpty()) cleaned.add(s);

            // Set global font/color flags before rendering
            Font f = getSelectedFont();
            Color c = selectedFontColor;
            boolean underlineFlag = fontSectionUnderline.isSelected();

            // Render items to pages using the current view mode — this method now updates pages correctly
            resetPagesForRender();
            renderItemsToPages(cleaned.toArray(new String[0]));

            // After rendering, ensure active page scaled header/preview etc.
            SwingUtilities.invokeLater(() -> {
                // keep header stable
                JPanel page = pagePanels.get(activePageIndex);
                autoScalePage(page, canvasScroll);
            });

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
            //Restore a blank content area
            centerContentPanel.removeAll();
            centerContentPanel.revalidate();
            centerContentPanel.repaint();


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
    // IMPORT CONTENT SECTION
    // ===========================
    public static JPanel importContentSection() {
        RoundedPanel outer = new RoundedPanel(25);
        outer.setOpaque(false);
        outer.setLayout(new BorderLayout());
        outer.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        outer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 520));

        JPanel headerBar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
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
                @Override
                protected void paintComponent(Graphics g) {
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
            pushUndoState();

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
                styleButton(new JButton("Apply to Page"), new Color(150, 165, 190));
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
                textBlock.setBorder(new LineBorder(new Color(141, 157, 177), 2, true));
                textBlock.setBackground(Color.WHITE);

                JScrollPane textScroll = new JScrollPane(textBlock);
                centerContentPanel.add(textScroll, BorderLayout.CENTER);
                refresh(pagePanels.get(activePageIndex));
                return;
            }

            // If ONLY a logo was selected, apply it at top of center area
            if (headerLogoLabel.getIcon() != null) {
                JLabel logoPreview = new JLabel(headerLogoLabel.getIcon());
                logoPreview.setHorizontalAlignment(SwingConstants.CENTER);
                centerContentPanel.add(logoPreview, BorderLayout.NORTH);
                refresh(pagePanels.get(activePageIndex));
                return;
            }

            JOptionPane.showMessageDialog(null, "Nothing to apply yet!");
        });

        outer.add(content, BorderLayout.CENTER);

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
    // CALCULATIONS SECTION
    // ===========================
    public static JPanel calculationsSection() {
        RoundedPanel outer = new RoundedPanel(25);
        outer.setOpaque(false);
        outer.setLayout(new BorderLayout());
        outer.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        outer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 420));

        JPanel headerBar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
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
            @Override
            protected void paintComponent(Graphics g) {
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
            if (mulOp.isSelected()) opsList.add("×");
            if (divOp.isSelected()) opsList.add("÷");

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

                Font f = getSelectedFont();

                for (String p : problems) {
                    JLabel line = new JLabel(p);

                    line.setFont(f);
                    line.setForeground(selectedFontColor);

                    // underline support
                    if (fontSectionUnderline.isSelected()) {
                        Map<TextAttribute, Object> map = new HashMap<>(f.getAttributes());
                        map.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
                        line.setFont(f.deriveFont(map));
                    }

                    // STEP-5: Alignment support
                    switch (selectedAlignment) {

                        case StyleConstants.ALIGN_CENTER -> line.setHorizontalAlignment(SwingConstants.CENTER);
                        case StyleConstants.ALIGN_RIGHT -> line.setHorizontalAlignment(SwingConstants.RIGHT);
                        default -> line.setHorizontalAlignment(SwingConstants.LEFT);
                    }

// STEP 3 — REQUIRED FOR ALIGNMENT TO ACTUALLY SHOW
                    line.setAlignmentX(
                            selectedAlignment == StyleConstants.ALIGN_CENTER ? Component.CENTER_ALIGNMENT :
                                    selectedAlignment == StyleConstants.ALIGN_RIGHT ? Component.RIGHT_ALIGNMENT :
                                            Component.LEFT_ALIGNMENT
                    );

                    line.setMaximumSize(new Dimension(Integer.MAX_VALUE, line.getPreferredSize().height));

                    line.setBorder(BorderFactory.createEmptyBorder(2, 12, 2, 0));
                    mathPanel.add(line);
                }


                mathPanel.add(Box.createVerticalStrut(20));
            }

            // ===============================
            // APPLY TO THE PAGE (NOT SIDEBAR)
            // ===============================
// Build text version for Font Section input box
            StringBuilder sb = new StringBuilder();

            for (String op : opsList) {
                sb.append(op).append(" Problems\n");
                java.util.List<String> problems = MathGen.generate(op, selectedRange, count);
                for (String p : problems) {
                    sb.append(p).append("\n");
                }
                sb.append("\n");
            }

// Push into the font text box
            if (fontSideInput != null) {
                fontSideInput.setText(sb.toString());
            }

            String[] items = sb.toString().split("\n");

            java.util.List<String> cleaned = new ArrayList<>();
            for (String s : items) if (!s.trim().isEmpty()) cleaned.add(s);

            JPanel rendered = renderItemsPanel(cleaned.toArray(new String[0]));
            activePageIndex = 0;
            pagePanel = pagePanels.get(0);
            centerContentPanel = getContentPanelFromPage(pagePanel);

            resetPagesForRender();
            renderItemsToPages(cleaned.toArray(new String[0]));


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
// QUICK FILL SECTION
// ===========================
    public static JPanel quickFillSection() {
        QuickFill quickFill = new QuickFill(null);
        RoundedPanel outer = new RoundedPanel(25);
        outer.setOpaque(false);
        outer.setLayout(new BorderLayout());
        outer.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        outer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 420));

        JPanel headerBar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
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

        // Grid for buttons
        JPanel grid = new JPanel(new GridLayout(0, 2, 12, 12));
        grid.setOpaque(false);

        Color grad1 = new Color(150, 165, 190);
        Color grad2 = new Color(110, 125, 155);

        java.util.function.Function<String, JButton> makeBtn = (text) -> {
            JButton btn = new JButton(text) {
                @Override
                protected void paintComponent(Graphics g) {
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

        // Map QuickFill criteria to buttons
        Map<String, QuickFill.Criteria> buttonMap = Map.ofEntries(
                Map.entry("A-Z", QuickFill.Criteria.A_Z),
                Map.entry("a-z", QuickFill.Criteria.a_z),
                Map.entry("1-20", QuickFill.Criteria.NUM_1_20),
                Map.entry("Sight Words", QuickFill.Criteria.SIGHT_WORDS),
                Map.entry("Colors", QuickFill.Criteria.COLORS),
                Map.entry("Animals", QuickFill.Criteria.ANIMALS),
                Map.entry("CVC Words", QuickFill.Criteria.CVC_WORDS),
                Map.entry("Shapes", QuickFill.Criteria.SHAPES),
                Map.entry("Addition", QuickFill.Criteria.ADDITION),
                Map.entry("Subtraction", QuickFill.Criteria.SUBTRACTION),
                Map.entry("Count 1-10", QuickFill.Criteria.COUNT_1_10)
        );


        // Selection holder
        final QuickFill.Criteria[] selectedCriteria = new QuickFill.Criteria[1];

        // Create buttons + listeners
        for (Map.Entry<String, QuickFill.Criteria> entry : buttonMap.entrySet()) {
            JButton btnTest = makeBtn.apply(entry.getKey());
            QuickFill.Criteria crit = entry.getValue();

            btnTest.addActionListener(e -> selectedCriteria[0] = crit);

            grid.add(btnTest);
        }

        grid.add(new JLabel());
        content.add(grid);
        content.add(Box.createVerticalStrut(10));

        // GENERATE BUTTON
        JButton generateBtn = styleButton(new JButton("Generate"), new Color(150, 165, 190));
        generateBtn.setForeground(Color.WHITE);
        generateBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        fullWidth.accept(generateBtn);

        generateBtn.addActionListener(e -> {

            if (selectedCriteria[0] == null) {
                JOptionPane.showMessageDialog(null, "Please select a criteria first.");
                return;
            }

            if (selectedCriteria[0] == QuickFill.Criteria.SHAPES) {
                quickFill.showShapeSelector();
                return;
            }
            pushUndoState();
            String[] items = quickFill.quickfill(selectedCriteria[0]);

            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBackground(Color.WHITE);
            panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

            for (String s : items) {
                Font f = getSelectedFont();
                JLabel lbl = new JLabel(s);
                lbl.setFont(f);
                lbl.setForeground(selectedFontColor);

                // underline
                if (fontSectionUnderline.isSelected()) {
                    Map<TextAttribute, Object> map = new HashMap<>(f.getAttributes());
                    map.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
                    lbl.setFont(f.deriveFont(map));
                }

                // STEP-5: Alignment
                switch (selectedAlignment) {
                    case StyleConstants.ALIGN_CENTER -> lbl.setHorizontalAlignment(SwingConstants.CENTER);
                    case StyleConstants.ALIGN_RIGHT -> lbl.setHorizontalAlignment(SwingConstants.RIGHT);
                    default -> lbl.setHorizontalAlignment(SwingConstants.LEFT);
                }

                // STEP 3 — REQUIRED FOR ALIGNMENT TO ACTUALLY DISPLAY IN BOXLAYOUT
                lbl.setAlignmentX(
                        selectedAlignment == StyleConstants.ALIGN_CENTER ? Component.CENTER_ALIGNMENT :
                                selectedAlignment == StyleConstants.ALIGN_RIGHT ? Component.RIGHT_ALIGNMENT :
                                        Component.LEFT_ALIGNMENT
                );

                lbl.setMaximumSize(new Dimension(Integer.MAX_VALUE, lbl.getPreferredSize().height));

                panel.add(lbl);
            }
// Build text version for Font Section input box
            StringBuilder sb = new StringBuilder();
            for (String s : items) sb.append(s).append("\n");

// Push into the Font section text box
            if (fontSideInput != null) {
                fontSideInput.setText(sb.toString());
            }

            String[] itemsClean = sb.toString().split("\n");

            java.util.List<String> cleaned = new ArrayList<>();
            for (String s : itemsClean) if (!s.trim().isEmpty()) cleaned.add(s);

            JPanel rendered = renderItemsPanel(cleaned.toArray(new String[0]));
            pushUndoState();
            activePageIndex = 0;
            pagePanel = pagePanels.get(0);
            centerContentPanel = getContentPanelFromPage(pagePanel);

            resetPagesForRender();
            renderItemsToPages(cleaned.toArray(new String[0]));


        });

        content.add(generateBtn);
        content.add(Box.createVerticalStrut(10));

        // RESET BUTTON
        JButton resetQuickBtn = styleButton(new JButton("Reset Quick Fill"), new Color(150, 165, 190));
        resetQuickBtn.setForeground(Color.WHITE);
        resetQuickBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        resetQuickBtn.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        resetQuickBtn.setFocusPainted(false);
        resetQuickBtn.setContentAreaFilled(false);
        resetQuickBtn.setOpaque(false);

        resetQuickBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        resetQuickBtn.setMaximumSize(new Dimension(180, 40));

        resetQuickBtn.addActionListener(e -> {
            activePageIndex = 0;
            pagePanel = pagePanels.get(0);
            centerContentPanel = getContentPanelFromPage(pagePanel);

            centerContentPanel.removeAll();
            centerContentPanel.repaint();
            centerContentPanel.revalidate();
        });

        content.add(resetQuickBtn);
        content.add(Box.createVerticalStrut(10));
        content.add(Box.createVerticalStrut(10));

        outer.add(content, BorderLayout.CENTER);

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
    // TEMPLATE SECTION
    // ===========================
    public static JPanel templateSection() {
        RoundedPanel outer = new RoundedPanel(25);
        outer.setOpaque(false);
        outer.setLayout(new BorderLayout());
        outer.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        outer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 420));

        JPanel headerBar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
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

        Function<String, JButton> makeBtn = getStringJButtonFunction();

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

    private static Function<String, JButton> getStringJButtonFunction() {
        Color grad1 = new Color(150, 165, 190);
        Color grad2 = new Color(110, 125, 155);

        Function<String, JButton> makeBtn = (text) -> {
            JButton btn = new JButton(text) {
                @Override
                protected void paintComponent(Graphics g) {
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
        return makeBtn;
    }

    // ===========================
    // SUPPORT UTILITIES
    // ===========================
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
            GradientPaint gp = new GradientPaint(0, 0, new Color(180, 210, 230), 0, getHeight(), new Color(60, 90, 120));
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
            Color normal = new Color(255, 255, 255, 0);
            Color hover = new Color(255, 255, 255, 80);

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                icon.setOpaque(false);
                icon.setBackground(hover);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                icon.setOpaque(false);
                icon.setBackground(null);
            }
        });
        return icon;
    }

    // Creates the chat popup panel used inside a JDialog
    private static JPanel createChatPopupPanel(JDialog parent) {
        RoundedPanel popup = new RoundedPanel(30);
        popup.setLayout(new BorderLayout());
        popup.setBackground(Color.WHITE);
        popup.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        popup.setPreferredSize(new Dimension(320, 480));

        RoundedPanel header = new RoundedPanel(30) {
            @Override
            protected void paintComponent(Graphics g) {
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
        pagePanels.get(activePageIndex).revalidate();
        pagePanels.get(activePageIndex).repaint();

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

    // ============================================================
// FIXED + CLEANED HEADER PANEL METHOD
// ============================================================
    public static JPanel buildHeaderPanel(JPanel pagePanel, WorksheetSettings settings) {

        // ---- HEADER PANEL ----
        RoundedPanel header = new RoundedPanel(12) {
            @Override
            protected void paintComponent(Graphics g) {

                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Soft white background
                g2.setColor(new Color(255, 255, 255));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);

                // Border
                g2.setColor(new Color(7, 7, 7));
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
            }
        };

        header.setOpaque(true);
        header.setLayout(new BorderLayout(12, 8));
        header.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));

        // ------------------ LOGO AREA ------------------
        headerLogoLabel = new JLabel();
        headerLogoLabel.setHorizontalAlignment(SwingConstants.LEFT);
        headerLogoLabel.setVerticalAlignment(SwingConstants.CENTER);

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

        // ------------------ DYNAMIC HEIGHT FIX (CORRECT VERSION) ------------------

        // publish reference globally
        headerPanel = header;
// ------------------ DYNAMIC HEIGHT FIX (CORRECT VERSION) ------------------
        SwingUtilities.invokeLater(() -> {
            int nameH = headerNameLabel.getPreferredSize().height;
            int instrH = headerInstructionsLabel.getPreferredSize().height;

            int padding = 30; // top + bottom spacing
            int dynamicHeight = nameH + instrH + padding;

            int minHeight = 80;
            int maxHeight = 200;

            int finalHeight = Math.max(minHeight, Math.min(maxHeight, dynamicHeight));

            header.setPreferredSize(new Dimension(0, finalHeight));
            header.revalidate();
            header.repaint();
        });

        return header;
    }


    // Resize an image to a specific target height (keeps aspect ratio)
    public static ImageIcon resizeToFitHeight(Image img, int targetHeight) {
        if (img == null) return null;
        int h = img.getHeight(null);
        int w = img.getWidth(null);
        if (h <= 0 || w <= 0) return null;
        double scale = (double) targetHeight / h;
        int newW = (int) (w * scale);
        int newH = (int) (h * scale);
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
        pagePanels.get(activePageIndex)
                .revalidate();
        pagePanels.get(activePageIndex)
                .repaint();
    }

    public static void applyAlignmentToEditor(int align) {
        // store as current selection
        selectedAlignment = align;

        if (centerContentPanel == null || centerContentPanel.getComponentCount() == 0) return;

        Component comp = centerContentPanel.getComponent(0);

        // If center content is a JScrollPane (typical for editor)
        if (comp instanceof JScrollPane scroll) {
            Component view = scroll.getViewport().getView();

            // If it's a JTextPane -> apply paragraph attributes to entire doc
            if (view instanceof JTextPane editor) {
                StyledDocument doc = editor.getStyledDocument();
                SimpleAttributeSet attrs = new SimpleAttributeSet();
                StyleConstants.setAlignment(attrs, align);
                doc.setParagraphAttributes(0, doc.getLength(), attrs, false);
                editor.revalidate();
                editor.repaint();
                return;
            }

            // If it's a JTextArea -> wrap behavior is limited; we'll try to center by replacing with a JTextPane
            if (view instanceof JTextArea ta) {
                String txt = ta.getText();
                JTextPane newPane = new JTextPane();
                newPane.setText(txt);
                newPane.setEditable(ta.isEditable());
                newPane.setBackground(ta.getBackground());
                newPane.setFont(ta.getFont());
                StyledDocument doc = newPane.getStyledDocument();
                SimpleAttributeSet attrs = new SimpleAttributeSet();
                StyleConstants.setAlignment(attrs, align);
                doc.setParagraphAttributes(0, doc.getLength(), attrs, false);
                scroll.setViewportView(newPane);
                centerContentPanel.revalidate();
                centerContentPanel.repaint();
                return;
            }
        }

        // If center content is a JPanel with multiple components (mathPanel / quickFill panel)
        if (comp instanceof JPanel panel) {
            for (Component child : panel.getComponents()) {
                // LABELS: adjust horizontal alignment and alignmentX for box layout
                if (child instanceof JLabel lbl) {
                    switch (align) {
                        case StyleConstants.ALIGN_CENTER -> {
                            lbl.setHorizontalAlignment(SwingConstants.CENTER);
                            lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
                        }
                        case StyleConstants.ALIGN_RIGHT -> {
                            lbl.setHorizontalAlignment(SwingConstants.RIGHT);
                            lbl.setAlignmentX(Component.RIGHT_ALIGNMENT);
                        }
                        default -> {
                            lbl.setHorizontalAlignment(SwingConstants.LEFT);
                            lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
                        }
                    }
                    // make sure label takes full width so horizontalAlignment renders visually
                    lbl.setMaximumSize(new Dimension(Integer.MAX_VALUE, lbl.getPreferredSize().height));
                    lbl.revalidate();
                }

                // If child is a JScrollPane -> try to update the inner editor/textpane
                if (child instanceof JScrollPane sp) {
                    Component v = sp.getViewport().getView();
                    if (v instanceof JTextPane ep) {
                        StyledDocument d = ep.getStyledDocument();
                        SimpleAttributeSet attrs = new SimpleAttributeSet();
                        StyleConstants.setAlignment(attrs, align);
                        d.setParagraphAttributes(0, d.getLength(), attrs, false);
                        ep.revalidate();
                    } else if (v instanceof JTextArea ta) {
                        // convert to JTextPane to enable alignment
                        String txt = ta.getText();
                        JTextPane np = new JTextPane();
                        np.setText(txt);
                        np.setEditable(ta.isEditable());
                        StyledDocument d = np.getStyledDocument();
                        SimpleAttributeSet attrs = new SimpleAttributeSet();
                        StyleConstants.setAlignment(attrs, align);
                        d.setParagraphAttributes(0, d.getLength(), attrs, false);
                        sp.setViewportView(np);
                        sp.revalidate();
                    }
                }
            }

            panel.revalidate();
            panel.repaint();
        }
    }

    public static Font getSelectedFont() {
        String fam = "SansSerif";
        int size = 24;
        int style = Font.PLAIN;

        try {
            fam = ((String) ((JComboBox) fontSectionComboFamily).getSelectedItem());
        } catch (Exception ignored) {
        }

        try {
            size = (Integer) fontSectionSizeSpinner.getValue();
        } catch (Exception ignored) {
        }

        try {
            boolean bold = fontSectionBold.isSelected();
            boolean italic = fontSectionItalic.isSelected();
            if (bold) style |= Font.BOLD;
            if (italic) style |= Font.ITALIC;
        } catch (Exception ignored) {
        }

        return new Font(fam, style, size);
    }

    public static void addNewPage() {

        // Create the page
        JPanel newPage = new JPanel(new BorderLayout());
        newPage.setOpaque(false);
// === MATCH SIZE OF PAGE 1 OR USE DEFAULT ===
        Dimension baseSize = pagePanels.isEmpty()
                ? new Dimension(1000, 1400)   // default for first page
                : pagePanels.get(0).getPreferredSize();

        newPage.setPreferredSize(baseSize);
        newPage.setMinimumSize(baseSize);
        newPage.setMaximumSize(baseSize);

        JPanel renderPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(Color.WHITE);
                g2.fillRect(0, 0, getWidth(), getHeight());

                if (showMargins) {
                    g2.setColor(new Color(0, 0, 0, 60));
                    g2.drawRect(marginLeft, marginTop,
                            getWidth() - marginLeft - marginRight,
                            getHeight() - marginTop - marginBottom);
                }
            }
        };

        renderPanel.setBorder(new LineBorder(new Color(170, 170, 255), 2, true));
        renderPanel.setPreferredSize(new Dimension(1000, 1400));

        JPanel header = buildHeaderPanel(newPage, new WorksheetSettings("Default", "", 20));
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(Color.WHITE);

        newPage.add(header, BorderLayout.NORTH);
        newPage.add(content, BorderLayout.CENTER);

        pagePanels.add(newPage);
        pageContainer.add(newPage);
        pageContainer.revalidate();
        pageContainer.repaint();

        // Set this page as active
        // mark current page globally
        activePageIndex = pagePanels.size() - 1;
        pagePanel = newPage;
        centerContentPanel = content;   // you already have this, keep it
        int newIndex = pagePanels.size() - 1;
        newPage.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                activatePage(newIndex);
            }
        });

        // Make sure the header and content we created are present
        // Set this page as active (use helper)
        activatePage(newIndex);
    }

    // ---------- Add these helper methods (place near other utilities) ----------
    public static JPanel getContentPanelFromPage(JPanel page) {
        // page layout: header at BorderLayout.NORTH, content at BorderLayout.CENTER
        // so getComponent(1) is the content panel (safer with check)
        for (Component c : page.getComponents()) {
            if (c instanceof JPanel) {
                BorderLayout bl = (BorderLayout) page.getLayout();
                // try to find CENTER directly
            }
        }
        // fallback: assume index 1 is center content (your addNewPage follows that)
        try {
            Component c = page.getComponent(1);
            if (c instanceof JPanel) return (JPanel) c;
        } catch (Exception ignored) {
        }
        return null;
    }

    public static void activatePage(int index) {
        if (index < 0 || index >= pagePanels.size()) return;

        // de-highlight previous
        if (activePageIndex >= 0 && activePageIndex < pagePanels.size()) {
            JPanel prev = pagePanels.get(activePageIndex);
            if (prev != null) prev.setBorder(new LineBorder(new Color(170, 170, 255), 2, true));
        }

        activePageIndex = index;
        pagePanel = pagePanels.get(activePageIndex);

        // set the centerContentPanel reference to the clicked page's center panel
        JPanel content = getContentPanelFromPage(pagePanel);
        if (content != null) {
            centerContentPanel = content;
        }

        // visually mark active page
        pagePanel.setBorder(new LineBorder(new Color(60, 120, 220), 3, true));

        // focus first editor inside the content
        if (centerContentPanel.getComponentCount() > 0) {
            Component comp = centerContentPanel.getComponent(0);
            if (comp instanceof JScrollPane sp) {
                Component view = sp.getViewport().getView();
                if (view instanceof JTextPane tp) tp.requestFocusInWindow();
                else if (view instanceof JTextArea ta) ta.requestFocusInWindow();
            }
        }

        pageContainer.revalidate();
        pageContainer.repaint();
        canvasScrollRevalidateSafely();

        // ⭐ MOST IMPORTANT PART — AUTO-SCALE PAGE WHEN SWITCHED
        SwingUtilities.invokeLater(() ->
                autoScalePage(pagePanel, canvasScroll)
        );
    }


    // small helper so activatePage can revalidate the canvas scroll (you'll need to add this method)
    private static void canvasScrollRevalidateSafely() {
       // canvasScroll is local in createAndShowUI; easiest option: call updateCanvasLayout for active page
        if (pagePanels.size() > 0) {
            JPanel p = pagePanels.get(activePageIndex);
            // If you named your canvasWrapper and canvasScroll as local variables, either make them fields
            // or just revalidate the page and pageContainer (this is sufficient)
            p.revalidate();
            p.repaint();
            pageContainer.revalidate();
            pageContainer.repaint();
        }
    }

    public static void autoScalePage(JPanel page, JScrollPane scroll) {

        int pageW = page.getPreferredSize().width;
        int pageH = page.getPreferredSize().height;

        int viewW = scroll.getViewport().getWidth();
        int viewH = scroll.getViewport().getHeight();

        if (viewW <= 0 || viewH <= 0) return;

        double scaleX = (double) viewW / pageW;
        double scaleY = (double) viewH / pageH;

        // choose the smaller scale (fit whole page)
        pageScale = Math.min(scaleX, scaleY) * 0.95;  // 5% padding

        int newW = (int) (pageW * pageScale);
        int newH = (int) (pageH * pageScale);

        // Apply scaled size
        page.setPreferredSize(new Dimension(newW, newH));
        Component headerComp = ((BorderLayout) page.getLayout()).getLayoutComponent(BorderLayout.NORTH);

        if (headerComp != null && headerNameLabel != null && headerInstructionsLabel != null) {

            // Recalculate REAL required height based on text wrapping
            headerNameLabel.revalidate();
            headerInstructionsLabel.revalidate();

            int nameH = headerNameLabel.getPreferredSize().height;
            int insH = headerInstructionsLabel.getPreferredSize().height;

            // Add safe padding
            int padding = 40;

            // Final unscaled height that guarantees no clipping
            int requiredHeight = nameH + insH + padding;

            // Do NOT scale down header height — it must always fit its content
            int finalHeight = requiredHeight;

            // Force header to the new height
            headerComp.setPreferredSize(new Dimension(page.getPreferredSize().width, finalHeight));
            headerComp.revalidate();
            headerComp.repaint();
        }


        page.revalidate();
        page.repaint();
    }

    /**
     * Render items as a JPanel according to selectedViewMode
     */
    public static JPanel renderItemsPanel(String[] items) {
        JPanel container = new JPanel();
        container.setOpaque(false);

        switch (selectedViewMode) {

            // ==========================
            // GRID VIEW
            // ==========================
            case GRID -> {
                JPanel grid = new JPanel();
                grid.setOpaque(false);
                grid.setLayout(new GridLayout(0, 1, 8, 8));

                for (String s : items) {
                    JPanel card = new JPanel(new BorderLayout());
                    card.setBackground(Color.WHITE);
                    card.setBorder(new LineBorder(new Color(170, 170, 190), 2, true));

                    JLabel lbl = new JLabel("<html><center>" + s + "</center></html>");
                    lbl.setFont(getSelectedFont());
                    lbl.setForeground(selectedFontColor);

                    // ★ ALIGNMENT FOR GRID
                    if (selectedAlignment == StyleConstants.ALIGN_CENTER) {
                        lbl.setHorizontalAlignment(SwingConstants.CENTER);
                    } else if (selectedAlignment == StyleConstants.ALIGN_RIGHT) {
                        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
                    } else {
                        lbl.setHorizontalAlignment(SwingConstants.LEFT);
                    }

                    lbl.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
                    card.add(lbl, BorderLayout.CENTER);
                    grid.add(card);
                }

                container.setLayout(new BorderLayout());
                container.add(grid, BorderLayout.CENTER);
            }

            // ==========================
            // MATCH VIEW
            // ==========================
            case MATCH -> {
                container.setLayout(new GridLayout(1, 2, 12, 12));

                JPanel left = new JPanel();
                left.setOpaque(false);
                left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

                JPanel right = new JPanel();
                right.setOpaque(false);
                right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));

                // LEFT SIDE LABELS — ALIGNMENT & COLOR
                for (String s : items) {
                    JLabel l = new JLabel(s);
                    l.setFont(getSelectedFont());
                    l.setForeground(selectedFontColor);

                    // ★ Alignment fix (MATCH LEFT)
                    if (selectedAlignment == StyleConstants.ALIGN_CENTER) {
                        l.setHorizontalAlignment(SwingConstants.CENTER);
                        l.setAlignmentX(Component.CENTER_ALIGNMENT);
                    } else if (selectedAlignment == StyleConstants.ALIGN_RIGHT) {
                        l.setHorizontalAlignment(SwingConstants.RIGHT);
                        l.setAlignmentX(Component.RIGHT_ALIGNMENT);
                    } else {
                        l.setHorizontalAlignment(SwingConstants.LEFT);
                        l.setAlignmentX(Component.LEFT_ALIGNMENT);
                    }

                    l.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));

                    left.add(Box.createVerticalStrut(6));
                    left.add(l);
                }

                // RIGHT SIDE PLACEHOLDERS (unchanged)
                for (int i = 0; i < items.length; i++) {
                    JPanel placeholder = new JPanel();
                    placeholder.setPreferredSize(new Dimension(100, 48));
                    placeholder.setBorder(new LineBorder(new Color(200, 200, 200), 2, true));
                    placeholder.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
                    placeholder.setBackground(Color.WHITE);
                    placeholder.setAlignmentX(Component.LEFT_ALIGNMENT);

                    JLabel ptext = new JLabel("Image / Match " + (i + 1));
                    ptext.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

                    placeholder.add(ptext);
                    right.add(Box.createVerticalStrut(6));
                    right.add(placeholder);
                }

                JScrollPane leftScroll = new JScrollPane(left);
                leftScroll.setBorder(null);
                JScrollPane rightScroll = new JScrollPane(right);
                rightScroll.setBorder(null);

                container.add(leftScroll);
                container.add(rightScroll);
            }

            // ==========================
            // LIST VIEW
            // ==========================
            default -> {
                container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

                for (int i = 0; i < items.length; i++) {
                    String s = items[i];
                    JLabel lbl = new JLabel(s);
                    lbl.setFont(getSelectedFont());
                    lbl.setForeground(selectedFontColor);

                    // ★ Alignment fix for LIST view
                    if (selectedAlignment == StyleConstants.ALIGN_CENTER) {
                        lbl.setHorizontalAlignment(SwingConstants.CENTER);
                        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
                    } else if (selectedAlignment == StyleConstants.ALIGN_RIGHT) {
                        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
                        lbl.setAlignmentX(Component.RIGHT_ALIGNMENT);
                    } else {
                        lbl.setHorizontalAlignment(SwingConstants.LEFT);
                        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
                    }

                    lbl.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
                    container.add(lbl);

                    if (i < items.length - 1) {
                        JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
                        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
                        container.add(sep);
                    }
                }
            }
        }

        return container;
    }




    public static void renderItemsToPages(String[] items) {
        lastRenderedItems = items;

        ViewMode mode = selectedViewMode;

        // Ensure we are operating on the currently active page
        ensureCenterContentPanel();

        if (mode == ViewMode.LIST || mode == ViewMode.MATCH) {
            // Keep the previous LIST/MATCH behavior but tidy up page splitting
            int pageIndex = activePageIndex;
            JPanel page = pagePanels.get(pageIndex);
            centerContentPanel = getContentPanelFromPage(page);
            centerContentPanel.removeAll();

            int availableHeight = page.getPreferredSize().height
                    - headerPanel.getPreferredSize().height
                    - 40;

            int usedHeight = 0;

            JPanel currentPageContent = new JPanel();
            currentPageContent.setLayout(new BoxLayout(currentPageContent, BoxLayout.Y_AXIS));
            currentPageContent.setOpaque(false);

            for (String item : items) {
                // Render a single-item panel (list-style)
                JPanel itemPanel = renderItemsPanel(new String[]{item});
                itemPanel.doLayout();
                int itemHeight = itemPanel.getPreferredSize().height;
                if (usedHeight + itemHeight > availableHeight) {
                    // commit current page content
                    centerContentPanel.add(currentPageContent, BorderLayout.NORTH);
                    // start new page
                    addNewPage();
                    page = pagePanels.get(activePageIndex);
                    centerContentPanel = getContentPanelFromPage(page);
                    currentPageContent = new JPanel();
                    currentPageContent.setLayout(new BoxLayout(currentPageContent, BoxLayout.Y_AXIS));
                    currentPageContent.setOpaque(false);
                    usedHeight = 0;
                }
                currentPageContent.add(itemPanel);
                usedHeight += itemHeight;
            }

            centerContentPanel.add(currentPageContent, BorderLayout.NORTH);
            centerContentPanel.revalidate();
            centerContentPanel.repaint();
            return;
        }

        // -------- GRID MODE (robust) ----------
        // ---------- EXACT GRID MODE (uses gridRows/gridCols/boxesPerPage) ----------
        if (mode == ViewMode.GRID) {

            int pageIndex = activePageIndex;
            JPanel page = pagePanels.get(pageIndex);
            centerContentPanel = getContentPanelFromPage(page);

            centerContentPanel.removeAll();

            // page usable area (leave padding)
            int pageW = page.getPreferredSize().width - 60;
            int pageH = page.getPreferredSize().height
                    - headerPanel.getPreferredSize().height
                    - 60;

            int rows = Math.max(1, gridRows);
            int cols = Math.max(1, gridCols);

            // boxes per page derived from setting but never exceed rows*cols
            int pageCapacity = Math.min(boxesPerPage, rows * cols);
            if (pageCapacity <= 0) pageCapacity = rows * cols;

            int total = items.length;
            int start = 0;

            while (start < total) {

                int end = Math.min(start + pageCapacity, total);
                String[] subset = java.util.Arrays.copyOfRange(items, start, end);

                // Create grid with layout matching rows x cols
                JPanel grid = new JPanel(new GridLayout(rows, cols, 16, 16));
                grid.setOpaque(false);
                grid.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

                // cell sizes estimated from page area
                int cellW = pageW / cols;
                int cellH = pageH / rows;

                for (int i = 0; i < rows * cols; i++) {
                    JPanel cell = new JPanel(new BorderLayout());
                    cell.setBackground(Color.WHITE);
                    cell.setBorder(new LineBorder(new Color(170,170,190), 2, true));
                    cell.setPreferredSize(new Dimension(cellW, cellH));

                    if (i < subset.length) {
                        Font baseFont = getSelectedFont();
                        JLabel lbl = new JLabel("<html><center>" + subset[i] + "</center></html>", SwingConstants.CENTER);
                        lbl.setFont(baseFont);
                        lbl.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
                        lbl.setForeground(selectedFontColor);

                        // Underline if required
                        if (fontSectionUnderline.isSelected()) {
                            Map<TextAttribute, Object> map = new HashMap<>(baseFont.getAttributes());
                            map.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
                            lbl.setFont(baseFont.deriveFont(map));
                        }

                        // Try to autoscale font to fit cell if enabled
                        if (gridAutoScaleText) {
                            Font fTry = baseFont;
                            // simple downscaling loop until fits (cheap heuristic)
                            for (int sz = baseFont.getSize(); sz >= 8; sz--) {
                                fTry = baseFont.deriveFont((float) sz);
                                lbl.setFont(fTry);
                                lbl.revalidate();
                                Dimension pref = lbl.getPreferredSize();
                                // leave small padding
                                if (pref.width <= cellW - 12 && pref.height <= cellH - 12) {
                                    break;
                                }
                            }
                        }

                        // alignment settings
                        switch (selectedAlignment) {
                            case StyleConstants.ALIGN_CENTER -> lbl.setHorizontalAlignment(SwingConstants.CENTER);
                            case StyleConstants.ALIGN_RIGHT -> lbl.setHorizontalAlignment(SwingConstants.RIGHT);
                            default -> lbl.setHorizontalAlignment(SwingConstants.LEFT);
                        }

                        cell.add(lbl, BorderLayout.CENTER);
                    }

                    grid.add(cell);
                }

                centerContentPanel.add(grid, BorderLayout.CENTER);
                centerContentPanel.revalidate();
                centerContentPanel.repaint();

                start = end;

                if (start < total) {
                    addNewPage();
                    pageIndex = activePageIndex;
                    page = pagePanels.get(pageIndex);
                    centerContentPanel = getContentPanelFromPage(page);
                }
            }

            return;
        }

    }/**
     * Reduce a label's font size until the rendered text fits within maxWidth x maxHeight
     * Keeps the original font family and style but adjusts the size down.
     */
    public static void scaleLabelFontToFit(JLabel label, int maxWidth, int maxHeight) {
        if (label == null) return;
        Font base = label.getFont();
        if (base == null) return;

        // Start from current size, step down until it fits, but avoid making it tiny
        int size = base.getSize();
        FontMetrics fm;
        int textW, textH;

        // try to avoid HTML rendering pitfalls: use plain text measurement
        String text = label.getText();
        // Remove HTML tags if any, for measurement simplicity
        if (text != null && text.startsWith("<html>")) {
            text = text.replaceAll("<[^>]*>", "").trim();
        }

        for (int s = size; s >= 8; s--) {
            Font trial = base.deriveFont((float) s);
            fm = label.getFontMetrics(trial);
            // measure as single line
            textW = fm.stringWidth(text);
            textH = fm.getHeight();

            if (textW <= maxWidth && textH <= maxHeight) {
                label.setFont(trial);
                return;
            }
        }

        // if no size fits, set to minimal size
        label.setFont(base.deriveFont(8f));
    }
    private static void ensureCenterContentPanel() {
        if (pagePanels.isEmpty()) {
            addNewPage();
        }
        if (activePageIndex < 0 || activePageIndex >= pagePanels.size()) {
            activePageIndex = 0;
        }
        if (pagePanel == null) {
            pagePanel = pagePanels.get(activePageIndex);
        }
        if (centerContentPanel == null) {
            centerContentPanel = getContentPanelFromPage(pagePanels.get(activePageIndex));
        }
    }
    public static void applyFontToActivePage(Font newFont, Color color, int alignment) {
        if (centerContentPanel == null) return;
        pushUndoState();
        for (Component comp : centerContentPanel.getComponents()) {

            if (comp instanceof JLabel lbl) {
                lbl.setFont(newFont);
                lbl.setForeground(color);
            }

            else if (comp instanceof JTextPane tp) {
                tp.setFont(newFont);
                tp.setForeground(color);

                StyledDocument doc = tp.getStyledDocument();
                SimpleAttributeSet attrs = new SimpleAttributeSet();
                StyleConstants.setAlignment(attrs, alignment);
                doc.setParagraphAttributes(0, doc.getLength(), attrs, false);
            }

            else if (comp instanceof JScrollPane sp) {
                Component v = sp.getViewport().getView();
                if (v instanceof JTextPane tp2) {
                    tp2.setFont(newFont);
                    tp2.setForeground(color);

                    StyledDocument doc = tp2.getStyledDocument();
                    SimpleAttributeSet attrs = new SimpleAttributeSet();
                    StyleConstants.setAlignment(attrs, alignment);
                    doc.setParagraphAttributes(0, doc.getLength(), attrs, false);
                }
            }
        }

        centerContentPanel.revalidate();
        centerContentPanel.repaint();
    }
    private static void renderItemsToSpecificPage(JPanel page, String[] items) {
        JPanel content = getContentPanelFromPage(page);
        content.removeAll();

        for (String s : items) {
            JLabel lbl = new JLabel(s);
            lbl.setFont(new Font("SansSerif", Font.PLAIN, 20));
            content.add(lbl);
        }

        content.revalidate();
        content.repaint();
    }

}



