<template>
  <div>
    <el-row :gutter="20">
      <el-col :span="6" v-for="pet in pets" :key="pet.id">
        <el-card class="pet-card" :body-style="{ padding: '0px' }">
          <img :src="pet.img" class="pet-img">
          <div style="padding: 14px;">
            <div style="display: flex; justify-content: space-between; align-items: center;">
              <span style="font-weight: bold; font-size: 16px;">{{ pet.name }}</span>
              <el-tag size="small" :type="pet.species === '猫' ? 'warning' : ''">{{ pet.breed }}</el-tag>
            </div>
            <div style="color: #999; font-size: 13px; margin-top: 10px;">
              <span>{{ pet.age }}岁</span> | <span>{{ pet.weight }}kg</span>
            </div>
            <div style="color: #999; font-size: 12px; margin-top: 5px;" v-if="pet.vaccinations && pet.vaccinations.length > 0">
              <div>疫苗: {{ pet.vaccinations.slice(0, 3).join(', ') }}{{ pet.vaccinations.length > 3 ? '...' : '' }}</div>
            </div>
            <div style="color: #999; font-size: 12px; margin-top: 5px;" v-else>
              <div>疫苗: 未接种</div>
            </div>
            <div style="color: #999; font-size: 12px; margin-top: 5px;" v-if="pet.medicalHistory">
              <span>病史: {{ pet.medicalHistory.substring(0, 20) }}{{ pet.medicalHistory.length > 20 ? '...' : '' }}</span>
            </div>
            <div style="margin-top: 15px; text-align: right;">
              <el-button type="primary" size="small" @click="handleEditPet(pet)">编辑</el-button>
              <el-button type="danger" size="small" @click="handleDeletePet(pet.id)">删除</el-button>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <!-- 添加宠物卡片 -->
      <el-col :span="6">
        <el-card class="pet-card-add" :body-style="{ padding: '0px' }" @click="openAddPetDialog">
          <img src="" class="pet-img" style="visibility: hidden;">
          <div class="add-pet-content">
            <el-icon :size="40"><Plus /></el-icon>
            <div style="margin-top: 10px;">添加宠物</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 添加/编辑宠物对话框 -->
    <el-dialog v-model="dialogVisible" :title="editingPet ? '编辑宠物' : '添加宠物'" width="500px">
      <el-form :model="petForm" label-width="80px">
        <el-form-item label="宠物名称">
          <el-input v-model="petForm.name" placeholder="请输入宠物名称"></el-input>
        </el-form-item>
        <el-form-item label="种类">
          <el-select v-model="petForm.species" placeholder="请选择种类" style="width: 100%" @change="onSpeciesChange">
            <el-option 
              v-for="species in speciesList" 
              :key="species.id" 
              :label="species.speciesName" 
              :value="species.speciesName">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="品种">
          <el-select v-model="petForm.breed" placeholder="请选择品种" style="width: 100%" filterable :disabled="!petForm.species">
            <el-option 
              v-for="breed in breedList" 
              :key="breed.id" 
              :label="breed.breedName" 
              :value="breed.breedName">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="年龄">
          <el-input-number v-model="petForm.age" :min="0" :max="30" placeholder="宠物年龄"></el-input-number>
        </el-form-item>
        <el-form-item label="体重(kg)">
          <el-input-number v-model="petForm.weight" :min="0" :precision="1" placeholder="宠物体重"></el-input-number>
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="petForm.gender" placeholder="请选择性别" style="width: 100%">
            <el-option label="雄性" value="雄性"></el-option>
            <el-option label="雌性" value="雌性"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="病史">
          <el-input 
            v-model="petForm.medicalHistory" 
            type="textarea" 
            placeholder="请输入宠物病史" 
            :rows="3">
          </el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="editingPet ? updatePetHandler() : addPetHandler()">
            {{ editingPet ? '更新' : '添加' }}
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, defineProps, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getUserPets, addPet, updatePet, deletePet, getPetSpecies, getPetBreedsBySpeciesId } from '@/services/api'

// 种类列表
const speciesList = ref([])

// 品种列表
const breedList = ref([])

// 种类ID映射（种类名称 -> 种类ID）
const speciesIdMap = ref({})

// 弹窗控制
const dialogVisible = ref(false)

// 编辑状态
const editingPet = ref(null)

// 宠物列表
const pets = ref([])

// 宠物表单
const petForm = reactive({
  name: '',
  species: '',
  breed: '',
  age: null,
  weight: null,
  gender: '雄性',
  medicalHistory: ''
})

// 获取种类列表
const fetchSpeciesList = async () => {
  try {
    const response = await getPetSpecies()
    speciesList.value = response.data || []
    // 建立种类名称到ID的映射
    speciesIdMap.value = {}
    speciesList.value.forEach(species => {
      speciesIdMap.value[species.speciesName] = species.id
    })
  } catch (error) {
    console.error('获取种类列表失败:', error)
    ElMessage.error('获取种类列表失败')
  }
}

// 种类变化时获取对应品种
const onSpeciesChange = async (speciesName) => {
  petForm.breed = ''
  breedList.value = []
  
  const speciesId = speciesIdMap.value[speciesName]
  if (speciesId) {
    try {
      const response = await getPetBreedsBySpeciesId(speciesId)
      breedList.value = response.data || []
    } catch (error) {
      console.error('获取品种列表失败:', error)
      ElMessage.error('获取品种列表失败')
    }
  }
}

