<template>
  <div class="overview-wrapper">
    <el-row :gutter="20" style="margin-bottom: 20px;">
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <el-icon class="stat-icon" style="color: #E6A23C"><Timer /></el-icon>
            <div class="stat-info">
              <div class="stat-num">{{ paginatedQueueData.length }}</div>
              <div class="stat-label">当前候诊人数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <el-icon class="stat-icon" style="color: #67C23A"><Checked /></el-icon>
            <div class="stat-info">
              <div class="stat-num">12</div>
              <div class="stat-label">今日已接诊</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <el-icon class="stat-icon" style="color: #F56C6C"><Money /></el-icon>
            <div class="stat-info">
              <div class="stat-num">2</div>
              <div class="stat-label">待缴费单据</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <el-icon class="stat-icon"><UserFilled /></el-icon>
            <div class="stat-info">
              <div class="stat-num">2</div>
              <div class="stat-label">今日新增会员</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="queue-card" header="当前候诊队列 (实时)">
      <el-table :data="paginatedQueueData" stripe style="width: 100%" size="small">
        <el-table-column prop="no" label="排队号" width="80"></el-table-column>
        <el-table-column prop="petName" label="宠物" width="100"></el-table-column>
        <el-table-column prop="owner" label="主人"></el-table-column>
        <el-table-column prop="doctor" label="挂号医生"></el-table-column>
        <el-table-column prop="status" label="状态">
          <template #default="scope">
            <el-tag v-if="scope.row.status === '待取号'" type="info">待取号</el-tag>
            <el-tag v-else-if="scope.row.status === '待就诊'" type="warning">待就诊</el-tag>
            <el-tag v-else-if="scope.row.status === '就诊中'" type="primary">就诊中</el-tag>
            <el-tag v-else-if="scope.row.status === '就诊完成'" type="success">就诊完成</el-tag>
            <el-tag v-else type="info">等待叫号</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default>
            <el-button link type="primary" size="small">调整顺序</el-button>
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
          :total="filteredQueueData.length"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :pager-count="5"
          prev-text="上一页"
          next-text="下一页"
          :page-count="Math.ceil(filteredQueueData.length / pageSize)"
          :locale="{
            total: `共 {total} 条记录`,
            pagesize: '条/页',
            goto: '跳至',
            pageClassifier: '页'
          }"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { 
  Timer, 
  Checked, 
  Money, 
  UserFilled 
} from '@element-plus/icons-vue'

// 分页相关数据
const currentPage = ref(1)
const pageSize = ref(10)

// 模拟数据：候诊队列
const queueData = ref([
  { no: 'A001', petName: '豆豆', owner: '张伟', doctor: '王医生', status: '待就诊' },
  { no: 'A002', petName: '雪球', owner: '刘洋', doctor: '李医生', status: '待就诊' },
  { no: 'A003', petName: '小白', owner: '李四', doctor: '王医生', status: '待取号' },
  { no: 'A004', petName: '小黑', owner: '王五', doctor: '李医生', status: '就诊中' },
  { no: 'A005', petName: '花花', owner: '赵六', doctor: '王医生', status: '待就诊' },
  { no: 'A006', petName: '毛毛', owner: '孙七', doctor: '李医生', status: '待取号' },
  { no: 'A007', petName: '绒绒', owner: '周八', doctor: '王医生', status: '就诊完成' },
  { no: 'A008', petName: '球球', owner: '吴九', doctor: '李医生', status: '待就诊' },
  { no: 'A009', petName: '大黄', owner: '郑十', doctor: '王医生', status: '待取号' },
  { no: 'A010', petName: '二黄', owner: '王十一', doctor: '李医生', status: '就诊中' },
  { no: 'A011', petName: '小强', owner: '李十二', doctor: '王医生', status: '待就诊' },
  { no: 'A012', petName: '旺财', owner: '张十三', doctor: '李医生', status: '待取号' }
])

// 过滤待就诊的数据
const filteredQueueData = computed(() => {
  return queueData.value.filter(item => item.status === '待就诊')
})

// 计算当前页的候诊数据（仅待就诊）
const paginatedQueueData = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredQueueData.value.slice(start, end)
})

// 分页事件处理
const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
}

const handleCurrentChange = (val) => {
  currentPage.value = val
}

// 监听自定义事件，接收取号数据
const handleCheckIn = (data) => {
  // 生成排队号
  const queueNumber = `A${String(queueData.value.length + 1).padStart(3, '0')}`
  
  // 添加到候诊队列
  queueData.value.push({
    no: queueNumber,
    petName: data.pet,
    owner: data.name,
    doctor: data.doctor,
    status: data.status
  })
}

// 暴露方法给父组件
defineExpose({
  handleCheckIn
})
</script>

<style scoped>
.overview-wrapper {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.queue-card {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.queue-card :deep(.el-card__body) {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.queue-card :deep(.el-table) {
  flex: 1;
}

.pagination-container {
  display: flex;
  justify-content: center;
  padding: 20px 0;
}

.stat-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  cursor: pointer;
}

.stat-icon {
  font-size: 40px;
  color: #409EFF;
}

.stat-info {
  text-align: right;
}

.stat-num {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  color: #909399;
  font-size: 12px;
}
</style>