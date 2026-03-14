-- 删除现有数据库（如果存在）
DROP DATABASE IF EXISTS pet_hospital;

-- 宠物医院管理系统数据库脚本

-- 创建数据库
CREATE DATABASE IF NOT EXISTS pet_hospital CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE pet_hospital;

-- 用户表（宠物主人）
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL COMMENT '用户名（手机号或邮箱）',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    name VARCHAR(100) NOT NULL COMMENT '姓名',
    phone VARCHAR(20) UNIQUE COMMENT '电话（唯一）',
    email VARCHAR(100) UNIQUE COMMENT '邮箱（唯一）',
    address TEXT COMMENT '地址',
    image VARCHAR(255) COMMENT '头像图片路径',
    role VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '角色',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    -- 确保手机号和邮箱至少有一个不为空
    CONSTRAINT chk_contact CHECK (phone IS NOT NULL OR email IS NOT NULL)
) COMMENT='用户表（宠物主人）';

-- 宠物表
CREATE TABLE pets (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    name VARCHAR(100) NOT NULL COMMENT '宠物名',
    species VARCHAR(50) NOT NULL COMMENT '种类',
    breed VARCHAR(100) COMMENT '品种',
    age INT COMMENT '年龄',
    gender ENUM('male', 'female') NOT NULL COMMENT '性别',
    weight DECIMAL(5,2) COMMENT '重量(kg)',
    medical_history TEXT COMMENT '病史',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) COMMENT='宠物表';

-- 前台表
CREATE TABLE receptionists (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    employee_id VARCHAR(20) UNIQUE NOT NULL COMMENT '工号',
    name VARCHAR(100) NOT NULL COMMENT '姓名',
    phone VARCHAR(20) UNIQUE COMMENT '电话（唯一）',
    email VARCHAR(100) UNIQUE COMMENT '邮箱（唯一）',
    hire_date DATE COMMENT '入职日期',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    status ENUM('active', 'inactive') DEFAULT 'active' COMMENT '状态',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    -- 确保手机号和邮箱至少有一个不为空
    CONSTRAINT chk_receptionist_contact CHECK (phone IS NOT NULL OR email IS NOT NULL)
) COMMENT='前台表';

-- 医生表
CREATE TABLE doctors (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    employee_id VARCHAR(20) UNIQUE NOT NULL COMMENT '工号',
    name VARCHAR(100) NOT NULL COMMENT '姓名',
    department ENUM('内科', '外科', '护理部') NOT NULL COMMENT '科室',
    position VARCHAR(100) COMMENT '职位',
    phone VARCHAR(20) UNIQUE COMMENT '电话（唯一）',
    email VARCHAR(100) UNIQUE COMMENT '邮箱（唯一）',
    hire_date DATE COMMENT '入职日期',
    qualification TEXT COMMENT '资质',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    status ENUM('active', 'inactive') DEFAULT 'active' COMMENT '状态',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    -- 确保手机号和邮箱至少有一个不为空
    CONSTRAINT chk_doctor_contact CHECK (phone IS NOT NULL OR email IS NOT NULL)
) COMMENT='医生表';

-- 院长表
CREATE TABLE directors (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    employee_id VARCHAR(20) UNIQUE NOT NULL COMMENT '工号',
    name VARCHAR(100) NOT NULL COMMENT '姓名',
    phone VARCHAR(20) UNIQUE COMMENT '电话（唯一）',
    email VARCHAR(100) UNIQUE COMMENT '邮箱（唯一）',
    hire_date DATE COMMENT '入职日期',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    status ENUM('active', 'inactive') DEFAULT 'active' COMMENT '状态',
    image VARCHAR(500) COMMENT '头像 URL',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    -- 确保手机号和邮箱至少有一个不为空
    CONSTRAINT chk_director_contact CHECK (phone IS NOT NULL OR email IS NOT NULL)
) COMMENT='院长表';

-- 医生排班表
CREATE TABLE doctor_schedules (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    doctor_id BIGINT NOT NULL COMMENT '医生ID',
    department VARCHAR(50) NOT NULL COMMENT '科室',
    schedule_date DATE NOT NULL COMMENT '排班日期',
    start_time TIME NOT NULL COMMENT '开始时间',
    end_time TIME NOT NULL COMMENT '结束时间',
    shift_type VARCHAR(50) COMMENT '班次类型',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE,
    UNIQUE KEY unique_doctor_schedule (doctor_id, schedule_date, start_time)
) COMMENT='医生排班表';

