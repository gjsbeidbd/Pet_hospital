<template>
  <div class="schedule-page">
    <el-card shadow="never" class="top-card">
      <div class="top-row">
        <div class="actions">
          <el-date-picker
            v-model="selectedMonth"
            type="month"
            format="YYYY-MM"
            value-format="YYYY-MM"
            placeholder="选择月份"
            size="default"
            @change="onMonthChange"
          />

          <el-button type="primary" @click="autoSchedule" :loading="isAutoScheduling">
            <el-icon><MagicStick /></el-icon>
            一键排班（全科室）
          </el-button>
        </div>

        <div class="filter-area">
          <el-select 
            v-model="selectedDepartment" 
            placeholder="筛选科室" 
            style="width: 180px"
            clearable
            @change="onDepartmentChange"
          >
            <el-option
              v-for="dept in departments"
              :key="dept"
              :label="dept"
              :value="dept"
            />
          </el-select>
        </div>
      </div>

      <div class="meta-row">
        <span class="range">{{ formatMonthRange() }}</span>
        <span class="hint">（显示全部科室排班，可筛选查看）</span>
      </div>
    </el-card>

    <el-card shadow="never" class="board-card" v-loading="isLoading">
      <div class="legend">
        <span class="legend-item"><i class="dot morning"></i>白班 (08:00-17:00)</span>
        <span class="legend-item"><i class="dot rest"></i>休息</span>
      </div>

      <div class="schedule-list" v-if="filteredDepartments.length > 0">
        <div v-for="dept in filteredDepartments" :key="dept" class="dept-section">
          <div class="dept-header">
            <span class="dept-title">{{ dept }}</span>
            <span class="dept-nurses">护士：{{ getNursesByDept(dept).map(n => n.name).join('、') || '暂无' }}</span>
          </div>
          
          <div class="table-wrap">
            <div class="header-row">
              <div class="shift-cell">日期</div>
              <div v-for="day in monthDays" :key="day.date" class="day-cell" :class="{ 'weekend': day.isWeekend }">
                <div>{{ day.label }}</div>
                <small>{{ day.dateNum }}</small>
              </div>
            </div>

            <div v-for="shift in SHIFT_NAMES" :key="shift" class="data-row" :class="getShiftClass(shift)">
              <div class="shift-cell">姓名</div>
              <div
                v-for="day in monthDays"
                :key="day.date"
                class="edit-cell"
                :class="{ 'weekend': day.isWeekend }"
                @click="openEditDialog(dept, shift, day)"
              >
                <template v-if="getScheduleValue(dept, shift, day.date) === '休息'">
                  <el-tag size="small" type="info">休息</el-tag>
                </template>
                <template v-else-if="getScheduleValue(dept, shift, day.date)">
                  <span class="nurse-name">{{ getNurseName(getScheduleValue(dept, shift, day.date)) }}</span>
                </template>
                <template v-else>
                  <span class="empty">-</span>
                </template>
              </div>
            </div>
          </div>
        </div>
      </div>

      <el-empty v-else description="暂无排班数据" />
    </el-card>

    <el-dialog v-model="autoScheduleDialogVisible" title="一键排班（全科室）" width="600px">
      <el-alert
        title="将为所有科室自动生成当月排班"
        description="系统将按照轮班规则为每个科室的护士自动分配班次"
        type="info"
        :closable="false"
        style="margin-bottom: 20px"
      />
      <el-form label-width="100px">
        <el-form-item label="排班月份">
          <span>{{ selectedMonth }}</span>
        </el-form-item>
        <el-form-item label="科室数量">
          <span>{{ departments.length }} 个</span>
        </el-form-item>
        <el-form-item label="护士总数">
          <span>{{ allNurses.length }} 人</span>
        </el-form-item>
        <el-form-item label="排班天数">
          <span>{{ monthDays.length }} 天</span>
        </el-form-item>
      </el-form>
      <div class="preview-area">
        <div class="preview-title">科室预览：</div>
        <el-scrollbar max-height="200px">
          <div v-for="dept in departments" :key="dept" class="preview-dept">
            <div class="dept-name">{{ dept }}</div>
            <div class="dept-nurses">
              护士：{{ getNursesByDept(dept).map(n => n.name).join('、') || '暂无' }}
            </div>
          </div>
        </el-scrollbar>
      </div>
      <template #footer>
        <el-button @click="autoScheduleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmAutoSchedule" :loading="isAutoScheduling">确认排班</el-button>
      </template>
    </el-dialog>

    <!-- 编辑排班弹窗 -->
    <el-dialog v-model="editDialogVisible" title="修改排班" width="500px">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="科室">
          <span>{{ editForm.department }}</span>
        </el-form-item>
        <el-form-item label="日期">
          <span>{{ editForm.date }} ({{ editForm.dayLabel }})</span>
        </el-form-item>
        <el-form-item label="班次">
          <span>{{ editForm.shift }}</span>
        </el-form-item>
        <el-form-item label="护士">
          <el-select v-model="editForm.receptionistId" placeholder="选择护士或休息" clearable>
            <el-option label="休息" value="rest" />
            <el-option
              v-for="nurse in editForm.nurses"
              :key="nurse.id"
              :label="nurse.name"
              :value="nurse.id.toString()"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmEdit" :loading="isSaving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { MagicStick } from '@element-plus/icons-vue'
