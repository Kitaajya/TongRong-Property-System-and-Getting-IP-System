# 通融物管 APP (TongRong Property Management) v2.1.0

---

## 项目概述

通融物管 APP 是一个基于 Spring Boot 构建的**物业管理综合后端服务系统**，提供**用户认证授权、采购管理、商品库存管理、员工信息管理、实时天气查询、IP智能定位及文件导出**等核心功能。项目采用 RESTful API 设计，内置多级缓存机制以应对高并发场景，适用于中小型物业公司的日常运营管理。

> **注⚠️：本人声明，此说明书本人懒得写，直接用deepseek生成的。后端java、Spring Boot、数据库MySQL由本人所写，但是前端界面代码是Opencode AI写的，因为我早就不学前端三件套了，所以关于前端的框架知识已经忘得一干二净。有时候学长学姐们说我有的后端代码写的不合理，可我根本就不知道哪里错了，所以我直接用AI改了。另外，我写代码是怎么舒服怎么来的，因此，不小心把controller和业务代码写在了一起，没办法，只能重构。所以在2026年8月16日进行了项目重构.前端界面我用的感觉良好，如果有建议修改的话可以联系我。如果本系统有什么问题与本说明书不符，可以发QQ邮箱联系本人，联系方式在最下面**


**当前版本：** v2.1.0  
**开发总周期（包括所有历史版本）：** 2026年7月27日 - 2026年8月15日

| APP版本 | 改动说明 |
|------|------|
| 1.1.1  | 用Spring Boot实现了简单的增删改查功能，仅局限于控制台 |
| 1.1.2  | 引入MySQL数据库添加100个不同职业与身份的员工 |
| 1.1.3  | 用Spring Boot实现了增删改查 |
| 1.1.4  | 利用现有API实现了天气查询功能以及用户登录-注册功能 |
| 1.2.1  | 用Spring Boot实现了API增删改查和IO操作、增加了商品-购物系统 |
| 1.2.2  | 用Opencode AI制作前端界面，使得Spring Boot的增删改查操作可视化 |
| 1.2.3  | 前端增加了评论功能，但只能做到单独评论，无法互动 |
| 1.2.4  | 前端增加了可互动评论功能，通过折叠评论嵌套层楼评论实现互动，正想办法增加消息功能 |
| 1.2.4(新增功能)  | 后端新增QQ邮箱SMTP服务的验证码功能 |
| 1.2.4(新增功能)  | 身份认证：分为普通用户、物管、商家三类，各自看到的前端内容不同 |
| 1.2.4(新增功能)  | 消息通知功能上线 |
| 1.2.4(新增功能)  | 点赞与删除自己评论功能上线 |
| 1.2.4(新增功能)  | 改名功能上线 |
| 2.1.0  | 继1.2.4版本，对本系统进行了重构，分离controller与业务代码 |
 

---

## 技术栈

| 技术 | 版本 |
|------|------|
| Spring Boot | 4.1.0 |
| Java | 17 |
| MySQL | 8.4.8 |
| Spring JDBC | 7.0.8 |
| Maven | 3.9.16 |
| Servlet API | Jakarta EE |

---

## 一、核心功能模块

### 1. 用户认证与授权系统

提供完整的**注册、登录、登出、登录状态查询**功能，未登录用户无法访问任何 `/api/**` 接口。

#### 功能清单

| 功能 | 请求方式 | 路径 | 说明 |
|------|----------|------|------|
| 用户注册 | POST | `/api/auth/register` | 用户名(必填)、密码(≥6位)、邮箱/姓名/电话(选填) |
| 用户登录 | POST | `/api/auth/login` | 验证密码，创建会话，记录登录日志 |
| 用户登出 | POST | `/api/auth/logout` | 销毁会话 |
| 登录状态 | GET | `/api/auth/status` | 返回当前登录用户信息 |

#### 安全机制

- **密码加密：** SHA-256(明文密码 + salt) 十六进制存储
- **会话管理：** HttpSession 存储用户信息（id, username, fullName）
- **拦截器保护：** 所有 `/api/**` 请求（登录/注册接口除外）需登录访问
- **登录日志：** 自动记录 IP、User-Agent、登录时间、登录结果

#### 数据表结构

**LogIn.users（用户主表）**

