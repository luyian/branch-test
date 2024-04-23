package com.example.demo;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

public class HuaWeiTest {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            String line = scanner.nextLine();
            solution(line);
        }

    }

    private static void solution(String line) {
        // key值 value个数
        Map<Integer, Integer> map = new HashMap<>();
        Arrays.stream(line.split(" "))
                .forEach(x -> {
                    int i = Integer.parseInt(x);
                    map.put(i, map.getOrDefault(i, 0) + 1);
                });
        // 找出最大个数
        Integer max = map.values().stream().max(Integer::compareTo).get();
        // 遍历最大个数值，排序
        List<Integer> newArr = map.keySet().stream()
                .filter(k -> map.get(k).equals(max))
                .sorted(Integer::compareTo)
                .collect(Collectors.toList());
        // 获取中位数
        Integer res = 0;
        int size = newArr.size();
        if (size % 2 == 0) {
            res = (newArr.get(size / 2) + newArr.get(size / 2 - 1)) / 2;
        } else {
            res = newArr.get(size / 2);
        }

        System.out.print(res);
    }



    @Test
    public void test01() {
        try(Scanner scanner = new Scanner(System.in)) {
            String in = scanner.nextLine();
            test1(in);
        }
    }

    public void test1(String in) {
        Map<Integer, Integer> integerIntegerHashMap = new HashMap<>();
        Arrays.stream(in.split(" "))
                .forEach(item -> {
                    int i = Integer.parseInt(item);
                    integerIntegerHashMap.put(i, integerIntegerHashMap.getOrDefault(i, 0)+1);
                });
        Integer max = integerIntegerHashMap.values().stream().max(Integer::compareTo).get();

        List<Integer> list = integerIntegerHashMap.keySet().stream()
                .filter(item -> max.equals(integerIntegerHashMap.get(item)))
                .sorted(Integer::compareTo)
                .collect(Collectors.toList());

        int res = 0;
        int size = list.size();
        if (size % 2 == 0) {
            res = (list.get(size / 2) + list.get(size/2 - 1)) / 2;
        } else {
            res = list.get(size / 2);
        }
        System.out.println(res);
    }
   

    /**
     * url 拼接
     * @throws Exception
     */
    @Test
    public void test02() throws Exception{
        Scanner scanner = new Scanner(System.in);
        String s = scanner.nextLine();


        StringBuilder stringBuilder = new StringBuilder("");
        Arrays.asList(s.split(",")).forEach(
                item -> {
                    String s1 = item.replaceAll("/+", "/");
                    if (s1.length() > 0) {
                        stringBuilder.append(s1);
                    }
                });
        System.out.println(stringBuilder.toString());

        ArrayList res = new ArrayList<Integer>();
    }
}
