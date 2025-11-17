package com.espaneg.ui;

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
        leftContent.add(gridSection());
        leftContent.add(fontSection());
        leftContent.add(importContentSection());
        leftContent.add(colorPaletteSection());
        leftContent.add(calculationsSection());
        leftContent.add(quickFillSection());
        leftContent.add(templateSection());
        
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
// GRID SECTION (UI ONLY – NOW WITH ORIENTATION CHECKBOXES)
// ============================================================
    public static JPanel gridSection() {

        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(Color.WHITE);
        outer.setMaximumSize(new Dimension(250, 350));

        JButton header = new JButton("▼  Grid");
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
        // Show Grid Checkbox
        // ------------------------
        JCheckBox showGrid = new JCheckBox("Show Grid");
        showGrid.setBackground(Color.WHITE);
        showGrid.setFont(new Font("SansSerif", Font.PLAIN, 12));

        // ------------------------
        // Grid orientation checkboxes
        // ------------------------
        JLabel orientationLabel = new JLabel("Grid Orientation:");
        orientationLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JCheckBox verticalGrid = new JCheckBox("Vertical");
        JCheckBox horizontalGrid = new JCheckBox("Horizontal");
        JCheckBox noneGrid = new JCheckBox("None");

        verticalGrid.setBackground(Color.WHITE);
        horizontalGrid.setBackground(Color.WHITE);
        noneGrid.setBackground(Color.WHITE);

        verticalGrid.setFont(new Font("SansSerif", Font.PLAIN, 12));
        horizontalGrid.setFont(new Font("SansSerif", Font.PLAIN, 12));
        noneGrid.setFont(new Font("SansSerif", Font.PLAIN, 12));

        // If "None" is selected, disable the others
        noneGrid.addActionListener(e -> {
            if (noneGrid.isSelected()) {
                verticalGrid.setSelected(false);
                horizontalGrid.setSelected(false);
            }
        });

        // If Vertical or Horizontal is selected, unselect "None"
        verticalGrid.addActionListener(e -> {
            if (verticalGrid.isSelected()) noneGrid.setSelected(false);
        });

        horizontalGrid.addActionListener(e -> {
            if (horizontalGrid.isSelected()) noneGrid.setSelected(false);
        });

        // ------------------------
        // Grid Size Spinner
        // ------------------------
        JLabel sizeLabel = new JLabel("Grid Size:");
        sizeLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));

        SpinnerNumberModel sizeModel =
                new SpinnerNumberModel(20, 5, 100, 1);
        JSpinner sizeSpinner = new JSpinner(sizeModel);
        sizeSpinner.setMaximumSize(new Dimension(200, 30));

        // ------------------------
        // Grid Color Picker Button
        // ------------------------
        JButton colorButton = new JButton("Choose Grid Color");
        colorButton.setFocusPainted(false);
        colorButton.setMaximumSize(new Dimension(200, 30));

        // ------------------------
        // Grid Opacity Slider
        // ------------------------
        JLabel opacityLabel = new JLabel("Grid Opacity:");
        opacityLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JSlider opacitySlider = new JSlider(0, 100, 60);
        opacitySlider.setMaximumSize(new Dimension(200, 40));
        opacitySlider.setMajorTickSpacing(20);
        opacitySlider.setPaintTicks(true);

        // ------------------------
        // Apply Button
        // ------------------------
        JButton applyButton = new JButton("Apply Grid to Canvas");
        applyButton.setFocusPainted(false);
        applyButton.setMaximumSize(new Dimension(200, 30));

        // ===== Add components to panel =====
        content.add(showGrid);
        content.add(Box.createVerticalStrut(8));

        content.add(orientationLabel);
        content.add(verticalGrid);
        content.add(horizontalGrid);
        content.add(noneGrid);
        content.add(Box.createVerticalStrut(10));

        content.add(sizeLabel);
        content.add(sizeSpinner);
        content.add(Box.createVerticalStrut(8));

        content.add(colorButton);
        content.add(Box.createVerticalStrut(8));

        content.add(opacityLabel);
        content.add(opacitySlider);
        content.add(Box.createVerticalStrut(10));

        content.add(applyButton);
        content.add(Box.createVerticalStrut(10));

        // ===== Expand/Collapse Toggle =====
        header.addActionListener(e -> {
            boolean visible = content.isVisible();
            content.setVisible(!visible);
            header.setText((visible ? "▼  " : "▲  ") + "Grid");
            outer.revalidate();
            outer.repaint();
        });

        outer.add(header, BorderLayout.NORTH);
        outer.add(content, BorderLayout.CENTER);

        return outer;
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

        String[] families = { "SansSerif", "Serif", "Monospaced", "Dialog", "Arial" };
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

        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(Color.WHITE);
        outer.setMaximumSize(new Dimension(250, 350));

        JButton header = new JButton("▼  Colour Palette");
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
        // TEXT COLOR PICKER
        // ------------------------
        JButton textColorButton = new JButton("Choose Text Colour");
        textColorButton.setFocusPainted(false);
        textColorButton.setMaximumSize(new Dimension(200, 30));

        JLabel textColorPreview = new JLabel("Text Colour Preview");
        textColorPreview.setOpaque(true);
        textColorPreview.setBackground(Color.BLACK);
        textColorPreview.setForeground(Color.WHITE);
        textColorPreview.setFont(new Font("SansSerif", Font.PLAIN, 12));
        textColorPreview.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
        textColorPreview.setMaximumSize(new Dimension(200, 30));

        // ------------------------
        // BACKGROUND COLOR PICKER
        // ------------------------
        JButton backgroundColorButton = new JButton("Choose Background Colour");
        backgroundColorButton.setFocusPainted(false);
        backgroundColorButton.setMaximumSize(new Dimension(200, 30));

        JLabel bgColorPreview = new JLabel("Background Preview");
        bgColorPreview.setOpaque(true);
        bgColorPreview.setBackground(Color.WHITE);
        bgColorPreview.setFont(new Font("SansSerif", Font.PLAIN, 12));
        bgColorPreview.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
        bgColorPreview.setMaximumSize(new Dimension(200, 30));

        // ------------------------
        // PRESET SWATCH COLORS
        // ------------------------
        JLabel swatchLabel = new JLabel("Preset Colours:");
        swatchLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JPanel swatchPanel = new JPanel();
        swatchPanel.setLayout(new GridLayout(2, 6, 5, 5));
        swatchPanel.setMaximumSize(new Dimension(220, 60));
        swatchPanel.setBackground(Color.WHITE);

        Color[] swatches = {
                new Color(0,0,0), new Color(255,255,255),
                new Color(255,0,0), new Color(0,128,0),
                new Color(0,0,255), new Color(255,165,0),
                new Color(128,0,128), new Color(0,139,139),
                new Color(255,20,147), new Color(139,69,19),
                new Color(75,0,130), new Color(173,255,47)
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

        // ------------------------
        // APPLY + RESET BUTTONS
        // ------------------------
        JButton applyButton = new JButton("Apply Colours");
        applyButton.setFocusPainted(false);
        applyButton.setMaximumSize(new Dimension(200, 30));

        JButton resetButton = new JButton("Reset Colours");
        resetButton.setFocusPainted(false);
        resetButton.setMaximumSize(new Dimension(200, 30));

        // ===== ACTIONS =====

        textColorButton.addActionListener(e -> {
            Color chosen = JColorChooser.showDialog(null, "Choose Text Colour", Color.BLACK);
            if (chosen != null) {
                textColorPreview.setBackground(chosen);
                textColorPreview.setForeground(chosen.equals(Color.BLACK) ? Color.WHITE : Color.BLACK);
            }
        });

        backgroundColorButton.addActionListener(e -> {
            Color chosen = JColorChooser.showDialog(null, "Choose Background Colour", Color.WHITE);
            if (chosen != null) {
                bgColorPreview.setBackground(chosen);
            }
        });

        resetButton.addActionListener(e -> {
            textColorPreview.setBackground(Color.BLACK);
            textColorPreview.setForeground(Color.WHITE);
            bgColorPreview.setBackground(Color.WHITE);
        });

        // ===== ADD COMPONENTS =====
        content.add(textColorButton);
        content.add(textColorPreview);
        content.add(Box.createVerticalStrut(10));

        content.add(backgroundColorButton);
        content.add(bgColorPreview);
        content.add(Box.createVerticalStrut(10));

        content.add(swatchLabel);
        content.add(swatchPanel);
        content.add(Box.createVerticalStrut(10));

        content.add(applyButton);
        content.add(resetButton);
        content.add(Box.createVerticalStrut(10));

        // ===== EXPAND/COLLAPSE =====
        header.addActionListener(e -> {
            boolean visible = content.isVisible();
            content.setVisible(!visible);
            header.setText((visible ? "▼  " : "▲  ") + "Colour Palette");
            outer.revalidate();
            outer.repaint();
        });

        outer.add(header, BorderLayout.NORTH);
        outer.add(content, BorderLayout.CENTER);

        return outer;
    }
    // ============================================================