| 字段 | 类型 | 说明 |
|------|------|------|
| id | INT UNSIGNED | 用户ID(主键) |
| username | VARCHAR(50) | 用户名(登录账号) |
| email | VARCHAR(100) | 电子邮箱(唯一) |
| password_hash | CHAR(64) | SHA2(明文+salt,256) |
| salt | VARCHAR(32) | 密码盐值 |
| full_name | VARCHAR(100) | 真实姓名 |
| phone | VARCHAR(20) | 手机号码 |
| status | TINYINT | 1-正常, 0-禁用 |
| last_login_ip | VARCHAR(45) | 最后登录IP |
| last_login_time | DATETIME | 最后登录时间 |
| login_count | INT UNSIGNED | 登录总次数 |

**LogIn.login_logs（登录日志表）**

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT UNSIGNED | 日志ID |
| user_id | INT UNSIGNED | 用户ID |
| login_time | DATETIME | 登录时间 |
| ip_address | VARCHAR(45) | 登录IP |
| user_agent | VARCHAR(500) | 浏览器信息 |
| login_result | TINYINT | 1-成功, 0-失败 |

#### 默认测试账号

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | 123456 | 管理员 |
| zhangsan | 123456 | 普通用户 |
| lisi | 123456 | 普通用户 |

---

### 2. 采购管理系统

提供完整的**商品管理、库存管理、订单审批、评论系统**功能，覆盖商品从入库到出库的全生命周期。

#### 2.1 商品管理

| 功能 | 请求方式 | 路径 | 说明 |
|------|----------|------|------|
| 查看全部商品 | GET | `/api/purchase/travel` | 返回所有商品列表 |
| 查询单个商品 | GET | `/api/purchase/select/single/product` | 按名称精确查询 |
| 模糊搜索 | GET | `/api/purchase/select/single/product/undefined` | 名称/描述/供应商模糊匹配 |
| 添加商品 | POST | `/api/purchase/add/single/product` | 单个商品入库 |
| 批量添加 | POST | `/api/purchase/add/more/product` | 批量插入相同商品 |
| 修改商品 | PUT | `/api/purchase/edit/single/product` | 更新商品信息 |
| 删除商品 | DELETE | `/api/purchase/delete/single/product` | 需提供ID和名称双重验证 |

#### 2.2 库存管理

| 功能 | 请求方式 | 路径 | 说明 |
|------|----------|------|------|
| 库存检查 | GET | `/api/purchase/check/stock` | 检查指定商品库存是否≥12 |
| 库存预警 | GET | `/api/purchase/stock/warning` | 列出所有库存低于阈值(默认12)的商品 |
| 商品发货 | PUT | `/api/purchase/send/single/product` | 原子扣减库存(STOCK-1)，需库存>12 |

**库存预警阈值：** 默认12件，可通过 `@RequestParam(defaultValue = "12")` 动态调整

**发货安全机制：**
```sql
UPDATE products SET STOCK = STOCK - 1 WHERE name = ? AND STOCK > 12
```
使用原子SQL操作，避免并发竞态条件。

#### 2.3 价格计算

| 功能 | 请求方式 | 路径 | 说明 |
|------|----------|------|------|
| 单个商品价格 | GET | `/api/purchase/select/single/product/price` | 查询单个商品价格 |
| 多个商品价格 | GET | `/api/purchase/select/more/product/price` | 批量查询价格 |
| 计算总价(单商品×数量) | GET | `/api/purchase/calculate/total/price` | 单价×数量 |
| 计算总价(多商品求和) | GET | `/api/purchase/calculate/total/price/with/name` | 多个商品价格求和 |

#### 2.4 订单审批流程

| 功能 | 请求方式 | 路径 | 说明 |
|------|----------|------|------|
| 查看待审核 | GET | `/api/purchase/pending` | 列出所有 status='待审核' 的商品 |
| 审批商品 | PUT | `/api/purchase/review` | 更新状态为 '已通过' 或 '已驳回' |

**状态流转：** 待审核 → 已通过/已驳回

#### 2.5 商品评论系统

| 功能 | 请求方式 | 路径 | 说明 |
|------|----------|------|------|
| 发表评论 | POST | `/api/purchase/comment/insert` | 自动关联当前登录用户 |
| 查看评论 | GET | `/api/purchase/comment/list` | 可指定商品名过滤(可选) |

