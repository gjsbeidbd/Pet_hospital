<template>
  <div class="business-report-wrapper">
    <el-row :gutter="20" style="margin-bottom: 20px;">
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="data-card">
            <div>
              <div class="label">今日营收</div>
              <div class="num">¥ {{ Number(todayRevenue).toLocaleString() }}</div>
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
              <div class="num">{{ monthCompleted }}</div>
            </div>
            <div class="icon-box" style="background: #67C23A;"><el-icon><FirstAidKit /></el-icon></div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="data-card">
            <div>
              <div class="label">用户总数</div>
              <div class="num">{{ totalUsers.toLocaleString() }}</div>
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
              <div class="num" style="color: #F56C6C;">{{ drugWarning }}</div>
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
        <el-card header="本月科室预约占比">
          <div id="chart-disease" style="height: 350px; width: 100%;"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { onMounted, nextTick, ref } from 'vue';
import { Wallet, FirstAidKit, User, Warning } from '@element-plus/icons-vue';
import { getTodayRevenue, getMonthCompletedCount, getTotalUsersCount, getDrugWarningCount, getLastSevenDaysRevenue, getDepartmentCount } from '@/services/api';

const todayRevenue = ref(0);
const monthCompleted = ref(0);
const totalUsers = ref(0);
const drugWarning = ref(0);
const sevenDaysData = ref([]);
const departmentData = ref([]);

const fetchTodayRevenue = async () => {
  try {
    const res = await getTodayRevenue()
    console.log('今日营收响应:', res)
    let amount = res.data
    if (amount === null || amount === undefined) {
      amount = 0
    }
    if (typeof amount === 'string') {
      amount = parseFloat(amount)
    }
    todayRevenue.value = amount
    console.log('今日营收值:', todayRevenue.value)
  } catch (error) {
    console.error('获取今日营收失败:', error)
    todayRevenue.value = 0
  }
};

const fetchMonthCompleted = async () => {
  try {
    const res = await getMonthCompletedCount()
    monthCompleted.value = res.data?.data ?? res.data ?? 0
  } catch (error) {
    console.error('获取本月接诊量失败:', error)
    monthCompleted.value = 0
  }
};

const fetchTotalUsers = async () => {
  try {
    const res = await getTotalUsersCount()
    totalUsers.value = res.data?.data ?? res.data ?? 0
  } catch (error) {
    console.error('获取用户总数失败:', error)
    totalUsers.value = 0
  }
};

const fetchDrugWarning = async () => {
  try {
    const res = await getDrugWarningCount()
    drugWarning.value = res.data?.data ?? res.data ?? 0
  } catch (error) {
    console.error('获取库存预警失败:', error)
    drugWarning.value = 0
  }
};

const fetchSevenDaysRevenue = async () => {
  try {
    const res = await getLastSevenDaysRevenue()
    sevenDaysData.value = res.data || []
  } catch (error) {
    console.error('获取近七天营收失败:', error)
    sevenDaysData.value = []
  }
};

const fetchDepartmentCount = async () => {
  try {
    const res = await getDepartmentCount()
    departmentData.value = res.data || []
  } catch (error) {
    console.error('获取科室预约数失败:', error)
    departmentData.value = []
  }
};

// 图表初始化
const initCharts = async () => {
  const echarts = await import('echarts');

  const weekDays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];

  const xAxisData = sevenDaysData.value.map(item => {
    const d = new Date(item.date);
    return weekDays[d.getDay()];
  });

  const seriesData = sevenDaysData.value.map(item => parseFloat(item.total) || 0);

  const chartRevenue = echarts.init(document.getElementById('chart-revenue'));
  chartRevenue.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: xAxisData },
    yAxis: { type: 'value' },
    series: [{ name: '营收(元)', type: 'line', smooth: true, data: seriesData, color: '#409EFF', areaStyle: {} }]
  });

  const chartDisease = echarts.init(document.getElementById('chart-disease'));
  const departmentChartData = departmentData.value.map(item => ({
    value: item.count,
    name: item.department || '未知'
  }));
  chartDisease.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: '0%' },
    series: [{
      name: '科室预约',
      type: 'pie',
      radius: ['40%', '70%'],
      avoidLabelOverlap: false,
      itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
      data: departmentChartData.length > 0 ? departmentChartData : [
        { value: 0, name: '暂无数据' }
      ]
    }]
  });

  window.addEventListener('resize', () => {
    chartRevenue.resize();
    chartDisease.resize();
  });
};

onMounted(async () => {
  await fetchTodayRevenue();
  await fetchMonthCompleted();
  await fetchTotalUsers();
  await fetchDrugWarning();
  await fetchSevenDaysRevenue();
  await fetchDepartmentCount();
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