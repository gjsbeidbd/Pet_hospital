<template>
  <div class="director-staff-management-wrapper">
    <el-card class="director-staff-management-card">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>员工资料管理</span>
          <div style="display: flex; align-items: center; gap: 10px;">
            <el-input
              v-model="searchKeyword"
              placeholder="请输入员工姓名或工号"
              style="width: 200px;"
              clearable
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button type="success" @click="openAddStaffDialog">新增员工</el-button>
          </div>
        </div>
      </template>
      
      <el-table :data="filteredStaffList" border style="width: 100%;">
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
          <template #default="scope">
            <el-button link type="primary" size="small" @click="openEditStaffDialog(scope.row)">编辑</el-button>
            <el-button link type="danger" size="small" @click="deleteStaff(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页组件 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          :small="false"
          :disabled="false"
          :background="true"
          layout="total, sizes, prev, pager, next, jumper"
          :total="staffList.length"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :pager-count="5"
          prev-text="上一页"
          next-text="下一页"
          :page-count="Math.ceil(staffList.length / pageSize)"
          :locale="{
            total: `共 {total} 条记录`,
            pagesize: '条/页',
            goto: '跳至',
            pageClassifier: '页'
          }"
        />
      </div>
    </el-card>
    
    <!-- 新增/编辑员工弹窗 -->
    <el-dialog 
      v-model="staffDialogVisible" 
      :title="editingStaff ? '编辑员工资料' : '新增员工'"
      width="600px"
    >
      <el-form :model="staffForm" label-width="80px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="工号" prop="id">
              <el-input v-model="staffForm.id" :disabled="!!editingStaff" placeholder="选择科室后自动生成"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="staffForm.name"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="职位" prop="role">
              <el-select v-model="staffForm.role" placeholder="请选择职位" style="width: 100%">
                <el-option label="医生" value="医生"></el-option>
                <el-option label="前台" value="前台"></el-option>
                <el-option label="护士" value="护士"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属科室" prop="department">
              <el-select v-model="staffForm.department" placeholder="请选择科室" style="width: 100%">
                <el-option label="内科" value="内科"></el-option>
                <el-option label="外科" value="外科"></el-option>
                <el-option label="护理部" value="护理部"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="staffForm.phone"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-switch
                v-model="staffForm.status"
                active-text="在职"
                inactive-text="离职"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="staffDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveStaff">{{ editingStaff ? '保存' : '新增' }}</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'

// 分页相关数据
const currentPage = ref(1)
const pageSize = ref(10)
const searchKeyword = ref('')

// 弹窗相关数据
const staffDialogVisible = ref(false)
const editingStaff = ref(null)

// 科室代码映射
const departmentCodes = {
  '内科': 'NK',
  '外科': 'WK',
  '护理部': 'HL'
}

// 员工表单数据
const staffForm = reactive({
  id: '',
  name: '',
  role: '',
  department: '',
  phone: '',
  status: true
})

// 监听科室变化，自动生成工号
watch(
  () => staffForm.department,
  (newDepartment) => {
    if (newDepartment) {
      // 生成新工号
      const deptCode = departmentCodes[newDepartment] || 'OT';
      const today = new Date();
      const dateStr = `${today.getFullYear()}${String(today.getMonth() + 1).padStart(2, '0')}${String(today.getDate()).padStart(2, '0')}`;
      
      // 如果是编辑模式且科室改变了，则更新工号
      if (editingStaff.value) {
        // 检查科室是否真的改变了
        if (editingStaff.value.department !== newDepartment) {
          // 查找当天该科室的最大序号
          let maxSeq = 0;
          staffList.value.forEach(staff => {
            if (staff.id.startsWith(`${deptCode}${dateStr}`)) {
              const seq = parseInt(staff.id.slice(8));
              if (seq > maxSeq) maxSeq = seq;
            }
          });
          
          const newSeq = String(maxSeq + 1).padStart(3, '0');
          staffForm.id = `${deptCode}${dateStr}${newSeq}`;
        }
      } 
      // 如果是新增模式
      else if (!editingStaff.value) {
        // 查找当天该科室的最大序号
        let maxSeq = 0;
        staffList.value.forEach(staff => {
          if (staff.id.startsWith(`${deptCode}${dateStr}`)) {
            const seq = parseInt(staff.id.slice(8));
            if (seq > maxSeq) maxSeq = seq;
          }
        });
        
        const newSeq = String(maxSeq + 1).padStart(3, '0');
        staffForm.id = `${deptCode}${dateStr}${newSeq}`;
      }
    }
  }
)

