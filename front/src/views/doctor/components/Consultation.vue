<template>
  <div style="height: calc(100vh - 100px); display: flex; gap: 20px;">
    <!-- 候诊列表 -->
    <div style="width: 300px; background: #fff; border-radius: 4px; padding: 10px; display: flex; flex-direction: column;">
      <div style="padding-bottom: 10px; border-bottom: 1px solid #eee; font-weight: bold; display: flex; justify-content: space-between;">
        <span>候诊列表 ({{ waitingList.length }})</span>
        <el-button type="primary" link icon="Refresh" @click="$message.success('列表已刷新')"></el-button>
      </div>
      <div style="flex: 1; overflow-y: auto; margin-top: 10px;">
        <el-card
            v-for="(p, index) in waitingList"
            :key="p.id"
            class="patient-card"
            :class="{ 
              active: currentPatient && currentPatient.id === p.id
            }"
            shadow="hover"
            :body-style="{ padding: '10px' }"
        >
          <!-- 患者信息标题行 -->
          <div style="display: flex; justify-content: space-between; align-items: center; cursor: pointer;" @click="selectPatient(p)">
            <div style="flex: 1;">
              <div style="display: flex; justify-content: space-between; align-items: center;">
                <span style="font-weight: bold;">{{ p.no }} {{ p.name }}</span>
                <el-tag size="small" :type="currentPatient && currentPatient.id === p.id ? 'success' : 'warning'">{{ currentPatient && currentPatient.id === p.id ? '正在候诊' : '待候诊' }}</el-tag>
              </div>
              <div style="color: #999; font-size: 12px; margin-top: 5px;">
                主人: {{ p.owner }} | {{ p.breed }}
              </div>
            </div>
            <el-icon style="color: #909399;" :size="16">
              <ArrowRight v-if="expandedPatientId !== p.id" />
              <ArrowDown v-else />
            </el-icon>
          </div>
          
          <!-- 展开的患者详细信息 -->
          <div v-if="expandedPatientId === p.id" style="margin-top: 10px; padding-top: 10px; border-top: 1px solid #eee;">
            <div style="font-size: 13px; color: #606266; margin-bottom: 8px;">
              <span style="font-weight: bold;">主诉：</span>{{ p.reason }}
            </div>
            
            <div style="display: flex; gap: 10px; margin-top: 10px;">
              <el-button 
                v-if="p.status === 'waiting'" 
                type="primary" 
                size="small" 
                @click.stop="callPatient(p)"
              >
                开始就诊
              </el-button>
              <el-button 
                v-else-if="p.status === 'in_progress'" 
                type="success" 
                size="small" 
                @click.stop="loadExistingForm(p)"
              >
                继续就诊
              </el-button>
              <el-button 
                v-else 
                type="info" 
                size="small" 
                @click.stop="loadExistingForm(p)"
              >
                查看病历
              </el-button>
            </div>
          </div>
        </el-card>
        <el-empty v-if="waitingList.length === 0" description="暂无候诊" :image-size="60"></el-empty>
      </div>
    </div>

    <!-- 诊疗表单 -->
    <div style="flex: 1; background: #fff; border-radius: 4px; padding: 20px; overflow-y: auto;">
      <div v-if="!currentPatient" style="height: 100%; display: flex; justify-content: center; align-items: center; flex-direction: column; color: #909399;">
        <el-icon size="60"><Service /></el-icon>
        <p style="margin-top: 10px;">请从左侧列表点击患者卡片展开信息，然后点击"开始就诊"按钮</p>
      </div>

      <div v-else>
        <!-- 页面顶部操作栏 -->
        <div style="display: flex; justify-content: space-between; align-items: center; border-bottom: 2px solid #409eff; padding-bottom: 15px; margin-bottom: 20px;">
          <div>
            <span style="font-size: 20px; font-weight: bold; margin-right: 10px;">{{ currentPatient.name }}</span>
            <el-tag>{{ currentPatient.breed }}</el-tag>
            <el-tag type="info" style="margin-left: 5px;">{{ currentPatient.age }}岁</el-tag>
            <el-tag type="warning" style="margin-left: 5px;">体重: {{ currentPatient.weight }}kg</el-tag>
            <el-tag v-if="medicalForm.isDraftSaved" type="success" style="margin-left: 5px;">已保存</el-tag>
          </div>
          <div>
            <el-button type="primary" plain @click="viewHistory">查看历史病历</el-button>
            <el-button type="success" @click="saveDraft">保存草稿</el-button>
            <el-button type="primary" @click="finishDiagnose">完成诊疗</el-button>
          </div>
        </div>

        <el-form label-position="top" :model="medicalForm">
          <el-row :gutter="20">
            <el-col :span="24">
              <div class="section-title">临床检查</div>
            </el-col>
            <el-col :span="12">
              <el-form-item label="主诉 (Symptom)">
                <el-input v-model="currentPatient.reason" type="textarea" :rows="2"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="临床症状">
                <el-input v-model="medicalForm.symptoms" type="textarea" :rows="2" placeholder="体温、心率、呼吸等..."></el-input>
              </el-form-item>
            </el-col>

            <el-col :span="24">
              <div class="section-title">初步诊断结果 (Preliminary Diagnosis)</div>
              <el-form-item>
                <el-input v-model="medicalForm.diagnosis" placeholder="请输入初步诊断结果"></el-input>
              </el-form-item>
            </el-col>

            <el-col :span="24">
              <div class="section-title">检查申请</div>
              <el-form-item label="检查项目">
                <el-select 
                  v-model="medicalForm.selectedExamination" 
                  placeholder="请选择检查项目（可不选）" 
                  clearable
                  style="width: 100%"
                >
                  <el-option label="不开检查单" value=""></el-option>
                  <el-option 
                    v-for="item in examinationOptions" 
                    :key="item.value" 
                    :label="item.label" 
                    :value="item.value"
                  ></el-option>
                </el-select>
                <div style="margin-top: 10px; font-size: 14px; color: #666;">
                  <div v-if="!medicalForm.selectedExamination">当前选择：不开检查单，可直接填写诊断结果</div>
                  <div v-else>当前选择：{{ medicalForm.selectedExamination }}，需填写检查结果</div>
                </div>
              </el-form-item>
              
              <!-- 检查结果输入框 -->
              <el-form-item label="检查结果" v-if="medicalForm.selectedExamination && medicalForm.selectedExamination !== ''">
                <el-input v-model="medicalForm.examinationResult" type="textarea" :rows="3" placeholder="请输入检查结果" clearable></el-input>
              </el-form-item>
            </el-col>

            <el-col :span="24">
              <div class="section-title">手术申请</div>
              <el-form-item label="手术项目">
                <el-select 
                  v-model="medicalForm.selectedSurgery" 
                  placeholder="请选择手术项目（可不选）" 
                  clearable
                  style="width: 100%"
                >
                  <el-option label="不开手术单" value=""></el-option>
                  <el-option 
                    v-for="item in surgeryOptions" 
                    :key="item.value" 
                    :label="item.label" 
                    :value="item.value"
                  ></el-option>
                </el-select>
                <div style="margin-top: 10px; font-size: 14px; color: #666;">
                  <div v-if="!medicalForm.selectedSurgery">当前选择：不开手术单</div>
                  <div v-else>当前选择：{{ medicalForm.selectedSurgery }}，需填写手术结果</div>
                </div>
              </el-form-item>
              
              <!-- 手术结果输入框 -->
              <el-form-item label="手术结果" v-if="medicalForm.selectedSurgery && medicalForm.selectedSurgery !== ''">
                <el-input v-model="medicalForm.surgeryResult" type="textarea" :rows="3" placeholder="请输入手术结果" clearable></el-input>
              </el-form-item>
            </el-col>

            <el-col :span="24">
              <div class="section-title">
                处方开具 (Prescription)
                <el-button type="primary" size="small" @click="addDrug" style="float: right;">
                  <el-icon><Plus /></el-icon>
                  添加药品
                </el-button>
              </div>
              <el-table :data="medicalForm.drugs" border size="small">
                <el-table-column label="药品类别" width="150">
                  <template #default="scope">
                    <el-select v-model="scope.row.type" placeholder="选择类别" @change="onDrugTypeChange(scope.row)">
                      <el-option v-for="type in drugTypes" :key="type" :label="type" :value="type"></el-option>
                    </el-select>
                  </template>
                </el-table-column>
                <el-table-column label="药品名称" width="200">
                  <template #default="scope">
                    <el-select v-model="scope.row.name" placeholder="选择药品" filterable :disabled="!scope.row.type" @change="onDrugNameChange(scope.row)">
                      <el-option v-for="drug in getDrugsByType(scope.row.type)" :key="drug.id" :label="drug.name" :value="drug.name">
                        <span style="float: left">{{ drug.name }}</span>
                        <span style="float: right; color: #8492a6; font-size: 13px">库存:{{ drug.stock }}</span>
                      </el-option>
                    </el-select>
                  </template>
                </el-table-column>
                <el-table-column label="数量" width="120">
                  <template #default="scope">
                    <el-input-number v-model="scope.row.count" :min="1" size="small" style="width: 80px;"></el-input-number>
                  </template>
                </el-table-column>
                <el-table-column label="单位" width="100">
                  <template #default="scope">
                    <el-input v-model="scope.row.unit" size="small" placeholder="单位" :disabled="true"></el-input>
                  </template>
                </el-table-column>
                <el-table-column label="用法用量 (医嘱)">
                  <template #default="scope">
                    <el-input v-model="scope.row.usage" size="small" placeholder="例如：每日2次，饭后喂食"></el-input>
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="80">
                  <template #default="scope">
                    <el-button type="danger" size="small" @click="removeDrug(scope.$index)">删除</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-col>

            <el-col :span="24" style="margin-top: 20px;">
              <div class="section-title">附加医嘱</div>
              <el-form-item>
                <el-input 
                  v-model="medicalForm.advicesText" 
                  type="textarea" 
                  :rows="3" 
                  placeholder="请输入附加医嘱，例如：禁食24小时、限制运动、一周后复查、佩戴伊丽莎白圈等..."
                ></el-input>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </div>
    </div>
  </div>

  <!-- 历史病历抽屉 -->
  <el-drawer v-model="historyDrawer" title="该宠物历史就诊记录" size="60%">
    <div v-if="currentPatient">
      <el-table :data="patientHistory" border style="width: 100%" class="data-table" empty-text="暂无历史就诊记录">
        <el-table-column prop="visitDate" label="就诊日期" width="120" align="center">
          <template #default="scope">
            <span style="font-weight: bold;">{{ scope.row.visitDate }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="doctorName" label="接诊医生" width="120" align="center"></el-table-column>
        <el-table-column prop="diagnosis" label="诊断结果" min-width="200" show-overflow-tooltip></el-table-column>
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
            <el-button type="primary" size="small" @click="viewMedicalRecordDetail(scope.row)">查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailDialogVisible" title="病历详情" width="60%" top="5vh">
      <div v-if="selectedRecord" class="medical-record-detail">
        <div class="report-header">
          <h1 class="hospital-name">宠物医院信息管理系统病历报告</h1>
          <div class="report-meta">
            <div class="meta-item">
              <span class="meta-label">报告编号：</span>
              <span class="meta-value">MR-{{ selectedRecord.id || '000000' }}</span>
            </div>
            <div class="meta-item">
              <span class="meta-label">就诊日期：</span>
              <span class="meta-value">{{ selectedRecord.visitDate }}</span>
            </div>
          </div>
        </div>

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
              <span class="label">主治医生：</span>
              <span class="value">{{ selectedRecord.doctorName || '未知医生' }}</span>
            </div>
            <div class="info-item">
              <span class="label">就诊科室：</span>
              <span class="value">{{ selectedRecord.department || '未知科室' }}</span>
            </div>
          </div>
        </div>

        <div class="section-block">
          <div class="section-title">主诉与症状</div>
          <div class="info-list">
            <div class="info-item-full">
              <span class="label">临床症状：</span>
              <span class="value">{{ selectedRecord.symptoms || selectedRecord.treatment || '无' }}</span>
            </div>
          </div>
        </div>

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

        <div class="section-block">
          <div class="section-title">诊断结果</div>
          <div class="diagnosis-content">{{ selectedRecord.diagnosis || '暂无诊断结果' }}</div>
        </div>

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

        <div class="section-block" v-if="parsePrescriptionAdvices(selectedRecord.prescription)">
          <div class="section-title">医嘱建议</div>
          <div class="advices-content">{{ parsePrescriptionAdvices(selectedRecord.prescription) }}</div>
        </div>

        <div class="section-block" v-if="selectedRecord.treatment">
          <div class="section-title">治疗方案</div>
          <div class="advices-content">{{ selectedRecord.treatment }}</div>
        </div>

        <div class="section-block" v-if="selectedRecord.notes">
          <div class="section-title">备注信息</div>
          <div class="notes-content">{{ selectedRecord.notes }}</div>
        </div>

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
              <span class="date-value">{{ selectedRecord.visitDate }}</span>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>
  </el-drawer>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Service, Refresh, Plus, Delete, ArrowRight, ArrowDown } from '@element-plus/icons-vue'
