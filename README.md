# 民政养老补贴资格复核服务

## 项目简介

本项目是民政系统养老补贴资格复核服务，基于 Spring Boot 开发，实现老人档案管理、补贴类型管理、社区核查、异动预警、停发补发和财政拨付等核心业务功能。

## 技术栈

- Java 17
- Spring Boot 3.2.5
- Spring Data JPA
- MySQL 8.0
- Lombok
- Maven

## 原始需求

> 民政系统需要养老补贴资格复核服务，Spring Boot 接口管理老人档案、补贴类型、社区核查、异动预警、停发补发和财政拨付。业务字段包括老人身份证、户籍、年龄、失能等级、低保状态、银行卡、居住地址、社区探访、死亡注销、迁出记录和发放月份。社区工作人员定期复核老人是否仍符合高龄、护理或困难补贴；民政部门审核异动；财政按有效名单拨付。服务要区分信息未核实、资格变化、老人去世、迁出辖区、银行卡异常和历史月份补发。

## 核心功能模块

### 1. 老人档案管理
- 老人信息增删改查
- 死亡注销登记
- 迁出辖区登记
- 支持按姓名、身份证、社区、状态等多条件查询

### 2. 补贴类型管理
- 补贴类型配置（高龄补贴、护理补贴、困难补贴）
- 补贴金额、申请条件配置
- 补贴启用/停用管理

### 3. 老人补贴资格
- 补贴申请
- 资格审核
- 补贴停发/恢复
- 补贴资格查询

### 4. 社区核查
- 社区工作人员定期核查录入
- 核查结果审核
- 核查记录查询
- 自动生成异动预警

### 5. 异动预警
- 信息未核实预警
- 资格变化预警
- 老人去世预警
- 迁出辖区预警
- 银行卡异常预警
- 历史月份补发预警
- 预警处理与审核

### 6. 停发补发
- 补贴停发
- 补贴恢复
- 历史月份补发
- 调整记录查询

### 7. 财政拨付
- 月度拨付单生成
- 拨付执行
- 拨付失败重试
- 补发拨付
- 拨付记录查询

## 启动方式

### 前置要求

- JDK 17 或更高版本
- Maven 3.6+
- MySQL 8.0+

### 本地启动

#### 1. 准备数据库

创建 MySQL 数据库：

```sql
CREATE DATABASE pension_review CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

#### 2. 配置数据库连接

修改 `src/main/resources/application.yml` 中的数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/pension_review?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&useSSL=false
    username: root
    password: 123456
```

#### 3. 安装依赖

```bash
mvn clean install -DskipTests
```

#### 4. 启动服务

```bash
mvn spring-boot:run
```

访问地址：http://localhost:8080/api

### Docker 一键启动（推荐）

#### 前置要求

- Docker 20.10+
- Docker Compose v2+

#### 启动命令

```bash
docker compose up --build
```

后台运行：

```bash
docker compose up --build -d
```

#### 停止和清理

```bash
docker compose down
```

如需清理数据卷：

```bash
docker compose down -v
```

#### 访问地址

- 服务地址：http://localhost:8080/api
- MySQL：localhost:3306，数据库名：pension_review，用户名：root，密码：123456

#### Docker Compose 服务说明

- `mysql`: MySQL 8.0 数据库服务
- `app`: Spring Boot 应用服务，依赖 MySQL 服务启动

## API 接口列表

所有接口前缀：`/api`

### 老人档案管理

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /elders | 新增老人档案 |
| PUT | /elders/{id} | 更新老人档案 |
| DELETE | /elders/{id} | 删除老人档案 |
| GET | /elders/{id} | 根据ID查询老人 |
| GET | /elders/id-card/{idCard} | 根据身份证查询老人 |
| GET | /elders | 分页查询老人列表 |
| POST | /elders/{id}/deceased | 标记老人去世 |
| POST | /elders/{id}/moved-out | 标记老人迁出 |

### 补贴类型管理

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /subsidy-types | 新增补贴类型 |
| PUT | /subsidy-types/{id} | 更新补贴类型 |
| DELETE | /subsidy-types/{id} | 删除补贴类型 |
| GET | /subsidy-types/{id} | 根据ID查询补贴类型 |
| GET | /subsidy-types | 分页查询补贴类型列表 |
| GET | /subsidy-types/active | 查询所有启用的补贴类型 |

