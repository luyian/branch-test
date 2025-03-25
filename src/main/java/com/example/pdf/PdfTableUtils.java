package com.example.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;

import java.io.IOException;
import java.util.List;

public class PdfTableUtils {

    private static final float MARGIN = 50;
    private static final float ROW_HEIGHT = 20;
    private static final float TABLE_WIDTH = PDRectangle.A4.getWidth() - 2 * MARGIN;
    private static final float HEADER_HEIGHT = 30;

    public static void createTable(PDDocument document, PDPageContentStream contentStream, PDType0Font font, List<String[]> data) throws IOException {
        float yStart = PDRectangle.A4.getHeight() - MARGIN;
        float yPosition = yStart;

        // Draw header background
        PDColor headerBackgroundColor = new PDColor(new float[]{0.8f, 0.8f, 0.8f}, PDDeviceRGB.INSTANCE);
        contentStream.setNonStrokingColor(headerBackgroundColor);
        contentStream.addRect(MARGIN, yPosition, TABLE_WIDTH, HEADER_HEIGHT);
        contentStream.fill();

        // Draw header text
        contentStream.setFont(font, 12);
        contentStream.setNonStrokingColor(0, 0, 0);
        yPosition -= 15; // Center the text vertically in the header
        drawCenteredText(contentStream, font, "姓名", MARGIN, yPosition, TABLE_WIDTH / 3);
        drawCenteredText(contentStream, font, "科目", MARGIN + TABLE_WIDTH / 3, yPosition, TABLE_WIDTH / 3);
        drawCenteredText(contentStream, font, "成绩", MARGIN + 2 * TABLE_WIDTH / 3, yPosition, TABLE_WIDTH / 3);

        // Reset yPosition for rows
        yPosition = yStart - HEADER_HEIGHT;

        // Draw rows
        for (String[] row : data) {
            yPosition -= ROW_HEIGHT;
            if (yPosition < MARGIN) {
                // Add new page if needed
                pageBreak(document, contentStream);
                yPosition = yStart - HEADER_HEIGHT;
            }
            drawRow(contentStream, font, row, yPosition);
        }
    }

    private static void drawRow(PDPageContentStream contentStream, PDType0Font font, String[] row, float yPosition) throws IOException {
        contentStream.setFont(font, 10);
        contentStream.setNonStrokingColor(0, 0, 0);
        drawCenteredText(contentStream, font, row[0], MARGIN, yPosition, TABLE_WIDTH / 3);
        drawCenteredText(contentStream, font, row[1], MARGIN + TABLE_WIDTH / 3, yPosition, TABLE_WIDTH / 3);
        drawCenteredText(contentStream, font, row[2], MARGIN + 2 * TABLE_WIDTH / 3, yPosition, TABLE_WIDTH / 3);
    }

    private static void drawCenteredText(PDPageContentStream contentStream, PDType0Font font, String text, float x, float y, float width) throws IOException {
        float fontSize = 10; // Font size for rows
        float textWidth = font.getStringWidth(text) / 1000 * fontSize;
        contentStream.beginText();
        contentStream.newLineAtOffset(x + (width - textWidth) / 2, y - 5); // Adjust y for vertical centering
        contentStream.showText(text);
        contentStream.endText();
    }

    private static void pageBreak(PDDocument document, PDPageContentStream contentStream) throws IOException {
        contentStream.close();
        PDPage newPage = new PDPage();
        document.addPage(newPage);
        contentStream = new PDPageContentStream(document, newPage);
    }
}
