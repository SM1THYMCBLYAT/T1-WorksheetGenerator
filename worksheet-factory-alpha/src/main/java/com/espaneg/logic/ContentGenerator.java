package com.espaneg.logic;

import com.espaneg.model.WorksheetSettings;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JSlider;
import java.util.List;

public class ContentGenerator {

    private final WorksheetService worksheetService;

    // the service when ContentGenerator is created
    public ContentGenerator() {
        this.worksheetService = new WorksheetService();
    }

    // --- Core Setting Retrieval Function ---

    public WorksheetSettings getSettings(
            JTextField studentNameField,
            JComboBox<String> worksheetTypeBox,
            JSlider fontSizeSlider) {

        // Get raw data from UI comp
        String name = studentNameField.getText();

        //handle potential null/type errors
        String type = (String) worksheetTypeBox.getSelectedItem();
        if (type == null) {
            type = "Default"; // Provide a safe default if nothing selected
        }

        int size = fontSizeSlider.getValue();

        // Return the new settings object
        return new WorksheetSettings(name, type, size);
    }
    public List<String> generateWorksheet(WorksheetSettings settings, int itemCount) {
        // the complex generation logic to the WorksheetService
        return worksheetService.generateContent(settings, itemCount);
    }
}