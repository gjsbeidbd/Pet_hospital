<template>
  <div class="layout-container">
    <!-- 左侧菜单栏 -->
    <el-aside class="sidebar" :width="isCollapse ? '64px' : '220px'">
      <!-- 系统名称和 Logo -->
      <div class="logo-wrapper">
        <img v-if="!isCollapse" src="/logo.svg" alt="Logo" class="logo" />
        <span v-if="!isCollapse" class="system-name">宠物医院信息管理系统</span>
      </div>
      <el-menu
        :default-active="internalActiveMenu"
        :collapse="isCollapse"
        background-color="#333e50"
        text-color="#fff"
        active-text-color="#ffd700"
        class="sidebar-menu"
      >
        <template v-for="item in menuList" :key="item.index">
          <!-- 有子菜单的情况 -->
          <el-sub-menu v-if="item.children && item.children.length" :index="item.index">
            <template #title>
              <el-icon><component :is="getIconComponent(item.icon)" /></el-icon>
              <span>{{ item.title }}</span>
            </template>
            <el-menu-item
              v-for="child in item.children"
              :key="child.index"
              :index="child.index"
              @click="handleMenuClick(child)"
            >
              <el-icon><component :is="getIconComponent(child.icon)" /></el-icon>
              <span>{{ child.title }}</span>
            </el-menu-item>
          </el-sub-menu>
          <!-- 无子菜单的情况 -->
          <el-menu-item
            v-else
            :index="item.index"
            @click="handleMenuClick(item)"
          >
            <el-icon><component :is="getIconComponent(item.icon)" /></el-icon>
            <span>{{ item.title }}</span>
          </el-menu-item>
        </template>
      </el-menu>
      
      <div class="collapse-btn" @click="toggleCollapse">
        <el-icon :size="20">
          <Fold v-if="!isCollapse" />
          <Expand v-else />
        </el-icon>
      </div>
    </el-aside>

    <!-- 右侧内容区域 -->
    <div class="content-wrapper">
      <!-- 顶部导航 -->
      <el-header class="header">
        <div class="header-right">
          <span class="role-badge" :class="roleClass">{{ roleText }}</span>
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="36" :src="userAvatar" icon="User" />
              <span class="username">{{ username }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
    
      <!-- 内容区域 -->
      <el-main class="content">
        <slot></slot>
      </el-main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  House, ArrowDown, User, Fold, Expand,
  Sugar, Calendar, Document, Edit, Lock,
  Monitor, FirstAidKit, Money, DocumentCopy,
  DataLine, UserFilled, Box, Setting
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

interface MenuItem {
  index: string
  title: string
  icon: string
  path?: string
  children?: MenuItem[]
}

interface Props {
  role: string
  roleText: string
  menuList: MenuItem[]
  username?: string
  userAvatar?: string
  activeMenu?: string
}

const props = withDefaults(defineProps<Props>(), {
  username: '用户',
  userAvatar: '',
  activeMenu: '1'
})

const emit = defineEmits<{
  (e: 'update:activeMenu', value: string): void
}>()

const isCollapse = ref(false)

// 使用 computed 来同步 props 和内部的 activeMenu
const internalActiveMenu = computed({
  get: () => props.activeMenu,
  set: (value) => {
    emit('update:activeMenu', value)
  }
})

const roleClass = computed(() => props.role.toLowerCase())

const handleMenuClick = (item: MenuItem) => {
  // 直接通过 emit 更新父组件的 activeMenu
  emit('update:activeMenu', item.index)
  if (item.path) {
    router.push(item.path)
  }
}

const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

// 图标映射表
const iconMap: Record<string, any> = {
  House,
  Sugar,
  Calendar,
  Document,
  Edit,
  Lock,
  User,
  Monitor,
  FirstAidKit,
  Money,
  DocumentCopy,
  DataLine,
  UserFilled,
  Box,
  Setting
}

// 获取图标组件
const getIconComponent = (iconName: string) => {
  return iconMap[iconName] || User
}

const handleCommand = (command: string) => {
  if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      localStorage.removeItem('token')
      localStorage.removeItem('userRole')
      localStorage.removeItem('userId')
      router.push('/login')
      ElMessage.success('已退出登录')
    })
  }
}

