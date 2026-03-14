<template>
  <div>
    <el-card>
      <template #header>
        <div style="font-weight: bold;">待缴费账单列表</div>
      </template>
      <el-table :data="bills" border stripe>
        <el-table-column prop="billId" label="账单编号" width="180"></el-table-column>
        <el-table-column prop="petName" label="关联宠物" width="120"></el-table-column>
        <el-table-column prop="items" label="消费项目"></el-table-column>
        <el-table-column prop="amount" label="金额" width="120">
          <template #default="scope">
            <span style="color: #F56C6C; font-weight: bold;">￥{{ scope.row.amount }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="scope">
            <el-button type="success" size="small" @click="openPayModal(scope.row)">去缴费</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="bills.length === 0" description="恭喜您，暂无待缴费账单！"></el-empty>
    </el-card>
    
    <!-- 支付弹窗 -->
    <el-dialog v-model="payDialogVisible" title="账单支付" width="400px">
      <div v-if="currentBill">
        <el-descriptions :column="1" border size="small">
          <el-descriptions-item label="账单编号">{{ currentBill.billId }}</el-descriptions-item>
          <el-descriptions-item label="消费项目">{{ currentBill.items }}</el-descriptions-item>
          <el-descriptions-item label="关联宠物">{{ currentBill.petName }}</el-descriptions-item>
        </el-descriptions>
        <div class="bill-total">待支付：￥{{ currentBill.amount }}</div>
        <el-form label-position="top">
          <el-form-item label="选择支付方式">
            <el-radio-group v-model="payType" style="width: 100%; display: flex;">
              <el-radio-button label="alipay" style="flex: 1;">支付宝</el-radio-button>
              <el-radio-button label="wx" style="flex: 1;">微信支付</el-radio-button>
              <el-radio-button label="card" style="flex: 1;">会员卡余额</el-radio-button>
            </el-radio-group>
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="payDialogVisible = false">取消</el-button>
        <el-button type="success" @click="confirmPay">确认支付</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, defineProps, defineEmits } from 'vue'
import { ElMessage } from 'element-plus'

// 定义props
const props = defineProps({
  bills: {
    type: Array,
    required: true
  }
})

// 定义emits
const emit = defineEmits(['pay-bill'])

// 缴费相关
const payDialogVisible = ref(false)
const currentBill = ref(null)
const payType = ref('alipay')

// 打开支付弹窗
const openPayModal = (bill) => {
  currentBill.value = bill
  payDialogVisible.value = true
}

// 确认支付
const confirmPay = () => {
  // 模拟支付成功
  emit('pay-bill', currentBill.value.billId)
  ElMessage.success(`账单【${currentBill.value.billId}】支付成功！`)
  payDialogVisible.value = false
}
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