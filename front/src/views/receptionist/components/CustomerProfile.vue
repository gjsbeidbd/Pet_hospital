<template>
  <div class="customer-profile-wrapper">
    <el-card class="customer-profile-card">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>客户/宠物资料管理</span>
          <div style="display: flex; gap: 10px;">
            <el-input placeholder="请输入手机号/宠物名/客户名" style="width: 300px;" v-model="searchKeyword"></el-input>
            <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
            <el-button type="success" :icon="Plus" @click="openNewProfileDialog">新建档案</el-button>
          </div>
        </div>
      </template>
      
      <el-table :data="paginatedCustomerList" border stripe v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="name" label="客户姓名" width="120"></el-table-column>
        <el-table-column prop="phone" label="联系电话" width="150"></el-table-column>
        <el-table-column prop="email" label="邮箱" width="200"></el-table-column>
        <el-table-column prop="address" label="地址" min-width="200" show-overflow-tooltip></el-table-column>
        <el-table-column prop="pets" label="名下宠物" width="200">
          <template #default="scope">
            <el-tag size="small" v-for="p in scope.row.pets" :key="p.id" style="margin-right: 5px; margin-bottom: 5px;">{{ p.name }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="注册日期" width="120"></el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="openEditProfileDialog(scope.row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDeleteCustomer(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

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

    <el-dialog v-model="newProfileDialogVisible" title="新建客户与宠物档案" width="700px">
      <el-form label-width="80px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-card header="客户信息" shadow="never">
              <el-form-item label="手机号">
                <el-input v-model="customerForm.phone" placeholder="请输入手机号"></el-input>
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
                        <el-select v-model="petForm.species" placeholder="请选择种类" style="width: 100%" @change="handleSpeciesChange(index, petForm.species)">
                          <el-option v-for="s in petSpecies" :key="s.id" :label="s.speciesName" :value="s.speciesName"></el-option>
                        </el-select>
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

    <el-dialog v-model="editProfileDialogVisible" title="编辑客户与宠物档案" width="700px">
      <el-form label-width="80px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-card header="客户信息" shadow="never">
              <el-form-item label="手机号">
                <el-input v-model="editCustomerForm.phone" disabled></el-input>
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
                        <el-select v-model="petForm.species" placeholder="请选择种类" style="width: 100%" @change="handleEditSpeciesChange(index, petForm.species)">
                          <el-option v-for="s in petSpecies" :key="s.id" :label="s.speciesName" :value="s.speciesName"></el-option>
                        </el-select>
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
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus, Upload } from '@element-plus/icons-vue'
import { getAllUsers, getAllPets, getUserInfo, getUserPets, addPet, updatePet, deletePet, deleteUser, getPetSpecies, getPetBreedsBySpeciesId, updateUserInfo, changeUserPassword, registerUser } from '@/services/api'

const emit = defineEmits(['open-new-profile-dialog'])

const currentPage = ref(1)
const pageSize = ref(10)
const searchKeyword = ref('')
const loading = ref(false)

const newProfileDialogVisible = ref(false)
const editProfileDialogVisible = ref(false)

const customerForm = reactive({
  name: '',
  phone: '',
  email: '',
  address: '',
  password: ''
})

const editCustomerForm = reactive({
  id: '',
  name: '',
  phone: '',
  email: '',
  address: '',
  password: ''
})

const newPetForms = ref([])
const newPetFormCollapsed = ref([])

const editPetForms = ref([])
const editPetFormCollapsed = ref([])

const getBreedOptions = (speciesName) => {
  const species = petSpecies.value.find(s => s.speciesName === speciesName)
  if (!species) return []
  return petBreeds.value.filter(b => b.speciesId === species.id).map(b => b.breedName)
}

const customerImageUrl = ref('')
const editCustomerImageUrl = ref('')

const customerList = ref([])
const allPets = ref([])
const petSpecies = ref([])
const petBreeds = ref([])

