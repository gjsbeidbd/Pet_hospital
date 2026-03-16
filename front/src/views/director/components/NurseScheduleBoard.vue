<template>
  <div class="schedule-page">
    <el-card shadow="never" class="top-card">
      <div class="top-row">
        <div class="actions">
          <span style="font-weight: 600; color: #606266;">科室：护理部</span>

          <el-date-picker
            v-model="selectedDate"
            type="date"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            placeholder="选择日期"
            size="small"
            @change="updateScheduleData"
          />

          <el-button size="small" @click="exportSchedule">导出</el-button>
        </div>
      </div>

      <div class="meta-row">
        <span class="range">{{ formatDateRange() }}</span>
        <span class="hint">（点击格子可排班）</span>
      </div>
    </el-card>

    <el-card shadow="never" class="board-card">
      <div class="legend">
        <span class="legend-item"><i class="dot morning"></i>白班</span>
        <span class="legend-item"><i class="dot afternoon"></i>中班</span>
        <span class="legend-item"><i class="dot night"></i>夜班</span>
        <span class="legend-item"><i class="dot rest"></i>休息</span>
      </div>

      <div class="table-wrap">
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
    </el-card>

    <el-dialog v-model="cellDialogVisible" title="编辑排班" width="420px">
      <el-form label-width="90px">
        <el-form-item label="科室">
          <span>{{ selectedDepartment || '-' }}</span>
        </el-form-item>
        <el-form-item label="班次">
          <span>{{ editingShiftName || '-' }}</span>
        </el-form-item>
        <el-form-item label="值班护士">
          <el-select v-model="selectedCellStaffId" placeholder="请选择护士" style="width: 100%">
            <el-option label="休息" value="休息" />
            <el-option
              v-for="staff in nursesInSelectedDept"
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
import { getReceptionistSchedules, saveReceptionistSchedulesBatch } from '@/services/api'

const selectedDate = ref(new Date())
const selectedDepartment = ref('护理部') // 默认护理部
const weekdays = ref([])
const scheduleData = ref([])
const allNurses = ref([])

const cellDialogVisible = ref(false)
const editingRowRef = ref(null)
const editingDayKey = ref(null)
const editingShiftName = ref(null)
const selectedCellStaffId = ref('')

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
  allNurses.value.forEach(item => item.department && set.add(item.department))
  return Array.from(set)
})

const nursesInSelectedDept = computed(() => {
  // 护士固定为护理部，直接返回所有护士
  return allNurses.value.filter(item => item.department === '护理部')
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

const formatDateRange = () => {
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
  const target = allNurses.value.find(item => item.id === id)
  return target ? target.name : id
}

const fetchAllNurses = async () => {
  try {
    // 获取员工列表
    const response = await fetch('/api/staff')
    if (!response.ok) throw new Error('获取员工失败')
    const res = await response.json()
    if (!Array.isArray(res)) return
    // 前台即护士
    allNurses.value = res.filter(item => item.role === '前台')
    
    // 默认选择护理部
    selectedDepartment.value = '护理部'
  } catch (error) {
    console.error('获取护士列表失败:', error)
    ElMessage.error('获取护士列表失败')
  }
}

const updateScheduleData = async () => {
  weekdays.value = getWeekDates(selectedDate.value)
  const rows = createEmptyRows()

  if (!selectedDepartment.value) {
    scheduleData.value = rows
    return
  }

  try {
    const res = await getReceptionistSchedules({
      startDate: weekdays.value[0].fullDate,
      endDate: weekdays.value[6].fullDate,
      department: selectedDepartment.value
    })

    if (Array.isArray(res.data)) {
      res.data.forEach(item => {
        const dayKey = getDayKey(item.scheduleDate)
        const shiftName = normalizeShiftName(item.shiftType || '')
        const staffId = item.receptionistId || ''
        const row = rows.find(r => r.time === shiftName)
        if (row && DAY_KEYS.includes(dayKey)) row[dayKey] = staffId
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
  // 护士固定为护理部，不需要检查科室
  if (!nursesInSelectedDept.value.length) {
    ElMessage.warning('当前暂无护士')
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
    ElMessage.warning('请选择值班护士')
    return
  }

  editingRowRef.value[editingDayKey.value] = selectedCellStaffId.value
  
  // 保存到数据库
  try {
    const schedules = []
    scheduleData.value.forEach(row => {
      DAY_KEYS.forEach(dayKey => {
        if (row[dayKey] && row[dayKey] !== '休息') {
          const dayIndex = DAY_KEYS.indexOf(dayKey)
          const date = weekdays.value[dayIndex]?.fullDate
          if (date) {
            schedules.push({
              receptionistId: row[dayKey],
              department: selectedDepartment.value,
              scheduleDate: date,
              startTime: getShiftStartTime(row.time),
              endTime: getShiftEndTime(row.time),
              shiftType: row.time
            })
          }
        }
      })
    })
    
    if (schedules.length > 0) {
      await saveReceptionistSchedulesBatch(schedules)
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

const exportSchedule = () => {
  ElMessage.info('导出功能开发中...')
}

onMounted(async () => {
  await fetchAllNurses()
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

.actions {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
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
</style>
