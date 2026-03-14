<template>
  <div class="doctor-schedule-wrapper">
    <el-card class="doctor-schedule-card">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>医生排班管理</span>
          <div style="display: flex; align-items: center; gap: 10px;">
            <el-select 
              v-model="selectedDepartmentForView" 
              placeholder="选择科室" 
              style="width: 150px; margin-right: 10px;"
              @change="updateScheduleData"
            >
              <el-option 
                v-for="dept in departments" 
                :key="dept" 
                :label="dept" 
                :value="dept"
              />
              <el-option label="全部科室" value="" />
            </el-select>
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
        <el-table-column prop="time" label="时间段" width="150"></el-table-column>
        <el-table-column v-for="day in weekdays" :key="day.key" :label="day.label">
          <template #header>
            <div>{{ day.label }}<br/><span style="font-size: 12px; color: #909399;">{{ day.date }}</span></div>
          </template>
          <template #default="scope">
            <div :class="['schedule-cell', getClassByShift(scope.row.time)]">
              <div v-if="scope.row[day.key] === '休息'" class="rest-period">
                <el-tag type="info" size="small">休息</el-tag>
              </div>
              <div v-else-if="scope.row[day.key]" class="doctor-name">
                {{ getDoctorName(scope.row[day.key]) }}
              </div>
              <div v-else class="empty-slot">-</div>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <!-- 排班设置弹窗 -->
    <el-dialog 
      v-model="scheduleDialogVisible" 
      title="医生排班设置" 
      width="90%" 
      :before-close="handleClose"
      :fullscreen="isFullscreen"
      class="schedule-dialog"
    >
      <div class="dialog-content">
        <el-tabs v-model="activeTab">
          <el-tab-pane label="按科室排班" name="byDepartment">
            <div class="tab-header">
              <el-alert
                title="说明：您可以为不同科室设置排班模板，设置完成后点击底部的保存按钮即可应用到指定周期"
                type="info"
                show-icon
                style="margin-bottom: 20px;"
              />
              
              <div class="header-actions">
                <el-button 
                  @click="toggleFullscreen"
                  size="small"
                  circle
                  title="切换全屏"
                >
                  <el-icon>
                    <FullScreen />
                  </el-icon>
                </el-button>
              </div>
            </div>
            
            <div class="schedule-content-wrapper">
              <!-- 科室选择和医生列表 -->
              <div class="doctor-selection-panel">
                <el-card>
                  <template #header>
                    <span>科室与医生</span>
                  </template>
                  <el-select 
                    v-model="selectedDepartment" 
                    placeholder="请选择科室" 
                    style="width: 100%; margin-bottom: 15px;"
                    @change="onDepartmentChange"
                    :disabled="isSaving"
                  >
                    <el-option 
                      v-for="dept in departments" 
                      :key="dept" 
                      :label="dept" 
                      :value="dept"
                    />
                  </el-select>
                  
                  <div style="margin-bottom: 10px; font-weight: bold;">{{ selectedDepartment || '请选择科室' }}</div>
                  <el-checkbox-group 
                    v-model="selectedDoctorsInDept" 
                    style="display: flex; flex-direction: column; gap: 10px;"
                    :disabled="isSaving"
                  >
                    <el-checkbox 
                      v-for="doctor in doctorsInSelectedDept" 
                      :key="doctor.id" 
                      :label="doctor.id"
                    >
                      {{ doctor.name }} ({{ doctor.department }})
                    </el-checkbox>
                  </el-checkbox-group>
                  
                  <!-- 排班说明 -->
                  <div style="margin-top: 15px; padding: 10px; background-color: #f5f7fa; border-radius: 4px;">
                    <h4 style="margin: 0 0 8px 0; font-size: 14px;">排班说明</h4>
                    <ul style="margin: 0; padding-left: 20px; font-size: 13px; color: #606266;">
                      <li>白班：08:00-16:00</li>
                      <li>中班：16:00-00:00</li>
                      <li>夜班：00:00-08:00</li>
                      <li>休息：不安排值班</li>
                    </ul>
                  </div>
                </el-card>
              </div>
              
              <!-- 排班模板设置 -->
              <div class="schedule-template-panel">
                <el-card>
                  <template #header>
                    <div style="display: flex; justify-content: space-between; align-items: center;">
                      <span>排班模板设置</span>
                      <div>
                        <el-button 
                          size="small" 
                          @click="clearAllSchedules"
                          :disabled="isSaving"
                        >
                          清空全部
                        </el-button>
                        <el-button 
                          size="small" 
                          type="primary" 
                          @click="autoAssignSchedules"
                          :disabled="isSaving || !selectedDepartment"
                        >
                          自动分配
                        </el-button>
                      </div>
                    </div>
                  </template>
                  
                  <!-- 周排班模板说明 -->
                  <div style="margin-bottom: 20px;">
                    <el-alert
                      title="提示：设置完成后点击底部的保存按钮即可应用到指定周期"
                      type="info"
                      show-icon
                    />
                  </div>
                  
                  <!-- 周排班模板 -->
                  <div style="margin-bottom: 15px; font-weight: bold;">一周排班模板</div>
                  <div class="schedule-table-wrapper">
                    <el-table 
                      :data="departmentScheduleTemplate" 
                      border 
                      style="width: 100%"
                      :header-cell-style="{ background: '#f5f7fa', color: '#606266' }"
                    >
                      <el-table-column prop="time" label="时间段" width="150">
                        <template #default="scope">
                          <div>{{ scope.row.time }}</div>
                          <div style="font-size: 12px; color: #909399;">{{ scope.row.description }}</div>
                        </template>
                      </el-table-column>
                      <el-table-column 
                        v-for="day in weekDays" 
                        :key="day.key" 
                        :label="day.label"
                        width="120"
                      >
                        <template #default="scope">
                          <el-select 
                            v-model="scope.row[day.key]" 
                            placeholder="请选择医生" 
                            style="width: 100%"
                            clearable
                            :disabled="isSaving"
                            @change="handleScheduleChange(scope.row, day.key, scope.$index)"
                          >
                            <el-option 
                              v-for="doctor in doctorsInSelectedDept" 
                              :key="doctor.id" 
                              :label="doctor.name" 
                              :value="doctor.id"
                            />
                            <el-option label="休息" value="休息" />
                            <el-option label="空闲" value="" />
                          </el-select>
                        </template>
                      </el-table-column>
                    </el-table>
                  </div>
                  
                  <!-- 排班统计 -->
                  <div style="margin-top: 20px;">
                    <h4>排班统计</h4>
                    <div class="stats-table-wrapper">
                      <el-table 
                        :data="scheduleStats" 
                        border 
                        style="width: 100%"
                        size="small"
                      >
                        <el-table-column prop="doctorName" label="医生姓名" width="120"></el-table-column>
                        <el-table-column prop="totalShifts" label="总班次" width="80"></el-table-column>
                        <el-table-column prop="morningShifts" label="白班" width="80"></el-table-column>
                        <el-table-column prop="afternoonShifts" label="中班" width="80"></el-table-column>
                        <el-table-column prop="nightShifts" label="夜班" width="80"></el-table-column>
                        <el-table-column prop="restDays" label="休息天数" width="90"></el-table-column>
                      </el-table>
                    </div>
                  </div>
                </el-card>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="scheduleDialogVisible = false" :disabled="isSaving">取消</el-button>
          <el-button 
            type="primary" 
            @click="saveScheduleToPeriod" 
            :loading="isSaving"
            :disabled="!selectedDepartment"
          >
            {{ isSaving ? '保存中...' : '保存' }}
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox, ElIcon } from 'element-plus'
import { applyScheduleToPeriod as applyScheduleToPeriodAPI, getSchedules } from '@/services/api'
import {FullScreen} from '@element-plus/icons-vue'

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
const activeTab = ref('byDepartment')

