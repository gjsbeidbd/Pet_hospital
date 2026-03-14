<template>
  <div class="receptionist-schedule-wrapper">
    <el-card class="receptionist-schedule-card">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>前台/护士排班管理</span>
          <div style="display: flex; align-items: center; gap: 10px;">
            <el-date-picker
              v-model="selectedDate"
              type="date"
              placeholder="选择日期"
              format="YYYY年MM月DD日"
              value-format="YYYY-MM-DD"
              @change="updateScheduleData"
            />
            <span>{{ formatDateRange() }}</span>
            <el-button type="primary" @click="openScheduleDialog">排班设置</el-button>
          </div>
        </div>
      </template>
      
      <el-table :data="scheduleData" border style="width: 100%;">
        <el-table-column prop="time" label="时间段" width="120"></el-table-column>
        <el-table-column v-for="day in weekdays" :key="day.key" :label="day.label">
          <template #header>{{ day.label }}</template>
          <template #default="scope">
            <div :class="['schedule-cell', getClassByShift(scope.row.time)]">
              {{ scope.row[day.key] }}
              <el-tag v-if="scope.row[day.key] === '休息'" size="small" type="info" style="margin-left: 5px;">休息</el-tag>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <!-- 排班设置弹窗 -->
    <el-dialog v-model="scheduleDialogVisible" title="前台/护士排班设置" width="800px">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="按天排班" name="daily">
          <el-form label-width="80px">
            <el-form-item label="选择日期">
              <el-date-picker
                v-model="scheduleForm.selectedDay"
                type="date"
                placeholder="请选择日期"
                format="YYYY年MM月DD日"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
            
            <el-table :data="scheduleForm.dailySchedule" border style="width: 100%">
              <el-table-column prop="time" label="时间段" width="150"></el-table-column>
              <el-table-column label="值班人员">
                <template #default="scope">
                  <el-select 
                    v-model="scope.row.staff" 
                    placeholder="请选择值班人员" 
                    style="width: 100%"
                    filterable
                  >
                    <el-option 
                      v-for="receptionist in receptionists" 
                      :key="receptionist.id" 
                      :label="`${receptionist.name} (${receptionist.id})`" 
                      :value="receptionist.id"
                    />
                  </el-select>
                </template>
              </el-table-column>
            </el-table>
          </el-form>
        </el-tab-pane>
        
        <el-tab-pane label="按周排班" name="weekly">
          <el-table :data="weeklyScheduleTable" border style="width: 100%">
            <el-table-column prop="time" label="时间段" width="120"></el-table-column>
            <el-table-column 
              v-for="day in weekDays" 
              :key="day.key" 
              :label="day.label"
            >
              <template #header>{{ day.label }}</template>
              <template #default="scope">
                <el-select 
                  v-model="scope.row[day.key]" 
                  placeholder="请选择值班人员" 
                  style="width: 100%"
                  filterable
                >
                  <el-option 
                    v-for="receptionist in receptionists" 
                    :key="receptionist.id" 
                    :label="`${receptionist.name} (${receptionist.id})`" 
                    :value="receptionist.id"
                  />
                </el-select>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="scheduleDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveSchedule">保存排班</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { ElMessage } from 'element-plus'

// 选择的日期
const selectedDate = ref(new Date())

// 星期列表
const weekdays = ref([])
const weekDays = ref([
  { key: 'monday', label: '周一' },
  { key: 'tuesday', label: '周二' },
  { key: 'wednesday', label: '周三' },
  { key: 'thursday', label: '周四' },
  { key: 'friday', label: '周五' },
  { key: 'saturday', label: '周六' },
  { key: 'sunday', label: '周日' }
])

// 排班数据
const scheduleData = ref([])

// 排班设置弹窗可见性
const scheduleDialogVisible = ref(false)
const activeTab = ref('daily')

// 排班表单数据
const scheduleForm = reactive({
  selectedDay: '',
  dailySchedule: []
})

// 模拟数据：前台/护士列表
const receptionists = ref([
  { id: '1002', name: '李晓红', role: '前台', department: '服务部' },
  { id: '1005', name: '王护士', role: '护士', department: '护理部' }
])

