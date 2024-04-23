package com.example.thread;

//wait 和 notify
public class ProducerConsumerWithWaitNofity {
    public static void main(String[] args) {
        Resource resource = new Resource();

        //生产者线程
        ProducerThread p1 = new ProducerThread(resource);
        ProducerThread p2 = new ProducerThread(resource);
        ProducerThread p3 = new ProducerThread(resource);
        //消费者线程
        ConsumerThread c1 = new ConsumerThread(resource);
        ConsumerThread c2 = new ConsumerThread(resource);
        ConsumerThread c3 = new ConsumerThread(resource);

        p1.start();
        p2.start();
        p3.start();
        c1.start();
        c2.start();
        c3.start();
    }

    /**
     * 生产者线程
     */
    static class ProducerThread extends Thread {
        private final Resource resource;

        public ProducerThread(Resource resource) {
            this.resource = resource;
        }

        @Override
        public void run() {
            //不断地生产资源
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                resource.add();
            }
        }
    }


    /**
     * 消费者线程
     */
    static class ConsumerThread extends Thread {
        private final Resource resource;

        public ConsumerThread(Resource resource) {
            this.resource = resource;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                resource.remove();
            }
        }
    }


    /**
     * 公共资源类
     */
    static class Resource {//重要
        //当前资源数量
        private int currentCount = 0;

        /**
         * 从资源池中取走资源
         */
        public synchronized void remove() {
            if (currentCount > 0) {
                currentCount--;
                System.out.println("--- 消费者" +
                        Thread.currentThread().getName() +
                        "消耗一件资源，" + "当前线程池有" + currentCount + "个");
                notifyAll();//通知生产者生产资源
            } else {
                try {
                    //如果没有资源，则消费者进入等待状态
                    wait();
                    System.out.println("--- 消费者" +
                            Thread.currentThread().getName() + "线程进入等待状态");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * 向资源池中添加资源
         */
        public synchronized void add() {
            //资源池中允许存放的资源数目
            int maxCount = 10;
            if (currentCount < maxCount) {
                currentCount++;
                System.out.println("+++" +
                        Thread.currentThread().getName() + "生产一件资源，当前资源池有"
                        + currentCount + "个");
                //通知等待的消费者
                notifyAll();
            } else {
                //如果当前资源池中有10件资源
                try {
                    wait();//生产者进入等待状态，并释放锁
                    System.out.println("+++" +
                            Thread.currentThread().getName() + "线程进入等待");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}


