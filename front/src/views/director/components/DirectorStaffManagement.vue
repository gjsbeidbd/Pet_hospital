  <template>
  <div class="director-staff-management-wrapper">
    <el-card class="director-staff-management-card">
      <!-- 选项卡 -->
      <el-tabs v-model="activeTab" type="border-card" class="staff-tabs">
        <!-- 医生管理 Tab -->
        <el-tab-pane label="医生管理" name="doctor">
          <div class="tab-content">
            <!-- 搜索和筛选 -->
            <div class="search-bar">
              <el-input
                v-model="searchKeywordDoctor"
                placeholder="搜索医生姓名或工号"
                clearable
                style="width: 300px"
                @input="handleSearchDoctor"
              >
                <template #prefix>
                  <el-icon><Search /></el-icon>
                </template>
              </el-input>
              
              <el-select
                v-model="selectedDepartmentDoctor"
                placeholder="选择科室"
                clearable
                style="width: 200px; margin-left: 10px"
                @change="currentPageDoctor = 1"
              >
                <el-option
                  v-for="dept in departments"
                  :key="dept.code"
                  :label="dept.name"
                  :value="dept.name"
                />
              </el-select>
              
              <el-button 
                type="primary" 
                icon="Plus" 
                style="margin-left: 10px"
                @click="openAddDoctorDialog"
              >
                新增医生
              </el-button>
            </div>

            <!-- 表格 -->
            <el-table
              :data="filteredDoctorList"
              border
              stripe
              style="width: 100%; flex: 1"
              :cell-style="{padding: '8px 0'}"
            >
              <el-table-column prop="employeeId" label="工号" width="140" fixed></el-table-column>
              <el-table-column prop="name" label="姓名" width="100"></el-table-column>
              <el-table-column prop="department" label="科室" width="120"></el-table-column>
              <el-table-column prop="position" label="职称" min-width="120" show-overflow-tooltip></el-table-column>
              <el-table-column prop="phone" label="联系电话" width="130"></el-table-column>
              <el-table-column prop="idCard" label="身份证号码" width="180" show-overflow-tooltip></el-table-column>
              <el-table-column prop="address" label="居住地址" width="200" show-overflow-tooltip></el-table-column>
              <el-table-column label="操作" width="180" fixed="right">
                <template #default="{ row }">
                  <el-button type="primary" size="small" @click="openEditDoctorDialog(row)">编辑</el-button>
                  <el-button type="danger" size="small" @click="deleteStaff(row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>

            <!-- 分页 -->
            <div class="pagination-container">
              <el-pagination
                v-model:current-page="currentPageDoctor"
                v-model:page-size="pageSizeDoctor"
                :page-sizes="[10, 20, 50, 100]"
                :total="filteredDoctorList.length"
                layout="total, sizes, prev, pager, next, jumper"
                @size-change="handleSizeChangeDoctor"
                @current-change="handleCurrentChangeDoctor"
              />
            </div>
          </div>
        </el-tab-pane>

        <!-- 护士/前台管理 Tab -->
        <el-tab-pane label="护士/前台管理" name="nurse">
          <div class="tab-content">
            <!-- 搜索和筛选 -->
            <div class="search-bar">
              <el-input
                v-model="searchKeywordNurse"
                placeholder="搜索护士/前台姓名或工号"
                clearable
                style="width: 300px"
                @input="handleSearchNurse"
              >
                <template #prefix>
                  <el-icon><Search /></el-icon>
                </template>
              </el-input>
              
              <el-select
                v-model="selectedDepartmentNurse"
                placeholder="选择科室"
                clearable
                style="width: 200px; margin-left: 10px"
                @change="currentPageNurse = 1"
              >
                <el-option
                  v-for="dept in departments"
                  :key="dept.code"
                  :label="dept.name"
                  :value="dept.name"
                />
              </el-select>
              
              <el-button 
                type="primary" 
                icon="Plus" 
                style="margin-left: 10px"
                @click="openAddNurseDialog"
              >
                新增护士/前台
              </el-button>
            </div>

            <!-- 表格 -->
            <el-table
              :data="filteredNurseList"
              border
              stripe
              style="width: 100%; flex: 1"
              :cell-style="{padding: '8px 0'}"
            >
              <el-table-column prop="employeeId" label="工号" width="140" fixed></el-table-column>
              <el-table-column prop="name" label="姓名" width="100"></el-table-column>
              <el-table-column prop="department" label="科室" width="120"></el-table-column>
              <el-table-column prop="phone" label="联系电话" width="130"></el-table-column>
              <el-table-column prop="position" label="职称" min-width="120" show-overflow-tooltip></el-table-column>
              <el-table-column prop="idCard" label="身份证号码" width="180" show-overflow-tooltip></el-table-column>
              <el-table-column prop="address" label="居住地址" width="200" show-overflow-tooltip></el-table-column>
              <el-table-column label="操作" width="180" fixed="right">
                <template #default="{ row }">
                  <el-button type="primary" size="small" @click="openEditNurseDialog(row)">编辑</el-button>
                  <el-button type="danger" size="small" @click="deleteStaff(row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>

            <!-- 分页 -->
            <div class="pagination-container">
              <el-pagination
                v-model:current-page="currentPageNurse"
                v-model:page-size="pageSizeNurse"
                :page-sizes="[10, 20, 50, 100]"
                :total="filteredNurseList.length"
                layout="total, sizes, prev, pager, next, jumper"
                @size-change="handleSizeChangeNurse"
                @current-change="handleCurrentChangeNurse"
              />
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
    
    <!-- 新增/编辑员工弹窗 (医生) -->
    <el-dialog 
      v-model="doctorDialogVisible" 
      :title="editingDoctor ? '编辑医生资料' : '新增医生'"
      width="700px"
    >
      <el-form :model="doctorForm" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="工号" prop="id">
              <el-input v-model="doctorForm.id" :disabled="!!editingDoctor" placeholder="选择科室后自动生成"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="doctorForm.name"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="所属科室" prop="department">
              <el-select v-model="doctorForm.department" placeholder="请选择科室" style="width: 100%">
                <el-option
                  v-for="dept in departments.filter(d => d.name !== '护理部')"
                  :key="dept.id"
                  :label="dept.name"
                  :value="dept.name"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="职称" prop="position">
              <el-select v-model="doctorForm.position" placeholder="请选择职称" style="width: 100%">
                <el-option 
                  label="住院医师" 
                  value="住院医师"
                ></el-option>
                <el-option 
                  label="主治医师" 
                  value="主治医师"
                ></el-option>
                <el-option 
                  label="副主任医师" 
                  value="副主任医师"
                ></el-option>
                <el-option 
                  label="主任医师" 
                  value="主任医师"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone">
              <el-input 
                v-model="doctorForm.phone" 
                placeholder="请输入 11 位手机号码" 
                maxlength="11"
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="出生日期" prop="birthday">
              <el-date-picker
                v-model="doctorForm.birthday"
                type="date"
                placeholder="请选择出生日期"
                style="width: 100%"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="身份证号码" prop="idCard">
              <el-input v-model="doctorForm.idCard" placeholder="请输入 18 位身份证号码" maxlength="18"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="居住地址" prop="address">
              <el-input v-model="doctorForm.address" type="textarea" :rows="2" placeholder="请输入详细居住地址" maxlength="200"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="doctorDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveDoctor">{{ editingDoctor ? '保存' : '新增' }}</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 新增/编辑员工弹窗 (护士/前台) -->
    <el-dialog 
      v-model="nurseDialogVisible" 
      :title="editingNurse ? '编辑员工资料' : '新增护士/前台'"
      width="700px"
    >
      <el-form :model="nurseForm" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="工号" prop="id">
              <el-input v-model="nurseForm.id" :disabled="!!editingNurse" placeholder="选择科室后自动生成"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="nurseForm.name"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="所属科室" prop="department">
              <el-select v-model="nurseForm.department" placeholder="请选择科室" style="width: 100%">
                <el-option
                  v-for="dept in departments"
                  :key="dept.id"
                  :label="dept.name"
                  :value="dept.name"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="职称" prop="position">
              <el-select v-model="nurseForm.position" placeholder="请选择职称" style="width: 100%">
                <el-option 
                  label="护士" 
                  value="护士"
                ></el-option>
                <el-option 
                  label="护师" 
                  value="护师"
                ></el-option>
                <el-option 
                  label="主管护师" 
                  value="主管护师"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone">
              <el-input 
                v-model="nurseForm.phone" 
                placeholder="请输入 11 位手机号码" 
                maxlength="11"
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="出生日期" prop="birthday">
              <el-date-picker
                v-model="nurseForm.birthday"
                type="date"
                placeholder="请选择出生日期"
                style="width: 100%"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="身份证号码" prop="idCard">
              <el-input v-model="nurseForm.idCard" placeholder="请输入 18 位身份证号码" maxlength="18"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="居住地址" prop="address">
              <el-input v-model="nurseForm.address" type="textarea" :rows="2" placeholder="请输入详细居住地址" maxlength="200"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="nurseDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveNurse">{{ editingNurse ? '保存' : '新增' }}</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { getAllStaff, updateStaff, deleteStaff as deleteStaffApi, getDepartments, addStaff, getReceptionistDepartments } from '@/services/api'

