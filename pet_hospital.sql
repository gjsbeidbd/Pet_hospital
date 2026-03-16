-- 删除现有数据库（如果存在）
DROP DATABASE IF EXISTS pet_hospital;

-- 宠物医院管理系统数据库脚本

-- 创建数据库
CREATE DATABASE IF NOT EXISTS pet_hospital CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE pet_hospital;

-- 宠物种类表
CREATE TABLE pet_species (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    species_name VARCHAR(50) NOT NULL COMMENT '种类名称（如：狗、猫、鸟等）',
    description TEXT COMMENT '种类描述',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY unique_species_name (species_name)
) COMMENT='宠物种类表';

-- 宠物品种表
CREATE TABLE pet_breeds (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    species_id BIGINT NOT NULL COMMENT '种类 ID',
    breed_name VARCHAR(100) NOT NULL COMMENT '品种名称',
    description TEXT COMMENT '品种描述',
    image VARCHAR(255) COMMENT '品种图片路径',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (species_id) REFERENCES pet_species(id) ON DELETE CASCADE,
    UNIQUE KEY unique_species_breed (species_id, breed_name)
) COMMENT='宠物品种表';

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

-- 前台表（护士表）
CREATE TABLE receptionists (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    employee_id VARCHAR(20) UNIQUE NOT NULL COMMENT '工号',
    name VARCHAR(100) NOT NULL COMMENT '姓名',
    birthday DATE COMMENT '出生日期',
    id_card VARCHAR(18) COMMENT '身份证号码',
    address VARCHAR(200) COMMENT '居住地址',
    phone VARCHAR(20) UNIQUE COMMENT '电话（唯一）',
    email VARCHAR(100) UNIQUE COMMENT '邮箱（唯一）',
    department VARCHAR(50) NOT NULL COMMENT '科室',
    position VARCHAR(100) COMMENT '职称（如：护士、护师、主管护师）',
    hire_date DATE COMMENT '入职日期',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    status ENUM('active', 'inactive') DEFAULT 'active' COMMENT '状态',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    -- 确保手机号和邮箱至少有一个不为空
    CONSTRAINT chk_receptionist_contact CHECK (phone IS NOT NULL OR email IS NOT NULL)
) COMMENT='前台表（护士表）';



-- 医生表
CREATE TABLE doctors (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    employee_id VARCHAR(20) UNIQUE NOT NULL COMMENT '工号',
    name VARCHAR(100) NOT NULL COMMENT '姓名',
    birthday DATE COMMENT '出生日期',
    id_card VARCHAR(18) COMMENT '身份证号码',
    address VARCHAR(200) COMMENT '居住地址',
    department VARCHAR(50) NOT NULL COMMENT '科室',
    position VARCHAR(100) COMMENT '职称（如：住院医师、主治医师等）',
    phone VARCHAR(20) UNIQUE COMMENT '电话（唯一）',
    email VARCHAR(100) UNIQUE COMMENT '邮箱（唯一）',
    hire_date DATE COMMENT '入职日期',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    status ENUM('active', 'inactive') DEFAULT 'active' COMMENT '状态',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    -- 确保手机号和邮箱至少有一个不为空
    CONSTRAINT chk_doctor_contact CHECK (phone IS NOT NULL OR email IS NOT NULL)
) COMMENT='医生表';

-- 科室表
CREATE TABLE departments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '科室 ID',
    code VARCHAR(20) UNIQUE NOT NULL COMMENT '科室代码（如：NK、WK）',
    name VARCHAR(50) NOT NULL COMMENT '科室名称',
    description TEXT COMMENT '科室描述',
    doctor_count INT DEFAULT 0 COMMENT '医生人数',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY unique_department_code (code),
    UNIQUE KEY unique_department_name (name)
) COMMENT='科室表';

