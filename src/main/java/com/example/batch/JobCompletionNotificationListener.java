package com.example.batch;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.stereotype.Component;

/**
 * 作业完成通知监听器
 *
 * <p>在 Job 执行前后打印状态信息，便于观察批处理执行过程。</p>
 *
 * @author claude
 */
@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

    @Override
    public void beforeJob(JobExecution jobExecution) {
        System.out.println("========== 批处理作业开始 ==========");
        System.out.println("作业名称：" + jobExecution.getJobInstance().getJobName());
        System.out.println("启动时间：" + jobExecution.getStartTime());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            System.out.println("========== 批处理作业完成 ==========");
            System.out.println("结束时间：" + jobExecution.getEndTime());
        } else if (jobExecution.getStatus() == BatchStatus.FAILED) {
            System.out.println("========== 批处理作业失败 ==========");
            jobExecution.getAllFailureExceptions()
                    .forEach(e -> System.out.println("失败原因：" + e.getMessage()));
        }
    }
}
