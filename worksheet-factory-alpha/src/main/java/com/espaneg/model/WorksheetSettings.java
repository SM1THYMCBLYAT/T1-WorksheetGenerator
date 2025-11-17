package com.espaneg.model; // Adjust package as necessary

import java.awt.Color;

public class WorksheetSettings {

    // --- Grid Properties ---
    private boolean showGrid = false;
    private boolean gridVertical = true;
    private boolean gridHorizontal = false;
    private int gridSize = 20; // Default size in pixels
    private Color gridColor = new Color(170, 170, 255); // Default grid color
    private float gridOpacity = 60.0f; // Stored as a percentage (0 to 100)

    // --- Other Settings (Example placeholders) ---
    private int fontSize = 16;
    private String worksheetTitle = "New Worksheet";

    public WorksheetSettings(String name, String type, int size) {
        // Initialize with default values (already done above, but good practice)
    }
    // ------------------------------------------------
    // 1. SETTER FUNCTION
    // ------------------------------------------------


    public void updateGridSettings(boolean show, boolean v, boolean h, int size, Color color, float opacity) {
        this.showGrid = show;
        this.gridVertical = v;
        this.gridHorizontal = h;
        this.gridSize = size;
        this.gridColor = color; // Store the base color
        this.gridOpacity = opacity;
    }

    // ------------------------------------------------
    // 2. GETTER FUNCTIONS
    // ------------------------------------------------

    public boolean isShowGrid() {
        return showGrid;
    }

    public boolean isGridVertical() {
        return gridVertical;
    }

    public boolean isGridHorizontal() {
        return gridHorizontal;
    }

    public int getGridSize() {
        return gridSize;
    }

    public Color getGridColor() {
        // The WorksheetGenerator should handle applying the opacity
        return gridColor;
    }

    public float getGridOpacity() {
        return gridOpacity;
    }
    // Inside WorksheetSettings.java

    private String worksheetType = "Tracing Letters"; // Example default value



    // Add this getter
    public String getWorksheetType() {
        return this.worksheetType;
    }


    public void setWorksheetType(String worksheetType) {
        this.worksheetType = worksheetType;
    }


        private String studentName = "";  // Default to empty string

        public String getStudentName() {
            return this.studentName;
        }

        public void setStudentName(String studentName) {
            this.studentName = studentName;
        }

        // --- Font Size Methods ---
        public int getFontSize() {
            return this.fontSize;
        }

        public void setFontSize(int fontSize) {
            this.fontSize = fontSize;
        }


    }
