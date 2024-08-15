package com.example.thread;
import java.util.concurrent.*;
import java.lang.Thread;

public class ThreadPoolTest {
    public static void main(String[] args) {


        int corePoolSize = Runtime.getRuntime().availableProcessors(); // 核心线程数
        int maxPoolSize = corePoolSize * 2; // 最大线程数，可根据实际情况调整
        long keepAliveTime = 60L; // 空闲线程存活时间，单位秒
        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(6000); // 有界队列，大小根据需求调整
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        RejectedExecutionHandler handler = new ThreadPoolExecutor.CallerRunsPolicy(); // 自定义拒绝策略

        ExecutorService executor = new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                keepAliveTime,
                TimeUnit.SECONDS,
                queue,
                threadFactory,
                handler
        );

        for (int i = 0; i < 4000; i++) {
            int j = i;
            executor.submit(() -> {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(j);
            });
        }
        // 记得关闭线程池
        executor.shutdown();
        while (!executor.isTerminated()) {}
        System.out.println("所有任务完成");

    }
}
