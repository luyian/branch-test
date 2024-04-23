package com.example.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorTest {
    public static void main(String[] args) {
        System.out.println("main start....");
//        ExecutorService executorService = Executors.newFixedThreadPool(5);
//        ExecutorService executorService = Executors.newCachedThreadPool();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        RunDemo runDemo = new RunDemo();
        for (int i = 0; i < 5; i++) {
            executorService.submit(runDemo);
        }
        executorService.shutdown();

        System.out.println("main down....");
    }
}
