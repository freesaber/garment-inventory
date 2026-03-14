<template>
  <div class="app-container">
    <!-- 盘点开单区域 -->
    <el-card shadow="never" style="margin-bottom: 15px">
      <template #header>
        <span>📋 库存盘点</span>
      </template>
      
      <el-form :inline="true" style="margin-bottom: 15px">
        <el-form-item label="搜索SKU">
          <el-input v-model="scanCode" placeholder="输入SKU编码" style="width: 300px" @keyup.enter="handleScan" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleScan">添加商品</el-button>
          <el-button @click="clearItems">清空</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="stocktakeItems" border style="width: 100%">
        <el-table-column label="SKU编码" align="center" prop="skuCode" width="150" />
        <el-table-column label="商品名称" align="center" prop="goodsName" :show-overflow-tooltip="true" />
        <el-table-column label="颜色" align="center" width="100">
          <template #default="scope">{{ getColorLabel(scope.row.color) }}</template>
        </el-table-column>
        <el-table-column label="尺码" align="center" width="80">
          <template #default="scope">{{ getSizeLabel(scope.row.size) }}</template>
        </el-table-column>
        <el-table-column label="系统库存" align="center" width="100" prop="systemQty" />
        <el-table-column label="实盘数量" align="center" width="120">
          <template #default="scope">
            <el-input-number v-model="scope.row.actualQty" :min="0" size="small" style="width: 100px" @change="calcDiff(scope.row)" />
          </template>
        </el-table-column>
        <el-table-column label="成本价" align="center" width="120">
          <template #default="scope">
            <el-input-number v-model="scope.row.costPrice" :min="0" :precision="2" size="small" style="width: 100px" @change="calcDiff(scope.row)" />
          </template>
        </el-table-column>
        <el-table-column label="盈亏数量" align="center" width="100">
          <template #default="scope">
            <span :style="{ color: scope.row.diffQty > 0 ? 'green' : scope.row.diffQty < 0 ? 'red' : '' }">
              {{ scope.row.diffQty > 0 ? '+' : '' }}{{ scope.row.diffQty }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="盈亏金额" align="center" width="100">
          <template #default="scope">
            <span :style="{ color: scope.row.diffAmount > 0 ? 'green' : scope.row.diffAmount < 0 ? 'red' : '' }">
              {{ scope.row.diffAmount > 0 ? '+' : '' }}¥{{ scope.row.diffAmount }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="80">
          <template #default="scope">
            <el-button link type="danger" @click="removeItem(scope.$index)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div style="margin-top: 15px; display: flex; justify-content: space-between; align-items: center">
        <div>
          <span style="font-size: 14px; color: #666">共 {{ stocktakeItems.length }} 种商品</span>
          <span style="margin-left: 20px; font-size: 16px">
            盈亏金额: <span :style="{ color: totalDiffAmount > 0 ? 'green' : totalDiffAmount < 0 ? 'red' : '#333', fontWeight: 'bold' }">
              {{ totalDiffAmount > 0 ? '+' : '' }}¥{{ totalDiffAmount.toFixed(2) }}
            </span>
          </span>
        </div>
        <el-button type="primary" size="large" @click="handleSubmit" :disabled="stocktakeItems.length === 0">
          创建盘点单
        </el-button>
      </div>
    </el-card>

    <!-- 盘点单列表 -->
    <el-card shadow="never">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span>📊 盘点单列表</span>
          <el-form :inline="true" size="small">
            <el-form-item>
              <el-select v-model="queryParams.status" placeholder="状态" clearable style="width: 100px">
                <el-option label="待审核" value="0" />
                <el-option label="已完成" value="1" />
                <el-option label="已取消" value="2" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="getList">搜索</el-button>
            </el-form-item>
          </el-form>
        </div>
      </template>
      
      <el-table :data="stocktakeList" border>
        <el-table-column label="盘点单号" align="center" prop="stocktakeNo" width="150" />
        <el-table-column label="门店" align="center" prop="deptName" width="120" />
        <el-table-column label="商品数" align="center" width="80">
          <template #default="scope">{{ scope.row.itemList?.length || 0 }}</template>
        </el-table-column>
        <el-table-column label="盈亏金额" align="center" width="120">
          <template #default="scope">
            <span :style="{ color: scope.row.diffAmount > 0 ? 'green' : scope.row.diffAmount < 0 ? 'red' : '' }">
              {{ scope.row.diffAmount > 0 ? '+' : '' }}¥{{ scope.row.diffAmount }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="状态" align="center" width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.status === '0'" type="warning">待审核</el-tag>
            <el-tag v-else-if="scope.row.status === '1'" type="success">已完成</el-tag>
            <el-tag v-else-if="scope.row.status === '2'" type="info">已取消</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" align="center" prop="createTime" width="160" />
        <el-table-column label="操作" align="center" width="200">
          <template #default="scope">
            <el-button link type="primary" @click="viewDetail(scope.row)">详情</el-button>
            <el-button link type="success" @click="handleAudit(scope.row)" v-if="scope.row.status === '0'" v-hasPermi="['inventory:stocktake:edit']">审核</el-button>
            <el-button link type="warning" @click="handleCancel(scope.row)" v-if="scope.row.status === '0'" v-hasPermi="['inventory:stocktake:edit']">取消</el-button>
            <el-button link type="danger" @click="handleDelete(scope.row)" v-if="scope.row.status !== '1'" v-hasPermi="['inventory:stocktake:remove']">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog title="盘点单详情" v-model="detailOpen" width="800px">
      <el-descriptions :column="2" border v-if="currentStocktake">
        <el-descriptions-item label="盘点单号">{{ currentStocktake.stocktakeNo }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag v-if="currentStocktake.status === '0'" type="warning">待审核</el-tag>
          <el-tag v-else-if="currentStocktake.status === '1'" type="success">已完成</el-tag>
          <el-tag v-else-if="currentStocktake.status === '2'" type="info">已取消</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="门店">{{ currentStocktake.deptName }}</el-descriptions-item>
        <el-descriptions-item label="商品数">{{ currentStocktake.itemCount }}</el-descriptions-item>
        <el-descriptions-item label="盈亏金额">
          <span :style="{ color: currentStocktake.diffAmount > 0 ? 'green' : currentStocktake.diffAmount < 0 ? 'red' : '' }">
            {{ currentStocktake.diffAmount > 0 ? '+' : '' }}¥{{ currentStocktake.diffAmount }}
          </span>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentStocktake.createTime }}</el-descriptions-item>
      </el-descriptions>
      
      <el-table :data="currentStocktake?.itemList || []" border style="margin-top: 15px">
        <el-table-column label="SKU编码" align="center" prop="skuCode" width="130" />
        <el-table-column label="商品名称" align="center" prop="goodsName" />
        <el-table-column label="颜色" align="center" width="80">
          <template #default="scope">{{ getColorLabel(scope.row.color) }}</template>
        </el-table-column>
        <el-table-column label="尺码" align="center" width="60">
          <template #default="scope">{{ getSizeLabel(scope.row.size) }}</template>
        </el-table-column>
        <el-table-column label="系统库存" align="center" width="80" prop="systemQty" />
        <el-table-column label="实盘数量" align="center" width="80" prop="actualQty" />
        <el-table-column label="盈亏数量" align="center" width="80">
          <template #default="scope">
            <span :style="{ color: scope.row.diffQty > 0 ? 'green' : scope.row.diffQty < 0 ? 'red' : '' }">
              {{ scope.row.diffQty > 0 ? '+' : '' }}{{ scope.row.diffQty }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="盈亏金额" align="center" width="80">
          <template #default="scope">
            <span :style="{ color: scope.row.diffAmount > 0 ? 'green' : scope.row.diffAmount < 0 ? 'red' : '' }">
              {{ scope.row.diffAmount > 0 ? '+' : '' }}¥{{ scope.row.diffAmount }}
            </span>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup name="Stocktake" lang="ts">
import { listStocktake, getStocktake, createStocktake, auditStocktake, cancelStocktake, delStocktake } from "@/api/inventory/stocktake"
import { listGoods } from "@/api/inventory/goods"
import { listStock } from "@/api/inventory/stock"
import { getDicts } from "@/api/system/dict/data"

const { proxy } = getCurrentInstance() as ComponentInternalInstance

const scanCode = ref("")
const stocktakeItems = ref<any[]>([])
const stocktakeList = ref<any[]>([])
const total = ref(0)
const detailOpen = ref(false)
const currentStocktake = ref<any>(null)

const queryParams = ref({ pageNum: 1, pageSize: 10, status: undefined })

const colorOptions = ref<any[]>([])
const sizeOptions = ref<any[]>([])

const totalDiffAmount = computed(() => stocktakeItems.value.reduce((sum, item) => sum + (item.diffAmount || 0), 0))

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
    // 先查询商品SKU信息
    const goodsRes = await listGoods({ skuCode: scanCode.value, pageSize: 1 })
    if (goodsRes.rows && goodsRes.rows.length > 0) {
      const goods = goodsRes.rows[0]
      const sku = goods.skuList?.find((s: any) => s.skuCode === scanCode.value)
      if (sku) {
        // 检查是否已存在
        const existItem = stocktakeItems.value.find(item => item.skuId === sku.skuId)
        if (existItem) {
          proxy?.$modal.msgWarning("该SKU已添加")
        } else {
          // 查询当前库存
          const stockRes = await listStock({ skuCode: scanCode.value, pageSize: 1 })
          const systemQty = stockRes.rows?.[0]?.quantity || 0
          
          stocktakeItems.value.push({
            skuId: sku.skuId,
            skuCode: sku.skuCode,
            goodsName: goods.goodsName,
            color: sku.color,
            size: sku.size,
            systemQty: systemQty,
            actualQty: systemQty,
            costPrice: goods.purchasePrice || 0,
            diffQty: 0,
            diffAmount: 0
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

const calcDiff = (row: any) => {
  row.diffQty = row.actualQty - row.systemQty
  row.diffAmount = (row.costPrice || 0) * row.diffQty
}

const removeItem = (index: number) => {
  stocktakeItems.value.splice(index, 1)
}

const clearItems = () => {
  stocktakeItems.value = []
}

const handleSubmit = async () => {
  if (stocktakeItems.value.length === 0) return
  
  try {
    const data = {
      itemList: stocktakeItems.value.map(item => ({
        skuId: item.skuId,
        skuCode: item.skuCode,
        goodsName: item.goodsName,
        color: item.color,
        size: item.size,
        systemQty: item.systemQty,
        actualQty: item.actualQty,
        costPrice: item.costPrice,
        diffQty: item.diffQty,
        diffAmount: item.diffAmount
      }))
    }
    
    const res = await createStocktake(data)
    proxy?.$modal.msgSuccess("盘点单创建成功：" + res.data?.stocktakeNo || "")
    stocktakeItems.value = []
    getList()
  } catch (e: any) {
    proxy?.$modal.msgError(e.message || "创建失败")
  }
}

const getList = async () => {
  const res = await listStocktake(queryParams.value)
  stocktakeList.value = res.rows || []
  total.value = res.total || 0
}

const viewDetail = async (row: any) => {
  const res = await getStocktake(row.stocktakeId)
  currentStocktake.value = res.data
  detailOpen.value = true
}

const handleAudit = async (row: any) => {
  await proxy?.$modal.confirm('确认审核？审核后将根据盘点结果调整库存。')
  await auditStocktake(row.stocktakeId)
  proxy?.$modal.msgSuccess("审核成功，库存已调整")
  getList()
}

const handleCancel = async (row: any) => {
  await proxy?.$modal.confirm('确认取消该盘点单？')
  await cancelStocktake(row.stocktakeId)
  proxy?.$modal.msgSuccess("已取消")
  getList()
}

const handleDelete = async (row: any) => {
  await proxy?.$modal.confirm('确认删除该盘点单？')
  await delStocktake(row.stocktakeId)
  proxy?.$modal.msgSuccess("删除成功")
  getList()
}

onMounted(() => {
  loadDicts()
  getList()
})
</script>
