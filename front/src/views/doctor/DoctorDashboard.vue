<script setup>
import {
  ref,
  onMounted
} from 'vue'
import {
  ElMessage,
} from 'element-plus'
import { useRouter } from 'vue-router'
import { getDoctorInfo, getAllAppointmentsByDepartment, getDrugInventory } from '@/services/api'
import Layout from '@/components/Layout.vue'

// 导入子组件
import Consultation from './components/Consultation.vue'
import MedicalRecords from './components/MedicalRecords.vue'
import Schedule from './components/Schedule.vue'
import DoctorProfile from './components/DoctorProfile.vue'
import DoctorChangePassword from './components/DoctorChangePassword.vue'
import DoctorHome from './components/DoctorHome.vue'

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
    title: '首页',
    icon: 'HomeFilled'
  },
  {
    index: '2',
    title: '接诊工作台',
    icon: 'Monitor'
  },
  {
    index: '3',
    title: '病历查询',
    icon: 'DocumentCopy'
  },
  {
    index: '4',
    title: '排班表查看',
    icon: 'Calendar'
  },
  {
    index: '5',
    title: '个人中心',
    icon: 'User',
    children: [
      {
        index: '5-1',
        title: '信息管理',
        icon: 'Edit'
      },
      {
        index: '5-2',
        title: '账户管理',
        icon: 'Lock'
      }
    ]
  }
]

const waitingList = ref([])
const doctorInfo = ref(null)
const drugOptions = ref([])

// 加载药品选项
const loadDrugOptions = async () => {
  try {
    const res = await getDrugInventory()
    const drugs = res.data || []
    // 将数据库的药品数据转换为组件需要的格式
    drugOptions.value = drugs
      .filter(drug => drug.isActive)
      .map(drug => ({
        id: drug.id,
        name: drug.name,
        type: drug.type,
        price: drug.price,
        stock: drug.stock,
        unit: drug.unit || '盒'
      }))
    console.log('药品选项已加载:', drugOptions.value)
  } catch (error) {
    console.error('加载药品选项失败:', error)
    // 加载失败时使用空数组
    drugOptions.value = []
  }
}

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
  // 子组件已经显示了成功消息，这里不需要重复显示
}

const handleFinishDiagnose = async (patientId) => {
  try {
    // 从后端重新加载候诊列表
    if (doctorInfo.value?.department) {
      await loadWaitingList(doctorInfo.value.department)
    }
    ElMessage.success('已完成诊疗')
  } catch (error) {
    console.error('重新加载候诊列表失败:', error)
    // 如果失败，从本地列表中移除
    waitingList.value = waitingList.value.filter(p => p.id !== patientId)
  }
}

const handleRefreshList = () => {
  if (doctorInfo.value?.department) {
    loadWaitingList(doctorInfo.value.department)
  }
}

// 页面加载时的初始化逻辑
onMounted(() => {
  // 可以在这里添加初始化逻辑
  fetchUserInfo()
  loadDrugOptions()
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
    console.log('用户ID:', userId)
    if (userId) {
      const response = await getDoctorInfo(userId)
      console.log('医生信息:', response.data)
      userName.value = response.data.name || '医生'
      let avatarUrl = response.data.avatar || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
      if (avatarUrl && !avatarUrl.startsWith('http') && !avatarUrl.startsWith('https')) {
        avatarUrl = 'http://localhost:8080' + avatarUrl
      }
      userAvatar.value = avatarUrl
      doctorInfo.value = response.data
      localStorage.setItem('doctorInfo', JSON.stringify(response.data))
      
      // 加载候诊列表
      if (response.data.department) {
        await loadWaitingList(response.data.department)
      }
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

const loadWaitingList = async (department) => {
  try {
    const res = await getAllAppointmentsByDepartment(department)
    const appointments = res.data || []
    const doctorIdStr = localStorage.getItem('userId')
    const doctorId = doctorIdStr ? parseInt(doctorIdStr) : null
    
    console.log('科室:', department, '医生ID:', doctorId, '所有预约:', appointments)
    
    // 过滤出本科室且属于当前医生的预约，并且只显示待取号、待就诊和正在就诊的预约
    const filteredAppointments = appointments.filter(app => 
      app.department === department && 
      app.doctorId !== null && app.doctorId === doctorId &&  // 只显示当前医生的预约
      (app.status === 'pending' || app.status === 'waiting' || app.status === 'in_progress')
    )
    
    console.log('过滤后的预约:', filteredAppointments)
    
    waitingList.value = filteredAppointments
      .map(app => ({
        id: app.id,
        petId: app.petId,
        no: `A${String(app.id).padStart(3, '0')}`,
        name: app.petName || `宠物 ${app.petId}`,
        breed: app.petBreed || '',
        age: 0,
        weight: 0,
        owner: app.userName || `用户 ${app.userId}`,
        ownerId: app.userId,
        reason: app.reason || '',
        type: '普通',
        department: app.department || '',
        doctorId: app.doctorId,
        doctorName: app.doctorName,
        doctorTitle: app.doctorTitle,
        status: app.status
      }))
    console.log('候诊列表:', waitingList.value)
  } catch (error) {
    console.error('加载候诊列表失败:', error)
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
    <!-- 医生首页 -->
    <div v-if="activeMenu === '1'">
      <DoctorHome />
    </div>

    <!-- 接诊工作台 -->
    <div v-if="activeMenu === '2'">
      <Consultation
        :waiting-list="waitingList"
        :drug-options="drugOptions"
        @call-patient="handleCallPatient"
        @finish-diagnose="handleFinishDiagnose"
        @refresh-list="handleRefreshList"
        ref="consultationRef"
      />
    </div>

    <!-- 病历查询 -->
    <div v-if="activeMenu === '3'">
      <MedicalRecords />
    </div>

    <!-- 我的排班 -->
    <div v-if="activeMenu === '4'">
      <Schedule />
    </div>

    <!-- 5-1. 信息管理 -->
    <div v-if="activeMenu === '5-1'">
      <DoctorProfile />
    </div>

    <!-- 5-2. 账户管理 -->
    <div v-if="activeMenu === '5-2'">
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