// 分页相关数据
const currentPage = ref(1)
const pageSize = ref(10)
const searchKeyword = ref('')
const selectedDepartment = ref('') // 新增：选中的科室

// 科室列表
const departments = ref([])

// 根据科室名称获取科室代码
const getDepartmentCode = (deptName) => {
  if (!departments.value || departments.value.length === 0) {
    console.warn('科室列表为空');
    return 'OT';
  }
  const dept = departments.value.find(d => d.name === deptName);
  const code = dept ? dept.code : 'OT';
  console.log(`科室 ${deptName} 对应的代码为：${code}`);
  return code;
}

// 医生弹窗相关数据
const doctorDialogVisible = ref(false)
const editingDoctor = ref(null)

// 医生表单数据
const doctorForm = reactive({
  id: '',
  name: '',
  role: '医生',
  department: '',
  phone: '',
  birthday: '',
  idCard: '',
  address: '',
  position: '', // 职称/职位
  status: true
})

// 护士/前台弹窗相关数据
const nurseDialogVisible = ref(false)
const editingNurse = ref(null)

// 护士/前台表单数据
const nurseForm = reactive({
  id: '',
  name: '',
  role: '',
  department: '',
  phone: '',
  birthday: '',
  idCard: '',
  address: '',
  position: '', // 职称/职位
  status: true
})

