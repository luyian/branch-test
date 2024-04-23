package com.example.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * 输入字符串s，输出s中包含所有整数的最小和
 * 说明
 * 1. 字符串s，只包含 a-z A-Z +- ；
 * 2. 合法的整数包括
 *     1） 正整数 一个或者多个0-9组成，如 0 2 3 002 102
 *     2）负整数 负号 - 开头，数字部分由一个或者多个0-9组成，如 -0 -012 -23 -00023
 */
// 注意类名必须为 Main, 不要有任何 package xxx 信息
public class Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        String inStr = in.nextLine();
        // 先判断有没有负号
        // 有符号，取最大连续数字
        // 没符号，按位取正 bb1244-34bb
        List<Integer> list1 = new ArrayList<>();
        List<Integer> list2 = new ArrayList<>();
        inStr = inStr.replaceAll("\\+", "");

        // 先截取全部负数
        String tmpStr = inStr;
        while (tmpStr.indexOf('-') != -1) {
            tmpStr = tmpStr.substring(tmpStr.indexOf("-")+1);
            if (tmpStr.length() > 0) {
                int numIndex = subNum(tmpStr);
                if (numIndex != 0) {
                    String valueStr = "-" + tmpStr.substring(0, numIndex);
                    list2.add(Integer.parseInt(valueStr));
                    inStr = inStr.replace(valueStr, "");
                    tmpStr = tmpStr.substring(numIndex);
                }
            }
        }
        // 剔除负数，取正数
        for (Integer integer : list2) {
            inStr = inStr.replace(integer.toString(), "");
        }
        char[] chars = inStr.toCharArray();
        for (char aChar : chars) {
            if ('0' <= aChar && aChar <='9') {
                list1.add(Integer.parseInt("" + aChar));
            }
        }

        int sum = 0;
        // 取值计算
        for (Integer integer : list1) {
            System.out.println(integer);
            sum+=integer;
        }
        for (Integer integer : list2) {
            System.out.println(integer);
            sum+=integer;
        }
        System.out.println(sum);
    }


    /**
     * 获取下一个不为数字的下标
     */
    public static int subNum(String str) {
        char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if ((chars[i] >= 'a' && chars[i] <= 'z') || (chars[i] >= 'A' && chars[i] <= 'Z') || chars[i] == '-') {
                return i;
            }
        }
        return str.length();
    }
}