// 科室选择
const selectedDepartment = ref('')
const selectedDoctorsInDept = ref([])

// 用于查看排班的科室选择
const selectedDepartmentForView = ref('')

// 保存状态
const isSaving = ref(false)

// 全屏状态
const isFullscreen = ref(false)

// 从数据库获取医生列表
const doctors = ref([])

// 获取医生数据
const fetchDoctors = async () => {
  try {
    // 模拟从后端获取医生数据
    // 实际项目中这里应该是真实的API调用
    doctors.value = [
      { id: '1', name: '王建国', role: '医生', department: '内科' },
      { id: '2', name: '李晓明', role: '医生', department: '外科' },
      { id: '4', name: '张伟', role: '医生', department: '内科' },
      { id: '5', name: '刘芳', role: '医生', department: '内科' },
      { id: '6', name: '陈志强', role: '医生', department: '外科' },
      { id: '7', name: '赵磊', role: '医生', department: '外科' }
    ];
  } catch (error) {
    console.error('获取医生数据失败:', error);
    ElMessage.error('获取医生数据失败');
  }
}

// 科室列表
const departments = computed(() => {
  const deptSet = new Set()
  doctors.value.forEach(doctor => deptSet.add(doctor.department))
  return Array.from(deptSet)
})

// 当前科室的医生
const doctorsInSelectedDept = computed(() => {
  return doctors.value.filter(doctor => doctor.department === selectedDepartment.value)
})

