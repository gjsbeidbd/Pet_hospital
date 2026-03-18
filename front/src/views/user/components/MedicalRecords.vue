<template>
  <div class="medical-records-wrapper">
    <el-card class="medical-records-card">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span style="font-size: 18px; font-weight: bold;">我的宠物病历档案</span>
          <el-input
            v-model="searchKeyword"
            placeholder="搜索宠物名称或诊断结果"
            style="width: 300px;"
            clearable
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </div>
      </template>
      
      <!-- 病历列表 -->
      <div class="records-list">
        <el-table :data="filteredRecords" border style="width: 100%" class="data-table" empty-text="暂无病历记录">
          <el-table-column prop="petName" label="宠物名称" width="120" align="center">
            <template #default="scope">
              <span style="font-weight: bold;">{{ scope.row.petName || scope.row.title }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="visitDate" label="就诊日期" width="120" align="center">
            <template #default="scope">
              <span style="color: #409EFF;">{{ formatDate(scope.row.visitDate) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="doctorName" label="主治医生" width="120" align="center">
            <template #default="scope">
              {{ scope.row.doctorName || scope.row.doctor || '未知医生' }}
            </template>
          </el-table-column>
          <el-table-column prop="diagnosis" label="诊断结果" min-width="200" show-overflow-tooltip>
            <template #default="scope">
              <span v-if="scope.row.diagnosis" style="color: #F56C6C;">{{ scope.row.diagnosis }}</span>
              <span v-else style="color: #999;">无诊断信息</span>
            </template>
          </el-table-column>
          <el-table-column prop="examination" label="检查项目" width="120" align="center">
            <template #default="scope">
              <el-tag v-if="scope.row.examination" type="info" size="small">{{ scope.row.examination }}</el-tag>
              <span v-else style="color: #999;">无</span>
            </template>
          </el-table-column>
          <el-table-column prop="surgery" label="手术项目" width="120" align="center">
            <template #default="scope">
              <el-tag v-if="scope.row.surgery" type="warning" size="small">{{ scope.row.surgery }}</el-tag>
              <span v-else style="color: #999;">无</span>
            </template>
          </el-table-column>
          <el-table-column prop="prescriptionCount" label="处方药品" width="100" align="center">
            <template #default="scope">
              <el-tag v-if="scope.row.prescriptionCount > 0" type="success" size="small">
                {{ scope.row.prescriptionCount }}种
              </el-tag>
              <span v-else style="color: #999;">无</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120" fixed="right" align="center">
            <template #default="scope">
              <el-button type="primary" size="small" @click="viewDetail(scope.row)">查看详情</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      
      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[5, 10, 20, 50]"
          :total="medicalRecords.length"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          background
        />
      </div>
    </el-card>
    
    <!-- 病历详情弹窗 -->
    <el-dialog v-model="detailDialogVisible" title="病历详情" width="80%" top="5vh">
      <div v-if="selectedRecord" class="medical-record-detail">
        <!-- 病历头部信息 -->
        <div class="record-header">
          <h2 style="text-align: center; margin-bottom: 20px;">宠物医院病历单</h2>
          <el-row :gutter="20">
            <el-col :span="8">
              <div class="info-item">
                <span class="label">病历号：</span>
                <span class="value">{{ selectedRecord.id || '未知' }}</span>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="info-item">
                <span class="label">就诊日期：</span>
                <span class="value">{{ formatDate(selectedRecord.visitDate) }}</span>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="info-item">
                <span class="label">状态：</span>
                <span class="value">
                  <el-tag :type="getStatusType(selectedRecord.status)">{{ selectedRecord.status || '未知状态' }}</el-tag>
                </span>
              </div>
            </el-col>
          </el-row>
        </div>
        
        <!-- 宠物信息 -->
        <el-card shadow="never" style="margin-bottom: 20px;">
          <template #header>
            <strong>宠物信息</strong>
          </template>
          <el-row :gutter="20">
            <el-col :span="8">
              <div class="info-item">
                <span class="label">宠物名称：</span>
                <span class="value">{{ selectedRecord.petName || '未知' }}</span>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="info-item">
                <span class="label">宠物品种：</span>
                <span class="value">{{ selectedRecord.petBreed || '未知' }}</span>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="info-item">
                <span class="label">宠物年龄：</span>
                <span class="value">{{ selectedRecord.petAge || '未知' }}岁</span>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="info-item">
                <span class="label">宠物性别：</span>
                <span class="value">{{ selectedRecord.petGender || '未知' }}</span>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="info-item">
                <span class="label">主人姓名：</span>
                <span class="value">{{ selectedRecord.ownerName || '未知' }}</span>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="info-item">
                <span class="label">联系电话：</span>
                <span class="value">{{ selectedRecord.ownerPhone || '未知' }}</span>
              </div>
            </el-col>
          </el-row>
        </el-card>
        
        <!-- 诊断信息 -->
        <el-card shadow="never" style="margin-bottom: 20px;">
          <template #header>
            <strong>诊断信息</strong>
          </template>
          <el-row :gutter="20">
            <el-col :span="8">
              <div class="info-item">
                <span class="label">主治医生：</span>
                <span class="value">{{ selectedRecord.doctorName || selectedRecord.doctor || '未知医生' }}</span>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="info-item">
                <span class="label">科室：</span>
                <span class="value">{{ selectedRecord.department || '未知科室' }}</span>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="info-item">
                <span class="label">就诊类型：</span>
                <span class="value">{{ selectedRecord.visitType || '普通门诊' }}</span>
              </div>
            </el-col>
            <el-col :span="24">
              <div class="info-item">
                <span class="label">主诉：</span>
                <span class="value">{{ selectedRecord.chiefComplaint || selectedRecord.reason || '无主诉信息' }}</span>
              </div>
            </el-col>
            <el-col :span="24">
              <div class="info-item">
                <span class="label">临床症状：</span>
                <span class="value">{{ selectedRecord.symptoms || '无症状描述' }}</span>
              </div>
            </el-col>
            <el-col :span="24">
              <div class="info-item">
                <span class="label">诊断结果：</span>
                <span class="value">{{ selectedRecord.diagnosis || '无诊断结果' }}</span>
              </div>
            </el-col>
            <el-col :span="24" v-if="selectedRecord.examinations && selectedRecord.examinations.length > 0">
              <div class="info-item">
                <span class="label">检查项目：</span>
                <span class="value">{{ selectedRecord.examinations.join(', ') }}</span>
              </div>
            </el-col>
          </el-row>
        </el-card>
        
        <!-- 处方信息 -->
        <el-card shadow="never" style="margin-bottom: 20px;" v-if="selectedRecord.prescriptions && selectedRecord.prescriptions.length > 0">
          <template #header>
            <strong>处方信息</strong>
          </template>
          <el-table :data="selectedRecord.prescriptions" border style="width: 100%">
            <el-table-column prop="name" label="药品名称" width="150"></el-table-column>
            <el-table-column prop="count" label="数量" width="80"></el-table-column>
            <el-table-column prop="usage" label="用法用量"></el-table-column>
          </el-table>
        </el-card>
        
        <!-- 医嘱建议 -->
        <el-card shadow="never" style="margin-bottom: 20px;" v-if="selectedRecord.advices && selectedRecord.advices.length > 0">
          <template #header>
            <strong>医嘱建议</strong>
          </template>
          <div class="info-item">
            <span class="value">{{ selectedRecord.advices.join(', ') }}</span>
          </div>
        </el-card>
        
        <!-- 费用信息 -->
        <el-card shadow="never" style="margin-bottom: 20px;">
          <template #header>
            <strong>费用明细</strong>
          </template>
          <el-row :gutter="20">
            <el-col :span="6">
              <div class="info-item">
                <span class="label">检查费：</span>
                <span class="value">¥{{ selectedRecord.examFee || 0 }}</span>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="info-item">
                <span class="label">药费：</span>
                <span class="value">¥{{ selectedRecord.medicineFee || 0 }}</span>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="info-item">
                <span class="label">治疗费：</span>
                <span class="value">¥{{ selectedRecord.treatmentFee || 0 }}</span>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="info-item">
                <span class="label">总费用：</span>
                <span class="value">¥{{ selectedRecord.totalFee || (selectedRecord.examFee + selectedRecord.medicineFee + selectedRecord.treatmentFee) || 0 }}</span>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
          <el-button type="primary" @click="printRecord">打印病历</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { getMedicalRecordsByPetId } from '@/services/api'

// 控制展开的面板
const activeNames = ref([])

// 详情弹窗可见性
const detailDialogVisible = ref(false)

// 选中的病历记录
const selectedRecord = ref(null)

// 分页相关数据
const currentPage = ref(1)
const pageSize = ref(5)

// 病历数据
const medicalRecords = ref([])

// 搜索关键词
const searchKeyword = ref('')

// 计算当前页的病历数据
const paginatedRecords = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return medicalRecords.value.slice(start, end)
})

// 搜索过滤后的病历数据
const filteredRecords = computed(() => {
  if (!searchKeyword.value) {
    return paginatedRecords.value
  }
  
  const keyword = searchKeyword.value.toLowerCase()
  return paginatedRecords.value.filter(record => 
    (record.petName && record.petName.toLowerCase().includes(keyword)) ||
    (record.diagnosis && record.diagnosis.toLowerCase().includes(keyword)) ||
    (record.doctorName && record.doctorName.toLowerCase().includes(keyword))
  )
})

// 分页事件处理
const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
}

