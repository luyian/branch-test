package com.example.thread;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

class Share {

    private Integer number = 0;

    private ReentrantLock lock = new ReentrantLock();

    private Condition newCondition = lock.newCondition();

    // +1 的方法
    public void incr() {
        try {
            lock.lock(); // 加锁
            while (number != 0) {
                newCondition.await();//沉睡
            }
            number++;
            System.out.println(Thread.currentThread().getName() + "::" + number);
            newCondition.signal(); //唤醒另一个沉睡的线程
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    // -1 的方法
    public void decr() {
        try {
            lock.lock();
            while (number != 1) {
                newCondition.await();
            }
            number--;
            System.out.println(Thread.currentThread().getName() + "::" + number);
            newCondition.signal();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}

public class LockDemo2 {
    public static void main(String[] args) {
        Share share = new Share();

        new Thread(()->{
            for (int i=0;i<=10;i++){
                share.incr();
            }
        },"AA").start();

        new Thread(()->{
            for (int i=0;i<=10;i++){
                share.decr();
            }
        },"BB").start();
        /**
         * AA::1
         * BB::0
         * AA::1
         * BB::0
         * .....
         */
    }
}
