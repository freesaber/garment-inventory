<template>
  <div class="cashier-container">
    <!-- 左侧：商品列表 -->
    <div class="cashier-left">
      <div class="scan-area">
        <el-input
          ref="scanInput"
          v-model="scanCode"
          placeholder="扫描条码或输入SKU"
          size="large"
          @keyup.enter="handleScan"
          @keyup.f1="handleCheckout"
          clearable
          autofocus
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>
      
      <div class="cart-header">
        <span>🛒 购物车 ({{ cartItems.length }}种/{{ totalQuantity }}件)</span>
        <el-button text type="danger" @click="clearCart" :disabled="cartItems.length === 0">清空</el-button>
      </div>
      
      <div class="cart-list">
        <div v-if="cartItems.length === 0" class="cart-empty">
          <el-icon size="60" color="#ccc"><ShoppingCart /></el-icon>
          <p>购物车为空</p>
          <p class="tips">扫描商品条码开始销售</p>
        </div>
        <div v-for="(item, index) in cartItems" :key="item.skuId" class="cart-item">
          <div class="item-info">
            <div class="item-name">{{ item.goodsName }}</div>
            <div class="item-spec">{{ getColorLabel(item.color) }} / {{ getSizeLabel(item.size) }}</div>
            <div class="item-sku">{{ item.skuCode }}</div>
          </div>
          <div class="item-price">¥{{ item.price.toFixed(2) }}</div>
          <div class="item-qty">
            <el-button-group>
              <el-button size="small" @click="decreaseQty(index)" :disabled="item.quantity <= 1">-</el-button>
              <el-button size="small" disabled>{{ item.quantity }}</el-button>
              <el-button size="small" @click="increaseQty(index)">+</el-button>
            </el-button-group>
          </div>
          <div class="item-subtotal">¥{{ (item.price * item.quantity).toFixed(2) }}</div>
          <el-button class="item-remove" text type="danger" @click="removeItem(index)">
            <el-icon><Delete /></el-icon>
          </el-button>
        </div>
      </div>
    </div>
    
    <!-- 右侧：结算区域 -->
    <div class="cashier-right">
      <div class="settlement-header">
        <span>💰 结算</span>
      </div>
      
      <div class="settlement-body">
        <div class="amount-row">
          <span>商品数量</span>
          <span>{{ totalQuantity }} 件</span>
        </div>
        <div class="amount-row">
          <span>商品金额</span>
          <span>¥{{ totalAmount.toFixed(2) }}</span>
        </div>
        <div class="amount-row discount">
          <span>优惠金额</span>
          <div class="discount-input">
            <el-input-number v-model="discountAmount" :min="0" :max="totalAmount" :precision="2" size="small" controls-position="right" />
          </div>
        </div>
        <div class="amount-row total">
          <span>应收金额</span>
          <span class="total-amount">¥{{ payAmount.toFixed(2) }}</span>
        </div>
        
        <div class="pay-section">
          <div class="pay-label">支付方式</div>
          <div class="pay-methods">
            <div 
              v-for="method in payMethods" 
              :key="method.value" 
              class="pay-method"
              :class="{ active: payType === method.value }"
              @click="payType = method.value"
            >
              <el-icon :size="24"><component :is="method.icon" /></el-icon>
              <span>{{ method.label }}</span>
            </div>
          </div>
        </div>
        
        <div class="cash-section" v-if="payType === 'cash'">
          <div class="cash-label">收款金额</div>
          <div class="cash-input">
            <el-input 
              v-model="receivedAmount" 
              size="large" 
              placeholder="输入收款金额"
              @input="calcChange"
            >
              <template #prefix>¥</template>
            </el-input>
          </div>
          <div class="quick-amount">
            <el-button v-for="amt in quickAmounts" :key="amt" size="small" @click="setReceivedAmount(amt)">
              ¥{{ amt }}
            </el-button>
          </div>
          <div class="change-row" v-if="changeAmount > 0">
            <span>找零</span>
            <span class="change-amount">¥{{ changeAmount.toFixed(2) }}</span>
          </div>
        </div>
      </div>
      
      <div class="settlement-footer">
        <el-button size="large" @click="clearCart" :disabled="cartItems.length === 0">取消 (Esc)</el-button>
        <el-button type="primary" size="large" @click="handleCheckout" :disabled="cartItems.length === 0">
          结算 (F1)
        </el-button>
      </div>
    </div>
    
    <!-- 结算成功弹窗 -->
    <el-dialog 
      v-model="successVisible" 
      title="结算成功" 
      width="400px" 
      center
      :close-on-click-modal="false"
    >
      <div class="success-content">
        <el-icon :size="60" color="#67c23a"><CircleCheckFilled /></el-icon>
        <div class="success-amount">¥{{ lastPayAmount.toFixed(2) }}</div>
        <div class="success-change" v-if="lastChangeAmount > 0">
          找零: ¥{{ lastChangeAmount.toFixed(2) }}
        </div>
        <div class="success-no">{{ lastSaleNo }}</div>
      </div>
      <template #footer>
        <el-button type="primary" @click="closeSuccess">确定 (Enter)</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, ShoppingCart, Delete, CircleCheckFilled, Money, Cellphone, CreditCard } from '@element-plus/icons-vue'
