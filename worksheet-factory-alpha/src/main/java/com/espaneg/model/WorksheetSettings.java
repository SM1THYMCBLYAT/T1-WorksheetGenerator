package com.espaneg.model;

    public class WorksheetSettings {
        private final String studentName;
        private final String worksheetType;
        private final int fontSize;

        public WorksheetSettings(String studentName, String worksheetType, int fontSize) {
            this.studentName = studentName;
            this.worksheetType = worksheetType;
            this.fontSize = fontSize;
        }

        // Getters
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