**评论特点：**
- 未登录用户无法操作本系统的任何功能
- 评论包含：商品名、用户名、内容、创建时间
- 评论可以回复指定人物，包括自己
- 评论暂时未安装闪屏功能
- 不按商品名过滤查询，显示所有评论，不分类别

---

### 3. 员工信息管理

提供完整的员工档案**增、删、改、查**功能，所有数据存储于 `log_in` 表。

| 功能 | 请求方式 | 路径 | 说明 |
|------|----------|------|------|
| 新增员工 | POST | `/api/add` | 自动生成20位UUID作为主键 |
| 编辑员工 | POST | `/api/edit` | 根据ID更新员工信息 |
| 删除员工 | GET | `/api/delete` | 根据ID删除员工记录 |
| 查询全部 | GET | `/api/select` | 返回所有员工列表 |

**数据表结构（log_in）**

| 字段 | 类型 | 说明 |
|------|------|------|
| id | VARCHAR(20) | 主键，UUID生成(20位) |
| name | VARCHAR(5) | 员工姓名 |
| department | VARCHAR(20) | 所属部门 |
| gender | VARCHAR(1) | 性别 |
| work | VARCHAR(10) | 岗位/工种 |

**请求示例：**
```bash
# 新增员工
POST /api/add?name=张三&department=工程部&gender=男&work=维修

# 编辑员工
POST /api/edit?id=e770f25b40d04e8aa93a&name=李四&department=财务部&gender=女&work=会计

# 删除员工
GET /api/delete?id=e770f25b40d04e8aa93a

# 查询全部
GET /api/select
```

---

### 4. 智能IP定位

根据访问者IP自动识别地理位置，为日志记录、区域化服务和天气查询提供位置上下文。

#### 定位策略（双接口自动故障切换）

| 优先级 | 服务商 | 接口地址 | 说明 |
|--------|--------|----------|------|
| 主接口 | pconline | `whois.pconline.com.cn/ipJson.jsp` | 返回省+市，响应快 |
| 备用接口 | ip-api | `ip-api.com/json` | 主接口失败时自动切换 |

#### 内网智能识别

自动识别以下内网/本地地址，不发起外部查询：
- 127.x、0.x、localhost
- 10.x、172.x、192.168.x
- ::1（IPv6本地回环）

#### 查询结果示例

| 访问来源 | 返回结果 |
|----------|----------|
| 本机/内网 | 本机/内网 |
| 北京公网IP | 北京市 |
| 广东省深圳市 | 广东省深圳市 |
| 查询失败 | 返回IP原文 |

#### 缓存机制

| 缓存对象 | 有效期 | 实现方式 |
|----------|--------|----------|
| IP → 地理位置 | 永久(进程生命周期) | ConcurrentHashMap |

**效果：** 同一IP只查询一次，后续请求毫秒级响应

---

### 5. 实时天气查询

集成 **Open-Meteo** 免费天气 API，提供基于地理位置的实时天气信息。

#### 接口信息

| 功能 | 请求方式 | 路径 |
|------|----------|------|
| 获取天气文本 | GET | `/api/weather/getWeather` |
| 获取结构化信息 | GET | `/api/weather/info` |

#### 返回内容（/info 接口）

```json
{
  "location": "北纬40.19、东经118.12地区",
  "temperature": 26.5,
  "windSpeed": 12.3,
  "weatherCode": 0,
  "weatherText": "晴"
}
```

#### 天气编码对照表

| WMO编码 | 天气状况 |
|---------|----------|
| 0 | 晴 |
| 1, 2, 3 | 多云 |
| 45, 48 | 雾 |
| 51, 53, 55 | 毛毛雨/小雨 |
| 61, 63, 65 | 雨 |
| 80, 81, 82 | 阵雨 |
| 95, 96, 99 | 雷暴 |
| 其他 | 未知天气 |

#### 默认监测坐标

| 参数 | 值 |
|------|-----|
| 北纬 | 40.191534° |
| 东经 | 118.120463° |
| 时区 | Asia/Shanghai |

---

### 6. 文件导出功能

支持将数据库表数据导出为文本文件，**内置白名单校验**防止SQL注入。

