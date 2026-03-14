import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import ReceptionistDashboard from '../views/receptionist/ReceptionistDashboard.vue'
import DoctorDashboard from '../views/doctor/DoctorDashboard.vue'
import DirectorDashboard from '../views/director/DirectorDashboard.vue'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  // 用户路由
  {
    path: '/user',
    name: 'UserDashboard',
    component: () => import('../views/user/UserDashboard.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/user/profile',
    name: 'UserProfile',
    component: () => import('../views/user/components/Profile.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/user/password',
    name: 'UserPassword',
    component: () => import('../views/user/components/ChangePassword.vue'),
    meta: { requiresAuth: true }
  },
  // 前台路由
  {
    path: '/receptionist',
    name: 'ReceptionistDashboard',
    component: ReceptionistDashboard,
    meta: { requiresAuth: true }
  },
  // 医生路由
  {
    path: '/doctor',
    name: 'DoctorDashboard',
    component: DoctorDashboard,
    meta: { requiresAuth: true }
  },
  // 院长路由
  {
    path: '/director',
    name: 'DirectorDashboard',
    component: DirectorDashboard,
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 全局路由守卫
router.beforeEach((to, from, next) => {
  // 不需要登录的页面
  const noAuthPages = ['/login']
  
  // 检查是否已登录
  const token = localStorage.getItem('token')
  
  // 如果访问受保护页面但未登录，重定向到登录页
  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else if (to.path === '/login' && token) {
    // 如果已经登录且访问登录页，根据角色重定向
    const userRole = localStorage.getItem('userRole')
    if (userRole) {
      switch(userRole) {
        case 'USER':
          next('/user')
          break
        case 'RECEPTIONIST':
          next('/receptionist')
          break
        case 'DOCTOR':
          next('/doctor')
          break
        case 'ADMIN':
          next('/director')
          break
        default:
          next('/login') // 默认返回登录页
          break
      }
    } else {
      next('/login') // 没有角色信息，返回登录页
    }
  } else if (to.path === '/' && token) {
    // 如果访问根路径且已登录，根据角色重定向
    const userRole = localStorage.getItem('userRole')
    if (userRole) {
      switch(userRole) {
        case 'USER':
          next('/user')
          break
        case 'RECEPTIONIST':
          next('/receptionist')
          break
        case 'DOCTOR':
          next('/doctor')
          break
        case 'ADMIN':
          next('/director')
          break
        default:
          next('/login')
          break
      }
    } else {
      next('/login')
    }
  } else {
    next()
  }
})

export default router