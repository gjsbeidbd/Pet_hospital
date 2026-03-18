<template>
  <div class="medical-records-wrapper">
    <el-card class="medical-records-card">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>病历查询</span>
          <el-input
            v-model="searchKeyword"
            placeholder="搜索宠物名称或主人姓名"
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
        <el-table :data="paginatedRecords" border style="width: 100%">
          <el-table-column prop="petName" label="宠物名称" width="120"></el-table-column>
          <el-table-column prop="ownerName" label="主人姓名" width="120"></el-table-column>
          <el-table-column prop="visitTime" label="就诊时间" width="180"></el-table-column>
          <el-table-column prop="doctor" label="主治医生" width="120"></el-table-column>
          <el-table-column prop="diagnosis" label="诊断结果"></el-table-column>
          <el-table-column label="操作" width="120">
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
    <el-dialog v-model="detailDialogVisible" title="病历详情" width="800px" :fullscreen="isFullscreen">
      <div class="medical-record-detail">
        <div class="record-header">
          <el-row :gutter="20">
            <el-col :span="8">
              <div class="info-item">
                <span class="label">病历编号：</span>
                <span class="value">{{ selectedRecord.recordNo }}</span>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="info-item">
                <span class="label">就诊日期：</span>
                <span class="value">{{ selectedRecord.date }}</span>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="info-item">
                <span class="label">状态：</span>
                <span class="value">
                  <el-tag :type="getStatusType(selectedRecord.status)">{{ selectedRecord.status }}</el-tag>
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
                <span class="value">{{ selectedRecord.petName }}</span>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="info-item">
                <span class="label">宠物品种：</span>
                <span class="value">{{ selectedRecord.petBreed }}</span>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="info-item">
                <span class="label">宠物年龄：</span>
                <span class="value">{{ selectedRecord.petAge }}岁</span>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="info-item">
                <span class="label">宠物性别：</span>
                <span class="value">{{ selectedRecord.petGender }}</span>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="info-item">
                <span class="label">主人姓名：</span>
                <span class="value">{{ selectedRecord.ownerName }}</span>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="info-item">
                <span class="label">联系电话：</span>
                <span class="value">{{ selectedRecord.ownerPhone }}</span>
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
                <span class="value">{{ selectedRecord.doctor }}</span>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="info-item">
                <span class="label">科室：</span>
                <span class="value">{{ selectedRecord.department }}</span>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="info-item">
                <span class="label">就诊类型：</span>
                <span class="value">{{ selectedRecord.visitType }}</span>
              </div>
            </el-col>
            <el-col :span="24">
              <div class="info-item">
                <span class="label">主诉：</span>
                <span class="value">{{ selectedRecord.chiefComplaint }}</span>
              </div>
            </el-col>
            <el-col :span="24">
              <div class="info-item">
                <span class="label">临床症状：</span>
                <span class="value">{{ selectedRecord.symptoms }}</span>
              </div>
            </el-col>
            <el-col :span="24">
              <div class="info-item">
                <span class="label">诊断结果：</span>
                <span class="value">{{ selectedRecord.diagnosis }}</span>
              </div>
            </el-col>
          </el-row>
        </el-card>
        
        <!-- 检查项目 -->
        <el-card shadow="never" style="margin-bottom: 20px;" v-if="selectedRecord.examinations && selectedRecord.examinations.length > 0">
          <template #header>
            <strong>检查项目</strong>
          </template>
          <el-row :gutter="20">
            <el-col :span="24">
              <el-checkbox-group v-model="selectedRecord.examinations" disabled>
                <el-checkbox v-for="exam in selectedRecord.examinations" :key="exam" :label="exam" border></el-checkbox>
              </el-checkbox-group>
            </el-col>
          </el-row>
        </el-card>
        
        <!-- 处方信息 -->
        <el-card shadow="never" style="margin-bottom: 20px;" v-if="selectedRecord.prescriptions && selectedRecord.prescriptions.length > 0">
          <template #header>
            <strong>处方信息</strong>
          </template>
          <el-table :data="selectedRecord.prescriptions" border stripe>
            <el-table-column prop="name" label="药品名称" width="200"></el-table-column>
            <el-table-column prop="count" label="数量" width="100"></el-table-column>
            <el-table-column prop="usage" label="用法用量"></el-table-column>
          </el-table>
        </el-card>
        
        <!-- 医嘱 -->
        <el-card shadow="never" style="margin-bottom: 20px;" v-if="selectedRecord.advices && selectedRecord.advices.length > 0">
          <template #header>
            <strong>医嘱</strong>
          </template>
          <el-row :gutter="20">
            <el-col :span="24">
              <el-checkbox-group v-model="selectedRecord.advices" disabled>
                <el-checkbox v-for="advice in selectedRecord.advices" :key="advice" :label="advice" border></el-checkbox>
              </el-checkbox-group>
            </el-col>
          </el-row>
        </el-card>
        
        <!-- 费用信息 -->
        <el-card shadow="never" style="margin-bottom: 20px;">
          <template #header>
            <strong>费用信息</strong>
          </template>
          <el-row :gutter="20">
            <el-col :span="8">
              <div class="info-item">
                <span class="label">检查费：</span>
                <span class="value">¥{{ selectedRecord.examFee }}</span>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="info-item">
                <span class="label">药费：</span>
                <span class="value">¥{{ selectedRecord.medicineFee }}</span>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="info-item">
                <span class="label">治疗费：</span>
                <span class="value">¥{{ selectedRecord.treatmentFee }}</span>
              </div>
            </el-col>
            <el-col :span="24">
              <div class="info-item" style="text-align: right; margin-top: 10px;">
                <strong>总费用：</strong>
                <strong style="font-size: 18px; color: #f56c6c;">¥{{ selectedRecord.totalFee }}</strong>
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
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'

