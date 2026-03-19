<template>
  <div class="appointment-wrapper">
    <el-card class="appointment-card">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>在线预约挂号</span>
          <el-button type="primary" @click="bookingDialogVisible = true">发起新预约</el-button>
        </div>
      </template>

      <el-table :data="paginatedAppointments" border style="width: 100%" v-loading="loading">
        <el-table-column prop="appointmentDate" label="预约日期" min-width="110">
          <template #default="scope">
            {{ scope.row.appointmentDate }}
          </template>
        </el-table-column>
        <el-table-column prop="appointmentTime" label="预约时间" min-width="90">
          <template #default="scope">
            {{ scope.row.appointmentTime }}
          </template>
        </el-table-column>
        <el-table-column prop="petName" label="就诊宠物" min-width="100"></el-table-column>
        <el-table-column prop="petSpecies" label="种类" min-width="70"></el-table-column>
        <el-table-column prop="petBreed" label="品种" min-width="90"></el-table-column>
        <el-table-column prop="doctorName" label="预约医生" min-width="100"></el-table-column>
        <el-table-column prop="department" label="科室" min-width="90"></el-table-column>
        <el-table-column prop="reason" label="病情描述" min-width="150" show-overflow-tooltip></el-table-column>
        <el-table-column prop="status" label="当前状态" min-width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.status === 'pending'" type="info">待取号</el-tag>
            <el-tag v-else-if="scope.row.status === 'waiting'" type="warning">待就诊</el-tag>
            <el-tag v-else-if="scope.row.status === 'in_progress'" type="primary">正在就诊</el-tag>
            <el-tag v-else-if="scope.row.status === 'pending_payment'" type="danger">待缴费</el-tag>
            <el-tag v-else-if="scope.row.status === 'completed'" type="success">已完成</el-tag>
            <el-tag v-else-if="scope.row.status === 'cancelled'" type="danger">已取消</el-tag>
            <el-tag v-else type="info">未知状态</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right" align="center">
          <template #default="scope">
            <el-button 
              v-if="scope.row.status === 'pending' || scope.row.status === 'confirmed'" 
              type="danger" 
              size="small"
              @click="handleCancelAppointment(scope.row.id)"
            >
              取消
            </el-button>
            <el-button v-else type="primary" size="small" @click="viewAppointmentDetail(scope.row)">详情</el-button>
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
          :total="appointments.length"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :pager-count="5"
          prev-text="上一页"
          next-text="下一页"
          :page-count="Math.ceil(appointments.length / pageSize)"
          :locale="{
            total: `共 {total} 条记录`,
            pagesize: '条/页',
            goto: '跳至',
            pageClassifier: '页'
          }"
        />
      </div>
    </el-card>
  </div>

  <!-- 发起新预约弹窗 -->
  <el-dialog v-model="bookingDialogVisible" title="发起新预约" width="500px">
    <el-form label-width="100px" :model="bookingForm" v-loading="submitting">
      <el-form-item label="选择宠物" prop="petId">
        <el-select v-model="bookingForm.petId" placeholder="请选择就诊宠物" style="width: 100%">
          <el-option
            v-for="pet in pets"
            :key="pet.id"
            :label="`${pet.name} (${pet.breed})`"
            :value="pet.id"
          >
          </el-option>
        </el-select>
      </el-form-item>
      
      <el-form-item label="预约科室" prop="department">
        <el-select v-model="bookingForm.department" placeholder="请选择科室" style="width: 100%" @change="handleDepartmentChange">
          <el-option 
            v-for="dept in departments" 
            :key="dept.id" 
            :label="dept.name" 
            :value="dept.name"
          ></el-option>
        </el-select>
      </el-form-item>
      
      <el-form-item label="预约日期" prop="appointmentDate">
        <el-date-picker
          v-model="bookingForm.appointmentDate"
          type="date"
          placeholder="请选择预约日期"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
          :disabled-date="disabledDate"
          @change="handleDateChange"
          style="width: 100%"
        ></el-date-picker>
      </el-form-item>
      
      <el-form-item label="指定医生" prop="doctorId">
        <el-select v-model="bookingForm.doctorId" placeholder="请选择医生" style="width: 100%" :disabled="!bookingForm.appointmentDate || !bookingForm.department || !doctors || doctors.length === 0">
          <el-option 
            v-for="doctor in doctors || []" 
            :key="doctor.id" 
            :label="getDoctorLabel(doctor)"
            :value="doctor.id"
          ></el-option>
        </el-select>
        <div v-if="bookingForm.appointmentDate && bookingForm.department" style="font-size: 12px; color: #909399; margin-top: 5px; display: flex; align-items: center; gap: 4px;">
          <el-icon><InfoFilled /></el-icon>
          <span>仅显示 {{ bookingForm.appointmentDate }} 值班的医生</span>
        </div>
      </el-form-item>
      
      <el-form-item label="病情描述" prop="reason">
        <el-input
          v-model="bookingForm.reason"
          type="textarea"
          placeholder="请简要描述宠物的症状或就诊原因"
          :rows="3"
        ></el-input>
      </el-form-item>
    </el-form>
    
    <template #footer>
      <el-button @click="bookingDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="submitBooking" :loading="submitting">确认预约</el-button>
    </template>
  </el-dialog>

  <!-- 预约详情弹窗 -->
  <el-dialog v-model="detailDialogVisible" title="预约详情" width="500px">
    <el-descriptions :column="1" border v-if="selectedAppointment" label-class-name="detail-label">
      <el-descriptions-item label="预约日期">{{ selectedAppointment.appointmentDate }}</el-descriptions-item>
      <el-descriptions-item label="预约时间">{{ selectedAppointment.appointmentTime }}</el-descriptions-item>
      <el-descriptions-item label="就诊宠物">{{ selectedAppointment.petName }}</el-descriptions-item>
      <el-descriptions-item label="宠物种类">{{ selectedAppointment.petSpecies }}</el-descriptions-item>
      <el-descriptions-item label="宠物品种">{{ selectedAppointment.petBreed }}</el-descriptions-item>
      <el-descriptions-item label="预约医生">{{ selectedAppointment.doctorName }}</el-descriptions-item>
      <el-descriptions-item label="预约科室">{{ selectedAppointment.department }}</el-descriptions-item>
      <el-descriptions-item label="当前状态">
        <el-tag v-if="selectedAppointment.status === 'pending'" type="info">待取号</el-tag>
        <el-tag v-else-if="selectedAppointment.status === 'waiting'" type="warning">待就诊</el-tag>
        <el-tag v-else-if="selectedAppointment.status === 'in_progress'" type="primary">正在就诊</el-tag>
        <el-tag v-else-if="selectedAppointment.status === 'pending_payment'" type="danger">待缴费</el-tag>
        <el-tag v-else-if="selectedAppointment.status === 'completed'" type="success">已完成</el-tag>
        <el-tag v-else-if="selectedAppointment.status === 'cancelled'" type="danger">已取消</el-tag>
        <el-tag v-else type="info">未知状态</el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="病情描述">{{ selectedAppointment.reason || '无' }}</el-descriptions-item>
    </el-descriptions>
    <template #footer>
      <el-button type="primary" @click="detailDialogVisible = false">关闭</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, defineProps, computed, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { InfoFilled } from '@element-plus/icons-vue'
