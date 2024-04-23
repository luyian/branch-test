package com.example.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 要实现对系统运行中的错误和用户行为进行统计，通常会采用上传日志的方式。鉴于日志产生频繁，我们需要对日志进行本地缓存处理。
 * 为此，我们可以设计一个代码段，其中包含两个参数：timer 和 count。
 * 其中，timer 表示每隔多少秒后将本地日志上传一次，而 count 表示累积多少条日志后进行一次上传，请设计一段代码来满足以上需求。
 * 提示：
 * 1. 前端不使用 setinterval 更佳
 * 2. 后端建议写成一个定时类
 */
public class LogUploader {

    private List<String> logs = new ArrayList<>();
    private final int count;
    private final long timer;
    private ScheduledExecutorService scheduler;

    public LogUploader(long timer, int count) {
        this.count = count;
        this.timer = timer;
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    /**
     * 添加日志方法，达到设置最大值上传
     * @param log 日志记录
     */
    public void addLog(String log) {
        logs.add(log);
        if (logs.size() >= count) {
            uploadLogs();
        }
    }

    /**
     * 上传日志
     */
    private void uploadLogs() {
        // 日志上传
        System.out.println("Uploading logs: " + logs);
        // 上传后清空日志列表
        logs.clear(); 
    }

    /**
     * 开启日志定时上传任务
     */
    public void startPeriodicUpload() {
        scheduler.scheduleAtFixedRate(() -> {
            if (!logs.isEmpty()) {
                uploadLogs();
            }
        }, timer, timer, TimeUnit.SECONDS);
    }

    /**
     * 关闭定时任务
     */
    public void stopPeriodicUpload() {
        scheduler.shutdown();
    }

    public static void main(String[] args) {
        // 设置累积10条日志或每隔5秒上传
        LogUploader logUploader = new LogUploader(5, 10);
        // 开始定期上传任务
        logUploader.startPeriodicUpload();

        // 模拟日志产生
        for (int i = 0; i < 20; i++) {
            logUploader.addLog("Log " + i);
            try {
                // 模拟日志产生的时间间隔
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // 停止定期上传任务
        logUploader.stopPeriodicUpload();
    }
}