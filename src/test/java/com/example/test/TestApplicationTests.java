package com.example.test;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "spring.batch.job.enabled=false")
class TestApplicationTests {

    @Test
    void contextLoads() {
    }

}
