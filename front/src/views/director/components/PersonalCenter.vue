<template>
  <div class="personal-center-wrapper">
    <el-card>
      <template #header>
        <span style="font-size: 16px; font-weight: bold;">{{ title }}</span>
      </template>
      
      <!-- 个人信息 -->
      <div v-show="activeType === 'profile'">
        <el-form label-width="100px" style="max-width: 600px; margin: 0 auto;">
          <el-form-item label="头像">
            <el-upload
              class="avatar-uploader"
              :show-file-list="false"
              :on-success="handleAvatarSuccess"
              :before-upload="beforeAvatarUpload"
            >
              <img v-if="profileForm.avatar" :src="profileForm.avatar" class="avatar" />
              <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
            </el-upload>
          </el-form-item>
          <el-form-item label="姓名">
            <el-input v-model="profileForm.name" placeholder="请输入姓名" />
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="profileForm.phone" placeholder="请输入手机号" />
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input v-model="profileForm.email" placeholder="请输入邮箱" />
          </el-form-item>
          <el-form-item label="入职日期">
            <el-date-picker
              v-model="profileForm.joinDate"
              type="date"
              placeholder="选择入职日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              style="width: 100%"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="saveProfile">保存修改</el-button>
            <el-button @click="resetProfile">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- 账户安全 -->
      <div v-show="activeType === 'security'">
        <el-form 
          ref="passwordFormRef"
          :model="passwordForm" 
          :rules="passwordRules" 
          label-width="120px"
          style="max-width: 600px; margin: 0 auto;"
        >
          <el-form-item label="当前密码" prop="oldPassword">
            <el-input 
              v-model="passwordForm.oldPassword" 
              type="password" 
              placeholder="请输入当前密码"
              show-password
            />
          </el-form-item>
          <el-form-item label="新密码" prop="newPassword">
            <el-input 
              v-model="passwordForm.newPassword" 
              type="password" 
              placeholder="请输入新密码"
              show-password
            />
            <div style="font-size: 12px; color: #909399; margin-top: 5px;">
              密码长度 6-20 位，可包含字母和数字
            </div>
          </el-form-item>
          <el-form-item label="确认新密码" prop="confirmPassword">
            <el-input 
              v-model="passwordForm.confirmPassword" 
              type="password" 
              placeholder="请再次输入新密码"
              show-password
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="changePassword" :loading="isChanging">确认修改</el-button>
            <el-button @click="resetPasswordForm">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue';
import { ElMessage } from 'element-plus';
import { Plus } from '@element-plus/icons-vue';
import { getDirectorInfo, updateDirectorInfo, changeDirectorPassword } from '@/services/api';

// 定义 props
const props = defineProps({
  activeType: {
    type: String,
    default: 'profile'
  }
});

// 计算标题
const title = computed(() => {
  return props.activeType === 'profile' ? '个人信息' : '账户安全';
});

// 头像上传相关
const pendingImage = ref('');

// 个人资料表单
const profileForm = reactive({
  id: '',
  name: '',
  phone: '',
  email: '',
  joinDate: '',
  avatar: ''
});

// 原始资料备份（用于重置）
const originalProfile = ref(null);

// 密码表单
const passwordFormRef = ref(null);
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
});

// 密码修改状态
const isChanging = ref(false);

// 密码验证规则
const validateConfirmPassword = (rule, value, callback) => {
  if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的密码不一致'));
  } else {
    callback();
  }
};

const passwordRules = reactive({
  oldPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少 6 位', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度 6-20 位', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9]+$/, message: '密码只能包含字母和数字', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
});

// 处理菜单切换
const handleMenuChange = (value) => {
  // 可以在这里添加一些切换时的逻辑
};