import { getUserAppointments, createAppointment, cancelAppointment, getDepartments, getDoctorsByDepartment, getOnDutyDoctorsByDateAndDepartment } from '@/services/api'

// 定义props
const props = defineProps({
  pets: {
    type: Array,
    required: true
  }
})

// 加载状态
const loading = ref(false)
const submitting = ref(false)

// 分页相关数据
const currentPage = ref(1)
const pageSize = ref(10)

// 预约列表数据
const appointments = ref([])

// 科室和医生数据
const departments = ref([])
const doctors = ref([])
const allDoctorsInDepartment = ref([]) // 存储科室的所有医生
const onDutyDoctorIds = ref([]) // 存储值班的医生 ID

// 弹窗控制
const bookingDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const selectedAppointment = ref(null)

// 预约表单数据
const bookingForm = ref({
  petId: '',
  department: '',
  doctorId: null,
  appointmentDate: '',
  reason: ''
})

// 计算当前页的预约数据
const paginatedAppointments = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return appointments.value.slice(start, end)
})

// 分页事件处理
const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
}

const handleCurrentChange = (val) => {
  currentPage.value = val
}

// 加载用户预约列表
const loadUserAppointments = async () => {
  try {
    loading.value = true
    const userId = localStorage.getItem('userId')
    if (userId) {
      const res = await getUserAppointments(userId)
      appointments.value = res.data.map(appointment => ({
        ...appointment,
        petName: props.pets.find(pet => pet.id === appointment.petId)?.name || '未知宠物',
        petSpecies: props.pets.find(pet => pet.id === appointment.petId)?.species || '',
        petBreed: props.pets.find(pet => pet.id === appointment.petId)?.breed || '',
        doctorName: appointment.doctorName || (appointment.doctorId ? `医生 ${appointment.doctorId}` : '随机分配'),
        department: appointment.department || ''
      }))
    }
  } catch (error) {
    console.error('加载预约列表失败:', error)
    ElMessage.error('加载预约列表失败')
  } finally {
    loading.value = false
  }
}

// 加载科室列表
const loadDepartments = async () => {
  try {
    const res = await getDepartments()
    departments.value = res.data
  } catch (error) {
    console.error('加载科室失败:', error)
  }
}

// 科室变化时加载对应医生
const handleDepartmentChange = async (department) => {
  bookingForm.value.doctorId = null
  doctors.value = []
  allDoctorsInDepartment.value = []
  onDutyDoctorIds.value = []
  
  if (department) {
    try {
      const res = await getDoctorsByDepartment(department)
      // API 返回格式: {status: 200, data: [...]}
      const allDoctors = res.data || []
      allDoctorsInDepartment.value = allDoctors
      
      // 如果已经选择了日期，则过滤值班医生
      if (bookingForm.value.appointmentDate) {
        await loadOnDutyDoctors(String(bookingForm.value.appointmentDate), department)
      } else {
        doctors.value = allDoctors
      }
    } catch (error) {
      console.error('加载医生失败:', error)
    }
  }
}

