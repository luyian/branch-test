package com.example.batch.order;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 订单导出接口
 *
 * @author claude
 */
@RestController
@RequestMapping("/batch/order")
public class OrderExportController {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    @Qualifier("orderExportJob")
    private Job orderExportJob;

    /**
     * 手动触发订单导出
     *
     * @return 执行状态
     */
    @GetMapping("/export")
    public String exportOrder() throws Exception {
        JobParameters params = new JobParametersBuilder()
                .addLong("startAt", System.currentTimeMillis())
                .toJobParameters();
        JobExecution execution = jobLauncher.run(orderExportJob, params);
        return "订单导出状态：" + execution.getStatus() + "，文件：output/order_info.xlsx";
    }
}