// 搜索关键词
const searchKeyword = ref('')

// 详情弹窗可见性
const detailDialogVisible = ref(false)

// 选中的病历记录
const selectedRecord = ref({})

// 分页相关数据
const currentPage = ref(1)
const pageSize = ref(10)

// 是否全屏显示
const isFullscreen = ref(false)

// 模拟病历数据
const medicalRecords = ref([
  {
    recordNo: 'BL20230915001',
    date: '2023-09-15',
    petName: '旺财',
    petBreed: '金毛犬',
    petAge: 3,
    petGender: '公',
    ownerName: '张三',
    ownerPhone: '13800138001',
    doctor: '王建国',
    department: '内科专家诊室',
    visitType: '普通门诊',
    chiefComplaint: '连续两天不吃东西，精神萎靡',
    symptoms: '体温39.5℃，食欲不振，呕吐黄色液体',
    diagnosis: '急性肠胃炎',
    examinations: ['血液检查'],
    prescriptions: [
      { name: '阿莫西林颗粒', count: 2, usage: '每日2次，每次半包，饭后服用' },
      { name: '止吐针剂', count: 1, usage: '每日1次，肌肉注射' }
    ],
    advices: ['禁食12小时', '提供充足饮水'],
    examFee: 80,
    medicineFee: 120,
    treatmentFee: 50,
    totalFee: 250,
    status: '已归档',
    visitTime: '2023-09-15 14:00'
  },
  {
    recordNo: 'BL20230601001',
    date: '2023-06-01',
    petName: '咪咪',
    petBreed: '布偶猫',
    petAge: 1,
    petGender: '母',
    ownerName: '李四',
    ownerPhone: '13800138002',
    doctor: '张伟',
    department: '预防保健科',
    visitType: '疫苗接种',
    chiefComplaint: '年度疫苗接种',
    symptoms: '体温38.2℃，心肺听诊正常，体重4.2kg',
    diagnosis: '健康体检',
    examinations: [],
    prescriptions: [
      { name: '妙三多疫苗', count: 1, usage: '皮下注射' }
    ],
    advices: ['定期驱虫', '均衡营养'],
    examFee: 0,
    medicineFee: 180,
    treatmentFee: 20,
    totalFee: 200,
    status: '已完成',
    visitTime: '2023-06-01 10:00'
  },
  {
    recordNo: 'BL20230310001',
    date: '2023-03-10',
    petName: '旺财',
    petBreed: '金毛犬',
    petAge: 3,
    petGender: '公',
    ownerName: '张三',
    ownerPhone: '13800138001',
    doctor: '李明',
    department: '皮肤科',
    visitType: '专科门诊',
    chiefComplaint: '背部脱毛，瘙痒明显',
    symptoms: '背部圆形脱毛区，皮肤发红，有皮屑',
    diagnosis: '真菌性皮炎',
    examinations: ['伍德灯检查'],
    prescriptions: [
      { name: '特比萘芬软膏', count: 1, usage: '每日2次，患处涂抹' },
      { name: '除菌洗毛液', count: 1, usage: '每周2次，全身清洗' }
    ],
    advices: ['隔离饲养', '定期复查'],
    examFee: 50,
    medicineFee: 160,
    treatmentFee: 30,
    totalFee: 240,
    status: '治疗中',
    visitTime: '2023-03-10 15:30'
  },
  {
    recordNo: 'BL20221220001',
    date: '2022-12-20',
    petName: '咪咪',
    petBreed: '布偶猫',
    petAge: 1,
    petGender: '母',
    ownerName: '李四',
    ownerPhone: '13800138002',
    doctor: '王建国',
    department: '内科专家诊室',
    visitType: '急诊',
    chiefComplaint: '频繁蹲厕但无尿液排出',
    symptoms: '尿道口红肿，触诊膀胱敏感',
    diagnosis: '急性膀胱炎',
    examinations: ['尿液检查'],
    prescriptions: [
      { name: '抗生素', count: 1, usage: '每日2次，饭后服用' },
      { name: '利尿剂', count: 1, usage: '每日1次' }
    ],
    advices: ['增加饮水量', '按时服药', '一周后复查'],
    examFee: 50,
    medicineFee: 80,
    treatmentFee: 2000,
    totalFee: 2230,
    status: '术后恢复',
    visitTime: '2022-12-20 11:00'
  },
  {
    recordNo: 'BL20220510001',
    date: '2022-05-10',
    petName: '咪咪',
    petBreed: '布偶猫',
    petAge: 1,
    petGender: '母',
    ownerName: '李四',
    ownerPhone: '13800138002',
    doctor: '张伟',
    department: '外科',
    visitType: '择期手术',
    chiefComplaint: '计划绝育',
    symptoms: '体检各项指标正常',
    diagnosis: '择期绝育',
    examinations: ['术前血检'],
    prescriptions: [
      { name: '术后护理包', count: 1, usage: '按说明使用' }
    ],
    advices: ['术后观察精神状态', '按时拆线'],
    examFee: 100,
    medicineFee: 200,
    treatmentFee: 800,
    totalFee: 1100,
    status: '已完成',
    visitTime: '2022-05-10 13:00'
  }
])

// 计算过滤后的病历数据
const filteredRecords = computed(() => {
  if (!searchKeyword.value) {
    return medicalRecords.value
  }
  
  const keyword = searchKeyword.value.toLowerCase()
  return medicalRecords.value.filter(record => 
    record.petName.toLowerCase().includes(keyword) || 
    record.ownerName.toLowerCase().includes(keyword)
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