const paginatedCustomerList = computed(() => {
  let filteredList = customerList.value
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    filteredList = customerList.value.filter(customer => 
      customer.name.toLowerCase().includes(keyword) ||
      customer.phone.includes(keyword) ||
      customer.pets.some(pet => pet.name.toLowerCase().includes(keyword))
    )
  }
  
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredList.slice(start, end)
})

const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
}

const handleCurrentChange = (val) => {
  currentPage.value = val
}

const handleSearch = () => {
  currentPage.value = 1
}

const fetchData = async () => {
  loading.value = true
  try {
    const [usersRes, petsRes, speciesRes, breedsRes] = await Promise.all([
      getAllUsers(),
      getAllPets(),
      getPetSpecies(),
      getPetBreedsBySpeciesId('')
    ])
    
    if (usersRes.data && Array.isArray(usersRes.data)) {
      const users = usersRes.data.filter(user => user.role === 'USER')
      const pets = petsRes.data || []
      petSpecies.value = speciesRes.data || []
      petBreeds.value = breedsRes.data || []
      
      customerList.value = users.map(user => {
        const userPets = pets.filter(pet => pet.userId === user.id)
        return {
          id: user.id,
          name: user.name,
          phone: user.phone,
          email: user.email,
          address: user.address,
          createdAt: user.createdAt ? user.createdAt.substring(0, 10) : '',
          pets: userPets.map(pet => ({
            id: pet.id,
            name: pet.name,
            species: pet.species,
            breed: pet.breed,
            age: pet.age,
            weight: pet.weight,
            medicalHistory: pet.medicalHistory
          }))
        }
      })
    }
  } catch (error) {
    console.error('获取数据失败:', error)
    ElMessage.error('获取数据失败')
  } finally {
    loading.value = false
  }
}

const openNewProfileDialog = () => {
  customerForm.name = ''
  customerForm.phone = ''
  customerForm.email = ''
  customerForm.address = ''
  customerForm.password = ''
  
  newPetForms.value = []
  newPetFormCollapsed.value = []
  
  customerImageUrl.value = ''
  
  newProfileDialogVisible.value = true
  emit('open-new-profile-dialog')
}

const addNewPetForm = () => {
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
  newPetFormCollapsed.value.push(false)
}

const removeNewPetForm = (index) => {
  newPetForms.value.splice(index, 1)
  newPetFormCollapsed.value.splice(index, 1)
}

const toggleNewPetForm = (index) => {
  newPetFormCollapsed.value[index] = !newPetFormCollapsed.value[index]
}

const addEditPetForm = () => {
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
  editPetFormCollapsed.value.push(false)
}

const removeEditPetForm = (index) => {
  editPetForms.value.splice(index, 1)
  editPetFormCollapsed.value.splice(index, 1)
}

const toggleEditPetForm = (index) => {
  editPetFormCollapsed.value[index] = !editPetFormCollapsed.value[index]
}

const handleSpeciesChange = (index, species) => {
  newPetForms.value[index].breed = ''
}

const handleEditSpeciesChange = (index, species) => {
  editPetForms.value[index].breed = ''
}

const openEditProfileDialog = (customer) => {
  editCustomerForm.id = customer.id
  editCustomerForm.name = customer.name
  editCustomerForm.phone = customer.phone
  editCustomerForm.email = customer.email || ''
  editCustomerForm.address = customer.address || ''
  editCustomerForm.password = ''
  
  editPetForms.value = []
  editPetFormCollapsed.value = []
  if (customer.pets && customer.pets.length > 0) {
    customer.pets.forEach(pet => {
      editPetForms.value.push({
        id: pet.id,
        name: pet.name,
        species: pet.species || '狗',
        breed: pet.breed || '',
        age: pet.age,
        weight: pet.weight,
        medicalHistory: pet.medicalHistory || ''
      })
      editPetFormCollapsed.value.push(true)
    })
  }
  
  editCustomerImageUrl.value = ''
  
  editProfileDialogVisible.value = true
}

