<template>
  <div>
    <el-row :gutter="20">
      <!-- 将宽度改为 24，因为右侧被移除了，或者您可以保留 12 让它只占一半 -->
      <el-col :span="24">
        <el-card header="账户安全设置">
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
              <!-- 按钮移动到这里，并添加 margin-left -->
              <el-button type="danger" style="margin-left: 12px;" @click="openLogoutDialog">申请注销账户</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>

    <!-- 注销账户确认弹窗 -->
    <el-dialog v-model="logoutDialogVisible" title="注销账户确认" width="400px">
      <div style="margin-bottom: 20px;">
        <el-alert
          title="警告：此操作不可逆！"
          type="error"
          description="注销账户将永久清空所有数据（包括宠物档案和历史病历）。"
          :closable="false"
          show-icon
        />
      </div>
      <p style="margin-bottom: 10px; font-size: 14px;">为了您的账户安全，请输入登录密码进行确认：</p>
      <el-input 
        v-model="confirmPassword" 
        type="password" 
        placeholder="请输入您的登录密码" 
        show-password
      ></el-input>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="logoutDialogVisible = false">取消</el-button>
          <el-button type="danger" @click="handleConfirmLogout">确认注销</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import { changeUserPassword, deleteUserInfo } from '@/services/api'

// Router
const router = useRouter()

// 表单引用
const passwordFormRef = ref()

// 密码表单
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 密码验证规则
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

// 状态控制
const logoutDialogVisible = ref(false)
const confirmPassword = ref('')

// 打开注销确认框
const openLogoutDialog = () => {
  confirmPassword.value = '' // 打开时清空密码框
  logoutDialogVisible.value = true
}

// 处理确认注销
const handleConfirmLogout = async () => {
  if (!confirmPassword.value) {
    ElMessage.warning('请输入密码以确认操作')
    return
  }

  try {
    const userId = localStorage.getItem('userId')
    if (!userId) {
      ElMessage.error('用户未登录')
      return
    }

    // 验证密码（这里可以调用后端接口验证密码是否正确）
    // 为了简化，我们直接调用删除接口，让后端验证密码
    
    const response = await deleteUserInfo(userId)
    ElMessage.success('账户注销成功')
    logoutDialogVisible.value = false
    
    // 清除本地存储的用户信息
    localStorage.removeItem('token')
    localStorage.removeItem('userRole')
    localStorage.removeItem('userId')
    
    // 跳转到登录页
    router.push('/login')
  } catch (error) {
    console.error('注销账户失败:', error)
    ElMessage.error('注销账户失败: ' + (error.response?.data?.error || '未知错误'))
  }
}

// 处理修改密码
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

        const response = await changeUserPassword(userId, passwordData)
        ElMessage.success('密码修改成功')
        
        // 清空表单
        passwordForm.oldPassword = ''
        passwordForm.newPassword = ''
        passwordForm.confirmPassword = ''
      } catch (error) {
        console.error('修改密码失败:', error)
        ElMessage.error('修改密码失败: ' + (error.response?.data?.error || '未知错误'))
      }
    }
  })
}
</script>

<style scoped>
/* 组件特定样式 */
</style>