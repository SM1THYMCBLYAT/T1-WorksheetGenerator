package com.espaneg.services;

import com.espaneg.model.WorksheetSettings;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * PdfService - Student 4 (PDF Expert)
 * Handles all PDF generation for Worksheet Factory
 *
 * Compatible with Apache PDFBox 2.x (e.g. 2.0.31)
 */
public class PdfService {

    private static final float MARGIN = 40f;
    private static final float TITLE_SPACING = 40f;
    private static final float LINE_SPACING = 8f;

    // ============= MAIN PUBLIC METHOD ====================
    public void generateWorksheet(List<String> lines, WorksheetSettings settings, String outputPath)
            throws IOException {

        // === VALIDATION ===
        if (lines == null || settings == null) {
            throw new IllegalArgumentException("Settings or content list is null");
        }
        if (lines.isEmpty()) {
            throw new IllegalArgumentException("Content list is empty");
        }

        try (PDDocument doc = new PDDocument()) {

            // === SETUP FIRST PAGE ===
            PDPage page = new PDPage(PDRectangle.LETTER);
            doc.addPage(page);
            PDRectangle rect = page.getMediaBox();

            float pageWidth = rect.getWidth();
            float pageHeight = rect.getHeight();
            float usableWidth = pageWidth - (2 * MARGIN);

            PDFont font = selectFont(settings);
            float fontSize = settings.getFontSize();
            float lineHeight = fontSize + LINE_SPACING;

            // create first content stream for the first page
            PDPageContentStream cs = new PDPageContentStream(doc, page);

            try {
                // === DRAW BACKGROUND ===
                drawBackground(cs, rect, settings.getBackgroundColor());

                // === DRAW GRID (if enabled) ===
                if (settings.isShowGrid()) {
                    drawGrid(cs, rect, settings);
                }

                // === STARTING POSITION ===
                float currentY = pageHeight - MARGIN;

                // === DRAW TITLE ===
                if (settings.getWorksheetTitle() != null && !settings.getWorksheetTitle().isEmpty()) {
                    currentY = drawTitle(cs, settings, MARGIN, currentY, usableWidth);
                    currentY -= TITLE_SPACING;
                }

                // === DRAW STUDENT NAME ===
                if (settings.getStudentName() != null && !settings.getStudentName().isEmpty()) {
                    drawTextLine(cs, "Name: " + settings.getStudentName(),
                            MARGIN, currentY, font, fontSize,
                            settings.getTextColor(), "left", usableWidth);
                    currentY -= lineHeight * 1.5f;
                }

                // === DRAW IMPORTED TEXT ===
                if (settings.hasImportedText()) {
                    String[] importedLines = settings.getImportedText().split("\n");
                    for (String impLine : importedLines) {
                        if (currentY < MARGIN + lineHeight) {
                            cs.close();
                            page = createNewPage(doc, settings);
                            // new page content stream
                            cs = new PDPageContentStream(doc, page);
                            currentY = pageHeight - MARGIN;
                        }

                        drawTextLine(cs, impLine, MARGIN, currentY, font, fontSize,
                                settings.getTextColor(), settings.getTextAlignment(), usableWidth);
                        currentY -= lineHeight;
                    }
                    currentY -= lineHeight;
                }

                // === DRAW MAIN CONTENT LINES ===
                Color textColor = settings.isColorMode() ? settings.getTextColor() : Color.BLACK;

                for (String line : lines) {
                    String content = line != null ? line : "";

                    // === CHECK IF NEW PAGE NEEDED ===
                    if (currentY < MARGIN + lineHeight) {
                        cs.close();
                        page = createNewPage(doc, settings);
                        cs = new PDPageContentStream(doc, page);
                        currentY = pageHeight - MARGIN;
                    }

                    drawTextLine(cs, content, MARGIN, currentY, font, fontSize,
                            textColor, settings.getTextAlignment(), usableWidth);

                    // === DRAW UNDERLINE (if enabled) ===
                    if (settings.isFontUnderline()) {
                        float textWidth = (font.getStringWidth(content) / 1000f) * fontSize;
                        float underlineX = calculateAlignmentX(MARGIN, usableWidth, textWidth,
                                settings.getTextAlignment());
                        drawUnderline(cs, underlineX, currentY - 2f, textWidth, textColor);
                    }

                    currentY -= lineHeight;
                }

            } finally {
                // Ensure the content stream is closed
                cs.close();
            }

            // === SAVE FILE ===
            File outputFile = new File(outputPath);
            File parentDir = outputFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            doc.save(outputFile);

            // === SUCCESS MESSAGE ===
            System.out.println("✓ PDF generated successfully!");
            System.out.println("  Location: " + outputPath);
            System.out.println("  Lines: " + lines.size());
            System.out.println("  Pages: " + doc.getNumberOfPages());
        }
    }

    // ============= CREATE NEW PAGE ===================
    private PDPage createNewPage(PDDocument doc, WorksheetSettings settings) throws IOException {
        PDPage newPage = new PDPage(PDRectangle.LETTER);
        doc.addPage(newPage);

        PDRectangle rect = newPage.getMediaBox();
        // draw background and grid immediately on the new page
        try (PDPageContentStream tempCs = new PDPageContentStream(doc, newPage)) {
            drawBackground(tempCs, rect, settings.getBackgroundColor());
            if (settings.isShowGrid()) {
                drawGrid(tempCs, rect, settings);
            }
        }

        return newPage;
    }

