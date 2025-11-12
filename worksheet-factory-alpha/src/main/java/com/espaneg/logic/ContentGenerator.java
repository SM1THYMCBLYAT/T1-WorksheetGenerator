package com.espaneg.logic;

import com.espaneg.model.WorksheetSettings;

import javax.swing.*;
import java.awt.*;

public class ContentGenerator extends JPanel {

    // Fields must be instance variables to retrieve values later
    private final JTextField studentNameField;
    private final JComboBox<String> worksheetTypeBox;
    private final JSlider fontSizeSlider;
    private final JButton generateBtn;

    public ContentGenerator() {
        setLayout(new GridLayout(0, 1, 10, 10));
        setBorder(BorderFactory.createTitledBorder("Worksheet Controls"));

        // Initialize instance variables
        studentNameField = new JTextField();
        add(new JLabel("Student Name:"));
        add(studentNameField);

        worksheetTypeBox = new JComboBox<>(new String[]{"Tracing Letters", "Math Problems", "Counting"});
        add(new JLabel("Worksheet Type:"));
        add(worksheetTypeBox);

        fontSizeSlider = new JSlider(12, 48, 24);
        fontSizeSlider.setMajorTickSpacing(12);
        fontSizeSlider.setPaintTicks(true);
        fontSizeSlider.setPaintLabels(true);
        add(new JLabel("Font Size:"));
        add(fontSizeSlider);

        generateBtn = new JButton("Generate Worksheet");
        // Removed the temporary ActionListener here, it will be added by the main application
        add(generateBtn);
    }


    public WorksheetSettings getSettings() {
        String name = studentNameField.getText();
        String type = (String) worksheetTypeBox.getSelectedItem();
        int size = fontSizeSlider.getValue();
        return new WorksheetSettings(name, type, size);
    }


    public JButton getGenerateButton() {
        return generateBtn;
    }
}