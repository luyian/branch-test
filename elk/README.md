# 项目 ELK 演示方案

这套配置用于本地演示项目日志采集链路：

```text
Spring Boot 日志文件 -> Filebeat -> Logstash -> Elasticsearch -> Kibana
```

## 目录说明

- `docker-compose.yml`：启动 Elasticsearch、Logstash、Kibana、Filebeat。
- `filebeat/filebeat.yml`：采集项目 `logs/*.log` 文件。
- `logstash/pipeline/logstash.conf`：解析 Logback 日志格式并写入 Elasticsearch。
- `src/main/resources/logback-spring.xml`：项目日志输出到控制台和 `logs/test-app.log`。
- `com.example.kafka.ElkLogDemoController`：用于生成演示日志的接口。

## 启动 ELK

在项目根目录执行：

```bash
docker compose -f elk/docker-compose.yml up -d
```

查看容器：

```bash
docker compose -f elk/docker-compose.yml ps
```

Kibana 地址：

```text
http://localhost:5601
```

Elasticsearch 地址：

```text
http://localhost:9200
```

## 启动项目并生成日志

启动 Spring Boot 项目：

```bash
mvn spring-boot:run
```

当前项目同时存在 `application.properties` 和 `application.yml`，实际端口以启动日志为准。常见访问方式：

```bash
curl "http://localhost:8080/elk/demo?userId=user-1001&skuId=sku-1001"
curl "http://localhost:8080/elk/demo?userId=user-1001&skuId=sku-1001&error=true"
```

如果启动日志显示端口是 `8082`，把上面的 `8080` 改成 `8082`。

项目日志文件位置：

```text
logs/test-app.log
```

## Kibana 查询

在 Kibana 中创建 Data View：

```text
test-app-*
```

推荐查询：

```text
app_name : "test"
log_level : "INFO"
message : "ELK 演示"
trace_id : *
```

## 演示重点

- Filebeat 负责轻量采集日志文件。
- Logstash 负责解析时间、线程、级别、traceId、logger、正文。
- Elasticsearch 按天写入索引：`test-app-yyyy.MM.dd`。
- Kibana 可以按 traceId、日志级别、关键字检索。

这套配置关闭了 Elasticsearch 安全认证，只适合本地演示。生产环境需要开启账号密码、TLS、索引生命周期管理和访问控制。
