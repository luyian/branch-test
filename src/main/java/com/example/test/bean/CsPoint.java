package com.example.test.bean;

import lombok.Data;

import java.util.List;

@Data
public class CsPoint {
    private Integer workId;
    private Integer pointId;
    private String name;
    List<CsPointBom> pointBomList;
}