// 员工列表数据
const staffList = ref([])

// 在组件顶部添加响应式数据
const activeTab = ref('doctor') // 默认显示医生管理

// 医生相关的数据
const searchKeywordDoctor = ref('')
const selectedDepartmentDoctor = ref('')
const currentPageDoctor = ref(1)
const pageSizeDoctor = ref(10)

// 护士相关的数据
const searchKeywordNurse = ref('')
const selectedDepartmentNurse = ref('')
const currentPageNurse = ref(1)
const pageSizeNurse = ref(10)

// 加载员工数据
const loadStaffList = async () => {
  try {
    const response = await getAllStaff();
    const allStaff = response.data.map(item => ({
      id: item.id,
      employeeId: item.employeeId,
      name: item.name,
      department: item.department,
      phone: item.phone,
      birthday: item.birthday || '',
      idCard: item.idCard || '',
      address: item.address || '',
      position: item.position, // 职称
      status: item.status,
      role: item.position ? '护士' : '前台' // 根据职称判断，有职称为护士，否则为前台
    }));
    
    // 分离医生和护士/前台数据
    doctorList.value = allStaff.filter(staff => staff.position && ['住院医师', '主治医师', '副主任医师', '主任医师'].includes(staff.position));
    nurseList.value = allStaff.filter(staff => !staff.position || ['护士', '护师', '主管护师'].includes(staff.position));
  } catch (error) {
    console.error('加载员工数据失败:', error);
    ElMessage.error('加载员工数据失败，请检查后端服务');
  }
}

