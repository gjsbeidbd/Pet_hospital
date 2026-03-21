# 🏥 宠物医院管理系统 PetHospitalProject

<p align="center">
  <img src="https://img.shields.io/badge/Spring%20Boot-2.7.0-green" alt="Spring Boot">
  <img src="https://img.shields.io/badge/Vue-3-blue" alt="Vue">
  <img src="https://img.shields.io/badge/Element%20Plus-2.13.0-orange" alt="Element Plus">
  <img src="https://img.shields.io/badge/MySQL-8.0+-blue" alt="MySQL">
  <img src="https://img.shields.io/badge/Java-17+-red" alt="Java">
</p>

## 📋 项目简介

PetHospitalProject 是一个功能完善的宠物医院综合管理系统，采用前后端分离架构设计。系统支持四种角色（宠物主人、前台接待、医生、院长）的业务需求，提供从预约挂号、诊疗到收费结算的完整医疗服务流程。

### 核心功能

| 模块 | 功能描述 |
|------|----------|
| 🔐 身份认证 | 支持多角色登录（用户、前台、医生、院长），密码修改 |
| 🐾 宠物管理 | 宠物信息录入、品种分类、健康档案 |
| 📅 预约挂号 | 在线预约、取消预约、排班查询 |
| 🩺 诊疗管理 | 病历记录、诊断录入、处方开具 |
| 💊 药品库存 | 药品入库、出库、库存预警 |
| 💰 收费结算 | 费用明细、账单生成、支付管理 |
| 📊 数据统计 | 营业报表、医生排班、员工管理 |

## 🛠 技术栈

### 后端技术

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 2.7.0 | 轻量级Java Web框架 |
| MyBatis Plus | 3.5.3.1 | 持久层框架 |
| MySQL | 8.0+ | 关系型数据库 |
| Maven | 3.6+ | 项目构建工具 |

### 前端技术

| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.5.25 | 渐进式JavaScript框架 |
| Element Plus | 2.13.0 | UI组件库 |
| Vite | 7.2.4 | 构建工具 |
| Axios | 1.13.2 | HTTP客户端 |
| Vue Router | 4.6.3 | 路由管理 |
| ECharts | 6.0.0 | 数据可视化 |

## 📁 项目结构

```
PetHospitalProject/
│
├── src/main/java/edu/mycc/xhd/pethospitalproject/
│   ├── controller/          # 15个控制器
│   │   ├── AuthController          # 认证登录
│   │   ├── UserController          # 用户管理
│   │   ├── PetController           # 宠物管理
│   │   ├── AppointmentController   # 预约管理
│   │   ├── MedicalRecordController  # 病历管理
│   │   ├── DoctorScheduleManagementController # 医生排班
│   │   ├── ReceptionistScheduleController     # 前台排班
│   │   ├── DrugInventoryController  # 药品库存
│   │   ├── BillingController         # 收费结算
│   │   ├── DepartmentController      # 科室管理
│   │   ├── StaffController           # 员工管理
│   │   ├── AvatarController          # 头像上传
│   │   └── ...
│   │
│   ├── service/             # 14个服务层
│   │   ├── impl/                   # 业务实现
│   │   ├── AppointmentService
│   │   ├── BillingService
│   │   ├── MedicalRecordService
│   │   ├── DrugInventoryService
│   │   └── ...
│   │
│   ├── mapper/              # 15个数据访问层
│   │   ├── UserMapper
│   │   ├── AppointmentMapper
│   │   ├── BillingMapper
│   │   ├── BillingItemMapper
│   │   └── ...
│   │
│   ├── entity/              # 21个实体类
│   │   ├── User
│   │   ├── Pet
│   │   ├── Appointment
│   │   ├── MedicalRecord
│   │   ├── MedicalRecordDetail
│   │   ├── Billing
│   │   ├── BillingItem
│   │   ├── Doctor
│   │   ├── Receptionist
│   │   ├── Director
│   │   ├── Department
│   │   ├── DoctorSchedule
│   │   ├── DrugInventory
│   │   └── ...
│   │
│   └── config/               # 配置类
│       ├── DatabaseConfig
│       └── WebConfig
│
├── front/src/
│   ├── views/
│   │   ├── Login.vue              # 登录页面
│   │   ├── user/                  # 宠物主人
│   │   │   ├── UserDashboard.vue
│   │   │   └── components/
│   │   │       ├── Appointment.vue    # 预约挂号
│   │   │       ├── MyPets.vue          # 我的宠物
│   │   │       ├── MedicalRecords.vue # 病历查看
│   │   │       ├── PendingPayments.vue # 待缴费
│   │   │       └── Profile.vue        # 个人中心
│   │   │
│   │   ├── receptionist/          # 前台接待
│   │   │   ├── ReceptionistDashboard.vue
│   │   │   └── components/
│   │   │       ├── Registration.vue   # 客户登记
│   │   │       ├── Overview.vue       # 工作台
│   │   │       ├── Billing.vue        # 收费结算
│   │   │       └── Schedule.vue       # 排班查看
│   │   │
│   │   ├── doctor/                 # 医生
│   │   │   ├── DoctorDashboard.vue
│   │   │   └── components/
│   │   │       ├── Consultation.vue   # 接诊
│   │   │       ├── MedicalRecords.vue # 病历管理
│   │   │       └── Schedule.vue        # 排班查看
│   │   │
│   │   └── director/               # 院长
│   │       ├── DirectorDashboard.vue
│   │       └── components/
│   │           ├── BusinessReport.vue    # 营业报表
│   │           ├── DoctorScheduleBoard.vue # 医生排班
│   │           ├── NurseScheduleBoard.vue # 护士排班
│   │           ├── DrugInventory.vue     # 药品库存
│   │           └── StaffManagement.vue   # 员工管理
│   │
│   ├── services/
│   │   └── api.js                  # API统一管理
│   │
│   └── router/
│       └── index.js                # 路由配置
│
├── pet_hospital.sql         # 数据库脚本（20+表）
├── pom.xml                  # Maven配置
└── package.json             # 前端依赖
```