// 按周排班表格数据
const weeklyScheduleTable = ref([
  { time: '白班 (8:00-16:00)', monday: '', tuesday: '', wednesday: '', thursday: '', friday: '', saturday: '', sunday: '' },
  { time: '中班 (16:00-24:00)', monday: '', tuesday: '', wednesday: '', thursday: '', friday: '', saturday: '', sunday: '' },
  { time: '夜班 (24:00-8:00)', monday: '', tuesday: '', wednesday: '', thursday: '', friday: '', saturday: '', sunday: '' }
])

// 根据班次获取样式类
const getClassByShift = (shift) => {
  if (shift.includes('白班')) return 'morning'
  if (shift.includes('中班')) return 'afternoon'
  if (shift.includes('夜班')) return 'night'
  return ''
}

// 计算日期范围显示
const formatDateRange = () => {
  if (weekdays.value.length === 0) return ''
  const startDate = weekdays.value[0].date
  const endDate = weekdays.value[weekdays.value.length - 1].date
  return `${startDate} 至 ${endDate}`
}

// 获取一周的日期
const getWeekDates = (date) => {
  const currentDate = new Date(date)
  const dayOfWeek = currentDate.getDay() === 0 ? 7 : currentDate.getDay() // 周日为7
  const startDate = new Date(currentDate)
  startDate.setDate(currentDate.getDate() - dayOfWeek + 1) // 本周一
  
  const dates = []
  for (let i = 0; i < 7; i++) {
    const date = new Date(startDate)
    date.setDate(startDate.getDate() + i)
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    const dayLabel = ['周一', '周二', '周三', '周四', '周五', '周六', '周日'][i]
    dates.push({
      key: dayLabel,
      label: dayLabel,
      date: `${date.getFullYear()}-${month}-${day}`
    })
  }
  return dates
}

// 更新排班数据
const updateScheduleData = () => {
  weekdays.value = getWeekDates(selectedDate.value)
  
  // 模拟数据：根据日期生成排班表
  scheduleData.value = [
    { time: '白班 (8:00-16:00)', '周一': '王建国', '周二': '李晓红', '周三': '王建国', '周四': '休息', '周五': '张伟', '周六': '李晓红', '周日': '休息' },
    { time: '中班 (16:00-24:00)', '周一': '李晓红', '周二': '王建国', '周三': '李晓红', '周四': '王建国', '周五': '李医生', '周六': '张伟', '周日': '王建国' },
    { time: '夜班 (24:00-8:00)', '周一': '张伟', '周二': '张伟', '周三': '休息', '周四': '李晓红', '周五': '王建国', '周六': '王建国', '周日': '张伟' },
  ]
}

// 打开排班设置弹窗
const openScheduleDialog = () => {
  scheduleForm.selectedDay = ''
  scheduleForm.dailySchedule = [
    { time: '白班 (8:00-16:00)', staff: '' },
    { time: '中班 (16:00-24:00)', staff: '' },
    { time: '夜班 (24:00-8:00)', staff: '' }
  ]
  scheduleDialogVisible.value = true
}

// 保存排班
const saveSchedule = () => {
  // 这里可以添加实际的后端API调用逻辑
  console.log('保存前台/护士排班:', scheduleForm)
  
  scheduleDialogVisible.value = false
  ElMessage.success('前台/护士排班设置已保存')
}

// 初始化
updateScheduleData()
</script>

<style scoped>
.receptionist-schedule-wrapper {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.receptionist-schedule-card {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.receptionist-schedule-card :deep(.el-card__body) {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.receptionist-schedule-card :deep(.el-table) {
  flex: 1;
}

/* 课表样式 */
.schedule-cell {
  font-size: 13px;
  text-align: center;
  padding: 8px 0;
  border: 1px solid #EBEEF5;
}

.schedule-cell.morning {
  background-color: #E6F0FF;
}

.schedule-cell.afternoon {
  background-color: #F0FAF0;
}

.schedule-cell.night {
  background-color: #F0F0F0;
}
</style>