// 头像上传成功处理
const handleAvatarSuccess = (response, uploadFile) => {
  pendingImage.value = response.imageUrl;
  let avatarUrl = response.imageUrl;
  if (avatarUrl && !avatarUrl.startsWith('http') && !avatarUrl.startsWith('https')) {
    avatarUrl = 'http://localhost:8080' + avatarUrl;
  }
  profileForm.avatar = avatarUrl;
  ElMessage.success('头像上传成功');
};

// 上传前检查
const beforeAvatarUpload = (rawFile) => {
  if (rawFile.type !== 'image/jpeg' && rawFile.type !== 'image/png') {
    ElMessage.error('头像图片只能是 JPG 或 PNG 格式!');
    return false;
  } else if (rawFile.size / 1024 / 1024 > 2) {
    ElMessage.error('头像图片大小不能超过 2MB!');
    return false;
  }
  return true;
};

// 获取院长信息
const fetchDirectorInfo = async () => {
  try {
    const directorId = localStorage.getItem('userId');
    if (directorId) {
      const response = await getDirectorInfo(directorId);
      const data = response.data;
      
      profileForm.id = data.id || directorId;
      profileForm.name = data.name || '';
      profileForm.phone = data.phone || '';
      profileForm.email = data.email || '';
      profileForm.joinDate = data.hireDate || '';
      
      // 设置头像
      let avatarUrl = data.image || '';
      if (avatarUrl && !avatarUrl.startsWith('http') && !avatarUrl.startsWith('https')) {
        avatarUrl = 'http://localhost:8080' + avatarUrl;
      }
      profileForm.avatar = avatarUrl;
    }
  } catch (error) {
    console.error('获取院长信息失败:', error);
    ElMessage.error('获取个人信息失败');
  }
};

// 保存个人资料
const saveProfile = async () => {
  try {
    const directorId = localStorage.getItem('userId');
    await updateDirectorInfo(directorId, {
      name: profileForm.name,
      phone: profileForm.phone,
      email: profileForm.email,
      hireDate: profileForm.joinDate,
      image: pendingImage.value || profileForm.avatar
    });
    
    pendingImage.value = '';
    ElMessage.success('个人资料已更新');
  } catch (error) {
    console.error('更新个人资料失败:', error);
    ElMessage.error('更新失败，请重试');
  }
};

// 重置个人资料
const resetProfile = () => {
  fetchDirectorInfo();
  ElMessage.info('已重置为原始数据');
};

// 修改密码
const changePassword = async () => {
  if (!passwordFormRef.value) return;
  
  try {
    await passwordFormRef.value.validate();
    
    isChanging.value = true;
    
    const directorId = localStorage.getItem('userId');
    await changeDirectorPassword(directorId, {
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    });
    
    ElMessage.success('密码修改成功');
    resetPasswordForm();
  } catch (error) {
    if (error.response?.status === 401) {
      ElMessage.error('原密码错误');
    } else if (error.message?.includes('验证失败')) {
      // 表单验证失败，不显示错误
    } else {
      console.error('修改密码失败:', error);
      ElMessage.error('修改失败，请重试');
    }
  } finally {
    isChanging.value = false;
  }
};

// 重置密码表单
const resetPasswordForm = () => {
  passwordForm.oldPassword = '';
  passwordForm.newPassword = '';
  passwordForm.confirmPassword = '';
  if (passwordFormRef.value) {
    passwordFormRef.value.clearValidate();
  }
};

// 组件挂载时获取信息
onMounted(() => {
  fetchDirectorInfo();
});
</script>

<style scoped>
.personal-center-wrapper {
  padding: 20px;
  background: #f5f7fa;
  min-height: calc(100vh - 140px);
}

.avatar-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all 0.3s;
}

.avatar-uploader:hover {
  border-color: #409EFF;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  text-align: center;
  line-height: 178px;
}

.avatar {
  width: 178px;
  height: 178px;
  display: block;
  object-fit: cover;
}

:deep(.el-form-item__label) {
  font-weight: 500;
}

:deep(.el-select) {
  font-size: 14px;
}
</style>
