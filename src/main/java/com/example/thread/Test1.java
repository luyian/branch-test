package com.example.thread;

import org.junit.jupiter.api.Test;

import java.util.concurrent.Callable;

public class Test1 {

    public static int duckNum = 100;

    public static void main(String[] args) {
        // 生产者
        Thread product = new Thread(() -> {
            while (duckNum < 120) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("生产者生产第" + (++duckNum) + "只烤鸭");
            }
        });

        // 消费者
        Thread consumer = new Thread(() -> {
            while (duckNum > 0) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("消费者消费第" + (--duckNum) + "只烤鸭");
                System.out.println("消费者消费第" + (--duckNum) + "只烤鸭");
            }
        });

        consumer.start();
        product.start();

    }

    @Test
    public void test01() {

    }

    @Test
    public void test03() {
        System.out.println("test branch 3-1");
    }
    @Test
    public void test02() {
        System.out.println("test branch2");
    }

}
