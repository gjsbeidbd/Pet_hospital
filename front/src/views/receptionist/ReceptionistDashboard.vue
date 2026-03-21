<template>
  <Layout
    role="receptionist"
    roleText="前台接待"
    :menu-list="menuList"
    :username="userName"
    :user-avatar="userAvatar"
    :active-menu="activeMenu"
    ref="layoutRef"
    @update:active-menu="handleActiveMenuUpdate"
  >
    <!-- 1. 工作台概览 -->
    <div v-if="activeMenu === '1'" class="full-height">
      <Overview ref="overviewRef" />
    </div>

    <!-- 2. 挂号与分诊 -->
    <div v-if="activeMenu === '2'" class="full-height">
      <Registration @registration-complete="handleRegistrationComplete" @check-in="handleCheckIn" />
    </div>

    <!-- 3. 收费结算中心 -->
    <div v-if="activeMenu === '3'" class="full-height">
      <Billing @pay-bill="handlePayBill" />
    </div>

    <!-- 4. 客户资料管理 -->
    <div v-if="activeMenu === '4'" class="full-height">
      <CustomerProfile @open-new-profile-dialog="handleOpenNewProfileDialog" />
    </div>

    <!-- 5. 排班表查看 -->
    <div v-if="activeMenu === '5'" class="full-height">
      <Schedule />
    </div>

    <!-- 6-1. 个人信息 -->
    <div v-if="activeMenu === '6-1'" class="full-height">
      <Profile />
    </div>

    <!-- 6-2. 修改密码 -->
    <div v-if="activeMenu === '6-2'" class="full-height">
      <ChangePassword />
    </div>
  </Layout>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { useRouter } from 'vue-router';
import { getReceptionistInfo } from '@/services/api';
import Layout from '@/components/Layout.vue';

// 导入子组件
import Overview from './components/Overview.vue'
import Registration from './components/Registration.vue'
import Billing from './components/Billing.vue'
import CustomerProfile from './components/CustomerProfile.vue'
import Schedule from './components/Schedule.vue'
import Profile from './components/Profile.vue'
import ChangePassword from './components/ChangePassword.vue'

const activeMenu = ref('1');
const overviewRef = ref(null);
const userName = ref(''); // 用户名
const userAvatar = ref(''); // 用户头像
const layoutRef = ref(null);

// Router
const router = useRouter();

// 菜单配置
const menuList = [
  {
    index: '1',
    title: '工作台概览',
    icon: 'Monitor'
  },
  {
    index: '2',
    title: '挂号与分诊',
    icon: 'FirstAidKit'
  },
  {
    index: '3',
    title: '收费结算中心',
    icon: 'Money'
  },
  {
    index: '4',
    title: '客户资料管理',
    icon: 'User'
  },
  {
    index: '5',
    title: '排班表查看',
    icon: 'Calendar'
  },
  {
    index: '6',
    title: '个人中心',
    icon: 'User',
    children: [
      {
        index: '6-1',
        title: '信息管理',
        icon: 'Edit'
      },
      {
        index: '6-2',
        title: '账户管理',
        icon: 'Lock'
      }
    ]
  }
];

// --- Methods ---
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

// 处理注册完成事件
const handleRegistrationComplete = (data) => {
  ElMessage.success(`注册成功！客户：${data.userName}，宠物：${data.petName}。已完成挂号，请等待叫号。`);
};

// 处理支付账单事件
const handlePayBill = (billId) => {
  ElMessage.success(`账单 ${billId} 支付成功！`);
};

// 处理打开新建档案对话框事件
const handleOpenNewProfileDialog = () => {
  // 可以在这里添加额外的逻辑
};

// 处理取号事件
const handleCheckIn = (data) => {
  // 将取号数据传递给工作台组件
  if (overviewRef.value) {
    overviewRef.value.handleCheckIn(data);
  }
  ElMessage.success('取号成功，已自动加入候诊队列');
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

// 获取用户信息
const fetchUserInfo = async () => {
  try {
    const userId = localStorage.getItem('userId');
    if (userId) {
      const response = await getReceptionistInfo(userId);
      userName.value = response.data.name || '前台人员';
      let avatarUrl = response.data.avatar || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png';
      if (avatarUrl && !avatarUrl.startsWith('http') && !avatarUrl.startsWith('https')) {
        avatarUrl = 'http://localhost:8080' + avatarUrl;
      }
      userAvatar.value = avatarUrl;

      if (response.data.department) {
        localStorage.setItem('receptionistInfo', JSON.stringify({
          department: response.data.department
        }));
      }
    } else {
      userName.value = '前台人员';
      userAvatar.value = 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png';
    }
  } catch (error) {
    console.error('获取用户信息失败:', error);
    userName.value = '前台人员';
    userAvatar.value = 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png';
  }
};

// 在组件挂载时获取用户信息
onMounted(() => {
  fetchUserInfo();
});;
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