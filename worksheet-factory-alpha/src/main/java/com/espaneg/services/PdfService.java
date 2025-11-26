package com.espaneg.services;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class PdfService {

    private static PDDocument document;
    private static PDRectangle PAGE_SIZE = PDRectangle.A4;
    private static float MARGIN = 36f; // 0.5 inch margin

    // =====================================================
    // START PDF
    // =====================================================
    public static void startPDF(String path) {
        document = new PDDocument();
    }

    // =====================================================
    // EXPORT A SWING PAGE INTO THE PDF
    // =====================================================
    public static void exportPanelPage(JPanel page) {
        try {

            page.setSize(page.getPreferredSize());
            page.doLayout();
            page.validate();

            // Determine panel size
            Dimension pref = page.getPreferredSize();
            int w = pref.width > 0 ? pref.width : Math.max(1000, page.getWidth());
            int h = pref.height > 0 ? pref.height : Math.max(1400, page.getHeight());

            // SAFETY fallback
            if (w <= 0) w = 1000;
            if (h <= 0) h = 1400;

            // ============================================
            // RENDER SWING PAGE TO IMAGE
            // ============================================
            BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = img.createGraphics();
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, w, h);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            page.printAll(g2);
            g2.dispose();

            // ============================================
            // CREATE PDF PAGE
            // ============================================
            PDPage pdfPage = new PDPage(PAGE_SIZE);
            document.addPage(pdfPage);

            PDImageXObject pdImage = LosslessFactory.createFromImage(document, img);

            float pageW = PAGE_SIZE.getWidth();
            float pageH = PAGE_SIZE.getHeight();

            float availableW = pageW - MARGIN * 2;
            float availableH = pageH - MARGIN * 2;

            double scaleX = availableW / w;
            double scaleY = availableH / h;
            double scale = Math.min(scaleX, scaleY);

            float drawW = (float) (w * scale);
            float drawH = (float) (h * scale);

            float x = (pageW - drawW) / 2f;
            float y = (pageH - drawH) / 2f;

            // ============================================
            // DRAW IMAGE INTO PDF
            // ============================================
            PDPageContentStream cs = new PDPageContentStream(document, pdfPage);
            cs.drawImage(pdImage, x, y, drawW, drawH);
            cs.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // =====================================================
    // FINISH PDF
    // =====================================================
    public static void finishPDF(String path) {
        try {
            document.save(path);
            document.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
