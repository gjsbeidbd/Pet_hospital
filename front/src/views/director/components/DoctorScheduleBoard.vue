<template>
  <div class="schedule-page">
    <el-card shadow="never" class="top-card">
      <div class="top-row">
        <div class="left-actions">
          <el-select
            v-model="selectedDepartment"
            placeholder="选择科室"
            size="default"
            style="width: 150px"
            @change="updateScheduleData"
            clearable
          >
            <el-option v-for="dept in departments" :key="dept" :label="dept" :value="dept" />
          </el-select>

          <!-- 视图切换 -->
          <el-radio-group v-model="viewType" size="default" @change="onViewTypeChange">
            <el-radio-button label="周视图" value="week" />
            <el-radio-button label="月视图" value="month" />
          </el-radio-group>

          <!-- 日期选择器 -->
          <el-date-picker
            v-if="viewType === 'week'"
            v-model="selectedDate"
            type="date"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            placeholder="选择日期"
            size="default"
            @change="updateScheduleData"
          />
          <el-date-picker
            v-else
            v-model="selectedMonth"
            type="month"
            format="YYYY-MM"
            value-format="YYYY-MM"
            placeholder="选择月份"
            size="default"
            @change="updateScheduleData"
          />
        </div>

        <div class="right-actions">
          <el-button size="default" @click="openApplyDialog">排班复用</el-button>
          <el-button size="default" @click="exportSchedule">导出</el-button>
        </div>
      </div>

      <div class="meta-row">
        <span class="range">{{ formatDateRange() }}</span>
        <span class="hint">当前科室：{{ selectedDepartment || '未选择' }}（点击格子可排班）</span>
      </div>
    </el-card>

    <el-card shadow="never" class="board-card">
      <div class="legend">
        <span class="legend-item"><i class="dot morning"></i>白班</span>
        <span class="legend-item"><i class="dot afternoon"></i>中班</span>
        <span class="legend-item"><i class="dot night"></i>夜班</span>
        <span class="legend-item"><i class="dot rest"></i>休息</span>
      </div>

      <!-- 周视图 -->
      <div v-if="viewType === 'week'" class="table-wrap">
        <div class="header-row">
          <div class="shift-cell">班次</div>
          <div v-for="day in weekdays" :key="day.key" class="day-cell">
            <div>{{ day.label }}</div>
            <small>{{ day.date }}</small>
          </div>
        </div>

        <div v-for="row in scheduleData" :key="row.time" class="data-row" :class="getShiftClass(row.time)">
          <div class="shift-cell">{{ row.time }}</div>
          <div
            v-for="day in weekdays"
            :key="day.key"
            class="edit-cell"
            @click="editScheduleCell(day.key, row)"
          >
            <template v-if="row[day.key] === '休息'">
              <el-tag size="small" type="info">休息</el-tag>
            </template>
            <template v-else-if="row[day.key]">
              {{ getStaffName(row[day.key]) }}
            </template>
            <template v-else>
              <span class="empty">+ 添加</span>
            </template>
          </div>
        </div>
      </div>

      <!-- 月视图 -->
      <div v-else class="month-table-wrap">
        <div class="month-header-row">
          <div class="shift-cell">班次</div>
          <div v-for="day in monthDays" :key="day.key" class="month-day-cell">
            <div class="day-number">{{ day.day }}</div>
            <div class="day-label">{{ day.label }}</div>
          </div>
        </div>

        <div v-for="row in scheduleData" :key="row.time" class="month-data-row" :class="getShiftClass(row.time)">
          <div class="shift-cell">{{ row.time }}</div>
          <div
            v-for="day in monthDays"
            :key="day.key"
            class="month-edit-cell"
            :class="{ 'is-today': day.isToday }"
            @click="editScheduleCell(day.key, row)"
          >
            <template v-if="row[day.key] === '休息'">
              <el-tag size="small" type="info">休息</el-tag>
            </template>
            <template v-else-if="row[day.key]">
              <span class="staff-name">{{ getStaffName(row[day.key]) }}</span>
            </template>
            <template v-else>
              <span class="empty">+</span>
            </template>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 排班复用对话框 -->
    <el-dialog v-model="applyDialogVisible" title="排班复用" width="500px">
      <el-form :model="applyForm" label-width="120px">
        <el-alert
          title="说明"
          type="info"
          :closable="false"
          style="margin-bottom: 16px;"
        >
          将当前周的排班计划应用到指定周期范围
        </el-alert>
        
        <el-form-item label="应用周期">
          <el-radio-group v-model="applyForm.periodType">
            <el-radio label="month">按月</el-radio>
            <el-radio label="quarter">按季度</el-radio>
            <el-radio label="custom">自定义</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="应用时长" v-if="applyForm.periodType !== 'custom'">
          <el-input-number v-model="applyForm.duration" :min="1" :max="12" />
          <span style="margin-left: 8px;">{{ applyForm.periodType === 'month' ? '个月' : '个季度' }}</span>
        </el-form-item>
        
        <el-form-item label="起始日期" v-if="applyForm.periodType === 'custom'">
          <el-date-picker
            v-model="applyForm.startDate"
            type="date"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            placeholder="选择开始日期"
            style="width: 100%;"
          />
        </el-form-item>
        
        <el-form-item label="结束日期" v-if="applyForm.periodType === 'custom'">
          <el-date-picker
            v-model="applyForm.endDate"
            type="date"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            placeholder="选择结束日期"
            style="width: 100%;"
          />
        </el-form-item>
        
        <el-form-item label="排除日期">
          <el-select
            v-model="applyForm.excludeDates"
            multiple
            allow-create
            filterable
            placeholder="选择需要排除的日期（如节假日）"
            style="width: 100%;"
          >
            <el-option
              v-for="day in weekdays"
              :key="day.fullDate"
              :label="`${day.label} (${day.date})`"
              :value="day.fullDate"
            />
          </el-select>
          <div style="font-size: 12px; color: #909399; margin-top: 4px;">
            可输入需要排除的特殊日期，这些日期不会应用排班
          </div>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="applyDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmApplySchedule" :loading="applyLoading">
          确定应用
        </el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="cellDialogVisible" title="编辑排班" width="420px">
      <el-form label-width="90px">
        <el-form-item label="科室">
          <span>{{ selectedDepartment || '-' }}</span>
        </el-form-item>
        <el-form-item label="班次">
          <span>{{ editingShiftName || '-' }}</span>
        </el-form-item>
        <el-form-item label="值班医生">
          <el-select v-model="selectedCellStaffId" placeholder="请选择医生" style="width: 100%">
            <el-option label="休息" value="休息" />
            <el-option
              v-for="staff in doctorsInSelectedDept"
              :key="staff.id"
              :label="`${staff.name}（${staff.department}）`"
              :value="staff.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="cellDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmCellSchedule">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getDoctorSchedules, saveDoctorSchedulesBatch, getAllStaff } from '@/services/api'

