package com.example.thread;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

public class CreateThreadDemo4 implements Callable<String> {

    private String name;

    public CreateThreadDemo4(String name) {
        this.name = name;
    }

    @Override
    public String call() throws Exception {
        System.out.println(">>> " + name + " 线程任务启动");
        Date startTime = new Date();
        Thread.sleep(1000);
        Date endTime = new Date();
        long time = endTime.getTime() - startTime.getTime();
        System.out.println(">>> " + name + " 线程任务终止");
        return name + "线程任务返回运行结果, 当前任务耗时【" + time + "毫秒】";
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=====主程序开始运行======");
        Date startTime = new Date();

        ExecutorService executorService = Executors.newFixedThreadPool(5);

        // 创建多个有返回值的任务
        List<Future<String>> futureList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Future<String> submit = executorService.submit(new CreateThreadDemo4("线程0" + i));
            futureList.add(submit);
        }

        // 关闭线程池
        executorService.shutdown();

        // 获取并发任务结果
        for (Future<String> stringFuture : futureList) {
            String s = stringFuture.get();
            System.out.println(">>>> " + s);
        }
        Date endTime = new Date();
        System.out.println("---- 主程序结束运行 ----，程序运行耗时【" + (endTime.getTime() - startTime.getTime()) + "毫秒】");
    }
}
