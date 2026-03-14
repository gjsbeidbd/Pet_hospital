<template>
  <div>
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>内科专家诊室排班表</span>
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
  </div>
</template>

<script setup>
import { ref } from 'vue'

// 选择的日期
const selectedDate = ref(new Date())

// 星期列表
const weekdays = ref([])

// 排班数据
const scheduleData = ref([])

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

// 更新排班数据（只显示内科专家诊室医生的排班）
const updateScheduleData = () => {
  weekdays.value = getWeekDates(selectedDate.value)
  
  // 模拟数据：内科专家诊室医生排班表
  scheduleData.value = [
    { time: '白班 (8:00-16:00)', '周一': '王建国(主任)', '周二': '李晓红', '周三': '王建国(主任)', '周四': '休息', '周五': '李晓红', '周六': '张美华', '周日': '休息' },
    { time: '中班 (16:00-24:00)', '周一': '李晓红', '周二': '王建国(主任)', '周三': '李晓红', '周四': '王建国(主任)', '周五': '张美华', '周六': '李晓红', '周日': '王建国(主任)' },
    { time: '夜班 (24:00-8:00)', '周一': '张美华', '周二': '张美华', '周三': '休息', '周四': '李晓红', '周五': '王建国(主任)', '周六': '王建国(主任)', '周日': '李晓红' },
  ]
}

// 初始化
updateScheduleData()
</script>

<style scoped>
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