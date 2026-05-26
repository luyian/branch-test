package com.example.batch;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * Fork/Join 框架演示
 *
 * <p>使用大数组求和场景演示 Fork/Join 的分治思想：
 * 当任务规模超过阈值时拆分为子任务并行执行，否则直接计算。</p>
 *
 * @author claude
 */
public class ForkJoinDemo {

    /** 任务拆分阈值，子数组长度 ≤ 该值时直接计算 */
    private static final int THRESHOLD = 10000;

    /**
     * 数组求和的递归任务
     */
    public static class SumTask extends RecursiveTask<Long> {

        private final int[] array;
        private final int start;
        private final int end;

        public SumTask(int[] array, int start, int end) {
            this.array = array;
            this.start = start;
            this.end = end;
        }

        @Override
        protected Long compute() {
            int length = end - start;

            // 小于阈值，直接计算
            if (length <= THRESHOLD) {
                long sum = 0;
                for (int i = start; i < end; i++) {
                    sum += array[i];
                }
                return sum;
            }

            // 超过阈值，拆分为两个子任务
            int mid = start + length / 2;
            SumTask leftTask = new SumTask(array, start, mid);
            SumTask rightTask = new SumTask(array, mid, end);

            // fork：将左任务提交到线程池异步执行
            leftTask.fork();
            // 当前线程直接计算右任务
            Long rightResult = rightTask.compute();
            // join：等待左任务完成并获取结果
            Long leftResult = leftTask.join();

            return leftResult + rightResult;
        }
    }

    public static void main(String[] args) {
        // 准备测试数据：1 到 1亿
        int size = 100_000_000;
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = i + 1;
        }

        // 单线程求和（对比基准）
        long startTime = System.currentTimeMillis();
        long singleSum = 0;
        for (int value : array) {
            singleSum += value;
        }
        long singleCost = System.currentTimeMillis() - startTime;
        System.out.println("单线程求和结果：" + singleSum + "，耗时：" + singleCost + "ms");

        // Fork/Join 并行求和
        ForkJoinPool pool = new ForkJoinPool();
        SumTask task = new SumTask(array, 0, array.length);

        startTime = System.currentTimeMillis();
        Long forkJoinSum = pool.invoke(task);
        long forkJoinCost = System.currentTimeMillis() - startTime;
        System.out.println("Fork/Join求和结果：" + forkJoinSum + "，耗时：" + forkJoinCost + "ms");

        System.out.println("结果一致：" + (singleSum == forkJoinSum));
        System.out.println("线程池并行度：" + pool.getParallelism());
        pool.shutdown();
    }
}