const handleCurrentChange = (val) => {
  currentPage.value = val
}

// 处理面板展开/收缩
const handleChange = (val) => {
  console.log('展开的面板:', val)
}

// 查看详情
const viewDetail = (record) => {
  selectedRecord.value = record
  detailDialogVisible.value = true
}

// 打印病历
const printRecord = () => {
  ElMessage.info('打印功能已触发')
  // 实际项目中这里会调用打印功能
}

// 获取状态标签类型
const getStatusType = (status) => {
  switch (status) {
    case '已完成':
      return 'success'
    case '治疗中':
      return 'warning'
    case '术后恢复':
      return 'info'
    case '已归档':
      return 'info'
    default:
      return 'primary'
  }
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return '未知日期'
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN')
}

// 获取病历数据
const fetchMedicalRecords = async () => {
  try {
    // 获取用户ID
    const userId = localStorage.getItem('userId')
    if (!userId) {
      ElMessage.error('用户未登录')
      return
    }
    
    // 获取用户宠物列表
    const petResponse = await fetch(`http://localhost:8080/api/pets?userId=${userId}`)
    const pets = await petResponse.json()
    
    // 获取每个宠物的病历
      const allRecords = []
      for (const pet of pets) {
        const response = await getMedicalRecordsByPetId(pet.id)
        const records = response.data.map(record => ({
          ...record,
          petName: pet.name,
          petBreed: pet.breed,
          petAge: pet.age,
          petGender: pet.gender,
          title: `${formatDate(record.visitDate)} | ${pet.name} | ${record.diagnosis || '无诊断'}`,
          // 添加新字段
          examination: record.examination || '',
          surgery: record.surgery || '',
          prescriptionCount: record.prescription ? (JSON.parse(record.prescription).drugs?.length || 0) : 0
        }))
        allRecords.push(...records)
      }
    
    medicalRecords.value = allRecords
  } catch (error) {
    console.error('获取病历数据失败:', error)
    ElMessage.error('获取病历数据失败: ' + (error.response?.data?.error || error.message || '未知错误'))
  }
}

// 在组件挂载时获取病历数据
onMounted(() => {
  fetchMedicalRecords()
})
</script>

<style scoped>
.medical-records-wrapper {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.medical-records-card {
  flex: 1;
  display: flex;
  flex-direction: column;
  margin-top: auto;
}

.medical-records-card :deep(.el-card__body) {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.records-list {
  flex: 1;
  overflow-y: auto;
}

.pagination-container {
  display: flex;
  justify-content: center;
  padding: 20px 0;
}

.medical-record-detail {
  max-height: 70vh;
  overflow-y: auto;
}

.record-header {
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
}

.info-item {
  margin-bottom: 10px;
}

.label {
  display: inline-block;
  width: 80px;
  font-weight: bold;
}

.value {
  display: inline-block;
}

.dialog-footer {
  text-align: right;
}
</style>