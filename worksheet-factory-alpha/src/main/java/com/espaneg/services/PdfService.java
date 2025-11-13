package com.espaneg.services;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.printing.PDFPageable;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
/*
this class is used for business logic
 */
public class PdfService {
    public void createPDF(String text) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 20);
            contentStream.newLineAtOffset(100, 700);
            contentStream.showText(text);
            contentStream.endText();
            contentStream.close();

            document.save("output.pdf");
            System.out.println("PDF created successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Reads a PDF and prints text to console
    public void readPDF(String path) {
        try (PDDocument document = PDDocument.load(new File(path))) {
            org.apache.pdfbox.text.PDFTextStripper stripper = new org.apache.pdfbox.text.PDFTextStripper();
            String text = stripper.getText(document);
            System.out.println("PDF Content:\n" + text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void printPDF(String path) {
        try (PDDocument document = PDDocument.load(new File(path))) {
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPageable(new PDFPageable(document));
            if (job.printDialog()) {
                job.print();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}