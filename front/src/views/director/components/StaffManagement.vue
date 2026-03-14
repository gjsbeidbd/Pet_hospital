<template>
  <div class="staff-management-wrapper">
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between;">
          <span>医院职工名册</span>
          <el-button type="primary" :icon="Plus">新增员工</el-button>
        </div>
      </template>
      <el-table :data="staffList" border stripe>
        <el-table-column prop="id" label="工号" width="80"></el-table-column>
        <el-table-column prop="name" label="姓名" width="100"></el-table-column>
        <el-table-column prop="role" label="职位">
          <template #default="scope">
            <el-tag :type="scope.row.role === '医生' ? 'success' : 'info'">{{ scope.row.role }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="department" label="所属科室"></el-table-column>
        <el-table-column prop="phone" label="联系电话"></el-table-column>
        <el-table-column prop="status" label="状态">
          <template #default="scope">
            <el-switch v-model="scope.row.status" active-text="在职" inactive-text="离职"></el-switch>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template #default>
            <el-button link type="primary" :icon="Edit">编辑</el-button>
            <el-button link type="warning" :icon="Calendar">排班</el-button>
            <el-button link type="danger" :icon="Delete">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { Plus, Edit, Calendar, Delete } from '@element-plus/icons-vue';

// 模拟数据
const staffList = ref([
  { id: '1001', name: '王建国', role: '医生', department: '内科', phone: '13800000001', status: true },
  { id: '1002', name: '李晓红', role: '前台', department: '服务部', phone: '13800000002', status: true },
  { id: '1003', name: '张伟', role: '医生', department: '外科', phone: '13800000003', status: true },
]);
</script>

<style scoped>
.staff-management-wrapper {
  height: 100%;
  display: flex;
  flex-direction: column;
}
</style>