package com.example.config;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class ClickHouseConfig {

    @Bean
    public DataSource clickHouseDataSource() {
        return DataSourceBuilder.create()
                .url("jdbc:clickhouse://59.110.33.127:8123/solarmonitortest")
                .driverClassName("ru.yandex.clickhouse.ClickHouseDriver")
                .username("solarmonitor")
                .password("bzc@2022*qwe123")
                .build();
    }
}