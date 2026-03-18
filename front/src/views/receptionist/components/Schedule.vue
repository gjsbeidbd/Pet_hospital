<template>
  <div class="schedule-page">
    <el-card shadow="never" class="top-card">
      <template #header>
        <div class="header-content">
          <span class="page-title">我的排班</span>
          <div class="month-nav">
            <el-button @click="prevMonth" :icon="ArrowLeft" circle size="small" />
            <span class="current-month">{{ currentYear }}年{{ currentMonth }}月</span>
            <el-button @click="nextMonth" :icon="ArrowRight" circle size="small" />
          </div>
        </div>
      </template>

      <div class="calendar-container" v-loading="isLoading">
        <div class="calendar-header">
          <div class="weekday-cell">周日</div>
          <div class="weekday-cell">周一</div>
          <div class="weekday-cell">周二</div>
          <div class="weekday-cell">周三</div>
          <div class="weekday-cell">周四</div>
          <div class="weekday-cell">周五</div>
          <div class="weekday-cell">周六</div>
        </div>

        <div class="calendar-body">
          <div
            v-for="(day, index) in calendarDays"
            :key="index"
            class="day-cell"
            :class="{
              'other-month': !day.currentMonth,
              'today': day.isToday,
              'has-schedule': day.schedule,
              'weekend': day.isWeekend
            }"
          >
            <div class="day-number">{{ day.dateNum }}</div>
            <div class="schedule-info" v-if="day.currentMonth">
              <template v-if="day.schedule">
                <el-tag size="small" type="success" class="shift-tag">{{ day.schedule.shiftType }}</el-tag>
                <div class="time-info">{{ day.schedule.startTime?.substring(0, 5) }}-{{ day.schedule.endTime?.substring(0, 5) }}</div>
              </template>
              <template v-else>
                <el-tag size="small" type="info" class="rest-tag">休息</el-tag>
              </template>
            </div>
          </div>
        </div>
      </div>

      <div class="legend-row">
        <span class="legend-item"><i class="dot work"></i>上班</span>
        <span class="legend-item"><i class="dot rest"></i>休息</span>
        <span class="legend-item"><i class="dot today"></i>今天</span>
      </div>

      <div class="stats-row" v-if="scheduleList.length > 0">
        <el-statistic title="本月排班天数" :value="scheduleList.length" />
        <el-statistic title="休息天数" :value="restDays" />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { ArrowLeft, ArrowRight } from '@element-plus/icons-vue'
import { getMyNurseSchedules } from '@/services/api'

const currentYear = ref(2026)
const currentMonth = ref(1)
const scheduleList = ref([])
const scheduleMap = ref({})
const isLoading = ref(false)
const currentNurse = ref(null)

const daysInMonth = computed(() => {
  return new Date(currentYear.value, currentMonth.value, 0).getDate()
})

const restDays = computed(() => {
  return daysInMonth.value - scheduleList.value.length
})

const calendarDays = computed(() => {
  const days = []
  const firstDay = new Date(currentYear.value, currentMonth.value - 1, 1)
  const startDayOfWeek = firstDay.getDay()
  
  const today = new Date()
  const todayStr = `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, '0')}-${String(today.getDate()).padStart(2, '0')}`

  for (let i = 0; i < startDayOfWeek; i++) {
    const prevMonthDay = new Date(currentYear.value, currentMonth.value - 1, -startDayOfWeek + i + 1)
    days.push({
      dateNum: prevMonthDay.getDate(),
      currentMonth: false,
      isToday: false,
      isWeekend: prevMonthDay.getDay() === 0 || prevMonthDay.getDay() === 6,
      schedule: null
    })
  }

  for (let i = 1; i <= daysInMonth.value; i++) {
    const dateStr = `${currentYear.value}-${String(currentMonth.value).padStart(2, '0')}-${String(i).padStart(2, '0')}`
    const dayOfWeek = new Date(currentYear.value, currentMonth.value - 1, i).getDay()
    days.push({
      dateNum: i,
      date: dateStr,
      currentMonth: true,
      isToday: dateStr === todayStr,
      isWeekend: dayOfWeek === 0 || dayOfWeek === 6,
      schedule: scheduleMap.value[dateStr] || null
    })
  }

  const totalCells = Math.ceil(days.length / 7) * 7
  const remaining = totalCells - days.length
  for (let i = 1; i <= remaining; i++) {
    days.push({
      dateNum: i,
      currentMonth: false,
      isToday: false,
      isWeekend: false,
      schedule: null
    })
  }

  return days
})

