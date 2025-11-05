package com.example.pdf2word;

import java.io.IOException;

public class Pdt2WordTest {

    public static void main(String[] args) throws  IOException {
        try {
            EnhancedPdfToWordConverter.convertPdfToWordWithImagesAndTables("D:/img/060fabea-1177-43aa-915f-af9058b9b3de.pdf", "D:/img/1.docx");
            System.out.println("PDF转换Word成功！");
        } catch (IOException e) {
            System.err.println("转换失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