const selectedDate = ref(new Date())
const selectedDepartment = ref('')
const viewType = ref('week') // 'week' 或 'month'
const selectedMonth = ref(new Date().toISOString().slice(0, 7)) // YYYY-MM
const weekdays = ref([])
const monthDays = ref([])
const scheduleData = ref([])
const allDoctors = ref([])

const cellDialogVisible = ref(false)
const applyDialogVisible = ref(false)
const applyLoading = ref(false)
const editingRowRef = ref(null)
const editingDayKey = ref(null)
const editingShiftName = ref(null)
const selectedCellStaffId = ref('')

const applyForm = reactive({
  periodType: 'month', // month, quarter, custom
  duration: 1,
  startDate: '',
  endDate: '',
  excludeDates: []
})

const DAY_KEYS = ['monday', 'tuesday', 'wednesday', 'thursday', 'friday', 'saturday', 'sunday']
const DAY_LABELS = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
const SHIFT_NAMES = ['白班', '中班', '夜班']

const createEmptyRows = () => SHIFT_NAMES.map(name => ({
  time: name,
  monday: '',
  tuesday: '',
  wednesday: '',
  thursday: '',
  friday: '',
  saturday: '',
  sunday: ''
}))

const departments = computed(() => {
  const set = new Set()
  allDoctors.value.forEach(item => item.department && set.add(item.department))
  return Array.from(set)
})

