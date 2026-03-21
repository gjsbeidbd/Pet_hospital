<template>
  <div class="personal-center-wrapper">
    <el-card>
      <template #header>
        <span style="font-size: 16px; font-weight: bold;">{{ title }}</span>
      </template>

      <!-- 个人信息 -->
      <div v-show="activeType === 'profile'">
        <div class="form-container">
          <el-form label-width="80px" style="max-width: 600px;">
            <el-form-item label="头像">
              <el-upload
                class="avatar-uploader"
                :show-file-list="false"
                :http-request="handleAvatarUpload"
                :before-upload="beforeAvatarUpload"
              >
                <img v-if="displayedImageUrl" :src="displayedImageUrl" class="avatar" />
                <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
              </el-upload>
            </el-form-item>
            <el-form-item label="工&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;号">
              <el-input v-model="profileForm.employeeId" disabled />
            </el-form-item>
            <el-form-item label="姓&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;名">
              <el-input v-model="profileForm.name" />
            </el-form-item>
            <el-form-item label="出生日期">
              <el-date-picker
                v-model="profileForm.birthday"
                type="date"
                placeholder="选择出生日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
            <el-form-item label="身份证号">
              <el-input v-model="profileForm.idCard" maxlength="18" />
            </el-form-item>
            <el-form-item label="居住地址">
              <el-input v-model="profileForm.address" />
            </el-form-item>
            <el-form-item label="电&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;话">
              <el-input v-model="profileForm.phone" />
            </el-form-item>
            <el-form-item label="邮&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;箱">
              <el-input v-model="profileForm.email" />
            </el-form-item>
            <el-form-item label="入职日期">
              <el-date-picker
                v-model="profileForm.hireDate"
                type="date"
                placeholder="选择入职日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                style="width: 100%"
                disabled
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveProfile">保存修改</el-button>
            </el-form-item>
          </el-form>
        </div>
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
import { getDirectorInfo, updateDirectorInfo, changeDirectorPassword, uploadAvatar } from '@/services/api';

const props = defineProps({
  activeType: {
    type: String,
    default: 'profile'
  }
});

const title = computed(() => {
  return props.activeType === 'profile' ? '个人信息' : '账户安全';
});

const displayedImageUrl = ref('');
const pendingImage = ref('');

const profileForm = reactive({
  employeeId: '',
  name: '',
  birthday: '',
  idCard: '',
  address: '',
  phone: '',
  email: '',
  hireDate: '',
  image: ''
});

const originalProfile = ref(null);

const passwordFormRef = ref(null);
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
});

const isChanging = ref(false);

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

const handleAvatarUpload = async (options) => {
  const { file, onError, onSuccess } = options;

  const directorId = localStorage.getItem('userId');
  if (!directorId) {
    onError(new Error('用户未登录'));
    return;
  }

  const formData = new FormData();
  formData.append('file', file);
  formData.append('userId', directorId);
  formData.append('userType', 'DIRECTOR');

  try {
    const res = await uploadAvatar(formData);
    if (res.data.imageUrl) {
      pendingImage.value = res.data.imageUrl;
      let avatarUrl = res.data.imageUrl;
      if (avatarUrl && !avatarUrl.startsWith('http') && !avatarUrl.startsWith('https')) {
        avatarUrl = 'http://localhost:8080' + avatarUrl;
      }
      displayedImageUrl.value = avatarUrl;
      profileForm.image = avatarUrl;
      onSuccess(res.data);
      ElMessage.success('头像上传成功');
    } else if (res.data.error) {
      onError(new Error(res.data.error));
      ElMessage.error(res.data.error);
    }
  } catch (error) {
    onError(error);
    ElMessage.error('头像上传失败');
  }
};

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

const fetchDirectorInfo = async () => {
  try {
    const directorId = localStorage.getItem('userId');
    if (directorId) {
      const response = await getDirectorInfo(directorId);
      const data = response.data;

      profileForm.employeeId = data.employeeId || '';
      profileForm.name = data.name || '';
      profileForm.birthday = data.birthday || '';
      profileForm.idCard = data.idCard || '';
      profileForm.address = data.address || '';
      profileForm.phone = data.phone || '';
      profileForm.email = data.email || '';
      profileForm.hireDate = data.hireDate || '';

      let imageUrl = data.image || '';
      if (imageUrl && !imageUrl.startsWith('http') && !imageUrl.startsWith('https')) {
        imageUrl = 'http://localhost:8080' + imageUrl;
      }
      profileForm.image = imageUrl;
      displayedImageUrl.value = imageUrl;
      pendingImage.value = '';

      originalProfile.value = { ...profileForm };
    }
  } catch (error) {
    console.error('获取院长信息失败:', error);
    ElMessage.error('获取个人信息失败');
  }
};

const saveProfile = async () => {
  try {
    const directorId = localStorage.getItem('userId');
    await updateDirectorInfo(directorId, {
      name: profileForm.name,
      birthday: profileForm.birthday,
      idCard: profileForm.idCard,
      address: profileForm.address,
      phone: profileForm.phone,
      email: profileForm.email,
      hireDate: profileForm.hireDate,
      image: profileForm.image
    });
    ElMessage.success('保存成功');
    fetchDirectorInfo();
  } catch (error) {
    console.error('保存失败:', error);
    ElMessage.error('保存失败');
  }
};

const resetProfile = () => {
  if (originalProfile.value) {
    Object.assign(profileForm, originalProfile.value);
  }
};

const changePassword = async () => {
  passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        isChanging.value = true;
        const directorId = localStorage.getItem('userId');
        await changeDirectorPassword(directorId, {
          oldPassword: passwordForm.oldPassword,
          newPassword: passwordForm.newPassword
        });
        ElMessage.success('密码修改成功');
        resetPasswordForm();
      } catch (error) {
        console.error('修改密码失败:', error);
        ElMessage.error(error.response?.data?.error || '修改密码失败');
      } finally {
        isChanging.value = false;
      }
    }
  });
};

const resetPasswordForm = () => {
  passwordForm.oldPassword = '';
  passwordForm.newPassword = '';
  passwordForm.confirmPassword = '';
};

onMounted(() => {
  fetchDirectorInfo();
});
</script>

<style scoped>
.personal-center-wrapper {
  padding: 20px;
}

.form-container {
  display: flex;
  justify-content: center;
}

.avatar-uploader {
  width: 100px;
  height: 100px;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: border-color 0.3s;
}

.avatar-uploader:hover {
  border-color: #409eff;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.avatar {
  width: 100px;
  height: 100px;
  object-fit: cover;
}
</style>