-- 前台排班表
CREATE TABLE receptionist_schedules (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    receptionist_id BIGINT NOT NULL COMMENT '前台ID',
    schedule_date DATE NOT NULL COMMENT '排班日期',
    start_time TIME NOT NULL COMMENT '开始时间',
    end_time TIME NOT NULL COMMENT '结束时间',
    shift_type VARCHAR(50) COMMENT '班次类型',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (receptionist_id) REFERENCES receptionists(id) ON DELETE CASCADE,
    UNIQUE KEY unique_receptionist_schedule (receptionist_id, schedule_date, start_time)
) COMMENT='前台排班表';

-- 预约表
CREATE TABLE appointments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    pet_id BIGINT NOT NULL COMMENT '宠物ID',
    doctor_id BIGINT COMMENT '医生ID',
    appointment_date DATE NOT NULL COMMENT '预约日期',
    appointment_time TIME NOT NULL COMMENT '预约时间',
    status ENUM('pending', 'confirmed', 'completed', 'cancelled') DEFAULT 'pending' COMMENT '状态',
    reason TEXT COMMENT '预约原因',
    notes TEXT COMMENT '备注',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE SET NULL
) COMMENT='预约表';

-- 病历表
CREATE TABLE medical_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    pet_id BIGINT NOT NULL COMMENT '宠物ID',
    doctor_id BIGINT NOT NULL COMMENT '医生ID',
    appointment_id BIGINT COMMENT '预约ID',
    visit_date DATE NOT NULL COMMENT '就诊日期',
    diagnosis TEXT COMMENT '诊断',
    treatment TEXT COMMENT '治疗方案',
    prescription TEXT COMMENT '处方',
    notes TEXT COMMENT '备注',
    follow_up_required BOOLEAN DEFAULT FALSE COMMENT '是否需要复诊',
    follow_up_date DATE COMMENT '复诊日期',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE,
    FOREIGN KEY (appointment_id) REFERENCES appointments(id) ON DELETE SET NULL
) COMMENT='病历表';