const doctorsInSelectedDept = computed(() => {
  if (!selectedDepartment.value) return []
  return allDoctors.value.filter(item => item.department === selectedDepartment.value)
})

const getWeekDates = (dateValue) => {
  const date = new Date(dateValue)
  const weekday = date.getDay() === 0 ? 7 : date.getDay()
  const monday = new Date(date)
  monday.setDate(date.getDate() - weekday + 1)

  return DAY_KEYS.map((key, idx) => {
    const d = new Date(monday)
    d.setDate(monday.getDate() + idx)
    const m = d.getMonth() + 1
    const day = d.getDate()
    return {
      key,
      label: DAY_LABELS[idx],
      date: `${m}/${day}`,
      fullDate: d.toISOString().split('T')[0]
    }
  })
}

const getMonthDays = (monthStr) => {
  if (!monthStr) return []
  const [year, month] = monthStr.split('-').map(Number)
  const firstDay = new Date(year, month - 1, 1)
  const lastDay = new Date(year, month, 0)
  const daysInMonth = lastDay.getDate()
  const startWeekday = firstDay.getDay() === 0 ? 7 : firstDay.getDay()
  
  const days = []
  const today = new Date().toISOString().split('T')[0]
  
  // 添加上月补齐的天数
  for (let i = 1; i < startWeekday; i++) {
    days.push({ key: `prev-${i}`, day: '', label: '' })
  }
  
  // 添加当月的天数
  for (let day = 1; day <= daysInMonth; day++) {
    const date = new Date(year, month - 1, day)
    const weekday = date.getDay() === 0 ? 7 : date.getDay()
    const dateStr = date.toISOString().split('T')[0]
    days.push({
      key: dateStr,
      day: day.toString(),
      label: DAY_LABELS[weekday - 1],
      fullDate: dateStr,
      isToday: dateStr === today
    })
  }
  
  return days
}

const formatDateRange = () => {
  if (viewType.value === 'month') {
    if (!selectedMonth.value) return ''
    const [year, month] = selectedMonth.value.split('-')
    return `${year}年${parseInt(month)}月`
  }
  if (!weekdays.value.length) return ''
  return `${weekdays.value[0].date} 至 ${weekdays.value[6].date}`
}

const getShiftClass = (shiftName) => {
  if (shiftName.includes('白班')) return 'shift-morning'
  if (shiftName.includes('中班')) return 'shift-afternoon'
  if (shiftName.includes('夜班')) return 'shift-night'
  return ''
}

const getStaffName = (id) => {
  if (!id || id === '休息') return '休息'
  const target = allDoctors.value.find(item => item.id === id)
  return target ? target.name : id
}

const fetchAllDoctors = async () => {
  try {
    console.log('开始获取医生列表...')
    // 获取员工列表
    const staffRes = await getAllStaff()
    console.log('获取到的员工数据:', staffRes.data)
    
    if (!Array.isArray(staffRes.data)) {
      console.error('员工数据不是数组:', staffRes.data)
      return
    }
    
    // 过滤出医生
    allDoctors.value = staffRes.data.filter(item => item.role === '医生')
    console.log('过滤后的医生列表:', allDoctors.value)
    
    // 自动选择第一个科室（从医生数据中获取）
    if (allDoctors.value.length > 0 && !selectedDepartment.value) {
      const availableDepts = departments.value
      if (availableDepts.length > 0) {
        selectedDepartment.value = availableDepts[0]
        console.log('自动选择科室:', availableDepts[0])
      }
    }
  } catch (error) {
    console.error('获取医生列表失败详情:', error)
    ElMessage.error('获取医生列表失败：' + (error.message || '未知错误'))
  }
}

