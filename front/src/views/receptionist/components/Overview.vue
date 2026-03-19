<template>
  <div class="overview-wrapper">
    <el-row :gutter="20" style="margin-bottom: 20px;">
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <el-icon class="stat-icon" style="color: #E6A23C"><Timer /></el-icon>
            <div class="stat-info">
              <div class="stat-num">{{ paginatedQueueData.length }}</div>
              <div class="stat-label">当前候诊人数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <el-icon class="stat-icon" style="color: #67C23A"><Checked /></el-icon>
            <div class="stat-info">
              <div class="stat-num">{{ todayCompletedCount }}</div>
              <div class="stat-label">今日已接诊</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <el-icon class="stat-icon" style="color: #F56C6C"><Money /></el-icon>
            <div class="stat-info">
              <div class="stat-num">{{ pendingBillingCount }}</div>
              <div class="stat-label">待缴费单据</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <el-icon class="stat-icon"><UserFilled /></el-icon>
            <div class="stat-info">
              <div class="stat-num">{{ todayNewUsersCount }}</div>
              <div class="stat-label">今日新增用户</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="queue-card" header="当前候诊队列 (实时)">
      <el-table :data="paginatedQueueData" stripe style="width: 100%" v-loading="loading" empty-text="暂无候诊记录">
        <el-table-column prop="no" label="排队号" width="100" align="center" />
        <el-table-column prop="petName" label="宠物" width="120" align="center" />
        <el-table-column prop="owner" label="主人" align="center" />
        <el-table-column prop="doctor" label="挂号医生" align="center" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag v-if="scope.row.status === '待取号'" type="info">待取号</el-tag>
            <el-tag v-else-if="scope.row.status === '待就诊'" type="warning">待就诊</el-tag>
            <el-tag v-else-if="scope.row.status === '正在就诊'" type="primary">正在就诊</el-tag>
            <el-tag v-else-if="scope.row.status === '就诊完成'" type="success">就诊完成</el-tag>
            <el-tag v-else type="info">待就诊</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" align="center">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleAdjustOrder(scope.row, scope.$index)">调整顺序</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页组件 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[5, 10, 20, 50]"
          :small="false"
          :disabled="false"
          :background="true"
          layout="total, sizes, prev, pager, next, jumper"
          :total="filteredQueueData.length"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :pager-count="5"
          prev-text="上一页"
          next-text="下一页"
          :page-count="Math.ceil(filteredQueueData.length / pageSize)"
          :locale="{
            total: `共 {total} 条记录`,
            pagesize: '条/页',
            goto: '跳至',
            pageClassifier: '页'
          }"
        />
      </div>
    </el-card>

    <!-- 调整顺序对话框 -->
    <el-dialog v-model="adjustDialogVisible" title="调整候诊顺序" width="500px" destroy-on-close>
      <div v-if="currentAppointment">
        <el-alert type="info" :closable="false" style="margin-bottom: 20px;">
          当前选择：{{ currentAppointment.petName }} ({{ currentAppointment.owner }}) - 排队号 {{ currentAppointment.no }}
        </el-alert>
        <el-form label-width="100px">
          <el-form-item label="目标位置">
            <el-input-number v-model="targetPosition" :min="1" :max="filteredQueueData.length" />
            <span style="margin-left: 10px; color: #909399;">共 {{ filteredQueueData.length }} 人</span>
          </el-form-item>
        </el-form>
        <div style="margin-top: 20px;">
          <div style="font-weight: bold; margin-bottom: 10px;">调整后顺序预览：</div>
          <el-table :data="reorderedPreview" border size="small">
            <el-table-column prop="no" label="排队号" width="100" align="center" />
            <el-table-column prop="petName" label="宠物" />
            <el-table-column prop="owner" label="主人" />
            <el-table-column label="状态" width="80" align="center">
              <template #default="scope">
                <el-tag v-if="scope.row.status === '待就诊'" type="warning" size="small">待就诊</el-tag>
                <el-tag v-else-if="scope.row.status === '正在就诊'" type="primary" size="small">正在就诊</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
      <template #footer>
        <el-button @click="adjustDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmAdjustOrder">确认调整</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Timer,
  Checked,
  Money,
  UserFilled
} from '@element-plus/icons-vue'
import { getAllAppointments, getAllAppointmentsByDepartment, takeNumber, getReceptionistInfo, getTodayCompletedCount, getPendingBillingCount, getTodayNewUsersCount } from '@/services/api'

// 分页相关数据
const currentPage = ref(1)
const pageSize = ref(10)
const loading = ref(false)

// 从后端获取的预约数据
const appointmentList = ref([])

// 前台信息
const receptionistInfo = ref(null)

// 统计相关数据
const todayCompletedCount = ref(0)
const pendingBillingCount = ref(0)
const todayNewUsersCount = ref(0)

// 调整顺序相关
const adjustDialogVisible = ref(false)
const currentAppointment = ref(null)
const currentIndex = ref(0)
const targetPosition = ref(1)

// 加载前台信息
const loadReceptionistInfo = async () => {
  try {
    const userId = localStorage.getItem('userId')
    if (userId) {
      const res = await getReceptionistInfo(userId)
      receptionistInfo.value = res.data
      console.log('加载前台信息:', receptionistInfo.value)
    }
  } catch (error) {
    console.error('加载前台信息失败:', error)
  }
}

// 组件挂载时加载数据
onMounted(async () => {
  await loadReceptionistInfo()
  loadAppointments()
  loadStatistics()
})

