package com.example.test;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@EqualsAndHashCode
public class DemoData {
    @ExcelProperty("列1")
    private String string;
    @ExcelProperty("列2")
    @DateTimeFormat("yyyy年MM月dd日HH时mm分ss秒")
    private Date date;
    @ExcelProperty("列3")
    private Double doubleData;
    private String val;
}