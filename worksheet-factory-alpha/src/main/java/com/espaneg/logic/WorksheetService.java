package com.espaneg.logic;

import com.espaneg.model.WorksheetSettings;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class responsible for generating the actual content items
 * for a worksheet based on the user's settings.
 */
public class WorksheetService {

    /**
     * Generates a list of string content items for the worksheet.
     *
     * @param settings The WorksheetSettings object containing configuration data.
     * @param count The number of content items to generate.
     * @return A List of Strings, where each string is a generated worksheet item.
     */
    public List<String> generateContent(WorksheetSettings settings, int count) {
        List<String> contentList = new ArrayList<>();
        String baseTypeDescription;


        // Grid size is a visual dimension, not a content type.
        String worksheetType = settings.getWorksheetType();

        // Determine the base description based on the worksheet type
        switch (worksheetType) {
            case "Tracing Letters":
                baseTypeDescription = "Trace ABC for: ";
                break;
            case "Math Problems":
                // Use fontSize (proxy for difficulty) for dynamic content
                baseTypeDescription = "Level " + settings.getFontSize() + " Math Problem ";
                break;
            case "Counting":
                baseTypeDescription = "Count the stars ";
                break;
            default:
                baseTypeDescription = "Worksheet Item ";
                break;
        }

        // --- Grid Property Influence  ---
        String gridSuffix = "";
        if (settings.isShowGrid() && settings.isGridVertical()) {
            gridSuffix = " (Grid size " + settings.getGridSize() + "px)";
        }
        // ---------------------------------------------------------

        // Generate the specified number of content items
        for (int i = 1; i <= count; i++) {
            String item = baseTypeDescription + i + gridSuffix; // Add grid suffix

            // Personalize the content using the student name
            String name = settings.getStudentName();
            if (name != null && !name.isEmpty()) {
                item += " (for " + name + ")";
            }
            contentList.add(item);
        }

        return contentList;
    }
}