const updateScheduleData = async () => {
  if (viewType.value === 'week') {
    weekdays.value = getWeekDates(selectedDate.value)
  } else {
    monthDays.value = getMonthDays(selectedMonth.value)
  }
  
  const rows = createEmptyRows()

  if (!selectedDepartment.value) {
    scheduleData.value = rows
    return
  }

  try {
    let startDate, endDate
    if (viewType.value === 'week') {
      startDate = weekdays.value[0].fullDate
      endDate = weekdays.value[6].fullDate
    } else {
      const [year, month] = selectedMonth.value.split('-').map(Number)
      const firstDay = new Date(year, month - 1, 1)
      const lastDay = new Date(year, month, 0)
      startDate = firstDay.toISOString().split('T')[0]
      endDate = lastDay.toISOString().split('T')[0]
    }
    
    const res = await getDoctorSchedules({
      startDate,
      endDate,
      department: selectedDepartment.value
    })

    if (Array.isArray(res.data)) {
      res.data.forEach(item => {
        const dayKey = getDayKey(item.scheduleDate)
        const shiftName = normalizeShiftName(item.shiftType || '')
        const staffId = item.doctorId || ''
        const row = rows.find(r => r.time === shiftName)
        if (row && (DAY_KEYS.includes(dayKey) || dayKey.startsWith(selectedMonth.value))) {
          row[dayKey] = staffId
        }
      })
    }
  } catch (error) {
    console.error('获取排班失败:', error)
    ElMessage.error('获取排班失败')
  }

  scheduleData.value = rows
}

const getDayKey = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const weekday = date.getDay() === 0 ? 7 : date.getDay()
  return DAY_KEYS[weekday - 1]
}

const normalizeShiftName = (shift) => {
  if (!shift) return ''
  if (shift.includes('白') || shift.includes('早')) return '白班'
  if (shift.includes('中')) return '中班'
  if (shift.includes('夜')) return '夜班'
  return shift
}

const editScheduleCell = (dayKey, row) => {
  if (!selectedDepartment.value) {
    ElMessage.warning('请先选择科室')
    return
  }

  if (!doctorsInSelectedDept.value.length) {
    ElMessage.warning('当前科室暂无医生')
    return
  }

  editingRowRef.value = row
  editingDayKey.value = dayKey
  editingShiftName.value = row.time
  selectedCellStaffId.value = row[dayKey] || ''
  cellDialogVisible.value = true
}

const confirmCellSchedule = async () => {
  if (!editingRowRef.value || !editingDayKey.value) return
  if (!selectedCellStaffId.value) {
    ElMessage.warning('请选择值班医生')
    return
  }

  editingRowRef.value[editingDayKey.value] = selectedCellStaffId.value
  
  // 保存到数据库
  try {
    const schedules = []
    const currentDays = viewType.value === 'week' ? weekdays.value : monthDays.value
    
    scheduleData.value.forEach(row => {
      DAY_KEYS.forEach(dayKey => {
        if (row[dayKey] && row[dayKey] !== '休息') {
          const dayData = currentDays.find(d => d.key === dayKey)
          if (dayData && dayData.fullDate) {
            schedules.push({
              doctorId: row[dayKey],
              department: selectedDepartment.value,
              scheduleDate: dayData.fullDate,
              startTime: getShiftStartTime(row.time),
              endTime: getShiftEndTime(row.time),
              shiftType: row.time
            })
          }
        }
      })
    })
    
    if (schedules.length > 0) {
      await saveDoctorSchedulesBatch(schedules)
    }
  } catch (error) {
    console.error('保存排班失败:', error)
  }
  
  cellDialogVisible.value = false
  ElMessage.success('已更新')
}

const getShiftStartTime = (shiftName) => {
  if (shiftName === '白班') return '08:00:00'
  if (shiftName === '中班') return '16:00:00'
  if (shiftName === '夜班') return '00:00:00'
  return '09:00:00'
}

const getShiftEndTime = (shiftName) => {
  if (shiftName === '白班') return '16:00:00'
  if (shiftName === '中班') return '00:00:00'
  if (shiftName === '夜班') return '08:00:00'
  return '17:00:00'
}

