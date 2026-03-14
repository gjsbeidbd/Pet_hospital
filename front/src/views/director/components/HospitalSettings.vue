<template>
  <div class="hospital-settings-wrapper">
    <el-card class="settings-card">
      <template #header>
        <div class="card-header">
          <span class="page-title">医院服务与设置</span>
        </div>
      </template>
      
      <el-tabs v-model="activeTab" type="border-card" class="settings-tabs">
        <!-- 费用管理 -->
        <el-tab-pane label="费用管理" name="fee">
          <div class="table-toolbar">
            <el-button type="primary" @click="showAddFeeDialog">
              + 新增收费项目
            </el-button>
          </div>
          <el-table :data="priceSettings" border style="width: 100%" class="data-table">
            <el-table-column prop="item" label="收费项目" min-width="180" show-overflow-tooltip></el-table-column>
            <el-table-column prop="category" label="费用类别" width="120" show-overflow-tooltip></el-table-column>
            <el-table-column prop="price" label="价格" width="100">
              <template #default="scope">
                <span style="color: #F56C6C; font-weight: bold;">￥{{ scope.row.price }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="unit" label="单位" width="80"></el-table-column>
            <el-table-column prop="description" label="描述说明" min-width="200" show-overflow-tooltip></el-table-column>
            <el-table-column label="操作" width="180" fixed="right" align="center">
              <template #default="scope">
                <el-button type="primary" size="small" @click="showEditFeeDialog(scope.row)">修改</el-button>
                <el-button type="danger" size="small" @click="deletePrice(scope.row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <!-- 宠物种类管理 -->
        <el-tab-pane label="宠物种类管理" name="pet-type">
          <div class="table-toolbar">
            <el-button type="primary" @click="showAddPetTypeDialog">
              + 新增宠物种类
            </el-button>
          </div>
          
          <el-table :data="petTypes" border style="width: 100%" class="data-table">
            <el-table-column prop="typeName" label="宠物种类" width="120"></el-table-column>
            <el-table-column label="常见品种" min-width="350">
              <template #default="scope">
                <div class="breed-tags-container">
                  <el-tag 
                    v-for="(breed, index) in scope.row.breeds" 
                    :key="index"
                    size="default"
                    closable
                    @close="removeBreed(scope.row, index)"
                    class="breed-tag"
                  >
                    {{ breed }}
                  </el-tag>
                  <el-button 
                    link 
                    type="primary" 
                    size="small" 
                    @click="showAddBreedDialog(scope.row)"
                    class="add-breed-btn"
                  >
                    + 添加品种
                  </el-button>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="description" label="描述说明" min-width="200" show-overflow-tooltip></el-table-column>
            <el-table-column label="状态" width="100" align="center">
              <template #default="scope">
                <el-tag :type="scope.row.status ? 'success' : 'info'" size="default">
                  {{ scope.row.status ? '启用' : '禁用' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="220" fixed="right" align="center">
              <template #default="scope">
                <el-button type="primary" size="small" @click="showEditPetTypeDialog(scope.row)">修改</el-button>
                <el-button 
                  :type="scope.row.status ? 'warning' : 'success'" 
                  size="small" 
                  @click="togglePetTypeStatus(scope.row)"
                >
                  {{ scope.row.status ? '禁用' : '启用' }}
                </el-button>
                <el-button type="danger" size="small" @click="deletePetType(scope.row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <!-- 公告管理 -->
        <el-tab-pane label="公告管理" name="announcement">
          <div class="announcement-container">
            <el-row :gutter="24" class="announcement-grid">
              <el-col :xs="24" :sm="24" :md="14" :lg="14">
                <el-card shadow="hover" class="form-card">
                  <template #header>
                    <div class="card-header-title">
                      <span>📢 发布新公告</span>
                    </div>
                  </template>
                  <el-form :model="announcementForm" label-position="top">
                    <el-form-item label="公告标题">
                      <el-input 
                        v-model="announcementForm.title" 
                        placeholder="请输入公告标题"
                        maxlength="50"
                        show-word-limit
                        clearable
                      ></el-input>
                    </el-form-item>
                    <el-form-item label="公告类型">
                      <el-select v-model="announcementForm.type" placeholder="请选择公告类型" style="width: 100%" clearable>
                        <el-option label="系统通知" value="system"></el-option>
                        <el-option label="医院动态" value="news"></el-option>
                        <el-option label="优惠活动" value="promotion"></el-option>
                        <el-option label="重要公告" value="important"></el-option>
                      </el-select>
                    </el-form-item>
                    <el-form-item label="公告内容">
                      <el-input 
                        v-model="announcementForm.content" 
                        type="textarea" 
                        :rows="6" 
                        placeholder="请输入公告详细内容..."
                        maxlength="1000"
                        show-word-limit
                        resize="vertical"
                      ></el-input>
                    </el-form-item>
                    <el-form-item class="form-actions">
                      <el-button type="primary" @click="publishAnnouncement" :icon="CircleCheckFilled">立即发布</el-button>
                      <el-button @click="resetAnnouncementForm" :icon="RefreshLeft">重置</el-button>
                    </el-form-item>
                  </el-form>
                </el-card>
              </el-col>
              
              <el-col :xs="24" :sm="24" :md="10" :lg="10">
                <el-card shadow="hover" class="list-card">
                  <template #header>
                    <div class="card-header-title">
                      <span>📋 已发布公告</span>
                      <span class="count-badge">{{ announcements.length }}条</span>
                    </div>
                  </template>
                  <el-table :data="announcements" border style="width: 100%" height="450" class="announcement-table">
                    <el-table-column prop="title" label="标题" min-width="140" show-overflow-tooltip></el-table-column>
                    <el-table-column prop="type" label="类型" width="90" align="center">
                      <template #default="scope">
                        <el-tag :type="getAnnouncementTypeTag(scope.row.type)" size="small">
                          {{ getAnnouncementTypeName(scope.row.type) }}
                        </el-tag>
                      </template>
                    </el-table-column>
                    <el-table-column prop="publishDate" label="日期" width="110" align="center"></el-table-column>
                    <el-table-column label="操作" width="120" fixed="right" align="center">
                      <template #default="scope">
                        <el-button type="primary" link size="small" @click="viewAnnouncement(scope.row)">查看</el-button>
                        <el-button type="danger" link size="small" @click="deleteAnnouncement(scope.row)">删除</el-button>
                      </template>
                    </el-table-column>
                  </el-table>
                </el-card>
              </el-col>
            </el-row>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 新增/编辑费用项目对话框 -->
    <el-dialog 
      v-model="feeDialogVisible" 
      :title="isEditMode ? '修改收费项目' : '新增收费项目'" 
      width="500px"
      @close="resetFeeForm"
    >
      <el-form :model="feeForm" label-width="100px" :rules="feeRules" ref="feeFormRef">
        <el-form-item label="收费项目" prop="itemName">
          <el-input v-model="feeForm.itemName" placeholder="请输入收费项目名称" maxlength="100"></el-input>
        </el-form-item>
        <el-form-item label="费用类别" prop="category">
          <el-select v-model="feeForm.category" placeholder="请选择费用类别" style="width: 100%">
            <el-option label="诊查费" value="诊查费"></el-option>
            <el-option label="治疗费" value="治疗费"></el-option>
            <el-option label="手术费" value="手术费"></el-option>
            <el-option label="检查费" value="检查费"></el-option>
            <el-option label="护理费" value="护理费"></el-option>
            <el-option label="其他" value="其他"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="价格" prop="unitPrice">
          <el-input-number 
            v-model="feeForm.unitPrice" 
            :min="0" 
            :precision="2" 
            :step="1"
            controls-position="right"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="单位" prop="unit">
          <el-input v-model="feeForm.unit" placeholder="如：次、支、台等" maxlength="20"></el-input>
        </el-form-item>
        <el-form-item label="描述说明" prop="description">
          <el-input 
            v-model="feeForm.description" 
            type="textarea" 
            :rows="3" 
            placeholder="请输入费用说明"
            maxlength="500"
          ></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="feeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitFeeForm" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>

    <!-- 新增/编辑宠物种类对话框 -->
    <el-dialog 
      v-model="petTypeDialogVisible" 
      :title="isPetTypeEditMode ? '修改宠物种类' : '新增宠物种类'" 
      width="500px"
      @close="resetPetTypeForm"
    >
      <el-form :model="petTypeForm" label-width="100px" :rules="petTypeRules" ref="petTypeFormRef">
        <el-form-item label="宠物种类" prop="speciesName">
          <el-input v-model="petTypeForm.speciesName" placeholder="请输入宠物种类名称" maxlength="50"></el-input>
        </el-form-item>
        <el-form-item label="描述说明" prop="description">
          <el-input 
            v-model="petTypeForm.description" 
            type="textarea" 
            :rows="3" 
            placeholder="请输入种类描述"
            maxlength="500"
          ></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="petTypeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitPetTypeForm" :loading="petTypeSubmitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Plus, CircleCheckFilled, RefreshLeft } from '@element-plus/icons-vue';
import { 
  getFeeItems, 
  addFeeItem, 
  updateFeeItem, 
  deleteFeeItem,
  getPetSpecies,
  addPetSpecies,
  updatePetSpecies,
  deletePetSpecies,
  getPetBreedsBySpeciesId,
  addPetBreed
} from '@/services/api';

// 当前激活的选项卡
const activeTab = ref('fee');

// 费用管理数据
const priceSettings = ref([]);
const loadingFeeItems = ref(false);

// 费用表单相关
const feeDialogVisible = ref(false);
const isEditMode = ref(false);
const submitting = ref(false);
const feeFormRef = ref(null);
const feeForm = reactive({
  id: null,
  itemName: '',
  category: '',
  unitPrice: 0,
  unit: '',
  description: ''
});

// 表单验证规则
const feeRules = {
  itemName: [
    { required: true, message: '请输入收费项目名称', trigger: 'blur' },
    { min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  category: [
    { required: true, message: '请选择费用类别', trigger: 'change' }
  ],
  unitPrice: [
    { required: true, message: '请输入价格', trigger: 'blur' }
  ],
  unit: [
    { required: true, message: '请输入单位', trigger: 'blur' }
  ]
};

// 宠物种类数据
const petTypes = ref([]);
const loadingPetTypes = ref(false);

// 宠物种类表单相关
const petTypeDialogVisible = ref(false);
const isPetTypeEditMode = ref(false);
const petTypeSubmitting = ref(false);
const petTypeFormRef = ref(null);
const petTypeForm = reactive({
  id: null,
  speciesName: '',
  description: ''
});

// 宠物种类表单验证规则
const petTypeRules = {
  speciesName: [
    { required: true, message: '请输入宠物种类名称', trigger: 'blur' },
    { min: 1, max: 50, message: '长度在 1 到 50 个字符', trigger: 'blur' }
  ]
};

// 公告表单
const announcementForm = reactive({
  title: '',
  type: '',
  content: ''
});

// 公告列表
const announcements = ref([
  { id: 1, title: '系统维护通知', type: 'system', publishDate: '2026-03-10', content: '系统将于今晚 23:00 进行维护...' },
  { id: 2, title: '春季疫苗接种优惠活动', type: 'promotion', publishDate: '2026-03-08', content: '即日起至 4 月 30 日，疫苗接种享受 8 折优惠...' },
  { id: 3, title: '新医生坐诊通知', type: 'news', publishDate: '2026-03-05', content: '我院特邀资深兽医专家王医生于 3 月 15 日起正式坐诊...' },
  { id: 4, title: '春节期间门诊安排', type: 'important', publishDate: '2026-02-01', content: '春节期间门诊时间安排如下...' },
]);

// 加载费用项目
const loadFeeItems = async () => {
  try {
    loadingFeeItems.value = true;
    const response = await getFeeItems();
    // 将后端数据转换为前端格式
    priceSettings.value = response.data.map(item => ({
      id: item.id,
      item: item.itemName,
      category: item.category,
      price: item.unitPrice,
      unit: item.unit || '次',
      description: item.description,
      isActive: item.isActive
    }));
  } catch (error) {
    console.error('加载费用项目失败:', error);
    ElMessage.error('加载费用项目失败，请检查后端服务');
  } finally {
    loadingFeeItems.value = false;
  }
};

// 加载宠物种类
const loadPetTypes = async () => {
  try {
    loadingPetTypes.value = true;
    const response = await getPetSpecies();
    // 获取每个种类下的品种
    const typesWithBreeds = await Promise.all(
      response.data.map(async (species) => {
        try {
          const breedsResponse = await getPetBreedsBySpeciesId(species.id);
          return {
            id: species.id,
            typeName: species.speciesName,
            breeds: breedsResponse.data.map(breed => breed.breedName),
            description: species.description,
            status: species.isActive
          };
        } catch (error) {
          console.error(`加载种类 ${species.speciesName} 的品种失败:`, error);
          return {
            id: species.id,
            typeName: species.speciesName,
            breeds: [],
            description: species.description,
            status: species.isActive
          };
        }
      })
    );
    petTypes.value = typesWithBreeds;
  } catch (error) {
    console.error('加载宠物种类失败:', error);
    ElMessage.error('加载宠物种类失败，请检查后端服务');
  } finally {
    loadingPetTypes.value = false;
  }
};

// 组件挂载时加载数据
onMounted(() => {
  loadFeeItems();
  loadPetTypes();
});

// 显示新增费用对话框
const showAddFeeDialog = () => {
  isEditMode.value = false;
  feeDialogVisible.value = true;
};

// 显示编辑费用对话框
const showEditFeeDialog = (row) => {
  isEditMode.value = true;
  feeForm.id = row.id;
  feeForm.itemName = row.item;
  feeForm.category = row.category;
  feeForm.unitPrice = row.price;
  feeForm.unit = row.unit;
  feeForm.description = row.description || '';
  feeDialogVisible.value = true;
};

// 重置表单
const resetFeeForm = () => {
  if (feeFormRef.value) {
    feeFormRef.value.resetFields();
  }
  feeForm.id = null;
  feeForm.itemName = '';
  feeForm.category = '';
  feeForm.unitPrice = 0;
  feeForm.unit = '';
  feeForm.description = '';
};

// 提交表单
const submitFeeForm = async () => {
  if (!feeFormRef.value) return;
  
  await feeFormRef.value.validate(async (valid) => {
    if (!valid) return;
    
    try {
      submitting.value = true;
      const formData = {
        itemName: feeForm.itemName,
        category: feeForm.category,
        unitPrice: feeForm.unitPrice,
        unit: feeForm.unit,
        description: feeForm.description
      };
      
      if (isEditMode.value) {
        // 修改
        await updateFeeItem(feeForm.id, formData);
        ElMessage.success('修改成功');
      } else {
        // 新增
        await addFeeItem(formData);
        ElMessage.success('添加成功');
      }
      
      feeDialogVisible.value = false;
      await loadFeeItems(); // 重新加载数据
    } catch (error) {
      console.error('操作失败:', error);
      ElMessage.error(isEditMode.value ? '修改失败' : '添加失败');
    } finally {
      submitting.value = false;
    }
  });
};

// 删除费用项目
const savePrice = (row) => {
  console.log('保存费用:', row);
  ElMessage.success(`"${row.item}"价格已更新为 ${row.price}元/${row.unit}`);
};

const deletePrice = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除收费项目"${row.item}"吗？`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    );
    
    await deleteFeeItem(row.id);
    ElMessage.success('删除成功');
    await loadFeeItems(); // 重新加载数据
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error);
      ElMessage.error('删除失败');
    }
  }
};


// 显示新增宠物种类对话框
const showAddPetTypeDialog = () => {
  isPetTypeEditMode.value = false;
  petTypeDialogVisible.value = true;
};

// 显示编辑宠物种类对话框
const showEditPetTypeDialog = (row) => {
  isPetTypeEditMode.value = true;
  petTypeForm.id = row.id;
  petTypeForm.speciesName = row.typeName;
  petTypeForm.description = row.description || '';
  petTypeDialogVisible.value = true;
};

// 重置宠物种类表单
const resetPetTypeForm = () => {
  if (petTypeFormRef.value) {
    petTypeFormRef.value.resetFields();
  }
  petTypeForm.id = null;
  petTypeForm.speciesName = '';
  petTypeForm.description = '';
};

// 提交宠物种类表单
const submitPetTypeForm = async () => {
  if (!petTypeFormRef.value) return;
  
  await petTypeFormRef.value.validate(async (valid) => {
    if (!valid) return;
    
    try {
      petTypeSubmitting.value = true;
      const formData = {
        speciesName: petTypeForm.speciesName,
        description: petTypeForm.description
      };
      
      if (isPetTypeEditMode.value) {
        // 修改
        await updatePetSpecies(petTypeForm.id, formData);
        ElMessage.success('修改成功');
      } else {
        // 新增
        await addPetSpecies(formData);
        ElMessage.success('添加成功');
      }
      
      petTypeDialogVisible.value = false;
      await loadPetTypes(); // 重新加载数据
    } catch (error) {
      console.error('操作失败:', error);
      ElMessage.error(isPetTypeEditMode.value ? '修改失败' : '添加失败');
    } finally {
      petTypeSubmitting.value = false;
    }
  });
};

// 切换宠物种类状态
const togglePetTypeStatus = async (row) => {
  try {
    const newStatus = !row.status;
    await updatePetSpecies(row.id, {
      speciesName: row.typeName,
      description: row.description,
      isActive: newStatus
    });
    row.status = newStatus;
    ElMessage.success(`已${newStatus ? '启用' : '禁用'}"${row.typeName}"`);
  } catch (error) {
    console.error('更新状态失败:', error);
    ElMessage.error('更新状态失败');
  }
};

// 删除宠物种类
const deletePetType = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除宠物种类"${row.typeName}"吗？`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    );
    
    await deletePetSpecies(row.id);
    ElMessage.success('删除成功');
    await loadPetTypes(); // 重新加载数据
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error);
      ElMessage.error('删除失败');
    }
  }
};

