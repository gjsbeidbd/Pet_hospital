<template>
  <div class="drug-inventory-wrapper">
    <div style="margin-bottom: 15px; display: flex; gap: 10px; justify-content: space-between;">
      <div style="display: flex; gap: 10px;">
        <el-input placeholder="药品名称/编号" style="width: 300px;" v-model="drugSearch">
          <template #append><el-button :icon="Search">搜索</el-button></template>
        </el-input>
        <el-button type="warning" plain @click="filterLowStock">仅显示库存不足</el-button>
      </div>
      <el-button type="primary" @click="showAddDrugDialog">+ 新增药品</el-button>
    </div>
    <el-table :data="drugList" border>
      <el-table-column prop="code" label="药品编号" width="120"></el-table-column>
      <el-table-column prop="name" label="药品名称"></el-table-column>
      <el-table-column prop="type" label="分类" width="100"></el-table-column>
      <el-table-column prop="price" label="售价 (元)" width="100"></el-table-column>
      <el-table-column prop="stock" label="当前库存" width="120">
        <template #default="scope">
          <span>{{ scope.row.stock }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="unit" label="单位" width="80"></el-table-column>
      <el-table-column label="库存状态" width="100" align="center">
        <template #default="scope">
          <el-tag :type="scope.row.stock >= scope.row.warningStock ? 'success' : 'danger'" size="default">
            {{ scope.row.stock >= scope.row.warningStock ? '充足' : '不足' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="是否启用" width="100" align="center">
        <template #default="scope">
          <el-tag :type="scope.row.isActive ? 'success' : 'info'" size="default">
            {{ scope.row.isActive ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="280" fixed="right" align="center">
        <template #default="scope">
          <el-button type="primary" size="small" @click="showEditDrugDialog(scope.row)">修改</el-button>
          <el-button 
            :type="scope.row.isActive ? 'warning' : 'success'" 
            size="small" 
            @click="toggleDrugStatus(scope.row)"
          >
            {{ scope.row.isActive ? '禁用' : '启用' }}
          </el-button>
          <el-button type="danger" size="small" @click="deleteDrug(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <!-- 新增/编辑药品对话框 -->
    <el-dialog 
      v-model="drugDialogVisible" 
      :title="isEditMode ? '修改药品信息' : '新增药品'" 
      width="500px"
      @close="resetDrugForm"
    >
      <el-form :model="drugForm" label-width="100px" :rules="drugRules" ref="drugFormRef">
        <el-form-item label="药品名称" prop="name">
          <el-input v-model="drugForm.name" placeholder="请输入药品名称" maxlength="100"></el-input>
        </el-form-item>
        <el-form-item label="分类" prop="type">
          <el-select v-model="drugForm.type" placeholder="请选择药品分类" style="width: 100%">
            <el-option label="抗生素" value="抗生素"></el-option>
            <el-option label="疫苗" value="疫苗"></el-option>
            <el-option label="驱虫药" value="驱虫药"></el-option>
            <el-option label="消炎药" value="消炎药"></el-option>
            <el-option label="营养品" value="营养品"></el-option>
            <el-option label="耗材" value="耗材"></el-option>
            <el-option label="其他" value="其他"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number 
            v-model="drugForm.price" 
            :min="0" 
            :precision="2" 
            :step="1"
            controls-position="right"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="库存数量" prop="stock">
          <el-input-number 
            v-model="drugForm.stock" 
            :min="0" 
            :step="10"
            controls-position="right"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="预警库存" prop="warningStock">
          <el-input-number 
            v-model="drugForm.warningStock" 
            :min="0" 
            :step="5"
            controls-position="right"
            style="width: 100%"
          />
          <div style="font-size: 12px; color: #909399; margin-top: 5px;">低于此数量时显示缺货状态</div>
        </el-form-item>
        <el-form-item label="单位" prop="unit">
          <el-input v-model="drugForm.unit" placeholder="如：盒、支、粒等" maxlength="10"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="drugDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitDrugForm" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search } from '@element-plus/icons-vue';
import { 
  getDrugInventory, 
  addDrug, 
  updateDrug, 
  deleteDrug as deleteDrugApi
} from '@/services/api';

// 响应式状态
const drugSearch = ref('');
const drugDialogVisible = ref(false);
const isEditMode = ref(false);
const submitting = ref(false);
const drugFormRef = ref(null);
const drugForm = reactive({
  id: null,
  code: '',
  name: '',
  type: '',
  price: 0,
  stock: 0,
  warningStock: 20,  // 默认预警库存为 20
  unit: '',
  isActive: true  // 默认为启用状态
});

// 药品表单验证规则
const drugRules = {
  name: [
    { required: true, message: '请输入药品名称', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择药品分类', trigger: 'change' }
  ],
  price: [
    { required: true, message: '请输入价格', trigger: 'blur' }
  ],
  unit: [
    { required: true, message: '请输入单位', trigger: 'blur' }
  ]
};

// 药品列表数据
const drugList = ref([]);
const loadingDrugs = ref(false);

// 加载药品数据
const loadDrugInventory = async () => {
  try {
    loadingDrugs.value = true;
    const response = await getDrugInventory();
    drugList.value = response.data.map(item => ({
      id: item.id,
      code: item.code,
      name: item.name,
      type: item.type,
      price: item.price,
      stock: item.stock,
      warningStock: item.warningStock || 20,
      unit: item.unit,
      isActive: item.isActive
    }));
  } catch (error) {
    console.error('加载药品库存失败:', error);
    ElMessage.error('加载药品库存失败，请检查后端服务');
  } finally {
    loadingDrugs.value = false;
  }
};

// 方法
// 显示新增药品对话框
const showAddDrugDialog = () => {
  isEditMode.value = false;
  drugDialogVisible.value = true;
};

// 显示编辑药品对话框
const showEditDrugDialog = (row) => {
  isEditMode.value = true;
  drugForm.id = row.id; // 使用数据库主键 id
  drugForm.name = row.name;
  drugForm.type = row.type;
  drugForm.price = row.price;
  drugForm.stock = row.stock;
  drugForm.warningStock = row.warningStock || 20;  // 如果没有设置，默认为 20
  drugForm.unit = row.unit;
  drugForm.isActive = row.isActive !== undefined ? row.isActive : true;  // 保持原有状态
  drugDialogVisible.value = true;
};

// 重置表单
const resetDrugForm = () => {
  if (drugFormRef.value) {
    drugFormRef.value.resetFields();
  }
  drugForm.id = null;
  drugForm.name = '';
  drugForm.type = '';
  drugForm.price = 0;
  drugForm.stock = 0;
  drugForm.warningStock = 20;  // 重置为默认值
  drugForm.unit = '';
  drugForm.isActive = true;  // 重置为启用状态
};

// 提交表单
const submitDrugForm = async () => {
  if (!drugFormRef.value) return;
  
  await drugFormRef.value.validate(async (valid) => {
    if (!valid) return;
    
    try {
      submitting.value = true;
      const formData = {
        name: drugForm.name,
        type: drugForm.type,
        price: drugForm.price,
        stock: drugForm.stock,
        warningStock: drugForm.warningStock,
        unit: drugForm.unit,
        isActive: drugForm.isActive
      };
      
      if (isEditMode.value) {
        // 修改 - 不传递 code，保持原有编号
        await updateDrug(drugForm.id, formData);
        ElMessage.success('修改成功');
      } else {
        // 新增（编号由后端自动生成）
        await addDrug(formData);
        ElMessage.success('添加成功');
      }
      
      drugDialogVisible.value = false;
      await loadDrugInventory(); // 重新加载数据
    } catch (error) {
      console.error('操作失败:', error);
      ElMessage.error(isEditMode.value ? '修改失败' : '添加失败');
    } finally {
      submitting.value = false;
    }
  });
};

// 删除药品
const deleteDrug = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除药品"${row.name}"吗？`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    );
    
    await deleteDrugApi(row.id);
    ElMessage.success('删除成功');
    await loadDrugInventory(); // 重新加载数据
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error);
      ElMessage.error('删除失败');
    }
  }
};

// 切换药品启用状态
const toggleDrugStatus = async (row) => {
  try {
    const newStatus = !row.isActive;
    await updateDrug(row.id, {
      name: row.name,
      type: row.type,
      price: row.price,
      stock: row.stock,
      warningStock: row.warningStock,
      unit: row.unit,
      isActive: newStatus
    });
    row.isActive = newStatus;
    const status = newStatus ? '启用' : '禁用';
    ElMessage.success(`已${status}药品"${row.name}"`);
  } catch (error) {
    console.error('更新状态失败:', error);
    ElMessage.error('更新状态失败');
  }
};

const filterLowStock = () => {
  // 注意：这里简化处理，实际应保留原数据并用 computed 过滤
  drugList.value = drugList.value.filter(d => d.stock < (d.warningStock || 20));
  ElMessage.warning('已筛选出库存不足药品');
};

// 组件挂载时加载数据
onMounted(() => {
  loadDrugInventory();
});
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