// CALCULATIONS SECTION (UI ONLY – ranges, ops, problem count)
// ============================================================
    public static JPanel calculationsSection() {

        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(Color.WHITE);
        outer.setMaximumSize(new Dimension(250, 350));

        JButton header = new JButton("▼  Calculations");
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
        // NUMBER RANGE CHECKBOXES
        // ------------------------
        JLabel rangeLabel = new JLabel("Number Range:");
        rangeLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JCheckBox range20 = new JCheckBox("1 - 20");
        JCheckBox range50 = new JCheckBox("1 - 50");
        JCheckBox range100 = new JCheckBox("1 - 100");

        range20.setBackground(Color.WHITE);
        range50.setBackground(Color.WHITE);
        range100.setBackground(Color.WHITE);

        range20.setFont(new Font("SansSerif", Font.PLAIN, 12));
        range50.setFont(new Font("SansSerif", Font.PLAIN, 12));
        range100.setFont(new Font("SansSerif", Font.PLAIN, 12));

        // ------------------------
        // PROBLEM COUNT SPINNER
        // ------------------------
        JLabel problemLabel = new JLabel("How many problems?");
        problemLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));

        SpinnerNumberModel problemModel =
                new SpinnerNumberModel(10, 1, 50, 1);
        JSpinner problemSpinner = new JSpinner(problemModel);
        problemSpinner.setMaximumSize(new Dimension(200, 30));

        // ------------------------
        // OPERATION OPTIONS
        // ------------------------
        JLabel opsLabel = new JLabel("Operations:");
        opsLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JCheckBox addOp = new JCheckBox("Addition (+)");
        JCheckBox subOp = new JCheckBox("Subtraction (−)");
        JCheckBox mulOp = new JCheckBox("Multiplication (×)");
        JCheckBox divOp = new JCheckBox("Division (÷)");

        addOp.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subOp.setFont(new Font("SansSerif", Font.PLAIN, 12));
        mulOp.setFont(new Font("SansSerif", Font.PLAIN, 12));
        divOp.setFont(new Font("SansSerif", Font.PLAIN, 12));

        addOp.setBackground(Color.WHITE);
        subOp.setBackground(Color.WHITE);
        mulOp.setBackground(Color.WHITE);
        divOp.setBackground(Color.WHITE);

        // ------------------------
        // GENERATE BUTTON (UI ONLY)
        // ------------------------
        JButton generateButton = new JButton("Generate Problems");
        generateButton.setFocusPainted(false);
        generateButton.setMaximumSize(new Dimension(200, 30));

        // ===== ACTION PLACEHOLDER =====
        generateButton.addActionListener(e -> {
            // UI only — no functionality yet
            JOptionPane.showMessageDialog(null,
                    "This will generate math problems (feature coming later).");
        });

        // ===== ADD COMPONENTS =====
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

        // ===== EXPAND / COLLAPSE =====
        header.addActionListener(e -> {
            boolean visible = content.isVisible();
            content.setVisible(!visible);
            header.setText((visible ? "▼  " : "▲  ") + "Calculations");
            outer.revalidate();
            outer.repaint();
        });

        outer.add(header, BorderLayout.NORTH);
        outer.add(content, BorderLayout.CENTER);

        return outer;
    }
    // ============================================================
