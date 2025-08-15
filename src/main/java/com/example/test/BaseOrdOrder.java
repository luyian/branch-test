package com.example.test;

import lombok.Data;

import java.util.Objects;

@Data
public class BaseOrdOrder {
    private Long id;
    private String sn;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseOrdOrder that = (BaseOrdOrder) o;
        return Objects.equals(sn, that.sn);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(sn);
    }
}