    // ============= DRAW SINGLE LINE (text only) ===================
    private void drawTextLine(PDPageContentStream cs, String text,
                              float startX, float yPosition,
                              PDFont font, float fontSize,
                              Color color, String alignment,
                              float usableWidth) throws IOException {

        cs.setNonStrokingColor(color);

        // protect against null text
        if (text == null) text = "";

        float textWidth = (font.getStringWidth(text) / 1000f) * fontSize;
        float xPosition = calculateAlignmentX(startX, usableWidth, textWidth, alignment);

        cs.beginText();
        cs.setFont(font, fontSize);
        cs.newLineAtOffset(xPosition, yPosition);
        cs.showText(text);
        cs.endText();
    }

    // ============= CALCULATE X POSITION FOR ALIGNMENT ===================
    private float calculateAlignmentX(float startX, float usableWidth,
                                      float textWidth, String alignment) {

        String align = alignment != null ? alignment.toLowerCase() : "left";

        return switch (align) {
            case "center" -> startX + (usableWidth / 2f) - (textWidth / 2f);
            case "right" -> startX + usableWidth - textWidth;
            default -> startX;
        };
    }

    // ============= BACKGROUND COLOR =============
    private void drawBackground(PDPageContentStream cs, PDRectangle rect, Color bg)
            throws IOException {
        if (bg == null) bg = Color.WHITE;
        cs.setNonStrokingColor(bg);
        cs.addRect(0, 0, rect.getWidth(), rect.getHeight());
        cs.fill();
        cs.setNonStrokingColor(Color.BLACK); // reset to default text color
    }

    // ============= TITLE DRAWING =================
    private float drawTitle(PDPageContentStream cs, WorksheetSettings settings,
                            float startX, float startY, float usableWidth) throws IOException {

        String title = settings.getWorksheetTitle() != null ? settings.getWorksheetTitle() : "";
        float titleFontSize = settings.getFontSize() + 6;

        PDFont titleFont = PDType1Font.HELVETICA_BOLD;

        float titleWidth = (titleFont.getStringWidth(title) / 1000f) * titleFontSize;
        float titleX = startX + (usableWidth / 2f) - (titleWidth / 2f);

        cs.beginText();
        cs.setFont(titleFont, titleFontSize);
        cs.setNonStrokingColor(settings.getTextColor() != null ? settings.getTextColor() : Color.BLACK);
        cs.newLineAtOffset(titleX, startY);
        cs.showText(title);
        cs.endText();

        // return the Y position (unchanged) - calling code subtracts spacing
        return startY;
    }

    // ============= GRID DRAWING ==================
    private void drawGrid(PDPageContentStream cs, PDRectangle rect, WorksheetSettings settings)
            throws IOException {

        Color gridColor = settings.getGridColor() != null ? settings.getGridColor() : new Color(200, 200, 200);
        float opacity = Math.max(0f, Math.min(1f, settings.getGridOpacity() / 100f));
        float r = gridColor.getRed() / 255f;
        float g = gridColor.getGreen() / 255f;
        float b = gridColor.getBlue() / 255f;

        // note: PDFBox 2.x supports setStrokingColor(float,float,float)
        cs.setStrokingColor(r * opacity, g * opacity, b * opacity);
        cs.setLineWidth(0.5f);

        // vertical lines
        if (settings.isGridVertical()) {
            for (float x = 0; x <= rect.getWidth(); x += settings.getGridSize()) {
                cs.moveTo(x, 0);
                cs.lineTo(x, rect.getHeight());
            }
        }

        // horizontal lines
        if (settings.isGridHorizontal()) {
            for (float y = 0; y <= rect.getHeight(); y += settings.getGridSize()) {
                cs.moveTo(0, y);
                cs.lineTo(rect.getWidth(), y);
            }
        }

        cs.stroke();
    }

    // ============= UNDERLINE DRAWING ==================
    private void drawUnderline(PDPageContentStream cs, float x, float y,
                               float width, Color color) throws IOException {

        cs.setStrokingColor(color != null ? color : Color.BLACK);
        cs.setLineWidth(1f);
        cs.moveTo(x, y);
        cs.lineTo(x + width, y);
        cs.stroke();
    }

    // ============= FONT SELECTION ==================
    private PDFont selectFont(WorksheetSettings settings) {
        String family = settings.getFontFamily() != null ? settings.getFontFamily().toLowerCase() : "";
        boolean bold = settings.isFontBold();
        boolean italic = settings.isFontItalic();

        // Serif fonts (Times)
        if (family.contains("serif") && !family.contains("sans")) {
            if (bold && italic) return PDType1Font.TIMES_BOLD_ITALIC;
            if (bold) return PDType1Font.TIMES_BOLD;
            if (italic) return PDType1Font.TIMES_ITALIC;
            return PDType1Font.TIMES_ROMAN;
        }

        // Monospace fonts (Courier)
        if (family.contains("mono") || family.contains("courier")) {
            if (bold && italic) return PDType1Font.COURIER_BOLD_OBLIQUE;
            if (bold) return PDType1Font.COURIER_BOLD;
            if (italic) return PDType1Font.COURIER_OBLIQUE;
            return PDType1Font.COURIER;
        }

        // Default to Helvetica (sans)
        if (bold && italic) return PDType1Font.HELVETICA_BOLD_OBLIQUE;
        if (bold) return PDType1Font.HELVETICA_BOLD;
        if (italic) return PDType1Font.HELVETICA_OBLIQUE;
        return PDType1Font.HELVETICA;
    }

    // ============= UTILITY METHODS ====================

    public int calculateLinesPerPage(int fontSize) {
        float pageHeight = PDRectangle.LETTER.getHeight();
        float lineHeight = fontSize + LINE_SPACING;
        float usableHeight = pageHeight - (2 * MARGIN) - TITLE_SPACING;
        return Math.max(5, (int) (usableHeight / lineHeight));
    }

    public int estimatePageCount(int totalLines, int fontSize) {
        int linesPerPage = calculateLinesPerPage(fontSize);
        return (int) Math.ceil((double) totalLines / linesPerPage);
    }
}
