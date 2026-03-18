<template>
  <div class="registration-wrapper">
    <el-tabs v-model="activeTab" type="border-card" class="registration-tabs">
      <el-tab-pane label="现场挂号 (Walk-in)" name="walkin">
        <el-form label-width="100px" style="max-width: 600px; margin-top: 20px;">
          <el-form-item label="用户搜索">
            <el-input placeholder="输入手机号查询用户" v-model="searchPhone">
              <template #append>
                <el-button :icon="Search" @click="mockSearchUser">查询</el-button>
              </template>
            </el-input>
          </el-form-item>

          <div v-if="foundUser" style="background: #f4f4f5; padding: 10px; margin-bottom: 20px; border-radius: 4px;">
            <span style="font-weight: bold;">{{ foundUser.name }}</span> (手机号: {{ foundUser.phone }}, 余额: ￥{{ foundUser.balance }})
            <br>
            关联宠物:
            <el-radio-group v-model="selectedPet" size="small" v-if="userPets.length > 0">
              <el-radio-button v-for="pet in userPets" :key="pet.id" :label="pet.id">
                {{ pet.name }} ({{ pet.species }} - {{ pet.breed }})
              </el-radio-button>
            </el-radio-group>
            <span v-else style="color: #909399;">暂无宠物</span>
          </div>

          <el-form-item label="挂号科室">
            <el-select v-model="form.dept" placeholder="请选择科室" @change="handleDepartmentChange" :disabled="!!receptionistInfo?.department">
              <el-option v-for="dept in availableDepartments" :key="dept.id" :label="dept.name" :value="dept.name"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="指定医生">
            <el-select v-model="form.doctor" placeholder="可不选 (随机分配)" :disabled="!form.dept">
              <el-option v-for="doctor in doctors" :key="doctor.id" :label="`${doctor.name} (${doctor.title || '医生'})`" :value="doctor.id"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleRegister">确认挂号并打印小票</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>

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

      <el-tab-pane label="新用户注册并挂号" name="newuser">
        <el-form label-width="100px" style="max-width: 600px; margin: 20px auto 0;">
          <el-steps :active="regStep" finish-status="success" simple style="margin-bottom: 20px;">
            <el-step title="步骤 1: 登记用户信息" />
            <el-step title="步骤 2: 登记宠物信息" />
            <el-step title="步骤 3: 确认挂号" />
          </el-steps>

          <div v-if="regStep === 1">
            <el-form :model="registerForm" label-width="100px">
              <el-form-item label="客户姓名">
                <el-input v-model="registerForm.name" placeholder="必填"></el-input>
              </el-form-item>
              <el-form-item label="联系电话">
                <el-input v-model="registerForm.phone" placeholder="将作为登录账号"></el-input>
              </el-form-item>
              <el-form-item label="设置密码">
                <el-input v-model="registerForm.password" type="password" show-password></el-input>
              </el-form-item>
            </el-form>
            <div style="text-align: right;">
              <el-button type="primary" @click="regStep = 2" :disabled="!registerForm.name || !registerForm.phone">下一步</el-button>
            </div>
          </div>

          <div v-if="regStep === 2">
            <el-form :model="petForm" label-width="100px">
              <el-form-item label="宠物昵称">
                <el-input v-model="petForm.name" placeholder="必填"></el-input>
              </el-form-item>
              <el-form-item label="种类">
                <el-radio-group v-model="petForm.species" @change="petForm.breed = ''">
                  <el-radio label="狗">狗</el-radio>
                  <el-radio label="猫">猫</el-radio>
                  <el-radio label="其他">其他</el-radio>
                </el-radio-group>
              </el-form-item>
              <el-form-item label="品种">
                <el-select v-model="petForm.breed" placeholder="请选择品种" style="width: 100%" filterable>
                  <el-option v-for="breed in currentBreedOptions" :key="breed" :label="breed" :value="breed"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="年龄">
                <el-input-number v-model="petForm.age" :min="0" :max="30" style="width: 100px;"></el-input-number> 岁
              </el-form-item>
            </el-form>
            <div style="text-align: right;">
              <el-button @click="regStep = 1">上一步</el-button>
              <el-button type="primary" @click="regStep = 3" :disabled="!petForm.name || !petForm.breed">下一步</el-button>
            </div>
          </div>

          <div v-if="regStep === 3">
            <el-alert title="用户信息和宠物档案已创建完成，请选择挂号信息。" type="success" show-icon style="margin-bottom: 20px;"></el-alert>
            <el-form label-width="100px">
              <el-form-item label="本次就诊宠物">
                <el-tag type="success">{{ petForm.name }} ({{ petForm.breed }})</el-tag>
              </el-form-item>
              <el-form-item label="挂号科室">
                <el-select v-model="form.dept" placeholder="请选择科室" @change="handleDepartmentChange" :disabled="!!receptionistInfo?.department">
                  <el-option v-for="dept in availableDepartments" :key="dept.id" :label="dept.name" :value="dept.name"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="指定医生">
                <el-select v-model="form.doctor" placeholder="可不选 (随机分配)" :disabled="!form.dept">
                  <el-option v-for="doctor in doctors" :key="doctor.id" :label="`${doctor.name} (${doctor.title || '医生'})`" :value="doctor.id"></el-option>
                </el-select>
              </el-form-item>
            </el-form>
            <div style="text-align: right;">
              <el-button @click="regStep = 2">上一步</el-button>
              <el-button type="success" @click="handleFullRegistration">确认挂号并完成注册</el-button>
            </div>
          </div>
        </el-form>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { getAllUsers, getUserPets, getDepartments, getDoctorsByDepartment, getPetSpecies, getPetBreedsBySpeciesId, registerUser, addPet, getReceptionistInfo, getAllAppointmentsByDepartment, takeNumber } from '@/services/api'

