package com.example.thread;

import org.junit.jupiter.api.Test;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockTest {

    @Test
    public void test01() {
        Lock lock = new ReentrantLock();
        try {
            lock.lock();
            System.out.println("上锁了");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
            System.out.println("解锁了");
        }
    }


}