// 加载科室数据
const loadDepartments = async () => {
  try {
    const response = await getDepartments();
    console.log('科室接口原始数据:', response.data);
    // 只显示激活的科室
    departments.value = response.data.filter(dept => dept.isActive !== false);
    console.log('加载到的科室:', departments.value);
    
    if (departments.value.length === 0) {
      ElMessage.warning('暂无可用科室，请先在科室管理中添加科室');
    }
  } catch (error) {
    console.error('加载科室数据失败:', error);
    ElMessage.error('加载科室数据失败：' + (error.message || '未知错误'));
  }
}

// 加载前台表中的科室数据
const loadReceptionistDepartments = async () => {
  try {
    const response = await getReceptionistDepartments();
    console.log('前台科室数据:', response.data);
    
    // 将前台表中的科室添加到科室列表中（去重）
    const existingDeptNames = new Set(departments.value.map(d => d.name));
    
    response.data.forEach(dept => {
      if (!existingDeptNames.has(dept.name)) {
        departments.value.push({
          name: dept.name,
          code: 'RCPT', // 前台科室统一代码
          isActive: true
        });
        existingDeptNames.add(dept.name);
      }
    });
    
    console.log('合并后的科室列表:', departments.value);
  } catch (error) {
    console.error('加载前台科室数据失败:', error);
    // 不显示错误提示，只是无法使用前台表中的科室而已
  }
}

// 添加两个列表数组
const doctorList = ref([])
const nurseList = ref([])

// 计算属性 - 过滤后的医生列表
const filteredDoctorList = computed(() => {
  let filtered = doctorList.value
  
  if (selectedDepartmentDoctor.value) {
    filtered = filtered.filter(staff => staff.department === selectedDepartmentDoctor.value)
  }
  
  if (searchKeywordDoctor.value) {
    const keyword = searchKeywordDoctor.value.toLowerCase()
    filtered = filtered.filter(staff => 
      staff.name.toLowerCase().includes(keyword) || 
      staff.employeeId.includes(keyword)
    )
  }
  
  const start = (currentPageDoctor.value - 1) * pageSizeDoctor.value
  const end = start + pageSizeDoctor.value
  return filtered.slice(start, end)
})

// 计算属性 - 过滤后的护士列表
const filteredNurseList = computed(() => {
  let filtered = nurseList.value
  
  if (selectedDepartmentNurse.value) {
    filtered = filtered.filter(staff => staff.department === selectedDepartmentNurse.value)
  }
  
  if (searchKeywordNurse.value) {
    const keyword = searchKeywordNurse.value.toLowerCase()
    filtered = filtered.filter(staff => 
      staff.name.toLowerCase().includes(keyword) || 
      staff.employeeId.includes(keyword)
    )
  }
  
  const start = (currentPageNurse.value - 1) * pageSizeNurse.value
  const end = start + pageSizeNurse.value
  return filtered.slice(start, end)
})

// 医生相关的事件处理
const handleSearchDoctor = () => {
  currentPageDoctor.value = 1
}

const handleSizeChangeDoctor = (val) => {
  pageSizeDoctor.value = val
  currentPageDoctor.value = 1
}

const handleCurrentChangeDoctor = (val) => {
  currentPageDoctor.value = val
}

const openAddDoctorDialog = () => {
  editingDoctor.value = null
  Object.assign(doctorForm, {
    id: '',
    name: '',
    role: '医生',
    department: '',
    phone: '',
    birthday: '',
    idCard: '',
    address: '',
    position: '',
    status: true
  })
  
  // 生成医生编号：D + 5 位数字
  const maxSeq = doctorList.value.reduce((max, staff) => {
    const match = staff.employeeId?.match(/^D(\d{5})$/)
    return match ? Math.max(max, parseInt(match[1])) : max
  }, 0)
  
  doctorForm.id = `D${String(maxSeq + 1).padStart(5, '0')}`
  doctorDialogVisible.value = true
}

// 打开编辑员工弹窗 - 医生
const openEditDoctorDialog = (staff) => {
  editingDoctor.value = staff
  // 填充表单数据
  Object.assign(doctorForm, { 
    id: staff.employeeId,
    name: staff.name,
    role: '医生',
    department: staff.department,
    phone: staff.phone,
    birthday: staff.birthday || '',
    idCard: staff.idCard || '',
    address: staff.address || '',
    position: staff.position || '',
    status: staff.status
  })
  doctorDialogVisible.value = true
}