const getCurrentNurseFromStorage = () => {
  const userId = localStorage.getItem('userId')
  const userRole = localStorage.getItem('userRole')
  if (userId && userRole === 'RECEPTIONIST') {
    currentNurse.value = {
      id: parseInt(userId)
    }
    return true
  }
  return false
}

const fetchMySchedule = async () => {
  if (!currentNurse.value || !currentNurse.value.id) {
    ElMessage.warning('请先登录')
    return
  }

  isLoading.value = true
  scheduleList.value = []
  scheduleMap.value = {}

  try {
    const startDate = `${currentYear.value}-${String(currentMonth.value).padStart(2, '0')}-01`
    const endDate = `${currentYear.value}-${String(currentMonth.value).padStart(2, '0')}-${String(daysInMonth.value).padStart(2, '0')}`

    const res = await getMyNurseSchedules(currentNurse.value.id, startDate, endDate)

    if (res.data && res.data.data && Array.isArray(res.data.data)) {
      scheduleList.value = res.data.data
      res.data.data.forEach(item => {
        scheduleMap.value[item.scheduleDate] = item
      })
    }
  } catch (error) {
    console.error('获取排班失败:', error)
    ElMessage.error('获取排班信息失败')
  } finally {
    isLoading.value = false
  }
}

const prevMonth = () => {
  if (currentMonth.value === 1) {
    currentMonth.value = 12
    currentYear.value--
  } else {
    currentMonth.value--
  }
  fetchMySchedule()
}

const nextMonth = () => {
  if (currentMonth.value === 12) {
    currentMonth.value = 1
    currentYear.value++
  } else {
    currentMonth.value++
  }
  fetchMySchedule()
}

onMounted(() => {
  const now = new Date()
  currentYear.value = now.getFullYear()
  currentMonth.value = now.getMonth() + 1
  getCurrentNurseFromStorage()
  fetchMySchedule()
})
</script>

<style scoped>
.schedule-page {
  padding: 20px;
}

.top-card {
  margin-bottom: 20px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.month-nav {
  display: flex;
  align-items: center;
  gap: 15px;
}

.current-month {
  font-size: 16px;
  font-weight: 500;
  color: #409EFF;
  min-width: 100px;
  text-align: center;
}

.calendar-container {
  margin-top: 20px;
  border: 1px solid #EBEEF5;
  border-radius: 8px;
  overflow: hidden;
}

.calendar-header {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  background: #f5f7fa;
  border-bottom: 1px solid #EBEEF5;
}

.weekday-cell {
  padding: 12px 8px;
  text-align: center;
  font-weight: 600;
  color: #606266;
  font-size: 14px;
}

.calendar-body {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
}

.day-cell {
  min-height: 80px;
  padding: 8px;
  border-right: 1px solid #EBEEF5;
  border-bottom: 1px solid #EBEEF5;
  background: #fff;
  transition: background 0.2s;
}

.day-cell:nth-child(7n) {
  border-right: none;
}

.day-cell:hover {
  background: #f5f7fa;
}

.day-cell.other-month {
  background: #fafafa;
}

.day-cell.other-month .day-number {
  color: #c0c4cc;
}

.day-cell.today {
  background: #ecf5ff;
}

.day-cell.today .day-number {
  background: #409EFF;
  color: #fff;
  border-radius: 50%;
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.day-cell.weekend {
  background: #fdf6ec;
}

.day-cell.has-schedule {
  background: #f0f9eb;
}

.day-number {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 5px;
}

.schedule-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.shift-tag {
  width: fit-content;
}

.rest-tag {
  width: fit-content;
}

.time-info {
  font-size: 12px;
  color: #909399;
}

.legend-row {
  display: flex;
  gap: 20px;
  margin-top: 20px;
  padding: 10px 0;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #606266;
}

.dot {
  width: 12px;
  height: 12px;
  border-radius: 3px;
}

.dot.work {
  background: #67c23a;
}

.dot.rest {
  background: #909399;
}

.dot.today {
  background: #409EFF;
}

.stats-row {
  display: flex;
  gap: 40px;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #EBEEF5;
}
</style>
