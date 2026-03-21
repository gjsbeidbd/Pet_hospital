<template>
  <div>
    <el-card>
      <template #header>
        <span>个人信息修改</span>
      </template>
      
      <el-form label-width="80px" style="max-width: 600px;">
        <el-form-item label="头像">
          <el-upload
            class="avatar-uploader"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
            :before-upload="beforeAvatarUpload"
            :http-request="handleAvatarUpload"
          >
            <img v-if="displayedImageUrl" :src="displayedImageUrl" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="真实姓名">
          <el-input v-model="profileForm.name"></el-input>
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="profileForm.phone"></el-input>
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="profileForm.email"></el-input>
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="profileForm.address" placeholder="选填，用于上门服务"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="updateProfile">保存修改</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getUserInfo, updateUserInfo, uploadUserAvatar } from '@/services/api'

const pendingImage = ref('') // 用于存储待保存的图片URL
const displayedImageUrl = ref('') // 用于控制显示的头像URL

// 个人信息表单
const profileForm = reactive({
  id: '',
  username: '',
  name: '',
  phone: '',
  email: '',
  address: '',
  image: ''
})

// 头像上传成功处理
const handleAvatarSuccess = (response, uploadFile) => {
  // 将上传成功的头像URL保存到待保存变量
  pendingImage.value = response.imageUrl
  // 更新显示的头像URL，以便用户可以看到预览
  // 确保头像URL是有效的，如果是相对路径则加上基础URL
  let avatarUrl = response.imageUrl
  if (avatarUrl && !avatarUrl.startsWith('http') && !avatarUrl.startsWith('https')) {
    // 如果是相对路径，加上基础URL
    avatarUrl = 'http://localhost:8080' + avatarUrl
  }
  displayedImageUrl.value = avatarUrl
  // 不再显示上传成功的消息，避免用户误解
}

// 自定义头像上传处理
const handleAvatarUpload = async (options) => {
  const { file, onError, onSuccess } = options

  const formData = new FormData()
  formData.append('file', file)
  formData.append('userId', profileForm.id)
  formData.append('userType', 'user')

  try {
    const response = await uploadUserAvatar(formData)
    onSuccess(response.data)
  } catch (error) {
    onError(error)
    ElMessage.error('头像上传失败: ' + (error.response?.data?.error || '未知错误'))
  }
}

// 上传前检查
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

// 获取用户信息
const fetchUserInfo = async () => {
  try {
    const userId = localStorage.getItem('userId')
    if (userId) {
      const response = await getUserInfo(userId)
      const user = response.data
      profileForm.id = user.id
      profileForm.username = user.username
      profileForm.name = user.name
      profileForm.phone = user.phone
      profileForm.email = user.email
      profileForm.address = user.address
      profileForm.image = user.image || ''
      // 设置显示的头像URL为用户的头像
      // 确保头像URL是有效的，如果是相对路径则加上基础URL
      let avatarUrl = user.image || ''
      if (avatarUrl && !avatarUrl.startsWith('http') && !avatarUrl.startsWith('https')) {
        // 如果是相对路径，加上基础URL
        avatarUrl = 'http://localhost:8080' + avatarUrl
      }
      displayedImageUrl.value = avatarUrl
      // 确保待保存的图片URL为空
      pendingImage.value = ''
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
    ElMessage.error('获取用户信息失败')
  }
}

// 更新个人信息
const updateProfile = async () => {
  try {
    const userId = localStorage.getItem('userId')
    if (!userId) {
      ElMessage.error('用户未登录')
      return
    }
    
    // 构造要更新的用户数据，包含头像信息
    const userData = {
      name: profileForm.name,
      phone: profileForm.phone,
      email: profileForm.email,
      address: profileForm.address,
      image: pendingImage.value || profileForm.image // 使用新上传的头像或原有的头像
    }
    
    const response = await updateUserInfo(userId, userData)
    // 更新成功后，同步更新表单中的头像信息
    profileForm.image = userData.image
    // 更新显示的头像URL
    // 确保头像URL是有效的，如果是相对路径则加上基础URL
    let avatarUrl = userData.image
    if (avatarUrl && !avatarUrl.startsWith('http') && !avatarUrl.startsWith('https')) {
      // 如果是相对路径，加上基础URL
      avatarUrl = 'http://localhost:8080' + avatarUrl
    }
    displayedImageUrl.value = avatarUrl
    // 清空待保存的图片URL
    pendingImage.value = ''
    ElMessage.success('个人信息更新成功！')
    
    // 更新顶部导航栏的头像显示
    // 确保头像URL是有效的，如果是相对路径则加上基础URL
    let eventAvatarUrl = userData.image
    if (eventAvatarUrl && !eventAvatarUrl.startsWith('http') && !eventAvatarUrl.startsWith('https')) {
      // 如果是相对路径，加上基础URL
      eventAvatarUrl = 'http://localhost:8080' + eventAvatarUrl
    }
    const userAvatarEvent = new CustomEvent('userAvatarUpdated', { 
      detail: { avatar: eventAvatarUrl }
    });
    window.dispatchEvent(userAvatarEvent);
    
    console.log('更新的个人信息:', response.data)
  } catch (error) {
    console.error('更新用户信息失败:', error)
    ElMessage.error('更新用户信息失败: ' + (error.response?.data?.error || '未知错误'))
  }
}

// 在组件挂载时获取用户信息
onMounted(() => {
  fetchUserInfo()
})
</script>

<style scoped>
.avatar-uploader .avatar {
  width: 120px;
  height: 120px;
  display: block;
  border: 2px dashed #ccc;
  border-radius: 6px;
}
.avatar-uploader .el-upload {
  border: 2px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
  width: 120px;
  height: 120px;
}
.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 120px;
  height: 120px;
  text-align: center;
  border: 2px dashed #ccc;
  border-radius: 6px;
}
</style>