// 获取用户宠物列表
const fetchPets = async () => {
  try {
    const userId = localStorage.getItem('userId')
    if (userId) {
      const response = await getUserPets(userId)
      pets.value = response.data.map(pet => ({
        ...pet,
        img: 'https://picsum.photos/400/400?random=' + pet.id
      }))
    }
  } catch (error) {
    console.error('获取宠物列表失败:', error)
    ElMessage.error('获取宠物列表失败')
  }
}

// 打开添加宠物对话框
const openAddPetDialog = () => {
  // 重置表单
  petForm.name = ''
  petForm.species = ''
  petForm.breed = ''
  petForm.age = null
  petForm.weight = null
  petForm.medicalHistory = ''
  breedList.value = []
  editingPet.value = null
  dialogVisible.value = true
}

// 处理编辑宠物
const handleEditPet = async (pet) => {
  // 将宠物数据加载到表单中
  petForm.name = pet.name
  petForm.species = pet.species
  petForm.breed = pet.breed
  petForm.age = pet.age
  petForm.weight = pet.weight
  petForm.gender = pet.gender || '雄性'
  petForm.medicalHistory = pet.medicalHistory

  // 获取对应种类的品种列表
  const speciesId = speciesIdMap.value[pet.species]
  if (speciesId) {
    try {
      const response = await getPetBreedsBySpeciesId(speciesId)
      breedList.value = response.data || []
    } catch (error) {
      console.error('获取品种列表失败:', error)
    }
  }

  // 设置编辑状态
  editingPet.value = pet
  dialogVisible.value = true
}

// 添加宠物
const addPetHandler = async () => {
  if (!petForm.name || !petForm.breed) {
    return ElMessage.error('请填写完整的宠物信息')
  }

  try {
    const userId = localStorage.getItem('userId')
    if (!userId) {
      return ElMessage.error('用户信息不存在，请重新登录')
    }

    const petData = {
      userId: parseInt(userId),
      name: petForm.name,
      species: petForm.species,
      breed: petForm.breed,
      age: petForm.age || 0,
      weight: petForm.weight || 0,
      medicalHistory: petForm.medicalHistory || '',
      gender: petForm.gender
    }
    
    const response = await addPet(petData)
    ElMessage.success(response.data.message)
    
    // 关闭对话框
    dialogVisible.value = false
    
    // 重新获取宠物列表
    fetchPets()
  } catch (error) {
    console.error('添加宠物失败:', error)
    ElMessage.error('添加宠物失败: ' + (error.response?.data?.error || '未知错误'))
  }
}

// 更新宠物
const updatePetHandler = async () => {
  if (!petForm.name || !petForm.breed) {
    return ElMessage.error('请填写完整的宠物信息')
  }
  
  try {
    const userId = localStorage.getItem('userId')
    if (!userId) {
      return ElMessage.error('用户信息不存在，请重新登录')
    }
    
    const petData = {
      userId: parseInt(userId),
      name: petForm.name,
      species: petForm.species,
      breed: petForm.breed,
      age: petForm.age || 0,
      weight: petForm.weight || 0,
      medicalHistory: petForm.medicalHistory,
      gender: petForm.gender
    }
    
    const response = await updatePet(editingPet.value.id, petData)
    ElMessage.success(response.data.message)
    
    // 关闭对话框
    dialogVisible.value = false
    
    // 重新获取宠物列表
    fetchPets()
  } catch (error) {
    console.error('更新宠物失败:', error)
    ElMessage.error('更新宠物失败: ' + (error.response?.data?.error || '未知错误'))
  }
}

// 处理删除宠物
const handleDeletePet = (petId) => {
  ElMessageBox.confirm('确定要删除这只宠物吗？此操作不可撤销。', '删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const userId = localStorage.getItem('userId')
      if (!userId) {
        return ElMessage.error('用户信息不存在，请重新登录')
      }
      
      const response = await deletePet(petId, parseInt(userId))
      ElMessage.success(response.data.message)
      
      // 重新获取宠物列表
      fetchPets()
    } catch (error) {
      console.error('删除宠物失败:', error)
      ElMessage.error('删除宠物失败: ' + (error.response?.data?.error || '未知错误'))
    }
  }).catch(() => {
    // 用户取消删除
  })
}

// 在组件挂载时获取宠物列表和种类列表
onMounted(() => {
  fetchPets()
  fetchSpeciesList()
})
</script>

<style scoped>
.pet-card-add {
  display: flex;
  flex-direction: column;
  cursor: pointer;
  transition: transform 0.3s;
  margin-bottom: 20px;
  height: 100%;
  position: relative;
}

.pet-card-add :deep(.el-card__body) {
  display: flex;
  flex-direction: column;
  width: 100%;
  height: 100%;
  position: relative;
}

.pet-card-add:hover {
  transform: translateY(-5px);
}

.pet-card-add:hover .add-pet-content {
  color: #409EFF !important;
}

.pet-card {
  margin-bottom: 20px;
  transition: transform 0.3s;
  height: 100%;
}

.pet-card:hover {
  transform: translateY(-5px);
}

.pet-img {
  width: 100%;
  height: 150px;
  object-fit: cover;
  border-radius: 4px 4px 0 0;
}

.add-pet-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #ccc;
  transition: all 0.3s;
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
}
</style>