const openApplyDialog = () => {
  if (!selectedDepartment.value) {
    ElMessage.warning('请先选择科室')
    return
  }
  
  // 检查是否有排班数据
  const hasData = scheduleData.value.some(row => 
    DAY_KEYS.some(key => row[key] && row[key] !== '休息')
  )
  
  if (!hasData) {
    ElMessage.warning('当前周暂无排班数据，无法复用')
    return
  }
  
  // 重置表单
  applyForm.periodType = 'month'
  applyForm.duration = 1
  applyForm.startDate = ''
  applyForm.endDate = ''
  applyForm.excludeDates = []
  
  applyDialogVisible.value = true
}

const confirmApplySchedule = async () => {
  if (applyForm.periodType === 'custom') {
    if (!applyForm.startDate || !applyForm.endDate) {
      ElMessage.warning('请选择起始和结束日期')
      return
    }
    if (new Date(applyForm.startDate) > new Date(applyForm.endDate)) {
      ElMessage.warning('结束日期不能早于起始日期')
      return
    }
  }
  
  try {
    applyLoading.value = true
    
    // 计算应用范围
    let startDate, endDate
    const today = new Date()
    
    if (applyForm.periodType === 'month') {
      startDate = today.toISOString().split('T')[0]
      const end = new Date(today)
      end.setMonth(end.getMonth() + applyForm.duration)
      endDate = end.toISOString().split('T')[0]
    } else if (applyForm.periodType === 'quarter') {
      startDate = today.toISOString().split('T')[0]
      const end = new Date(today)
      end.setMonth(end.getMonth() + applyForm.duration * 3)
      endDate = end.toISOString().split('T')[0]
    } else {
      startDate = applyForm.startDate
      endDate = applyForm.endDate
    }
    
    // 生成所有需要排班的日期
    const allDates = []
    const current = new Date(startDate)
    const end = new Date(endDate)
    
    while (current <= end) {
      const dateStr = current.toISOString().split('T')[0]
      // 检查是否在排除日期中
      if (!applyForm.excludeDates.includes(dateStr)) {
        allDates.push(dateStr)
      }
      current.setDate(current.getDate() + 1)
    }
    
    // 根据当前周的排班生成新排班
    const schedules = []
    const weekStart = new Date(weekdays.value[0].fullDate)
    
    allDates.forEach(targetDate => {
      const targetWeekday = new Date(targetDate).getDay() || 7 // 1-7
      const sourceWeekdayIndex = targetWeekday - 1 // 0-6
      
      // 获取对应星期的排班数据
      scheduleData.value.forEach(row => {
        const dayKey = DAY_KEYS[sourceWeekdayIndex]
        const staffId = row[dayKey]
        
        if (staffId && staffId !== '休息') {
          schedules.push({
            doctorId: staffId,
            department: selectedDepartment.value,
            scheduleDate: targetDate,
            startTime: getShiftStartTime(row.time),
            endTime: getShiftEndTime(row.time),
            shiftType: row.time
          })
        }
      })
    })
    
    if (schedules.length === 0) {
      ElMessage.warning('没有可应用的排班数据')
      return
    }
    
    await saveDoctorSchedulesBatch(schedules)
    
    applyLoading.value = false
    applyDialogVisible.value = false
    
    ElMessage.success(`成功应用 ${schedules.length} 条排班记录`)
    
    // 刷新数据
    await updateScheduleData()
    
  } catch (error) {
    console.error('应用排班失败:', error)
    applyLoading.value = false
    ElMessage.error('应用排班失败：' + error.message)
  }
}

const exportSchedule = () => {
  ElMessage.info('导出功能开发中...')
}

const onViewTypeChange = () => {
  updateScheduleData()
}

onMounted(async () => {
  await fetchAllDoctors()
  await updateScheduleData()
})
</script>

<style scoped>
.schedule-page {
  height: 100%;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.top-card {
  margin-bottom: 0;
}

.top-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  flex-wrap: wrap;
}

.left-actions {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
  flex: 1;
}

