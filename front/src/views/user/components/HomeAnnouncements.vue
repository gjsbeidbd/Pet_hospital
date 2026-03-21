<template>
  <div class="home-container">
    <el-row :gutter="20">
      <el-col :span="16">
        <el-carousel height="300px" style="border-radius: 8px; overflow: hidden;">
          <el-carousel-item v-for="item in carouselItems" :key="item.id">
            <div class="carousel-item" :style="{ backgroundColor: item.bgColor }">
              <div class="carousel-content">
                <h2>{{ item.title }}</h2>
                <p>{{ item.subtitle }}</p>
              </div>
              <div class="carousel-icon">{{ item.icon }}</div>
            </div>
          </el-carousel-item>
        </el-carousel>
      </el-col>
      <el-col :span="8">
        <el-card header="医院通知" style="height: 300px; overflow-y: auto;">
          <div v-if="announcements.length === 0" class="no-data">暂无通知</div>
          <el-timeline v-else>
            <el-timeline-item
              v-for="item in announcements"
              :key="item.id"
              :timestamp="formatDate(item.publishDate)"
              placement="top"
              :type="item.category === 'USER' ? 'primary' : 'default'"
            >
              <strong>{{ item.title }}</strong>
              <div class="announcement-content">{{ item.content }}</div>
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="services-row">
      <el-col :span="6">
        <el-card class="service-card" shadow="hover">
          <div class="service-icon" style="background-color: #e3f2fd;">
            <span style="font-size: 32px;">🏥</span>
          </div>
          <h3>专业诊疗</h3>
          <p>资深兽医团队，精准诊断</p>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="service-card" shadow="hover">
          <div class="service-icon" style="background-color: #fff3e0;">
            <span style="font-size: 32px;">💉</span>
          </div>
          <h3>疫苗接种</h3>
          <p>各类宠物疫苗，放心接种</p>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="service-card" shadow="hover">
          <div class="service-icon" style="background-color: #e8f5e9;">
            <span style="font-size: 32px;">🔬</span>
          </div>
          <h3>检验化验</h3>
          <p>先进设备，快速准确</p>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="service-card" shadow="hover">
          <div class="service-icon" style="background-color: #fce4ec;">
            <span style="font-size: 32px;">💊</span>
          </div>
          <h3>药品超市</h3>
          <p>各类宠物药品，品质保障</p>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="tips-row">
      <el-col :span="12">
        <el-card header="养宠小知识" class="tips-card">
          <div class="tips-list">
            <div class="tip-item">
              <span class="tip-icon">🐾</span>
              <div class="tip-content">
                <h4>定期体检</h4>
                <p>建议每年为宠物进行一次全面体检，早发现早治疗</p>
              </div>
            </div>
            <div class="tip-item">
              <span class="tip-icon">🍖</span>
              <div class="tip-content">
                <h4>合理饮食</h4>
                <p>根据宠物年龄和体重选择合适的食物，避免过度喂养</p>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card header="就诊须知" class="tips-card">
          <div class="tips-list">
            <div class="tip-item">
              <span class="tip-icon">📋</span>
              <div class="tip-content">
                <h4>预约挂号</h4>
                <p>请提前预约，选择合适的医生和时间</p>
              </div>
            </div>
            <div class="tip-item">
              <span class="tip-icon">📱</span>
              <div class="tip-content">
                <h4>健康档案</h4>
                <p>随时查看宠物的就诊记录和健康信息</p>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAnnouncements } from '@/services/api'

const announcements = ref([])

const carouselItems = [
  { id: 1, title: '欢迎来到宠物医院', subtitle: '专业的宠物医疗服务，为您的爱宠健康保驾护航', icon: '🐕', bgColor: '#409EFF' },
  { id: 2, title: '24小时急诊服务', subtitle: '随时待命，为您的宠物提供紧急救治', icon: '🚑', bgColor: '#67C23A' },
  { id: 3, title: '权威兽医团队', subtitle: '经验丰富的专业兽医，为宠物健康负责', icon: '👨‍⚕️', bgColor: '#E6A23C' }
]

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN')
}

const fetchAnnouncements = async () => {
  try {
    const res = await getAnnouncements('USER')
    announcements.value = (res.data || []).slice(0, 10)
  } catch (error) {
    console.error('获取公告失败:', error)
  }
}

onMounted(() => {
  fetchAnnouncements()
})
</script>

<style scoped>
.home-container {
  padding: 20px;
}

.carousel-item {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 60px;
  box-sizing: border-box;
}

.carousel-content {
  color: white;
  max-width: 60%;
}

.carousel-content h2 {
  font-size: 32px;
  margin-bottom: 15px;
  color: white;
}

.carousel-content p {
  font-size: 16px;
  line-height: 1.6;
  color: rgba(255, 255, 255, 0.9);
}

.carousel-icon {
  font-size: 120px;
  opacity: 0.8;
}

.services-row {
  margin-top: 20px;
}

.service-card {
  text-align: center;
  padding: 20px 10px;
  transition: transform 0.3s;
}

.service-card:hover {
  transform: translateY(-5px);
}

.service-card h3 {
  margin: 15px 0 8px;
  color: #303133;
  font-size: 16px;
}

.service-card p {
  color: #909399;
  font-size: 13px;
  margin: 0;
}

.service-icon {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto;
}

.tips-row {
  margin-top: 20px;
}

.tips-card {
  height: 100%;
}

.tips-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.tip-item {
  display: flex;
  align-items: flex-start;
  gap: 15px;
  padding: 10px;
  background: #f5f7fa;
  border-radius: 8px;
}

.tip-icon {
  font-size: 28px;
  flex-shrink: 0;
}

.tip-content h4 {
  margin: 0 0 5px;
  color: #303133;
  font-size: 14px;
}

.tip-content p {
  margin: 0;
  color: #606266;
  font-size: 13px;
  line-height: 1.5;
}

.no-data {
  text-align: center;
  color: #909399;
  padding: 40px 0;
}

.announcement-content {
  font-size: 13px;
  color: #606266;
  margin-top: 5px;
  line-height: 1.5;
}
</style>