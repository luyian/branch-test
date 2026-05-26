# Spring Batch 分区并行批量导出

## 一、项目简介

基于 Spring Batch + EasyExcel + MyBatis-Plus 实现的大数据量订单导出方案，采用**分区并行**策略，将数据按 ID 范围拆分为多个分区，每个分区独立查询，所有分区通过 `synchronized` 共享一个 ExcelWriter 直接写入最终文件，无需后续合并。

**核心能力**：支持百万级数据的高效导出，内存占用恒定，无合并开销。

---

## 二、技术选型

| 组件 | 版本 | 用途 |
|------|------|------|
| Spring Batch | Spring Boot 内置 | 批处理框架，提供 Job/Step/Chunk 模型 |
| EasyExcel | 3.3.3 | 阿里巴巴 Excel 处理库，流式写入，内存友好 |
| MyBatis-Plus | 3.5.3.1 | ORM 框架，简化数据库查询 |
| MySQL | 8.0 | 数据源 |
| HikariCP | Spring Boot 内置 | 数据库连接池 |

---

## 三、整体架构

```
HTTP 请求 (GET /batch/order/export)
    │
    ▼
OrderExportController  ─────────── 触发导出
    │
    ▼
JobLauncher.run(orderExportJob)
    │
    ▼
beforeJob: SharedExcelWriter.open()  ── 创建最终文件
    │
    ▼
┌────────────────────────────────────────┐
│  orderExportStep（主 Step - 分区调度）    │
│                                        │
│  OrderPartitioner                      │
│  ├─ 查询总数                            │
│  ├─ 按 ID 范围均分为 N 个分区             │
│  └─ 每个分区携带 minId / maxId           │
└────────────────────────────────────────┘
    │
    ▼  TaskExecutor 并行分发
┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐
│ 分区 0    │ │ 分区 1    │ │ 分区 2    │ │ 分区 3    │
│ Reader   │ │ Reader   │ │ Reader   │ │ Reader   │
└──────────┘ └──────────┘ └──────────┘ └──────────┘
    │              │             │             │
    └──────┬───────┴──────┬──────┴──────┬──────┘
           │  synchronized │             │
           ▼              ▼             ▼
    ┌─────────────────────────────────────────┐
    │  SharedExcelWriter（共享，线程安全）       │
    │  output/order_info.xlsx                 │
    └─────────────────────────────────────────┘
    │
    ▼
afterJob: SharedExcelWriter.finish()  ── 刷盘关闭
```

---

## 四、核心组件说明

### 4.1 分区器 - OrderPartitioner

**职责**：将全量数据按 ID 范围均匀拆分为多个分区。

**算法**：
1. 查询表总数 `SELECT COUNT(*)`
2. 计算每个分区大小 `partitionSize = ceil(totalCount / gridSize)`
3. 依次查询每个分区的最大 ID 作为分区边界
4. 生成 `ExecutionContext`，携带 `minId`、`maxId`、`partitionIndex`

**示例**（84 万条数据，4 个分区）：

| 分区 | minId | maxId | 数据量 |
|------|-------|-------|--------|
| partition0 | 0 | 210000 | 21 万条 |
| partition1 | 210000 | 420000 | 21 万条 |
| partition2 | 420000 | 630000 | 21 万条 |
| partition3 | 630000 | 840000 | 21 万条 |

### 4.2 读取器 - OrderItemReader

**职责**：在分区 ID 范围内，使用游标分页逐批读取数据。

**读取策略**：
- 基于 `id > lastId AND id <= maxId` 的游标分页，避免传统 `OFFSET` 大偏移量的性能问题
- 每页读取 5000 条（`PAGE_SIZE`），转换为 `OrderExcelModel` 后逐条返回
- 分区内 `lastId` 单调递增，读取完毕返回 `null` 通知 Spring Batch 结束

**读取流程**：
```
lastId = minId
  │
  ├─ 查询: WHERE id > lastId AND id <= maxId LIMIT 5000
  ├─ 结果: [record_1, ..., record_5000]
  ├─ 更新 lastId = record_5000.id
  ├─ 逐条返回给 Chunk 处理
  │
  ├─ 当前页耗尽后，查询下一页
  │
  └─ 无数据时返回 null（分区读取结束）
```

### 4.3 共享写入器 - SharedExcelWriter

**职责**：所有分区共享同一个 ExcelWriter，通过 `synchronized` 保证并发写入安全。

**生命周期**（由 `JobExecutionListener` 管理）：
1. `beforeJob`：调用 `open()` 创建目标文件和 ExcelWriter
2. **Job 执行期间**：各分区并发调用 `synchronized write()` 写入数据
3. `afterJob`：调用 `finish()` 刷盘关闭

**关键设计**：
- `write()` 方法加 `synchronized`，保证多线程写入安全
- 写入瓶颈在 DB 查询而非磁盘 IO，锁竞争不影响整体吞吐量
- 无临时文件、无合并步骤，直接写入最终文件