// 显示添加品种对话框
const showAddBreedDialog = (row) => {
  ElMessageBox.prompt(`为"${row.typeName}"添加新品种`, '添加品种', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputPattern: /.+/,
    inputErrorMessage: '请输入品种名称'
  }).then(({ value }) => {
    // 检查是否已存在
    const exists = row.breeds.some(breed => breed === value);
    if (exists) {
      ElMessage.warning('该品种已存在');
      return;
    }
    
    row.breeds.push(value);
    ElMessage.success('品种添加成功');
  }).catch(() => {});
};

// 移除品种
const removeBreed = (row, index) => {
  row.breeds.splice(index, 1);
  ElMessage.success('品种已移除');
};;

// 公告管理方法
const publishAnnouncement = () => {
  if (!announcementForm.title || !announcementForm.type || !announcementForm.content) {
    ElMessage.warning('请填写完整的公告信息');
    return;
  }
  
  const newAnnouncement = {
    id: Date.now(),
    title: announcementForm.title,
    type: announcementForm.type,
    publishDate: new Date().toISOString().split('T')[0],
    content: announcementForm.content
  };
  
  announcements.value.unshift(newAnnouncement);
  ElMessage.success('公告发布成功');
  resetAnnouncementForm();
};

const resetAnnouncementForm = () => {
  announcementForm.title = '';
  announcementForm.type = '';
  announcementForm.content = '';
};

