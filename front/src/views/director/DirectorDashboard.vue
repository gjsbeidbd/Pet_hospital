<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { getDirectorInfo } from '@/services/api';
import Layout from '@/components/Layout.vue';

// 导入子组件
import BusinessReport from './components/BusinessReport.vue';
import DirectorStaffManagement from './components/DirectorStaffManagement.vue';
import DrugInventory from './components/DrugInventory.vue';
import HospitalSettings from './components/HospitalSettings.vue';
import PersonalCenter from './components/PersonalCenter.vue';
import DoctorScheduleBoard from './components/DoctorScheduleBoard.vue';
import NurseScheduleBoard from './components/NurseScheduleBoard.vue';

// Import Element Plus icons
import {
  DataLine,
  UserFilled,
  Box,
  Setting,
  Calendar,
  User,
} from '@element-plus/icons-vue';

// 响应式状态
const activeMenu = ref('1');
const userName = ref(''); // 用户名
const userAvatar = ref(''); // 用户头像
const layoutRef = ref(null);

// Router
const router = useRouter();

// 菜单配置
const menuList = [
  {
    index: '1',
    title: '经营数据报表',
    icon: 'DataLine'
  },
  {
    index: '2',
    title: '员工资料管理',
    icon: 'UserFilled'
  },
  {
    index: '3',
    title: '员工排班管理',
    icon: 'Calendar',
    children: [
      {
        index: '3-1',
        title: '医生排班'
      },
      {
        index: '3-2',
        title: '护士排班'
      }
    ]
  },
  {
    index: '4',
    title: '药品库存管理',
    icon: 'Box'
  },
  {
    index: '5',
    title: '医院服务设置',
    icon: 'Setting'
  },
  {
    index: '7',
    title: '个人中心',
    icon: 'User',
    children: [
      {
        index: '7-1',
        title: '个人信息'
      },
      {
        index: '7-2',
        title: '账户安全'
      }
    ]
  }
];

// 方法
const handleSelect = (key) => {
  activeMenu.value = key;
  if (layoutRef.value) {
    layoutRef.value.setActiveMenu(key);
  }
};

// 处理 Layout 组件的 activeMenu 更新事件
const handleActiveMenuUpdate = (menuIndex) => {
  activeMenu.value = menuIndex;
};

// 退出登录
const handleLogout = () => {
  // 清除本地存储的用户信息
  localStorage.removeItem('token');
  localStorage.removeItem('userRole');
  localStorage.removeItem('userId');
  
  // 跳转到登录页
  router.push('/login');
};

// 获取院长信息
const fetchDirectorInfo = async () => {
  try {
    const directorId = localStorage.getItem('userId');
    if (directorId) {
      const response = await getDirectorInfo(directorId);
      userName.value = response.data.name || '院长';
      // 设置用户头像
      let avatarUrl = response.data.image || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png';
      if (avatarUrl && !avatarUrl.startsWith('http') && !avatarUrl.startsWith('https')) {
        avatarUrl = 'http://localhost:8080' + avatarUrl;
      }
      userAvatar.value = avatarUrl;
    } else {
      userName.value = '院长';
      userAvatar.value = 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png';
    }
  } catch (error) {
    console.error('获取院长信息失败:', error);
    userName.value = '院长';
    userAvatar.value = 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png';
  }
};

// 在组件挂载时获取院长信息
onMounted(() => {
  fetchDirectorInfo();
});
</script>

<template>
  <Layout
    role="director"
    roleText="院长管理"
    :menu-list="menuList"
    :username="userName"
    :user-avatar="userAvatar"
    :active-menu="activeMenu"
    ref="layoutRef"
    @update:active-menu="handleActiveMenuUpdate"
  >
    <!-- 经营数据报表 -->
    <div v-show="activeMenu === '1'">
      <BusinessReport />
    </div>

    <!-- 员工资料管理 -->
    <div v-if="activeMenu === '2'">
      <DirectorStaffManagement />
    </div>

    <!-- 医生排班 -->
    <div v-if="activeMenu === '3-1'">
      <DoctorScheduleBoard />
    </div>

    <!-- 护士排班 -->
    <div v-if="activeMenu === '3-2'">
      <NurseScheduleBoard />
    </div>

    <!-- 药品库存管理 -->
    <div v-if="activeMenu === '4'">
      <DrugInventory />
    </div>

    <!-- 医院服务设置 -->
    <div v-if="activeMenu === '5'">
      <HospitalSettings />
    </div>
    
    <!-- 个人中心 - 个人信息 -->
    <div v-if="activeMenu === '7-1'">
      <PersonalCenter activeType="profile" />
    </div>
    
    <!-- 个人中心 - 账户安全 -->
    <div v-if="activeMenu === '7-2'">
      <PersonalCenter activeType="security" />
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