-- 院长表
CREATE TABLE directors (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    employee_id VARCHAR(20) UNIQUE NOT NULL COMMENT '工号',
    name VARCHAR(100) NOT NULL COMMENT '姓名',
    birthday DATE COMMENT '出生日期',
    id_card VARCHAR(18) COMMENT '身份证号码',
    address VARCHAR(200) COMMENT '居住地址',
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
    doctor_id BIGINT NOT NULL COMMENT '医生 ID',
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

-- 前台排班表（护士排班表）
CREATE TABLE receptionist_schedules (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    receptionist_id BIGINT NOT NULL COMMENT '前台 ID（护士 ID）',
    department VARCHAR(50) NOT NULL COMMENT '科室',
    schedule_date DATE NOT NULL COMMENT '排班日期',
    start_time TIME NOT NULL COMMENT '开始时间',
    end_time TIME NOT NULL COMMENT '结束时间',
    shift_type VARCHAR(50) COMMENT '班次类型',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (receptionist_id) REFERENCES receptionists(id) ON DELETE CASCADE,
    UNIQUE KEY unique_receptionist_schedule (receptionist_id, schedule_date, start_time)
) COMMENT='前台排班表（护士排班表）';

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
    code VARCHAR(20) UNIQUE NOT NULL COMMENT '药品编号',
    name VARCHAR(200) NOT NULL COMMENT '药品名称',
    type VARCHAR(50) NOT NULL COMMENT '药品分类（如：抗生素、疫苗等）',
    price DECIMAL(10,2) NOT NULL COMMENT '售价',
    stock INT NOT NULL DEFAULT 0 COMMENT '当前库存数量',
    warning_stock INT DEFAULT 20 COMMENT '预警库存（低于此值显示缺货）',
    unit VARCHAR(20) NOT NULL COMMENT '单位（如：盒、支、粒等）',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否启用',
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

-- 费用管理表
CREATE TABLE fee_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    item_name VARCHAR(200) NOT NULL COMMENT '费用项目名称',
    category ENUM('诊查费', '治疗费', '手术费', '检查费', '护理费', '其他') NOT NULL COMMENT '费用类别',
    unit_price DECIMAL(10,2) NOT NULL COMMENT '单价',
    unit VARCHAR(50) COMMENT '单位（如：次、支等）',
    description TEXT COMMENT '费用说明',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT='费用管理表';

-- 初始化数据

-- 插入费用项目数据
INSERT INTO fee_items (item_name, category, unit_price, unit, description, is_active) VALUES
('普通门诊诊查费', '诊查费', 50.00, '次', '普通门诊挂号及诊查费用', TRUE),
('专家门诊诊查费', '诊查费', 100.00, '次', '专家门诊挂号及诊查费用', TRUE),
('急诊诊查费', '诊查费', 80.00, '次', '急诊挂号及诊查费用', TRUE),
('血常规检查', '检查费', 30.00, '次', '全血细胞计数检查', TRUE),
('尿常规检查', '检查费', 20.00, '次', '尿液常规检查', TRUE),
('X 光检查', '检查费', 150.00, '部位', 'X 光拍片检查', TRUE),
('B 超检查', '检查费', 200.00, '部位', '超声波检查', TRUE),
('显微镜检查', '检查费', 50.00, '次', '皮肤刮片等显微镜检查', TRUE),
('静脉注射', '治疗费', 30.00, '次', '静脉注射给药', TRUE),
('肌肉注射', '治疗费', 20.00, '次', '肌肉注射给药', TRUE),
('皮下注射', '治疗费', 15.00, '次', '皮下注射给药', TRUE),
('输液治疗', '治疗费', 50.00, '次', '静脉输液治疗（不含药费）', TRUE),
('伤口清创术', '治疗费', 100.00, '次', '伤口清理和消毒', TRUE),
('缝合术', '治疗费', 150.00, '次', '伤口缝合（小）', TRUE),
('绝育手术', '手术费', 500.00, '台', '公犬/猫去势术', TRUE),
('卵巢子宫摘除术', '手术费', 800.00, '台', '母犬/猫绝育手术', TRUE),
('骨折内固定术', '手术费', 2000.00, '台', '骨折手术治疗', TRUE),
('肿瘤切除术', '手术费', 1500.00, '台', '软组织肿瘤切除', TRUE),
('住院护理费', '护理费', 100.00, '天', '普通住院护理', TRUE),
('重症监护费', '护理费', 300.00, '天', 'ICU 重症监护', TRUE),
('疫苗注射', '其他', 80.00, '针', '传染病疫苗接种', TRUE),
('狂犬疫苗', '其他', 100.00, '针', '狂犬病疫苗接种', TRUE);

-- 插入宠物种类数据
INSERT INTO pet_species (species_name, description, is_active) VALUES
('狗', '人类最忠实的朋友，适合家庭陪伴', TRUE),
('猫', '独立优雅的动物，适合室内饲养', TRUE),
('鸟', '色彩斑斓，鸣声悦耳', TRUE),
('兔子', '温顺可爱，素食动物', TRUE),
('仓鼠', '小型萌宠，易于饲养', TRUE);

-- 插入宠物品种数据
INSERT INTO pet_breeds (species_id, breed_name, description, image, is_active) VALUES
(1, '金毛寻回犬', '友善、聪明、忠诚的大型犬', NULL, TRUE),
(1, '拉布拉多', '活泼、友好、高能量的大型犬', NULL, TRUE),
(1, '哈士奇', '精力充沛、友好的雪橇犬', NULL, TRUE),
(1, '柯基', '活泼、聪明的小型牧牛犬', NULL, TRUE),
(1, '泰迪', '优雅、活跃的贵宾犬', NULL, TRUE),
(2, '英国短毛猫', '圆脸胖腮，性格温和', NULL, TRUE),
(2, '美国短毛猫', '体格强壮，性格独立', NULL, TRUE),
(2, '布偶猫', '温顺粘人，毛发华丽', NULL, TRUE),
(2, '暹罗猫', '活泼好动，叫声独特', NULL, TRUE),
(2, '波斯猫', '长毛高贵，性格安静', NULL, TRUE),
(3, '鹦鹉', '聪明伶俐，善于模仿', NULL, TRUE),
(3, '文鸟', '温和安静，羽毛美丽', NULL, TRUE),
(4, '荷兰垂耳兔', '耳朵下垂，性格温顺', NULL, TRUE),
(4, '侏儒兔', '体型小巧，活泼可爱', NULL, TRUE),
(5, '金丝熊', '金黄色毛发，体型较大', NULL, TRUE),
(5, '三线仓鼠', '背部有三条线，体型较小', NULL, TRUE);

-- 插入默认用户
INSERT INTO users (username, password, name, phone, email, address, role) VALUES
('13800138001', '123456', '张三', '13800138001', 'user1@pethospital.com', '北京市朝阳区', 'USER'),
('13800138002', '123456', '李四', '13800138002', 'user2@pethospital.com', '上海市浦东新区', 'USER');

-- 插入用户宠物信息
INSERT INTO pets (user_id, name, species, breed, age, gender, weight, medical_history) VALUES
(1, '旺财', '狗', '金毛', 3, 'male', 30.5, '无过敏史'),
(1, '咪咪', '猫', '英短', 2, 'female', 4.2, '定期驱虫'),
(2, '小黑', '狗', '拉布拉多', 5, 'male', 35.0, '有关节炎');

-- 插入前台信息（护士）
INSERT INTO receptionists (employee_id, name, birthday, id_card, address, department, position, phone, email, hire_date, password) VALUES
('N00001', '王丽丽', '1995-03-15', '110101199503150011', '北京市朝阳区建国路 88 号', '内科', '护士', '13900139001', 'receptionist1@pethospital.com', '2023-01-15', '123456'),
('N00002', '李小红', '1997-08-22', '110101199708220022', '北京市海淀区中关村大街 100 号', '外科', '护士', '13900139002', 'receptionist2@pethospital.com', '2022-03-20', '123456');

-- 插入医生信息
INSERT INTO doctors (employee_id, name, birthday, id_card, address, department, position, phone, email, hire_date, password) VALUES
('D00001', '王建国', '1985-06-15', '110101198506150011', '北京市朝阳区建国路 100 号', '内科', '主治医师', '13900139003', 'doctor1@pethospital.com', '2023-01-15', '123456'),
('D00002', '李晓明', '1988-11-08', '110101198811080022', '北京市东城区王府井大街 88 号', '外科', '副主任医师', '13900139004', 'doctor2@pethospital.com', '2022-03-20', '123456'),
('D00003', '张伟', '1990-05-20', '110101199005200033', '北京市西城区西直门内大街 120 号', '内科', '住院医师', '13900139012', 'neike2@pethospital.com', '2023-08-22', '123456'),
('D00004', '刘芳', '1992-09-10', '110101199209100044', '北京市朝阳区望京街道 66 号', '内科', '住院医师', '13900139013', 'neike3@pethospital.com', '2023-09-15', '123456'),
('D00005', '陈志强', '1987-03-25', '110101198703250055', '北京市丰台区丰台路 300 号', '外科', '主治医师', '13900139014', 'waike2@pethospital.com', '2023-04-10', '123456'),
('D00006', '赵磊', '1993-12-18', '110101199312180066', '北京市海淀区海淀路 50 号', '外科', '住院医师', '13900139015', 'waike3@pethospital.com', '2023-10-20', '123456');

-- 插入院长信息
INSERT INTO directors (employee_id, name, birthday, id_card, address, phone, email, hire_date, password) VALUES
('YZ001', '赵院长', '1975-04-10', '110101197504100077', '北京市朝阳区朝阳北路 200 号', '13900139006', 'director1@pethospital.com', '2020-01-01', '123456');

-- 插入医生排班信息
INSERT INTO doctor_schedules (doctor_id, department, schedule_date, start_time, end_time, shift_type) VALUES
(1, '内科', '2025-12-01', '09:00:00', '17:00:00', '白班'),
(2, '外科', '2025-12-01', '10:00:00', '18:00:00', '白班'),
(3, '内科', '2025-12-02', '08:00:00', '16:00:00', '白班');

-- 插入前台排班信息（护士排班）
INSERT INTO receptionist_schedules (receptionist_id, department, schedule_date, start_time, end_time, shift_type) VALUES
(1, '内科', '2025-12-01', '08:00:00', '16:00:00', '白班'),
(2, '外科', '2025-12-01', '16:00:00', '00:00:00', '中班');

-- 插入科室信息
INSERT INTO departments (code, name, description, doctor_count, is_active) VALUES
('NK', '内科', '负责内科疾病诊断治疗', 3, TRUE),
('WK', '外科', '负责外科手术和治疗', 3, TRUE),
('PFK', '皮肤科', '负责皮肤病诊断治疗', 0, TRUE),
('YK', '眼科', '负责眼科疾病诊断治疗', 0, TRUE),
('YK2', '牙科', '负责口腔疾病诊断治疗', 0, TRUE),
('ZK', '重症监护室', '负责危重病例救治', 0, TRUE),
('JZK', '急诊科', '负责 24 小时急诊服务', 0, TRUE),
('YXK', '医学影像科', '负责 X 光、B 超等影像学检查', 0, TRUE),
('JYK', '检验科', '负责血液、尿液等化验检查', 0, TRUE),
('YF', '药房', '负责药品管理和发放', 0, TRUE);

-- 插入预约信息
INSERT INTO appointments (user_id, pet_id, doctor_id, appointment_date, appointment_time, status, reason) VALUES
(1, 1, 1, '2025-12-01', '09:30:00', 'confirmed', '常规体检'),
(2, 3, 2, '2025-12-01', '10:30:00', 'pending', '关节炎复查');

-- 插入病历信息
INSERT INTO medical_records (pet_id, doctor_id, appointment_id, visit_date, diagnosis, treatment, prescription) VALUES
(1, 1, 1, '2025-12-01', '健康状况良好', '建议定期体检', '维生素片 1盒');

-- 插入药品库存信息
INSERT INTO drug_inventory (code, name, type, price, stock, warning_stock, unit, is_active) VALUES
('D001', '阿莫西林', '抗生素', 25.00, 120, 20, '盒', TRUE),
('D002', '狂犬疫苗', '疫苗', 80.00, 8, 10, '支', TRUE),
('D003', '伊丽莎白圈', '耗材', 15.00, 50, 15, '个', TRUE),
('D004', '体内驱虫片', '驱虫药', 45.00, 15, 20, '粒', FALSE);

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

-- 医生表查询
SELECT 
    d.id AS 'ID',
    d.employee_id AS '工号',
    d.name AS '姓名',
    d.department AS '科室',
    d.position AS '职位',
    d.phone AS '电话',
    d.email AS '邮箱'
FROM doctors d
ORDER BY d.department, d.name;