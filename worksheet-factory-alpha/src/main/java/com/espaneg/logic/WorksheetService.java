package com.espaneg.logic;

import com.espaneg.model.WorksheetSettings;

import java.util.ArrayList;
import java.util.List;

public class WorksheetService {

    public List<String> generateContent(WorksheetSettings settings, int count) {
        List<String> contentList = new ArrayList<>();
        String baseTypeDescription;

        // Uses data from the POJO to determine content
        switch (settings.getWorksheetType()) {
            case "Tracing Letters":
                baseTypeDescription = "Trace ABC for: ";
                break;
            case "Math Problems":
                baseTypeDescription = "Math Problem ";
                break;
            case "Counting":
                baseTypeDescription = "Count the stars ";
                break;
            default:
                baseTypeDescription = "Worksheet Item ";
                break;
        }

        
        for (int i = 1; i <= count; i++) {
            String item = String.format("%s%d - Name: %s (Type: %s, Size: %d)",
                    baseTypeDescription, i,
                    settings.getStudentName(),
                    settings.getWorksheetType(),
                    settings.getFontSize());
            contentList.add(item);
        }

        return contentList;
    }
}
