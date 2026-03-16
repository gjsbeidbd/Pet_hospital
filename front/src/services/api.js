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
      // token过期或无效，清除本地存储并跳转到登录页
      localStorage.removeItem('token');
      localStorage.removeItem('userRole');
      localStorage.removeItem('userId');
      // 如果不在登录页，则跳转到登录页
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
export const uploadUserAvatar = (formData) => {
  return api.post('/api/avatar/avatar', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
};

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

// 保存医生排班
export const saveDoctorSchedule = (data) => {
  return api.post('/api/doctor-schedules', data);
};

// 批量保存医生排班
export const saveDoctorSchedulesBatch = (data) => {
  return api.post('/api/doctor-schedules/batch', data);
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

// 保存前台排班
export const saveReceptionistSchedule = (data) => {
  return api.post('/api/receptionist-schedules', data);
};

// 批量保存前台排班
export const saveReceptionistSchedulesBatch = (data) => {
  return api.post('/api/receptionist-schedules/batch', data);
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

export default api;