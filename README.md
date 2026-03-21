# 宠物医院管理系统

一套功能完善的宠物医院综合管理系统，采用前后端分离架构。

## 技术栈

| 分类 | 技术 | 版本 |
|------|------|------|
| 后端 | Spring Boot | 2.7.0 |
| 后端 | MyBatis Plus | 3.5.3.1 |
| 数据库 | MySQL | 8.0+ |
| 前端 | Vue | 3 |
| 前端 | Element Plus | 2.13.0 |
| 前端 | Vite | 7.2.4 |

## 项目结构

```
PetHospitalProject/
├── src/main/java/edu/mycc/xhd/pethospitalproject/
│   ├── controller/     # 控制器层
│   ├── service/       # 业务逻辑层
│   ├── mapper/        # 数据访问层
│   ├── entity/        # 实体类
│   └── config/        # 配置类
├── front/             # 前端项目
│   └── src/
│       ├── views/     # 页面组件
│       ├── services/  # API接口
│       └── router/    # 路由配置
└── pet_hospital.sql   # 数据库脚本
```

## 角色说明

系统支持四种角色：

- **宠物主人** - 预约挂号、宠物管理、病历查看、缴费
- **前台接待** - 客户登记、收费结算、排班查看
- **医生** - 接诊、病历管理、排班查看
- **院长** - 营业报表、排班管理、员工管理、药品库存

## 快速开始

### 1. 数据库配置

```sql
CREATE DATABASE pet_hospital CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

执行 `pet_hospital.sql` 初始化数据库。

### 2. 修改配置

修改 `src/main/resources/application.properties` 中的数据库连接信息：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/pet_hospital
spring.datasource.username=root
spring.datasource.password=你的密码
```

### 3. 启动项目

```bash
# 后端
mvn spring-boot:run

# 前端
cd front
npm install
npm run dev
```

### 4. 访问地址

| 服务 | 地址 |
|------|------|
| 前端 | http://localhost:5180 |
| 后端 | http://localhost:8080 |

## 默认账户

| 角色 | 账号 | 密码 |
|------|------|------|
| 宠物主人 | 13800138001 | 13800138001 |
| 前台接待 | QS001 | 123456 |
| 医生 | YS001 | YS001 |
| 院长 | YZ001 | YZ001 |

## 主要功能

### 用户端
- 预约挂号
- 宠物管理
- 病历查看
- 待缴费账单
- 个人中心

### 医生端
- 接诊工作台
- 病历管理
- 排班查看

### 前台端
- 客户登记
- 收费结算
- 排班查看

### 院长端
- 营业报表
- 医生/护士排班
- 药品库存管理
- 员工管理

## 技术特点

- 前后端分离架构
- RESTful API 设计
- MyBatis Plus 简化数据库操作
- 统一响应格式
- 跨域支持
