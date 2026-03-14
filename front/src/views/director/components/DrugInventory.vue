<template>
  <div class="drug-inventory-wrapper">
    <div style="margin-bottom: 15px; display: flex; gap: 10px;">
      <el-input placeholder="药品名称/编号" style="width: 300px;" v-model="drugSearch">
        <template #append><el-button :icon="Search">搜索</el-button></template>
      </el-input>
      <el-button type="success" :icon="List">入库登记</el-button>
      <el-button type="warning" plain @click="filterLowStock">仅显示库存不足</el-button>
    </div>
    <el-table :data="drugList" border>
      <el-table-column prop="code" label="药品编号" width="120"></el-table-column>
      <el-table-column prop="name" label="药品名称"></el-table-column>
      <el-table-column prop="type" label="分类" width="100"></el-table-column>
      <el-table-column prop="price" label="售价 (元)" width="100"></el-table-column>
      <el-table-column prop="stock" label="当前库存" width="120">
        <template #default="scope">
          <span :class="scope.row.stock < 20 ? 'warning-text' : ''">{{ scope.row.stock }}</span>
          <el-tag size="small" type="danger" effect="dark" v-if="scope.row.stock < 20">缺货</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="unit" label="单位" width="80"></el-table-column>
      <el-table-column label="操作">
        <template #default="scope">
          <el-button type="primary" size="small" @click="openRestock(scope.row)">补货</el-button>
          <el-button size="small">调整价格</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <!-- 补货弹窗 -->
    <el-dialog v-model="restockDialog" title="药品入库补货" width="30%">
      <el-form v-if="currentDrug">
        <el-form-item label="药品名称">
          <el-input v-model="currentDrug.name" disabled></el-input>
        </el-form-item>
        <el-form-item label="入库数量">
          <el-input-number v-model="restockNum" :min="1" :step="10"></el-input-number>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="restockDialog = false">取消</el-button>
        <el-button type="primary" @click="confirmRestock">确认入库</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { ElMessage } from 'element-plus';
import { Search, List } from '@element-plus/icons-vue';

// 响应式状态
const drugSearch = ref('');
const restockDialog = ref(false);
const restockNum = ref(50);
const currentDrug = ref(null);

// 模拟数据
const drugList = ref([
  { code: 'D001', name: '阿莫西林', type: '抗生素', price: 25.0, stock: 120, unit: '盒' },
  { code: 'D002', name: '狂犬疫苗', type: '疫苗', price: 80.0, stock: 8, unit: '支' },
  { code: 'D003', name: '伊丽莎白圈', type: '耗材', price: 15.0, stock: 50, unit: '个' },
  { code: 'D004', name: '体内驱虫片', type: '驱虫药', price: 45.0, stock: 15, unit: '粒' },
]);

// 方法
const openRestock = (drug) => {
  currentDrug.value = drug;
  restockNum.value = 50;
  restockDialog.value = true;
};

const confirmRestock = () => {
  currentDrug.value.stock += restockNum.value;
  ElMessage.success(`【${currentDrug.value.name}】入库成功，库存已更新`);
  restockDialog.value = false;
};

const filterLowStock = () => {
  // 注意：这里简化处理，实际应保留原数据并用 computed 过滤
  drugList.value = drugList.value.filter(d => d.stock < 20);
  ElMessage.warning('已筛选出库存不足药品');
};
</script>

<style scoped>
.drug-inventory-wrapper {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.warning-text {
  color: #F56C6C;
  font-weight: bold;
}
</style>