// 优化的科室排班模板，包含更详细的时间段描述
const departmentScheduleTemplate = ref([
  { 
    time: '白班', 
    description: '08:00-16:00',
    monday: '', tuesday: '', wednesday: '', thursday: '', friday: '', saturday: '', sunday: '' 
  },
  { 
    time: '中班', 
    description: '16:00-00:00', 
    monday: '', tuesday: '', wednesday: '', thursday: '', friday: '', saturday: '', sunday: '' 
  },
  { 
    time: '夜班', 
    description: '00:00-08:00', 
    monday: '', tuesday: '', wednesday: '', thursday: '', friday: '', saturday: '', sunday: '' 
  }
])

// 排班统计
const scheduleStats = ref([])

// 根据医生ID获取医生姓名
const getDoctorName = (doctorId) => {
  if (!doctorId || doctorId === '休息') return '休息'
  const doctor = doctors.value.find(d => d.id === doctorId)
  return doctor ? doctor.name : doctorId
}

// 根据班次获取样式类
const getClassByShift = (shift) => {
  if (shift.includes('白班') || shift.includes('08:00-16:00')) return 'morning'
  if (shift.includes('中班') || shift.includes('16:00-00:00')) return 'afternoon'
  if (shift.includes('夜班') || shift.includes('00:00-08:00')) return 'night'
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
      key: dayLabel.toLowerCase(),
      label: dayLabel,
      date: `${date.getFullYear()}-${month}-${day}`
    })
  }
  return dates
}

// 科室变化时的处理
const onDepartmentChange = () => {
  // 默认选中该科室的所有医生
  selectedDoctorsInDept.value = doctorsInSelectedDept.value.map(doctor => doctor.id)
  updateScheduleStats()
}

// 更新排班统计
const updateScheduleStats = () => {
  if (!selectedDepartment.value) {
    scheduleStats.value = []
    return
  }

  const stats = []
  const doctorMap = {}
  
  // 初始化统计数据
  doctorsInSelectedDept.value.forEach(doctor => {
    doctorMap[doctor.id] = {
      doctorId: doctor.id,
      doctorName: doctor.name,
      totalShifts: 0,
      morningShifts: 0,
      afternoonShifts: 0,
      nightShifts: 0,
      restDays: 0
    }
  })

  // 遍历排班模板统计各医生的班次
  departmentScheduleTemplate.value.forEach(shift => {
    weekDays.value.forEach(day => {
      const doctorId = shift[day.key]
      if (doctorId && doctorId !== '休息') {
        if (doctorMap[doctorId]) {
          doctorMap[doctorId].totalShifts++
          if (shift.time === '白班') doctorMap[doctorId].morningShifts++
          if (shift.time === '中班') doctorMap[doctorId].afternoonShifts++
          if (shift.time === '夜班') doctorMap[doctorId].nightShifts++
        }
      } else if (doctorId === '休息') {
        // 统计休息天数，每个医生在一周内可能有多个休息时段
        Object.values(doctorMap).forEach(doctor => {
          // 每个时段算一次休息
          doctor.restDays++
        })
      }
    })
  })

  // 计算实际休息天数（一周7天，减去工作天数）
  Object.values(doctorMap).forEach(doctor => {
    // 计算该医生在一周中被安排的天数
    let workingDays = 0
    weekDays.value.forEach(day => {
      departmentScheduleTemplate.value.forEach(shift => {
        if (shift[day.key] === doctor.doctorId) {
          workingDays++
        }
      })
    })
    doctor.restDays = 7 - workingDays
  })

  scheduleStats.value = Object.values(doctorMap)
}

// 排班变更处理
const handleScheduleChange = (shiftRow, dayKey, shiftIndex) => {
  updateScheduleStats()
}

