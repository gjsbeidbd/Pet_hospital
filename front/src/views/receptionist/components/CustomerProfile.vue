<template>
  <div class="customer-profile-wrapper">
    <el-card class="customer-profile-card">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>客户/宠物档案管理</span>
          <div style="display: flex; gap: 10px;">
            <el-input placeholder="请输入手机号/宠物名/客户名" style="width: 300px;" v-model="searchKeyword"></el-input>
            <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
            <el-button type="success" :icon="Plus" @click="openNewProfileDialog">新建档案</el-button>
          </div>
        </div>
      </template>
      
      <el-table :data="paginatedCustomerList" border>
        <el-table-column prop="id" label="ID" width="60"></el-table-column>
        <el-table-column prop="name" label="客户姓名" width="120"></el-table-column>
        <el-table-column prop="phone" label="联系电话" width="150"></el-table-column>
        <el-table-column prop="pets" label="名下宠物">
          <template #default="scope">
            <el-tag size="small" v-for="p in scope.row.pets" :key="p" style="margin-right: 5px;">{{ p }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="regDate" label="注册日期"></el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="scope">
            <el-button link type="primary" size="small" @click="openEditProfileDialog(scope.row)">编辑</el-button>
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
          :total="customerList.length"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :pager-count="5"
          prev-text="上一页"
          next-text="下一页"
          :page-count="Math.ceil(customerList.length / pageSize)"
          :locale="{
            total: `共 {total} 条记录`,
            pagesize: '条/页',
            goto: '跳至',
            pageClassifier: '页'
          }"
        />
      </div>
    </el-card>

    <!-- 弹窗：新建档案 -->
    <el-dialog v-model="newProfileDialogVisible" title="新建客户与宠物档案" width="700px">
      <el-form label-width="80px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-card header="客户信息" shadow="never">
              <el-form-item label="头像">
                <el-upload
                  class="avatar-uploader"
                  action="/api/upload"
                  :show-file-list="false"
                  :on-success="handleAvatarSuccess"
                  :before-upload="beforeAvatarUpload"
                >
                  <img v-if="customerImageUrl" :src="customerImageUrl" class="avatar" />
                  <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
                </el-upload>
              </el-form-item>
              <el-form-item label="账号">
                <el-input v-model="customerForm.phone" placeholder="请输入手机号">
                  <template #append>
                    <el-tooltip content="手机号就是登录账号" placement="top">
                      <i class="el-icon-question"></i>
                    </el-tooltip>
                  </template>
                </el-input>
              </el-form-item>
              <el-form-item label="真实姓名">
                <el-input v-model="customerForm.name" placeholder="请输入真实姓名"></el-input>
              </el-form-item>
              <el-form-item label="邮箱">
                <el-input v-model="customerForm.email" placeholder="请输入邮箱"></el-input>
              </el-form-item>
              <el-form-item label="地址">
                <el-input v-model="customerForm.address" placeholder="请输入地址"></el-input>
              </el-form-item>
              <el-form-item label="设置密码">
                <el-input v-model="customerForm.password" type="password" show-password placeholder="请输入密码"></el-input>
              </el-form-item>
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card header="宠物信息" shadow="never">
              <div v-if="newPetForms.length === 0" style="text-align: center; padding: 20px;">
                <p>暂无宠物信息</p>
                <el-button type="primary" @click="addNewPetForm">添加宠物</el-button>
              </div>
              <div v-else>
                <div v-for="(petForm, index) in newPetForms" :key="index" style="margin-bottom: 15px; border: 1px dashed #ccc; border-radius: 4px;">
                  <div @click="toggleNewPetForm(index)" style="padding: 10px; cursor: pointer; background-color: #f5f7fa; border-bottom: 1px solid #ebeef5;">
                    <div style="display: flex; justify-content: space-between; align-items: center;">
                      <h4>宠物 {{ index + 1 }}: {{ petForm.name || '未命名' }}</h4>
                      <el-button type="danger" link @click.stop="removeNewPetForm(index)">删除</el-button>
                    </div>
                  </div>
                  <el-collapse-transition>
                    <div v-show="!newPetFormCollapsed[index]" style="padding: 10px;">
                      <el-form-item label="昵称">
                        <el-input v-model="petForm.name" placeholder="宠物名字"></el-input>
                      </el-form-item>
                      <el-form-item label="种类">
                        <el-radio-group v-model="petForm.species">
                          <el-radio label="狗">狗</el-radio>
                          <el-radio label="猫">猫</el-radio>
                          <el-radio label="其他">其他</el-radio>
                        </el-radio-group>
                      </el-form-item>
                      <el-form-item label="品种">
                        <el-select v-model="petForm.breed" placeholder="请选择品种" style="width: 100%" filterable>
                          <el-option v-for="breed in getBreedOptions(petForm.species)" :key="breed" :label="breed" :value="breed"></el-option>
                        </el-select>
                      </el-form-item>
                      <el-form-item label="年龄(岁)">
                        <el-input-number v-model="petForm.age" :min="0" :max="30" placeholder="宠物年龄"></el-input-number>
                      </el-form-item>
                      <el-form-item label="体重(kg)">
                        <el-input-number v-model="petForm.weight" :min="0" :precision="1" placeholder="宠物体重"></el-input-number>
                      </el-form-item>
                      <el-form-item label="病史">
                        <el-input v-model="petForm.medicalHistory" type="textarea" placeholder="请输入宠物病史" :rows="3"></el-input>
                      </el-form-item>
                    </div>
                  </el-collapse-transition>
                </div>
                <el-button type="primary" @click="addNewPetForm">继续添加宠物</el-button>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="newProfileDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCreateProfile">确认创建</el-button>
      </template>
    </el-dialog>

    <!-- 弹窗：编辑档案 -->
    <el-dialog v-model="editProfileDialogVisible" title="编辑客户与宠物档案" width="700px">
      <el-form label-width="80px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-card header="客户信息" shadow="never">
              <el-form-item label="头像">
                <el-upload
                  class="avatar-uploader"
                  action="/api/upload"
                  :show-file-list="false"
                  :on-success="handleAvatarSuccess"
                  :before-upload="beforeAvatarUpload"
                >
                  <img v-if="editCustomerImageUrl" :src="editCustomerImageUrl" class="avatar" />
                  <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
                </el-upload>
              </el-form-item>
              <el-form-item label="账号">
                <el-input v-model="editCustomerForm.phone" disabled>
                  <template #append>
                    <el-tooltip content="手机号就是登录账号" placement="top">
                      <i class="el-icon-question"></i>
                    </el-tooltip>
                  </template>
                </el-input>
              </el-form-item>
              <el-form-item label="真实姓名">
                <el-input v-model="editCustomerForm.name" placeholder="请输入真实姓名"></el-input>
              </el-form-item>
              <el-form-item label="邮箱">
                <el-input v-model="editCustomerForm.email" placeholder="请输入邮箱"></el-input>
              </el-form-item>
              <el-form-item label="地址">
                <el-input v-model="editCustomerForm.address" placeholder="请输入地址"></el-input>
              </el-form-item>
              <el-form-item label="设置密码">
                <el-input v-model="editCustomerForm.password" type="password" show-password placeholder="留空则不修改密码"></el-input>
              </el-form-item>
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card header="宠物信息" shadow="never">
              <div v-if="editPetForms.length === 0" style="text-align: center; padding: 20px;">
                <p>暂无宠物信息</p>
                <el-button type="primary" @click="addEditPetForm">添加宠物</el-button>
              </div>
              <div v-else>
                <div v-for="(petForm, index) in editPetForms" :key="index" style="margin-bottom: 15px; border: 1px dashed #ccc; border-radius: 4px;">
                  <div @click="toggleEditPetForm(index)" style="padding: 10px; cursor: pointer; background-color: #f5f7fa; border-bottom: 1px solid #ebeef5;">
                    <div style="display: flex; justify-content: space-between; align-items: center;">
                      <h4>宠物 {{ index + 1 }}: {{ petForm.name || '未命名' }}</h4>
                      <el-button type="danger" link @click.stop="removeEditPetForm(index)">删除</el-button>
                    </div>
                  </div>
                  <el-collapse-transition>
                    <div v-show="!editPetFormCollapsed[index]" style="padding: 10px;">
                      <el-form-item label="昵称">
                        <el-input v-model="petForm.name" placeholder="宠物名字"></el-input>
                      </el-form-item>
                      <el-form-item label="种类">
                        <el-radio-group v-model="petForm.species">
                          <el-radio label="狗">狗</el-radio>
                          <el-radio label="猫">猫</el-radio>
                          <el-radio label="其他">其他</el-radio>
                        </el-radio-group>
                      </el-form-item>
                      <el-form-item label="品种">
                        <el-select v-model="petForm.breed" placeholder="请选择品种" style="width: 100%" filterable>
                          <el-option v-for="breed in getBreedOptions(petForm.species)" :key="breed" :label="breed" :value="breed"></el-option>
                        </el-select>
                      </el-form-item>
                      <el-form-item label="年龄(岁)">
                        <el-input-number v-model="petForm.age" :min="0" :max="30" placeholder="宠物年龄"></el-input-number>
                      </el-form-item>
                      <el-form-item label="体重(kg)">
                        <el-input-number v-model="petForm.weight" :min="0" :precision="1" placeholder="宠物体重"></el-input-number>
                      </el-form-item>
                      <el-form-item label="病史">
                        <el-input v-model="petForm.medicalHistory" type="textarea" placeholder="请输入宠物病史" :rows="3"></el-input>
                      </el-form-item>
                    </div>
                  </el-collapse-transition>
                </div>
                <el-button type="primary" @click="addEditPetForm">继续添加宠物</el-button>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="editProfileDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleUpdateProfile">保存修改</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Plus, Upload } from '@element-plus/icons-vue'

