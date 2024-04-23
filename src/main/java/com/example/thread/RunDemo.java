package com.example.thread;

public class RunDemo implements Runnable{
    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + " run .....");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(Thread.currentThread().getName() + " run over .....");
    }
}
