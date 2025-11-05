package com.example.pdf2word;

import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 增强版PDF转Word转换器，支持图像和表格
 */
public class EnhancedPdfToWordConverter {

    /**
     * 将PDF文件转换为Word文档（支持图像和表格）
     *
     * @param pdfPath  PDF文件路径
     * @param wordPath 输出Word文件路径
     * @throws IOException 当文件读写出现问题时抛出
     */
    public static void convertPdfToWordWithImagesAndTables(String pdfPath, String wordPath) throws IOException {
        XWPFDocument document = new XWPFDocument();

        try {
            PdfReader reader = new PdfReader(pdfPath);
            int pageCount = reader.getNumberOfPages();

            for (int i = 1; i <= pageCount; i++) {
                // 提取文本内容（包括表格识别）
                extractTextWithTables(reader, i, document);

                // 提取图像
                extractImages(reader, i, document);

                // 添加分页符（除了最后一页）
                if (i < pageCount) {
                    document.createParagraph().createRun().addBreak();
                }
            }

            // 保存Word文档
            try (FileOutputStream out = new FileOutputStream(wordPath)) {
                document.write(out);
            }

        } finally {
            if (document != null) {
                document.close();
            }
        }
    }

    /**
     * 提取文本内容并识别表格结构
     */
    private static void extractTextWithTables(PdfReader reader, int pageNum, XWPFDocument document)
            throws IOException {
        // 获取页面文本策略
        SimpleTextExtractionStrategy strategy = new SimpleTextExtractionStrategy();
        String text = PdfTextExtractor.getTextFromPage(reader, pageNum, strategy);

        // 按行分割文本
        String[] lines = text.split("\n");

        for (String line : lines) {
            if (isTableLine(line)) {
                // 创建表格
                createTableFromLine(document, line);
            } else {
                // 创建普通段落
                XWPFParagraph paragraph = document.createParagraph();
                XWPFRun run = paragraph.createRun();
                run.setText(line);
            }
        }
    }

    /**
     * 判断是否为表格行（简单判断包含制表符或多个空格分隔）
     */
    private static boolean isTableLine(String line) {
        return line.contains("\t") || line.matches(".*\\s{2,}.*");
    }

    /**
     * 从文本行创建表格
     */
    private static void createTableFromLine(XWPFDocument document, String line) {
        // 分割表格列
        String[] cells = line.contains("\t") ? line.split("\t") : line.trim().split("\\s{2,}");

        // 创建表格
        XWPFTable table = document.createTable(1, cells.length);
        table.setTableAlignment(TableRowAlign.CENTER);

        // 填充表格数据
        XWPFTableRow row = table.getRow(0);
        for (int i = 0; i < cells.length; i++) {
            if (row.getCell(i) != null) {
                row.getCell(i).setText(cells[i].trim());
            }
        }
    }

    /**
     * 提取PDF中的图像
     */
    private static void extractImages(PdfReader reader, int pageNum, XWPFDocument document)
            throws IOException {
        PdfDictionary pageDict = reader.getPageN(pageNum);
        PdfDictionary resources = pageDict.getAsDict(PdfName.RESOURCES);

        if (resources != null) {
            PdfDictionary xObjects = resources.getAsDict(PdfName.XOBJECT);
            if (xObjects != null) {
                for (PdfName key : xObjects.getKeys()) {
                    PdfObject obj = xObjects.getDirectObject(key);
                    if (obj instanceof PRStream) {
                        PRStream stream = (PRStream) obj;
                        PdfObject subtype = stream.get(PdfName.SUBTYPE);

                        if (subtype != null && subtype.toString().equals(PdfName.IMAGE.toString())) {
                            try {
                                // 修改这一行
                                byte[] imgBytes = PdfReader.getStreamBytes(stream);
                                insertImageToDocument(document, imgBytes);
                            } catch (Exception e) {
                                // 图像处理失败时跳过
                                System.err.println("图像处理失败: " + e.getMessage());
                            }
                        }
                    }
                }
            }
        }
    }


    /**
     * 将图像插入到Word文档中
     */
    private static void insertImageToDocument(XWPFDocument document, byte[] imgBytes)
            throws IOException {
        XWPFParagraph paragraph = document.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun run = paragraph.createRun();
        try {
            // 判断图像类型并插入
            int format = getImageFormat(imgBytes);
            run.addPicture(new ByteArrayInputStream(imgBytes), format, "image",
                    Units.toEMU(400), Units.toEMU(300));
        } catch (Exception e) {
            // 如果图像插入失败，则添加占位符文本
            run.setText("[图像]");
        }
    }

    /**
     * 识别图像格式
     */
    private static int getImageFormat(byte[] imgBytes) {
        // 简单的格式识别
        if (imgBytes.length > 2 && imgBytes[0] == (byte) 0xFF && imgBytes[1] == (byte) 0xD8) {
            return XWPFDocument.PICTURE_TYPE_JPEG;
        } else if (imgBytes.length > 3 && imgBytes[0] == 'G' && imgBytes[1] == 'I' &&
                imgBytes[2] == 'F') {
            return XWPFDocument.PICTURE_TYPE_GIF;
        } else if (imgBytes.length > 7 && imgBytes[0] == 0x89 && imgBytes[1] == 'P' &&
                imgBytes[2] == 'N' && imgBytes[3] == 'G') {
            return XWPFDocument.PICTURE_TYPE_PNG;
        }
        return XWPFDocument.PICTURE_TYPE_JPEG; // 默认JPEG
    }

    /**
     * 使用示例
     */
    public static void main(String[] args) {
        try {
            convertPdfToWordWithImagesAndTables("input.pdf", "output_with_images_tables.docx");
            System.out.println("PDF转换Word成功（支持图像和表格）！");
        } catch (IOException e) {
            System.err.println("转换失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