const emit = defineEmits(['open-new-profile-dialog'])

// 分页相关数据
const currentPage = ref(1)
const pageSize = ref(10)
const searchKeyword = ref('')

// 弹窗控制
const newProfileDialogVisible = ref(false)
const editProfileDialogVisible = ref(false)

// 客户表单
const customerForm = reactive({
  name: '',
  phone: '',
  email: '',
  address: '',
  password: ''
})

// 编辑客户表单
const editCustomerForm = reactive({
  id: '',
  name: '',
  phone: '',
  email: '',
  address: '',
  password: ''
})

// 宠物表单数组（新建）
const newPetForms = ref([])
// 新建宠物表单折叠状态
const newPetFormCollapsed = ref([])

// 宠物表单数组（编辑）
const editPetForms = ref([])
// 编辑宠物表单折叠状态
const editPetFormCollapsed = ref([])

// 动态品种数据 (与用户端保持一致)
const breedOptionsMap = {
  '狗': ['金毛寻回犬', '泰迪/贵宾犬', '柴犬', '柯基', '拉布拉多'],
  '猫': ['英短蓝猫', '布偶猫', '暹罗猫', '加菲猫', '中华田园猫'],
  '其他': ['兔子', '仓鼠', '鸟类', '爬行动物']
}

// 获取品种选项
const getBreedOptions = (species) => {
  return breedOptionsMap[species] || []
}

