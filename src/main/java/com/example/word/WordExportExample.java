package com.example.word;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;

public class WordExportExample {

    public static void main(String[] args) throws Exception {
        // 创建一个新的Word文档
        XWPFDocument document = new XWPFDocument();

        // 创建一个标题
        XWPFParagraph titleParagraph = document.createParagraph();
        XWPFRun titleRun = titleParagraph.createRun();
        titleRun.setText("学生成绩表");
        titleRun.setBold(true);
        titleRun.setFontSize(16);

        // 创建一个段落
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun paragraphRun = paragraph.createRun();
        paragraphRun.setText("以下是学生的成绩信息：");

        // 创建一个表格
        XWPFTable table = document.createTable();
        // 设置表格宽度
        CTTblWidth tblWidth = table.getCTTbl().addNewTblPr().addNewTblW();
        tblWidth.setW(BigInteger.valueOf(5000));
        tblWidth.setType(STTblWidth.DXA);

        // 添加表头
        XWPFTableRow headerRow = table.getRow(0);
        headerRow.getCell(0).setText("姓名");
        headerRow.addNewTableCell().setText("科目");
        headerRow.addNewTableCell().setText("成绩");

        // 添加数据行
        String[][] data = {
                {"张三", "数学", "95"},
                {"李四", "语文", "88"},
                {"王五", "英语", "92"}
        };

        for (String[] row : data) {
            XWPFTableRow tableRow = table.createRow();
            tableRow.getCell(0).setText(row[0]);
            tableRow.getCell(1).setText(row[1]);
            tableRow.getCell(2).setText(row[2]);
        }

        // 插入图片
        String imagePath = "D:/img/29.png"; // 图片路径
        File imageFile = new File(imagePath);
        if (!imageFile.exists()) {
            System.out.println("图片文件不存在: " + imagePath);
        } else {
            try (FileInputStream imageStream = new FileInputStream(imageFile)) {
                XWPFParagraph imageParagraph = document.createParagraph();
                XWPFRun imageRun = imageParagraph.createRun();
                imageRun.addPicture(imageStream, XWPFDocument.PICTURE_TYPE_PNG, imagePath, Units.toEMU(150), Units.toEMU(150)); // 宽度和高度为200EMU（EMUs，英文度量单位）
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 保存文档到文件
        try (FileOutputStream out = new FileOutputStream("学生成绩表.docx")) {
            document.write(out);
        }

        // 关闭文档
        document.close();
    }
}
