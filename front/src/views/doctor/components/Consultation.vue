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
            :class="{ active: currentPatient && currentPatient.id === p.id }"
            shadow="hover"
            :body-style="{ padding: '10px' }"
            @click="callPatient(p)"
        >
          <div style="display: flex; justify-content: space-between;">
            <span style="font-weight: bold;">{{ p.no }} {{ p.name }}</span>
            <el-tag size="small" :type="p.type === '急诊' ? 'danger' : 'primary'">{{ p.type }}</el-tag>
          </div>
          <div style="color: #999; font-size: 12px; margin-top: 5px;">
            主人: {{ p.owner }} | {{ p.breed }}
          </div>
          <div style="margin-top: 5px; font-size: 13px;">
            主诉: {{ p.reason }}
          </div>
        </el-card>
        <el-empty v-if="waitingList.length === 0" description="暂无候诊" :image-size="60"></el-empty>
      </div>
    </div>

    <!-- 诊疗表单 -->
    <div style="flex: 1; background: #fff; border-radius: 4px; padding: 20px; overflow-y: auto;">
      <div v-if="!currentPatient" style="height: 100%; display: flex; justify-content: center; align-items: center; flex-direction: column; color: #909399;">
        <el-icon size="60"><Service /></el-icon>
        <p>请从左侧列表选择患者并点击"叫号接诊"</p>
      </div>

      <div v-else>
        <div style="display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid #eee; padding-bottom: 15px; margin-bottom: 15px;">
          <div>
            <span style="font-size: 20px; font-weight: bold; margin-right: 10px;">{{ currentPatient.name }}</span>
            <el-tag>{{ currentPatient.breed }}</el-tag>
            <el-tag type="info" style="margin-left: 5px;">{{ currentPatient.age }}岁</el-tag>
            <el-tag type="warning" style="margin-left: 5px;">体重: {{ currentPatient.weight }}kg</el-tag>
          </div>
          <div>
            <el-button type="primary" plain @click="historyDrawer = true">查看历史病历</el-button>
            <el-button type="success" @click="finishDiagnose">完成诊疗</el-button>
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
              <div class="section-title">检查申请</div>
              <el-form-item label="检查项目">
                <el-select 
                  v-model="medicalForm.selectedExamination" 
                  placeholder="请选择检查项目（可不选）" 
                  clearable
                  style="width: 100%"
                >
                  <el-option label="不开检查单" value=""></el-option>
                  <el-option label="CT检查" value="CT检查"></el-option>
                  <el-option label="X光检查" value="X光检查"></el-option>
                  <el-option label="B超检查" value="B超检查"></el-option>
                  <el-option label="血液检查" value="血液检查"></el-option>
                  <el-option label="尿液检查" value="尿液检查"></el-option>
                </el-select>
                <div style="margin-top: 10px; font-size: 14px; color: #666;">
                  <div v-if="!medicalForm.selectedExamination">当前选择：不开检查单，可直接填写诊断结果</div>
                  <div v-else>当前选择：{{ medicalForm.selectedExamination }}，需等待检查结果出具后再填写最终诊断结果</div>
                </div>
              </el-form-item>
              
              <!-- 检查状态显示 -->
              <el-form-item v-if="medicalForm.selectedExamination">
                <el-alert 
                  :title="`已申请检查：${medicalForm.selectedExamination}`" 
                  type="warning" 
                  show-icon
                  :closable="false"
                >
                  <template #default>
                    <div>
                      <p>检查状态：{{ medicalForm.examinationStatus || '等待检查中...' }}</p>
                      <el-button 
                        v-if="medicalForm.examinationStatus !== '已完成'" 
                        type="primary" 
                        size="small" 
                        @click="markExaminationComplete"
                        style="margin-top: 10px"
                      >
                        标记检查完成
                      </el-button>
                    </div>
                  </template>
                </el-alert>
              </el-form-item>
            </el-col>

            <el-col :span="24">
              <div class="section-title">诊断结果 (Diagnosis)</div>
              <el-form-item>
                <el-input v-model="medicalForm.diagnosis" placeholder="请输入初步诊断结果"></el-input>
              </el-form-item>
            </el-col>

            <el-col :span="24">
              <div class="section-title">
                处方开具 (Prescription)
                <el-button type="primary" link size="small" icon="Plus" @click="addDrug" style="float: right;">添加药品</el-button>
              </div>
              <el-table :data="medicalForm.drugs" border size="small">
                <el-table-column label="药品名称" width="200">
                  <template #default="scope">
                    <el-select v-model="scope.row.name" placeholder="选择药品" filterable>
                      <el-option v-for="drug in drugOptions" :key="drug.id" :label="drug.name" :value="drug.name">
                        <span style="float: left">{{ drug.name }}</span>
                        <span style="float: right; color: #8492a6; font-size: 13px">库存:{{ drug.stock }}</span>
                      </el-option>
                    </el-select>
                  </template>
                </el-table-column>
                <el-table-column label="数量" width="150">
                  <template #default="scope">
                    <el-input-number v-model="scope.row.count" :min="1" size="small"></el-input-number>
                  </template>
                </el-table-column>
                <el-table-column label="用法用量 (医嘱)">
                  <template #default="scope">
                    <el-input v-model="scope.row.usage" size="small" placeholder="例如：每日2次，饭后喂食"></el-input>
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="80">
                  <template #default="scope">
                    <el-button type="danger" link icon="Delete" @click="removeDrug(scope.$index)"></el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-col>

            <el-col :span="24" style="margin-top: 20px;">
              <div class="section-title">附加医嘱</div>
              <el-form-item>
                <el-checkbox-group v-model="medicalForm.advices">
                  <el-checkbox label="禁食24小时" border></el-checkbox>
                  <el-checkbox label="限制运动" border></el-checkbox>
                  <el-checkbox label="一周后复查" border></el-checkbox>
                  <el-checkbox label="佩戴伊丽莎白圈" border></el-checkbox>
                </el-checkbox-group>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </div>
    </div>
  </div>

  <!-- 历史病历抽屉 -->
  <el-drawer v-model="historyDrawer" title="该宠物历史就诊记录" size="40%">
    <div v-if="currentPatient">
      <el-timeline>
        <el-timeline-item timestamp="2023/05/10" placement="top">
          <el-card>
            <h4>接种疫苗 (妙三多)</h4>
            <p>医生：李医生</p>
          </el-card>
        </el-timeline-item>
        <el-timeline-item timestamp="2022/12/02" placement="top" color="#F56C6C">
          <el-card>
            <h4>后腿外伤处理</h4>
            <p>诊断：软组织挫伤，无骨折。</p>
            <p>处方：消炎喷剂，止痛药。</p>
          </el-card>
        </el-timeline-item>
      </el-timeline>
    </div>
  </el-drawer>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Service, Refresh, Plus, Delete } from '@element-plus/icons-vue'

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
const emit = defineEmits(['call-patient', 'finish-diagnose'])

