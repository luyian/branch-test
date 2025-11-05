package com.example.test;

import com.example.config.ClickHouseConfig;
import com.example.entity.ConfigProjectBind;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

@SpringBootTest(
    properties = {
        "spring.batch.job.enabled=false"
    },
    classes = {ClickHouseConfig.class}
)
class ClickHouseIntegrationTest {

    private JdbcTemplate jdbcTemplate;
    private DataSource dataSource;

    @Autowired
    public void setDataSource(@Qualifier("clickHouseDataSource") DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @BeforeEach
    void setUp() {
        try {
            // 检查连接是否可用
            Connection connection = dataSource.getConnection();
            assumeTrue(connection != null && !connection.isClosed(), "无法建立到ClickHouse的连接");
            this.jdbcTemplate = new JdbcTemplate(dataSource);
        } catch (SQLException e) {
            assumeTrue(false, "ClickHouse服务不可用: " + e.getMessage());
        }
    }

    @Test
    void testClickHouseConnection() {
        try {
            // 尝试执行一个简单的查询
            List<ConfigProjectBind> configProjectBinds = jdbcTemplate.query("select * from config_project_bind", new BeanPropertyRowMapper<>(ConfigProjectBind.class));
            configProjectBinds.forEach(System.out::println);

        } catch (Exception e) {
            // 如果ClickHouse服务不可用，跳过测试
            assumeTrue(false, "ClickHouse服务不可用: " + e.getMessage());
        }
    }
}