const viewAnnouncement = (row) => {
  ElMessageBox.alert(
    `<div style="text-align: left;">
      <h3>${row.title}</h3>
      <p><strong>类型:</strong> ${getAnnouncementTypeName(row.type)}</p>
      <p><strong>发布日期:</strong> ${row.publishDate}</p>
      <hr/>
      <p>${row.content}</p>
    </div>`,
    '公告详情',
    {
      dangerouslyUseHTMLString: true,
      confirmButtonText: '关闭'
    }
  );
};

const deleteAnnouncement = (row) => {
  ElMessageBox.confirm(
    `确定要删除公告"${row.title}"吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    const index = announcements.value.findIndex(item => item.id === row.id);
    if (index !== -1) {
      announcements.value.splice(index, 1);
      ElMessage.success('删除成功');
    }
  }).catch(() => {});
};

// 辅助函数：获取公告类型标签样式
const getAnnouncementTypeTag = (type) => {
  const tagMap = {
    system: 'info',
    news: 'success',
    promotion: 'warning',
    important: 'danger'
  };
  return tagMap[type] || 'info';
};

// 辅助函数：获取公告类型中文名称
const getAnnouncementTypeName = (type) => {
  const nameMap = {
    system: '系统通知',
    news: '医院动态',
    promotion: '优惠活动',
    important: '重要公告'
  };
  return nameMap[type] || '未知';
};
</script>

<style scoped>
.hospital-settings-wrapper {
  min-height: calc(100vh - 140px);
  display: flex;
  flex-direction: column;
  padding: 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8ecf1 100%);
}

.settings-card {
  flex: 1;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.settings-tabs {
  min-height: 500px;
}

:deep(.el-tabs__content) {
  padding: 24px;
}

.table-toolbar {
  margin-bottom: 16px;
  display: flex;
  justify-content: flex-start;
}

.data-table {
  border-radius: 8px;
  overflow: hidden;
}

.data-table :deep(.el-table__header th) {
  background-color: #f8f9fa;
  color: #606266;
  font-weight: 600;
}

/* 宠物品种标签样式 */
.breed-tags-container {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
  padding: 4px 0;
}

.breed-tag {
  transition: all 0.3s;
}

.breed-tag:hover {
  transform: scale(1.05);
}

.add-breed-btn {
  padding: 0 4px;
  font-size: 12px;
}

/* 公告管理样式 */
.announcement-container {
  width: 100%;
}

.announcement-grid {
  width: 100%;
}

.form-card,
.list-card {
  border-radius: 10px;
  margin-bottom: 0;
  height: 100%;
}

.card-header-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  font-size: 15px;
  color: #303133;
}

.count-badge {
  font-size: 12px;
  color: #909399;
  background: #f4f4f5;
  padding: 2px 8px;
  border-radius: 10px;
}

.form-actions {
  margin-top: 20px;
  text-align: right;
}

.announcement-table :deep(.el-table__header th) {
  background-color: #fafafa;
}

/* 响应式适配 */
@media screen and (max-width: 1366px) {
  .hospital-settings-wrapper {
    padding: 15px;
  }
  
  :deep(.el-tabs__content) {
    padding: 16px;
  }
}

@media screen and (max-width: 1024px) {
  .settings-tabs {
    min-height: 400px;
  }
}

/* Element Plus 组件覆盖样式 */
:deep(.el-tabs--border-card) {
  background: #fff;
  border: none;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  border-radius: 8px;
}

:deep(.el-card) {
  border-radius: 8px;
}

:deep(.el-input-number) {
  --el-input-number-width: 120px;
}

/* 表格操作按钮优化 */
:deep(.el-table .cell) {
  line-height: 20px;
}

:deep(.el-button + .el-button) {
  margin-left: 4px;
}

/* 滚动条美化 */
:deep(.el-table__body-wrapper)::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

:deep(.el-table__body-wrapper)::-webkit-scrollbar-thumb {
  background: #dcdfe6;
  border-radius: 3px;
}

:deep(.el-table__body-wrapper)::-webkit-scrollbar-thumb:hover {
  background: #c0c4cc;
}
</style>