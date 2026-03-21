<template>
  <div class="profile-wrapper">
    <el-card class="profile-card">
      <template #header>
        <span>个人信息修改</span>
      </template>

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
            <el-input v-model="profileForm.employeeId" disabled></el-input>
          </el-form-item>
          <el-form-item label="真实姓名">
            <el-input v-model="profileForm.name"></el-input>
          </el-form-item>
          <el-form-item label="出生日期">
            <el-date-picker
              v-model="profileForm.birthday"
              type="date"
              placeholder="选择出生日期"
              style="width: 100%"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
            ></el-date-picker>
          </el-form-item>
          <el-form-item label="身份证号">
            <el-input v-model="profileForm.idCard" maxlength="18"></el-input>
          </el-form-item>
          <el-form-item label="居住地址">
            <el-input v-model="profileForm.address"></el-input>
          </el-form-item>
          <el-form-item label="电话号码">
            <el-input v-model="profileForm.phone"></el-input>
          </el-form-item>
          <el-form-item label="邮&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;箱">
            <el-input v-model="profileForm.email"></el-input>
          </el-form-item>
          <el-form-item label="科&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;室">
            <el-input v-model="profileForm.department" disabled></el-input>
          </el-form-item>
          <el-form-item label="职&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;称">
            <el-input v-model="profileForm.position" disabled></el-input>
          </el-form-item>
          <el-form-item label="入职日期">
            <el-date-picker
              v-model="profileForm.hireDate"
              type="date"
              placeholder="选择入职日期"
              style="width: 100%"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              disabled
            ></el-date-picker>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="updateProfile">保存修改</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getDoctorInfo, updateDoctorInfo, uploadAvatar } from '@/services/api'

const displayedImageUrl = ref('')
const pendingImage = ref('')

const profileForm = reactive({
  employeeId: '',
  name: '',
  birthday: '',
  idCard: '',
  address: '',
  phone: '',
  email: '',
  department: '',
  position: '',
  hireDate: '',
  avatar: ''
})

const loading = ref(false)

const fetchProfile = async () => {
  loading.value = true
  try {
    const userId = localStorage.getItem('userId')
    const userRole = localStorage.getItem('userRole')
    if (userId && userRole === 'DOCTOR') {
      const res = await getDoctorInfo(userId)
      if (res.data) {
        const data = res.data
        profileForm.employeeId = data.employeeId || ''
        profileForm.name = data.name || ''
        profileForm.birthday = data.birthday || ''
        profileForm.idCard = data.idCard || ''
        profileForm.address = data.address || ''
        profileForm.phone = data.phone || ''
        profileForm.email = data.email || ''
        profileForm.department = data.department || ''
        profileForm.position = data.position || ''
        profileForm.hireDate = data.hireDate || ''
        profileForm.avatar = data.avatar || ''

        let avatarUrl = data.avatar || ''
        if (avatarUrl && !avatarUrl.startsWith('http') && !avatarUrl.startsWith('https')) {
          avatarUrl = 'http://localhost:8080' + avatarUrl
        }
        displayedImageUrl.value = avatarUrl
        pendingImage.value = ''
      }
    }
  } catch (error) {
    console.error('获取个人信息失败:', error)
    ElMessage.error('获取个人信息失败')
  } finally {
    loading.value = false
  }
}

const beforeAvatarUpload = (rawFile) => {
  if (rawFile.type !== 'image/jpeg' && rawFile.type !== 'image/png') {
    ElMessage.error('头像图片只能是 JPG 或 PNG 格式!')
    return false
  } else if (rawFile.size / 1024 / 1024 > 2) {
    ElMessage.error('头像图片大小不能超过 2MB!')
    return false
  }
  return true
}

const handleAvatarUpload = async (options) => {
  const { file, onError, onSuccess } = options

  const userId = localStorage.getItem('userId')
  const userRole = localStorage.getItem('userRole')
  if (!userId || userRole !== 'DOCTOR') {
    onError(new Error('用户未登录'))
    return
  }

  const formData = new FormData()
  formData.append('file', file)
  formData.append('userId', userId)
  formData.append('userType', 'DOCTOR')

  try {
    const res = await uploadAvatar(formData)
    if (res.data.imageUrl) {
      pendingImage.value = res.data.imageUrl
      let avatarUrl = res.data.imageUrl
      if (avatarUrl && !avatarUrl.startsWith('http') && !avatarUrl.startsWith('https')) {
        avatarUrl = 'http://localhost:8080' + avatarUrl
      }
      displayedImageUrl.value = avatarUrl
      onSuccess(res.data)
    } else if (res.data.error) {
      onError(new Error(res.data.error))
      ElMessage.error(res.data.error)
    }
  } catch (error) {
    onError(error)
    ElMessage.error('头像上传失败')
  }
}

const updateProfile = async () => {
  try {
    const userId = localStorage.getItem('userId')
    const userRole = localStorage.getItem('userRole')
    if (userId && userRole === 'DOCTOR') {
      const updateData = {
        name: profileForm.name,
        birthday: profileForm.birthday,
        idCard: profileForm.idCard,
        address: profileForm.address,
        phone: profileForm.phone,
        email: profileForm.email,
        avatar: pendingImage.value || profileForm.avatar
      }
      await updateDoctorInfo(userId, updateData)
      profileForm.avatar = updateData.avatar
      pendingImage.value = ''
      ElMessage.success('个人信息更新成功！')
    }
  } catch (error) {
    console.error('更新个人信息失败:', error)
    ElMessage.error('个人信息更新失败')
  }
}

onMounted(() => {
  fetchProfile()
})
</script>

<style scoped>
.profile-wrapper {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.profile-card {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.profile-card :deep(.el-card__body) {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.form-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.avatar-uploader .avatar {
  width: 120px;
  height: 120px;
  display: block;
  border-radius: 50%;
  object-fit: cover;
}

.avatar-uploader .el-upload {
  border: 2px dashed var(--el-border-color);
  border-radius: 50%;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
  width: 120px;
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  text-align: center;
  line-height: 100px;
  border: 2px dashed #d9d9d9;
  border-radius: 50%;
}
</style>