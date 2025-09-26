package com.example.test;

import com.example.enmu.AccountState;
import com.example.utils.EnumSelectUtil;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

public class EnumTest {

    @Test
    public void test01() {
        List<Map<String, Object>> enumValues = EnumSelectUtil.getEnumProperty(AccountState.class);
        System.out.println(enumValues);
    }

}
