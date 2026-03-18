<script setup>
import {
  ref,
  onMounted
} from 'vue'
import {
  ElMessage,
} from 'element-plus'
import { useRouter } from 'vue-router'
import { getDoctorInfo, getAllAppointmentsByDepartment } from '@/services/api'
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
    title: '排班表查看',
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

const waitingList = ref([])
const doctorInfo = ref(null)

const drugOptions = [
  // 抗生素类
  { id: 1, name: '阿莫西林胶囊', type: '抗生素', stock: 100, unit: '盒' },
  { id: 2, name: '头孢克肟片', type: '抗生素', stock: 80, unit: '盒' },
  { id: 3, name: '多西环素片', type: '抗生素', stock: 120, unit: '盒' },
  { id: 4, name: '克林霉素胶囊', type: '抗生素', stock: 60, unit: '盒' },
  { id: 5, name: '甲硝唑片', type: '抗生素', stock: 150, unit: '盒' },
  
  // 消炎药类
  { id: 6, name: '布洛芬片', type: '消炎药', stock: 200, unit: '盒' },
  { id: 7, name: '双氯芬酸钠片', type: '消炎药', stock: 150, unit: '盒' },
  { id: 8, name: '美洛昔康片', type: '消炎药', stock: 100, unit: '盒' },
  { id: 9, name: '泼尼松龙片', type: '消炎药', stock: 120, unit: '盒' },
  { id: 10, name: '地塞米松片', type: '消炎药', stock: 180, unit: '盒' },
  
  // 疫苗类
  { id: 11, name: '狂犬病疫苗', type: '疫苗', stock: 50, unit: '支' },
  { id: 12, name: '犬瘟热疫苗', type: '疫苗', stock: 60, unit: '支' },
  { id: 13, name: '猫三联疫苗', type: '疫苗', stock: 45, unit: '支' },
  { id: 14, name: '犬细小病毒疫苗', type: '疫苗', stock: 70, unit: '支' },
  { id: 15, name: '猫白血病疫苗', type: '疫苗', stock: 40, unit: '支' },
  
  // 驱虫药类
  { id: 16, name: '吡喹酮片', type: '驱虫药', stock: 90, unit: '盒' },
  { id: 17, name: '伊维菌素滴剂', type: '驱虫药', stock: 75, unit: '盒' },
  { id: 18, name: '芬苯达唑片', type: '驱虫药', stock: 110, unit: '盒' },
  { id: 19, name: '塞拉菌素滴剂', type: '驱虫药', stock: 65, unit: '盒' },
  { id: 20, name: '米尔贝肟片', type: '驱虫药', stock: 85, unit: '盒' },
  
  // 营养补充剂类
  { id: 21, name: '复合维生素片', type: '营养补充剂', stock: 120, unit: '瓶' },
  { id: 22, name: '钙片', type: '营养补充剂', stock: 150, unit: '瓶' },
  { id: 23, name: '鱼油软胶囊', type: '营养补充剂', stock: 80, unit: '瓶' },
  { id: 24, name: '益生菌粉', type: '营养补充剂', stock: 100, unit: '盒' },
  { id: 25, name: '关节保健片', type: '营养补充剂', stock: 60, unit: '瓶' },
  
  // 消化系统药类
  { id: 26, name: '胃复安片', type: '消化系统药', stock: 130, unit: '盒' },
  { id: 27, name: '多潘立酮片', type: '消化系统药', stock: 110, unit: '盒' },
  { id: 28, name: '蒙脱石散', type: '消化系统药', stock: 180, unit: '盒' },
  { id: 29, name: '乳果糖口服液', type: '消化系统药', stock: 95, unit: '瓶' },
  { id: 30, name: '西咪替丁片', type: '消化系统药', stock: 140, unit: '盒' },
  
  // 皮肤病药类
  { id: 31, name: '酮康唑乳膏', type: '皮肤病药', stock: 100, unit: '支' },
  { id: 32, name: '氯霉素软膏', type: '皮肤病药', stock: 150, unit: '支' },
  { id: 33, name: '红霉素软膏', type: '皮肤病药', stock: 160, unit: '支' },
  { id: 34, name: '硫磺软膏', type: '皮肤病药', stock: 200, unit: '支' },
  { id: 35, name: '特比萘芬乳膏', type: '皮肤病药', stock: 80, unit: '支' },
  
  // 眼药水类
  { id: 36, name: '氯霉素眼药水', type: '眼药水', stock: 120, unit: '支' },
  { id: 37, name: '妥布霉素眼药水', type: '眼药水', stock: 90, unit: '支' },
  { id: 38, name: '氧氟沙星眼药水', type: '眼药水', stock: 110, unit: '支' },
  { id: 39, name: '利福平眼药水', type: '眼药水', stock: 75, unit: '支' },
  { id: 40, name: '人工泪液', type: '眼药水', stock: 85, unit: '支' },
  
  // 耳药类
  { id: 41, name: '耳康滴耳液', type: '耳药', stock: 95, unit: '瓶' },
  { id: 42, name: '耳肤灵', type: '耳药', stock: 70, unit: '支' },
  { id: 43, name: '硼酸冰片滴耳液', type: '耳药', stock: 130, unit: '瓶' },
  { id: 44, name: '氧氟沙星滴耳液', type: '耳药', stock: 100, unit: '瓶' },
  { id: 45, name: '氯霉素滴耳液', type: '耳药', stock: 140, unit: '瓶' },
  
  // 麻醉药类
  { id: 46, name: '异氟烷', type: '麻醉药', stock: 20, unit: '瓶' },
  { id: 47, name: '丙泊酚注射液', type: '麻醉药', stock: 30, unit: '支' },
  { id: 48, name: '氯胺酮注射液', type: '麻醉药', stock: 25, unit: '支' },
  { id: 49, name: '地西泮注射液', type: '麻醉药', stock: 35, unit: '支' },
  { id: 50, name: '阿托品注射液', type: '麻醉药', stock: 40, unit: '支' }
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
      // 设置用户头像
      let avatarUrl = response.data.image || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
      if (avatarUrl && !avatarUrl.startsWith('http') && !avatarUrl.startsWith('https')) {
        avatarUrl = 'http://localhost:8080' + avatarUrl
      }
      userAvatar.value = avatarUrl
      doctorInfo.value = response.data
      
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
        no: `A${String(app.id).padStart(3, '0')}`,
        name: app.petName || `宠物 ${app.petId}`,
        breed: app.petBreed || '',
        age: 0,
        weight: 0,
        owner: app.userName || `用户 ${app.userId}`,
        reason: app.reason || '',
        type: app.emergencyLevel === 'emergency' ? '急诊' : '普通',
        department: app.department || '',
        doctorId: app.doctorId,
        doctorName: app.doctorName,
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
    <!-- 接诊工作台 -->
    <div v-if="activeMenu === '1'">
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