// 模拟数据：员工列表
const staffList = ref([
  { id: 'NK20230101001', name: '王建国', role: '医生', department: '内科', phone: '13800000001', status: true },
  { id: 'HL20230101001', name: '李晓红', role: '前台', department: '护理部', phone: '13800000002', status: true },
  { id: 'WK20230101001', name: '张伟', role: '医生', department: '外科', phone: '13800000003', status: true },
  { id: 'NK20230101002', name: '李医生', role: '医生', department: '内科', phone: '13800000004', status: true },
  { id: 'HL20230101002', name: '王护士', role: '护士', department: '护理部', phone: '13800000005', status: true },
  { id: 'WK20230101002', name: '张医生', role: '医生', department: '外科', phone: '13800000006', status: true }
])

// 计算过滤后的员工列表
const filteredStaffList = computed(() => {
  let filtered = staffList.value
  
  // 根据搜索关键词过滤
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    filtered = filtered.filter(staff => 
      staff.name.toLowerCase().includes(keyword) || 
      staff.id.includes(keyword)
    )
  }
  
  // 分页处理
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filtered.slice(start, end)
})

// 分页事件处理
const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
}

const handleCurrentChange = (val) => {
  currentPage.value = val
}

// 搜索处理
const handleSearch = () => {
  currentPage.value = 1
}

// 打开新增员工弹窗
const openAddStaffDialog = () => {
  editingStaff.value = null
  // 重置表单
  Object.assign(staffForm, {
    id: '',
    name: '',
    role: '',
    department: '',
    phone: '',
    status: true
  })
  staffDialogVisible.value = true
}

// 打开编辑员工弹窗
const openEditStaffDialog = (staff) => {
  editingStaff.value = staff
  // 填充表单数据
  Object.assign(staffForm, { ...staff })
  staffDialogVisible.value = true
}

// 保存员工信息
const saveStaff = () => {
  // 表单验证
  if (!staffForm.name || !staffForm.id) {
    ElMessage.error('请填写必填信息')
    return
  }
  
  if (editingStaff.value) {
    // 编辑模式
    const index = staffList.value.findIndex(s => s.id === editingStaff.value.id)
    if (index !== -1) {
      staffList.value[index] = { ...staffForm }
      ElMessage.success('员工信息更新成功')
    }
  } else {
    // 新增模式
    // 检查工号是否重复
    if (staffList.value.some(s => s.id === staffForm.id)) {
      ElMessage.error('工号已存在，请重新输入')
      return
    }
    
    staffList.value.push({ ...staffForm })
    ElMessage.success('新增员工成功')
  }
  
  staffDialogVisible.value = false
}

// 删除员工
const deleteStaff = (staff) => {
  ElMessageBox.confirm(
    `确定要删除员工 ${staff.name} 吗？此操作不可撤销。`,
    '确认删除',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(() => {
    staffList.value = staffList.value.filter(s => s.id !== staff.id)
    ElMessage.success('删除成功')
  }).catch(() => {
    // 用户取消删除
  })
}
</script>

<style scoped>
.director-staff-management-wrapper {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.director-staff-management-card {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.director-staff-management-card :deep(.el-card__body) {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.director-staff-management-card :deep(.el-table) {
  flex: 1;
}

.pagination-container {
  display: flex;
  justify-content: center;
  padding: 20px 0;
}
</style>