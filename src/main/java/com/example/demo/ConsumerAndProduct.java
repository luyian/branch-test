package com.example.demo;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * 生产者消费者
 */
public class ConsumerAndProduct {
    private int maxSize = 10;
    private ArrayBlockingQueue<Integer> queue = new ArrayBlockingQueue<>(maxSize);

    public static void main(String[] args) {
        ConsumerAndProduct consumerAndProduct = new ConsumerAndProduct();
        Consumer consumer = consumerAndProduct.new Consumer();


        consumer.start();
        for (int i = 0; i < 10; i++) {
            Product product = consumerAndProduct.new Product();
            product.start();
        }
    }

    class Consumer extends Thread {
        @Override
        public void run() {
            // 保证消费者是线程安全的
            while (true) {
                synchronized (queue) {
                    if (queue.size() > 0) {
                        // 生产
                        Integer poll = queue.poll();
                        System.out.println("消费者消费消息：" + poll);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }

                        queue.notify();
                    } else {
                        try {
                            queue.wait();
                        } catch (InterruptedException e) {
                            queue.notify();
                        }

                    }
                }
            }
        }
    }

    class Product extends Thread {
        @Override
        public void run() {
            // 保证生产者是线程安全的
            synchronized (queue) {
                if (queue.size() < 10) {
                    // 生产
                    queue.add(queue.size() + 1);
                    System.out.println("生产者生产消息，队列当前长度：" + queue.size());
                    queue.notify();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    try {
                        queue.wait();
                    } catch (InterruptedException e) {
                        queue.notify();
                    }

                }
            }
        }
    }
}