// 暴露方法给父组件，用于更新 activeMenu
defineExpose({
  setActiveMenu: (menuIndex: string) => {
    activeMenu.value = menuIndex
  }
})
</script>

<style scoped>
.layout-container {
  height: 100vh;
  display: flex;
}

.sidebar {
  background: #333e50;
  transition: width 0.3s ease;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  position: relative;
  height: 100vh;
}

.content-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.header {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  background: #faf5fb;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.15);
  padding: 0 24px;
  height: 60px;
  border-radius: 0 0 16px 16px;
  flex-shrink: 0;
  position: relative;
}

.header::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
  position: relative;
  z-index: 1;
}

.role-badge {
  background: linear-gradient(120deg, #6b77d9, #8e99ec);
  padding: 6px 16px;
  border-radius: 20px;
  color: white;
  font-size: 14px;
  font-weight: 500;
  box-shadow: 0 2px 8px rgba(107, 119, 217, 0.3);
}

.role-badge.user {
  background: linear-gradient(120deg, #6b77d9, #8e99ec);
}

.role-badge.receptionist {
  background: linear-gradient(120deg, #6b77d9, #8e99ec);
}

.role-badge.doctor {
  background: linear-gradient(120deg, #6b77d9, #8e99ec);
}

.role-badge.director {
  background: linear-gradient(120deg, #6b77d9, #8e99ec);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #333e50;
  cursor: pointer;
  padding: 6px 12px;
  border-radius: 8px;
  transition: all 0.3s ease;
  background: rgba(107, 119, 217, 0.1);
}

.user-info:hover {
  background: rgba(107, 119, 217, 0.2);
}

.username {
  font-size: 14px;
  text-shadow: none;
}

.main-container {
  display: flex;
  flex: 1;
  overflow: hidden;
}

.sidebar::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 1;
}

.logo-wrapper {
  padding: 24px 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  background: transparent;
  position: relative;
  z-index: 2;
}

.logo {
  height: 50px;
  width: auto;
}

.system-name {
  font-size: 16px;
  font-weight: 600;
  color: white;
  text-align: center;
}

.sidebar-menu {
  flex: 1;
  border-right: none;
  background: transparent;
  overflow-y: auto;
  position: relative;
  z-index: 2;
}

.sidebar-menu :deep(.el-menu-item) {
  color: rgba(255, 255, 255, 0.65);
  margin: 6px 12px;
  border-radius: 10px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  font-size: 15px;
  font-weight: 500;
}

.sidebar-menu :deep(.el-menu-item:hover) {
  background: rgba(255, 255, 255, 0.1);
  color: white;
  transform: translateX(4px);
}

.sidebar-menu :deep(.el-menu-item.is-active) {
  background: rgba(255, 255, 255, 0.2);
  color: white;
  box-shadow: 0 2px 8px rgba(255, 255, 255, 0.1);
}

.sidebar-menu :deep(.el-sub-menu__title) {
  color: rgba(255, 255, 255, 0.65);
  margin: 6px 12px;
  border-radius: 10px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  font-size: 15px;
  font-weight: 500;
}

.sidebar-menu :deep(.el-sub-menu__title:hover) {
  background: rgba(255, 255, 255, 0.1);
  color: white;
}

.sidebar-menu :deep(.el-sub-menu .el-menu-item) {
  padding-left: 48px !important;
  margin-left: 0;
}

.collapse-btn {
  padding: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: rgba(255, 255, 255, 0.65);
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  background: transparent;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  z-index: 2;
}

.collapse-btn:hover {
  background: rgba(255, 255, 255, 0.1);
  color: white;
}

.content {
  flex: 1;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8ecf1 100%);
  padding: 24px;
  overflow-y: auto;
}

@media screen and (max-width: 768px) {
  .sidebar {
    position: absolute;
    left: 0;
    top: 0;
    bottom: 0;
    z-index: 1000;
  }
  
  .sidebar:not(.is-collapse) {
    box-shadow: 2px 0 8px rgba(0, 0, 0, 0.15);
  }
}
</style>
