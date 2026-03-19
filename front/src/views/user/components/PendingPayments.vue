<template>
  <div>
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span style="font-weight: bold;">待缴费账单列表</span>
          <el-button type="primary" link :icon="Refresh" @click="fetchBills">刷新</el-button>
        </div>
      </template>
      <el-table :data="bills" border stripe v-loading="loading" empty-text="暂无待缴费账单">
        <el-table-column label="账单编号" width="180" align="center">
          <template #default="scope">
            <span>BIL-{{ scope.row.id }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="petName" label="关联宠物" width="120" align="center">
          <template #default="scope">
            <span>{{ scope.row.petName || '未知' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="消费项目" show-overflow-tooltip>
          <template #default="scope">
            <span>{{ scope.row.description || '无' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="金额" width="120" align="center">
          <template #default="scope">
            <span style="color: #F56C6C; font-weight: bold;">￥{{ scope.row.amount }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" align="center">
          <template #default="scope">
            <el-button type="success" size="small" @click="openPayModal(scope.row)">去缴费</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="bills.length === 0 && !loading" description="恭喜您，暂无待缴费账单！"></el-empty>
    </el-card>
    
    <!-- 支付弹窗 -->
    <el-dialog v-model="payDialogVisible" title="账单支付" width="400px">
      <div v-if="currentBill">
        <el-descriptions :column="1" border size="small">
          <el-descriptions-item label="账单编号">BIL-{{ currentBill.id }}</el-descriptions-item>
          <el-descriptions-item label="消费项目">{{ currentBill.description || '无' }}</el-descriptions-item>
          <el-descriptions-item label="关联宠物">{{ currentBill.petName || '未知' }}</el-descriptions-item>
        </el-descriptions>
        <div class="bill-total">待支付：￥{{ currentBill.amount }}</div>
        <el-form label-position="top">
          <el-form-item label="选择支付方式">
            <el-radio-group v-model="payType" style="width: 100%; display: flex;">
              <el-radio-button label="alipay" style="flex: 1;">支付宝</el-radio-button>
              <el-radio-button label="wx" style="flex: 1;">微信支付</el-radio-button>
              <el-radio-button label="cash" style="flex: 1;">现金支付</el-radio-button>
            </el-radio-group>
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="payDialogVisible = false">取消</el-button>
        <el-button type="success" @click="confirmPay" :loading="payLoading">确认支付</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import { getPendingBillingsByUserId, payBilling } from '@/services/api'

// 定义emits
const emit = defineEmits(['pay-bill'])

// 加载状态
const loading = ref(false)
const payLoading = ref(false)

// 账单数据
const bills = ref([])

// 缴费相关
const payDialogVisible = ref(false)
const currentBill = ref(null)
const payType = ref('alipay')

// 获取待缴费账单
const fetchBills = async () => {
  try {
    loading.value = true
    const userId = localStorage.getItem('userId')
    if (!userId) {
      ElMessage.warning('请先登录')
      return
    }
    const res = await getPendingBillingsByUserId(userId)
    bills.value = res.data || []
  } catch (error) {
    console.error('获取待缴费账单失败:', error)
    ElMessage.error('获取待缴费账单失败')
  } finally {
    loading.value = false
  }
}

// 打开支付弹窗
const openPayModal = (bill) => {
  currentBill.value = bill
  payDialogVisible.value = true
}

// 确认支付
const confirmPay = async () => {
  try {
    payLoading.value = true
    const paymentMethodMap = {
      'alipay': '支付宝',
      'wx': '微信支付',
      'cash': '现金支付'
    }
    await payBilling(currentBill.value.id, paymentMethodMap[payType.value])
    ElMessage.success(`账单【BIL-${currentBill.value.id}】支付成功！`)
    payDialogVisible.value = false
    bills.value = bills.value.filter(b => b.id !== currentBill.value.id)
    emit('pay-bill', currentBill.value.id)
  } catch (error) {
    console.error('支付失败:', error)
    ElMessage.error('支付失败')
  } finally {
    payLoading.value = false
  }
}

// 初始化
onMounted(() => {
  fetchBills()
})
</script>

<style scoped>
.bill-total {
  font-size: 30px;
  color: #F56C6C;
  font-weight: bold;
  text-align: center;
  margin: 20px 0;
}
</style>
