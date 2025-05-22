package com.example.funtion;

import java.util.List;

public interface QueryFactory<T extends BaseSearch> {
    List<? extends BaseSearch> queryPage(T search);
}
