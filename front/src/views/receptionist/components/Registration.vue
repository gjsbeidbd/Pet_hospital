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
            <span style="font-weight: bold;">{{ foundUser.name }}</span> (余额: ￥{{ foundUser.balance }})
            <br>
            关联宠物:
            <el-radio-group v-model="selectedPet" size="small">
              <el-radio-button label="旺财(狗)"></el-radio-button>
              <el-radio-button label="小黑(猫)"></el-radio-button>
            </el-radio-group>
          </div>

          <el-form-item label="挂号科室">
            <el-select v-model="form.dept" placeholder="请选择科室">
              <el-option label="全科门诊" value="all"></el-option>
              <el-option label="外科" value="wai"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="指定医生">
            <el-select v-model="form.doctor" placeholder="可不选 (随机分配)">
              <el-option label="王医生 (主任)" value="wang"></el-option>
              <el-option label="李医生" value="li"></el-option>
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
          <el-table-column prop="doctor" label="预约医生"></el-table-column>
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
                <el-select v-model="form.dept" placeholder="请选择科室">
                  <el-option label="全科门诊" value="all"></el-option>
                  <el-option label="外科" value="wai"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="指定医生">
                <el-select v-model="form.doctor" placeholder="可不选 (随机分配)">
                  <el-option label="王医生 (主任)" value="wang"></el-option>
                  <el-option label="李医生" value="li"></el-option>
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
import { ref, reactive, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'

const emit = defineEmits(['registration-complete', 'check-in'])

// 控制活动标签页
const activeTab = ref('walkin')

// 挂号表单数据
const searchPhone = ref('')
const foundUser = ref(null)
const selectedPet = ref('')
const form = reactive({ dept: 'all', doctor: '' })

// 注册步骤
const regStep = ref(1)

// 表单数据
const registerForm = reactive({ name: '', phone: '', password: '' })

// 动态品种数据 (与用户端保持一致)
const breedOptionsMap = {
  '狗': ['金毛寻回犬', '泰迪/贵宾犬', '柴犬', '柯基', '拉布拉多'],
  '猫': ['英短蓝猫', '布偶猫', '暹罗猫', '加菲猫', '中华田园猫'],
  '其他': ['兔子', '仓鼠', '鸟类', '爬行动物']
}

// 注册/新建档案共用的宠物信息
const petForm = reactive({
  name: '',
  species: '狗',
  breed: '',
  age: 1
})

const currentBreedOptions = computed(() => {
  return breedOptionsMap[petForm.species] || []
})

// 模拟数据：预约列表
const appointmentList = ref([
  { time: '10:30', name: '陈女士', pet: '巧克力', doctor: '王医生', status: '待取号' },
  { time: '11:00', name: '周先生', pet: '大黄', doctor: '李医生', status: '待取号' }
])

// 过滤掉已取号的预约
const filteredAppointmentList = computed(() => {
  return appointmentList.value.filter(item => item.status === '待取号')
})

// 方法
// 现场挂号 - 搜索用户
const mockSearchUser = () => {
  if(!searchPhone.value) return ElMessage.warning('请输入手机号')
  foundUser.value = { name: '张先生', balance: '200.00' }
  ElMessage.success('已读取用户信息')
}

// 现场挂号 - 确认挂号
const handleRegister = () => {
  if(!foundUser.value) return ElMessage.error('请先查询用户')
  ElMessage.success('挂号成功！排队号：A007，正在打印小票...')
  foundUser.value = null
  searchPhone.value = ''
}

// 注册用户 - 完成注册并挂号
const handleFullRegistration = () => {
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
  Object.assign(form, { dept: 'all', doctor: '' })
  regStep.value = 1
}

// 预约取号处理
const handleCheckIn = (row) => {
  // 更新预约状态为待就诊
  row.status = '待就诊'
  
  // 发送取号事件，通知工作台更新
  emit('check-in', {
    time: row.time,
    name: row.name,
    pet: row.pet,
    doctor: row.doctor,
    status: '待就诊'
  })
  
  ElMessage.success('取号成功，已自动加入候诊队列')
}
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