import { listGoods } from "@/api/inventory/goods"
import { createSale } from "@/api/inventory/sale"
import { getDicts } from "@/api/system/dict/data"

const scanInput = ref()
const scanCode = ref("")
const cartItems = ref<any[]>([])
const discountAmount = ref(0)
const payType = ref('cash')
const receivedAmount = ref<number | string>('')
const changeAmount = ref(0)
const successVisible = ref(false)
const lastPayAmount = ref(0)
const lastChangeAmount = ref(0)
const lastSaleNo = ref('')

const colorOptions = ref<any[]>([])
const sizeOptions = ref<any[]>([])

const payMethods = [
  { value: 'cash', label: '现金', icon: Money },
  { value: 'wechat', label: '微信', icon: Cellphone },
  { value: 'alipay', label: '支付宝', icon: CreditCard },
]

const quickAmounts = [50, 100, 200, 300, 500, 1000]

const totalQuantity = computed(() => cartItems.value.reduce((sum, item) => sum + item.quantity, 0))
const totalAmount = computed(() => cartItems.value.reduce((sum, item) => sum + item.price * item.quantity, 0))
const payAmount = computed(() => Math.max(0, totalAmount.value - discountAmount.value))

const getColorLabel = (value: string) => {
  if (!value) return '-'
  const item = colorOptions.value.find(d => d.value === value)
  return item ? item.label : value
}

const getSizeLabel = (value: string) => {
  if (!value) return '-'
  const item = sizeOptions.value.find(d => d.value === value)
  return item ? item.label : value
}

const loadDicts = async () => {
  try {
    const colorRes = await getDicts('gi_color')
    colorOptions.value = (colorRes.data || []).map((item: any) => ({ label: item.dictLabel, value: item.dictValue }))
    const sizeRes = await getDicts('gi_size')
    sizeOptions.value = (sizeRes.data || []).map((item: any) => ({ label: item.dictLabel, value: item.dictValue }))
  } catch (e) {
    console.error('加载字典失败', e)
  }
}

const handleScan = async () => {
  if (!scanCode.value) return
  
  try {
    const res = await listGoods({ skuCode: scanCode.value, pageSize: 1 })
    if (res.rows && res.rows.length > 0) {
      const goods = res.rows[0]
      const sku = goods.skuList?.find((s: any) => s.skuCode === scanCode.value)
      if (sku) {
        const existItem = cartItems.value.find(item => item.skuId === sku.skuId)
        if (existItem) {
          existItem.quantity++
        } else {
          cartItems.value.push({
            skuId: sku.skuId,
            skuCode: sku.skuCode,
            goodsName: goods.goodsName,
            color: sku.color,
            size: sku.size,
            price: goods.salePrice || 0,
            quantity: 1
          })
        }
        scanCode.value = ""
        // 播放提示音（可选）
        // new Audio('/scan.mp3').play()
      } else {
        ElMessage.warning("未找到匹配的SKU")
      }
    } else {
      ElMessage.warning("未找到商品")
    }
  } catch (e) {
    console.error(e)
  }
  
  // 重新聚焦到输入框
  nextTick(() => {
    scanInput.value?.focus()
  })
}

const increaseQty = (index: number) => {
  cartItems.value[index].quantity++
}

const decreaseQty = (index: number) => {
  if (cartItems.value[index].quantity > 1) {
    cartItems.value[index].quantity--
  }
}

const removeItem = (index: number) => {
  cartItems.value.splice(index, 1)
}

const clearCart = () => {
  cartItems.value = []
  discountAmount.value = 0
  receivedAmount.value = ''
  changeAmount.value = 0
}

const calcChange = () => {
  const received = parseFloat(String(receivedAmount.value)) || 0
  changeAmount.value = Math.max(0, received - payAmount.value)
}

const setReceivedAmount = (amt: number) => {
  receivedAmount.value = amt
  calcChange()
}

const handleCheckout = async () => {
  if (cartItems.value.length === 0) return
  
  // 现金支付时检查收款金额
  if (payType.value === 'cash') {
    const received = parseFloat(String(receivedAmount.value)) || 0
    if (received < payAmount.value) {
      ElMessage.error("收款金额不足")
      return
    }
  }
  
  try {
    const data = {
      itemList: cartItems.value.map(item => ({
        skuId: item.skuId,
        price: item.price,
        quantity: item.quantity
      })),
      payType: payType.value,
      payAmount: payAmount.value,
      discountAmount: discountAmount.value,
      totalAmount: totalAmount.value
    }
    
    const res = await createSale(data)
    
    // 显示成功弹窗
    lastPayAmount.value = payAmount.value
    lastChangeAmount.value = changeAmount.value
    lastSaleNo.value = res.data?.saleNo || ''
    successVisible.value = true
    
    // 清空购物车
    clearCart()
  } catch (e: any) {
    ElMessage.error(e.message || "结算失败")
  }
}

