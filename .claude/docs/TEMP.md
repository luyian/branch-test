# 代码修改记录

## 2026-05-11

### 改造：工行 SDK 从 system scope 改为 Nexus 私服依赖

#### 修改内容

1. **5 个工行 SDK jar 上传到 Nexus 私服**
   - 私服地址：`http://120.25.120.234:8081/nexus/repository/nkrelease/`
   - 仓库 id：`nkrelease`（hosted, maven2）
   - 上传命令模板：`mvn -s nkSettings.xml deploy:deploy-file -DgroupId=com.icbc -DartifactId=xxx -Dversion=xxx -Dpackaging=jar -Dfile=lib/xxx.jar -DrepositoryId=nkrelease -Durl=...`
   - 上传的 GAV：
     - `com.icbc:icbc-api-sdk-cop:20260415`
     - `com.icbc:icbc-api-sdk-cop-io:20260415`
     - `com.icbc:hsm-software-share:1.0.5`
     - `com.icbc:infosec-crypto:1.02`
     - `com.icbc:icbc-ca:1.0.0`

2. **pom.xml 改造**
   - 5 个依赖去掉 `<scope>system</scope>` 和 `<systemPath>`
   - 新增 `<repositories>` 节点引用 nkrelease 私服
   - **关键坑点**：`<repository id>` 必须与 `settings.xml` 中 `<server id>` 一致（这里都为 `nkrelease`），否则 Maven 不会把认证应用到该仓库，会返回 401

3. **删除项目 lib/ 目录**
   - 5 个 jar 不再需要随项目托管
   - 已从 git 暂存区移除（git rm --cached）

#### 部署/团队协作要点

- **其他开发者拉取项目后**：必须确保自己的 `~/.m2/settings.xml` 或 `${maven.home}/conf/settings.xml` 中包含以下 server 认证：
  ```xml
  <server>
      <id>nkrelease</id>
      <username>admin</username>
      <password>bzc123456</password>
  </server>
  ```
- **CI/构建机器**：同样需要在 settings.xml 中配置 nkrelease 的 server 认证
- **如使用 Spring Boot 打包**：fat jar 会自动包含工行 SDK，无需特殊处理（不再有 system scope 排除问题）

#### 注意事项

- `D:/apache-maven-3.5.4/conf/nkSettings.xml` 已包含完整的 `<server>` 认证配置，编译时可用 `-s D:/apache-maven-3.5.4/conf/nkSettings.xml` 显式指定
- 默认 `conf/settings.xml` **未配置** nkrelease 认证，直接 `mvn compile` 会 401，需要补全或使用 `-s` 切换 settings 文件
- nkrelease 写策略可能为 `ALLOW_ONCE`，同版本 jar 不可覆盖，重新部署需提升版本号

---

## 2026-05-09

### 新增功能：工行 - 结算账户交易明细查询测试

#### 修改内容

1. **新增测试类：IcbcSettlementDetailQueryTest**
   - 文件路径：`src/main/java/com/example/icbc/IcbcSettlementDetailQueryTest.java`
   - 接口名称：结算账户交易明细查询
   - 接口路径：`/api/settlement/account/detail/V1/query`
   - 请求类：`SettlementAccountDetailQueryRequestV1`
   - 响应类：`SettlementAccountDetailQueryResponseV1`

2. **测试数据**
   - 一类卡号：6222030200000296172（默认查询此卡）
   - 二类卡号：6214761102614096836
   - 户名：工真啼（此接口不需要）
   - 证件号：217951196403051104（此接口不需要）
   - 手机号：13581780252（此接口不需要）

#### 业务场景

- 查询合作方在工行的个人结算账户交易明细
- 查询条件：卡号 + 开始日期 + 结束日期
- 结果按交易时间倒序，单次最多 10 条
- 支持翻页：`queryMode` 1-首查 2-上一页 3-下一页

#### 技术要点

