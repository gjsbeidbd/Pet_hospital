<template>
  <div class="registration-wrapper">
    <el-tabs v-model="activeTab" type="border-card" class="registration-tabs">
      <el-tab-pane label="预约取号 (Check-in)" name="checkin">
        <el-table :data="filteredAppointmentList" border style="width: 100%">
          <el-table-column prop="time" label="预约时间" width="160"></el-table-column>
          <el-table-column prop="name" label="客户" width="100"></el-table-column>
          <el-table-column prop="pet" label="宠物" width="100"></el-table-column>
          <el-table-column prop="species" label="种类" width="80"></el-table-column>
          <el-table-column prop="breed" label="品种" width="100"></el-table-column>
          <el-table-column prop="doctor" label="预约医生" width="120"></el-table-column>
          <el-table-column prop="department" label="科室" width="100"></el-table-column>
          <el-table-column prop="status" label="状态">
            <template #default="scope">
              <el-tag :type="scope.row.status === '待取号' ? 'info' : 'success'">{{ scope.row.status }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作">
            <template #default="scope">
              <el-button v-if="scope.row.status === '待取号'" type="success" size="small" @click="handleCheckIn(scope.row)">取号</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="现场挂号 (Walk-in)" name="walkin">
        <el-form label-width="100px" style="max-width: 700px; margin-top: 20px;">
          <el-form-item label="搜索用户">
            <el-input
              v-model="userSearchKeyword"
              placeholder="输入手机号或姓名搜索用户"
              clearable
              @input="handleUserSearch"
              style="width: 100%;">
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <div v-if="selectedUser" style="background: #f5f7fa; padding: 15px; margin-bottom: 20px; border-radius: 4px;">
            <div style="margin-bottom: 10px;">
              <span style="font-weight: bold; font-size: 16px;">{{ selectedUser.name }}</span>
              <span style="color: #909399; margin-left: 10px;">手机号: {{ selectedUser.phone }}</span>
            </div>
            <div style="color: #606266;">关联宠物:</div>
          </div>

          <el-form-item label="选择宠物">
            <el-select
              v-model="selectedPetId"
              placeholder="请选择宠物"
              :disabled="!selectedUser"
              style="width: 70%;">
              <el-option
                v-for="pet in userPetList"
                :key="pet.id"
                :label="`${pet.name} (${pet.species} - ${pet.breed || '未填写'})`"
                :value="pet.id">
                <span style="float: left">{{ pet.name }}</span>
                <span style="float: right; color: #8492a6; font-size: 13px">{{ pet.species }} - {{ pet.breed || '未填写' }}</span>
              </el-option>
            </el-select>
            <el-button type="primary" plain @click="showAddPetDialog = true" :disabled="!selectedUser" style="margin-left: 10px;">新建宠物</el-button>
          </el-form-item>

          <el-form-item label="挂号科室">
            <el-select v-model="form.dept" placeholder="请选择科室" @change="handleDepartmentChange" style="width: 100%;">
              <el-option v-for="dept in availableDepartments" :key="dept.id" :label="dept.name" :value="dept.name"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="指定医生">
            <el-select v-model="form.doctor" placeholder="可不选 (随机分配)" :disabled="!form.dept" style="width: 100%;">
              <el-option v-for="doctor in doctors" :key="doctor.id" :label="`${doctor.name || ''} (${doctor.position || '医生'})`" :value="doctor.id"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleRegister" :disabled="!selectedUser || !selectedPetId || !form.dept">确认挂号并打印小票</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>

      <!-- 新建宠物对话框 -->
      <el-dialog v-model="showAddPetDialog" title="新建宠物档案" width="500px">
        <el-form :model="newPetForm" label-width="80px">
          <el-form-item label="宠物名称">
            <el-input v-model="newPetForm.name" placeholder="请输入宠物名称"></el-input>
          </el-form-item>
          <el-form-item label="种类">
            <el-select v-model="newPetForm.species" placeholder="请选择种类" style="width: 100%" @change="onNewPetSpeciesChange">
              <el-option
                v-for="species in speciesList"
                :key="species.id"
                :label="species.speciesName"
                :value="species.speciesName">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="品种">
            <el-select v-model="newPetForm.breed" placeholder="请选择品种" style="width: 100%" filterable :disabled="!newPetForm.species">
              <el-option
                v-for="breed in newPetBreedList"
                :key="breed.id"
                :label="breed.breedName"
                :value="breed.breedName">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="年龄">
            <el-input-number v-model="newPetForm.age" :min="0" :max="30"></el-input-number>
          </el-form-item>
          <el-form-item label="体重(kg)">
            <el-input-number v-model="newPetForm.weight" :min="0" :precision="1"></el-input-number>
          </el-form-item>
          <el-form-item label="性别">
            <el-select v-model="newPetForm.gender" placeholder="请选择性别" style="width: 100%">
              <el-option label="雄性" value="雄性"></el-option>
              <el-option label="雌性" value="雌性"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="病史">
            <el-input
              v-model="newPetForm.medicalHistory"
              type="textarea"
              placeholder="请输入宠物病史"
              :rows="3">
            </el-input>
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="showAddPetDialog = false">取消</el-button>
          <el-button type="primary" @click="handleAddPet">确认添加</el-button>
        </template>
      </el-dialog>

    </el-tabs>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { getAllUsers, getUserPets, getDepartments, getPetSpecies, getPetBreedsBySpeciesId, addPet, getReceptionistInfo, getAllAppointmentsByDepartment, takeNumber, createAppointment, getOnDutyDoctorsByDateAndDepartment } from '@/services/api'

const emit = defineEmits(['registration-complete', 'check-in'])

// 控制活动标签页
const activeTab = ref('checkin')

// 现场挂号 - 用户和宠物选择
const allUsers = ref([])
const userSearchKeyword = ref('')
const selectedUser = ref(null)
const userPetList = ref([])
const selectedPetId = ref('')
const form = reactive({ dept: '', doctor: '' })

// 新建宠物对话框
const showAddPetDialog = ref(false)
const newPetForm = reactive({
  name: '',
  species: '',
  breed: '',
  age: null,
  weight: null,
  gender: '雄性',
  medicalHistory: ''
})

// 宠物种类和品种数据（用于新建宠物对话框）
const speciesList = ref([])
const newPetBreedList = ref([])

const departments = ref([])
const doctors = ref([])
const receptionistInfo = ref(null)

const availableDepartments = computed(() => {
  if (receptionistInfo?.department) {
    return departments.value.filter(dept => dept.name === receptionistInfo.department)
  }
  return departments.value
})

// 动态预约列表数据
const appointmentList = ref([])

// 过滤掉已取号的预约（只显示当前前台所在科室的预约）
const filteredAppointmentList = computed(() => {
  if (!receptionistInfo?.department) {
    return appointmentList.value.filter(item => item.status === '待取号')
  }
  return appointmentList.value.filter(item => 
    item.status === '待取号' && item.department === receptionistInfo.department
  )
})

// 加载科室数据
const loadDepartments = async () => {
  try {
    const res = await getDepartments()
    departments.value = res.data || []
  } catch (error) {
    console.error('加载科室失败:', error)
  }
}

// 根据科室加载医生
const loadDoctors = async (department) => {
  try {
    const today = new Date().toISOString().split('T')[0]
    const res = await getOnDutyDoctorsByDateAndDepartment(today, department)
    doctors.value = res.data?.data || []
  } catch (error) {
    console.error('加载值班医生失败:', error)
    doctors.value = []
  }
}

// 科室变化时重新加载医生
const handleDepartmentChange = (department) => {
  form.doctor = ''
  if (department) {
    loadDoctors(department)
  } else {
    doctors.value = []
  }
}

// 加载宠物种类和品种数据
const loadPetData = async () => {
  try {
    const speciesRes = await getPetSpecies()
    speciesList.value = speciesRes.data || []
  } catch (error) {
    console.error('加载宠物数据失败:', error)
  }
}

// 种类变化时获取对应品种（用于新建宠物对话框）
const onNewPetSpeciesChange = async () => {
  newPetForm.breed = ''
  if (newPetForm.species) {
    try {
      const species = speciesList.value.find(s => s.speciesName === newPetForm.species)
      if (species) {
        const breedsRes = await getPetBreedsBySpeciesId(species.id)
        newPetBreedList.value = breedsRes.data || []
      }
    } catch (error) {
      console.error('加载品种列表失败:', error)
      newPetBreedList.value = []
    }
  } else {
    newPetBreedList.value = []
  }
}

// 方法
// 加载所有用户列表
const loadUsers = async () => {
  try {
    const res = await getAllUsers()
    allUsers.value = (res.data || []).filter(u => u.role === 'USER')
  } catch (error) {
    console.error('加载用户列表失败:', error)
  }
}

// 用户搜索处理
const handleUserSearch = () => {
  if (!userSearchKeyword.value) {
    selectedUser.value = null
    userPetList.value = []
    selectedPetId.value = ''
    return
  }
  
  const keyword = userSearchKeyword.value.toLowerCase()
  const foundUser = allUsers.value.find(u => 
    u.phone.toLowerCase().includes(keyword) || 
    u.name.toLowerCase().includes(keyword)
  )
  
  if (foundUser) {
    selectedUser.value = foundUser
    // 加载用户宠物
    loadUserPets(foundUser.id)
  } else {
    selectedUser.value = null
    userPetList.value = []
    selectedPetId.value = ''
  }
}

// 加载用户宠物
const loadUserPets = async (userId) => {
  selectedPetId.value = ''
  try {
    const res = await getUserPets(userId)
    userPetList.value = res.data || []
    if (userPetList.value.length > 0) {
      selectedPetId.value = userPetList.value[0].id
    }
  } catch (error) {
    console.error('加载用户宠物失败:', error)
    userPetList.value = []
  }
}

// 新建宠物
const handleAddPet = async () => {
  if (!newPetForm.name) {
    return ElMessage.warning('请填写宠物昵称')
  }
  try {
    const petData = {
      userId: selectedUser.value.id,
      name: newPetForm.name,
      species: newPetForm.species,
      breed: newPetForm.breed,
      age: newPetForm.age,
      weight: newPetForm.weight,
      medicalHistory: newPetForm.medicalHistory || '',
      gender: newPetForm.gender
    }
    await addPet(petData)
    ElMessage.success('宠物添加成功')

    // 重新加载用户宠物列表
    const res = await getUserPets(selectedUser.value.id)
    userPetList.value = res.data || []

    // 自动选择新建的宠物
    if (userPetList.value.length > 0) {
      selectedPetId.value = userPetList.value[userPetList.value.length - 1].id
    }

    // 关闭对话框并重置表单
    showAddPetDialog.value = false
    Object.assign(newPetForm, { name: '', species: '', breed: '', age: null, weight: null, gender: '雄性', medicalHistory: '' })
    newPetBreedList.value = []
  } catch (error) {
    console.error('添加宠物失败:', error)
    ElMessage.error('添加宠物失败')
  }
}

// 现场挂号 - 确认挂号
const handleRegister = async () => {
  if (!selectedUser.value) return ElMessage.error('请先搜索并选择用户')
  if (!selectedPetId.value) return ElMessage.error('请选择宠物')
  if (!form.dept) return ElMessage.error('请选择科室')

  try {
    const now = new Date()
    const appointmentData = {
      userId: selectedUser.value.id,
      petId: selectedPetId.value,
      doctorId: form.doctor || null,
      department: form.dept,
      appointmentDate: now.toISOString().split('T')[0],
      appointmentTime: now.toTimeString().split(' ')[0],
      status: 'waiting',
      reason: '现场挂号'
    }

    await createAppointment(appointmentData)

    ElMessage.success('排号成功，待就诊')

    // 重置表单
    userSearchKeyword.value = ''
    selectedUser.value = null
    selectedPetId.value = ''
    userPetList.value = []
    form.dept = ''
    form.doctor = ''
  } catch (error) {
    console.error('挂号失败:', error)
    ElMessage.error('挂号失败')
  }
}

// 预约取号处理
const handleCheckIn = async (row) => {
  try {
    // 调用取号 API
    if (row.id) {
      await takeNumber(row.id)
    }
    
    // 重新加载预约数据
    await loadAppointments()
    
    // 发送取号事件，通知工作台更新
    emit('check-in', {
      time: row.time,
      name: row.name,
      pet: row.pet,
      doctor: row.doctor,
      status: '待就诊'
    })
    
    ElMessage.success('取号成功，已自动加入候诊队列')
  } catch (error) {
    console.error('取号失败:', error)
    ElMessage.error('取号失败，请重试')
  }
}

// 加载前台信息
const loadReceptionistInfo = async () => {
  try {
    const userId = localStorage.getItem('userId')
    if (userId) {
      const res = await getReceptionistInfo(userId)
      receptionistInfo.value = res.data
      // 加载预约数据
      await loadAppointments()
    }
  } catch (error) {
    console.error('加载前台信息失败:', error)
  }
}

// 加载预约数据
const loadAppointments = async () => {
  try {
    // 如果前台有指定科室，则只加载该科室的预约
    const department = receptionistInfo.value?.department
    console.log('前台科室信息:', receptionistInfo.value)
    console.log('请求科室:', department)
    let res
    if (department) {
      res = await getAllAppointmentsByDepartment(department)
      console.log('按科室加载预约:', res.data)
    } else {
      res = await getAllAppointments()
      console.log('加载所有预约:', res.data)
    }
    const appointments = res.data || []
    // 过滤出本科室的预约，并且只显示待取号、待就诊和正在就诊的预约
    const filteredAppointments = appointments.filter(app => 
      app.department === department && 
      (app.status === 'pending' || app.status === 'waiting' || app.status === 'in_progress')
    )
    // 将后端数据转换为前端需要的格式
    appointmentList.value = filteredAppointments.map(app => ({
      id: app.id,
      time: app.appointmentTime,
      name: app.userName || `用户 ${app.userId}`,
      pet: app.petName || `宠物 ${app.petId}`,
      species: app.petSpecies || '',
      breed: app.petBreed || '',
      doctor: app.doctorName || `医生 ${app.doctorId}`,
      status: app.status === 'pending' ? '待取号' : 
              app.status === 'waiting' ? '待就诊' : 
              app.status === 'in_progress' ? '正在就诊' : 
              app.status === 'pending_payment' ? '待缴费' : 
              app.status === 'completed' ? '就诊完成' : 
              app.status === 'cancelled' ? '已取消' : '未知',
      department: app.department || ''
    }))
  } catch (error) {
    console.error('加载预约数据失败:', error)
  }
}

// 初始化
onMounted(() => {
  loadDepartments()
  loadPetData()
  loadReceptionistInfo()
  loadUsers()
})

// 监听科室信息变化，重新加载预约数据
watch(() => receptionistInfo.value?.department, () => {
  loadAppointments()
})
</script>

<style scoped>
.registration-wrapper {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.registration-tabs {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.registration-tabs :deep(.el-tabs__content) {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.registration-tabs :deep(.el-tab-pane) {
  flex: 1;
  display: flex;
  flex-direction: column;
  height: 100%;
}

.registration-tabs :deep(.el-table) {
  flex: 1;
}
</style>