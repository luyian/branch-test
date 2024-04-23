package com.example.demo;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class GanTangTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String str = scanner.nextLine();
        String num = scanner.nextLine();

        List<String> strings = Arrays.asList(str.split(","));
        List<String> collect = strings.stream().filter(i -> !num.equals(i)).collect(Collectors.toList());
        String[] res = collect.toArray(new String[collect.size()]);
        System.out.println(res.length + ", " + Arrays.toString(res));

        new ArrayList<>();
    }

    @Test
    public void test01() {
        int a = -10;
        int b = 2;
        int num = 0;
        for (int i = a; i <= b; i++) {
            num += i;
        }
        System.out.println(num);
    }

    @Test
    public void test03() {
        System.out.println(-1 + 0 + 1 + 2);
    }
}
