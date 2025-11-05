package com.example.test;

import org.junit.jupiter.api.Test;

import java.util.HashSet;

public class SetTest {
    @Test
    public void test() {
        HashSet<String> uniqueSet = new HashSet<>();
        boolean aa = uniqueSet.add("aa");
        boolean bb = uniqueSet.add("aa");
        System.out.println(aa);
        System.out.println(bb);
    }

    @Test
    public void test2() {
        OrdOrder  sss = new OrdOrder();
        sss.setDoaccount("123");
        test3(sss);
        System.out.println(sss.getDoaccount());
    }

    public void test3(OrdOrder  sss) {
        sss.setDoaccount("1235");
    }
}