// 加载统计数据
const loadStatistics = async () => {
  try {
    const [completedRes, billingRes, newUsersRes] = await Promise.all([
      getTodayCompletedCount(),
      getPendingBillingCount(),
      getTodayNewUsersCount()
    ])
    todayCompletedCount.value = completedRes.data?.data ?? completedRes.data ?? 0
    pendingBillingCount.value = billingRes.data?.data ?? billingRes.data ?? 0
    todayNewUsersCount.value = newUsersRes.data?.data ?? newUsersRes.data ?? 0
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

// 过滤待就诊的数据（从后端数据中过滤）
const filteredQueueData = computed(() => {
  // 使用后端数据
  if (appointmentList.value.length > 0) {
    console.log('原始预约数据状态:', appointmentList.value.map(app => ({id: app.id, status: app.status})))
    
    // 将后端数据转换为前端需要的格式
    const convertedData = appointmentList.value.map(app => {
      const statusText = app.status === 'waiting' ? '待就诊' : 
                        app.status === 'in_progress' ? '正在就诊' : 
                        app.status === 'pending' ? '待取号' : 
                        app.status === 'pending_payment' ? '待缴费' : 
                        app.status === 'completed' ? '就诊完成' : 
                        app.status === 'cancelled' ? '已取消' : '未知'
      
      console.log(`预约 ${app.id}: 后端状态=${app.status}, 前端显示=${statusText}`)
      
      return {
        no: `A${String(app.id).padStart(3, '0')}`,
        petName: app.petName || `宠物 ${app.petId}`,
        owner: app.userName || `用户 ${app.userId}`,
        doctor: app.doctorName || `医生 ${app.doctorId}`,
        status: statusText
      }
    })
    
    console.log('转换后的数据:', convertedData)
    return convertedData.filter(item => item.status === '待就诊' || item.status === '正在就诊')
  }
  return []
})

// 计算当前页的候诊数据（仅待就诊）
const paginatedQueueData = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredQueueData.value.slice(start, end)
})

// 调整后顺序预览
const reorderedPreview = computed(() => {
  if (!currentAppointment.value) return []
  const list = [...filteredQueueData.value]
  const currentItem = list.find(item => item.no === currentAppointment.value.no)
  if (!currentItem) return list

  const currentIdx = list.indexOf(currentItem)
  const targetIdx = targetPosition.value - 1

  list.splice(currentIdx, 1)
  list.splice(targetIdx, 0, currentItem)

  return list
})

// 打开调整顺序对话框
const handleAdjustOrder = (row, index) => {
  currentAppointment.value = row
  currentIndex.value = index + (currentPage.value - 1) * pageSize.value
  targetPosition.value = currentIndex.value + 1
  adjustDialogVisible.value = true
}

// 确认调整顺序
const confirmAdjustOrder = () => {
  ElMessage.success(`已将 ${currentAppointment.value.petName} 调整到第 ${targetPosition.value} 位`)
  adjustDialogVisible.value = false
}

// 分页事件处理
const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
}

const handleCurrentChange = (val) => {
  currentPage.value = val
}

// 加载本科室的预约数据
const loadAppointments = async () => {
  try {
    loading.value = true
    const department = receptionistInfo.value?.department
    console.log('当前科室:', department)

    let res
    if (department) {
      console.log('请求科室:', department)
      res = await getAllAppointmentsByDepartment(department)
    } else {
      console.log('没有科室信息，加载所有预约')
      res = await getAllAppointments()
    }
    
    const allAppointments = res.data || []
    console.log('API 返回:', res)
    console.log('所有预约数量:', allAppointments.length)
    
    // 过滤出本科室的预约，并且只显示待取号、待就诊和正在就诊的预约
    const filteredAppointments = allAppointments.filter(app => 
      app.department === department && 
      (app.status === 'pending' || app.status === 'waiting' || app.status === 'in_progress')
    )
    
    if (filteredAppointments.length > 0) {
      appointmentList.value = filteredAppointments
    } else if (!department && allAppointments.length > 0) {
      // 获取第一个预约的科室作为当前科室
      const firstDepartment = allAppointments[0].department
      if (firstDepartment) {
        console.log('从预约数据中提取科室:', firstDepartment)
        // 更新 receptionistInfo
        receptionistInfo.value = { department: firstDepartment }
        // 过滤出本科室的预约
        appointmentList.value = allAppointments.filter(app => 
          app.department === firstDepartment && 
          (app.status === 'pending' || app.status === 'waiting' || app.status === 'in_progress')
        )
      } else {
        appointmentList.value = []
      }
    } else {
      appointmentList.value = []
    }

    console.log('加载预约数据:', appointmentList.value)
    console.log('预约数量:', appointmentList.value.length)
  } catch (error) {
    console.error('加载预约数据失败:', error)
  } finally {
    loading.value = false
  }
}

// 监听自定义事件，接收取号数据
const handleCheckIn = async (data) => {
  // 生成排队号
  const queueNumber = `A${String(queueData.value.length + 1).padStart(3, '0')}`
  
  // 添加到候诊队列
  queueData.value.push({
    no: queueNumber,
    petName: data.pet,
    owner: data.name,
    doctor: data.doctor,
    status: data.status
  })
  
  // 从后端重新加载预约数据
  await loadAppointments()
}

// 暴露方法给父组件
defineExpose({
  handleCheckIn,
  loadAppointments
})
</script>

<style scoped>
.overview-wrapper {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.queue-card {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.queue-card :deep(.el-card__body) {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.queue-card :deep(.el-table) {
  flex: 1;
}

.pagination-container {
  display: flex;
  justify-content: center;
  padding: 20px 0;
}

.stat-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  cursor: pointer;
}

.stat-icon {
  font-size: 40px;
  color: #409EFF;
}

.stat-info {
  text-align: right;
}

.stat-num {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  color: #909399;
  font-size: 12px;
}
</style>