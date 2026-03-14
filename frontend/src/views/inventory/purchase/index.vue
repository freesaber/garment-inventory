<template>
  <div class="app-container">
    <!-- 采购开单区域 -->
    <el-card shadow="never" style="margin-bottom: 15px">
      <template #header>
        <span>📦 采购入库</span>
      </template>
      
      <el-form :inline="true" style="margin-bottom: 15px">
        <el-form-item label="入库目标">
          <el-select v-model="targetDeptId" placeholder="选择仓库或门店" style="width: 200px" filterable>
            <el-option-group label="仓库">
              <el-option v-for="dept in warehouseOptions" :key="dept.id" :label="dept.label" :value="dept.id" />
            </el-option-group>
            <el-option-group label="门店">
              <el-option v-for="dept in storeOptions" :key="dept.id" :label="dept.label" :value="dept.id" />
            </el-option-group>
          </el-select>
        </el-form-item>
        <el-form-item label="搜索SKU">
          <el-input v-model="scanCode" placeholder="输入SKU编码" style="width: 250px" @keyup.enter="handleScan" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleScan">添加商品</el-button>
          <el-button @click="clearItems">清空</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="purchaseItems" border style="width: 100%">
        <el-table-column label="SKU编码" align="center" prop="skuCode" width="150" />
        <el-table-column label="商品名称" align="center" prop="goodsName" :show-overflow-tooltip="true" />
        <el-table-column label="颜色" align="center" width="100">
          <template #default="scope">{{ getColorLabel(scope.row.color) }}</template>
        </el-table-column>
        <el-table-column label="尺码" align="center" width="80">
          <template #default="scope">{{ getSizeLabel(scope.row.size) }}</template>
        </el-table-column>
        <el-table-column label="进货价" align="center" width="120">
          <template #default="scope">
            <el-input-number v-model="scope.row.price" :min="0" :precision="2" size="small" style="width: 100px" />
          </template>
        </el-table-column>
        <el-table-column label="数量" align="center" width="100">
          <template #default="scope">
            <el-input-number v-model="scope.row.quantity" :min="1" size="small" style="width: 80px" />
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

      <div style="margin-top: 15px; display: flex; justify-content: space-between; align-items: center">
        <div>
          <span style="font-size: 14px; color: #666">共 {{ purchaseItems.length }} 种商品，{{ totalQuantity }} 件</span>
        </div>
        <div style="display: flex; align-items: center; gap: 15px">
          <div style="font-size: 20px">
            合计: <span style="color: #409eff; font-weight: bold">¥{{ totalAmount.toFixed(2) }}</span>
          </div>
          <el-button type="primary" size="large" @click="handleSubmit" :disabled="purchaseItems.length === 0 || !targetDeptId">
            确认入库
          </el-button>
        </div>
      </div>
    </el-card>

    <!-- 采购单列表 -->
    <el-card shadow="never">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span>📋 入库记录</span>
          <el-form :inline="true" size="small">
            <el-form-item>
              <el-select v-model="queryParams.status" placeholder="状态" clearable style="width: 100px">
                <el-option label="待入库" value="0" />
                <el-option label="已入库" value="1" />
                <el-option label="已取消" value="2" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="getList">搜索</el-button>
            </el-form-item>
          </el-form>
        </div>
      </template>
      
      <el-table :data="purchaseList" border>
        <el-table-column label="采购单号" align="center" prop="purchaseNo" width="150" />
        <el-table-column label="入库目标" align="center" prop="deptName" width="120" />
        <el-table-column label="商品数" align="center" width="80">
          <template #default="scope">{{ scope.row.itemList?.length || 0 }}</template>
        </el-table-column>
        <el-table-column label="总金额" align="center" width="120">
          <template #default="scope">¥{{ scope.row.totalAmount }}</template>
        </el-table-column>
        <el-table-column label="状态" align="center" width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.status === '0'" type="warning">待入库</el-tag>
            <el-tag v-else-if="scope.row.status === '1'" type="success">已入库</el-tag>
            <el-tag v-else-if="scope.row.status === '2'" type="info">已取消</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" align="center" prop="createTime" width="160" />
        <el-table-column label="操作" align="center" width="180">
          <template #default="scope">
            <el-button link type="primary" @click="viewDetail(scope.row)">详情</el-button>
            <el-button link type="success" @click="handleConfirm(scope.row)" v-if="scope.row.status === '0'" v-hasPermi="['inventory:purchase:edit']">入库</el-button>
            <el-button link type="warning" @click="handleCancel(scope.row)" v-if="scope.row.status === '0'" v-hasPermi="['inventory:purchase:edit']">取消</el-button>
            <el-button link type="danger" @click="handleDelete(scope.row)" v-if="scope.row.status !== '1'" v-hasPermi="['inventory:purchase:remove']">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog title="采购单详情" v-model="detailOpen" width="700px">
      <el-descriptions :column="2" border v-if="currentPurchase">
        <el-descriptions-item label="采购单号">{{ currentPurchase.purchaseNo }}</el-descriptions-item>
        <el-descriptions-item label="入库目标">{{ currentPurchase.deptName }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag v-if="currentPurchase.status === '0'" type="warning">待入库</el-tag>
          <el-tag v-else-if="currentPurchase.status === '1'" type="success">已入库</el-tag>
          <el-tag v-else-if="currentPurchase.status === '2'" type="info">已取消</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="总金额">¥{{ currentPurchase.totalAmount }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentPurchase.createTime }}</el-descriptions-item>
      </el-descriptions>
      
      <el-table :data="currentPurchase?.itemList || []" border style="margin-top: 15px">
        <el-table-column label="SKU编码" align="center" prop="skuCode" width="130" />
        <el-table-column label="商品名称" align="center" prop="goodsName" />
        <el-table-column label="颜色" align="center" width="80">
          <template #default="scope">{{ getColorLabel(scope.row.color) }}</template>
        </el-table-column>
        <el-table-column label="尺码" align="center" width="60">
          <template #default="scope">{{ getSizeLabel(scope.row.size) }}</template>
        </el-table-column>
        <el-table-column label="单价" align="center" width="80">
          <template #default="scope">¥{{ scope.row.price }}</template>
        </el-table-column>
        <el-table-column label="数量" align="center" width="60" prop="quantity" />
        <el-table-column label="小计" align="center" width="80">
          <template #default="scope">¥{{ scope.row.amount }}</template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup name="Purchase" lang="ts">