// 护士相关的事件处理
const handleSearchNurse = () => {
  currentPageNurse.value = 1
}

const handleSizeChangeNurse = (val) => {
  pageSizeNurse.value = val
  currentPageNurse.value = 1
}

const handleCurrentChangeNurse = (val) => {
  currentPageNurse.value = val
}

const openAddNurseDialog = () => {
  editingNurse.value = null
  Object.assign(nurseForm, {
    id: '',
    name: '',
    department: '', // 新增时不设置科室，让用户从科室表中选择
    phone: '',
    birthday: '',
    idCard: '',
    address: '',
    position: '护士', // 默认职称为护士
    status: true
  })
  
  // 生成护士/前台编号：N + 5 位数字
  const maxSeq = nurseList.value.reduce((max, staff) => {
    const match = staff.employeeId?.match(/^N(\d{5})$/)
    return match ? Math.max(max, parseInt(match[1])) : max
  }, 0)
  
  nurseForm.id = `N${String(maxSeq + 1).padStart(5, '0')}`
  nurseDialogVisible.value = true
}

// 打开编辑员工弹窗 - 护士/前台
const openEditNurseDialog = (staff) => {
  editingNurse.value = staff
  // 填充表单数据 - 使用 staff.department（从数据库获取的实际科室）
  Object.assign(nurseForm, { 
    id: staff.employeeId,
    name: staff.name,
    department: staff.department, // 编辑时显示数据库中存储的科室
    phone: staff.phone,
    birthday: staff.birthday || '',
    idCard: staff.idCard || '',
    address: staff.address || '',
    position: staff.position || '护士',
    status: staff.status
  })
  nurseDialogVisible.value = true
}

// 保存医生信息
const saveDoctor = async () => {
  // 表单验证
  if (!doctorForm.name || !doctorForm.id) {
    ElMessage.error('请填写必填信息')
    return
  }
  
  // 验证手机号（11 位数字）
  if (!/^1\d{10}$/.test(doctorForm.phone)) {
    ElMessage.error('请输入正确的 11 位手机号码')
    return
  }
  
  // 验证身份证号码（18 位）
  if (!/^\d{17}[\dXx]$/.test(doctorForm.idCard)) {
    ElMessage.error('请输入正确的 18 位身份证号码')
    return
  }
  
  try {
    if (editingDoctor.value) {
      // 编辑模式 - 调用 API 更新
      await updateStaff(editingDoctor.value.id, {
        role: '医生',
        name: doctorForm.name,
        birthday: doctorForm.birthday,
        idCard: doctorForm.idCard,
        address: doctorForm.address,
        department: doctorForm.department,
        phone: doctorForm.phone,
        position: doctorForm.position,
        status: doctorForm.status
      });
      ElMessage.success('医生信息更新成功')
    } else {
      // 新增模式 - 添加默认密码
      const newStaffData = {
        name: doctorForm.name,
        employeeId: doctorForm.id,
        department: doctorForm.department,
        role: '医生',
        position: doctorForm.position,
        phone: doctorForm.phone,
        email: `${doctorForm.phone}@pethospital.com`,
        birthday: doctorForm.birthday,
        idCard: doctorForm.idCard,
        address: doctorForm.address,
        hireDate: new Date().toISOString().split('T')[0],
        password: '123456'
      };
      
      console.log('提交的新增数据:', newStaffData);
      
      try {
        await addStaff(newStaffData);
        ElMessage.success('医生添加成功，默认密码：123456');
      } catch (error) {
        console.error('新增失败，详细错误:', error);
        throw error;
      }
    }
    
    doctorDialogVisible.value = false
    await loadStaffList()
  } catch (error) {
    console.error('保存失败:', error);
    ElMessage.error(editingDoctor.value ? '更新失败' : '新增失败')
  }
}