// 清空所有排班
const clearAllSchedules = () => {
  ElMessageBox.confirm(
    '确定要清空所有排班设置吗？此操作不可恢复。',
    '确认清空',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(() => {
    departmentScheduleTemplate.value.forEach(shift => {
      weekDays.value.forEach(day => {
        shift[day.key] = ''
      })
    })
    updateScheduleStats()
    ElMessage.success('已清空所有排班')
  }).catch(() => {
    // 用户取消操作
  })
}

// 自动分配排班
const autoAssignSchedules = () => {
  if (selectedDoctorsInDept.value.length === 0) {
    ElMessage.warning('请先选择医生')
    return
  }

  ElMessageBox.confirm(
    '确定要自动分配排班吗？这将覆盖当前的排班设置。',
    '确认自动分配',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info',
    }
  ).then(() => {
    // 自动分配逻辑：轮换安排医生
    departmentScheduleTemplate.value.forEach((shift, shiftIndex) => {
      weekDays.value.forEach((day, dayIndex) => {
        if (selectedDoctorsInDept.value.length > 0) {
          // 轮换分配医生
          const doctorIndex = (dayIndex + shiftIndex) % selectedDoctorsInDept.value.length
          shift[day.key] = selectedDoctorsInDept.value[doctorIndex]
        }
      })
    })
    
    // 随机安排一些休息日
    departmentScheduleTemplate.value.forEach((shift, shiftIndex) => {
      // 每个班次类型随机安排1-2天休息
      const restDaysCount = Math.floor(Math.random() * 2) + 1
      for (let i = 0; i < restDaysCount; i++) {
        const randomDayIndex = Math.floor(Math.random() * weekDays.value.length)
        shift[weekDays.value[randomDayIndex].key] = '休息'
      }
    })
    
    updateScheduleStats()
    ElMessage.success('已自动分配排班')
  }).catch(() => {
    // 用户取消操作
  })
}

// 更新排班数据
const updateScheduleData = async () => {
  weekdays.value = getWeekDates(selectedDate.value)
  
  try {
    // 获取一周的开始和结束日期
    const startDate = weekdays.value[0].date
    const endDate = weekdays.value[weekdays.value.length - 1].date
    
    // 调用API获取排班数据
    const response = await getSchedules({
      startDate,
      endDate,
      department: selectedDepartmentForView.value || ''
    })
    
    // 处理API返回的排班数据
    if (response.data && Array.isArray(response.data)) {
      // 按班次分组排班数据
      const scheduleByShift = {};
      response.data.forEach(schedule => {
        // 使用新的字段名
        const shiftKey = schedule.shiftType || schedule.shift;
        const date = new Date(schedule.scheduleDate || schedule.date);
        const dayOfWeek = getDayOfWeekFromDateString(date);
        
        if (!scheduleByShift[shiftKey]) {
          scheduleByShift[shiftKey] = {};
        }
        // 使用医生ID作为值
        scheduleByShift[shiftKey][dayOfWeek.toLowerCase()] = schedule.doctorId || schedule.employeeId;
      });
      
      // 构建表格数据
      scheduleData.value = [];
      Object.keys(scheduleByShift).forEach(shift => {
        const shiftData = { time: shift };
        weekDays.value.forEach(day => {
          const dayKey = day.key;
          shiftData[dayKey] = scheduleByShift[shift][dayKey] || '';
        });
        scheduleData.value.push(shiftData);
      });
    } else {
      // 如果API没有返回数据，使用默认数据
      scheduleData.value = [
        { time: '白班 (8:00-16:00)', monday: '1', tuesday: '4', wednesday: '1', thursday: '休息', friday: '5', saturday: '4', sunday: '休息' },
        { time: '中班 (16:00-24:00)', monday: '2', tuesday: '6', wednesday: '2', thursday: '6', friday: '7', saturday: '2', sunday: '7' },
        { time: '夜班 (24:00-8:00)', monday: '休息', tuesday: '休息', wednesday: '休息', thursday: '休息', friday: '休息', saturday: '休息', sunday: '休息' },
      ];
    }
  } catch (error) {
    console.error('获取排班数据失败:', error);
    
    // 如果API调用失败，使用默认数据
    scheduleData.value = [
      { time: '白班 (8:00-16:00)', monday: '1', tuesday: '4', wednesday: '1', thursday: '休息', friday: '5', saturday: '4', sunday: '休息' },
      { time: '中班 (16:00-24:00)', monday: '2', tuesday: '6', wednesday: '2', thursday: '6', friday: '7', saturday: '2', sunday: '7' },
      { time: '夜班 (24:00-8:00)', monday: '休息', tuesday: '休息', wednesday: '休息', thursday: '休息', friday: '休息', saturday: '休息', sunday: '休息' },
    ];
  }
}

// 打开排班设置弹窗
const openScheduleDialog = () => {
  scheduleDialogVisible.value = true
  updateScheduleStats()
}

