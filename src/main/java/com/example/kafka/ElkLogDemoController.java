package com.example.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/elk")
public class ElkLogDemoController {

    private static final Logger log = LoggerFactory.getLogger(ElkLogDemoController.class);

    @GetMapping("/demo")
    public Map<String, Object> demo(
            @RequestParam(defaultValue = "user-1001") String userId,
            @RequestParam(defaultValue = "sku-1001") String skuId,
            @RequestParam(defaultValue = "false") boolean error) {

        String traceId = UUID.randomUUID().toString().replace("-", "");
        MDC.put("traceId", traceId);

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("traceId", traceId);
        result.put("用户ID", userId);
        result.put("商品ID", skuId);

        try {
            log.info("收到 ELK 演示请求，用户ID={}，商品ID={}，是否模拟异常={}", userId, skuId, error);

            if (error) {
                try {
                    throw new IllegalStateException("模拟业务异常，用于演示 ELK 异常日志检索");
                } catch (RuntimeException e) {
                    log.error("ELK 演示接口出现异常，用户ID={}，商品ID={}", userId, skuId, e);
                    result.put("状态", "异常已记录");
                    result.put("说明", "请在 Kibana 中按 traceId 查询异常堆栈");
                    return result;
                }
            }

            log.info("ELK 演示请求处理完成，traceId={}", traceId);
            result.put("状态", "成功");
            result.put("说明", "请在 Kibana 中查询该 traceId 对应日志");
            return result;
        } finally {
            MDC.clear();
        }
    }
}
