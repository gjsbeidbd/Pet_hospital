<template>
  <div class="business-report-wrapper">
    <el-row :gutter="20" style="margin-bottom: 20px;">
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="data-card">
            <div>
              <div class="label">今日营收</div>
              <div class="num">¥ 12,580</div>
            </div>
            <div class="icon-box" style="background: #409EFF;"><el-icon><Wallet /></el-icon></div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="data-card">
            <div>
              <div class="label">本月接诊量</div>
              <div class="num">386 <span style="font-size: 12px; color: #67C23A;">(+12%)</span></div>
            </div>
            <div class="icon-box" style="background: #67C23A;"><el-icon><FirstAidKit /></el-icon></div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="data-card">
            <div>
              <div class="label">会员总数</div>
              <div class="num">1,205</div>
            </div>
            <div class="icon-box" style="background: #E6A23C;"><el-icon><User /></el-icon></div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="data-card">
            <div>
              <div class="label">库存预警</div>
              <div class="num" style="color: #F56C6C;">3</div>
            </div>
            <div class="icon-box" style="background: #F56C6C;"><el-icon><Warning /></el-icon></div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="16">
        <el-card header="近七日营收趋势分析">
          <div id="chart-revenue" style="height: 350px; width: 100%;"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card header="本月高发病种占比">
          <div id="chart-disease" style="height: 350px; width: 100%;"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { onMounted, nextTick } from 'vue';
import { Wallet, FirstAidKit, User, Warning } from '@element-plus/icons-vue';

// 图表初始化
const initCharts = async () => {
  const echarts = await import('echarts');
  
  const chartRevenue = echarts.init(document.getElementById('chart-revenue'));
  chartRevenue.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'] },
    yAxis: { type: 'value' },
    series: [{ name: '营收(元)', type: 'line', smooth: true, data: [12000, 13200, 10100, 13400, 9000, 23000, 21000], color: '#409EFF', areaStyle: {} }]
  });

  const chartDisease = echarts.init(document.getElementById('chart-disease'));
  chartDisease.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: '0%' },
    series: [{
      name: '病种分布',
      type: 'pie',
      radius: ['40%', '70%'],
      avoidLabelOverlap: false,
      itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
      data: [
        { value: 1048, name: '皮肤病' },
        { value: 735, name: '消化系统' },
        { value: 580, name: '疫苗接种' },
        { value: 484, name: '外伤' },
        { value: 300, name: '其他' }
      ]
    }]
  });

  window.addEventListener('resize', () => {
    chartRevenue.resize();
    chartDisease.resize();
  });
};

onMounted(() => {
  nextTick(() => initCharts());
});
</script>

<style scoped>
.business-report-wrapper {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.data-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.data-card .num {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.data-card .label {
  color: #909399;
  font-size: 14px;
}

.data-card .icon-box {
  width: 50px;
  height: 50px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
}
</style>