### 老人补贴资格

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /elder-subsidies/apply | 申请补贴 |
| POST | /elder-subsidies/{id}/approve | 审核通过补贴 |
| POST | /elder-subsidies/{id}/suspend | 停发补贴 |
| POST | /elder-subsidies/{id}/reactivate | 恢复补贴 |
| POST | /elder-subsidies/{id}/cancel | 取消补贴 |
| GET | /elder-subsidies/{id} | 查询补贴详情 |
| GET | /elder-subsidies/elder/{elderId} | 查询老人所有补贴 |
| GET | /elder-subsidies | 分页查询补贴列表 |
| GET | /elder-subsidies/active | 查询所有有效补贴 |
| GET | /elder-subsidies/unverified | 查询所有未核实补贴 |

### 社区核查

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /community-checks | 新增核查记录 |
| PUT | /community-checks/{id} | 更新核查记录 |
| POST | /community-checks/{id}/audit | 审核核查记录 |
| GET | /community-checks/{id} | 查询核查详情 |
| GET | /community-checks/elder/{elderId} | 查询老人核查记录 |
| GET | /community-checks | 分页查询核查列表 |
| GET | /community-checks/pending | 查询待审核核查 |
| GET | /community-checks/month/{checkMonth} | 按月查询核查记录 |

### 异动预警

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /abnormal-alerts | 新增异动预警 |
| POST | /abnormal-alerts/{id}/handle | 处理异动预警 |
| POST | /abnormal-alerts/generate-unverified | 生成未核实信息预警 |
| GET | /abnormal-alerts/{id} | 查询预警详情 |
| GET | /abnormal-alerts/elder/{elderId} | 查询老人预警记录 |
| GET | /abnormal-alerts | 分页查询预警列表 |
| GET | /abnormal-alerts/pending | 查询待处理预警 |
| GET | /abnormal-alerts/type/{alertType} | 按类型查询预警 |

### 停发补发

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /subsidy-adjustments/suspend | 创建停发调整 |
| POST | /subsidy-adjustments/reactivate | 创建恢复调整 |
| POST | /subsidy-adjustments/reissue | 创建补发调整 |
| GET | /subsidy-adjustments/{id} | 查询调整详情 |
| GET | /subsidy-adjustments/elder/{elderId} | 查询老人调整记录 |
| GET | /subsidy-adjustments | 分页查询调整列表 |
| GET | /subsidy-adjustments/month/{adjustMonth} | 按月查询调整记录 |
| GET | /subsidy-adjustments/type/{adjustType} | 按类型查询调整记录 |

### 财政拨付

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /finance-payments/generate-monthly | 生成月度拨付单 |
| POST | /finance-payments/generate-reissue/{adjustId} | 生成补发拨付单 |
| POST | /finance-payments/execute | 执行拨付 |
| POST | /finance-payments/{id}/retry | 重试拨付失败记录 |
| POST | /finance-payments/{id}/cancel | 取消拨付 |
| GET | /finance-payments/{id} | 查询拨付详情 |
| GET | /finance-payments/elder/{elderId} | 查询老人拨付记录 |
| GET | /finance-payments | 分页查询拨付列表 |
| GET | /finance-payments/month/{paymentMonth} | 按月查询拨付记录 |
| GET | /finance-payments/batch/{batchNo} | 按批次查询拨付记录 |
| GET | /finance-payments/pending | 查询待拨付记录 |

### 资格复核

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /review/monthly | 执行月度复核 |
| GET | /review/dashboard | 获取复核仪表盘统计 |
| POST | /review/process-check/{checkId} | 处理核查及关联预警 |
| POST | /review/historical-reissue | 创建历史月份补发 |

## 统一响应格式

所有接口返回格式统一：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

- `code`: 状态码，200 表示成功，其他表示失败
- `message`: 提示信息
- `data`: 返回数据

## 初始化数据

服务启动后会自动初始化以下测试数据：

### 补贴类型
1. 高龄补贴（HIGH_AGE）- 年满80周岁，每月200元
2. 护理补贴（NURSING）- 失能老人，每月300元
3. 困难补贴（DIFFICULTY）- 低保老人，每月250元

### 测试老人
系统预置5位测试老人，包含不同年龄、不同补贴资格的样例数据。

## 目录结构

