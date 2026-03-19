<template>
  <Layout
    role="user"
    roleText="宠物主人"
    :menu-list="menuList"
    :username="userName"
    :user-avatar="userAvatar"
    :active-menu="activeMenu"
    ref="layoutRef"
    @update:active-menu="handleActiveMenuUpdate"
  >
    <!-- 1. 首页公告 -->
    <div v-if="activeMenu === '1'">
      <HomeAnnouncements />
    </div>

    <!-- 2. 我的爱宠 -->
    <div v-if="activeMenu === '2'">
      <MyPets />
    </div>

    <!-- 3. 预约挂号 -->
    <div v-if="activeMenu === '3'" class="full-height">
      <Appointment :pets="pets" />
    </div>

    <!-- 4. 病历记录 -->
    <div v-if="activeMenu === '4'" class="full-height">
      <MedicalRecords />
    </div>

    <!-- 5-1. 个人信息 -->
    <div v-if="activeMenu === '5-1'">
      <Profile />
    </div>

    <!-- 5-2. 修改密码 -->
    <div v-if="activeMenu === '5-2'">
      <ChangePassword />
    </div>
  </Layout>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { getUserInfo } from '@/services/api'
import Layout from '@/components/Layout.vue'

// 导入子组件
import HomeAnnouncements from './components/HomeAnnouncements.vue'
import MyPets from './components/MyPets.vue'
import Appointment from './components/Appointment.vue'
import MedicalRecords from './components/MedicalRecords.vue'
import Profile from './components/Profile.vue'
import ChangePassword from './components/ChangePassword.vue'
import { getUserPets } from '@/services/api'

// Router
const router = useRouter()
const layoutRef = ref(null)

// --- State ---
const activeMenu = ref('1')
const userName = ref('') // 用户名
const userAvatar = ref('') // 用户头像
const pets = ref([]) // 用户宠物列表

// 菜单配置
const menuList = [
  {
    index: '1',
    title: '首页',
    icon: 'House'
  },
  {
    index: '2',
    title: '我的宠物',
    icon: 'Sugar'
  },
  {
    index: '3',
    title: '预约挂号',
    icon: 'Calendar'
  },
  {
    index: '4',
    title: '病历记录',
    icon: 'Document'
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

// --- Methods ---
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



// 获取用户信息
const fetchUserInfo = async () => {
  try {
    const userId = localStorage.getItem('userId')
    if (userId) {
      const response = await getUserInfo(userId)
      userName.value = response.data.name || '用户'
      // 设置用户头像，如果没有则使用默认头像
      // 确保头像URL是有效的，如果是相对路径则加上基础URL
      let avatarUrl = response.data.image || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
      if (avatarUrl && !avatarUrl.startsWith('http') && !avatarUrl.startsWith('https')) {
        // 如果是相对路径，加上基础URL
        avatarUrl = 'http://localhost:8080' + avatarUrl
      }
      userAvatar.value = avatarUrl
      
      // 加载用户宠物列表
      const petsResponse = await getUserPets(userId)
      pets.value = petsResponse.data || []
    } else {
      userName.value = '用户'
      userAvatar.value = 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
      pets.value = []
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
    userName.value = '用户'
    userAvatar.value = 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
    pets.value = []
    // ElMessage.error('获取用户信息失败')
  }
}

// 监听用户头像更新事件
const handleUserAvatarUpdated = (event) => {
  // 确保头像URL是有效的，如果是相对路径则加上基础URL
  let avatarUrl = event.detail.avatar || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
  if (avatarUrl && !avatarUrl.startsWith('http') && !avatarUrl.startsWith('https')) {
    // 如果是相对路径，加上基础URL
    avatarUrl = 'http://localhost:8080' + avatarUrl
  }
  userAvatar.value = avatarUrl
}

onMounted(() => {
  fetchUserInfo()
  window.addEventListener('userAvatarUpdated', handleUserAvatarUpdated)
})

onUnmounted(() => {
  window.removeEventListener('userAvatarUpdated', handleUserAvatarUpdated)
})
</script>

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