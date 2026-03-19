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
          <el-table-column label="报告编号" min-width="100" align="center">
            <template #default="scope">
              <span>MR-{{ scope.row.id }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="petName" label="宠物名称" min-width="100" align="center">
            <template #default="scope">
              <span>{{ scope.row.petName || scope.row.title }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="visitDate" label="就诊日期" min-width="110" align="center">
            <template #default="scope">
              <span>{{ formatDate(scope.row.visitDate) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="doctorName" label="主治医生" min-width="100" align="center">
            <template #default="scope">
              <span>{{ scope.row.doctorName || scope.row.doctor || '未知医生' }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="department" label="科室" min-width="100" align="center">
            <template #default="scope">
              <span>{{ scope.row.department || '未知科室' }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="diagnosis" label="诊断结果" min-width="150">
            <template #default="scope">
              <span>{{ scope.row.diagnosis || '无诊断信息' }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="examination" label="检查项目" min-width="100" align="center">
            <template #default="scope">
              <span>{{ scope.row.examination || '无' }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="surgery" label="手术项目" min-width="100" align="center">
            <template #default="scope">
              <span>{{ scope.row.surgery || '无' }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="prescriptionCount" label="处方药品" min-width="90" align="center">
            <template #default="scope">
              <span>{{ scope.row.prescriptionCount > 0 ? scope.row.prescriptionCount + '种' : '无' }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100" fixed="right" align="center">
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
    <el-dialog v-model="detailDialogVisible" title="病历详情" width="60%" top="5vh">
      <div v-if="selectedRecord" class="medical-record-detail">
        <!-- 报告头部 -->
        <div class="report-header">
          <h1 class="hospital-name">宠物医院信息管理系统病历报告</h1>
          <div class="report-meta">
            <div class="meta-item">
              <span class="meta-label">报告编号：</span>
              <span class="meta-value">MR-{{ selectedRecord.id || '000000' }}</span>
            </div>
            <div class="meta-item">
              <span class="meta-label">就诊日期：</span>
              <span class="meta-value">{{ formatDate(selectedRecord.visitDate) }}</span>
            </div>
          </div>
        </div>
        
        <!-- 预约信息 -->
        <div class="section-block">
          <div class="section-title">预约信息</div>
          <div class="info-grid-3">
            <div class="info-item">
              <span class="label">宠物名称：</span>
              <span class="value">{{ selectedRecord.petName || '未知' }}</span>
            </div>
            <div class="info-item">
              <span class="label">宠物品种：</span>
              <span class="value">{{ selectedRecord.petBreed || '未知' }}</span>
            </div>
            <div class="info-item">
              <span class="label">宠物年龄：</span>
              <span class="value">{{ selectedRecord.petAge || '未知' }}岁</span>
            </div>
            <div class="info-item">
              <span class="label">宠物性别：</span>
              <span class="value">{{ selectedRecord.petGender || '未知' }}</span>
            </div>
            <div class="info-item">
              <span class="label">主人姓名：</span>
              <span class="value">{{ selectedRecord.ownerName || '未知' }}</span>
            </div>
            <div class="info-item">
              <span class="label">联系电话：</span>
              <span class="value">{{ selectedRecord.ownerPhone || '未知' }}</span>
            </div>
            <div class="info-item">
              <span class="label">主治医生：</span>
              <span class="value">{{ selectedRecord.doctorName || selectedRecord.doctor || '未知医生' }}</span>
            </div>
            <div class="info-item">
              <span class="label">就诊科室：</span>
              <span class="value">{{ selectedRecord.department || '未知科室' }}</span>
            </div>
          </div>
        </div>
        
        <!-- 主诉与症状 -->
        <div class="section-block">
          <div class="section-title">主诉与症状</div>
          <div class="info-list">
            <div class="info-item-full">
              <span class="label">主诉：</span>
              <span class="value">{{ selectedRecord.chiefComplaint || selectedRecord.reason || '无主诉信息' }}</span>
            </div>
            <div class="info-item-full">
              <span class="label">临床症状：</span>
              <span class="value">{{ selectedRecord.symptoms || selectedRecord.treatment || '无症状描述' }}</span>
            </div>
          </div>
        </div>
        
        <!-- 检查项目 -->
        <div class="section-block" v-if="selectedRecord.examination">
          <div class="section-title">检查项目</div>
          <div class="info-list">
            <div class="info-item-full">
              <span class="label">检查项目：</span>
              <span class="value">{{ selectedRecord.examination }}</span>
            </div>
            <div class="info-item-full" v-if="selectedRecord.examinationResult">
              <span class="label">检查结果：</span>
              <span class="value result-text">{{ selectedRecord.examinationResult }}</span>
            </div>
          </div>
        </div>
        
        <!-- 手术项目 -->
        <div class="section-block" v-if="selectedRecord.surgery">
          <div class="section-title">手术项目</div>
          <div class="info-list">
            <div class="info-item-full">
              <span class="label">手术项目：</span>
              <span class="value">{{ selectedRecord.surgery }}</span>
            </div>
            <div class="info-item-full" v-if="selectedRecord.surgeryResult">
              <span class="label">手术结果：</span>
              <span class="value result-text">{{ selectedRecord.surgeryResult }}</span>
            </div>
          </div>
        </div>
        
        <!-- 诊断结果 -->
        <div class="section-block">
          <div class="section-title">诊断结果</div>
          <div class="diagnosis-content">{{ selectedRecord.diagnosis || '暂无诊断结果' }}</div>
        </div>
        
        <!-- 处方信息 -->
        <div class="section-block" v-if="selectedRecord.prescription && parsePrescriptionDrugs(selectedRecord.prescription).length > 0">
          <div class="section-title">处方信息</div>
          <div class="prescription-list">
            <div v-for="(drug, index) in parsePrescriptionDrugs(selectedRecord.prescription)" :key="index" class="drug-item">
              <div class="drug-line">
                <span class="drug-name">{{ drug.name }}</span>
                <span class="drug-type">（{{ drug.type || '其他' }}）</span>
                <span class="drug-count">{{ drug.count }} {{ drug.unit || '' }}</span>
              </div>
              <div class="drug-usage">用法用量：{{ drug.usage || '遵医嘱' }}</div>
            </div>
          </div>
        </div>
        
        <!-- 医嘱建议 -->
        <div class="section-block" v-if="selectedRecord.advicesText">
          <div class="section-title">医嘱建议</div>
          <div class="advices-content">{{ selectedRecord.advicesText }}</div>
        </div>
        
        <!-- 备注信息 -->
        <div class="section-block" v-if="selectedRecord.notes">
          <div class="section-title">备注信息</div>
          <div class="notes-content">{{ selectedRecord.notes }}</div>
        </div>
        
        <!-- 报告底部 -->
        <div class="report-footer">
          <div class="footer-left">
            <div class="signature-item">
              <span class="signature-label">主治医生签名：</span>
              <span class="signature-value">{{ selectedRecord.doctorName || selectedRecord.doctor || '________' }}</span>
            </div>
          </div>
          <div class="footer-right">
            <div class="date-item">
              <span class="date-label">报告日期：</span>
              <span class="date-value">{{ formatDate(selectedRecord.visitDate) }}</span>
            </div>
          </div>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
          <el-button type="primary" @click="printRecord">打印报告</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'

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

// 解析处方药品数据
const parsePrescriptionDrugs = (prescription) => {
  try {
    if (!prescription) return []
    const data = JSON.parse(prescription)
    return data.drugs || []
  } catch (error) {
    console.error('解析处方数据失败:', error)
    return []
  }
}

// 获取病历数据
const fetchMedicalRecords = async () => {
  try {
    // 获取用户 ID
    const userId = localStorage.getItem('userId')
    if (!userId) {
      ElMessage.error('用户未登录')
      return
    }
    
    // 获取用户宠物列表
    const petResponse = await fetch(`http://localhost:8080/api/pets?userId=${userId}`)
    const pets = await petResponse.json()
    
    console.log('获取到的宠物列表:', pets)
    
    if (!pets || pets.length === 0) {
      ElMessage.info('您还没有添加宠物')
      medicalRecords.value = []
      return
    }
    
    // 获取每个宠物的病历详情（使用新的联合查询接口）
    const allRecords = []
    for (const pet of pets) {
      try {
        // 调用新的 detail 接口
        const response = await fetch(`http://localhost:8080/api/medical-records/detail?petId=${pet.id}`)
        
        if (!response.ok) {
          console.error(`获取宠物 ${pet.name} 的病历失败，状态码:`, response.status)
          // 如果 detail 接口失败，尝试使用原来的接口
          const oldResponse = await fetch(`http://localhost:8080/api/medical-records?petId=${pet.id}`)
          if (oldResponse.ok) {
            const records = await oldResponse.json()
            const petRecords = records.map(record => ({
              ...record,
              petName: pet.name,
              petBreed: pet.breed,
              petAge: pet.age,
              petGender: pet.gender,
              ownerName: localStorage.getItem('userName') || '未知',
              title: `${formatDate(record.visitDate)} | ${pet.name} | ${record.diagnosis || '无诊断'}`,
              examination: record.examination || '',
              surgery: record.surgery || '',
              prescriptionCount: record.prescription ? (JSON.parse(record.prescription).drugs?.length || 0) : 0,
              chiefComplaint: '暂无主诉',
              symptoms: record.treatment || '无症状描述',
              diagnosis: record.diagnosis || '无诊断结果',
              examinationResult: record.examinationResult || '',
              surgeryResult: record.surgeryResult || '',
              advicesText: record.prescription ? (JSON.parse(record.prescription).advices || '') : ''
            }))
            allRecords.push(...petRecords)
          }
          continue
        }
        
        const records = await response.json()
        
        // 确保 records 是数组
        if (!Array.isArray(records)) {
          console.error('返回的数据不是数组:', records)
          continue
        }
        
        const petRecords = records.map(record => ({
          ...record,
          title: `${formatDate(record.visitDate)} | ${record.petName} | ${record.diagnosis || '无诊断'}`,
          // 添加表格显示字段
          examination: record.examination || '',
          surgery: record.surgery || '',
          prescriptionCount: record.prescription ? (JSON.parse(record.prescription).drugs?.length || 0) : 0,
          // 映射字段到前端显示（后端已经返回，直接使用）
          chiefComplaint: '暂无主诉', // 主诉在预约表中，需要额外关联
          symptoms: record.treatment || '无症状描述',
          diagnosis: record.diagnosis || '无诊断结果',
          examinationResult: record.examinationResult || '',
          surgeryResult: record.surgeryResult || '',
          advicesText: record.prescription ? (JSON.parse(record.prescription).advices || '') : ''
        }))
        allRecords.push(...petRecords)
      } catch (error) {
        console.error(`获取宠物 ${pet.name} 的病历失败:`, error)
      }
    }
    
    medicalRecords.value = allRecords
  } catch (error) {
    console.error('获取病历数据失败:', error)
    ElMessage.error('获取病历数据失败：' + (error.response?.data?.error || error.message || '未知错误'))
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
  padding: 20px;
  background: #fff;
}

/* 报告头部样式 */
.report-header {
  text-align: center;
  padding-bottom: 20px;
  border-bottom: 3px double #303133;
  margin-bottom: 25px;
}

.hospital-name {
  font-size: 24px;
  color: #303133;
  margin-bottom: 15px;
  font-weight: bold;
}

.report-meta {
  display: flex;
  justify-content: center;
  gap: 40px;
  margin-top: 15px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.meta-label {
  font-size: 14px;
  color: #909399;
}

.meta-value {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
}

/* 区块样式 */
.section-block {
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #dcdfe6;
}

.section-block:last-of-type {
  border-bottom: none;
}

.section-title {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 15px;
  padding-bottom: 8px;
  border-bottom: 1px solid #606266;
}

/* 信息网格布局 */
.info-grid-3 {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 15px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.info-item .label {
  font-size: 13px;
  color: #909399;
  font-weight: 500;
}

.info-item .value {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
}

/* 信息列表样式 */
.info-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.info-item-full {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.info-item-full .label {
  font-size: 13px;
  color: #909399;
  font-weight: 500;
}

.info-item-full .value {
  font-size: 14px;
  color: #303133;
  line-height: 1.6;
}

.result-text {
  color: #606266;
  font-weight: 500;
}

/* 诊断框样式 */
.diagnosis-content {
  font-size: 15px;
  color: #303133;
  font-weight: 500;
  line-height: 1.8;
  padding: 10px 0;
}

/* 处方列表样式 */
.prescription-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.drug-item {
  padding-bottom: 12px;
  border-bottom: 1px dashed #dcdfe6;
}

.drug-item:last-child {
  border-bottom: none;
  padding-bottom: 0;
}

.drug-line {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 5px;
}

.drug-name {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
}

.drug-type {
  font-size: 13px;
  color: #909399;
}

.drug-count {
  font-size: 13px;
  color: #606266;
  margin-left: auto;
}

.drug-usage {
  font-size: 13px;
  color: #606266;
  padding-left: 0;
}

/* 医嘱内容样式 */
.advices-content {
  font-size: 14px;
  color: #606266;
  line-height: 1.8;
  padding: 10px 0;
}

/* 备注内容样式 */
.notes-content {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  padding: 10px 0;
}

/* 报告底部样式 */
.report-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 20px;
  margin-top: 25px;
  border-top: 3px double #303133;
}

.footer-left,
.footer-right {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.signature-item,
.date-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.signature-label,
.date-label {
  font-size: 14px;
  color: #909399;
}

.signature-value,
.date-value {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
}

.dialog-footer {
  text-align: right;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .info-grid-3 {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .report-meta {
    flex-direction: column;
    gap: 10px;
  }
  
  .report-footer {
    flex-direction: column;
    gap: 15px;
    align-items: flex-start;
  }
}
</style>