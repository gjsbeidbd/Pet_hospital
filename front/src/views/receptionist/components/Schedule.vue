<template>
  <div class="schedule-wrapper">
    <el-card class="schedule-card">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>前台排班表</span>
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
            <el-button type="primary" @click="openLeaveDialog">申请请假</el-button>
          </div>
        </div>
      </template>
      <el-table :data="scheduleData" border style="width: 100%;">
        <el-table-column prop="time" label="时间段" width="100"></el-table-column>
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

    <!-- 请假申请弹窗 -->
    <el-dialog v-model="leaveDialogVisible" title="请假申请" width="500px">
      <el-form :model="leaveForm" label-width="100px">
        <el-form-item label="请假类型">
          <el-select v-model="leaveForm.type" placeholder="请选择请假类型">
            <el-option label="事假" value="事假"></el-option>
            <el-option label="病假" value="病假"></el-option>
            <el-option label="年假" value="年假"></el-option>
            <el-option label="调休" value="调休"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="开始时间">
          <el-date-picker
            v-model="leaveForm.startDate"
            type="date"
            placeholder="请选择开始日期"
            format="YYYY年MM月DD日"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-date-picker
            v-model="leaveForm.endDate"
            type="date"
            placeholder="请选择结束日期"
            format="YYYY年MM月DD日"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="请假原因">
          <el-input
            v-model="leaveForm.reason"
            type="textarea"
            placeholder="请输入请假原因"
            :rows="3"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="leaveDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitLeave">提交申请</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

// 选择的日期
const selectedDate = ref(new Date())

// 星期列表
const weekdays = ref([])

// 排班数据
const scheduleData = ref([])

// 请假申请弹窗可见性
const leaveDialogVisible = ref(false)

// 请假表单数据
const leaveForm = ref({
  type: '',
  startDate: '',
  endDate: '',
  reason: ''
})

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
    { time: '白班 (8:00-16:00)', '周一': '李晓红', '周二': '张美华', '周三': '李晓红', '周四': '休息', '周五': '张美华', '周六': '王建国', '周日': '休息' },
    { time: '中班 (16:00-24:00)', '周一': '张美华', '周二': '李晓红', '周三': '张美华', '周四': '李晓红', '周五': '王建国', '周六': '张美华', '周日': '李晓红' },
    { time: '夜班 (24:00-8:00)', '周一': '王建国', '周二': '王建国', '周三': '休息', '周四': '张美华', '周五': '李晓红', '周六': '李晓红', '周日': '王建国' },
  ]
}

// 打开请假申请弹窗
const openLeaveDialog = () => {
  // 重置表单数据
  leaveForm.value = {
    type: '',
    startDate: '',
    endDate: '',
    reason: ''
  }
  leaveDialogVisible.value = true
}

// 提交请假申请
const submitLeave = () => {
  // 表单验证
  if (!leaveForm.value.type) {
    ElMessage.error('请选择请假类型')
    return
  }
  if (!leaveForm.value.startDate) {
    ElMessage.error('请选择开始时间')
    return
  }
  if (!leaveForm.value.endDate) {
    ElMessage.error('请选择结束时间')
    return
  }
  if (!leaveForm.value.reason) {
    ElMessage.error('请输入请假原因')
    return
  }
  
  // 这里可以添加实际的后端API调用逻辑
  console.log('提交请假申请:', leaveForm.value)
  
  // 关闭弹窗并显示成功消息
  leaveDialogVisible.value = false
  ElMessage.success('请假申请已提交，请等待审批')
}

// 初始化
updateScheduleData()
</script>

<style scoped>
.schedule-wrapper {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.schedule-card {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.schedule-card :deep(.el-card__body) {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.schedule-card :deep(.el-table) {
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