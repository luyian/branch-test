package com.example.test;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode
public class Material {
    private String name;
    private String code;
    private String type;
    private String size;
    private String color;
    private String weight;
}