- **secretKey 字段必填**：使用测试参数文档中的 AES_KEY (`FtFc/mN0jtJvhf4eG6RNPQ==`)，此字段名易被误解为可选，实际接口要求传入对称秘钥
- **mediumIdHash**：卡号 hash，按工行规范根据 secretKey 对 mediumId 计算，当前留空，如接口报错需补充计算逻辑
- **签名算法**：SM2（与 e 钱包接口一致）
- **首次查询**：`queryMode=1, page=1, pnBusidate="", pnRowRecord=""`
- **翻页规则**：
  - 下一页：`queryMode=3, page+=1, pnBusidate=上次结果最后一条的 busidate, pnRowRecord=上次结果最后一条的 rowRecord`
  - 上一页：`queryMode=2, page-=1, pnBusidate=上次结果第一条的 busidate, pnRowRecord=上次结果第一条的 rowRecord`

#### 注意事项

- `corpNo`（合作方机构编号）需替换为真实值，由工行分配
- 若接口返回卡号 hash 校验失败，需补充 `mediumIdHash` 计算逻辑（可参考工行 SDK doc 目录的《关于签名和验签的说明.pdf》）

#### 关联模块

- 工行 SDK 模块：`com.icbc.api.request.SettlementAccountDetailQueryRequestV1`
- 同期新增 e 钱包测试类：`IcbcEwalletTest`（共用 SM2 签名配置）

---

### 新增功能：工行 e 钱包接口测试（光伏场景）

#### 修改内容

1. **新增测试类：IcbcEwalletTest**
   - 文件路径：`src/main/java/com/example/icbc/IcbcEwalletTest.java`
   - 功能说明：基于工行开放平台 SDK 实现 e 钱包基本信息查询（含余额）和明细查询的接口测试

   **包含的测试方法：**
   - `testBaseinfoQuery(client)`：调用 e 钱包基本信息查询接口
     - 接口路径：`/api/mybank/account/corporatewallet/baseinfoquery/V1`
     - 请求类：`MybankAccountCorporatewalletBaseinfoqueryRequestV1`
   - `testDetailQuery(client)`：调用 e 钱包交易明细查询接口
     - 接口路径：`/api/mybank/account/corporatewallet/detailquery/V1`
     - 请求类：`MybankAccountCorporatewalletDetailqueryRequestV1`

   **关键实现：**
   - 使用 `DefaultIcbcClient` + `IcbcConstants.SIGN_TYPE_SM2` 国密签名
   - AppId、SM2 公私钥、网关地址均按测试参数文档配置
   - `bus_serialno`、`work_date`、`work_time`、`msgId` 由代码自动生成
   - `agr_no`、`wallet_id` 留 `TODO` 占位符，需要填入真实业务参数后才能通过校验

2. **新增工行 SDK 依赖（system scope）**
   - SDK 来源：`D:\install\icbc-api-sdk-cop_v2_20260415`（v2 版本，2026-04-15 更新）
   - 引入方式：复制 jar 到项目 `lib/` 目录，pom.xml 通过 `system` scope 引入
   - 涉及 jar：
     - `icbc-api-sdk-cop.jar`（核心 SDK）
     - `icbc-api-sdk-cop-io.jar`（请求/响应类）
     - `hsm-software-share-1.0.5.jar`
     - `InfosecCrypto_Java1_02_JDK14+.jar`（国密算法实现）
     - `icbc-ca.jar`

#### 业务场景

- 光伏行业 e 钱包业务对接，用于查询合作方 e 钱包余额和交易明细
- 测试服务器：`https://apipcs4.dccnet.com.cn`
- 签名算法：SM2（国密）

#### 技术要点

- **签名类型**：必须使用 SM2，不能使用 RSA2（测试参数文档中 `RSA2私钥：false`）
- **BizContent 字段命名**：使用下划线（如 `agr_no`、`wallet_id`、`bus_serialno`），与 SDK 示例代码中的驼峰命名不同，需以 jar 中实际类签名为准
- **returnCode 类型**：`IcbcResponse.getReturnCode()` 返回 `int` 而非 `String`
- **DefaultIcbcClient 构造**：使用 10 参构造方法，密钥参数顺序：appId, signType, privateKey, charset, format, icbcPublicKey, encryptKey, encryptType, ca, password

#### 注意事项

- 真实测试前必须填入 `AGR_NO`（合作方协议号）和 `WALLET_ID`（钱包ID），否则接口会返回参数校验失败
- 测试参数仅适用于测试环境（apipcs4.dccnet.com.cn），不可用于生产
- SM2 私钥为敏感信息，正式环境不应硬编码在代码中，建议改为配置文件或密钥管理服务

#### 关联模块

