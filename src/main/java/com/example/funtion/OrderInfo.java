package com.example.funtion;

import lombok.Data;

import java.util.Date;

@Data
public class OrderInfo extends BaseSearch {
    private Integer orderId;
    private String stationNo;
    private String createUser;
    private Date createTime;
}
