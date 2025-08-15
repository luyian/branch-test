package com.example.test;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString
public class OrdOrder extends BaseOrdOrder {
    @ExcelProperty("电站编号")
    private String stationno;
    @ExcelProperty("运维单号")
    private String mtno;
    @ExcelProperty("需要派单人员")
    private String doname;
    @ExcelProperty("账号")
    private String doaccount;
    List< Material> materials;
    private BigDecimal total;
    private Date createTime;
}
