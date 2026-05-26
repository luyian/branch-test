package com.example.batch.order;

import org.springframework.batch.item.ItemWriter;

import java.util.List;

/**
 * EasyExcel 批量写入器（委托给共享写入器）
 *
 * <p>所有分区共享同一个 SharedExcelWriter，通过 synchronized 保证线程安全。
 * 写入器生命周期由 Job 监听器管理。</p>
 *
 * @author claude
 */
public class EasyExcelItemWriter<T> implements ItemWriter<T> {

    private final SharedExcelWriter<T> sharedWriter;

    public EasyExcelItemWriter(SharedExcelWriter<T> sharedWriter) {
        this.sharedWriter = sharedWriter;
    }

    @Override
    public void write(List<? extends T> items) {
        sharedWriter.write(items);
    }
}