// QUICK FILL SECTION (UI ONLY – BUTTON GRID LIKE SAMPLE)
// ============================================================
    public static JPanel quickFillSection() {

        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(Color.WHITE);
        outer.setMaximumSize(new Dimension(250, 500));

        JButton header = new JButton("▼  Quick Fill");
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

        // ===== Two-column button panel =====
        JPanel grid = new JPanel(new GridLayout(0, 2, 10, 10));
        grid.setBackground(Color.WHITE);
        grid.setMaximumSize(new Dimension(230, 350));

        // Gradient button creator (LIGHT BLUE)
        java.util.function.Function<String, JButton> makeButton = (text) -> {
            JButton btn = new JButton(text) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    GradientPaint gp = new GradientPaint(
                            0, 0, new Color(150, 200, 255),   // light blue top
                            getWidth(), getHeight(), new Color(90, 130, 255) // deeper blue bottom
                    );

                    g2.setPaint(gp);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                    super.paintComponent(g);
                }
            };

            btn.setFont(new Font("SansSerif", Font.BOLD, 12));
            btn.setForeground(Color.WHITE);
            btn.setOpaque(false);
            btn.setFocusPainted(false);
            btn.setContentAreaFilled(false);
            btn.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
            return btn;
        };


        // ===== ADD BUTTONS =====
        grid.add(makeButton.apply("A-Z"));
        grid.add(makeButton.apply("a-z"));

        grid.add(makeButton.apply("1-20"));
        grid.add(makeButton.apply("Sight Words"));

        grid.add(makeButton.apply("Colors"));
        grid.add(makeButton.apply("Animals"));

        grid.add(makeButton.apply("CVC Words"));
        grid.add(makeButton.apply("Shapes"));

        grid.add(makeButton.apply("Addition"));
        grid.add(makeButton.apply("Subtraction"));

        grid.add(makeButton.apply("Count 1-10"));
        grid.add(new JLabel()); // empty to keep grid aligned

        content.add(Box.createVerticalStrut(10));
        content.add(grid);
        content.add(Box.createVerticalStrut(10));

        // ===== EXPAND / COLLAPSE =====
        header.addActionListener(e -> {
            boolean visible = content.isVisible();
            content.setVisible(!visible);
            header.setText((visible ? "▼  " : "▲  ") + "Quick Fill");
            outer.revalidate();
            outer.repaint();
        });

        outer.add(header, BorderLayout.NORTH);
        outer.add(content, BorderLayout.CENTER);

        return outer;
    }
    // ============================================================
