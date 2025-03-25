package com.example.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PdfTest {
    public static void main(String[] args) {
        try (PDDocument document = new PDDocument()) {
            // 创建一个空白页面并将其添加到文档中
            PDPage page = new PDPage();
            document.addPage(page);

            // 创建内容流以在页面上绘制内容
            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            // 开始内容流
            contentStream.beginText();

            // 设置字体和字体大小
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);

            // 设置文本位置
            contentStream.newLineAtOffset(100, 700);

            // 写入文本
            contentStream.showText("Hello, Apache PDFBox!");

            // 结束文本流
            contentStream.endText();

            // 关闭内容流
            contentStream.close();

            // 保存文档
            document.save("HelloWorld.pdf");

            System.out.println("PDF created successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void test01() throws IOException {
        try (PDDocument document = new PDDocument()) {
            // 创建一个空白页面并将其添加到文档中
            PDPage page = new PDPage();
            document.addPage(page);

            // 创建内容流以在页面上绘制内容
            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            // 设置字体和字体大小
            PDType0Font font = PDType0Font.load(document, new File("src/main/resources/static/simfang.ttf"));

            // 设置字体和字体大小
            contentStream.setFont(font, 12);

            // 开始内容流
            contentStream.beginText();
            // 设置标题位置
            contentStream.newLineAtOffset(100, 750);
            contentStream.showText("学生成绩表");

            // 设置字体和字体大小
            contentStream.setFont(font, 10);

            // 定义表格数据
            List<StudentGrade> studentGrades = new ArrayList<>();
            studentGrades.add(new StudentGrade("张三", "数学", 95));
            studentGrades.add(new StudentGrade("李四", "数学", 88));
            studentGrades.add(new StudentGrade("王五", "数学", 76));
            studentGrades.add(new StudentGrade("张三", "英语", 85));
            studentGrades.add(new StudentGrade("李四", "英语", 92));
            studentGrades.add(new StudentGrade("王五", "英语", 88));

            // 设置表格列标题
            contentStream.newLineAtOffset(-100, -30);
            contentStream.showText("姓名");
            contentStream.newLineAtOffset(100, 0);
            contentStream.showText("科目");
            contentStream.newLineAtOffset(100, 0);
            contentStream.showText("成绩");

            // 绘制表格数据
            for (StudentGrade grade : studentGrades) {
                contentStream.newLineAtOffset(-200, -20);
                contentStream.showText(grade.getName());
                contentStream.newLineAtOffset(100, 0);
                contentStream.showText(grade.getSubject());
                contentStream.newLineAtOffset(100, 0);
                contentStream.showText(String.valueOf(grade.getGrade()));
            }

            // 结束文本流
            contentStream.endText();

            // 关闭内容流
            contentStream.close();

            // 保存文档
            document.save("StudentGrades.pdf");

            System.out.println("PDF created successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test02() throws IOException {
        PDDocument document = new PDDocument();
        // 创建一个空白页面并将其添加到文档中
        PDPage page = new PDPage();
        document.addPage(page);

        // 创建内容流以在页面上绘制内容
        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        // 设置字体和字体大小
        PDType0Font font = PDType0Font.load(document, new File("src/main/resources/static/simfang.ttf"));
        // 定义表格数据
        List<StudentGrade> studentGrades = new ArrayList<>();
        studentGrades.add(new StudentGrade("张三", "数学", 95));
        studentGrades.add(new StudentGrade("李四", "数学", 88));
        studentGrades.add(new StudentGrade("王五", "数学", 76));
        studentGrades.add(new StudentGrade("张三", "英语", 85));
        studentGrades.add(new StudentGrade("李四", "英语", 92));
        studentGrades.add(new StudentGrade("王五", "英语", 88));
        List<String[]> data = new ArrayList<>();
        for (StudentGrade grade : studentGrades) {
            String[] row = {grade.getName(), grade.getSubject(), String.valueOf(grade.getGrade())};
            data.add(row);
        }

        PdfTableUtils.createTable(document, contentStream, font, data);

        // 关闭内容流
        contentStream.close();

        // 保存文档
        document.save("StudentGrades.pdf");

        System.out.println("PDF created successfully.");
    }
}