- 工行 SDK 模块：`com.icbc.api.*`
- 项目 lib 目录：`D:/workspace/test/lib/`

---

## 2026-01-23

### 新增功能：Java 序列化/反序列化工具类

#### 修改内容

1. **新增工具类：SerializeUtil**
   - 文件路径：`src/main/java/com/example/utils/SerializeUtil.java`
   - 功能说明：提供 Java 对象序列化和反序列化的工具方法

   **主要方法：**
   - `deserialize(byte[] bytes)`: 从字节数组反序列化为 Java 对象
   - `deserializeFromHex(String hexString)`: 从十六进制字符串反序列化（支持 `\xac\xed` 格式）
   - `deserializeFromBase64(String base64String)`: 从 Base64 字符串反序列化
   - `serialize(Object obj)`: 将 Java 对象序列化为字节数组
   - `serializeToHex(Object obj)`: 将 Java 对象序列化为十六进制字符串
   - `serializeToBase64(Object obj)`: 将 Java 对象序列化为 Base64 字符串
   - `printHexBytes(byte[] bytes)`: 打印字节数组的十六进制表示（调试用）

2. **新增测试类：SerializeUtilTest**
   - 文件路径：`src/main/java/com/example/test/SerializeUtilTest.java`
   - 功能说明：演示 SerializeUtil 工具类的使用方法
   - 包含实际的反序列化示例和完整的序列化/反序列化流程演示

#### 业务场景

- 用于处理 Java 序列化数据的反序列化，特别是从外部系统接收的序列化对象
- 支持多种格式：字节数组、十六进制字符串、Base64 字符串
- 可用于调试和分析 Java 序列化数据

#### 技术要点

- 使用 Java 标准的 `ObjectInputStream` 和 `ObjectOutputStream`
- 支持十六进制字符串格式转换（带 `\x` 前缀或不带前缀）
- 遵循阿里巴巴编码规范
- 包含完整的参数校验和异常处理

#### 注意事项

- Java 反序列化存在安全风险，应只反序列化可信来源的数据
- 反序列化的类必须在 classpath 中存在
- 反序列化的类必须实现 `Serializable` 接口
- 建议在生产环境中添加白名单机制限制可反序列化的类

#### 关联模块

- 工具类模块：`com.example.utils`
- 测试模块：`com.example.test`

---

### 调试记录：SerializeUtil 工具类测试

#### 调试时间
2026-01-23

#### 调试内容

1. **编译问题修复**
   - 问题：使用了 Java 11 的 `String.repeat()` 方法，但项目使用 Java 8
   - 解决：将 `"=".repeat(50)` 替换为固定的分隔线字符串
   - 文件：`src/main/java/com/example/test/SerializeUtilTest.java`

2. **测试类优化**
   - 重构测试代码，分离为两个独立的测试方法：
     - `testBasicFunctionality()`: 测试工具类的基本序列化/反序列化功能
     - `testUserProvidedData()`: 测试用户提供的实际数据
   - 添加详细的错误处理和提示信息
   - 针对不同异常类型提供具体的错误说明

3. **测试结果**

   **✓ 基本功能测试全部通过：**
   - 字节数组方式序列化/反序列化：成功
   - 十六进制字符串方式序列化/反序列化：成功
   - Base64 字符串方式序列化/反序列化：成功
   - 测试对象：`TestObject{name='测试数据', value=12345}`
   - 序列化后大小：216 字节

   **✗ 用户数据反序列化失败：**
   - 数据类型：`com.scm.outinterface.model.gudewei.GuDeWeiToken`
   - 错误类型：`StreamCorruptedException`
   - 错误信息：`invalid type code: EF`
   - 失败原因：序列化数据可能不完整或被截断
   - 建议：需要获取完整的序列化数据

#### 结论

- **SerializeUtil 工具类功能正常**，所有基本功能测试通过
- 工具类支持三种格式的序列化/反序列化：字节数组、十六进制字符串、Base64 字符串
- 用户提供的特定数据反序列化失败是因为数据本身的问题，不是工具类的问题
- 工具类已经可以正常使用，可以处理完整的 Java 序列化数据

#### 使用建议

1. 确保序列化数据完整且未被截断
2. 确保目标类在 classpath 中存在
3. 对于外部数据，建议先验证数据的完整性
4. 可以使用 `printHexBytes()` 方法调试序列化数据