// 客户头像
const customerImageUrl = ref('')
const editCustomerImageUrl = ref('')

// 模拟数据：客户档案
const customerList = ref([
  { id: 101, name: '张三', phone: '13800138000', pets: ['旺财', '小强'], balance: 500, regDate: '2022-01-01' },
  { id: 102, name: '李四', phone: '13900139000', pets: ['咪咪'], balance: 0, regDate: '2023-05-20' },
  { id: 103, name: '王五', phone: '13700137000', pets: ['小白', '小黑'], balance: 200, regDate: '2023-01-15' },
  { id: 104, name: '赵六', phone: '13600136000', pets: ['花花'], balance: 0, regDate: '2023-03-22' },
  { id: 105, name: '钱七', phone: '13500135000', pets: ['大黄', '二黄'], balance: 1000, regDate: '2022-11-30' },
  { id: 106, name: '孙八', phone: '13400134000', pets: ['豆豆'], balance: 300, regDate: '2023-07-10' },
  { id: 107, name: '周九', phone: '13300133000', pets: ['球球'], balance: 0, regDate: '2023-09-05' },
  { id: 108, name: '吴十', phone: '13200132000', pets: ['毛毛', '绒绒'], balance: 150, regDate: '2023-02-18' }
])

// 计算当前页的客户数据
const paginatedCustomerList = computed(() => {
  // 如果有搜索关键词，先过滤数据
  let filteredList = customerList.value
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    filteredList = customerList.value.filter(customer => 
      customer.name.toLowerCase().includes(keyword) ||
      customer.phone.includes(keyword) ||
      customer.pets.some(pet => pet.toLowerCase().includes(keyword))
    )
  }
  
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredList.slice(start, end)
})

