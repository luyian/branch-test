package com.example.test;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class OrdOrder {
    @ExcelProperty("电站编号")
    private String stationno;
    @ExcelProperty("运维单号")
    private String mtno;
    @ExcelProperty("需要派单人员")
    private String doname;
    @ExcelProperty("账号")
    private String doaccount;
}
