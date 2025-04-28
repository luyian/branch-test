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
}
