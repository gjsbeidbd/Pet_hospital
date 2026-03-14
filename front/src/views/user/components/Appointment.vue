<template>
  <div class="appointment-wrapper">
    <el-card class="appointment-card">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>在线预约挂号</span>
          <el-button type="primary" @click="bookingDialogVisible = true">发起新预约</el-button>
        </div>
      </template>

      <el-table :data="paginatedAppointments" stripe style="width: 100%">
        <el-table-column prop="date" label="预约时间" width="180"></el-table-column>
        <el-table-column prop="petName" label="就诊宠物" width="120"></el-table-column>
        <el-table-column prop="doctor" label="预约医生" width="120"></el-table-column>
        <el-table-column prop="desc" label="病情描述"></el-table-column>
        <el-table-column prop="status" label="当前状态" width="120">
          <template #default="scope">
            <el-tag v-if="scope.row.status === '待取号'" type="info">待取号</el-tag>
            <el-tag v-else-if="scope.row.status === '待就诊'" type="warning">待就诊</el-tag>
            <el-tag v-else-if="scope.row.status === '就诊中'" type="primary">就诊中</el-tag>
            <el-tag v-else-if="scope.row.status === '就诊完成'" type="success">就诊完成</el-tag>
            <el-tag v-else type="info">未知状态</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="scope">
            <el-button v-if="scope.row.status === '待就诊'" type="danger" link size="small">取消预约</el-button>
            <el-button v-else type="primary" link size="small" disabled>查看详情</el-button>
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
    <el-form label-width="100px" :model="bookingForm">
      <el-form-item label="选择宠物">
        <el-select v-model="bookingForm.petId" placeholder="请选择就诊宠物">
          <el-option
            v-for="pet in pets"
            :key="pet.id"
            :label="`${pet.name} (${pet.breed})`"
            :value="pet.id">
          </el-option>
        </el-select>
      </el-form-item>
      
      <el-form-item label="预约科室">
        <el-select v-model="bookingForm.department" placeholder="请选择科室">
          <el-option label="全科门诊" value="general"></el-option>
          <el-option label="外科" value="surgery"></el-option>
          <el-option label="牙科" value="dentistry"></el-option>
          <el-option label="眼科" value="ophthalmology"></el-option>
          <el-option label="皮肤科" value="dermatology"></el-option>
        </el-select>
      </el-form-item>
      
      <el-form-item label="指定医生">
        <el-select v-model="bookingForm.doctor" placeholder="可不选 (随机分配)">
          <el-option label="王医生 (主任)" value="wang"></el-option>
          <el-option label="李医生 (副主任)" value="li"></el-option>
          <el-option label="张医生 (主治)" value="zhang"></el-option>
        </el-select>
      </el-form-item>
      
      <el-form-item label="预约时间">
        <el-date-picker
          v-model="bookingForm.appointmentTime"
          type="datetime"
          placeholder="请选择预约时间"
          format="YYYY-MM-DD HH:mm"
          value-format="YYYY-MM-DD HH:mm"
          :disabledDate="disabledDate"
          :disabledHours="disabledHours"
          :disabledMinutes="disabledMinutes">
        </el-date-picker>
      </el-form-item>
      
      <el-form-item label="病情描述">
        <el-input
          v-model="bookingForm.description"
          type="textarea"
          placeholder="请简要描述宠物的症状或就诊原因"
          :rows="3">
        </el-input>
      </el-form-item>
    </el-form>
    
    <template #footer>
      <el-button @click="bookingDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="submitBooking">确认预约</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, defineProps, computed } from 'vue'
import { ElMessage } from 'element-plus'

// 定义props
const props = defineProps({
  appointments: {
    type: Array,
    required: true
  },
  pets: {
    type: Array,
    required: true
  }
})

// 分页相关数据
const currentPage = ref(1)
const pageSize = ref(10)

// 计算当前页的预约数据
const paginatedAppointments = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return props.appointments.slice(start, end)
})

// 分页事件处理
const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
}

const handleCurrentChange = (val) => {
  currentPage.value = val
}

// 弹窗控制
const bookingDialogVisible = ref(false)

// 预约表单数据
const bookingForm = ref({
  petId: '',
  department: '',
  doctor: '',
  appointmentTime: '',
  description: ''
})

// 禁用过去的日期
const disabledDate = (time) => {
  return time.getTime() < Date.now() - 8.64e7
}

// 禁用小时 (工作时间 8:00-18:00)
const disabledHours = () => {
  return [
    0, 1, 2, 3, 4, 5, 6, 7,
    18, 19, 20, 21, 22, 23
  ]
}

// 禁用分钟 (只允许 00 和 30 分)
const disabledMinutes = (hour) => {
  if (hour >= 8 && hour < 18) {
    // 工作时间内每30分钟一个时段
    return []
  } else {
    // 非工作时间禁用所有分钟
    return Array.from({ length: 60 }, (_, i) => i)
  }
}

// 提交预约
const submitBooking = () => {
  if (!bookingForm.value.petId || !bookingForm.value.department || !bookingForm.value.appointmentTime) {
    ElMessage.error('请填写必填项')
    return
  }
  
  // 获取选中的宠物名称
  const selectedPet = props.pets.find(pet => pet.id === bookingForm.value.petId)
  
  // 构造预约对象
  const newAppointment = {
    date: bookingForm.value.appointmentTime,
    petName: selectedPet ? selectedPet.name : '',
    doctor: bookingForm.value.doctor || '随机分配',
    desc: bookingForm.value.description || '无描述',
    status: '待取号'
  }
  
  // 这里应该调用API提交预约，现在只是模拟
  console.log('提交预约:', newAppointment)
  
  // 关闭弹窗并重置表单
  bookingDialogVisible.value = false
  bookingForm.value = {
    petId: '',
    department: '',
    doctor: '',
    appointmentTime: '',
    description: ''
  }
  
  ElMessage.success('预约提交成功！')
}
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
</style>