import { getAllNurses, getAllNurseSchedules, saveReceptionistSchedulesBatch, updateReceptionistSchedule } from '@/services/api'

const selectedMonth = ref('')
const selectedDepartment = ref('')
const monthDays = ref([])
const allNurses = ref([])
const scheduleMap = ref({})
const isLoading = ref(false)
const isAutoScheduling = ref(false)

const autoScheduleDialogVisible = ref(false)
const editDialogVisible = ref(false)
const isSaving = ref(false)

const editForm = ref({
  department: '',
  date: '',
  dayLabel: '',
  shift: '',
  receptionistId: '',
  nurses: []
})

const SHIFT_NAMES = ['白班']

const departments = computed(() => {
  const set = new Set()
  allNurses.value.forEach(item => item.department && set.add(item.department))
  return Array.from(set).sort()
})

const filteredDepartments = computed(() => {
  if (!selectedDepartment.value) {
    return departments.value
  }
  return departments.value.filter(d => d === selectedDepartment.value)
})

const getNursesByDept = (dept) => {
  return allNurses.value.filter(item => item.department === dept)
}

const getMonthDays = (monthStr) => {
  if (!monthStr) return []
  const [year, month] = monthStr.split('-').map(Number)
  const days = []
  const daysInMonth = new Date(year, month, 0).getDate()
  
  for (let i = 1; i <= daysInMonth; i++) {
    const date = new Date(year, month - 1, i)
    const dayOfWeek = date.getDay()
    const weekDays = ['日', '一', '二', '三', '四', '五', '六']
    days.push({
      date: `${year}-${String(month).padStart(2, '0')}-${String(i).padStart(2, '0')}`,
      dateNum: i,
      label: `周${weekDays[dayOfWeek]}`,
      isWeekend: dayOfWeek === 0 || dayOfWeek === 6
    })
  }
  return days
}

const CHINESE_MONTHS = ['一', '二', '三', '四', '五', '六', '七', '八', '九', '十', '十一', '十二']

const formatMonthRange = () => {
  if (!selectedMonth.value) return ''
  const month = parseInt(selectedMonth.value.split('-')[1])
  const year = selectedMonth.value.split('-')[0]
  return `${year}年${CHINESE_MONTHS[month - 1]}月排班`
}

const getShiftClass = (shiftName) => {
  if (shiftName.includes('白班')) return 'shift-morning'
  return ''
}

const getShiftStartTime = (shiftName) => {
  if (shiftName === '白班') return '08:00:00'
  return '08:00:00'
}

const getShiftEndTime = (shiftName) => {
  if (shiftName === '白班') return '17:00:00'
  return '17:00:00'
}

const getNurseName = (id) => {
  if (!id || id === '休息') return ''
  const target = allNurses.value.find(item => item.id.toString() === id.toString())
  return target ? target.name : id
}

