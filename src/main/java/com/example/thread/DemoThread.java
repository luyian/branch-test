package com.example.thread;

public class DemoThread extends Thread{
    @Override
    public void run() {
        System.out.println("aaa");
        super.run();
    }
}