const emit = defineEmits(['registration-complete', 'check-in'])

// 控制活动标签页
const activeTab = ref('walkin')

// 挂号表单数据
const searchPhone = ref('')
const foundUser = ref(null)
const userPets = ref([])
const selectedPet = ref('')
const form = reactive({ dept: '', doctor: '' })

// 动态数据
const departments = ref([])
const doctors = ref([])
const receptionistInfo = ref(null)

// 注册步骤
const regStep = ref(1)

// 表单数据
const registerForm = reactive({ name: '', phone: '', password: '' })

// 宠物种类和品种数据
const petSpecies = ref([])
const petBreeds = ref([])

// 注册/新建档案共用的宠物信息
const petForm = reactive({
  name: '',
  species: '狗',
  breed: '',
  age: 1
})

const currentBreedOptions = computed(() => {
  const species = petSpecies.value.find(s => s.speciesName === petForm.species)
  if (!species) return []
  return petBreeds.value.filter(b => b.speciesId === species.id).map(b => b.breedName)
})

// 可用科室（如果前台有指定科室，则只显示该科室）
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
    const res = await getDoctorsByDepartment(department)
    doctors.value = res.data || []
  } catch (error) {
    console.error('加载医生失败:', error)
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
    const [speciesRes, breedsRes] = await Promise.all([
      getPetSpecies(),
      getPetBreedsBySpeciesId('')
    ])
    petSpecies.value = speciesRes.data || []
    petBreeds.value = breedsRes.data || []
  } catch (error) {
    console.error('加载宠物数据失败:', error)
  }
}

// 方法
// 现场挂号 - 搜索用户
const mockSearchUser = async () => {
  if(!searchPhone.value) return ElMessage.warning('请输入手机号')
  
  try {
    const res = await getAllUsers()
    const users = res.data || []
    const user = users.find(u => u.phone === searchPhone.value && u.role === 'USER')
    
    if (user) {
      foundUser.value = {
        id: user.id,
        name: user.name,
        phone: user.phone,
        balance: user.balance || '0.00'
      }
      
      // 加载用户的宠物
      const petsRes = await getUserPets(user.id)
      userPets.value = petsRes.data || []
      
      if (userPets.value.length > 0) {
        selectedPet.value = userPets.value[0].id
      } else {
        selectedPet.value = ''
      }
      
      ElMessage.success('已读取用户信息')
    } else {
      foundUser.value = null
      userPets.value = []
      selectedPet.value = ''
      ElMessage.error('未找到该用户')
    }
  } catch (error) {
    console.error('查询用户失败:', error)
    ElMessage.error('查询用户失败')
  }
}

// 现场挂号 - 确认挂号
const handleRegister = () => {
  if(!foundUser.value) return ElMessage.error('请先查询用户')
  if(!selectedPet.value) return ElMessage.error('请选择宠物')
  if(!form.dept) return ElMessage.error('请选择科室')
  
  ElMessage.success('挂号成功！排队号：A007，正在打印小票...')
  foundUser.value = null
  searchPhone.value = ''
  userPets.value = []
  selectedPet.value = ''
  form.dept = ''
  form.doctor = ''
}

// 注册用户 - 完成注册并挂号
const handleFullRegistration = async () => {
  try {
    // 注册用户
    const userData = {
      phone: registerForm.phone,
      email: '',
      password: registerForm.password,
      name: registerForm.name,
      address: '',
      role: 'USER'
    }
    
    const userRes = await registerUser(userData)
    const userId = userRes.data.id
    
    // 添加宠物
    const petData = {
      userId: userId,
      name: petForm.name,
      species: petForm.species,
      breed: petForm.breed,
      age: petForm.age,
      weight: 0,
      medicalHistory: ''
    }
    await addPet(petData)
    
    ElMessage.success(`注册成功！客户：${registerForm.name}，宠物：${petForm.name}。已完成挂号，请等待叫号。`)
    emit('registration-complete', {
      userName: registerForm.name,
      petName: petForm.name,
      dept: form.dept,
      doctor: form.doctor
    })
    
    // 重置表单
    Object.assign(registerForm, { name: '', phone: '', password: '' })
    Object.assign(petForm, { name: '', species: '狗', breed: '', age: 1 })
    Object.assign(form, { dept: '', doctor: '' })
    regStep.value = 1
  } catch (error) {
    console.error('注册失败:', error)
    ElMessage.error('注册失败')
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