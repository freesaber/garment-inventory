<template>
  <div class="app-container">
    <!-- 销售开单区域 -->
    <el-card shadow="never" style="margin-bottom: 15px">
      <template #header>
        <span>🛒 销售开单</span>
      </template>
      
      <!-- 商品扫描/搜索 -->
      <el-form :inline="true">
        <el-form-item label="扫描/搜索SKU">
          <el-input 
            ref="scanInput"
            v-model="scanCode" 
            placeholder="扫描条码或输入SKU编码" 
            style="width: 300px"
            @keyup.enter="handleScan"
            clearable
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleScan">添加商品</el-button>
          <el-button @click="clearCart">清空</el-button>
        </el-form-item>
      </el-form>

      <!-- 购物车列表 -->
      <el-table :data="cartItems" border style="width: 100%">
        <el-table-column label="SKU编码" align="center" prop="skuCode" width="150" />
        <el-table-column label="商品名称" align="center" prop="goodsName" :show-overflow-tooltip="true" />
        <el-table-column label="颜色" align="center" width="100">
          <template #default="scope">{{ getColorLabel(scope.row.color) }}</template>
        </el-table-column>
        <el-table-column label="尺码" align="center" width="80">
          <template #default="scope">{{ getSizeLabel(scope.row.size) }}</template>
        </el-table-column>
        <el-table-column label="单价" align="center" width="100">
          <template #default="scope">
            <el-input-number v-model="scope.row.price" :min="0" :precision="2" size="small" style="width: 80px" @change="calcTotal" />
          </template>
        </el-table-column>
        <el-table-column label="数量" align="center" width="100">
          <template #default="scope">
            <el-input-number v-model="scope.row.quantity" :min="1" size="small" style="width: 80px" @change="calcTotal" />
          </template>
        </el-table-column>
        <el-table-column label="小计" align="center" width="100">
          <template #default="scope">¥{{ (scope.row.price * scope.row.quantity).toFixed(2) }}</template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="80">
          <template #default="scope">
            <el-button link type="danger" @click="removeItem(scope.$index)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 结算区域 -->
      <div style="margin-top: 15px; display: flex; justify-content: space-between; align-items: center">
        <div>
          <span style="font-size: 14px; color: #666">共 {{ cartItems.length }} 种商品，{{ totalQuantity }} 件</span>
        </div>
        <div style="display: flex; align-items: center; gap: 15px">
          <div style="font-size: 20px">
            合计: <span style="color: #f56c6c; font-weight: bold">¥{{ totalAmount.toFixed(2) }}</span>
          </div>
          <div>
            <span style="margin-right: 10px">支付方式:</span>
            <el-select v-model="payType" style="width: 120px">
              <el-option label="现金" value="cash" />
              <el-option label="微信" value="wechat" />
              <el-option label="支付宝" value="alipay" />
            </el-select>
          </div>
          <el-button type="primary" size="large" @click="handleCheckout" :disabled="cartItems.length === 0">
            结算 (F1)
          </el-button>
        </div>
      </div>
    </el-card>

    <!-- 销售记录 -->
    <el-card shadow="never">
      <template #header>
        <span>📋 今日销售记录</span>
      </template>
      
      <el-table :data="saleList" border>
        <el-table-column label="销售单号" align="center" prop="saleNo" width="150" />
        <el-table-column label="商品数" align="center" width="80">
          <template #default="scope">{{ scope.row.itemList?.length || 0 }}</template>
        </el-table-column>
        <el-table-column label="总金额" align="center" width="100">
          <template #default="scope">¥{{ scope.row.totalAmount }}</template>
        </el-table-column>
        <el-table-column label="实付金额" align="center" width="100">
          <template #default="scope">¥{{ scope.row.payAmount }}</template>
        </el-table-column>
        <el-table-column label="支付方式" align="center" width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.payType === 'cash'" type="success">现金</el-tag>
            <el-tag v-else-if="scope.row.payType === 'wechat'" type="primary">微信</el-tag>
            <el-tag v-else-if="scope.row.payType === 'alipay'" type="info">支付宝</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="时间" align="center" prop="createTime" width="160" />
        <el-table-column label="操作" align="center" width="120">
          <template #default="scope">
            <el-button link type="primary" @click="viewSale(scope.row)">详情</el-button>
            <el-button link type="danger" @click="handleDelete(scope.row)" v-hasPermi="['inventory:sale:remove']">作废</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 销售单详情对话框 -->
    <el-dialog title="销售单详情" v-model="detailOpen" width="600px">
      <el-descriptions :column="2" border v-if="currentSale">
        <el-descriptions-item label="销售单号">{{ currentSale.saleNo }}</el-descriptions-item>
        <el-descriptions-item label="支付方式">
          <el-tag v-if="currentSale.payType === 'cash'" type="success">现金</el-tag>
          <el-tag v-else-if="currentSale.payType === 'wechat'" type="primary">微信</el-tag>
          <el-tag v-else-if="currentSale.payType === 'alipay'" type="info">支付宝</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="总金额">¥{{ currentSale.totalAmount }}</el-descriptions-item>
        <el-descriptions-item label="实付金额">¥{{ currentSale.payAmount }}</el-descriptions-item>
        <el-descriptions-item label="时间" :span="2">{{ currentSale.createTime }}</el-descriptions-item>
      </el-descriptions>
      
      <el-table :data="currentSale?.itemList || []" border style="margin-top: 15px">
        <el-table-column label="SKU编码" align="center" prop="skuCode" width="130" />
        <el-table-column label="商品名称" align="center" prop="goodsName" />
        <el-table-column label="颜色" align="center" width="80">
          <template #default="scope">{{ getColorLabel(scope.row.color) }}</template>
        </el-table-column>
        <el-table-column label="尺码" align="center" width="60">
          <template #default="scope">{{ getSizeLabel(scope.row.size) }}</template>
        </el-table-column>
        <el-table-column label="单价" align="center" width="80">¥{{ scope.row.price }}</el-table-column>
        <el-table-column label="数量" align="center" width="60" prop="quantity" />
        <el-table-column label="小计" align="center" width="80">¥{{ scope.row.amount }}</el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup name="Sale" lang="ts">