-- 药品库存表
CREATE TABLE drug_inventory (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    drug_name VARCHAR(200) NOT NULL COMMENT '药品名称',
    description TEXT COMMENT '描述',
    quantity INT NOT NULL DEFAULT 0 COMMENT '数量',
    unit_price DECIMAL(10,2) NOT NULL COMMENT '单价',
    supplier VARCHAR(200) COMMENT '供应商',
    expiration_date DATE COMMENT '过期日期',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT='药品库存表';

-- 账单表
CREATE TABLE billing (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    appointment_id BIGINT COMMENT '预约ID',
    amount DECIMAL(10,2) NOT NULL COMMENT '金额',
    status ENUM('pending', 'paid', 'cancelled') DEFAULT 'pending' COMMENT '状态',
    payment_method VARCHAR(50) COMMENT '支付方式',
    payment_date TIMESTAMP NULL COMMENT '支付日期',
    description TEXT COMMENT '描述',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (appointment_id) REFERENCES appointments(id) ON DELETE SET NULL
) COMMENT='账单表';

-- 医院设置表
CREATE TABLE hospital_settings (
    id INT PRIMARY KEY AUTO_INCREMENT,
    setting_key VARCHAR(100) UNIQUE NOT NULL COMMENT '设置键',
    setting_value TEXT COMMENT '设置值',
    description VARCHAR(255) COMMENT '描述',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT='医院设置表';

-- 公告表
CREATE TABLE announcements (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL COMMENT '标题',
    content TEXT NOT NULL COMMENT '内容',
    author VARCHAR(100) COMMENT '作者',
    publish_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '发布日期',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否激活',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT='公告表';

-- 初始化数据

-- 插入默认用户
INSERT INTO users (username, password, name, phone, email, address, role) VALUES
('13800138001', '123456', '张三', '13800138001', 'user1@pethospital.com', '北京市朝阳区', 'USER'),
('13800138002', '123456', '李四', '13800138002', 'user2@pethospital.com', '上海市浦东新区', 'USER');

-- 插入用户宠物信息
INSERT INTO pets (user_id, name, species, breed, age, gender, weight, medical_history) VALUES
(1, '旺财', '狗', '金毛', 3, 'male', 30.5, '无过敏史'),
(1, '咪咪', '猫', '英短', 2, 'female', 4.2, '定期驱虫'),
(2, '小黑', '狗', '拉布拉多', 5, 'male', 35.0, '有关节炎');

-- 插入前台信息
INSERT INTO receptionists (employee_id, name, phone, email, hire_date, password) VALUES
('QS001', '王前台', '13900139001', 'receptionist1@pethospital.com', '2023-01-15', '123456'),
('QS002', '李前台', '13900139002', 'receptionist2@pethospital.com', '2022-03-20', '123456');

-- 插入医生信息
INSERT INTO doctors (employee_id, name, department, position, phone, email, hire_date, qualification, password) VALUES
('YS001', '王建国', '内科', '主治医师', '13900139003', 'doctor1@pethospital.com', '2023-01-15', '执业医师资格证', '123456'),
('YS002', '李晓明', '外科', '副主任医师', '13900139004', 'doctor2@pethospital.com', '2022-03-20', '副主任医师资格证', '123456'),
('YS004', '张伟', '内科', '住院医师', '13900139012', 'neike2@pethospital.com', '2023-08-22', '执业医师资格证', '123456'),
('YS005', '刘芳', '内科', '住院医师', '13900139013', 'neike3@pethospital.com', '2023-09-15', '执业医师资格证', '123456'),
('YS006', '陈志强', '外科', '主治医师', '13900139014', 'waike2@pethospital.com', '2023-04-10', '执业医师资格证', '123456'),
('YS007', '赵磊', '外科', '住院医师', '13900139015', 'waike3@pethospital.com', '2023-10-20', '执业医师资格证', '123456');

-- 插入院长信息
INSERT INTO directors (employee_id, name, phone, email, hire_date, password) VALUES
('YZ001', '赵院长', '13900139006', 'director1@pethospital.com', '2020-01-01', '123456');

-- 插入医生排班信息
INSERT INTO doctor_schedules (doctor_id, department, schedule_date, start_time, end_time, shift_type) VALUES
(1, '内科', '2025-12-01', '09:00:00', '17:00:00', '门诊'),
(2, '外科', '2025-12-01', '10:00:00', '18:00:00', '手术'),
(3, '内科', '2025-12-02', '08:00:00', '16:00:00', '门诊');

-- 插入前台排班信息
INSERT INTO receptionist_schedules (receptionist_id, schedule_date, start_time, end_time, shift_type) VALUES
(1, '2025-12-01', '08:00:00', '16:00:00', '早班'),
(2, '2025-12-01', '16:00:00', '00:00:00', '晚班');

-- 插入预约信息
INSERT INTO appointments (user_id, pet_id, doctor_id, appointment_date, appointment_time, status, reason) VALUES
(1, 1, 1, '2025-12-01', '09:30:00', 'confirmed', '常规体检'),
(2, 3, 2, '2025-12-01', '10:30:00', 'pending', '关节炎复查');

-- 插入病历信息
INSERT INTO medical_records (pet_id, doctor_id, appointment_id, visit_date, diagnosis, treatment, prescription) VALUES
(1, 1, 1, '2025-12-01', '健康状况良好', '建议定期体检', '维生素片 1盒');

-- 插入药品库存信息
INSERT INTO drug_inventory (drug_name, description, quantity, unit_price, supplier, expiration_date) VALUES
('阿莫西林胶囊', '抗生素类药物', 100, 25.00, '北京制药有限公司', '2026-12-31'),
('维生素C片', '营养补充剂', 200, 15.00, '上海药业集团', '2027-06-30'),
('止痛药', '缓解疼痛', 50, 30.00, '广州医药公司', '2026-08-15');

-- 插入账单信息
INSERT INTO billing (user_id, appointment_id, amount, status, payment_method, description) VALUES
(1, 1, 150.00, 'paid', '微信支付', '常规体检费用');

-- 插入公告信息
INSERT INTO announcements (title, content, author, is_active) VALUES
('医院春节放假通知', '本院将于春节期间（2026年2月10日-2月17日）放假，2月18日正式上班。急诊24小时开放。', '院长办公室', TRUE),
('新增在线预约功能', '即日起，用户可通过官网进行在线预约挂号，方便快捷。', '信息技术部', TRUE);

-- 插入医院设置
INSERT INTO hospital_settings (setting_key, setting_value, description) VALUES
('hospital_name', '爱心宠物医院', '医院名称'),
('hospital_address', '北京市朝阳区幸福大街123号', '医院地址'),
('hospital_phone', '010-12345678', '医院电话'),
('work_hours', '每天 8:00-18:00', '工作时间');

-- 医生排班表查询
SELECT 
    ds.schedule_date AS '日期',
    d.department AS '科室',
    d.name AS '医生姓名',
    d.employee_id AS '工号',
    ds.shift_type AS '班次类型',
    ds.start_time AS '开始时间',
    ds.end_time AS '结束时间'
FROM doctor_schedules ds
JOIN doctors d ON ds.doctor_id = d.id
ORDER BY d.department, ds.schedule_date, ds.start_time;

-- 前台排班表查询
SELECT 
    rs.schedule_date AS '日期',
    r.name AS '前台姓名',
    r.employee_id AS '工号',
    rs.shift_type AS '班次类型',
    rs.start_time AS '开始时间',
    rs.end_time AS '结束时间'
FROM receptionist_schedules rs
JOIN receptionists r ON rs.receptionist_id = r.id
ORDER BY rs.schedule_date, rs.start_time;