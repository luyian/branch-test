package com.example.pdf;

import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import java.io.File;
import java.io.IOException;

public class PDFMerger {

    public static void main(String[] args) {
        // 获取桌面路径
        String desktopPath = System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "tmp";
        // 输出PDF文件路径
        String outputFile = System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "merged_invoice.pdf";

        try {
            mergePDFs(desktopPath, outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void mergePDFs(String inputFolderPath, String outputFile) throws IOException {
        PDFMergerUtility merger = new PDFMergerUtility();
        merger.setDestinationFileName(outputFile);

        // 获取输入文件夹下的所有PDF文件
        File folder = new File(inputFolderPath);
        File[] listOfFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));

        if (listOfFiles == null || listOfFiles.length == 0) {
            System.out.println("No PDF files found in the specified folder.");
            return;
        }

        for (File file : listOfFiles) {
            merger.addSource(file);
        }

        merger.mergeDocuments(null);

        // 打开合并后的文档以设置纸张大小为A5纵向
        try (PDDocument document = PDDocument.load(new File(outputFile))) {
            PDRectangle a5Landscape = PDRectangle.A5; // 默认横向
            PDRectangle a5Portrait = new PDRectangle(a5Landscape.getHeight(), a5Landscape.getWidth()); // 转换为纵向

            for (PDPage page : document.getPages()) {
                page.setMediaBox(a5Portrait);
            }
            document.save(outputFile);
        }
    }
}