import { startConsultation as startConsultationAPI, finishConsultation as finishConsultationAPI, getFeeItems, addMedicalRecord, getMedicalRecordDetailsByDoctorId, updateMedicalRecord, addBilling } from '@/services/api'

// 定义props
const props = defineProps({
  waitingList: {
    type: Array,
    required: true
  },
  drugOptions: {
    type: Array,
    required: true
  }
})

// 定义emits
const emit = defineEmits(['call-patient', 'finish-diagnose', 'refresh-list'])

// 响应式状态
const historyDrawer = ref(false)
const currentPatient = ref(null)
const expandedPatientId = ref(null)
const examinationOptions = ref([]) // 检查项目选项
const surgeryOptions = ref([]) // 手术项目选项
const patientHistory = ref([]) // 患者历史病历数据
const detailDialogVisible = ref(false)
const selectedRecord = ref(null)

const medicalForm = reactive({
  symptoms: '',
  diagnosis: '',
  drugs: [],
  advicesText: '',
  selectedExamination: '',
  examinationStatus: '',
  selectedSurgery: '',
  surgeryStatus: '',
  isDraftSaved: false
})

// 组件挂载时加载检查费和手术费数据
onMounted(async () => {
  await loadExaminationOptions()
  await loadSurgeryOptions()
})