const getScheduleKey = (dept, shift, date) => {
  return `${dept}_${shift}_${date}`
}

const getScheduleValue = (dept, shift, date) => {
  const key = getScheduleKey(dept, shift, date)
  return scheduleMap.value[key] || ''
}

const fetchAllNurses = async () => {
  try {
    const res = await getAllNurses()
    if (res.data && Array.isArray(res.data)) {
      allNurses.value = res.data
    }
  } catch (error) {
    console.error('获取护士列表失败:', error)
    ElMessage.error('获取护士列表失败')
  }
}

const fetchScheduleData = async () => {
  if (!selectedMonth.value || monthDays.value.length === 0) return

  isLoading.value = true
  scheduleMap.value = {}

  try {
    const startDate = monthDays.value[0].date
    const endDate = monthDays.value[monthDays.value.length - 1].date

    const res = await getAllNurseSchedules(startDate, endDate)

    if (res.data && res.data.data && Array.isArray(res.data.data)) {
      res.data.data.forEach(item => {
        const key = getScheduleKey(item.department, item.shiftType, item.scheduleDate)
        scheduleMap.value[key] = item.receptionistId?.toString() || ''
      })
    }
  } catch (error) {
    console.error('获取排班失败:', error)
    ElMessage.error('获取排班失败')
  } finally {
    isLoading.value = false
  }
}

const onMonthChange = async () => {
  monthDays.value = getMonthDays(selectedMonth.value)
  await fetchScheduleData()
}

const onDepartmentChange = () => {
}

const autoSchedule = () => {
  if (departments.value.length === 0) {
    ElMessage.warning('暂无科室信息，无法排班')
    return
  }
  if (!selectedMonth.value) {
    ElMessage.warning('请先选择月份')
    return
  }
  autoScheduleDialogVisible.value = true
}

const confirmAutoSchedule = async () => {
  isAutoScheduling.value = true

  try {
    const allSchedules = []

    departments.value.forEach(dept => {
      const nurses = getNursesByDept(dept)
      if (nurses.length === 0) return

      monthDays.value.forEach((day, dayIdx) => {
        SHIFT_NAMES.forEach((shiftName) => {
          const nurse = nurses[dayIdx % nurses.length]
          if (nurse) {
            allSchedules.push({
              receptionistId: nurse.id,
              department: dept,
              scheduleDate: day.date,
              shiftType: shiftName,
              startTime: getShiftStartTime(shiftName),
              endTime: getShiftEndTime(shiftName)
            })
          }
        })
      })
    })

    if (allSchedules.length === 0) {
      ElMessage.warning('没有可排班的护士')
      return
    }

    await saveReceptionistSchedulesBatch(allSchedules)
    autoScheduleDialogVisible.value = false
    ElMessage.success('一键排班完成并已保存')
    
    await fetchScheduleData()
  } catch (error) {
    console.error('一键排班失败:', error)
    ElMessage.error('一键排班失败')
  } finally {
    isAutoScheduling.value = false
  }
}

const openEditDialog = (dept, shift, day) => {
  const nurses = getNursesByDept(dept)
  const currentReceptionistId = getScheduleValue(dept, shift, day.date)

  let dayLabel = day.label
  if (day.isWeekend) {
    dayLabel += ' (周末)'
  }

  editForm.value = {
    department: dept,
    date: day.date,
    dayLabel: dayLabel,
    shift: shift,
    receptionistId: currentReceptionistId === '休息' ? 'rest' : currentReceptionistId,
    nurses: nurses
  }
  editDialogVisible.value = true
}

