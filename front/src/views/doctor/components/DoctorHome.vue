<template>
  <div class="doctor-home">
    <el-row :gutter="20">
      <el-col :span="24">
        <h2>欢迎回来，{{ doctorName }}医生</h2>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="stats-row">
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-content">
            <el-icon class="stat-icon" color="#409EFF"><User /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ todayPatientCount }}</div>
              <div class="stat-label">今日就诊</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-content">
            <el-icon class="stat-icon" color="#67C23A"><DocumentChecked /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ todayFinishedCount }}</div>
              <div class="stat-label">已完成</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-content">
            <el-icon class="stat-icon" color="#E6A23C"><Clock /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ todayWaitingCount }}</div>
              <div class="stat-label">待就诊</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="14">
        <el-card header="今日就诊患者">
          <div class="patient-list" :class="{ 'is-empty': todayPatients.length === 0 }">
            <el-table :data="todayPatients" style="width: 100%" max-height="400">
              <el-table-column prop="petName" label="宠物名" width="100"></el-table-column>
              <el-table-column prop="ownerName" label="主人" width="100"></el-table-column>
              <el-table-column prop="species" label="种类" width="80"></el-table-column>
              <el-table-column prop="visitTime" label="就诊时间" width="120"></el-table-column>
              <el-table-column prop="status" label="状态">
                <template #default="scope">
                  <el-tag v-if="scope.row.status === '已完成'" type="success">{{ scope.row.status }}</el-tag>
                  <el-tag v-else-if="scope.row.status === '待就诊'">{{ scope.row.status }}</el-tag>
                  <el-tag v-else type="warning">{{ scope.row.status }}</el-tag>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-card>
      </el-col>
      <el-col :span="10">
        <el-card header="院长公告">
          <div v-if="announcements.length === 0" class="no-data">暂无公告</div>
          <div v-else class="announcement-list">
            <div v-for="item in announcements" :key="item.id" class="announcement-item">
              <div class="announcement-title">{{ item.title }}</div>
              <div class="announcement-content">{{ item.content }}</div>
              <div class="announcement-meta">
                <span>{{ item.author }}</span>
                <span>{{ formatDate(item.publishDate) }}</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { User, DocumentChecked, Clock } from '@element-plus/icons-vue'
import { getAppointmentsByDoctorId, getAnnouncements } from '@/services/api'

const doctorName = ref('')
const todayPatientCount = ref(0)
const todayFinishedCount = ref(0)
const todayWaitingCount = ref(0)
const todayPatients = ref([])
const announcements = ref([])

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN')
}

const fetchTodayPatients = async () => {
  try {
    const doctorId = localStorage.getItem('userId')
    const doctorStr = localStorage.getItem('doctorInfo')
    const doctor = doctorStr ? JSON.parse(doctorStr) : {}
    doctorName.value = doctor.name || '医生'

    const res = await getAppointmentsByDoctorId(doctorId)
    const appointments = res.data || []

    todayPatientCount.value = appointments.length
    todayFinishedCount.value = appointments.filter(r => r.status === 'completed').length
    todayWaitingCount.value = appointments.filter(r => r.status === 'pending' || r.status === 'waiting').length

    todayPatients.value = appointments.map(app => ({
      petName: app.petName || '未知',
      ownerName: app.userName || '未知',
      species: app.petSpecies || '未知',
      visitTime: app.appointmentTime || '',
      status: getStatusText(app.status)
    }))
  } catch (error) {
    console.error('获取今日患者失败:', error)
  }
}

const getStatusText = (status) => {
  const statusMap = {
    'pending': '待就诊',
    'waiting': '待就诊',
    'completed': '已完成',
    'cancelled': '已取消'
  }
  return statusMap[status] || '待就诊'
}

const fetchAnnouncements = async () => {
  try {
    const res = await getAnnouncements('DOCTOR')
    announcements.value = (res.data || []).slice(0, 5)
  } catch (error) {
    console.error('获取公告失败:', error)
  }
}

onMounted(() => {
  fetchTodayPatients()
  fetchAnnouncements()
})
</script>

<style scoped>
.doctor-home {
  padding: 20px;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  text-align: center;
}

.stat-content {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 20px;
}

.stat-icon {
  font-size: 48px;
}

.stat-info {
  text-align: left;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.no-data {
  text-align: center;
  color: #909399;
  padding: 40px 0;
}

.announcement-list {
  max-height: 400px;
  overflow-y: auto;
}

.announcement-item {
  padding: 15px 0;
  border-bottom: 1px solid #ebeef5;
}

.announcement-item:last-child {
  border-bottom: none;
}

.announcement-title {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 8px;
}

.announcement-content {
  font-size: 14px;
  color: #606266;
  margin-bottom: 8px;
  line-height: 1.5;
}

.announcement-meta {
  font-size: 12px;
  color: #909399;
  display: flex;
  justify-content: space-between;
}

.patient-footer {
  font-size: 12px;
  color: #909399;
  display: flex;
  justify-content: space-between;
  padding: 15px 0;
  border-top: 1px solid #ebeef5;
  margin-top: 10px;
}

.patient-list {
  max-height: 400px;
  overflow-y: auto;
}

.patient-list.is-empty {
  min-height: 100px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.announcement-meta {
  font-size: 12px;
  color: #909399;
  display: flex;
  justify-content: space-between;
}
</style>