const closeSuccess = () => {
  successVisible.value = false
  nextTick(() => {
    scanInput.value?.focus()
  })
}

// 键盘快捷键
const handleKeydown = (e: KeyboardEvent) => {
  if (e.key === 'F1') {
    e.preventDefault()
    handleCheckout()
  } else if (e.key === 'Escape') {
    if (successVisible.value) {
      closeSuccess()
    } else {
      clearCart()
    }
  } else if (e.key === 'Enter' && successVisible.value) {
    closeSuccess()
  }
}

onMounted(() => {
  loadDicts()
  window.addEventListener('keydown', handleKeydown)
  nextTick(() => {
    scanInput.value?.focus()
  })
})

onUnmounted(() => {
  window.removeEventListener('keydown', handleKeydown)
})
</script>

<style scoped>
.cashier-container {
  display: flex;
  height: calc(100vh - 84px);
  background: #f5f7fa;
  gap: 15px;
  padding: 15px;
}

.cashier-left {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
}

.scan-area {
  padding: 15px;
  border-bottom: 1px solid #eee;
}

.scan-area :deep(.el-input__wrapper) {
  padding: 12px 15px;
  font-size: 16px;
}

.cart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 15px;
  background: #fafafa;
  border-bottom: 1px solid #eee;
  font-weight: bold;
}

.cart-list {
  flex: 1;
  overflow-y: auto;
  padding: 10px;
}

.cart-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #999;
}

.cart-empty .tips {
  font-size: 12px;
  margin-top: 10px;
}

.cart-item {
  display: flex;
  align-items: center;
  padding: 12px;
  border-bottom: 1px solid #f0f0f0;
  transition: background 0.2s;
}

.cart-item:hover {
  background: #fafafa;
}

.item-info {
  flex: 1;
  min-width: 0;
}

.item-name {
  font-size: 14px;
  font-weight: 500;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.item-spec {
  font-size: 12px;
  color: #999;
  margin-top: 2px;
}

.item-sku {
  font-size: 11px;
  color: #ccc;
  margin-top: 2px;
}

.item-price {
  width: 80px;
  text-align: right;
  font-size: 14px;
  color: #666;
}

.item-qty {
  width: 120px;
  text-align: center;
  margin: 0 10px;
}

.item-subtotal {
  width: 80px;
  text-align: right;
  font-size: 14px;
  font-weight: 500;
  color: #f56c6c;
}

.item-remove {
  margin-left: 10px;
}

.cashier-right {
  width: 360px;
  display: flex;
  flex-direction: column;
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
}

.settlement-header {
  padding: 15px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  font-size: 18px;
  font-weight: bold;
}

.settlement-body {
  flex: 1;
  padding: 15px;
  overflow-y: auto;
}

.amount-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
  font-size: 14px;
}

.amount-row.discount {
  color: #e6a23c;
}

.discount-input {
  width: 120px;
}

.amount-row.total {
  font-size: 18px;
  font-weight: bold;
  border-bottom: none;
  padding: 15px 0;
}

.total-amount {
  font-size: 28px;
  color: #f56c6c;
}

.pay-section {
  margin-top: 15px;
}

.pay-label {
  font-size: 14px;
  color: #666;
  margin-bottom: 10px;
}

.pay-methods {
  display: flex;
  gap: 10px;
}

.pay-method {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 15px 10px;
  border: 2px solid #e4e7ed;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.pay-method:hover {
  border-color: #409eff;
}

.pay-method.active {
  border-color: #409eff;
  background: #ecf5ff;
}

.pay-method span {
  margin-top: 5px;
  font-size: 12px;
}

.cash-section {
  margin-top: 20px;
  padding-top: 15px;
  border-top: 1px solid #f0f0f0;
}

.cash-label {
  font-size: 14px;
  color: #666;
  margin-bottom: 10px;
}

.cash-input :deep(.el-input__wrapper) {
  padding: 10px 15px;
}

.cash-input :deep(.el-input__inner) {
  font-size: 24px;
  font-weight: bold;
}

.quick-amount {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 10px;
}

.quick-amount .el-button {
  flex: 1;
  min-width: calc(33% - 6px);
}

.change-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 15px;
  padding: 15px;
  background: #fdf6ec;
  border-radius: 8px;
  font-size: 16px;
}

.change-amount {
  font-size: 24px;
  font-weight: bold;
  color: #e6a23c;
}

.settlement-footer {
  display: flex;
  gap: 10px;
  padding: 15px;
  border-top: 1px solid #eee;
}

.settlement-footer .el-button {
  flex: 1;
  height: 50px;
  font-size: 16px;
}

.success-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
}

.success-amount {
  font-size: 36px;
  font-weight: bold;
  color: #67c23a;
  margin-top: 15px;
}

.success-change {
  font-size: 18px;
  color: #e6a23c;
  margin-top: 10px;
}

.success-no {
  font-size: 14px;
  color: #999;
  margin-top: 15px;
}
</style>