// 响应式状态
const historyDrawer = ref(false)
const currentPatient = ref(null)

const medicalForm = reactive({
  symptoms: '',
  diagnosis: '',
  drugs: [],
  advices: [],
  selectedExamination: '',
  examinationStatus: ''
})

// 方法
const callPatient = (patient) => {
  if (currentPatient.value && currentPatient.value.id !== patient.id) {
    ElMessageBox.confirm('当前还有未完成的诊疗，确定要切换患者吗？', '提示')
        .then(() => startConsultation(patient))
  } else {
    startConsultation(patient)
  }
}

const startConsultation = (patient) => {
  currentPatient.value = patient
  medicalForm.symptoms = ''
  medicalForm.diagnosis = ''
  medicalForm.drugs = []
  medicalForm.advices = []
  medicalForm.selectedExamination = ''
  medicalForm.examinationStatus = ''
  emit('call-patient', patient)
}

const addDrug = () => {
  medicalForm.drugs.push({ name: '', count: 1, usage: '' })
}

const removeDrug = (index) => {
  medicalForm.drugs.splice(index, 1)
}

// 标记检查完成
const markExaminationComplete = () => {
  medicalForm.examinationStatus = '已完成'
  ElMessage.success('检查状态已更新为已完成，现在可以填写最终诊断结果了')
}

const finishDiagnose = () => {
  // 检查是否选择了检查但还未完成
  if (medicalForm.selectedExamination && medicalForm.examinationStatus !== '已完成') {
    return ElMessage.warning('已申请检查但尚未完成，请等待检查结果或标记检查完成后再提交诊疗')
  }
  
  if (!medicalForm.diagnosis) {
    return ElMessage.warning('请填写诊断结果')
  }

  // 构建完成信息
  let message = `确定完成对 [${currentPatient.value.name}] 的诊疗吗？`
  
  // 如果有检查申请，添加提醒
  if (medicalForm.selectedExamination) {
    message += `\n\n注意：已申请检查项目：${medicalForm.selectedExamination}`
  }
  
  // 如果有处方，添加提醒
  if (medicalForm.drugs && medicalForm.drugs.length > 0) {
    message += `\n\n注意：已开具处方，请确保药品和用法用量正确。`
  }

  ElMessageBox.confirm(message, '完成确认', {
    confirmButtonText: '确定提交',
    cancelButtonText: '取消',
    type: 'success'
  }).then(() => {
    ElMessage.success('病历提交成功，相关信息已发送至相应部门')
    emit('finish-diagnose', currentPatient.value.id)
    currentPatient.value = null
  })
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
  border-left-color: #409eff;
  background-color: #ecf5ff;
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
</style>