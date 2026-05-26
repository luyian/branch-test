# 工行 SDK 部署到 Nexus 私服指南

> 本文档记录将工行开放平台 SDK（5 个本地 jar）从 `system` scope 改造为 Nexus 私服依赖的完整流程。
>
> 适用环境：Maven 3.5+ / Spring Boot 项目 / Windows 或 Linux

---

## 一、背景

工行开放平台的 SDK jar 不在 Maven 中央仓库，开发者从 [open.icbc.com.cn](https://open.icbc.com.cn) 手动下载后通常有两种引入方式：

| 方式 | 优点 | 缺点 |
|---|---|---|
| `system` scope + `<systemPath>` | 改动最小，开箱即用 | Spring Boot fat jar 默认不含；多人协作每人都要本地放 jar；Maven 已弃用 |
| **Nexus 私服**（本指南） | 团队共享、一次上传所有人可用、CI 友好、Boot 打包正常 | 需要私服基础设施 |

---

## 二、私服信息

| 项 | 值 |
|---|---|
| 私服地址 | `http://120.25.120.234:8081/nexus/repository/nkrelease/` |
| 仓库 id | `nkrelease` |
| 仓库类型 | hosted（可写） |
| 认证账号 | `admin` |
| 认证密码 | `bzc123456` |
| 配套 settings | `D:/apache-maven-3.5.4/conf/nkSettings.xml` |

---

## 三、待上传的 5 个 jar

| 文件名 | groupId | artifactId | version |
|---|---|---|---|
| `icbc-api-sdk-cop.jar` | `com.icbc` | `icbc-api-sdk-cop` | `20260415` |
| `icbc-api-sdk-cop-io.jar` | `com.icbc` | `icbc-api-sdk-cop-io` | `20260415` |
| `hsm-software-share-1.0.5.jar` | `com.icbc` | `hsm-software-share` | `1.0.5` |
| `InfosecCrypto_Java1_02_JDK14+.jar` | `com.icbc` | `infosec-crypto` | `1.02` |
| `icbc-ca.jar` | `com.icbc` | `icbc-ca` | `1.0.0` |

> 版本号来源于工行 SDK 包发布日期或 jar 文件名中的版本号。

---

## 四、上传步骤

### 4.1 上传命令模板

```bash
mvn deploy:deploy-file \
    -s D:/apache-maven-3.5.4/conf/nkSettings.xml \
    -DgroupId=<groupId> \
    -DartifactId=<artifactId> \
    -Dversion=<version> \
    -Dpackaging=jar \
    -Dfile=<本地jar路径> \
    -DrepositoryId=nkrelease \
    -Durl=http://120.25.120.234:8081/nexus/repository/nkrelease/
```

**关键参数说明：**

| 参数 | 含义 |
|---|---|
| `-s` | 显式指定 settings.xml（必须含 `<server id="nkrelease">` 认证） |
| `-DrepositoryId` | 仓库 id，**必须与 settings.xml 中 `<server id>` 一致**，否则 401 |
| `-Durl` | 私服仓库 URL |
| `-Dfile` | 本地 jar 文件路径，包含特殊字符（如 `+`）时用双引号包起来 |

### 4.2 完整 5 条命令（逐个复制执行）

```bash
# 1. icbc-api-sdk-cop.jar
mvn deploy:deploy-file \
    -s D:/apache-maven-3.5.4/conf/nkSettings.xml \
    -DgroupId=com.icbc -DartifactId=icbc-api-sdk-cop -Dversion=20260415 \
    -Dpackaging=jar -Dfile=lib/icbc-api-sdk-cop.jar \
    -DrepositoryId=nkrelease \
    -Durl=http://120.25.120.234:8081/nexus/repository/nkrelease/

# 2. icbc-api-sdk-cop-io.jar
mvn deploy:deploy-file \
    -s D:/apache-maven-3.5.4/conf/nkSettings.xml \
    -DgroupId=com.icbc -DartifactId=icbc-api-sdk-cop-io -Dversion=20260415 \
    -Dpackaging=jar -Dfile=lib/icbc-api-sdk-cop-io.jar \
    -DrepositoryId=nkrelease \
    -Durl=http://120.25.120.234:8081/nexus/repository/nkrelease/

# 3. hsm-software-share-1.0.5.jar
mvn deploy:deploy-file \
    -s D:/apache-maven-3.5.4/conf/nkSettings.xml \
    -DgroupId=com.icbc -DartifactId=hsm-software-share -Dversion=1.0.5 \
    -Dpackaging=jar -Dfile=lib/hsm-software-share-1.0.5.jar \
    -DrepositoryId=nkrelease \
    -Durl=http://120.25.120.234:8081/nexus/repository/nkrelease/

# 4. InfosecCrypto_Java1_02_JDK14+.jar （文件名含 +，注意双引号）
mvn deploy:deploy-file \
    -s D:/apache-maven-3.5.4/conf/nkSettings.xml \
    -DgroupId=com.icbc -DartifactId=infosec-crypto -Dversion=1.02 \
    -Dpackaging=jar "-Dfile=lib/InfosecCrypto_Java1_02_JDK14+.jar" \
    -DrepositoryId=nkrelease \
    -Durl=http://120.25.120.234:8081/nexus/repository/nkrelease/

# 5. icbc-ca.jar
mvn deploy:deploy-file \
    -s D:/apache-maven-3.5.4/conf/nkSettings.xml \
    -DgroupId=com.icbc -DartifactId=icbc-ca -Dversion=1.0.0 \
    -Dpackaging=jar -Dfile=lib/icbc-ca.jar \
    -DrepositoryId=nkrelease \
    -Durl=http://120.25.120.234:8081/nexus/repository/nkrelease/
```

### 4.3 验证上传结果

```bash
# 浏览 Nexus 上的工行 artifact 列表
curl -u admin:bzc123456 \
  "http://120.25.120.234:8081/nexus/service/rest/repository/browse/nkrelease/com/icbc/"
```

期望看到 5 个目录：
```
hsm-software-share
icbc-api-sdk-cop
icbc-api-sdk-cop-io
icbc-ca
infosec-crypto
```

---

## 五、pom.xml 改造

### 5.1 改造前（system scope 方式）

```xml
<dependencies>
    <!-- 工行开放平台 SDK，本地 jar 引入 -->
    <dependency>
        <groupId>com.icbc</groupId>
        <artifactId>icbc-api-sdk-cop</artifactId>
        <version>20260415</version>
        <scope>system</scope>
        <systemPath>${project.basedir}/lib/icbc-api-sdk-cop.jar</systemPath>
    </dependency>
    <dependency>
        <groupId>com.icbc</groupId>
        <artifactId>icbc-api-sdk-cop-io</artifactId>
        <version>20260415</version>
        <scope>system</scope>
        <systemPath>${project.basedir}/lib/icbc-api-sdk-cop-io.jar</systemPath>
    </dependency>
    <dependency>
        <groupId>com.icbc</groupId>
        <artifactId>hsm-software-share</artifactId>
        <version>1.0.5</version>
        <scope>system</scope>
        <systemPath>${project.basedir}/lib/hsm-software-share-1.0.5.jar</systemPath>
    </dependency>
    <dependency>
        <groupId>com.icbc</groupId>
        <artifactId>infosec-crypto</artifactId>
        <version>1.02</version>
        <scope>system</scope>
        <systemPath>${project.basedir}/lib/InfosecCrypto_Java1_02_JDK14+.jar</systemPath>
    </dependency>
    <dependency>
        <groupId>com.icbc</groupId>
        <artifactId>icbc-ca</artifactId>
        <version>1.0.0</version>
        <scope>system</scope>
        <systemPath>${project.basedir}/lib/icbc-ca.jar</systemPath>
    </dependency>
</dependencies>
```

### 5.2 改造后（Nexus 私服方式）

```xml
<dependencies>
    <!-- 工行开放平台 SDK，从 Nexus 私服拉取 -->
    <dependency>
        <groupId>com.icbc</groupId>
        <artifactId>icbc-api-sdk-cop</artifactId>
        <version>20260415</version>
    </dependency>
    <dependency>
        <groupId>com.icbc</groupId>
        <artifactId>icbc-api-sdk-cop-io</artifactId>
        <version>20260415</version>
    </dependency>
    <dependency>
        <groupId>com.icbc</groupId>
        <artifactId>hsm-software-share</artifactId>
        <version>1.0.5</version>
    </dependency>
    <dependency>
        <groupId>com.icbc</groupId>
        <artifactId>infosec-crypto</artifactId>
        <version>1.02</version>
    </dependency>
    <dependency>
        <groupId>com.icbc</groupId>
        <artifactId>icbc-ca</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>

<repositories>
    <!-- 工行 SDK 私服仓库；id 必须与 settings.xml 中 <server id> 一致以应用认证 -->
    <repository>
        <id>nkrelease</id>
        <name>ICBC SDK Repo</name>
        <url>http://120.25.120.234:8081/nexus/repository/nkrelease/</url>
        <releases>
            <enabled>true</enabled>
        </releases>
        <snapshots>
            <enabled>false</enabled>
        </snapshots>
    </repository>
</repositories>
```

### 5.3 关键变化

| 项 | 改造前 | 改造后 |
|---|---|---|
| `<scope>` | `system` | 默认（compile） |
| `<systemPath>` | 必须 | 删除 |
| 项目 `lib/` 目录 | 必须存在 | 可以删除 |
| `<repositories>` | 不需要 | 必须新增（指向 Nexus） |

---

## 六、settings.xml 配置（开发者本地 / CI）

每个开发者或 CI 构建机器，必须在 Maven 的 `settings.xml` 中配置 nkrelease 仓库的认证信息，否则会返回 **401 Unauthorized**。

### 6.1 配置位置（任选其一）

| 位置 | 影响范围 |
|---|---|
| `~/.m2/settings.xml`（用户级，推荐） | 当前用户所有项目 |
| `${maven.home}/conf/settings.xml`（全局） | 该机器所有用户 |
| 项目自定义 settings 文件 + `mvn -s xxx.xml` | 仅指定时使用 |

### 6.2 必备配置

```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0">
    <servers>
        <!-- 工行 SDK 私服认证 -->
        <server>
            <id>nkrelease</id>
            <username>admin</username>
            <password>bzc123456</password>
        </server>
    </servers>
</settings>
```

### 6.3 认证匹配原理

Maven 通过 `<server id>` 与 `<repository id>` 或 `<mirror id>` 进行匹配：

```
pom.xml <repository id="nkrelease">
                ↓ 匹配
settings.xml <server id="nkrelease">
                ↓ 应用
访问私服时使用 admin / bzc123456 认证
```

**❗常见错误**：repository id 与 server id 不一致，即使认证已配，Maven 也不会应用，结果 401。

---

## 七、团队协作流程

### 7.1 SDK 升级时（如工行发布新版 SDK）

```bash
# 1. 替换 lib/ 目录的 jar 包
# 2. 上传新版本到 Nexus（提升 version 号，nkrelease 仓库一般是 ALLOW_ONCE 写策略，同版本不可覆盖）
mvn deploy:deploy-file ... -Dversion=20260520 ...
# 3. 修改 pom.xml 中对应 dependency 的 <version>
# 4. 提交 pom.xml 到 git
# 5. 通知团队成员 mvn -U clean compile 强制更新
```

### 7.2 新成员加入项目

1. 拉取项目代码（`lib/` 目录已不存在）
2. 在自己的 `~/.m2/settings.xml` 加上 §6.2 的 `<server>` 配置
3. `mvn clean compile` 自动从 Nexus 拉取所有依赖

### 7.3 CI/CD（Jenkins / GitLab CI 等）

在 CI 的构建机器或容器镜像中：
1. 准备含完整 `<server>` 认证的 `settings.xml`
2. 构建命令使用 `mvn -s /path/to/settings.xml clean package`

Docker 示例：
```dockerfile
COPY ci-settings.xml /root/.m2/settings.xml
RUN mvn clean package -DskipTests
```

---

## 八、常见问题

### Q1: `Not authorized, ReasonPhrase: Unauthorized`（401）

**原因**：settings.xml 缺少 `<server id="nkrelease">` 配置，或 server id 与 pom 中 repository id 不一致。

**排查**：
```bash
# 检查 settings.xml 是否包含 nkrelease 的 server
grep -A3 "<server>" ~/.m2/settings.xml | grep -B1 "nkrelease"
```

### Q2: `Premature end of Content-Length delimited message body`

**原因**：私服或网络传输大 jar 时连接中断（icbc-api-sdk-cop-io 有 17MB）。

**解决**：直接重试 `mvn compile`。Maven 会自动续传或重新下载。

### Q3: `Cannot access nkrelease in offline mode`

**原因**：使用了 `-o`（offline）模式，且本地仓库没有该 jar 缓存。

**解决**：去掉 `-o` 参数，让 Maven 联网拉取。

### Q4: 同版本 jar 上传第二次失败

**原因**：Nexus hosted 仓库通常默认 `writePolicy=ALLOW_ONCE`，禁止覆盖已存在的 release 版本。

**解决**：
- 提升版本号后重新上传
- 或联系 Nexus 管理员临时改为 `ALLOW`，覆盖后改回

### Q5: Spring Boot fat jar 是否包含工行 SDK？

**改造前（system scope）**：默认**不**包含。需要在 `spring-boot-maven-plugin` 配置 `<includeSystemScope>true</includeSystemScope>`。

**改造后（Nexus 依赖）**：默认**包含**，无需特殊配置，`java -jar xxx.jar` 直接可用。

---

## 九、回滚方案

如需回退到 `system` scope 方式：

1. 恢复项目 `lib/` 目录的 5 个 jar
2. 还原 pom.xml 到 §5.1 状态
3. 删除 `<repositories>` 节点
4. （可选）从 Nexus 删除 5 个上传的 artifact

---

## 十、参考资料

- [Maven Deploy Plugin - deploy-file](https://maven.apache.org/plugins/maven-deploy-plugin/deploy-file-mojo.html)
- [Maven Settings - servers](https://maven.apache.org/settings.html#Servers)
- [Sonatype Nexus Repository Manager 文档](https://help.sonatype.com/repomanager3)
- 工行开放平台：<https://open.icbc.com.cn>
