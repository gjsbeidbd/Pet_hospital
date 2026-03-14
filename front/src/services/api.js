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

// 获取排班信息
export const getSchedules = (params) => {
  return api.get('/api/schedules', { params });
};

// 应用排班到周期
export const applyScheduleToPeriod = (data) => {
  return api.post('/api/schedules/apply-to-period', data);
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



export default api;