package com.example.funtion;


import lombok.Data;

@Data
public class BomInfo extends BaseSearch{
    private Integer id;
    private String materialCode;
    private String materialName;
    private String materialGroup;
}
