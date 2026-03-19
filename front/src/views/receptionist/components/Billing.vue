<template>
  <div class="billing-wrapper">
    <el-card class="billing-card">
      <template #header>
        <div style="display: flex; justify-content: space-between;">
          <span>待缴费列表</span>
          <el-button type="primary" link :icon="Refresh" @click="fetchBillingList">刷新</el-button>
        </div>
      </template>
      
      <el-table :data="paginatedBillingList" stripe style="width: 100%" v-loading="loading" empty-text="暂无待缴费记录">
        <el-table-column prop="id" label="单据号" width="120" align="center">
          <template #default="scope">
            <span>BIL-{{ scope.row.id }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="userName" label="客户" width="120" align="center">
          <template #default="scope">
            <span>{{ scope.row.userName || '未知' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="petName" label="宠物" width="100" align="center">
          <template #default="scope">
            <span>{{ scope.row.petName || '未知' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="主要消费项目" show-overflow-tooltip>
          <template #default="scope">
            <span>{{ scope.row.description || '无' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="应收金额" width="120" align="center">
          <template #default="scope">
            <span style="color: red; font-weight: bold;">￥{{ scope.row.amount }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" align="center">
          <template #default="scope">
            <el-button type="primary" size="small" @click="viewDetail(scope.row)">详情</el-button>
            <el-button type="success" size="small" @click="openPayModal(scope.row)">结算</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页组件 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[5, 10, 20, 50]"
          :small="false"
          :disabled="false"
          :background="true"
          layout="total, sizes, prev, pager, next, jumper"
          :total="billingList.length"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :pager-count="5"
          prev-text="上一页"
          next-text="下一页"
          :page-count="Math.ceil(billingList.length / pageSize)"
          :locale="{
            total: `共 {total} 条记录`,
            pagesize: '条/页',
            goto: '跳至',
            pageClassifier: '页'
          }"
        />
      </div>
    </el-card>

    <!-- 弹窗：账单详情 -->
    <el-dialog v-model="detailDialogVisible" title="账单详情" width="700px" destroy-on-close>
      <div v-if="currentOrder">
        <el-descriptions :column="2" border style="margin-bottom: 20px;">
          <el-descriptions-item label="单据号">BIL-{{ currentOrder.id }}</el-descriptions-item>
          <el-descriptions-item label="客户">{{ currentOrder.userName || '未知' }}</el-descriptions-item>
          <el-descriptions-item label="宠物">{{ currentOrder.petName || '未知' }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ formatDate(currentOrder.createdAt) }}</el-descriptions-item>
        </el-descriptions>
        
        <div style="margin-bottom: 10px; font-weight: bold;">费用明细：</div>
        <el-table :data="billingItems" border style="width: 100%" v-loading="itemsLoading">
          <el-table-column prop="category" label="费用类别" width="100" align="center">
            <template #default="scope">
              <el-tag :type="getCategoryTagType(scope.row.category)">{{ scope.row.category }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="itemName" label="项目名称" show-overflow-tooltip />
          <el-table-column prop="quantity" label="数量" width="80" align="center" />
          <el-table-column prop="unit" label="单位" width="80" align="center">
            <template #default="scope">
              <span>{{ scope.row.unit || '-' }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="unitPrice" label="单价" width="100" align="center">
            <template #default="scope">
              <span>￥{{ scope.row.unitPrice }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="totalPrice" label="小计" width="100" align="center">
            <template #default="scope">
              <span style="color: #F56C6C; font-weight: bold;">￥{{ scope.row.totalPrice }}</span>
            </template>
          </el-table-column>
        </el-table>
        
        <div class="detail-total">
          合计金额：<span class="amount">￥{{ currentOrder.amount }}</span>
        </div>
      </div>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
        <el-button type="success" @click="openPayFromDetail">立即结算</el-button>
      </template>
    </el-dialog>

    <!-- 弹窗：收银台 -->
    <el-dialog v-model="payDialogVisible" title="收银台" width="400px" destroy-on-close>
      <div v-if="currentOrder">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="单据号">BIL-{{ currentOrder.id }}</el-descriptions-item>
          <el-descriptions-item label="客户">{{ currentOrder.userName || '未知' }}</el-descriptions-item>
          <el-descriptions-item label="宠物">{{ currentOrder.petName || '未知' }}</el-descriptions-item>
          <el-descriptions-item label="项目明细">{{ currentOrder.description || '无' }}</el-descriptions-item>
        </el-descriptions>
        <div class="total-price">￥{{ currentOrder.amount }}</div>
        <el-form label-position="top">
          <el-form-item label="支付方式">
            <el-radio-group v-model="payType">
              <el-radio-button label="wx">微信支付</el-radio-button>
              <el-radio-button label="alipay">支付宝</el-radio-button>
              <el-radio-button label="cash">现金支付</el-radio-button>
            </el-radio-group>
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="payDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handlePay" :loading="payLoading">确认收款</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import { getPendingBillings, payBilling, getBillingItems } from '@/services/api'

const emit = defineEmits(['pay-bill'])

// 分页相关数据
const currentPage = ref(1)
const pageSize = ref(10)

// 加载状态
const loading = ref(false)
const payLoading = ref(false)
const itemsLoading = ref(false)

// 收银相关
const payDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const currentOrder = ref(null)
const payType = ref('wx')

// 账单明细
const billingItems = ref([])

// 待缴费列表
const billingList = ref([])

// 获取待缴费列表
const fetchBillingList = async () => {
  try {
    loading.value = true
    const res = await getPendingBillings()
    billingList.value = res.data || []
  } catch (error) {
    console.error('获取待缴费列表失败:', error)
    ElMessage.error('获取待缴费列表失败')
  } finally {
    loading.value = false
  }
}

// 计算当前页的待缴费数据
const paginatedBillingList = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return billingList.value.slice(start, end)
})

// 分页事件处理
const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
}

const handleCurrentChange = (val) => {
  currentPage.value = val
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN')
}

// 获取费用类别标签类型
const getCategoryTagType = (category) => {
  const typeMap = {
    '挂号费': 'primary',
    '检查费': 'success',
    '手术费': 'danger',
    '药品费': 'warning',
    '治疗费': 'info',
    '护理费': '',
    '其他': ''
  }
  return typeMap[category] || ''
}

// 查看详情
const viewDetail = async (order) => {
  currentOrder.value = order
  detailDialogVisible.value = true
  
  // 获取账单明细
  try {
    itemsLoading.value = true
    const res = await getBillingItems(order.id)
    billingItems.value = res.data || []
  } catch (error) {
    console.error('获取账单明细失败:', error)
    ElMessage.error('获取账单明细失败')
    billingItems.value = []
  } finally {
    itemsLoading.value = false
  }
}

// 从详情弹窗打开支付
const openPayFromDetail = () => {
  detailDialogVisible.value = false
  payDialogVisible.value = true
}

// 缴费 - 打开支付弹窗
const openPayModal = (order) => {
  currentOrder.value = order
  payDialogVisible.value = true
}

// 缴费 - 确认支付
const handlePay = async () => {
  try {
    payLoading.value = true
    const paymentMethodMap = {
      'wx': '微信支付',
      'alipay': '支付宝',
      'cash': '现金支付'
    }
    await payBilling(currentOrder.value.id, paymentMethodMap[payType.value])
    payDialogVisible.value = false
    billingList.value = billingList.value.filter(item => item.id !== currentOrder.value.id)
    ElMessage.success('收款成功！')
    emit('pay-bill', currentOrder.value.id)
    
    // 如果当前页没有数据了，回到上一页
    const totalPages = Math.ceil(billingList.value.length / pageSize.value)
    if (currentPage.value > totalPages && totalPages > 0) {
      currentPage.value = totalPages
    }
  } catch (error) {
    console.error('支付失败:', error)
    ElMessage.error('支付失败')
  } finally {
    payLoading.value = false
  }
}

// 初始化
onMounted(() => {
  fetchBillingList()
})
</script>

<style scoped>
.billing-wrapper {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.billing-card {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.billing-card :deep(.el-card__body) {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.billing-card :deep(.el-table) {
  flex: 1;
}

.pagination-container {
  display: flex;
  justify-content: center;
  padding: 20px 0;
}

.total-price {
  font-size: 28px;
  color: #F56C6C;
  font-weight: bold;
  text-align: center;
  margin: 20px 0;
}

.detail-total {
  text-align: right;
  padding: 15px 0;
  font-size: 16px;
  border-top: 1px solid #ebeef5;
  margin-top: 15px;
}

.detail-total .amount {
  font-size: 24px;
  color: #F56C6C;
  font-weight: bold;
}
</style>
