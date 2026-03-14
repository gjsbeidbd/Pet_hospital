<template>
  <div>
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card header="账户安全设置">
          <el-form label-width="100px" style="max-width: 600px;">
            <el-form-item label="旧密码">
              <el-input v-model="passwordForm.oldPassword" type="password" show-password placeholder="请输入旧密码"></el-input>
            </el-form-item>
            <el-form-item label="新密码">
              <el-input v-model="passwordForm.newPassword" type="password" show-password placeholder="请输入新密码"></el-input>
            </el-form-item>
            <el-form-item label="确认新密码">
              <el-input v-model="passwordForm.confirmPassword" type="password" show-password placeholder="请再次输入新密码"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="warning" @click="updatePassword">更新密码</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { ElMessage } from 'element-plus'

// 密码表单
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 密码验证规则
const validatePassword = () => {
  if (!passwordForm.oldPassword) {
    ElMessage.error('请输入旧密码')
    return false
  }
  if (!passwordForm.newPassword) {
    ElMessage.error('请输入新密码')
    return false
  }
  if (passwordForm.newPassword.length < 6) {
    ElMessage.error('新密码长度至少6位')
    return false
  }
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    ElMessage.error('两次输入的密码不一致')
    return false
  }
  return true
}

// 更新密码
const updatePassword = () => {
  if (!validatePassword()) {
    return
  }
  
  // 这里可以添加实际的后端API调用逻辑
  console.log('提交密码修改:', passwordForm)
  
  ElMessage.success('密码修改成功！')
  
  // 重置表单
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
}
</script>

<style scoped>
/* 组件特定样式 */
</style>