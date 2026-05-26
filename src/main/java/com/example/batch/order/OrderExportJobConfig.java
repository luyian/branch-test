package com.example.batch.order;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

/**
 * 订单导出 Job 配置（分区并行）
 *
 * <p>将数据按 ID 范围分成多个分区，每个分区独立线程读取，
 * 所有分区共享一个 ExcelWriter 直接写入最终文件，无需后续合并。</p>
 *
 * @author claude
 */
@Configuration
public class OrderExportJobConfig {

    private static final String OUTPUT_DIR = "output";
    private static final String OUTPUT_FILE = OUTPUT_DIR + "/order_info.xlsx";

    /** 每批处理条数 */
    private static final int CHUNK_SIZE = 1000;

    /** 每页查询条数 */
    private static final int PAGE_SIZE = 5000;

    /** 分区数（并行线程数） */
    private static final int GRID_SIZE = 4;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    /**
     * 共享 Excel 写入器，所有分区通过 synchronized 并发写入同一文件
     */
    @Bean
    public SharedExcelWriter<OrderExcelModel> sharedExcelWriter() {
        return new SharedExcelWriter<>("订单数据", OrderExcelModel.class);
    }

    /**
     * 分区器：按行数均分，计算每个分区的 ID 边界
     */
    @Bean
    public Partitioner orderPartitioner() {
        return new OrderPartitioner(orderInfoMapper);
    }

    /**
     * 分区 Reader：每个分区拿到自己的 minId/maxId，独立读取
     */
    @Bean
    @StepScope
    public OrderItemReader orderItemReader(
            @Value("#{stepExecutionContext['minId']}") Long minId,
            @Value("#{stepExecutionContext['maxId']}") Long maxId) {
        return new OrderItemReader(orderInfoMapper, PAGE_SIZE, minId, maxId);
    }

    /**
     * Writer：委托给共享写入器，所有分区共用
     */
    @Bean
    public EasyExcelItemWriter<OrderExcelModel> orderItemWriter() {
        return new EasyExcelItemWriter<>(sharedExcelWriter());
    }

    @Bean
    public TaskExecutor orderExportTaskExecutor() {
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor("order-export-");
        executor.setConcurrencyLimit(GRID_SIZE);
        return executor;
    }

    /**
     * 从 Step（每个分区执行的 Step）
     */
    @Bean
    public Step workerStep() {
        return stepBuilderFactory.get("workerStep")
                .<OrderExcelModel, OrderExcelModel>chunk(CHUNK_SIZE)
                .reader(orderItemReader(null, null))
                .writer(orderItemWriter())
                .build();
    }

    /**
     * 主 Step：分区调度，将任务拆分后分发给 workerStep 并行执行
     */
    @Bean
    public Step orderExportStep() {
        return stepBuilderFactory.get("orderExportStep")
                .partitioner("workerStep", orderPartitioner())
                .step(workerStep())
                .gridSize(GRID_SIZE)
                .taskExecutor(orderExportTaskExecutor())
                .build();
    }

    @Bean
    public Job orderExportJob() {
        return jobBuilderFactory.get("orderExportJob")
                .incrementer(new RunIdIncrementer())
                .listener(new JobExecutionListener() {
                    @Override
                    public void beforeJob(JobExecution jobExecution) {
                        sharedExcelWriter().open(OUTPUT_FILE);
                    }

                    @Override
                    public void afterJob(JobExecution jobExecution) {
                        sharedExcelWriter().finish();
                    }
                })
                .start(orderExportStep())
                .build();
    }
}
