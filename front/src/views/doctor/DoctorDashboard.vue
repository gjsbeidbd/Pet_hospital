<script setup>
import {
  ref,
  onMounted
} from 'vue'
import {
  ElMessage,
} from 'element-plus'
import { useRouter } from 'vue-router'
import { getUserInfo } from '@/services/api'
import Layout from '@/components/Layout.vue'

// 导入子组件
import Consultation from './components/Consultation.vue'
import MedicalRecords from './components/MedicalRecords.vue'
import Schedule from './components/Schedule.vue'
import DoctorProfile from './components/DoctorProfile.vue'
import DoctorChangePassword from './components/DoctorChangePassword.vue'

// ========== 响应式状态 ==========
const activeMenu = ref('1')
const consultationRef = ref(null)
const userName = ref('') // 用户名
const userAvatar = ref('') // 用户头像
const layoutRef = ref(null)

// Router
const router = useRouter()

// 菜单配置
const menuList = [
  {
    index: '1',
    title: '接诊工作台',
    icon: 'Monitor'
  },
  {
    index: '2',
    title: '病历查询',
    icon: 'DocumentCopy'
  },
  {
    index: '3',
    title: '我的排班',
    icon: 'Calendar'
  },
  {
    index: '4',
    title: '个人中心',
    icon: 'User',
    children: [
      {
        index: '4-1',
        title: '修改个人信息',
        icon: 'Edit'
      },
      {
        index: '4-2',
        title: '账户密码修改',
        icon: 'Lock'
      }
    ]
  }
]

const waitingList = ref([
  { id: 1, no: 'A007', name: '旺财', breed: '金毛', age: 3, weight: 28, owner: '张三', reason: '连续两天不吃东西，呕吐', type: '普通' },
  { id: 2, no: 'A008', name: '咪咪', breed: '布偶猫', age: 1, weight: 4.2, owner: '李四', reason: '眼睛发炎，流泪', type: '普通' },
  { id: 3, no: 'B001', name: '大黄', breed: '中华田园犬', age: 5, weight: 15, owner: '王五', reason: '车祸外伤', type: '急诊' }
])

const drugOptions = [
  { id: 1, name: '阿莫西林 (宠物用)', stock: 50 },
  { id: 2, name: '益生菌调理剂', stock: 120 },
  { id: 3, name: '消炎眼药水', stock: 30 },
  { id: 4, name: '驱虫药 (外驱)', stock: 200 }
]

// ========== 方法 ==========
const handleSelect = (key) => {
  activeMenu.value = key
  if (layoutRef.value) {
    layoutRef.value.setActiveMenu(key)
  }
}

// 处理 Layout 组件的 activeMenu 更新事件
const handleActiveMenuUpdate = (menuIndex) => {
  activeMenu.value = menuIndex
}

const handleCallPatient = (patient) => {
  ElMessage.success(`已开始接诊：${patient.name}`)
}

const handleFinishDiagnose = (patientId) => {
  waitingList.value = waitingList.value.filter(p => p.id !== patientId)
}

// 页面加载时的初始化逻辑
onMounted(() => {
  // 可以在这里添加初始化逻辑
  fetchUserInfo()
})

// 退出登录
const handleLogout = () => {
  // 清除本地存储的用户信息
  localStorage.removeItem('token')
  localStorage.removeItem('userRole')
  localStorage.removeItem('userId')
  
  // 跳转到登录页
  router.push('/login')
}

// 获取用户信息
const fetchUserInfo = async () => {
  try {
    const userId = localStorage.getItem('userId')
    if (userId) {
      const response = await getUserInfo(userId)
      userName.value = response.data.name || '医生'
      // 设置用户头像
      let avatarUrl = response.data.image || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
      if (avatarUrl && !avatarUrl.startsWith('http') && !avatarUrl.startsWith('https')) {
        avatarUrl = 'http://localhost:8080' + avatarUrl
      }
      userAvatar.value = avatarUrl
    } else {
      userName.value = '医生'
      userAvatar.value = 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
    userName.value = '医生'
    userAvatar.value = 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
  }
}
</script>

<template>
  <Layout
    role="doctor"
    roleText="执业医师"
    :menu-list="menuList"
    :username="userName"
    :user-avatar="userAvatar"
    :active-menu="activeMenu"
    ref="layoutRef"
    @update:active-menu="handleActiveMenuUpdate"
  >
    <!-- 接诊工作台 -->
    <div v-if="activeMenu === '1'">
      <Consultation 
        :waiting-list="waitingList" 
        :drug-options="drugOptions"
        @call-patient="handleCallPatient"
        @finish-diagnose="handleFinishDiagnose"
        ref="consultationRef"
      />
    </div>

    <!-- 病历查询 -->
    <div v-if="activeMenu === '2'">
      <MedicalRecords />
    </div>

    <!-- 我的排班 -->
    <div v-if="activeMenu === '3'">
      <Schedule />
    </div>

    <!-- 4-1. 个人信息 -->
    <div v-if="activeMenu === '4-1'">
      <DoctorProfile />
    </div>

    <!-- 4-2. 修改密码 -->
    <div v-if="activeMenu === '4-2'">
      <DoctorChangePassword />
    </div>
  </Layout>
</template>

<style scoped>
.full-height {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.full-height > div {
  flex: 1;
  display: flex;
  flex-direction: column;
}
</style>