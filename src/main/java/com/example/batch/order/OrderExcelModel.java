package com.example.batch.order;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import java.util.Date;

/**
 * 订单 Excel 导出模型（对应 order_info 表）
 *
 * @author claude
 */
@Data
public class OrderExcelModel {

    @ExcelProperty("主键ID")
    @ColumnWidth(25)
    private Long id;

    @ExcelProperty("流水号")
    @ColumnWidth(20)
    private String code;

    @ExcelProperty("用户名")
    @ColumnWidth(15)
    private String cusName;

    @ExcelProperty("地址")
    @ColumnWidth(30)
    private String address;

    @ExcelProperty("描述")
    @ColumnWidth(40)
    private String detail;

    @ExcelProperty("电话")
    @ColumnWidth(18)
    private String phone;

    @ExcelProperty("创建时间")
    @ColumnWidth(22)
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ExcelProperty("更新时间")
    @ColumnWidth(22)
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}