// 保存排班到周期
const saveScheduleToPeriod = () => {
  if (!selectedDepartment.value) {
    ElMessage.warning('请先选择科室')
    return
  }
  
  // 检查是否有任何排班安排
  let hasSchedule = false
  for (const shift of departmentScheduleTemplate.value) {
    for (const day of weekDays.value) {
      if (shift[day.key]) {
        hasSchedule = true
        break
      }
    }
    if (hasSchedule) break
  }
  
  if (!hasSchedule) {
    ElMessage.warning('请先设置排班')
    return
  }
  
  // 使用当前选择的日期范围
  const startDate = weekdays.value[0].date;
  const endDate = weekdays.value[weekdays.value.length - 1].date;
  
  isSaving.value = true;
  
  // 确认对话框
  ElMessageBox.confirm(
    `确定要将"${selectedDepartment.value}"科室的排班模板保存到 ${startDate} 至 ${endDate} 吗？`,
    '确认保存',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      // 调用后端API保存排班到周期
      const response = await applyScheduleToPeriodAPI({
        template: departmentScheduleTemplate.value,
        department: selectedDepartment.value,
        startDate: startDate,
        endDate: endDate
      });
      
      ElMessage.success(response.data.message || `"${selectedDepartment.value}"科室排班已保存到指定周期`)
      
      // 关闭对话框
      scheduleDialogVisible.value = false
      
      // 重新加载排班数据
      updateScheduleData()
    } catch (error) {
      console.error('保存排班到周期失败:', error)
      ElMessage.error(error.response?.data?.error || '保存排班到周期失败')
    } finally {
      isSaving.value = false;
    }
  }).catch(() => {
    // 用户取消操作
    isSaving.value = false;
  })
}

// 关闭对话框前的处理
const handleClose = (done) => {
  if (isSaving.value) {
    return
  }
  
  ElMessageBox.confirm('排班尚未保存，确定要关闭吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(() => {
      done()
    })
    .catch(() => {
      // 取消关闭
    })
}

// 切换全屏
const toggleFullscreen = () => {
  isFullscreen.value = !isFullscreen.value
}

// 获取星期几的中文名称
const getDayOfWeekFromDateString = (date) => {
  const dayOfWeek = date.getDay();
  const days = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];
  return days[dayOfWeek];
}

// 监听科室变化，更新统计
watch(selectedDepartment, () => {
  updateScheduleStats()
})

// 初始化
onMounted(() => {
  fetchDoctors()
  updateScheduleData()
})
</script>

<style scoped>
.doctor-schedule-wrapper {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.doctor-schedule-card {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.doctor-schedule-card :deep(.el-card__body) {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.doctor-schedule-card :deep(.el-table) {
  flex: 1;
}

/* 课表样式 */
.schedule-cell {
  font-size: 12px;
  text-align: center;
  padding: 8px 0;
  border: 1px solid #EBEEF5;
  min-height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.schedule-cell.morning {
  background-color: #f0f9ff;
}

.schedule-cell.afternoon {
  background-color: #f5fff0;
}

.schedule-cell.night {
  background-color: #f8f8f8;
}

.doctor-name {
  font-weight: 500;
  color: #303133;
}

.rest-period {
  color: #909399;
}

.empty-slot {
  color: #c0c4cc;
}

/* 弹窗内容样式 */
.dialog-content {
  max-height: calc(100vh - 200px);
  overflow-y: auto;
}

.schedule-dialog :deep(.el-dialog__body) {
  padding: 20px;
}

.tab-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
}

.header-actions {
  flex-shrink: 0;
  margin-left: 10px;
}

.schedule-content-wrapper {
  display: flex;
  gap: 20px;
  min-height: 400px;
}

.doctor-selection-panel {
  width: 300px;
  flex-shrink: 0;
}

.schedule-template-panel {
  flex: 1;
  min-width: 0; /* 允许flex项目收缩到其内容的大小以下 */
}

.schedule-table-wrapper {
  max-height: 300px;
  overflow-x: auto;
}

.stats-table-wrapper {
  max-height: 200px;
  overflow-x: auto;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .schedule-content-wrapper {
    flex-direction: column;
  }
  
  .doctor-selection-panel {
    width: 100%;
  }
}

@media (max-width: 768px) {
  .schedule-content-wrapper {
    gap: 10px;
  }
  
  .tab-header {
    flex-direction: column;
    align-items: stretch;
  }
  
  .header-actions {
    margin-left: 0;
    margin-top: 10px;
    display: flex;
    justify-content: flex-end;
  }
}
</style>