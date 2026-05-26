package com.example.batch.order;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.read.listener.PageReadListener;
import com.alibaba.excel.write.metadata.WriteSheet;

import java.io.File;
import java.util.Arrays;

/**
 * 分区 Excel 合并器
 *
 * <p>将多个分区文件按顺序合并为一个 xlsx，合并完成后删除临时分区文件。</p>
 *
 * @author claude
 */
public class OrderExcelMerger {

    /**
     * 合并分区文件
     *
     * @param outputDir   分区文件所在目录
     * @param targetFile  合并后的目标文件路径
     */
    public static void merge(String outputDir, String targetFile) {
        File dir = new File(outputDir);
        File[] partFiles = dir.listFiles((d, name) -> name.startsWith("order_part_") && name.endsWith(".xlsx"));
        if (partFiles == null || partFiles.length == 0) {
            System.out.println("没有找到分区文件");
            return;
        }

        // 按分区序号排序
        Arrays.sort(partFiles, (a, b) -> {
            int indexA = extractIndex(a.getName());
            int indexB = extractIndex(b.getName());
            return Integer.compare(indexA, indexB);
        });

        ExcelWriter writer = EasyExcel.write(targetFile, OrderExcelModel.class).build();
        WriteSheet sheet = EasyExcel.writerSheet("订单数据").build();

        int totalCount = 0;
        for (File partFile : partFiles) {
            int[] count = {0};
            EasyExcel.read(partFile, OrderExcelModel.class, new PageReadListener<OrderExcelModel>(dataList -> {
                writer.write(dataList, sheet);
                count[0] += dataList.size();
            }, 5000)).sheet().doRead();
            totalCount += count[0];
            System.out.println("合并 " + partFile.getName() + "，" + count[0] + " 条");
        }

        writer.finish();
        System.out.println("合并完成，共 " + totalCount + " 条 → " + targetFile);

        // 删除临时分区文件
        for (File partFile : partFiles) {
            partFile.delete();
        }
    }

    private static int extractIndex(String fileName) {
        String num = fileName.replace("order_part_", "").replace(".xlsx", "");
        return Integer.parseInt(num);
    }
}
