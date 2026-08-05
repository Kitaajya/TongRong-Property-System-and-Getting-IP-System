# 通融物管 APP (TongRong Property Management)

## 项目概述

通融物管 APP 是一个基于 Spring Boot 构建的物业管理后端服务系统，提供**智能IP定位**、**员工信息管理**和**实时天气查询**三大核心功能。项目采用 RESTful API 设计，内置多级缓存机制以应对高并发场景，适用于中小型物业公司的日常运营管理。

**当前版本**：v1.2.1

---

## 技术栈

| 技术 | 版本 |
|------|------|
| Spring Boot | 4.1.0 |
| Java | 17 |
| MySQL | 8.4.8 |
| Spring JDBC | 7.0.8 |
| Maven | 3.9.16 |

---

## 核心功能

### 一、智能IP定位

根据访问者IP自动识别地理位置，为日志记录、区域化服务和天气查询提供位置上下文。

#### 定位策略（双接口自动故障切换）

| 优先级 | 服务商 | 接口地址 | 说明 |
|--------|--------|----------|------|
| 主接口 | pconline | whois.pconline.com.cn | 返回省+市，响应快 |
| 备用接口 | ip-api | ip-api.com/json | 主接口失败时自动切换 |

#### 内网智能识别

自动识别以下内网/本地地址，不发起外部查询，避免无效请求：
- `127.x`、`0.x`、`localhost`
- `10.x`、`172.x`、`192.168.x`
- `::1`（IPv6本地回环）

#### 查询结果示例

| 访问来源 | 返回结果 |
|----------|----------|
| 本机/内网穿透 | `本机/内网穿透` |
| 北京公网IP | `北京市` |
| 广东省深圳市 | `广东省深圳市` |
| 查询失败 | 返回IP原文 |

#### 核心代码实现

```java
public static String getLocation(String ip) {
    if (ip == null || ip.isBlank()) return "未知";
    if (isLocalIp(ip)) return "本机/内网";
    
    // 先查缓存，避免重复查询IP库
    String cached = IP_LOCATION_CACHE.get(ip);
    if (cached != null) return cached;
    
    // 主接口查询，失败自动切换备用接口
    String location = lookupPconline(ip);
    if (location == null) location = lookupIpApi(ip);
    
    if (location != null) {
        IP_LOCATION_CACHE.put(ip, location);
        return location;
    }
    return ip;
}
```

#### 缓存机制

- **缓存对象**：IP → 地理位置映射
- **有效期**：永久（进程生命周期内）
- **实现方式**：ConcurrentHashMap（线程安全）
- **效果**：同一IP只查询一次，后续请求毫秒级响应

---

### 二、员工信息管理

提供完整的员工档案增删改查功能，所有数据存储于 MySQL 数据库。

#### 接口清单

| 功能 | 请求方式 | 路径 | 说明 |
|------|----------|------|------|
| 新增员工 | POST | `/api/add` | 自动生成20位UUID作为主键 |
| 编辑员工 | POST | `/api/edit` | 根据ID更新员工信息 |
| 删除员工 | GET | `/api/delete` | 根据ID删除员工记录 |
| 查询全部 | GET | `/api/select` | 返回所有员工列表 |

#### 请求示例

**新增员工**
```
POST /api/add?name=张三&department=工程部&gender=男&work=维修
```

**编辑员工**
```
POST /api/edit?id=e770f25b40d04e8aa93a&name=李四&department=财务部&gender=女&work=会计
```

**删除员工**
```
GET /api/delete?id=e770f25b40d04e8aa93a
```

**查询全部**
```
GET /api/select
```

#### 数据表结构（log_in）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | varchar(20) | 主键，UUID生成 |
| name | varchar(5) | 员工姓名 |
| department | varchar(20) | 所属部门 |
| gender | varchar(1) | 性别 |
| salary | decimal(10,2) | 薪资（预留字段） |
| work | varchar(10) | 岗位/工种 |

---

### 三、实时天气查询

集成 **Open-Meteo** 免费天气 API，提供基于地理位置的实时天气信息。**IP定位功能为天气查询提供访问者地理位置上下文**，便于日志追踪和区域化服务。

#### 接口信息

| 功能 | 请求方式 | 路径 |
|------|----------|------|
| 获取天气 | GET | `/api/weather/getWeather` |

#### 返回内容

- 🌡️ 实时温度（℃）
- 💨 实时风速（km/h）
- ☁️ 天气状况（晴/多云/雨/雷暴等）
- 📍 当前地理位置（基于IP定位自动识别）

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

- 北纬：40.19°
- 东经：118.12°
- 时区：Asia/Shanghai

---

### 四、高性能缓存机制

为应对高并发访问，项目内置多级缓存，**无需额外引入Redis**：

| 缓存类型 | 缓存对象 | 有效期 | 实现方式 |
|----------|----------|--------|----------|
| IP地区缓存 | IP → 地理位置映射 | 永久 | ConcurrentHashMap |
| 天气数据缓存 | 格式化天气文本 | 10分钟 | ConcurrentHashMap |
| 应用启动预热 | 天气数据 | 启动时异步加载 | ApplicationReadyEvent |