```
src/main/java/com/civil/pension/
├── PensionSubsidyReviewApplication.java  # 启动类
├── common/                                # 公共类
│   ├── Result.java                        # 统一响应
│   └── PageResult.java                    # 分页响应
├── config/                                # 配置类
│   └── DataInitializer.java               # 数据初始化
├── controller/                            # 控制器层
│   ├── ElderController.java
│   ├── SubsidyTypeController.java
│   ├── ElderSubsidyController.java
│   ├── CommunityCheckController.java
│   ├── AbnormalAlertController.java
│   ├── SubsidyAdjustmentController.java
│   ├── FinancePaymentController.java
│   └── ReviewController.java
├── entity/                                # 实体类
│   ├── Elder.java                         # 老人档案
│   ├── SubsidyType.java                   # 补贴类型
│   ├── ElderSubsidy.java                  # 老人补贴
│   ├── CommunityCheck.java                # 社区核查
│   ├── AbnormalAlert.java                 # 异动预警
│   ├── SubsidyAdjustment.java             # 停发补发
│   └── FinancePayment.java                # 财政拨付
├── enums/                                 # 枚举类
│   ├── DisabilityLevel.java               # 失能等级
│   ├── ElderStatus.java                   # 老人状态
│   ├── SubsidyStatus.java                 # 补贴状态
│   ├── AlertType.java                     # 预警类型
│   ├── CheckStatus.java                   # 核查状态
│   └── PaymentStatus.java                 # 拨付状态
├── exception/                             # 异常处理
│   ├── BusinessException.java             # 业务异常
│   └── GlobalExceptionHandler.java        # 全局异常处理
├── repository/                            # 数据访问层
│   ├── ElderRepository.java
│   ├── SubsidyTypeRepository.java
│   ├── ElderSubsidyRepository.java
│   ├── CommunityCheckRepository.java
│   ├── AbnormalAlertRepository.java
│   ├── SubsidyAdjustmentRepository.java
│   └── FinancePaymentRepository.java
└── service/                               # 业务逻辑层
    ├── ElderService.java
    ├── SubsidyTypeService.java
    ├── ElderSubsidyService.java
    ├── CommunityCheckService.java
    ├── AbnormalAlertService.java
    ├── SubsidyAdjustmentService.java
    ├── FinancePaymentService.java
    └── ReviewService.java
```

## 业务流程说明

### 补贴申请流程
1. 录入老人档案信息
2. 为老人申请相应补贴
3. 民政部门审核通过
4. 补贴生效，进入发放名单

### 死亡/迁出异动处理流程
1. 外部系统或人工上报老人去世/迁出信息
2. 系统接收异动，立即暂停该老人所有后续月份补贴
3. 自动生成社区核查任务，由社区工作人员核实
4. 自动生成异动预警，供民政部门审核
5. 历史已发放的补贴记录完整保留，不删除

### 失能等级变更流程
1. 社区复核上传失能等级评估结果
2. 系统根据新失能等级计算对应补贴金额
3. 创建可追溯的金额调整记录（记录原等级、新等级、原金额、新金额）
4. 从下月起按新金额计算补贴
5. 历史已发放月份的记录保持不变，确保可追溯

### 银行卡异常处理流程
1. 财政拨付时自动校验银行卡信息
2. 校验失败时：标记失败详细原因、生成银行卡异常预警、自动暂停补贴
3. 社区工作人员联系家属更新银行账户信息
4. 更新银行卡信息后，系统自动恢复暂停的补贴
5. 对拨付失败的记录重新发放

### 月度复核流程
1. 社区工作人员对辖区老人进行定期核查
2. 核查发现异常情况（去世、迁出、银行卡异常等）
3. 系统自动生成异动预警
4. 民政部门审核异动预警
5. 审核通过后自动执行补贴停发/恢复等调整
6. 生成财政拨付单
7. 财政部门按有效名单拨付

### 历史补发流程
1. 确认需要补发的历史月份
2. 创建补发调整记录
3. 生成补发拨付单
4. 执行补发拨付

## 注意事项

1. 服务启动时会自动初始化测试数据，方便测试使用
2. 生产环境请修改数据库密码和相关敏感配置
3. 数据库表结构由 JPA 自动创建（`ddl-auto: update`）
4. 所有金额单位为元，保留两位小数
5. 月份格式统一为 `yyyy-MM`，如 `2024-01`