.right-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.meta-row {
  margin-top: 8px;
  display: flex;
  justify-content: space-between;
  color: #909399;
  font-size: 12px;
}

.range {
  color: #303133;
  font-weight: 600;
}

.board-card {
  flex: 1;
  min-height: 0;
  overflow: hidden;
}

.legend {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  margin-bottom: 12px;
  font-size: 12px;
  color: #606266;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.dot {
  display: inline-block;
  width: 10px;
  height: 10px;
  border-radius: 50%;
}

.dot.morning { background: #409EFF; }
.dot.afternoon { background: #67C23A; }
.dot.night { background: #E6A23C; }
.dot.rest { background: #909399; }

.table-wrap {
  border: 1px solid #EBEEF5;
  border-radius: 4px;
  overflow: hidden;
}

.header-row {
  display: grid;
  grid-template-columns: 80px repeat(7, 1fr);
  background: #F5F7FA;
  border-bottom: 1px solid #EBEEF5;
}

.day-cell {
  padding: 8px;
  text-align: center;
  border-left: 1px solid #EBEEF5;
  font-size: 13px;
}

.day-cell small {
  display: block;
  color: #909399;
  font-size: 12px;
  margin-top: 2px;
}

.shift-cell {
  padding: 12px 8px;
  text-align: center;
  font-weight: 600;
  background: #FAFAFA;
  border-right: 1px solid #EBEEF5;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
}

.data-row {
  display: grid;
  grid-template-columns: 80px repeat(7, 1fr);
  border-bottom: 1px solid #EBEEF5;
}

.data-row:last-child {
  border-bottom: none;
}

.edit-cell {
  padding: 12px 8px;
  text-align: center;
  border-left: 1px solid #EBEEF5;
  cursor: pointer;
  transition: background 0.2s;
  font-size: 13px;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 40px;
}

.edit-cell:hover {
  background: #F5F7FA;
}

.empty {
  color: #C0C4CC;
  font-size: 12px;
}

.shift-morning .edit-cell { background: #ecf5ff; }
.shift-afternoon .edit-cell { background: #f0f9eb; }
.shift-night .edit-cell { background: #fdf6ec; }

/* 月视图样式 */
.month-table-wrap {
  border: 1px solid #EBEEF5;
  border-radius: 4px;
  overflow: hidden;
}

.month-header-row {
  display: grid;
  grid-template-columns: 80px repeat(31, 1fr);
  background: #F5F7FA;
  border-bottom: 1px solid #EBEEF5;
}

.month-day-cell {
  padding: 6px 4px;
  text-align: center;
  border-left: 1px solid #EBEEF5;
  font-size: 12px;
  min-width: 32px;
}

.month-day-cell .day-number {
  font-weight: 600;
  color: #303133;
  margin-bottom: 2px;
}

.month-day-cell .day-label {
  color: #909399;
  font-size: 11px;
}

.month-day-cell.is-today .day-number {
  color: #409EFF;
}

.month-data-row {
  display: grid;
  grid-template-columns: 80px repeat(31, 1fr);
  border-bottom: 1px solid #EBEEF5;
}

.month-data-row:last-child {
  border-bottom: none;
}

.month-edit-cell {
  padding: 4px 2px;
  text-align: center;
  border-left: 1px solid #EBEEF5;
  cursor: pointer;
  transition: background 0.2s;
  font-size: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 36px;
  overflow: hidden;
}

.month-edit-cell:hover {
  background: #F5F7FA;
}

.month-edit-cell .staff-name {
  font-size: 11px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.month-edit-cell.is-today {
  background: #ecf5ff;
}

.shift-morning .month-edit-cell { background: #ecf5ff; }
.shift-afternoon .month-edit-cell { background: #f0f9eb; }
.shift-night .month-edit-cell { background: #fdf6ec; }

/* 排班复用对话框样式 */
.apply-info {
  background: #f0f9eb;
  border: 1px solid #e1f3d8;
  padding: 12px;
  border-radius: 4px;
  margin-bottom: 16px;
}

.apply-info p {
  margin: 4px 0;
  font-size: 13px;
  color: #606266;
}
</style>