const handleAvatarSuccess = (response, uploadFile) => {
  customerImageUrl.value = URL.createObjectURL(uploadFile.raw)
}

const handleEditAvatarSuccess = (response, uploadFile) => {
  editCustomerImageUrl.value = URL.createObjectURL(uploadFile.raw)
}

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

const handleCreateProfile = async () => {
  if (!customerForm.phone || !customerForm.name || !customerForm.password) {
    return ElMessage.error('请填写完整的客户信息')
  }
  
  if (customerForm.phone.length !== 11) {
    return ElMessage.error('手机号必须为11位')
  }
  
  for (let i = 0; i < newPetForms.value.length; i++) {
    if (!newPetForms.value[i].name) {
      return ElMessage.error(`请填写第 ${i + 1} 个宠物的名字`)
    }
  }
  
  try {
    loading.value = true
    
    const userData = {
      phone: customerForm.phone,
      email: customerForm.email,
      password: customerForm.password,
      name: customerForm.name,
      address: customerForm.address,
      role: 'USER'
    }
    
    const userRes = await registerUser(userData)
    const userId = userRes.data.id
    
    for (const petForm of newPetForms.value) {
      const petData = {
        userId: userId,
        name: petForm.name,
        species: petForm.species,
        breed: petForm.breed,
        age: petForm.age,
        weight: petForm.weight,
        medicalHistory: petForm.medicalHistory
      }
      await addPet(petData)
    }
    
    ElMessage.success('新建档案成功')
    newProfileDialogVisible.value = false
    fetchData()
  } catch (error) {
    console.error('新建档案失败:', error)
    ElMessage.error('新建档案失败')
  } finally {
    loading.value = false
  }
}

const handleUpdateProfile = async () => {
  if (!editCustomerForm.name) {
    return ElMessage.error('请填写客户姓名')
  }
  
  try {
    loading.value = true
    
    const userId = editCustomerForm.id
    
    const userData = {
      name: editCustomerForm.name,
      email: editCustomerForm.email,
      address: editCustomerForm.address
    }
    
    await updateUserInfo(userId, userData)
    
    if (editCustomerForm.password) {
      await changeUserPassword(userId, {
        oldPassword: '',
        newPassword: editCustomerForm.password
      })
    }
    
    const existingPetIds = []
    for (const petForm of editPetForms.value) {
      const petData = {
        userId: userId,
        name: petForm.name,
        species: petForm.species,
        breed: petForm.breed,
        age: petForm.age,
        weight: petForm.weight,
        medicalHistory: petForm.medicalHistory
      }
      
      if (petForm.id) {
        await updatePet(petForm.id, petData)
        existingPetIds.push(petForm.id)
      } else {
        await addPet(petData)
      }
    }
    
    const currentPets = await getUserPets(userId)
    if (currentPets.data && Array.isArray(currentPets.data)) {
      for (const pet of currentPets.data) {
        if (!existingPetIds.includes(pet.id)) {
          await deletePet(pet.id, userId)
        }
      }
    }
    
    ElMessage.success('编辑档案成功')
    editProfileDialogVisible.value = false
    fetchData()
  } catch (error) {
    console.error('编辑档案失败:', error)
    ElMessage.error('编辑档案失败')
  } finally {
    loading.value = false
  }
}

const handleDeleteCustomer = (customer) => {
  ElMessageBox.confirm(
    `确定要删除客户"${customer.name}"吗？删除后该客户及其名下所有宠物数据将被永久删除。`,
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await deleteUser(customer.id)
      ElMessage.success('删除成功')
      fetchData()
    } catch (error) {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {
    // 用户取消删除
  })
}

onMounted(() => {
  fetchData()
})
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

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.avatar-uploader {
  text-align: center;
}

.avatar-uploader :deep(.el-upload) {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: border-color 0.3s;
}

.avatar-uploader :deep(.el-upload:hover) {
  border-color: #409EFF;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  line-height: 178px;
  text-align: center;
}

.avatar {
  width: 178px;
  height: 178px;
  display: block;
}
</style>