// 分页事件处理
const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
}

const handleCurrentChange = (val) => {
  currentPage.value = val
}

// 搜索处理
const handleSearch = () => {
  currentPage.value = 1
}

// 方法
// 新建档案 - 打开弹窗
const openNewProfileDialog = () => {
  // 重置表单
  customerForm.name = ''
  customerForm.phone = ''
  customerForm.email = ''
  customerForm.address = ''
  customerForm.password = ''
  
  // 重置宠物表单数组
  newPetForms.value = []
  newPetFormCollapsed.value = []
  
  customerImageUrl.value = ''
  
  newProfileDialogVisible.value = true
  emit('open-new-profile-dialog')
}

// 添加新的宠物表单（新建）
const addNewPetForm = () => {
  // 如果已有宠物表单，将最后一个折叠
  if (newPetForms.value.length > 0) {
    newPetFormCollapsed.value[newPetForms.value.length - 1] = true;
  }
  
  newPetForms.value.push({
    name: '',
    species: '狗',
    breed: '',
    age: null,
    weight: null,
    medicalHistory: ''
  })
  // 添加新宠物时直接展开
  newPetFormCollapsed.value.push(false)
}

// 删除宠物表单（新建）
const removeNewPetForm = (index) => {
  newPetForms.value.splice(index, 1)
  newPetFormCollapsed.value.splice(index, 1)
}

// 切换新建宠物表单的折叠状态
const toggleNewPetForm = (index) => {
  newPetFormCollapsed.value[index] = !newPetFormCollapsed.value[index]
}

// 添加新的宠物表单（编辑）
const addEditPetForm = () => {
  // 如果已有宠物表单，将最后一个折叠
  if (editPetForms.value.length > 0) {
    editPetFormCollapsed.value[editPetForms.value.length - 1] = true;
  }
  
  editPetForms.value.push({
    name: '',
    species: '狗',
    breed: '',
    age: null,
    weight: null,
    medicalHistory: ''
  })
  // 添加新宠物时直接展开
  editPetFormCollapsed.value.push(false)
}

// 删除宠物表单（编辑）
const removeEditPetForm = (index) => {
  editPetForms.value.splice(index, 1)
  editPetFormCollapsed.value.splice(index, 1)
}

// 切换编辑宠物表单的折叠状态
const toggleEditPetForm = (index) => {
  editPetFormCollapsed.value[index] = !editPetFormCollapsed.value[index]
}

// 编辑档案 - 打开弹窗
const openEditProfileDialog = (customer) => {
  // 填充表单数据
  editCustomerForm.id = customer.id
  editCustomerForm.name = customer.name
  editCustomerForm.phone = customer.phone
  editCustomerForm.email = customer.email || ''
  editCustomerForm.address = customer.address || ''
  editCustomerForm.password = ''
  
  // 初始化宠物表单数组（这里简化处理，实际应该从后端获取宠物详细信息）
  editPetForms.value = []
  editPetFormCollapsed.value = []
  if (customer.pets && customer.pets.length > 0) {
    customer.pets.forEach(petName => {
      editPetForms.value.push({
        name: petName,
        species: '狗', // 默认值
        breed: '', // 默认值
        age: null,
        weight: null,
        medicalHistory: ''
      })
      // 默认折叠所有表单
      editPetFormCollapsed.value.push(true)
    })
  }
  
  editCustomerImageUrl.value = ''
  
  editProfileDialogVisible.value = true
}