// 加载值班医生
const loadOnDutyDoctors = async (date, department) => {
  if (!date || !department) {
    doctors.value = []
    return
  }
  try {
    const res = await getOnDutyDoctorsByDateAndDepartment(date, department)
    // API 返回格式: {status: 200, data: {code: "0", data: [...]}
    doctors.value = res.data?.data || []
  } catch (error) {
    console.error('加载值班医生失败:', error)
    // 如果获取值班医生失败，显示所有医生
    doctors.value = allDoctorsInDepartment.value || []
  }
}

// 预约日期变化时重新加载值班医生
const handleDateChange = async () => {
  bookingForm.value.doctorId = null
  
  if (bookingForm.value.appointmentDate && bookingForm.value.department) {
    await loadOnDutyDoctors(String(bookingForm.value.appointmentDate), bookingForm.value.department)
  } else if (bookingForm.value.department) {
    // 如果没有选择日期，显示科室所有医生
    doctors.value = allDoctorsInDepartment.value || []
  } else {
    doctors.value = []
  }
}

// 禁用过去的日期
const disabledDate = (time) => {
  return time.getTime() < Date.now() - 8.64e7
}

// 获取医生显示标签
const getDoctorLabel = (doctor) => {
  const name = doctor?.name || '未知医生'
  const position = doctor?.position || '医生'
  return `${name} (${position})`
}

// 提交预约
const submitBooking = async () => {
  if (!bookingForm.value.petId || !bookingForm.value.department || !bookingForm.value.appointmentDate) {
    ElMessage.error('请填写必填项')
    return
  }
  
  try {
    submitting.value = true
    const userId = localStorage.getItem('userId')
    
    const now = new Date()
    const hours = String(now.getHours()).padStart(2, '0')
    const minutes = String(now.getMinutes()).padStart(2, '0')
    const seconds = String(now.getSeconds()).padStart(2, '0')
    const currentTime = `${hours}:${minutes}:${seconds}`
    
    const appointmentData = {
      userId: Number(userId),
      petId: bookingForm.value.petId,
      doctorId: bookingForm.value.doctorId,
      appointmentDate: bookingForm.value.appointmentDate,
      appointmentTime: currentTime,
      reason: bookingForm.value.reason || '',
      department: bookingForm.value.department
    }
    
    await createAppointment(appointmentData)
    
    // 关闭弹窗并重置表单
    bookingDialogVisible.value = false
    bookingForm.value = {
      petId: '',
      department: '',
      doctorId: null,
      appointmentDate: '',
      reason: ''
    }
    
    // 重新加载预约列表
    await loadUserAppointments()
    
    ElMessage.success('预约提交成功！')
  } catch (error) {
    console.error('预约提交失败:', error)
    console.error('错误响应:', error.response?.data)
    ElMessage.error('预约提交失败，请重试')
  } finally {
    submitting.value = false
  }
}

// 取消预约
const handleCancelAppointment = async (appointmentId) => {
  try {
    await cancelAppointment(appointmentId)
    // 重新加载预约列表
    await loadUserAppointments()
    ElMessage.success('预约已取消')
  } catch (error) {
    console.error('取消预约失败:', error)
    ElMessage.error('取消预约失败，请重试')
  }
}

// 查看预约详情
const viewAppointmentDetail = (appointment) => {
  selectedAppointment.value = appointment
  detailDialogVisible.value = true
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    'pending': '待取号',
    'waiting': '待就诊',
    'in_progress': '正在就诊',
    'completed': '已完成',
    'cancelled': '已取消'
  }
  return statusMap[status] || '未知状态'
}

// 初始化
onMounted(async () => {
  await loadDepartments()
  await loadUserAppointments()
})

// 监听宠物列表变化，更新预约列表中的宠物名称
watch(() => props.pets, () => {
  appointments.value = appointments.value.map(appointment => ({
    ...appointment,
    petName: props.pets.find(pet => pet.id === appointment.petId)?.name || '未知宠物',
    petSpecies: props.pets.find(pet => pet.id === appointment.petId)?.species || '',
    petBreed: props.pets.find(pet => pet.id === appointment.petId)?.breed || ''
  }))
}, { deep: true })
</script>

<style scoped>
.appointment-wrapper {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.appointment-card {
  flex: 1;
  display: flex;
  flex-direction: column;
  margin-top: auto;
}

.appointment-card :deep(.el-card__body) {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.appointment-card :deep(.el-table) {
  flex: 1;
}

.pagination-container {
  display: flex;
  justify-content: center;
  padding: 20px 0;
}

.detail-label {
  width: 100px;
  text-align: right;
}

:deep(.detail-label) {
  width: 100px;
}

:deep(.el-descriptions__label) {
  width: 100px;
  min-width: 100px;
}
</style>