const confirmEdit = async () => {
  isSaving.value = true

  try {
    let receptionistId = null
    let status = 'active'

    if (editForm.value.receptionistId === 'rest' || editForm.value.receptionistId === '') {
      status = 'rest'
    } else {
      receptionistId = parseInt(editForm.value.receptionistId)
    }

    const scheduleData = {
      receptionistId: receptionistId,
      department: editForm.value.department,
      scheduleDate: editForm.value.date,
      shiftType: editForm.value.shift,
      startTime: editForm.value.shift === '白班' ? '08:00:00' : '08:00:00',
      endTime: editForm.value.shift === '白班' ? '17:00:00' : '17:00:00',
      status: status
    }

    await updateReceptionistSchedule(scheduleData)

    await fetchScheduleData()

    editDialogVisible.value = false
    ElMessage.success('排班修改成功')
  } catch (error) {
    console.error('修改排班失败:', error)
    ElMessage.error('修改排班失败')
  } finally {
    isSaving.value = false
  }
}

onMounted(async () => {
  const now = new Date()
  selectedMonth.value = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`
  monthDays.value = getMonthDays(selectedMonth.value)
  
  await fetchAllNurses()
  await fetchScheduleData()
})
</script>

<style scoped>
.schedule-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 20px;
  height: calc(100vh - 84px);
  box-sizing: border-box;
}

.top-card {
  flex-shrink: 0;
}

.top-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
}

.actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.filter-area {
  display: flex;
  align-items: center;
  gap: 12px;
}

.meta-row {
  margin-top: 12px;
  display: flex;
  align-items: center;
  gap: 12px;
  color: #606266;
  font-size: 14px;
}

.range {
  font-weight: 500;
  color: #303133;
}

.hint {
  font-size: 13px;
  color: #909399;
}

.board-card {
  flex: 1;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.legend {
  display: flex;
  gap: 24px;
  margin-bottom: 16px;
  flex-shrink: 0;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #606266;
}

.dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  display: inline-block;
}

.dot.morning {
  background: #67c23a;
}

.dot.rest {
  background: #909399;
}

.schedule-list {
  flex: 1;
  overflow: auto;
}

.dept-section {
  margin-bottom: 24px;
  border: 1px solid #EBEEF5;
  border-radius: 4px;
  overflow: hidden;
}

.dept-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #f5f7fa;
  border-bottom: 1px solid #EBEEF5;
}

.dept-title {
  font-weight: 600;
  font-size: 15px;
  color: #303133;
}

.dept-nurses {
  font-size: 13px;
  color: #606266;
}

.table-wrap {
  overflow-x: auto;
  width: 100%;
}

.header-row {
  display: flex;
  background: #fafafa;
  border-bottom: 1px solid #EBEEF5;
  position: sticky;
  top: 0;
  z-index: 1;
  width: 100%;
}

.header-row > div:last-child,
.data-row > div:last-child {
  border-right: none;
}

.shift-cell {
  width: 60px;
  min-width: 60px;
  padding: 8px 4px;
  font-weight: 600;
  color: #606266;
  text-align: center;
  border-right: 1px solid #EBEEF5;
  font-size: 13px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.day-cell {
  flex: 1;
  min-width: 40px;
  padding: 4px 2px;
  text-align: center;
  border-right: 1px solid #EBEEF5;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.day-cell.weekend {
  background: #fdf6ec;
}

.day-cell small {
  color: #909399;
  font-size: 11px;
}

.data-row {
  display: flex;
  border-bottom: 1px solid #EBEEF5;
  width: 100%;
}

.data-row:last-child {
  border-bottom: none;
}

.edit-cell {
  flex: 1;
  min-width: 40px;
  padding: 6px 2px;
  border-right: 1px solid #EBEEF5;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  cursor: pointer;
}

.edit-cell:hover {
  background: #f0f9ff;
}

.edit-cell.weekend {
  background: #fdf6ec;
}

.empty {
  color: #c0c4cc;
}

.nurse-name {
  font-weight: 500;
  color: #303133;
}

.shift-morning {
  background: #f0f9eb;
}

.preview-area {
  margin-top: 16px;
  border: 1px solid #EBEEF5;
  border-radius: 4px;
  padding: 12px;
}

.preview-title {
  font-weight: 600;
  margin-bottom: 12px;
  color: #303133;
}

.preview-dept {
  padding: 8px 12px;
  margin-bottom: 8px;
  background: #f5f7fa;
  border-radius: 4px;
}

.dept-name {
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.dept-nurses {
  font-size: 13px;
  color: #606266;
}
</style>
