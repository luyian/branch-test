# 代码修改记录

## 2026-05-26 去掉分区文件合并步骤，改为 SharedExcelWriter 共享写入（synchronized 直接写最终文件，省去 24 秒合并开销）

## 2026-05-26 新增 Spring Batch 分区并行批量导出说明文档（`batch/README.md`，含架构、组件、性能、设计要点）

## 2026-05-26 修复 MySQL "Too many connections"：BatchConfig 和 application.yml 限制 HikariCP 连接池大小（最大5、最小空闲2）

## 2026-05-26 订单导出改为 MyBatis-Plus 分页读取，新增 OrderInfoEntity/Mapper、分页插件配置，测试类改为 Spring 容器启动

## 2026-05-26 新增 Spring Batch + EasyExcel 导出 order_info 表数据（`batch/order` 包，接口 `GET /batch/order/export`）

## 2026-05-26 新增 BatchTest 测试类，脱离 Web 容器单独测试 Processor/Reader/ForkJoin 各组件

## 2026-05-26 完善 Spring Batch 演示（新增 BatchConfig 配置类，补全 Reader→Processor→Writer 完整流程，完善监听器和启动器）

## 2026-05-26 新增 Fork/Join 框架演示（大数组并行求和，`batch` 包下）

## 2026-05-26 新增责任链模式演示（费用审批场景，`design/chain` 包下，含 README 说明文档）

## 2026-05-26 新增策略模式演示（支付场景，`design/strategy` 包下，含 README 说明文档）

## 2026-05-11 工行 SDK 从 system scope 改为 Nexus 私服依赖（nkrelease 仓库），删除 lib/ 目录

## 2026-05-09 新增工行结算账户交易明细查询测试类 `IcbcSettlementDetailQueryTest`，SM2 签名

## 2026-05-09 新增工行 e 钱包接口测试类 `IcbcEwalletTest`（光伏场景，基本信息查询 + 明细查询），引入工行 SDK 依赖

## 2026-01-23 新增 `SerializeUtil` 序列化工具类及测试类，支持字节数组/十六进制/Base64 三种格式