// 头像上传成功处理
const handleAvatarSuccess = (response, uploadFile) => {
  customerImageUrl.value = URL.createObjectURL(uploadFile.raw)
}

// 编辑时头像上传成功处理
const handleEditAvatarSuccess = (response, uploadFile) => {
  editCustomerImageUrl.value = URL.createObjectURL(uploadFile.raw)
}

// 上传前检查
const beforeAvatarUpload = (rawFile) => {
  if (rawFile.type !== 'image/jpeg' && rawFile.type !== 'image/png') {
    ElMessage.error('头像图片只能是 JPG 或 PNG 格式!')
    return false
  } else if (rawFile.size / 1024 / 1024 > 2) {
    ElMessage.error('头像图片大小不能超过 2MB!')
    return false
  }
  return true
}

// 处理创建档案
const handleCreateProfile = () => {
  if (!customerForm.phone || !customerForm.name || !customerForm.password) {
    return ElMessage.error('请填写完整的客户信息')
  }
  
  // 验证宠物信息
  for (let i = 0; i < newPetForms.value.length; i++) {
    const petForm = newPetForms.value[i]
    if (!petForm.name || !petForm.breed) {
      return ElMessage.error(`请填写第${i + 1}个宠物的完整信息`)
    }
  }
  
  // 添加新客户到列表
  const newCustomer = {
    id: customerList.value.length > 0 ? Math.max(...customerList.value.map(c => c.id)) + 1 : 101,
    name: customerForm.name,
    phone: customerForm.phone,
    email: customerForm.email,
    address: customerForm.address,
    pets: newPetForms.value.map(pet => pet.name),
    balance: 0,
    regDate: new Date().toISOString().split('T')[0]
  }
  customerList.value.push(newCustomer)
  
  ElMessage.success('档案创建成功！')
  newProfileDialogVisible.value = false
}

// 处理更新档案
const handleUpdateProfile = () => {
  if (!editCustomerForm.name) {
    return ElMessage.error('请填写客户姓名')
  }
  
  // 验证宠物信息
  for (let i = 0; i < editPetForms.value.length; i++) {
    const petForm = editPetForms.value[i]
    if (!petForm.name || !petForm.breed) {
      return ElMessage.error(`请填写第${i + 1}个宠物的完整信息`)
    }
  }
  
  // 更新客户列表中的数据
  const index = customerList.value.findIndex(customer => customer.id === editCustomerForm.id)
  if (index !== -1) {
    customerList.value[index].name = editCustomerForm.name
    customerList.value[index].email = editCustomerForm.email
    customerList.value[index].address = editCustomerForm.address
    // 更新宠物信息
    customerList.value[index].pets = editPetForms.value.map(pet => pet.name)
  }
  
  ElMessage.success('档案更新成功！')
  editProfileDialogVisible.value = false
}
</script>

<style scoped>
.customer-profile-wrapper {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.customer-profile-card {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.customer-profile-card :deep(.el-card__body) {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.customer-profile-card :deep(.el-table) {
  flex: 1;
}

.pagination-container {
  display: flex;
  justify-content: center;
  padding: 20px 0;
}

.avatar-uploader .avatar {
  width: 120px;
  height: 120px;
  display: block;
  border: 2px dashed #ccc;
  border-radius: 6px;
}
.avatar-uploader .el-upload {
  border: 2px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
  width: 120px;
  height: 120px;
}
.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 120px;
  height: 120px;
  text-align: center;
  border: 2px dashed #ccc;
  border-radius: 6px;
}
</style>