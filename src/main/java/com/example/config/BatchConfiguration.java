package com.example.config;

import com.example.batch.JobCompletionNotificationListener;
import com.example.batch.UserItemProcessor;
import com.example.demo.User;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;



    @Bean
    public ItemReader<User> reader() {
        return new FlatFileItemReaderBuilder<User>()
                .name("userItemReader")
                .resource(new ClassPathResource("users.csv"))
                .delimited()
                .names(new String[]{"name", "age"})
                .fieldSetMapper(new BeanWrapperFieldSetMapper<User>() {{
                    setTargetType(User.class);
                }})
                .build();
    }

    @Bean
    public ItemProcessor<User, User> processor() {
        return new UserItemProcessor();
    }

    @Bean
    public ItemWriter<User> writer() {
        return items -> {
            for (User user : items) {
                System.out.println("处理后的用户: " + user.toString());
            }
        };
    }

    @Bean
    public Job importUserJob(JobCompletionNotificationListener listener, Step step1) {
        return jobBuilderFactory.get("importUserJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step1)
                .end()
                .build();
    }

    @Bean
    public Step step1(ItemReader<User> reader,
                      ItemProcessor<User, User> processor,
                      ItemWriter<User> writer) {
        return stepBuilderFactory.get("step1")
                .<User, User>chunk(10)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

}
