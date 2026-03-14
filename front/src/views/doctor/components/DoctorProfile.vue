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
            action="/api/upload"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
            :before-upload="beforeAvatarUpload"
          >
            <img v-if="imageUrl" :src="imageUrl" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="工号">
          <el-input v-model="profileForm.id" disabled>
            <template #append>
              <el-tooltip content="工号是您的登录账号" placement="top">
                <i class="el-icon-question"></i>
              </el-tooltip>
            </template>
          </el-input>
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
        <el-form-item label="科室">
          <el-input v-model="profileForm.department" disabled></el-input>
        </el-form-item>
        <el-form-item label="职位">
          <el-input v-model="profileForm.position" disabled></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="updateProfile">保存修改</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

// 个人信息表单
const profileForm = reactive({
  id: '1001',
  name: '王建国',
  phone: '13800000001',
  email: 'wangjianguo@example.com',
  department: '内科',
  position: '主任医师'
})

const imageUrl = ref('')

// 头像上传成功处理
const handleAvatarSuccess = (response, uploadFile) => {
  imageUrl.value = URL.createObjectURL(uploadFile.raw)
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

// 更新个人信息
const updateProfile = () => {
  ElMessage.success('个人信息更新成功！')
  console.log('更新的个人信息:', profileForm)
}
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