| 功能 | 请求方式 | 路径 | 说明 |
|------|----------|------|------|
| 读取表数据 | GET | `/api/operate/select/database/name` | 白名单校验后查询 |
| 写入文件 | GET | `/api/operate/write` | 读取表数据并追加到文件 |

**安全机制：**
```java
// 白名单校验：只允许合法的表名
List<String> validTableNames = jdbcTemplate.queryForList(
    "SELECT table_name FROM information_schema.tables WHERE table_schema = DATABASE()", 
    String.class
);
if (!validTableNames.contains(nameOfPointedDatabaseName))
    throw new IllegalArgumentException("非法的表名");
```

---

### 7. 个人认证（模拟）

| 功能 | 请求方式 | 路径 | 说明 |
|------|----------|------|------|
| 实名认证查询 | GET | `/api/permission/get` | 验证姓名+身份证号是否匹配 |

**数据表：** `id_card_auth`（100条模拟实名数据）

---

## 二、高性能缓存机制

为应对高并发访问，项目内置多级缓存，无需额外引入Redis：

| 缓存类型 | 缓存对象 | 有效期 | 实现方式 |
|----------|----------|--------|----------|
| IP地区缓存 | IP → 地理位置 | 永久 | ConcurrentHashMap |
| 天气数据缓存 | 格式化天气文本 | 10分钟 | ConcurrentHashMap |
| 天气数据缓存 | 温度/风速/编码 | 10分钟 | ConcurrentHashMap |
| 应用启动预热 | 天气数据 | 启动时异步加载 | ApplicationReadyEvent |

**核心优势：** IP定位和天气查询均优先命中缓存，不重复调用外部API，大幅降低响应延迟和外部接口调用费用。

---

## 三、项目结构

```
TongRong_Property_Company_2nd/
├── pom.xml
├── src/main/java/org/designer/tongrong_property_company_2nd/
│   ├── TongRongPropertyCompany2ndApplication.java
│   ├── SetPersonalPermission.java
│   ├── GetNameOfDatabaseWhichWeForgot.java
│   │
│   ├── auth/                              # 认证模块
│   │   ├── AuthController.java                # /api/auth/**
│   │   ├── AuthService.java                   # 注册/登录业务逻辑
│   │   ├── AuthMapper.java                    # 用户表 SQL
│   │   ├── EmailCodeService.java              # 邮箱验证码
│   │   ├── LoginInterceptor.java              # 登录拦截器
│   │   └── WebConfig.java                     # 拦截器注册
│   │
│   ├── common/                            # 公共组件
│   │   ├── SessionHelper.java                 # Session 工具类
│   │   ├── PermissionInterceptor.java         # 商家权限拦截器
│   │   ├── MerchantOnly.java                  # @MerchantOnly 注解
│   │   ├── AccessDeniedException.java         # 权限不足异常
│   │   └── GlobalExceptionHandler.java        # 全局异常处理
│   │
│   ├── controller/                        # 控制器层
│   │   ├── WorkerController.java              # /api/** 员工管理
│   │   └── PurchaseController.java            # /api/purchase/** 商品/评论/消息/用户
│   │
│   ├── entity/                            # 实体类
│   │   ├── Worker.java
│   │   ├── User.java
│   │   ├── Product.java
│   │   ├── Comment.java
│   │   ├── CommentMessage.java
│   │   └── Person.java
│   │
│   ├── mapper/                            # 数据访问层
│   │   ├── WorkerMapper.java
│   │   ├── UserMapper.java
│   │   ├── ProductMapper.java
│   │   ├── CommentMapper.java
│   │   ├── PermissionMapper.java
│   │   └── DatabaseMapper.java
│   │
│   ├── service/                           # 业务逻辑层
│   │   ├── WorkerService.java
│   │   ├── UserService.java
│   │   ├── ProductService.java
│   │   ├── CommentService.java
│   │   ├── PermissionService.java
│   │   └── DatabaseService.java
│   │
│   ├── weather/                           # (未动)
│   │   └── OpenMeteoDemo.java
│   └── io/                                # (未动)
│       └── GetInformationToFile.java
│
├── src/main/resources/
│   └── application.properties
└── uploads/
```

---

## 四、快速开始

### 环境要求

- JDK 17+
- MySQL 8.0+
- Maven 3.6+（或使用内置 Maven Wrapper）