### 4.4 写入器 - EasyExcelItemWriter

**职责**：Spring Batch 的 `ItemWriter` 适配器，将 Chunk 数据委托给 `SharedExcelWriter` 写入。

### 4.5 导出模型 - OrderExcelModel

**导出字段**：

| 字段 | Excel 列名 | 列宽 | 格式 |
|------|-----------|------|------|
| id | 主键ID | 25 | - |
| code | 流水号 | 20 | - |
| cusName | 用户名 | 15 | - |
| address | 地址 | 30 | - |
| detail | 描述 | 40 | - |
| phone | 电话 | 18 | - |
| createTime | 创建时间 | 22 | yyyy-MM-dd HH:mm:ss |
| updateTime | 更新时间 | 22 | yyyy-MM-dd HH:mm:ss |

---

## 五、关键参数配置

| 参数 | 默认值 | 说明 |
|------|--------|------|
| `GRID_SIZE` | 4 | 分区数（并行线程数） |
| `CHUNK_SIZE` | 1000 | 每批处理条数，影响事务粒度和内存占用 |
| `PAGE_SIZE` | 5000 | 每次数据库查询条数，影响 DB 交互次数 |
| `OUTPUT_DIR` | output | 输出文件目录 |
| `hikari.maximum-pool-size` | 5 | 数据库连接池最大连接数 |
| `batch.job.enabled` | false | 禁止应用启动时自动执行 Job |

---

## 六、使用方式

### 6.1 接口调用

```
GET /batch/order/export
```

**响应示例**：
```
订单导出状态：COMPLETED，文件：output/order_info.xlsx
```

### 6.2 程序化调用

```java
@Autowired
private JobLauncher jobLauncher;

@Autowired
@Qualifier("orderExportJob")
private Job orderExportJob;

public void export() throws Exception {
    JobParameters params = new JobParametersBuilder()
            .addLong("startAt", System.currentTimeMillis())
            .toJobParameters();
    JobExecution execution = jobLauncher.run(orderExportJob, params);

    // Job 完成后合并分区文件
    OrderExcelMerger.merge("output", "output/order_info.xlsx");
}
```

---

## 七、性能参考

以 84 万条订单数据、4 分区并行为例：

| 阶段 | 耗时 | 说明 |
|------|------|------|
| 分区计算 | ~1s | 查询总数 + 4 次边界查询 |
| 并行读取 + 共享写入 | ~9s | 4 线程并行查询，synchronized 写入同一文件 |
| **总耗时** | **~10s** | 无合并开销 |

**内存占用**：单分区峰值约 10MB，4 分区共约 40MB，不随数据量增长。

---

## 八、设计要点

### 8.1 为什么用游标分页而不是 OFFSET 分页？

传统 `LIMIT offset, size` 在 offset 很大时需要扫描并跳过前 N 行，性能急剧下降。游标分页 `WHERE id > lastId LIMIT size` 始终走索引，查询耗时恒定。

### 8.2 为什么共享 ExcelWriter 而不是每个分区写独立文件？

分区独立文件需要后续合并，合并过程需要将 Excel 反序列化再重新序列化，84 万条数据合并耗时约 24 秒，甚至超过了 Job 本身的执行时间。共享 ExcelWriter + `synchronized` 写入虽然存在锁竞争，但瓶颈在 DB 查询（每页 5000 条需要数毫秒），写入锁等待时间可忽略不计。

### 8.3 为什么 Reader 用 @StepScope 而 Writer 不用？

Reader 需要 `@StepScope`，因为每个分区有独立的 `minId`/`maxId` 和游标状态（`lastId`）。Writer 不需要 `@StepScope`，因为所有分区共享同一个 `SharedExcelWriter`，通过 `synchronized` 保证线程安全。

### 8.4 为什么 Chunk Size 和 Page Size 不一样？

- `CHUNK_SIZE = 1000`：控制事务提交频率，每 1000 条提交一次，失败只回滚当前批次
- `PAGE_SIZE = 5000`：控制 DB 查询频率，每次查 5000 条缓存在内存中，减少网络往返

两者解耦，可独立调优。

---

## 九、文件清单

```
batch/order/
├── OrderExportJobConfig.java   ── Job/Step/分区/线程池/监听器 配置
├── OrderPartitioner.java       ── 按 ID 范围拆分分区
├── OrderItemReader.java        ── 游标分页读取器
├── SharedExcelWriter.java      ── 线程安全的共享 Excel 写入器
├── EasyExcelItemWriter.java    ── ItemWriter 适配器（委托给 SharedExcelWriter）
├── OrderExcelModel.java        ── Excel 导出模型（列定义）
├── OrderInfoEntity.java        ── 数据库实体
├── OrderInfoMapper.java        ── MyBatis-Plus Mapper
├── OrderExportController.java  ── HTTP 触发接口
├── MybatisPlusConfig.java      ── 分页插件配置
└── OrderExportTest.java        ── 集成测试
```