// TEMPLATE LAYOUT SECTION (UI ONLY – 2-COLUMN TEMPLATE BUTTONS)
// ============================================================
    public static JPanel templateSection() {

        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(Color.WHITE);
        outer.setMaximumSize(new Dimension(250, 420));

        JButton header = new JButton("▼  Template Layouts");
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

        // ===== 2-COLUMN GRID =====
        JPanel grid = new JPanel(new GridLayout(0, 2, 10, 10));
        grid.setBackground(Color.WHITE);
        grid.setMaximumSize(new Dimension(230, 350));

        // LIGHT BLUE BUTTON BUILDER
        java.util.function.Function<String, JButton> makeButton = (text) -> {
            JButton btn = new JButton(text) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    GradientPaint gp = new GradientPaint(
                            0, 0, new Color(150, 200, 255),     // light blue
                            getWidth(), getHeight(), new Color(90, 130, 255) // blue
                    );

                    g2.setPaint(gp);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);

                    super.paintComponent(g);
                }
            };

            btn.setFont(new Font("SansSerif", Font.BOLD, 12));
            btn.setForeground(Color.WHITE);
            btn.setOpaque(false);
            btn.setFocusPainted(false);
            btn.setContentAreaFilled(false);
            btn.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
            return btn;
        };

        // ===== TEMPLATE OPTIONS =====
        grid.add(makeButton.apply("Basic"));
        grid.add(makeButton.apply("Lined"));

        grid.add(makeButton.apply("Graph"));
        grid.add(makeButton.apply("Handwriting"));

        grid.add(makeButton.apply("Math Grid"));
        grid.add(makeButton.apply("Table"));

        grid.add(makeButton.apply("Flashcards"));
        grid.add(makeButton.apply("Blank"));

        content.add(Box.createVerticalStrut(10));
        content.add(grid);
        content.add(Box.createVerticalStrut(10));

        // ===== EXPAND/COLLAPSE =====
        header.addActionListener(e -> {
            boolean visible = content.isVisible();
            content.setVisible(!visible);
            header.setText((visible ? "▼  " : "▲  ") + "Template Layouts");
            outer.revalidate();
            outer.repaint();
        });

        outer.add(header, BorderLayout.NORTH);
        outer.add(content, BorderLayout.CENTER);

        return outer;
    }

    // ============================================================
// GENERIC COLLAPSIBLE SECTION (NOW WORKING LIKE STUDENT DETAILS)
// ============================================================
    public static JPanel section(String title) {

        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(Color.WHITE);
        outer.setMaximumSize(new Dimension(250, 200));

        JButton header = new JButton("▼  " + title);
        header.setFont(new Font("SansSerif", Font.BOLD, 14));
        header.setFocusPainted(false);
        header.setContentAreaFilled(false);
        header.setBorderPainted(false);
        header.setHorizontalAlignment(SwingConstants.LEFT);

        // CONTENT PANEL (collapsed by default)
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Color.WHITE);
        content.setVisible(false);

        // Placeholder content so expansion works visually.
        // You can replace this later with actual controls.
        JLabel placeholder = new JLabel("Content for " + title);
        placeholder.setFont(new Font("SansSerif", Font.PLAIN, 12));
        placeholder.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 5));

        content.add(placeholder);
        content.add(Box.createVerticalStrut(10));

        // Toggle behaviour
        header.addActionListener(e -> {
            boolean visible = content.isVisible();
            content.setVisible(!visible);
            header.setText((visible ? "▼  " : "▲  ") + title);
            outer.revalidate();
            outer.repaint();
        });

        outer.add(header, BorderLayout.NORTH);
        outer.add(content, BorderLayout.CENTER);

        return outer;
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