> **核心优势**：IP定位和天气查询均优先命中缓存，不重复调用外部API，大幅降低响应延迟和外部接口调用费用。

---

## 项目结构

```
TongRong_Property_Company_2nd/
├── src/main/java/org/designer/tongrong_property_company_2nd/
│   ├── controller/
│   │   ├── AddWorker.java        # 新增员工（POST /api/add）
│   │   ├── EditWork.java         # 编辑员工（POST /api/edit）
│   │   ├── FireWorker.java       # 删除员工（GET /api/delete）
│   │   └── SelectWorker.java     # 查询全部（GET /api/select）
│   ├── weather/
│   │   └── OpenMeteoDemo.java    # 天气查询 + IP定位 + 多级缓存
│   └── TongRongPropertyCompany2ndApplication.java  # 启动类
├── src/main/resources/
│   └── application.properties    # 应用配置（数据库连接等）
├── pom.xml                       # Maven依赖管理
├── mvnw / mvnw.cmd              # Maven Wrapper
└── HELP.md                       # 项目参考文档
```

---

## 快速开始

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

# 3. 创建数据库（MySQL）
CREATE DATABASE TongRong_Company CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

# 4. 创建员工表
CREATE TABLE log_in (
    id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(5),
    department VARCHAR(20),
    gender VARCHAR(1),
    salary DECIMAL(10,2),
    work VARCHAR(10)
);

# 5. 修改 application.properties 中的数据库连接信息

# 6. 启动应用（使用Maven Wrapper）
./mvnw spring-boot:run    # Linux/Mac
mvnw.cmd spring-boot:run  # Windows
```

应用启动后默认监听 `http://localhost:8080`

---

## API 测试示例

### 1. 获取天气信息（含IP定位）

```bash
curl "http://localhost:8080/api/weather/getWeather"
```

**返回示例**：

```
(北纬40.19、东经118.12地区)
🌡️ 温度：26.5 ℃
💨 风速：12.3 km/h
☁️ 天气状况：晴
```

**服务端日志**：
```
访问者在广东省深圳市
```

---

### 2. 新增员工

```bash
curl -X POST "http://localhost:8080/api/add?name=王小明&department=客服部&gender=男&work=客服专员"
```

**返回**：`1`（表示成功插入1条记录）

---

### 3. 查询全部员工

```bash
curl "http://localhost:8080/api/select"
```

**返回示例**：
```json
[
  {
    "id": "a1b2c3d4e5f6g7h8i9j0",
    "name": "王小明",
    "department": "客服部",
    "gender": "男",
    "salary": null,
    "work": "客服专员"
  }
]
```

---

### 4. 删除员工

```bash
curl "http://localhost:8080/api/delete?id=a1b2c3d4e5f6g7h8i9j0"
```

**返回**：`1`（表示成功删除1条记录）

---

## 配置说明

### 数据库连接（application.properties）

```properties
spring.application.name=TongRong_Property_Company_2nd

spring.datasource.url=jdbc:mysql://localhost:3306/TongRong_Company?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8
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

---

## 版本历史

| 版本 | 日期 | 更新内容 |
|------|------|----------|
| v1.0.0 | - | 初始版本：员工管理基础CRUD |
| v1.1.0 | - | 新增天气查询模块，集成Open-Meteo API |
| v1.2.0 | - | 引入多级缓存（ConcurrentHashMap）+ IP定位功能 |
| **v1.2.1** | **2026-08** | **优化缓存策略、完善异常处理、增加启动预热** |

---

## 开发计划

| 功能模块 | 状态 | 计划版本 |
|----------|------|----------|
| 员工信息管理 | ✅ 已完成 | v1.0 |
| 实时天气查询 | ✅ 已完成 | v1.1 |
| IP智能定位+缓存 | ✅ 已完成 | v1.2 |
| 用户认证与权限 | 🚧 规划中 | v2.0 |
| 物业管理端界面 | 🚧 规划中 | v2.0 |
| 小程序/APP前端 | 🚧 规划中 | v3.0 |

---

## 注意事项

1. **数据库安全**：生产环境请使用环境变量（如 `${DB_PASSWORD}`）或配置中心存储密码，切勿硬编码。

2. **外部API依赖**：IP定位依赖 pconline 和 ip-api 服务，天气功能依赖 Open-Meteo API，需确保服务器具备外网访问能力。

3. **并发安全**：使用 `ConcurrentHashMap` 保证多线程读写安全，无需额外加锁。

4. **JDK兼容性**：项目使用 JDK 17 编译，运行时需 JDK 17 或更高版本。

5. **数据库字符集**：建议使用 `utf8mb4` 字符集以支持完整Unicode（如emoji）。

---

## 联系方式

- **作者**：贾奕嘉
- **QQ邮箱**：3662308525@qq.com或conandoyle1@qq.com
- **项目名称**：通融物管 APP（TongRong Property Management）
- **当前版本**：v1.2.1（2026年7月27日->2026年8月4日）
- **许可协议**：你想用就用吧，你用了我开心，别忘了给一个star💕
