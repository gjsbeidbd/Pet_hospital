import axios from 'axios';

// 创建axios实例
const api = axios.create({
  baseURL: 'http://localhost:8080', // 后端服务地址
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
});

// 请求拦截器
api.interceptors.request.use(
  config => {
    // 从localStorage获取token
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  error => {
    return Promise.reject(error);
  }
);

// 响应拦截器
api.interceptors.response.use(
  response => {
    return response;
  },
  error => {
    if (error.response?.status === 401) {
      const errorData = error.response?.data;
      if (errorData?.error) {
        return Promise.reject(error);
      }
      localStorage.removeItem('token');
      localStorage.removeItem('userRole');
      localStorage.removeItem('userId');
      if (window.location.pathname !== '/login') {
        window.location.href = '/login';
      }
    }
    return Promise.reject(error);
  }
);

// 获取用户信息
export const getUserInfo = (userId) => {
  return api.get(`/api/users/${userId}`);
};

// 获取前台信息
export const getReceptionistInfo = (receptionistId) => {
  return api.get(`/api/auth/receptionist/${receptionistId}`);
};

export const updateReceptionistInfo = (receptionistId, data) => {
  return api.put(`/api/auth/receptionist/${receptionistId}`, data);
};

export const changeReceptionistPassword = (receptionistId, passwordData) => {
  return api.put(`/api/auth/receptionist/${receptionistId}/password`, passwordData);
};

export const getDoctorInfo = (doctorId) => {
  return api.get(`/api/auth/doctor/${doctorId}`);
};

export const updateDoctorInfo = (doctorId, data) => {
  return api.put(`/api/auth/doctor/${doctorId}`, data);
};

export const changeDoctorPassword = (doctorId, passwordData) => {
  return api.put(`/api/auth/doctor/${doctorId}/password`, passwordData);
};

export const getAnnouncements = (category) => {
  const params = category ? { category } : {};
  return api.get('/api/announcements', { params });
};

export const createAnnouncement = (announcementData) => {
  return api.post('/api/announcements', announcementData);
};

export const deleteAnnouncement = (id) => {
  return api.delete(`/api/announcements/${id}`);
};

// 获取所有用户（客户）
export const getAllUsers = () => {
  return api.get('/api/users');
};

// 注册新用户
export const registerUser = (userData) => {
  return api.post('/api/users', userData);
};

// 删除用户
export const deleteUser = (userId) => {
  return api.delete(`/api/users/${userId}`);
};

// 获取所有宠物
export const getAllPets = () => {
  return api.get('/api/pets');
};

// 获取院长信息
export const getDirectorInfo = (directorId) => {
  return api.get(`/api/directors/${directorId}`);
};

// 更新院长信息
export const updateDirectorInfo = (directorId, directorData) => {
  return api.put(`/api/directors/${directorId}`, directorData);
};

// 修改院长密码
export const changeDirectorPassword = (directorId, passwordData) => {
  return api.put(`/api/directors/${directorId}/password`, passwordData);
};

// 更新用户信息
export const updateUserInfo = (userId, userData) => {
  return api.put(`/api/users/${userId}`, userData);
};

// 修改用户密码
export const changeUserPassword = (userId, passwordData) => {
  return api.put(`/api/users/${userId}/password`, passwordData);
};

// 删除用户
export const deleteUserInfo = (userId) => {
  return api.delete(`/api/users/${userId}`);
};

// 上传用户头像
export const uploadAvatar = (formData) => {
  return api.post('/api/avatar/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
};

export const uploadUserAvatar = uploadAvatar;

// 获取用户宠物列表
export const getUserPets = (userId) => {
  return api.get(`/api/pets?userId=${userId}`);
};

// 添加宠物
export const addPet = (petData) => {
  return api.post('/api/pets', petData);
};

// 更新宠物信息
export const updatePet = (petId, petData) => {
  return api.put(`/api/pets/${petId}`, petData);
};

// 删除宠物
export const deletePet = (petId, userId) => {
  return api.delete(`/api/pets/${petId}?userId=${userId}`);
};

// 获取宠物信息
export const getPetById = (petId, userId) => {
  return api.get(`/api/pets/${petId}?userId=${userId}`);
};

// 获取宠物病历列表
export const getMedicalRecordsByPetId = (petId) => {
  return api.get(`/api/medical-records?petId=${petId}`);
};

// 获取医生病历列表
export const getMedicalRecordsByDoctorId = (doctorId) => {
  return api.get(`/api/medical-records/doctor?doctorId=${doctorId}`);
};

// 获取医生病历详情列表（包含关联数据）
export const getMedicalRecordDetailsByDoctorId = (doctorId) => {
  return api.get(`/api/medical-records/doctor/detail?doctorId=${doctorId}`);
};

// 获取所有病历详情
export const getAllMedicalRecordDetail = () => {
  return api.get('/api/medical-records/all');
};

// 根据科室获取病历详情
export const getMedicalRecordDetailByDepartment = (department) => {
  return api.get(`/api/medical-records/department?department=${encodeURIComponent(department)}`);
};

// 添加病历
export const addMedicalRecord = (medicalRecordData) => {
  return api.post('/api/medical-records', medicalRecordData);
};

// 更新病历
export const updateMedicalRecord = (id, medicalRecordData) => {
  return api.put(`/api/medical-records/${id}`, medicalRecordData);
};

// ==================== 排班管理相关 ====================

// 获取医生排班信息（按科室和日期范围）
export const getDoctorSchedules = (params) => {
  return api.get('/api/doctor-schedules/department/month', { params });
};

// 获取所有医生排班信息（按日期范围）
export const getAllDoctorSchedules = (startDate, endDate) => {
  return api.get('/api/doctor-schedules/date-range', { 
    params: { startDate, endDate } 
  });
};

// 获取医生自己的排班信息
export const getMyDoctorSchedules = (doctorId, startDate, endDate) => {
  return api.get(`/api/doctor-schedules/doctor/${doctorId}`, { 
    params: { startDate, endDate } 
  });
};

// 根据日期和科室获取值班医生 ID 列表
export const getOnDutyDoctorsByDateAndDepartment = (date, department) => {
  return api.get('/api/doctor-schedules/on-duty/doctors', { 
    params: { date, department } 
  });
};

// 保存医生排班
export const saveDoctorSchedule = (data) => {
  return api.post('/api/doctor-schedules', data);
};

// 批量保存医生排班
export const saveDoctorSchedulesBatch = (data) => {
  return api.post('/api/doctor-schedules/batch', data);
};

export const updateDoctorSchedule = (data) => {
  return api.put('/api/doctor-schedules', data);
};

// 删除医生排班
export const deleteDoctorSchedule = (id) => {
  return api.delete(`/api/doctor-schedules/${id}`);
};

// 获取前台排班信息
export const getReceptionistSchedules = (params) => {
  return api.get('/api/receptionist-schedules', { params });
};

// 获取所有护士排班信息（按日期范围）
export const getAllNurseSchedules = (startDate, endDate) => {
  return api.get('/api/receptionist-schedules/date-range', { 
    params: { startDate, endDate } 
  });
};

// 获取护士自己的排班信息
export const getMyNurseSchedules = (receptionistId, startDate, endDate) => {
  return api.get(`/api/receptionist-schedules/receptionist/${receptionistId}`, { 
    params: { startDate, endDate } 
  });
};

// 保存前台排班
export const saveReceptionistSchedule = (data) => {
  return api.post('/api/receptionist-schedules', data);
};

// 批量保存前台排班
export const saveReceptionistSchedulesBatch = (data) => {
  return api.post('/api/receptionist-schedules/batch', data);
};

export const updateReceptionistSchedule = (data) => {
  return api.put('/api/receptionist-schedules', data);
};

// 删除前台排班
export const deleteReceptionistSchedule = (id) => {
  return api.delete(`/api/receptionist-schedules/${id}`);
};

// 获取所有护士列表
export const getAllNurses = () => {
  return api.get('/api/staff/receptionists');
};

// ==================== 费用管理相关 ====================

// 获取所有收费项目
export const getFeeItems = () => {
  return api.get('/api/fee-items');
};

// 新增收费项目
export const addFeeItem = (feeData) => {
  return api.post('/api/fee-items', feeData);
};

// 更新收费项目
export const updateFeeItem = (feeId, feeData) => {
  return api.put(`/api/fee-items/${feeId}`, feeData);
};

// 删除收费项目
export const deleteFeeItem = (feeId) => {
  return api.delete(`/api/fee-items/${feeId}`);
};

// ==================== 账单管理相关 ====================

// 获取待缴费列表
export const getPendingBillings = () => {
  return api.get('/api/billing/pending');
};

// 获取用户待缴费列表
export const getPendingBillingsByUserId = (userId) => {
  return api.get(`/api/billing/pending/user/${userId}`);
};

// 支付账单
export const payBilling = (id, paymentMethod) => {
  return api.post(`/api/billing/pay/${id}`, { paymentMethod });
};

// 创建账单
export const addBilling = (billingData) => {
  return api.post('/api/billing', billingData);
};

// 获取账单详情
export const getBillingById = (id) => {
  return api.get(`/api/billing/${id}`);
};

// 获取账单明细
export const getBillingItems = (id) => {
  return api.get(`/api/billing/${id}/items`);
};

// ==================== 宠物种类管理相关 ====================

// 获取所有宠物种类
export const getPetSpecies = () => {
  return api.get('/api/pet-species');
};

// 新增宠物种类
export const addPetSpecies = (speciesData) => {
  return api.post('/api/pet-species', speciesData);
};

// 更新宠物种类
export const updatePetSpecies = (speciesId, speciesData) => {
  return api.put(`/api/pet-species/${speciesId}`, speciesData);
};

// 删除宠物种类
export const deletePetSpecies = (speciesId) => {
  return api.delete(`/api/pet-species/${speciesId}`);
};

// 获取某个种类下的所有品种
export const getPetBreedsBySpeciesId = (speciesId) => {
  return api.get(`/api/pet-breeds?speciesId=${speciesId}`);
};

// 新增宠物品种
export const addPetBreed = (breedData) => {
  return api.post('/api/pet-breeds', breedData);
};

// 更新宠物品种
export const updatePetBreed = (breedId, breedData) => {
  return api.put(`/api/pet-breeds/${breedId}`, breedData);
};

// 删除宠物品种
export const deletePetBreed = (breedId) => {
  return api.delete(`/api/pet-breeds/${breedId}`);
};

// ==================== 科室管理相关 ====================

// 获取所有科室
export const getDepartments = () => {
  return api.get('/api/departments');
};

// 新增科室
export const addDepartment = (departmentData) => {
  return api.post('/api/departments', departmentData);
};

// 更新科室
export const updateDepartment = (departmentId, departmentData) => {
  return api.put(`/api/departments/${departmentId}`, departmentData);
};

// 删除科室
export const deleteDepartment = (departmentId) => {
  return api.delete(`/api/departments/${departmentId}`);
};

// 获取各科室医生人数统计
export const getDepartmentStats = () => {
  return api.get('/api/departments/stats');
};

// ==================== 药品库存管理相关 ====================

// 获取所有药品
export const getDrugInventory = () => {
  return api.get('/api/drug-inventory');
};

// 新增药品
export const addDrug = (drugData) => {
  return api.post('/api/drug-inventory', drugData);
};

// 更新药品
export const updateDrug = (drugId, drugData) => {
  return api.put(`/api/drug-inventory/${drugId}`, drugData);
};

// 删除药品
export const deleteDrug = (drugId) => {
  return api.delete(`/api/drug-inventory/${drugId}`);
};

// ==================== 员工管理相关 ====================

// 获取所有员工
export const getAllStaff = () => {
  return api.get('/api/staff');
};

// 根据 ID 获取员工
export const getStaffById = (staffId) => {
  return api.get(`/api/staff/${staffId}`);
};

// 更新员工信息
export const updateStaff = (staffId, staffData) => {
  return api.put(`/api/staff/${staffId}`, staffData);
};

// 删除员工
export const deleteStaff = (staffId) => {
  return api.delete(`/api/staff/${staffId}`);
};

// 新增员工
export const addStaff = (staffData) => {
  return api.post('/api/staff', staffData);
};

// 获取前台人员的科室列表
export const getReceptionistDepartments = () => {
  return api.get('/api/staff/receptionist-departments');
};

// ==================== 医生管理相关 ====================

// 根据科室获取医生列表
export const getDoctorsByDepartment = (department) => {
  return api.get(`/api/staff/doctors?department=${encodeURIComponent(department)}`);
};

// 获取所有医生列表
export const getAllDoctors = () => {
  return api.get('/api/staff/doctors');
};

// ==================== 排班管理相关 ====================

// ==================== 预约管理相关 ====================

// 获取用户预约列表
export const getUserAppointments = (userId) => {
  return api.get(`/api/appointments?userId=${userId}`);
};

// 创建新预约
export const createAppointment = (appointmentData) => {
  return api.post('/api/appointments', appointmentData);
};

// 取消预约
export const cancelAppointment = (appointmentId) => {
  return api.put(`/api/appointments/${appointmentId}/cancel`);
};

// 获取预约详情
export const getAppointmentById = (appointmentId) => {
  return api.get(`/api/appointments/${appointmentId}`);
};

// 获取所有预约（用于前台/医生查看）
export const getAllAppointments = () => {
  return api.get('/api/appointments/all');
};

// 根据医生ID获取今日预约
export const getAppointmentsByDoctorId = (doctorId) => {
  return api.get(`/api/appointments/doctor?doctorId=${doctorId}`);
};

// 根据科室获取所有预约（用于前台）
export const getAllAppointmentsByDepartment = (department) => {
  return api.get('/api/appointments/all-by-department?department=' + encodeURIComponent(department));
};

// 更新预约状态
export const updateAppointmentStatus = (appointmentId, status) => {
  return api.put(`/api/appointments/${appointmentId}/status?status=${status}`);
};

// 前台取号
export const takeNumber = (appointmentId) => {
  return api.put(`/api/appointments/${appointmentId}/take-number`);
};

export const startConsultation = (appointmentId, doctorId) => {
  return api.put(`/api/appointments/${appointmentId}/start-consultation?doctorId=${doctorId}`);
};

export const finishConsultation = (appointmentId, doctorId) => {
  return api.put(`/api/appointments/${appointmentId}/finish-consultation?doctorId=${doctorId}`);
};

// 统计相关 API
export const getTodayCompletedCount = () => {
  return api.get('/api/appointments/today-completed/count');
};

export const getTodayCompletedCountByDepartment = (department) => {
  return api.get(`/api/appointments/today-completed/department?department=${encodeURIComponent(department)}`);
};

export const getPendingBillingCount = () => {
  return api.get('/api/billing/pending/count');
};

export const getTodayNewUsersCount = () => {
  return api.get('/api/users/today-new/count');
};

export const getTodayRevenue = () => {
  return api.get('/api/billing/today-revenue');
};

export const getMonthCompletedCount = () => {
  return api.get('/api/appointments/month-completed');
};

export const getDepartmentCount = () => {
  return api.get('/api/appointments/department-count');
};

export const getTotalUsersCount = () => {
  return api.get('/api/users/total/count');
};

export const getLastSevenDaysRevenue = () => {
  return api.get('/api/billing/last-seven-days');
};

export const getDrugWarningCount = () => {
  return api.get('/api/drug-inventory/warning/count');
};

export default api;