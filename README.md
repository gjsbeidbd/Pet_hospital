# PetHospitalProject 宠物医院管理系统

## 项目简介

PetHospitalProject 是一个基于Spring Boot和Vue.js的宠物医院管理系统，提供完整的宠物医疗服务解决方案，包括用户预约、医生诊断、病历管理、药品库存管理等功能。

## 技术栈

### 后端
- Spring Boot 2.7.0
- MyBatis Plus
- MySQL 8.0+
- Maven 3.6+

### 前端
- Vue 3
- Element Plus
- Axios
- Vite

## 环境要求

- Java 17+
- Node.js (版本^20.19.0 || >=22.12.0)
- MySQL 8.0+
- Maven 3.6+

## 快速开始

### 1. 数据库配置

1. 创建MySQL数据库：
```sql
CREATE DATABASE pet_hospital CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 执行SQL脚本初始化数据库表结构和数据：
```bash
# 执行 sql/pet_hospital.sql 文件
```

### 2. 后端配置

1. 修改数据库连接配置文件 `src/main/resources/application.properties`：
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/pet_hospital?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=123456
```

### 3. 前端配置

1. 进入前端目录并安装依赖：
```bash
cd front
npm install
```

### 4. 启动项目

#### 方法一：使用启动脚本（推荐）

直接双击运行以下脚本之一：

- `start-dev.bat` - 简单的开发环境启动脚本
- `start-project.bat` - 带有基本提示的启动脚本
- `start-project.ps1` - 功能更丰富的PowerShell脚本（需要PowerShell执行权限）

#### 方法二：手动启动

1. 启动后端服务：
```bash
mvn spring-boot:run
```

2. 启动前端服务：
```bash
cd front
npm run dev
```

### 5. 访问应用

- 前端地址：http://localhost:5180
- 后端API地址：http://localhost:8080

## 默认账户

### 用户账户
- 用户名/手机号：13800138001
- 密码：13800138001

### 前台账户
- 工号：QS001
- 密码：123456

### 医生账户
- 工号：YS001
- 密码：YS001

### 院长账户
- 工号：YZ001
- 密码：YZ001

## 功能模块

### 用户模块
- 宠物信息管理
- 预约挂号
- 病历查看
- 个人信息维护

### 前台模块
- 客户接待登记
- 预约管理
- 收费结算
- 客户信息维护

### 医生模块
- 病历管理
- 诊断记录录入
- 处方开具
- 病患历史记录查看

### 院长模块
- 员工管理
- 系统配置
- 数据统计分析
- 财务报表查看

## API文档

### 认证接口
- POST /api/auth/login - 用户登录
- POST /api/auth/register - 用户注册

### 用户相关接口
- GET /api/users/{id} - 获取用户信息
- PUT /api/users/{id} - 更新用户信息

### 宠物相关接口
- GET /api/pets - 获取用户宠物列表
- POST /api/pets - 新增宠物
- GET /api/pets/{id} - 获取宠物信息
- PUT /api/pets/{id} - 更新宠物信息
- DELETE /api/pets/{id} - 删除宠物

### 病历相关接口
- GET /api/medical-records - 获取宠物病历列表
- GET /api/medical-records/doctor - 获取医生病历列表
- POST /api/medical-records - 创建病历
- GET /api/medical-records/{id} - 获取病历详情
- PUT /api/medical-records/{id} - 更新病历
- DELETE /api/medical-records/{id} - 删除病历

## 项目结构

```
PetHospitalProject/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── edu/
│   │   │       └── mycc/
│   │   │           └── xhd/
│   │   │               └── pethospitalproject/
│   │   │                   ├── PetHospitalProjectApplication.java
│   │   │                   ├── controller/     # 控制器层
│   │   │                   ├── service/        # 业务逻辑层
│   │   │                   ├── mapper/         # 数据访问层
│   │   │                   └── entity/        # 实体类
│   │   └── resources/
│   │       └── application.properties
├── front/                  # 前端项目
│   ├── src/
│   │   ├── assets/         # 静态资源
│   │   ├── router/         # 路由配置
│   │   ├── services/       # API服务
│   │   ├── views/          # 页面视图
│   │   │   ├── user/       # 用户角色页面
│   │   │   ├── receptionist/ # 前台角色页面
│   │   │   ├── doctor/     # 医生角色页面
│   │   │   ├── director/   # 院长角色页面
│   │   │   └── Login.vue   # 登录页面
│   │   ├── App.vue
│   │   └── main.js
│   ├── package.json
│   └── vite.config.js
├── sql/
│   └── pet_hospital.sql    # 数据库脚本
├── start-dev.bat           # 开发环境启动脚本
├── start-project.bat       # 项目启动脚本
├── start-project.ps1       # PowerShell启动脚本
└── README.md
```

## 开发规范

### 命名规范
- 数据库表名：小写字母加下划线，如user_info
- Java类名：大驼峰命名法，如UserInfoService
- JavaScript变量：小驼峰命名法，如userInfo
- Vue组件：大驼峰命名法，如UserInfo.vue

### 代码规范
- 后端遵循Google Java Style Guide
- 前端遵循ESLint推荐规则
- 提交代码前需通过代码检查和测试

## 部署说明

### 后端部署
1. 修改application.properties配置文件
2. 执行mvn clean package打包
3. 运行jar包：java -jar PetHospitalProject.jar

### 前端部署
1. 修改API基础路径配置
2. 执行npm run build构建生产版本
3. 将dist目录部署到Web服务器