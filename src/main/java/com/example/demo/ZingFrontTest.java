package com.example.demo;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class ZingFrontTest {

    @Test
    public void test01() {
        String str = "rontFingZ";
        System.out.println(stringCheck(str));
    }

    /**
     * 设计一个检测程序，当输入一个字符串之后，请判断这个字符串能否通过重新排列，组成“ZingFront”字符串（必须大小写完全相同），并需要基于原始字符串输出每次调整之后的字符串，直至排列成新字符串；举例：
     * 1. 输 入 “rontFingZ” ， 可 以 重 新 排 列 成 "ZingFront" ， 则 返 回 True ，可能的一种排列调整为：rontFingZ->ZrontFing->ZirontFng->ZiontFrng->ZintFrong->ZingtFron->ZingFront；
     * 2. 若输入"rontinGztz"，由于大小写和缺少字符，无法组成 ZingFront，则返回False；加分项：调整字母排列次数，使得调整次数最少；
     */

    /**
     * 1、先判断长度，长度不符返回 false
     * 2、逐位比较，如果相同比较下一位
     * 3、如果不同判断字符是否存在，不存在返回 false
     * 4、存在就将字符数组当前下标和目标字符下标值交换，最终跑完返回 true
     */
    public boolean stringCheck(String str) {
        String defaultString = "ZingFront";

        if (str.length() != defaultString.length()) {
            return false;
        }

        char[] defaultChars = defaultString.toCharArray();
        char[] targetChars = str.toCharArray();

        String tmpStr = str;
        char tmpChar;
        for (int i = 0; i < defaultChars.length; i++) {
            char curChar = defaultChars[i];
            // 比较 & 交换
            if (targetChars[i] != curChar) {
                int curIndex = tmpStr.indexOf(curChar, i);
                if (curIndex == -1) {
                    return false;
                } else {
                    // 交换
                    tmpChar = targetChars[i];
                    targetChars[i] = curChar;
                    targetChars[curIndex] = tmpChar;
                    // 交换后的字符串
                    tmpStr = new String(targetChars);
                    System.out.println(tmpStr);
                }
            }
        }
        return true;
    }

    /**
     * 编程题：现有 6 个颜色各不相同的水桶，其桶身和桶盖颜色相同，现在将桶身和桶盖随机打乱组合，
     * 求全部桶盖和桶身都不匹配的组合有多少种？请把所有组合都打印出来；
     */
    /**
     * 思路：直接遍历
     */
    @Test
    public void test03() {
        Set<String> bodys = new HashSet<>();
        Set<String> covers = new HashSet<>();

        // 生产桶
        for (int i = 1; i < 7; i++) {
            bodys.add("body color" + i);
            covers.add("cover color" + i);
        }

        // 遍历
        for (String body : bodys) {
            for (String cover : covers) {
                // 颜色不相同输出
                if (body.charAt(body.length() - 1) != cover.charAt(cover.length() - 1)) {
                    System.out.println(body + " --> " + cover);
                }
            }
        }

    }




    @Test
    public void test02() throws Exception{
        saveLog(1000, 5);
    }

    public void saveLog(int timer, int count) throws InterruptedException {
        Timer myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("do...");
            }
        }, 0, timer);

        Thread.sleep(5000);
    }
    
}