import { listSale, getSale, createSale, delSale } from "@/api/inventory/sale"
import { listGoods } from "@/api/inventory/goods"
import { getDicts } from "@/api/system/dict/data"

const { proxy } = getCurrentInstance() as ComponentInternalInstance

const scanCode = ref("")
const cartItems = ref<any[]>([])
const saleList = ref<any[]>([])
const payType = ref("cash")
const detailOpen = ref(false)
const currentSale = ref<any>(null)

const colorOptions = ref<any[]>([])
const sizeOptions = ref<any[]>([])

// 计算属性
const totalAmount = computed(() => cartItems.value.reduce((sum, item) => sum + item.price * item.quantity, 0))
const totalQuantity = computed(() => cartItems.value.reduce((sum, item) => sum + item.quantity, 0))

// 获取颜色名称
const getColorLabel = (value: string) => {
  if (!value) return '-'
  const item = colorOptions.value.find(d => d.value === value)
  return item ? item.label : value
}

// 获取尺码名称
const getSizeLabel = (value: string) => {
  if (!value) return '-'
  const item = sizeOptions.value.find(d => d.value === value)
  return item ? item.label : value
}

// 加载字典
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

// 扫描/搜索商品
const handleScan = async () => {
  if (!scanCode.value) return
  
  try {
    // 搜索SKU
    const res = await listGoods({ skuCode: scanCode.value, pageSize: 1 })
    if (res.rows && res.rows.length > 0) {
      const goods = res.rows[0]
      // 查找匹配的SKU
      const sku = goods.skuList?.find((s: any) => s.skuCode === scanCode.value || s.barcode === scanCode.value)
      if (sku) {
        // 检查购物车是否已有该SKU
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
            price: sku.salePrice || goods.salePrice,
            quantity: 1
          })
        }
        scanCode.value = ""
      } else {
        proxy?.$modal.msgWarning("未找到匹配的SKU")
      }
    } else {
      proxy?.$modal.msgWarning("未找到商品")
    }
  } catch (e) {
    console.error(e)
  }
}

// 移除商品
const removeItem = (index: number) => {
  cartItems.value.splice(index, 1)
}

// 清空购物车
const clearCart = () => {
  cartItems.value = []
}

// 计算合计
const calcTotal = () => {
  // 触发重新计算
}

// 结算
const handleCheckout = async () => {
  if (cartItems.value.length === 0) return
  
  try {
    const saleData = {
      payType: payType.value,
      itemList: cartItems.value.map(item => ({
        skuId: item.skuId,
        price: item.price,
        quantity: item.quantity
      }))
    }
    
    const res = await createSale(saleData)
    proxy?.$modal.msgSuccess("开单成功！销售单号：" + res.data?.saleNo || "")
    
    // 清空购物车
    cartItems.value = []
    
    // 刷新销售记录
    loadSaleList()
  } catch (e: any) {
    proxy?.$modal.msgError(e.message || "开单失败")
  }
}

// 加载今日销售记录
const loadSaleList = async () => {
  const today = new Date().toISOString().slice(0, 10)
  const res = await listSale({ createTime: today })
  saleList.value = res.rows || []
}

// 查看销售单详情
const viewSale = async (row: any) => {
  const res = await getSale(row.saleId)
  currentSale.value = res.data
  detailOpen.value = true
}

// 作废销售单
const handleDelete = async (row: any) => {
  await proxy?.$modal.confirm('确认要作废该销售单吗？')
  await delSale(row.saleId)
  proxy?.$modal.msgSuccess("作废成功")
  loadSaleList()
}

// 快捷键 F1 结算
const handleKeydown = (e: KeyboardEvent) => {
  if (e.key === 'F1') {
    e.preventDefault()
    handleCheckout()
  }
}

onMounted(() => {
  loadDicts()
  loadSaleList()
  window.addEventListener('keydown', handleKeydown)
})

onUnmounted(() => {
  window.removeEventListener('keydown', handleKeydown)
})
</script>
