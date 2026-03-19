<template>
  <div class="medical-records-wrapper">
    <el-card class="medical-records-card">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center; gap: 20px;">
          <span>病历查询</span>
          <div style="display: flex; align-items: center; gap: 10px;">
            <el-select v-model="selectedDepartment" placeholder="选择科室" clearable style="width: 150px;" @change="handleDepartmentChange">
              <el-option v-for="dept in departments" :key="dept" :label="dept" :value="dept" />
            </el-select>
            <el-input
              v-model="searchKeyword"
              placeholder="搜索宠物名称或主人姓名"
              style="width: 250px;"
              clearable
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
          </div>
        </div>
      </template>
      
      <!-- 病历列表 -->
      <div class="records-list">
        <el-table :data="paginatedRecords" border style="width: 100%" class="data-table" empty-text="暂无病历记录" v-loading="loading">
          <el-table-column label="报告编号" min-width="100" align="center">
            <template #default="scope">
              <span>MR-{{ scope.row.id }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="petName" label="宠物名称" min-width="100" align="center">
            <template #default="scope">
              <span>{{ scope.row.petName || '未知' }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="visitDate" label="就诊日期" min-width="110" align="center">
            <template #default="scope">
              <span>{{ formatDate(scope.row.visitDate) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="doctorName" label="主治医生" min-width="100" align="center">
            <template #default="scope">
              <span>{{ scope.row.doctorName || '未知医生' }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="department" label="科室" min-width="100" align="center">
            <template #default="scope">
              <span>{{ scope.row.department || '未知科室' }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="diagnosis" label="诊断结果" min-width="150" show-overflow-tooltip>
            <template #default="scope">
              <span>{{ scope.row.diagnosis || '无' }}</span>
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
          <el-table-column prop="prescription" label="处方药品" min-width="90" align="center">
            <template #default="scope">
              <span>{{ scope.row.prescription ? '有' : '无' }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100" fixed="right" align="center">
            <template #default="scope">
              <el-button type="primary" size="small" @click="viewDetail(scope.row)">查看详情</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      
      <!-- 分页组件 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          :small="false"
          :disabled="false"
          :background="true"
          layout="total, sizes, prev, pager, next, jumper"
          :total="filteredRecords.length"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :pager-count="5"
          prev-text="上一页"
          next-text="下一页"
          :page-count="Math.ceil(filteredRecords.length / pageSize)"
          :locale="{
            total: `共 {total} 条记录`,
            pagesize: '条/页',
            goto: '跳至',
            pageClassifier: '页'
          }"
        />
      </div>
    </el-card>
    
    <!-- 详情弹窗 -->
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
              <span class="value">{{ selectedRecord.doctorName || '未知医生' }}</span>
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
              <span class="value">{{ selectedRecord.chiefComplaint || '无主诉信息' }}</span>
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
              <span class="signature-value">{{ selectedRecord.doctorName || '________' }}</span>
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
import { getAllMedicalRecordDetail, getMedicalRecordDetailByDepartment, getDepartments } from '@/services/api'

// 搜索关键词
const searchKeyword = ref('')

// 选中的科室
const selectedDepartment = ref('')

// 科室列表
const departments = ref([])

// 加载状态
const loading = ref(false)

// 详情弹窗可见性
const detailDialogVisible = ref(false)

// 选中的病历记录
const selectedRecord = ref({})

// 分页相关数据
const currentPage = ref(1)
const pageSize = ref(10)

// 病历数据
const medicalRecords = ref([])

// 获取科室列表
const fetchDepartments = async () => {
  try {
    const res = await getDepartments()
    departments.value = res.data.map(dept => dept.name)
  } catch (error) {
    console.error('获取科室列表失败:', error)
  }
}

// 获取病历数据
const fetchMedicalRecords = async () => {
  try {
    loading.value = true
    let res
    if (selectedDepartment.value) {
      res = await getMedicalRecordDetailByDepartment(selectedDepartment.value)
    } else {
      res = await getAllMedicalRecordDetail()
    }
    medicalRecords.value = res.data || []
  } catch (error) {
    console.error('获取病历数据失败:', error)
    ElMessage.error('获取病历数据失败')
  } finally {
    loading.value = false
  }
}

// 科室变化处理
const handleDepartmentChange = () => {
  currentPage.value = 1
  fetchMedicalRecords()
}

// 格式化日期
const formatDate = (date) => {
  if (!date) return '未知'
  if (typeof date === 'string') return date
  return date.toString()
}

// 计算过滤后的病历数据
const filteredRecords = computed(() => {
  if (!searchKeyword.value) {
    return medicalRecords.value
  }
  
  const keyword = searchKeyword.value.toLowerCase()
  return medicalRecords.value.filter(record => 
    (record.petName && record.petName.toLowerCase().includes(keyword)) || 
    (record.ownerName && record.ownerName.toLowerCase().includes(keyword))
  )
})

// 计算当前页的病历数据
const paginatedRecords = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredRecords.value.slice(start, end)
})

// 分页事件处理
const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
}

const handleCurrentChange = (val) => {
  currentPage.value = val
}

// 查看详情
const viewDetail = (record) => {
  selectedRecord.value = {
    ...record,
    advicesText: record.prescription ? (parsePrescriptionAdvices(record.prescription)) : ''
  }
  detailDialogVisible.value = true
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

// 解析处方医嘱
const parsePrescriptionAdvices = (prescription) => {
  try {
    if (!prescription) return ''
    const data = JSON.parse(prescription)
    return data.advices || ''
  } catch (error) {
    console.error('解析处方医嘱失败:', error)
    return ''
  }
}

// 打印病历
const printRecord = () => {
  ElMessage.info('打印功能已触发')
}

// 初始化
onMounted(async () => {
  await fetchDepartments()
  await fetchMedicalRecords()
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
  width: auto;
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
  width: auto;
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
  }
}
</style>