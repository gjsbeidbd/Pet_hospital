<template>
  <div class="billing-wrapper">
    <el-card class="billing-card">
      <template #header>
        <div style="display: flex; justify-content: space-between;">
          <span>待缴费列表</span>
          <el-button type="primary" link :icon="Refresh">刷新</el-button>
        </div>
      </template>
      
      <el-table :data="paginatedBillingList" stripe style="width: 100%">
        <el-table-column prop="id" label="单据号" width="120"></el-table-column>
        <el-table-column prop="user" label="客户" width="120"></el-table-column>
        <el-table-column prop="pet" label="宠物" width="100"></el-table-column>
        <el-table-column prop="items" label="主要消费项目" show-overflow-tooltip></el-table-column>
        <el-table-column prop="amount" label="应收金额">
          <template #default="scope">
            <span style="color: red; font-weight: bold;">￥{{ scope.row.amount }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="scope">
            <el-button link type="primary" size="small" @click="openBillDetails(scope.row)">明细</el-button>
            <el-button type="primary" size="small" @click="openPayModal(scope.row)">结算</el-button>
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

    <!-- 弹窗：收银台 -->
    <el-dialog v-model="payDialogVisible" title="收银台" width="400px" destroy-on-close>
      <div v-if="currentOrder">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="客户">{{ currentOrder.user }}</el-descriptions-item>
          <el-descriptions-item label="项目明细">{{ currentOrder.items }}</el-descriptions-item>
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
        <el-button type="primary" @click="handlePay">确认收款</el-button>
      </template>
    </el-dialog>

    <!-- 弹窗：账单明细 -->
    <el-dialog v-model="billDetailDialogVisible" :title="`账单明细: ${currentBillDetails.id}`" width="500px">
      <div style="font-size: 14px; margin-bottom: 10px;">
        客户: <strong>{{ currentBillDetails.user }}</strong> | 宠物: <strong>{{ currentBillDetails.pet }}</strong>
      </div>
      <el-table :data="currentBillDetails.details" border stripe max-height="300">
        <el-table-column prop="item" label="项目名称"></el-table-column>
        <el-table-column prop="quantity" label="数量" width="80"></el-table-column>
        <el-table-column prop="unitPrice" label="单价(元)" width="100"></el-table-column>
        <el-table-column prop="total" label="小计(元)" width="100">
          <template #default="scope">
            <span style="color: #F56C6C;">{{ scope.row.total }}</span>
          </template>
        </el-table-column>
      </el-table>
      <div style="text-align: right; margin-top: 15px; font-size: 16px;">
        <strong>总金额：<span class="total-price" style="font-size: 20px;">￥{{ currentBillDetails.amount }}</span></strong>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'

const emit = defineEmits(['pay-bill'])

// 分页相关数据
const currentPage = ref(1)
const pageSize = ref(10)

// 收银相关
const payDialogVisible = ref(false)
const currentOrder = ref(null)
const payType = ref('wx')
const billDetailDialogVisible = ref(false)
const currentBillDetails = ref({})

// 模拟数据：待缴费列表 (增加 details 字段)
const billingList = ref([
  {
    id: 'ORD2023102401',
    user: '赵雷',
    pet: '皮皮',
    items: '进口狂犬疫苗, 挂号费',
    amount: '120.00',
    details: [
      { item: '挂号费', quantity: 1, unitPrice: '20.00', total: '20.00' },
      { item: '进口狂犬疫苗', quantity: 1, unitPrice: '100.00', total: '100.00' }
    ]
  },
  {
    id: 'ORD2023102402',
    user: '孙艺',
    pet: '汤圆',
    items: '绝育手术, 术后消炎药',
    amount: '850.00',
    details: [
      { item: '绝育手术(公)', quantity: 1, unitPrice: '800.00', total: '800.00' },
      { item: '术后消炎药', quantity: 2, unitPrice: '25.00', total: '50.00' }
    ]
  },
  {
    id: 'ORD2023102403',
    user: '王五',
    pet: '小白',
    items: '体检套餐, 疫苗接种',
    amount: '320.00',
    details: [
      { item: '基础体检', quantity: 1, unitPrice: '150.00', total: '150.00' },
      { item: '六联疫苗', quantity: 1, unitPrice: '170.00', total: '170.00' }
    ]
  },
  {
    id: 'ORD2023102404',
    user: '李四',
    pet: '花花',
    items: '皮肤病治疗',
    amount: '280.00',
    details: [
      { item: '皮肤病检查', quantity: 1, unitPrice: '80.00', total: '80.00' },
      { item: '外用药膏', quantity: 2, unitPrice: '50.00', total: '100.00' },
      { item: '口服药物', quantity: 1, unitPrice: '100.00', total: '100.00' }
    ]
  },
  {
    id: 'ORD2023102405',
    user: '张三',
    pet: '大黄',
    items: '骨折手术',
    amount: '1200.00',
    details: [
      { item: 'X光检查', quantity: 1, unitPrice: '200.00', total: '200.00' },
      { item: '手术费用', quantity: 1, unitPrice: '800.00', total: '800.00' },
      { item: '住院护理', quantity: 2, unitPrice: '100.00', total: '200.00' }
    ]
  },
  {
    id: 'ORD2023102406',
    user: '陈六',
    pet: '咪咪',
    items: '牙齿清洁',
    amount: '150.00',
    details: [
      { item: '牙齿清洁', quantity: 1, unitPrice: '150.00', total: '150.00' }
    ]
  }
])

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

// 方法
// 缴费 - 打开支付弹窗
const openPayModal = (order) => {
  currentOrder.value = order
  payDialogVisible.value = true
}

// 缴费 - 确认支付
const handlePay = () => {
  payDialogVisible.value = false
  billingList.value = billingList.value.filter(item => item.id !== currentOrder.value.id)
  ElMessage.success('收款成功！')
  emit('pay-bill', currentOrder.value.id)
  
  // 如果当前页没有数据了，回到上一页
  const totalPages = Math.ceil(billingList.value.length / pageSize.value)
  if (currentPage.value > totalPages && totalPages > 0) {
    currentPage.value = totalPages
  }
}

// 缴费 - 打开明细弹窗
const openBillDetails = (order) => {
  currentBillDetails.value = order
  billDetailDialogVisible.value = true
}
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

/* 支付弹窗总金额 */
.total-price {
  font-size: 28px;
  color: #F56C6C;
  font-weight: bold;
  text-align: center;
  margin: 20px 0;
}
</style>