## 🗄 数据库表结构

| 表名 | 说明 |
|------|------|
| users | 宠物主人用户表 |
| pets | 宠物信息表 |
| pet_species | 宠物种类表 |
| pet_breeds | 宠物品种表 |
| doctors | 医生表 |
| receptionists | 前台/护士表 |
| directors | 院长表 |
| departments | 科室表 |
| appointments | 预约表 |
| medical_records | 病历表 |
| doctor_schedules | 医生排班表 |
| receptionist_schedules | 前台排班表 |
| drug_inventory | 药品库存表 |
| billing | 账单表 |
| billing_items | 账单明细表 |

## ⚙️ 环境要求

| 环境 | 版本要求 |
|------|----------|
| Java | 17+ |
| Node.js | ^20.19.0 或 >=22.12.0 |
| MySQL | 8.0+ |
| Maven | 3.6+ |

## 🚀 快速开始

### 1. 数据库配置

```sql
CREATE DATABASE pet_hospital CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

执行 `pet_hospital.sql` 初始化数据库表结构和数据。

### 2. 后端配置

修改 `src/main/resources/application.properties`：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/pet_hospital?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=你的密码
```

### 3. 前端配置

```bash
cd front
npm install
```

### 4. 启动项目

**方式一：使用启动脚本（推荐）**

- `start-dev.bat` - 开发环境启动
- `start-project.bat` - 项目启动
- `start-project.ps1` - PowerShell脚本

**方式二：手动启动**

```bash
mvn spring-boot:run    # 启动后端
cd front && npm run dev  # 启动前端
```

### 5. 访问应用

| 服务 | 地址 |
|------|------|
| 前端页面 | http://localhost:5180 |
| 后端API | http://localhost:8080 |

## 👤 默认账户

| 角色 | 账号 | 密码 |
|------|------|------|
| 宠物主人 | 13800138001 | 13800138001 |
| 前台接待 | QS001 | 123456 |
| 医生 | YS001 | YS001 |
| 院长 | YZ001 | YZ001 |

## 📱 功能模块详解

### 🐕 宠物主人端
- **我的宠物** - 管理宠物信息（名称、品种、年龄、体重等）
- **预约挂号** - 选择科室、医生、时间进行预约
- **病历查看** - 查看宠物历史就诊记录
- **待缴费** - 查看待支付账单
- **个人中心** - 修改个人信息和密码

### 🏥 前台接待端
- **客户登记** - 新客户信息录入、宠物登记
- **工作台** - 今日预约、待处理事项概览
- **收费结算** - 账单生成、费用收取
- **排班查看** - 查看医生排班情况

### 🩺 医生端
- **接诊** - 查看待诊患者、开始诊疗
- **病历管理** - 录入诊断、处方、检查结果
- **排班查看** - 查看个人排班

### 👔 院长端
- **营业报表** - 收入统计、数据分析（ECharts图表）
- **医生排班** - 安排医生值班
- **护士排班** - 安排前台/护士值班
- **药品库存** - 药品管理、库存预警
- **员工管理** - 医生、护士信息管理

## 📡 API接口文档

### 认证接口
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/auth/login | 用户登录 |
| POST | /api/auth/register | 用户注册 |

### 用户接口
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/users/{id} | 获取用户信息 |
| PUT | /api/users/{id} | 更新用户信息 |

### 宠物接口
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/pets | 获取宠物列表 |
| POST | /api/pets | 新增宠物 |
| GET | /api/pets/{id} | 获取宠物详情 |
| PUT | /api/pets/{id} | 更新宠物信息 |
| DELETE | /api/pets/{id} | 删除宠物 |

### 预约接口
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/appointments | 获取预约列表 |
| POST | /api/appointments | 创建预约 |
| PUT | /api/appointments/{id}/cancel | 取消预约 |

### 病历接口
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/medical-records | 获取病历列表 |
| POST | /api/medical-records | 创建病历 |
| GET | /api/medical-records/{id} | 获取病历详情 |
| PUT | /api/medical-records/{id} | 更新病历 |

### 账单接口
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/billing | 获取账单列表 |
| POST | /api/billing | 创建账单 |
| PUT | /api/billing/{id}/pay | 支付账单 |

## 📦 部署说明

### 后端部署
```bash
mvn clean package
java -jar target/PetHospitalProject-0.0.1-SNAPSHOT.jar
```

### 前端部署
```bash
cd front
npm run build
# 将 dist 目录部署到 Nginx 或其他 Web 服务器
```

## 📄 许可证

本项目仅供学习交流使用。

---

<p align="center">
  Made with ❤️ by PetHospital Team
</p>