### 启动步骤

```bash
# 1. 克隆仓库
git clone [repository-url]

# 2. 进入项目目录
cd TongRong_Property_Company_2nd

# 3. 创建数据库
mysql -u root -p
CREATE DATABASE TongRong_Company CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE LogIn CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE PurchaseBase CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 4. 执行SQL脚本（按顺序）
source src/main/sql/login_system.sql
source src/main/sql/comments.sql
source src/main/sql/mock_id_card_auth.sql

# 5. 修改 application.properties 中的数据库连接信息

# 6. 启动应用
./mvnw spring-boot:run    # Linux/Mac
mvnw.cmd spring-boot:run  # Windows
```

应用启动后默认监听 `http://localhost:8080`

---

## 五、API 接口测试示例（本机测试|暂时未制定固定url）

### 1. 用户注册
```bash
curl -X POST "http://localhost:8080/api/auth/register" \
  -d "username=testuser&password=123456&fullName=测试用户&email=test@example.com"
```

### 2. 用户登录
```bash
curl -X POST "http://localhost:8080/api/auth/login" \
  -d "username=testuser&password=123456"
```

### 3. 获取天气信息
```bash
curl "http://localhost:8080/api/weather/info"
```

### 4. 查看全部商品
```bash
curl "http://localhost:8080/api/purchase/travel"
```

### 5. 添加商品
```bash
curl -X POST "http://localhost:8080/api/purchase/add/single/product" \
  -d "productsName=测试商品&productsCategory=办公用品&productsPrice=99.99&productsStock=50&productsSupplier=测试供应商&productsDescription=测试描述"
```

### 6. 库存检查
```bash
curl "http://localhost:8080/api/purchase/check/stock?productName=测试商品"
```

### 7. 商品发货
```bash
curl -X PUT "http://localhost:8080/api/purchase/send/single/product?productName=测试商品"
```

### 8. 发表评论
```bash
curl -X POST "http://localhost:8080/api/purchase/comment/insert" \
  -d "productName=测试商品&content=这是一个测试评论"
```

### 9. 新增员工
```bash
curl -X POST "http://localhost:8080/api/add?name=王小明&department=客服部&gender=男&work=客服专员"
```

### 10. 查询全部员工
```bash
curl "http://localhost:8080/api/select"
```

---

## 六、配置说明

### application.properties

