package com.example.test.bean;

import lombok.Data;

import java.util.List;

@Data
public class CsStartWork {
    private Integer workId;
    private List<CsPoint> pointList;
}
