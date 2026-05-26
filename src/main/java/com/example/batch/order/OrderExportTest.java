package com.example.batch.order;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * 订单导出测试类
 *
 * <p>启动 Spring 容器，通过 JobLauncher 触发分区并行导出 Job。
 * 所有分区共享一个 ExcelWriter 直接写入最终文件，无需后续合并。</p>
 *
 * @author claude
 */
public class OrderExportTest {

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext context = SpringApplication.run(
                com.example.TestApplication.class, args);

        try {
            JobLauncher jobLauncher = context.getBean(JobLauncher.class);
            Job orderExportJob = context.getBean("orderExportJob", Job.class);

            System.out.println("===== 触发分区并行导出 Job =====");
            long startTime = System.currentTimeMillis();

            JobParameters params = new JobParametersBuilder()
                    .addLong("startAt", System.currentTimeMillis())
                    .toJobParameters();
            JobExecution execution = jobLauncher.run(orderExportJob, params);

            long cost = System.currentTimeMillis() - startTime;
            System.out.println();
            System.out.println("===== 导出完成 =====");
            System.out.println("Job 状态：" + execution.getStatus());
            System.out.println("总耗时：" + cost + "ms");
            System.out.println("输出文件：output/order_info.xlsx");
        } finally {
            context.close();
        }
    }
}
