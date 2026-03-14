<template>
  <div class="dashboard-container">
    <el-row :gutter="20" class="panel-group">
      <el-col :span="6" class="card-panel-col">
        <div class="card-panel" @click="handleSetLineChartData('newVisitis')">
          <div class="card-panel-icon-wrapper icon-people">
            <el-icon class="card-panel-icon"><User /></el-icon>
          </div>
          <div class="card-panel-description">
            <div class="card-panel-text">我的宠物</div>
            <count-to :start-val="0" :end-val="petCount" :duration="2600" class="card-panel-num" />
          </div>
        </div>
      </el-col>
      
      <el-col :span="6" class="card-panel-col">
        <div class="card-panel" @click="handleSetLineChartData('messages')">
          <div class="card-panel-icon-wrapper icon-message">
            <el-icon class="card-panel-icon"><Calendar /></el-icon>
          </div>
          <div class="card-panel-description">
            <div class="card-panel-text">预约次数</div>
            <count-to :start-val="0" :end-val="appointmentCount" :duration="3000" class="card-panel-num" />
          </div>
        </div>
      </el-col>
      
      <el-col :span="6" class="card-panel-col">
        <div class="card-panel" @click="handleSetLineChartData('purchases')">
          <div class="card-panel-icon-wrapper icon-money">
            <el-icon class="card-panel-icon"><Document /></el-icon>
          </div>
          <div class="card-panel-description">
            <div class="card-panel-text">病历记录</div>
            <count-to :start-val="0" :end-val="recordCount" :duration="3200" class="card-panel-num" />
          </div>
        </div>
      </el-col>
      
      <el-col :span="6" class="card-panel-col">
        <div class="card-panel" @click="handleSetLineChartData('shoppings')">
          <div class="card-panel-icon-wrapper icon-shopping">
            <el-icon class="card-panel-icon"><Bell /></el-icon>
          </div>
          <div class="card-panel-description">
            <div class="card-panel-text">系统通知</div>
            <count-to :start-val="0" :end-val="noticeCount" :duration="3600" class="card-panel-num" />
          </div>
        </div>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="16">
        <el-card class="box-card">
          <template #header>
            <div class="card-header">
              <span>最近预约</span>
            </div>
          </template>
          <el-table :data="recentAppointments" style="width: 100%">
            <el-table-column prop="date" label="预约时间" width="180"></el-table-column>
            <el-table-column prop="petName" label="宠物名称" width="120"></el-table-column>
            <el-table-column prop="doctor" label="医生" width="120"></el-table-column>
            <el-table-column prop="desc" label="预约事项"></el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="getStatusType(scope.row.status)">{{ scope.row.status }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card class="box-card">
          <template #header>
            <div class="card-header">
              <span>系统公告</span>
            </div>
          </template>
          <div class="announcement-list">
            <div 
              v-for="(item, index) in announcements" 
              :key="index" 
              class="announcement-item"
            >
              <div class="announcement-title">{{ item.title }}</div>
              <div class="announcement-date">{{ item.date }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { User, Calendar, Document, Bell } from '@element-plus/icons-vue'
import CountTo from 'vue-count-to/src/vue-countTo.vue'

// 数据
const petCount = ref(2)
const appointmentCount = ref(5)
const recordCount = ref(3)
const noticeCount = ref(2)

const recentAppointments = ref([
  { date: '2023-11-20 10:00', petName: '旺财', doctor: '王医生', desc: '年度体检', status: '待取号' },
  { date: '2023-09-15 14:00', petName: '咪咪', doctor: '李医生', desc: '拉肚子', status: '就诊完成' },
  { date: '2023-08-10 09:30', petName: '小黑', doctor: '张医生', desc: '疫苗接种', status: '已取消' }
])

const announcements = ref([
  { title: '医院春节放假通知', date: '2023-12-01' },
  { title: '新增在线预约功能', date: '2023-11-15' }
])

// 方法
const handleSetLineChartData = (type) => {
  console.log('查看数据:', type)
}

const getStatusType = (status) => {
  switch (status) {
    case '待取号':
      return 'warning'
    case '就诊完成':
      return 'success'
    case '已取消':
      return 'info'
    default:
      return 'primary'
  }
}

onMounted(() => {
  // 可以在这里获取实际数据
})
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
  background-color: #f0f2f5;
}

.panel-group {
  margin-top: 18px;
}

.card-panel-col {
  margin-bottom: 32px;
}

.card-panel {
  height: 108px;
  cursor: pointer;
  font-size: 12px;
  position: relative;
  overflow: hidden;
  color: #666;
  background: #fff;
  box-shadow: 4px 4px 40px rgba(0, 0, 0, 0.05);
  border-color: rgba(0, 0, 0, 0.05);
  display: flex;
  align-items: center;
  padding: 20px;
}

.card-panel:hover {
  box-shadow: 4px 4px 40px rgba(0, 0, 0, 0.1);
}

.card-panel-icon-wrapper {
  float: left;
  margin: 14px 0 0 14px;
  padding: 16px;
  transition: all 0.38s ease-out;
  border-radius: 6px;
}

.card-panel-icon {
  float: left;
  font-size: 48px;
}

.card-panel-description {
  float: right;
  font-weight: bold;
}

.card-panel-text {
  line-height: 18px;
  color: rgba(0, 0, 0, 0.45);
  font-size: 16px;
  margin-bottom: 12px;
}

.card-panel-num {
  font-size: 20px;
}

.icon-people {
  color: #40c9c6;
}

.icon-message {
  color: #36a3f7;
}

.icon-money {
  color: #f4516c;
}

.icon-shopping {
  color: #34bfa3;
}

.box-card {
  margin-bottom: 20px;
}

.card-header {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.announcement-list {
  max-height: 300px;
  overflow-y: auto;
}

.announcement-item {
  padding: 10px 0;
  border-bottom: 1px solid #eee;
}

.announcement-item:last-child {
  border-bottom: none;
}

.announcement-title {
  font-size: 14px;
  color: #333;
  margin-bottom: 5px;
}

.announcement-date {
  font-size: 12px;
  color: #999;
}
</style>