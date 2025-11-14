package com.espaneg.model;

public class WorksheetSettings {

    private final String studentName;
    private final String worksheetType;
    private final int fontSize;

    public WorksheetSettings(String name, String type, int size) {
        this.studentName = name;
        this.worksheetType = type;
        this.fontSize = size;
    }

    // Getters required by WorksheetService
    public String getStudentName() {
        return studentName;
    }

    public String getWorksheetType() {
        return worksheetType;
    }

    public int getFontSize() {
        return fontSize;
    }
}

