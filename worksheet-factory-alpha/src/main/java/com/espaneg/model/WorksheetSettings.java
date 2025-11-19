package com.espaneg.model;

import java.awt.Color;
import java.awt.Font;

public class WorksheetSettings {

    // --- Grid Properties ---
    private boolean showGrid = false;
    private boolean gridVertical = true;
    private boolean gridHorizontal = false;
    private int gridSize = 20;
    private Color gridColor = new Color(229, 20, 20);
    private float gridOpacity = 60.0f;

    // --- Font Properties ---
    private String fontFamily = "SansSerif";
    private int fontSize = 16;
    private boolean fontBold = false;
    private boolean fontItalic = false;
    private boolean fontUnderline = false;
    private String textAlignment = "left";
    private Color textColor = Color.BLACK;
    private Color backgroundColor = Color.WHITE;

    // --- Import Content Properties ---
    private String logoPath = null;
    private java.util.List<String> imagePaths = new java.util.ArrayList<>();
    private String importedText = "";
    private boolean isColorMode = true;

    // --- Other Settings ---
    private String worksheetTitle = "New Worksheet";
    private String worksheetType = "Tracing Letters";
    private String studentName = "";

    public WorksheetSettings(String name, String type, int size) {
        this.studentName = name;
        this.worksheetType = type;
        this.fontSize = size;
    }

    // ------------------------------------------------
    // GRID METHODS
    // ------------------------------------------------

    public void updateGridSettings(boolean show, boolean v, boolean h, int size, Color color, float opacity) {
        this.showGrid = show;
        this.gridVertical = v;
        this.gridHorizontal = h;
        this.gridSize = size;
        this.gridColor = color;
        this.gridOpacity = opacity;
    }

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
        return gridColor;
    }

    public float getGridOpacity() {
        return gridOpacity;
    }

    // ------------------------------------------------
    // FONT METHODS
    // ------------------------------------------------

    public void updateFontSettings(String family, int size, boolean bold, boolean italic,
                                   boolean underline, String alignment) {
        this.fontFamily = family;
        this.fontSize = size;
        this.fontBold = bold;
        this.fontItalic = italic;
        this.fontUnderline = underline;
        this.textAlignment = alignment;
    }

    public String getFontFamily() {
        return fontFamily;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public boolean isFontBold() {
        return fontBold;
    }

    public boolean isFontItalic() {
        return fontItalic;
    }

    public boolean isFontUnderline() {
        return fontUnderline;
    }

    public String getTextAlignment() {
        return textAlignment;
    }

    public void setTextAlignment(String alignment) {
        this.textAlignment = alignment;
    }

    public Font getFont() {
        int style = Font.PLAIN;
        if (fontBold) style |= Font.BOLD;
        if (fontItalic) style |= Font.ITALIC;
        return new Font(fontFamily, style, fontSize);
    }

    // ------------------------------------------------
    // COLOR METHODS (ENHANCED FOR COLOUR PALETTE)
    // ------------------------------------------------

    /**
     * Update both text and background colors
     */
    public void updateColorSettings(Color textColor, Color backgroundColor) {
        this.textColor = textColor;
        this.backgroundColor = backgroundColor;
    }

    public Color getTextColor() {
        return textColor;
    }

    public void setTextColor(Color textColor) {
        this.textColor = textColor;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    // ------------------------------------------------
    // IMPORT CONTENT METHODS
    // ------------------------------------------------

    public void updateImportSettings(String logo, java.util.List<String> images,
                                     String text, boolean colorMode) {
        this.logoPath = logo;
        this.imagePaths = images != null ? new java.util.ArrayList<>(images) : new java.util.ArrayList<>();
        this.importedText = text;
        this.isColorMode = colorMode;
    }

    public String getLogoPath() {
        return logoPath;
    }

    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
    }

    public java.util.List<String> getImagePaths() {
        return imagePaths;
    }

    public void setImagePaths(java.util.List<String> imagePaths) {
        this.imagePaths = imagePaths;
    }

    public void addImagePath(String path) {
        this.imagePaths.add(path);
    }

    public void clearImages() {
        this.imagePaths.clear();
    }

    public String getImportedText() {
        return importedText;
    }

    public void setImportedText(String importedText) {
        this.importedText = importedText;
    }

    public boolean isColorMode() {
        return isColorMode;
    }

    public void setColorMode(boolean colorMode) {
        this.isColorMode = colorMode;
    }

    public boolean hasLogo() {
        return logoPath != null && !logoPath.isEmpty();
    }

    public boolean hasImages() {
        return imagePaths != null && !imagePaths.isEmpty();
    }

    public boolean hasImportedText() {
        return importedText != null && !importedText.isEmpty();
    }

    // ------------------------------------------------
    // OTHER GETTERS & SETTERS
    // ------------------------------------------------

    public String getWorksheetType() {
        return worksheetType;
    }

    public void setWorksheetType(String worksheetType) {
        this.worksheetType = worksheetType;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getWorksheetTitle() {
        return worksheetTitle;
    }

    public void setWorksheetTitle(String worksheetTitle) {
        this.worksheetTitle = worksheetTitle;
    }
}