// 保存护士/前台信息
const saveNurse = async () => {
  // 表单验证
  if (!nurseForm.name || !nurseForm.id) {
    ElMessage.error('请填写必填信息')
    return
  }
  
  // 验证手机号（11 位数字）
  if (!/^1\d{10}$/.test(nurseForm.phone)) {
    ElMessage.error('请输入正确的 11 位手机号码')
    return
  }
  
  // 验证身份证号码（18 位）
  if (!/^\d{17}[\dXx]$/.test(nurseForm.idCard)) {
    ElMessage.error('请输入正确的 18 位身份证号码')
    return
  }
  
  try {
    if (editingNurse.value) {
      // 编辑模式 - 调用 API 更新
      await updateStaff(editingNurse.value.id, {
        role: '护士',
        name: nurseForm.name,
        birthday: nurseForm.birthday,
        idCard: nurseForm.idCard,
        address: nurseForm.address,
        department: nurseForm.department,
        phone: nurseForm.phone,
        position: nurseForm.position,
        status: nurseForm.status
      });
      ElMessage.success('员工信息更新成功')
    } else {
      // 新增模式 - 添加默认密码
      const newStaffData = {
        name: nurseForm.name,
        employeeId: nurseForm.id,
        department: nurseForm.department,
        role: '护士',
        position: nurseForm.position,
        phone: nurseForm.phone,
        email: `${nurseForm.phone}@pethospital.com`,
        birthday: nurseForm.birthday,
        idCard: nurseForm.idCard,
        address: nurseForm.address,
        hireDate: new Date().toISOString().split('T')[0],
        password: '123456'
      };
      
      console.log('提交的新增数据:', newStaffData);
      
      try {
        await addStaff(newStaffData);
        ElMessage.success('员工添加成功，默认密码：123456');
      } catch (error) {
        console.error('新增失败，详细错误:', error);
        throw error;
      }
    }
    
    nurseDialogVisible.value = false
    await loadStaffList()
  } catch (error) {
    console.error('保存失败:', error);
    ElMessage.error(editingNurse.value ? '更新失败' : '新增失败')
  }
}

// 删除员工
const deleteStaff = async (staff) => {
  console.log('准备删除的员工信息:', staff);
  console.log('员工 ID:', staff.id);
  
  try {
    await ElMessageBox.confirm(
      `确定要删除员工 ${staff.name} 吗？此操作不可撤销。`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    );
    
    console.log('用户确认删除，调用 API...');
    const result = await deleteStaffApi(staff.id);
    console.log('删除结果:', result);
    
    ElMessage.success('删除成功')
    await loadStaffList() // 重新加载数据
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败，错误详情:', error);
      ElMessage.error('删除失败：' + (error.message || '未知错误'))
    }
  }
}
// 组件挂载时加载数据
onMounted(() => {
  loadDepartments() // 加载科室数据
  loadReceptionistDepartments() // 加载前台表中的科室数据
  loadStaffList()
})
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
  overflow: hidden;
}

.staff-tabs {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.staff-tabs :deep(.el-tabs__header) {
  margin: 0;
  padding: 0 20px;
  background-color: #f5f7fa;
  border-bottom: 1px solid #e4e7ed;
}

.staff-tabs :deep(.el-tabs__content) {
  flex: 1;
  overflow: auto;
  padding: 20px;
}

.tab-content {
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.search-bar {
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.search-bar .el-input,
.search-bar .el-select {
  min-width: 200px;
}

/* 表格样式优化 */
:deep(.el-table) {
  font-size: 14px;
}

:deep(.el-table th) {
  background-color: #fafafa;
  color: #606266;
  font-weight: 600;
  padding: 12px 0;
}

:deep(.el-table td) {
  padding: 10px 0;
}

:deep(.el-table .cell) {
  padding: 0 10px;
}

/* 分页样式 */
.pagination-container {
  display: flex;
  justify-content: center;
  padding: 16px 0;
  margin-top: auto;
  background-color: #fff;
  border-top: 1px solid #f0f0f0;
}

/* 响应式适配 */
@media screen and (max-width: 1400px) {
  .search-bar .el-input,
  .search-bar .el-select {
    min-width: 180px;
  }
}

@media screen and (max-width: 1200px) {
  .search-bar {
    flex-direction: column;
    align-items: stretch;
  }
  
  .search-bar .el-input,
  .search-bar .el-select,
  .search-bar .el-button {
    width: 100%;
  }
}
</style>
