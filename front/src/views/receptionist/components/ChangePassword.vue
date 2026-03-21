<template>
  <div class="change-password-wrapper">
    <el-row :gutter="20" class="change-password-row">
      <el-col :span="24">
        <el-card header="账户安全设置" class="change-password-card">
          <el-form
            label-width="100px"
            style="max-width: 600px;"
            :model="passwordForm"
            :rules="passwordRules"
            ref="passwordFormRef"
          >
            <el-form-item label="旧密码" prop="oldPassword">
              <el-input
                v-model="passwordForm.oldPassword"
                type="password"
                show-password
                placeholder="请输入旧密码"
              ></el-input>
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input
                v-model="passwordForm.newPassword"
                type="password"
                show-password
                placeholder="请输入新密码"
              ></el-input>
            </el-form-item>
            <el-form-item label="确认新密码" prop="confirmPassword">
              <el-input
                v-model="passwordForm.confirmPassword"
                type="password"
                show-password
                placeholder="请再次输入新密码"
              ></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="warning" @click="handleChangePassword">更新密码</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { changeReceptionistPassword } from '@/services/api'

const passwordFormRef = ref()

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入旧密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

const handleChangePassword = () => {
  passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const userId = localStorage.getItem('userId')
        if (!userId) {
          ElMessage.error('用户未登录')
          return
        }

        const passwordData = {
          oldPassword: passwordForm.oldPassword,
          newPassword: passwordForm.newPassword
        }

        await changeReceptionistPassword(userId, passwordData)
        ElMessage.success('密码修改成功')

        passwordForm.oldPassword = ''
        passwordForm.newPassword = ''
        passwordForm.confirmPassword = ''
      } catch (error) {
        console.error('修改密码失败:', error)
        ElMessage.error(error.response?.data?.error || '修改密码失败')
      }
    }
  })
}
</script>

<style scoped>
.change-password-wrapper {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.change-password-row {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.change-password-card {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.change-password-card :deep(.el-card__body) {
  flex: 1;
  display: flex;
  flex-direction: column;
}
</style>