```properties
spring.application.name=TongRong_Property_Company_2nd

spring.datasource.url=jdbc:mysql://localhost:3306/TongRong_Company?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8&allowPublicKeyRetrieval=true
spring.datasource.username=******
spring.datasource.password=******
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

### 可调整参数（OpenMeteoDemo.java）

| 参数 | 默认值 | 说明 |
|------|--------|------|
| CACHE_EXPIRE_MS | 10分钟 | 天气缓存有效期 |
| lat / lon | 40.19 / 118.12 | 默认监测坐标 |
| 连接超时 | 8秒 | HTTP请求超时限制 |
| 读取超时 | 8秒 | HTTP响应读取限制 |

### 可调整参数（PurchaseBase.java）

| 参数 | 默认值 | 说明 |
|------|--------|------|
| DEFAULT_VALUE_OF_STOCK | 12 | 库存预警阈值 |

---

## 七、数据库设计

### 数据库列表

| 数据库名 | 说明 |
|----------|------|
| TongRong_Company | 主数据库（员工管理、认证数据） |
| LogIn | 用户认证数据库 |
| PurchaseBase | 采购管理数据库 |

### 核心表结构

**PurchaseBase.products（商品表）**

| 字段 | 类型 | 说明 |
|------|------|------|
| id | INT | 商品ID(主键) |
| name | VARCHAR(100) | 商品名称 |
| category | VARCHAR(50) | 分类 |
| price | DECIMAL(10,2) | 单价 |
| stock | INT | 库存数量 |
| supplier | VARCHAR(100) | 供应商 |
| description | TEXT | 商品描述 |
| status | VARCHAR(20) | 状态(待审核/已通过/已驳回) |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

**PurchaseBase.comments（评论表）**

| 字段 | 类型 | 说明 |
|------|------|------|
| id | INT | 评论ID(主键) |
| product_name | VARCHAR(100) | 商品名称 |
| username | VARCHAR(50) | 评论用户 |
| content | TEXT | 评论内容 |
| create_time | DATETIME | 评论时间 |

---

## 八、前端管理后台

项目内置 **index.html** 单页管理后台，提供可视化操作界面。

### 功能面板

| 模块 | 功能 |
|------|------|
| 商品管理 | 全部商品、查询、模糊搜索、添加、批量添加、修改/删除 |
| 价格管理 | 单个价格、多个价格、总价计算 |
| 库存管理 | 库存检查、库存预警、商品发货 |
| 审批管理 | 商品审批（通过/驳回） |
| 评论管理 | 发表评论、查看评论 |

### 界面特性

- **响应式设计：** 适配PC和移动端
- **实时天气卡片：** 显示当前天气和温度
- **动态钟表：** 显示实时时间
- **库存状态徽标：** 库存充足(绿)/偏低(黄)/预警(红)
- **自动刷新：** 评论列表每30秒自动更新


---

## 九、开发计划

| 功能模块 | 状态 | 计划版本 |
|----------|------|----------|
| 员工信息管理 | ✅ 已完成 | v1.0 |
| 实时天气查询 | ✅ 已完成 | v1.1 |
| IP智能定位+缓存 | ✅ 已完成 | v1.2 |
| 用户认证与授权 | ✅ 已完成 | v1.2.2 |
| 采购管理系统 | ✅ 已完成 | v1.2.2 |
| 商品审批流程 | ✅ 已完成 | v1.2.2 |
| 评论系统 |  ✅已完成 | v1.2.3 |
| 管理后台界面 | ✅ 已完成 | v1.2.3 |
| 文件数据导出 | ✅ 已完成 | v1.2.3 |
| 评论回复 |  ✅已完成 | v1.2.4 |
| QQ邮箱昂验证码功能 | ✅ 已完成 | v1.2.4 |
| 访问控制与权限控制 | ✅ 已完成 | v1.2.4 |
|评论回复通知 |✅ 已完成 | v1.2.4 |
| 删评功能 |✅ 已完成 | v1.2.4 |
| 修改用户名 |✅ 已完成 | v1.2.4 |
| 分离controller与业务代码 |✅ 已完成 | v2.1.0 |
| 物业管理端界面 | 🚧 规划中 | x |
| 小程序/APP前端 | 🚧 规划中 | x |
| 高并发 | 🚧 规划中 | v3.1 |


---

## 十、注意事项

### 安全建议

1. **数据库密码：** 生产环境请使用环境变量（如 `${DB_PASSWORD}`）或配置中心存储密码，切勿硬编码。
2. **SQL注入防护：** 所有数据库操作均使用参数化查询（`?` 占位符），已有效防止SQL注入。
3. **会话安全：** 使用 HttpSession 管理登录状态，建议生产环境配置 Session 超时时间。

### 外部API依赖

- **IP定位：** 依赖 pconline 和 ip-api 服务（免费）
- **天气查询：** 依赖 Open-Meteo API（免费，无需API Key）

### 性能优化

- **并发安全：** 使用 ConcurrentHashMap 保证多线程读写安全
- **缓存预热：** 应用启动后自动预热天气数据到缓存
- **连接超时：** 外部API请求设置8秒超时，避免阻塞

### JDK兼容性

- 项目使用 JDK 17 编译，运行时需 JDK 17 或更高版本


---

## 十一、联系方式

| 项目 | 信息 |
|------|------|
| **作者** | Jia Yijia |
| **学校** | 中国地质大学·长城学院 |
| **主邮箱** | conandoyle1@qq.com |
| **备用邮箱** | 3662308525@qq.com |
| **项目名称** | 通融物管 APP（TongRong Property Management） |
| **历史版本** | v1.2.1 (2026年7月27日 - 2026年8月4日) |
| **历史版本** | v1.2.2 (2026年8月5日 - 2026年8月9日)  |
| **历史版本** | v1.2.3 (2026年8月9日 - 2026年8月15日) |
| **历史版本** | v1.2.4 (2026年8月15日 - 2026年8月18日)       |
| **当前版本** | v2.1.0 (2026年8月18日 - 未完成)       |
| **许可协议** | JYJ License |

---

> **💕 你想用就用吧，你用了我开心，别忘了给一个 star✨ 💕**
