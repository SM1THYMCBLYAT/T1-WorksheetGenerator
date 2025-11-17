package com.espaneg.logic;

import com.espaneg.model.WorksheetSettings;
import java.util.ArrayList;
import java.util.List;

public class WorksheetService {

    public List<String> generateContent(WorksheetSettings settings, int count) {
        List<String> contentList = new ArrayList<>();
        String baseTypeDescription;

        // Determine the base description based on the worksheet type selected in the UI
        switch (settings.getWorksheetType()) {
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

        // Generate the specified number of content items
        for (int i = 1; i <= count; i++) {
            String item = baseTypeDescription + i;

            // Personalize the content if the student name is available
            String name = settings.getStudentName();
            if (name != null && !name.isEmpty()) {
                item += " (for " + name + ")";
            }
            contentList.add(item);
        }

        return contentList;
    }
}