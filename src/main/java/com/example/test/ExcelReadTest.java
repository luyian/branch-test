package com.example.test;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class ExcelReadTest {

    /**
     * 最简单的读
     * <p>
     * 1. 创建excel对应的实体对象 参照{@link DemoData}
     * <p>
     * 2. 由于默认一行行的读取excel，所以需要创建excel一行一行的回调监听器，参照{@link DemoDataListener}
     * <p>
     * 3. 直接读即可
     */
    @Test
    public void simpleRead() {
        // 写法1：JDK8+ ,不用额外写一个DemoDataListener
        // since: 3.0.0-beta1
        String fileName = "demo.xlsx";
        // 这里默认每次会读取100条数据 然后返回过来 直接调用使用数据就行
        // 具体需要返回多少行可以在`PageReadListener`的构造函数设置
        EasyExcel.read(fileName, DemoData.class, new PageReadListener<DemoData>(dataList -> {
            for (DemoData demoData : dataList) {
                log.info("读取到一条数据{}", JSON.toJSONString(demoData));
            }
        })).sheet().doRead();
    }





    /**
     * 最简单的写
     * <p>
     * 1. 创建excel对应的实体对象 参照{@link DemoData}
     * <p>
     * 2. 直接写即可
     */
    @Test
    public void simpleWrite() {
        // 注意 simpleWrite在数据量不大的情况下可以使用（5000以内，具体也要看实际情况），数据量大参照 重复多次写入

        // 写法1 JDK8+
        // since: 3.0.0-beta1
        String fileName = "a.xlsx";
        // 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
        // 如果这里想使用03 则 传入excelType参数即可
        EasyExcel.write(fileName, DemoData.class)
                .sheet("模板")
                .includeColumnFieldNames(Arrays.asList("string", "date", "doubleData"))
                .doWrite(() -> {
                    return Collections.emptyList();
                });
    }


    @Test
    public void genericTemplate() {
        String fileName = "test.xlsx";
        genericTemplate(fileName, DemoData.class, Arrays.asList("string", "date", "doubleData"));
    }

    /**
     * 泛型写入方法
     * @param fileName 文件名
     * @param clazz 实体类类型
     */
    public void genericTemplate(String fileName, Class clazz, List<String> includeColumns) {
        EasyExcel.write(fileName, clazz)
                .sheet("模板")
                .includeColumnFieldNames(includeColumns)
                .doWrite(() -> Collections.emptyList());
    }




    @Test
    public void readStationData() {
        // 写法1：JDK8+ ,不用额外写一个DemoDataListener
        // since: 3.0.0-beta1
        String fileName = "需批量指派.xlsx";
        // 这里默认每次会读取100条数据 然后返回过来 直接调用使用数据就行
        // 具体需要返回多少行可以在`PageReadListener`的构造函数设置
        List<OrdOrder> list = new ArrayList<>();
        EasyExcel.read(fileName, OrdOrder.class, new PageReadListener<OrdOrder>(dataList -> {
            for (OrdOrder demoData : dataList) {
                list.add(demoData);
            }
        })).sheet().doRead();

        // 按用户分组
        Map<String, List<OrdOrder>> map = list.stream().collect(Collectors.groupingBy(OrdOrder::getDoaccount));
        map.forEach((k, v) -> {
            System.out.println(k);
            System.out.println(v.size());
        });

        list.forEach(System.out::println);
        System.out.println(list.size());
    }

}
