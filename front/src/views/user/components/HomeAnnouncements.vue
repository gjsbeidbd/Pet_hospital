<template>
  <div>
    <el-alert
        title="温馨提示：系统已上线缴费功能，请及时处理待缴费用。"
        type="warning"
        show-icon
        style="margin-bottom: 20px;"
    />
    <el-row :gutter="20">
      <el-col :span="16">
        <el-carousel height="300px" style="border-radius: 8px; overflow: hidden;">
          <el-carousel-item v-for="item in 3" :key="item">
            <img
                :src="`https://picsum.photos/800/400?random=${item}`"
                style="width: 100%; height: 100%; object-fit: cover;"
            >
          </el-carousel-item>
        </el-carousel>
      </el-col>
      <el-col :span="8">
        <el-card header="医院通知" style="height: 300px; overflow-y: auto;">
          <el-timeline>
            <el-timeline-item timestamp="2023/11/24" placement="top" type="primary">
              <strong>系统升级：移动端缴费功能上线</strong>
            </el-timeline-item>
            <el-timeline-item timestamp="2023/11/15" placement="top">
              冬季疫苗接种优惠活动开始
            </el-timeline-item>
            <el-timeline-item timestamp="2023/11/01" placement="top">
              王医生本月专家门诊排班调整
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
// 此组件无需额外的逻辑
</script>

<style scoped>
/* 组件特定样式可以在这里添加 */
</style>