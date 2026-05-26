package com.example.batch.order;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;

import java.io.File;
import java.util.List;

/**
 * 线程安全的共享 Excel 写入器
 *
 * <p>所有分区共享同一个 ExcelWriter 实例，通过 synchronized 保证并发写入安全，
 * 避免分区独立文件 + 后续合并的开销。</p>
 *
 * @author claude
 */
public class SharedExcelWriter<T> {

    private final String sheetName;
    private final Class<T> clazz;
    private ExcelWriter excelWriter;
    private WriteSheet writeSheet;

    public SharedExcelWriter(String sheetName, Class<T> clazz) {
        this.sheetName = sheetName;
        this.clazz = clazz;
    }

    /**
     * 打开写入器，创建目标文件
     *
     * @param filePath 目标文件路径
     */
    public void open(String filePath) {
        File parent = new File(filePath).getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }
        this.excelWriter = EasyExcel.write(filePath, clazz).build();
        this.writeSheet = EasyExcel.writerSheet(sheetName).build();
    }

    /**
     * 线程安全的批量写入
     *
     * @param items 待写入数据列表
     */
    public synchronized void write(List<? extends T> items) {
        excelWriter.write(items, writeSheet);
    }

    /**
     * 关闭写入器，刷盘
     */
    public void finish() {
        if (excelWriter != null) {
            excelWriter.finish();
            excelWriter = null;
        }
    }
}
