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
            <el-button type="primary" @click="addNewPrice" :icon="Plus">
              + 新增收费项目
            </el-button>
          </div>
          <el-table :data="priceSettings" border style="width: 100%" class="data-table">
            <el-table-column prop="item" label="收费项目" min-width="180" show-overflow-tooltip></el-table-column>
            <el-table-column prop="category" label="费用类别" width="120" show-overflow-tooltip></el-table-column>
            <el-table-column prop="price" label="当前价格" width="140">
              <template #default="scope">
                <el-input-number 
                  v-model="scope.row.price" 
                  :min="0" 
                  :precision="2" 
                  :step="1"
                  size="small"
                  controls-position="right"
                  style="width: 120px"
                />
              </template>
            </el-table-column>
            <el-table-column prop="unit" label="单位" width="100"></el-table-column>
            <el-table-column label="操作" width="180" fixed="right" align="center">
              <template #default="scope">
                <el-button type="primary" size="small" @click="savePrice(scope.row)">保存</el-button>
                <el-button type="danger" size="small" @click="deletePrice(scope.row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <!-- 宠物种类管理 -->
        <el-tab-pane label="宠物种类管理" name="pet-type">
          <div class="table-toolbar">
            <el-button type="primary" @click="showAddPetTypeDialog" :icon="Plus">
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
                <el-button type="primary" size="small" @click="savePetType(scope.row)">保存</el-button>
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
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Plus, CircleCheckFilled, RefreshLeft } from '@element-plus/icons-vue';

// 当前激活的选项卡
const activeTab = ref('fee');

// 费用管理数据
const priceSettings = ref([
  { item: '普通挂号费', category: '诊疗费', price: 20, unit: '次' },
  { item: '专家挂号费', category: '诊疗费', price: 50, unit: '次' },
  { item: '住院护理费', category: '住院费', price: 100, unit: '天' },
  { item: '绝育手术基础费', category: '手术费', price: 600, unit: '次' },
  { item: '疫苗接种费', category: '防疫费', price: 80, unit: '针' },
  { item: '血常规检查', category: '检查费', price: 120, unit: '次' },
  { item: 'X 光检查', category: '检查费', price: 200, unit: '次' },
  { item: 'B 超检查', category: '检查费', price: 150, unit: '次' },
]);

// 宠物种类数据
const petTypes = ref([
  { typeName: '猫', breeds: ['英短', '美短', '布偶', '暹罗', '波斯', '金吉拉'], description: '包括各种品种的猫', status: true },
  { typeName: '狗', breeds: ['泰迪', '比熊', '金毛', '拉布拉多', '哈士奇', '萨摩耶', '边境牧羊犬'], description: '包括各种品种的狗', status: true },
  { typeName: '兔', breeds: ['垂耳兔', '侏儒兔', '安哥拉兔', '荷兰猪'], description: '包括各种品种的兔子', status: true },
  { typeName: '仓鼠', breeds: ['金丝熊', '三线仓鼠', '一线仓鼠', '公婆仓鼠'], description: '包括仓鼠、金丝熊等小型啮齿类', status: true },
  { typeName: '鸟', breeds: ['鹦鹉', '文鸟', '珍珠鸟', '虎皮鹦鹉'], description: '包括鹦鹉、文鸟等观赏鸟类', status: true },
  { typeName: '异宠', breeds: ['龙猫', '雪貂', '刺猬', '守宫'], description: '包括龙猫、雪貂等特殊宠物', status: false },
]);

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

// 费用管理方法
const savePrice = (row) => {
  console.log('保存费用:', row);
  ElMessage.success(`"${row.item}"价格已更新为 ${row.price}元/${row.unit}`);
};

const deletePrice = (row) => {
  ElMessageBox.confirm(
    `确定要删除收费项目"${row.item}"吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    const index = priceSettings.value.findIndex(item => item.item === row.item);
    if (index !== -1) {
      priceSettings.value.splice(index, 1);
      ElMessage.success('删除成功');
    }
  }).catch(() => {});
};

const addNewPrice = () => {
  ElMessageBox.prompt('请输入新收费项目名称', '新增收费项目', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputPattern: /.+/,
    inputErrorMessage: '请输入项目名称'
  }).then(({ value }) => {
    priceSettings.value.push({
      item: value,
      category: '其他',
      price: 0,
      unit: '次'
    });
    ElMessage.success('添加成功');
  }).catch(() => {});
};

// 宠物种类管理方法
const savePetType = (row) => {
  console.log('保存宠物种类:', row);
  ElMessage.success(`"${row.typeName}"信息已保存`);
};

const togglePetTypeStatus = (row) => {
  row.status = !row.status;
  ElMessage.success(`已${row.status ? '启用' : '禁用'}"${row.typeName}"`);
};

const deletePetType = (row) => {
  ElMessageBox.confirm(
    `确定要删除宠物种类"${row.typeName}"吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    const index = petTypes.value.findIndex(item => item.typeName === row.typeName);
    if (index !== -1) {
      petTypes.value.splice(index, 1);
      ElMessage.success('删除成功');
    }
  }).catch(() => {});
};

// 添加新的宠物种类对话框
const showAddPetTypeDialog = () => {
  ElMessageBox.prompt('请输入新宠物种类名称', '新增宠物种类', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputPattern: /.+/,
    inputErrorMessage: '请输入种类名称'
  }).then(({ value }) => {
    // 检查是否已存在
    const exists = petTypes.value.some(item => item.typeName === value);
    if (exists) {
      ElMessage.warning('该宠物种类已存在');
      return;
    }
    
    petTypes.value.push({
      typeName: value,
      breeds: [],
      description: '',
      status: true
    });
    ElMessage.success('添加成功，您可以继续添加该种类的常见品种');
  }).catch(() => {});
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