import { listPurchase, getPurchase, createPurchase, confirmPurchase, cancelPurchase, delPurchase } from "@/api/inventory/purchase"
import { listGoods } from "@/api/inventory/goods"
import { getDicts } from "@/api/system/dict/data"
import { listDept } from "@/api/system/dept"

const { proxy } = getCurrentInstance() as ComponentInternalInstance

const scanCode = ref("")
const targetDeptId = ref<number | undefined>()
const purchaseItems = ref<any[]>([])
const purchaseList = ref<any[]>([])
const total = ref(0)
const detailOpen = ref(false)
const currentPurchase = ref<any>(null)

const queryParams = ref({ pageNum: 1, pageSize: 10, status: undefined })

const colorOptions = ref<any[]>([])
const sizeOptions = ref<any[]>([])
const warehouseOptions = ref<any[]>([])
const storeOptions = ref<any[]>([])

const totalAmount = computed(() => purchaseItems.value.reduce((sum, item) => sum + item.price * item.quantity, 0))
const totalQuantity = computed(() => purchaseItems.value.reduce((sum, item) => sum + item.quantity, 0))

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

const loadDepts = async () => {
  try {
    const res = await listDept()
    const depts = res.data || []
    // 根据部门类型分组
    warehouseOptions.value = depts.filter((d: any) => d.deptType === '1').map((d: any) => ({ id: d.deptId, label: d.deptName }))
    storeOptions.value = depts.filter((d: any) => d.deptType === '0').map((d: any) => ({ id: d.deptId, label: d.deptName }))
    
    // 默认选择第一个仓库
    if (warehouseOptions.value.length > 0 && !targetDeptId.value) {
      targetDeptId.value = warehouseOptions.value[0].id
    }
  } catch (e) {
    console.error('加载部门失败', e)
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
        const existItem = purchaseItems.value.find(item => item.skuId === sku.skuId)
        if (existItem) {
          existItem.quantity++
        } else {
          purchaseItems.value.push({
            skuId: sku.skuId,
            skuCode: sku.skuCode,
            goodsName: goods.goodsName,
            color: sku.color,
            size: sku.size,
            price: goods.purchasePrice || 0,
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

const removeItem = (index: number) => {
  purchaseItems.value.splice(index, 1)
}

const clearItems = () => {
  purchaseItems.value = []
}

const handleSubmit = async () => {
  if (purchaseItems.value.length === 0 || !targetDeptId.value) return
  
  try {
    const data = {
      deptId: targetDeptId.value,
      itemList: purchaseItems.value.map(item => ({
        skuId: item.skuId,
        price: item.price,
        quantity: item.quantity
      }))
    }
    
    const res = await createPurchase(data)
    proxy?.$modal.msgSuccess("入库成功：" + res.data?.purchaseNo || "")
    purchaseItems.value = []
    getList()
  } catch (e: any) {
    proxy?.$modal.msgError(e.message || "入库失败")
  }
}

const getList = async () => {
  const res = await listPurchase(queryParams.value)
  purchaseList.value = res.rows || []
  total.value = res.total || 0
}

const viewDetail = async (row: any) => {
  const res = await getPurchase(row.purchaseId)
  currentPurchase.value = res.data
  detailOpen.value = true
}

const handleConfirm = async (row: any) => {
  await proxy?.$modal.confirm('确认入库？入库后库存将增加。')
  await confirmPurchase(row.purchaseId)
  proxy?.$modal.msgSuccess("入库成功")
  getList()
}

const handleCancel = async (row: any) => {
  await proxy?.$modal.confirm('确认取消该采购单？')
  await cancelPurchase(row.purchaseId)
  proxy?.$modal.msgSuccess("已取消")
  getList()
}

const handleDelete = async (row: any) => {
  await proxy?.$modal.confirm('确认删除该采购单？')
  await delPurchase(row.purchaseId)
  proxy?.$modal.msgSuccess("删除成功")
  getList()
}

onMounted(() => {
  loadDicts()
  loadDepts()
  getList()
})
</script>