// 查看历史病历
const viewHistory = async () => {
  if (!currentPatient.value) {
    ElMessage.warning('请先选择患者')
    return
  }

  try {
    const doctorId = localStorage.getItem('userId')
    const medicalRecordsRes = await getMedicalRecordDetailsByDoctorId(parseInt(doctorId))
    const medicalRecords = medicalRecordsRes.data || []

    const patientRecords = medicalRecords.filter(record =>
      record.petId === currentPatient.value.petId || record.petId === currentPatient.value.id
    )

    patientHistory.value = patientRecords.map(record => ({
      id: record.id,
      visitDate: record.visitDate,
      doctorName: record.doctorName || '未知医生',
      diagnosis: record.diagnosis || '暂无诊断',
      examination: record.examination || '',
      surgery: record.surgery || '',
      prescriptionCount: record.prescription ? JSON.parse(record.prescription).drugs?.length || 0 : 0,
      fullRecord: record
    }))

    historyDrawer.value = true
  } catch (error) {
    console.error('获取历史病历失败:', error)
    ElMessage.error('获取历史病历失败')
  }
}

// 查看病历详情
const viewMedicalRecordDetail = (record) => {
  selectedRecord.value = record.fullRecord || record
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

// 加载检查项目选项
const loadExaminationOptions = async () => {
  try {
    const res = await getFeeItems()
    const feeItems = res.data || []
    // 过滤出category为"检查费"的项目
    examinationOptions.value = feeItems
      .filter(item => item.category === '检查费' && item.isActive)
      .map(item => ({
        label: item.itemName,
        value: item.itemName
      }))
    console.log('检查项目选项:', examinationOptions.value)
  } catch (error) {
    console.error('加载检查项目失败:', error)
    // 如果加载失败，使用默认选项
    examinationOptions.value = [
      { label: 'CT检查', value: 'CT检查' },
      { label: 'X光检查', value: 'X光检查' },
      { label: 'B超检查', value: 'B超检查' },
      { label: '血液检查', value: '血液检查' },
      { label: '尿液检查', value: '尿液检查' }
    ]
  }
}

// 药品类别列表
const drugTypes = ref([
  '抗生素', '消炎药', '疫苗', '驱虫药', '营养补充剂', 
  '消化系统药', '皮肤病药', '眼药水', '耳药', '麻醉药'
])

// 根据类别获取药品列表
const getDrugsByType = (type) => {
  if (!type) return []
  return props.drugOptions.filter(drug => drug.type === type)
}

// 药品类别选择变化事件
const onDrugTypeChange = (row) => {
  // 清空之前选择的药品名称和单位
  row.name = ''
  row.unit = ''
}

// 药品名称选择变化事件
const onDrugNameChange = (row) => {
  if (row.name) {
    // 根据选择的药品名称获取对应的单位
    const selectedDrug = props.drugOptions.find(drug => drug.name === row.name)
    if (selectedDrug) {
      row.unit = selectedDrug.unit || '盒'
    }
  } else {
    row.unit = ''
  }
}

// 加载手术项目选项
const loadSurgeryOptions = async () => {
  try {
    const res = await getFeeItems()
    const feeItems = res.data || []
    // 过滤出category为"手术费"的项目
    surgeryOptions.value = feeItems
      .filter(item => item.category === '手术费' && item.isActive)
      .map(item => ({
        label: item.itemName,
        value: item.itemName
      }))
    console.log('手术项目选项:', surgeryOptions.value)
  } catch (error) {
    console.error('加载手术项目失败:', error)
    // 如果加载失败，使用默认选项
    surgeryOptions.value = [
      { label: '绝育手术', value: '绝育手术' },
      { label: '骨折修复', value: '骨折修复' },
      { label: '肿瘤切除', value: '肿瘤切除' },
      { label: '剖腹产', value: '剖腹产' },
      { label: '其他手术', value: '其他手术' }
    ]
  }
}

// 保存草稿（保存到病历表，不修改预约状态）
const saveDraft = async () => {
  if (!currentPatient.value) {
    ElMessage.warning('请先选择患者')
    return
  }

  try {
    // 获取当前医生的病历记录
    const doctorId = localStorage.getItem('userId')
    const res = await getMedicalRecordDetailsByDoctorId(doctorId)
    const medicalRecords = res.data || []
    
    // 查找与该患者相关的最近病历记录
    const patientRecords = medicalRecords.filter(record => 
      record.appointmentId === currentPatient.value.id
    )
    
    let existingRecordId = null
    if (patientRecords.length > 0) {
      // 使用最新的记录ID
      existingRecordId = patientRecords[patientRecords.length - 1].id
    }

    // 构建病历数据
      const medicalRecordData = {
        petId: currentPatient.value.petId || currentPatient.value.id,
        doctorId: parseInt(doctorId),
        appointmentId: currentPatient.value.id,
        visitDate: new Date().toISOString().split('T')[0],
        diagnosis: medicalForm.diagnosis || '',
        treatment: medicalForm.symptoms || '',
        prescription: JSON.stringify({
          drugs: medicalForm.drugs,
          advices: medicalForm.advicesText
        }),
        examination: medicalForm.selectedExamination || '',
        examinationResult: medicalForm.examinationResult || '',
        surgery: medicalForm.selectedSurgery || '',
        surgeryResult: medicalForm.surgeryResult || '',
        notes: medicalForm.notes || '',
        followUpRequired: false,
        followUpDate: null
      }

    // 如果存在记录，则更新；否则创建新记录
    if (existingRecordId) {
      await updateMedicalRecord(existingRecordId, medicalRecordData)
      ElMessage.success('草稿已更新到病历')
    } else {
      await addMedicalRecord(medicalRecordData)
      ElMessage.success('草稿已保存到病历')
    }
    
    medicalForm.isDraftSaved = true
    setTimeout(() => {
      medicalForm.isDraftSaved = false
    }, 3000)
  } catch (error) {
    console.error('保存草稿失败:', error)
    ElMessage.error('保存草稿失败')
  }
}

const togglePatient = (patient) => {
  if (expandedPatientId.value === patient.id) {
    expandedPatientId.value = null
  } else {
    expandedPatientId.value = patient.id
  }
}

const selectPatient = (patient) => {
  if (patient.status === 'waiting' || patient.status === 'in_progress') {
    if (expandedPatientId.value === patient.id) {
      loadExistingForm(patient)
    } else {
      expandedPatientId.value = patient.id
    }
  }
}

const loadExistingForm = async (patient) => {
  try {
    // 获取当前医生的病历记录
    const doctorId = localStorage.getItem('userId')
    const res = await getMedicalRecordDetailsByDoctorId(doctorId)
    const medicalRecords = res.data || []

    // 查找与该患者相关的最近病历记录
    const patientRecords = medicalRecords.filter(record =>
      record.appointmentId === patient.id
    )
    
    // 如果有病历记录，加载最近的一条
    if (patientRecords.length > 0) {
      const latestRecord = patientRecords[patientRecords.length - 1] // 获取最新的记录
      
      // 解析处方信息
      let prescriptionData = { drugs: [], advices: '' }
      try {
        if (latestRecord.prescription) {
          prescriptionData = JSON.parse(latestRecord.prescription)
        }
      } catch (error) {
        console.error('解析处方数据失败:', error)
      }
      
      // 加载数据到表单
      medicalForm.symptoms = latestRecord.treatment || ''
      medicalForm.diagnosis = latestRecord.diagnosis || ''
      medicalForm.drugs = prescriptionData.drugs || []
      medicalForm.advicesText = prescriptionData.advices || ''
      medicalForm.notes = latestRecord.notes || ''
      
      // 直接使用新的字段
      medicalForm.selectedExamination = latestRecord.examination || ''
      medicalForm.examinationResult = latestRecord.examinationResult || ''
      medicalForm.selectedSurgery = latestRecord.surgery || ''
      medicalForm.surgeryResult = latestRecord.surgeryResult || ''
      
      // 根据之前保存的数据设置检查状态和手术状态
      if (medicalForm.selectedExamination && medicalForm.selectedExamination !== '') {
        medicalForm.examinationStatus = '等待检查中...'
      } else {
        medicalForm.examinationStatus = ''
      }
      
      if (medicalForm.selectedSurgery && medicalForm.selectedSurgery !== '') {
        medicalForm.surgeryStatus = '等待手术中...'
      } else {
        medicalForm.surgeryStatus = ''
      }
      
      ElMessage.success('已加载之前保存的数据')
    } else {
      // 没有找到病历记录，清空表单
      medicalForm.symptoms = ''
      medicalForm.diagnosis = ''
      medicalForm.drugs = []
      medicalForm.advicesText = ''
      medicalForm.selectedExamination = ''
      medicalForm.selectedSurgery = ''
      medicalForm.examinationStatus = ''
      medicalForm.surgeryStatus = ''
      
      ElMessage.info('没有找到之前保存的数据，已清空表单')
    }
  } catch (error) {
    console.error('加载病历数据失败:', error)
    ElMessage.error('加载病历数据失败')
    
    // 如果加载失败，清空表单
    medicalForm.symptoms = ''
    medicalForm.diagnosis = ''
    medicalForm.drugs = []
    medicalForm.advicesText = ''
    medicalForm.selectedExamination = ''
    medicalForm.selectedSurgery = ''
    medicalForm.examinationStatus = ''
    medicalForm.surgeryStatus = ''
  }
  
  // 设置当前患者
  currentPatient.value = patient
  medicalForm.examinationStatus = ''
  medicalForm.surgeryStatus = ''
  medicalForm.isDraftSaved = false
  emit('call-patient', patient)
}

const callPatient = async (patient) => {
  try {
    const doctorId = localStorage.getItem('userId')
    // 调用后端 API 更新预约状态为"正在就诊"
    await startConsultationAPI(patient.id, doctorId)
    ElMessage.success(`已开始接诊：${patient.name}`)
    expandedPatientId.value = null
    
    // 发出刷新列表事件
    emit('refresh-list')
  } catch (error) {
    console.error('开始就诊失败:', error)
    ElMessage.error('开始就诊失败')
    return
  }
  
  currentPatient.value = patient
  medicalForm.symptoms = ''
  medicalForm.diagnosis = ''
  medicalForm.drugs = []
  medicalForm.advicesText = ''
  medicalForm.selectedExamination = ''
  medicalForm.examinationStatus = ''
  medicalForm.selectedSurgery = ''
  medicalForm.surgeryStatus = ''
  medicalForm.isDraftSaved = false
  emit('call-patient', patient)
}

const addDrug = () => {
  medicalForm.drugs.push({ type: '', name: '', count: 1, unit: '', usage: '' })
}

const removeDrug = (index) => {
  medicalForm.drugs.splice(index, 1)
}

const finishDiagnose = () => {
  // 检查是否填写了诊断结果
  if (!medicalForm.diagnosis) {
    return ElMessage.warning('请填写诊断结果')
  }

  // 构建完成信息
  let message = `确定完成对 [${currentPatient.value.name}] 的诊疗吗？`
  
  // 如果有检查申请，添加提醒
  if (medicalForm.selectedExamination) {
    message += `\n\n注意：已申请检查项目：${medicalForm.selectedExamination}`
    if (medicalForm.examinationResult) {
      message += `，检查结果：${medicalForm.examinationResult}`
    }
  }
  
  // 如果有手术申请，添加提醒
  if (medicalForm.selectedSurgery) {
    message += `\n\n注意：已申请手术项目：${medicalForm.selectedSurgery}`
    if (medicalForm.surgeryResult) {
      message += `，手术结果：${medicalForm.surgeryResult}`
    }
  }
  
  // 如果有处方，添加提醒
  if (medicalForm.drugs && medicalForm.drugs.length > 0) {
    message += `\n\n注意：已开具处方，请确保药品和用法用量正确。`
  }

  ElMessageBox.confirm(message, '完成确认', {
    confirmButtonText: '确定提交',
    cancelButtonText: '取消',
    type: 'success'
  }).then(async () => {
    try {
      const doctorId = localStorage.getItem('userId')
      
      // 查找与该患者相关的最近病历记录
      const medicalRecordsRes = await getMedicalRecordDetailsByDoctorId(parseInt(doctorId))
      const medicalRecords = medicalRecordsRes.data || []
      const patientRecords = medicalRecords.filter(record => 
        record.appointmentId === currentPatient.value.id
      )
      
      let existingRecordId = null
      if (patientRecords.length > 0) {
        // 找到最近的记录
        const latestRecord = patientRecords[patientRecords.length - 1]
        existingRecordId = latestRecord.id
      }

      // 构建病历数据
      const medicalRecordData = {
        petId: currentPatient.value.petId || currentPatient.value.id, // 优先使用petId，如果没有则使用id
        doctorId: parseInt(doctorId),
        appointmentId: currentPatient.value.id, // 使用预约ID作为病历的关联
        visitDate: new Date().toISOString().split('T')[0], // 当前日期
        diagnosis: medicalForm.diagnosis || '',
        treatment: medicalForm.symptoms || '', // 临床症状作为治疗方案
        prescription: JSON.stringify({
          drugs: medicalForm.drugs,
          advices: medicalForm.advicesText
        }),
        examination: medicalForm.selectedExamination || '',
        examinationResult: medicalForm.examinationResult || '',
        surgery: medicalForm.selectedSurgery || '',
        surgeryResult: medicalForm.surgeryResult || '',
        notes: medicalForm.notes || '',
        followUpRequired: false,
        followUpDate: null
      }

      // 保存病历到数据库
      if (existingRecordId) {
        // 更新现有记录
        await updateMedicalRecord(existingRecordId, medicalRecordData)
      } else {
        // 新增记录
        await addMedicalRecord(medicalRecordData)
      }
      
      // 创建账单和账单明细
      await createBillingForConsultation()
      
      // 调用后端 API 更新预约状态为"就诊完成"
      await finishConsultationAPI(currentPatient.value.id, doctorId)
      
      ElMessage.success('病历提交成功，账单已生成，请前往缴费')
      
      // 发出刷新列表事件
      emit('refresh-list')
    } catch (error) {
      console.error('完成就诊失败:', error)
      ElMessage.error('完成就诊失败')
      return
    }
    
    emit('finish-diagnose', currentPatient.value.id)
    currentPatient.value = null
  })
}

// 创建账单
const createBillingForConsultation = async () => {
  try {
    const billingItems = []
    const feeItemsRes = await getFeeItems()
    const feeItems = feeItemsRes.data || []
    
    // 1. 添加挂号费（根据医生级别）
    const doctorTitle = currentPatient.value.doctorTitle || '主治医师'
    let registrationFee = null
    if (doctorTitle.includes('主任') || doctorTitle.includes('专家')) {
      registrationFee = feeItems.find(item => item.itemName.includes('专家') && item.category === '诊查费')
    } else {
      registrationFee = feeItems.find(item => item.itemName.includes('普通') && item.category === '诊查费')
    }
    
    if (registrationFee) {
      billingItems.push({
        itemName: registrationFee.itemName,
        category: '挂号费',
        quantity: 1,
        unitPrice: registrationFee.unitPrice,
        totalPrice: registrationFee.unitPrice,
        unit: registrationFee.unit || '次',
        description: doctorTitle.includes('主任') ? '主任医师及以上专家门诊诊查费' : '主治医生普通门诊诊查费'
      })
    }
    
    // 2. 添加检查费
    if (medicalForm.selectedExamination) {
      const examFee = feeItems.find(item => item.itemName === medicalForm.selectedExamination && item.category === '检查费')
      if (examFee) {
        billingItems.push({
          itemName: examFee.itemName,
          category: '检查费',
          quantity: 1,
          unitPrice: examFee.unitPrice,
          totalPrice: examFee.unitPrice,
          unit: examFee.unit || '次',
          description: medicalForm.examinationResult || ''
        })
      }
    }
    
    // 3. 添加手术费
    if (medicalForm.selectedSurgery) {
      const surgeryFee = feeItems.find(item => item.itemName === medicalForm.selectedSurgery && item.category === '手术费')
      if (surgeryFee) {
        billingItems.push({
          itemName: surgeryFee.itemName,
          category: '手术费',
          quantity: 1,
          unitPrice: surgeryFee.unitPrice,
          totalPrice: surgeryFee.unitPrice,
          unit: surgeryFee.unit || '台',
          description: medicalForm.surgeryResult || ''
        })
      }
    }
    
    // 4. 添加药品费
    if (medicalForm.drugs && medicalForm.drugs.length > 0) {
      for (const drug of medicalForm.drugs) {
        // 优先从drugOptions中获取价格（因为那里有完整的药品信息）
        const drugFromOptions = props.drugOptions.find(item => item.name === drug.name)
        let unitPrice = 0
        let unit = '盒'

        if (drugFromOptions) {
          unitPrice = drugFromOptions.price || 0
          unit = drugFromOptions.unit || '盒'
        } else {
          // 如果drugOptions中没有，则从feeItems中查找
          const drugFee = feeItems.find(item => item.itemName === drug.name && item.category === '药品费')
          if (drugFee) {
            unitPrice = drugFee.unitPrice
            unit = drugFee.unit || '盒'
          }
        }

        billingItems.push({
          itemName: drug.name,
          category: '药品费',
          quantity: drug.count || 1,
          unitPrice: unitPrice,
          totalPrice: unitPrice * (drug.count || 1),
          unit: drug.unit || unit,
          description: drug.usage || ''
        })
      }
    }
    
    // 如果有费用项目，创建账单
    if (billingItems.length > 0) {
      const billingData = {
        userId: currentPatient.value.ownerId || localStorage.getItem('userId'),
        appointmentId: currentPatient.value.id,
        description: billingItems.map(item => item.itemName).join('、'),
        items: billingItems
      }
      
      await addBilling(billingData)
      console.log('账单创建成功，明细：', billingItems)
    }
  } catch (error) {
    console.error('创建账单失败:', error)
  }
}

// 暴露给父组件的方法
defineExpose({
  currentPatient,
  medicalForm
})
</script>

<style scoped>
.patient-card {
  cursor: pointer;
  margin-bottom: 10px;
  border-left: 4px solid transparent;
  transition: 0.3s;
}

.patient-card:hover {
  background-color: #f5f7fa;
}

.patient-card.active {
  border-left-color: #67c23a;
  background-color: #f0f9ff;
}

.patient-card.selected {
  border-left-color: #409eff;
  background-color: #ecf5ff;
}

.patient-card.active .patient-info,
.patient-card.selected .patient-info {
  color: #303133;
}

.patient-info {
  transition: 0.3s;
}

.consultation-box {
  background: #fff;
  padding: 20px;
  border-radius: 4px;
  height: 100%;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
}

.section-title {
  font-weight: bold;
  border-left: 4px solid #409eff;
  padding-left: 10px;
  margin-bottom: 15px;
  font-size: 16px;
}

.medical-record-detail {
  max-height: 70vh;
  overflow-y: auto;
  padding: 20px;
  background: #fff;
}

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

.section-block {
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #dcdfe6;
}

.section-block:last-of-type {
  border-bottom: none;
}

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

.diagnosis-content {
  font-size: 15px;
  color: #303133;
  font-weight: 500;
  line-height: 1.8;
  padding: 10px 0;
}

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

.advices-content,
.notes-content {
  font-size: 14px;
  color: #606266;
  line-height: 1.8;
  padding: 10px 0;
}

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
  gap: 8px;
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
</style>