<template>
  <div class="app-container">
    <!-- 调拨开单区域 -->
    <el-card shadow="never" style="margin-bottom: 15px">
      <template #header>
        <span>📦 库存调拨</span>
      </template>
      
      <el-form :inline="true" style="margin-bottom: 15px">
        <el-form-item label="调出门店">
          <el-tree-select
            v-model="deptIdOut"
            :data="deptOptions"
            :props="{ value: 'id', label: 'label', children: 'children' }"
            value-key="id"
            placeholder="请选择调出门店"
            style="width: 200px"
            check-strictly
          />
        </el-form-item>
        <el-form-item label="调入门店">
          <el-tree-select
            v-model="deptIdIn"
            :data="deptOptions"
            :props="{ value: 'id', label: 'label', children: 'children' }"
            value-key="id"
            placeholder="请选择调入门店"
            style="width: 200px"
            check-strictly
          />
        </el-form-item>
        <el-form-item label="搜索SKU">
          <el-input v-model="scanCode" placeholder="输入SKU编码" style="width: 250px" @keyup.enter="handleScan" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleScan">添加商品</el-button>
          <el-button @click="clearItems">清空</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="transferItems" border style="width: 100%">
        <el-table-column label="SKU编码" align="center" prop="skuCode" width="150" />
        <el-table-column label="商品名称" align="center" prop="goodsName" :show-overflow-tooltip="true" />
        <el-table-column label="颜色" align="center" width="100">
          <template #default="scope">{{ getColorLabel(scope.row.color) }}</template>
        </el-table-column>
        <el-table-column label="尺码" align="center" width="80">
          <template #default="scope">{{ getSizeLabel(scope.row.size) }}</template>
        </el-table-column>
        <el-table-column label="调拨数量" align="center" width="120">
          <template #default="scope">
            <el-input-number v-model="scope.row.quantity" :min="1" size="small" style="width: 100px" />
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
          <span style="font-size: 14px; color: #666">共 {{ transferItems.length }} 种商品，{{ totalQuantity }} 件</span>
        </div>
        <el-button type="primary" size="large" @click="handleSubmit" :disabled="transferItems.length === 0 || !deptIdOut || !deptIdIn">
          创建调拨单
        </el-button>
      </div>
    </el-card>

    <!-- 调拨单列表 -->
    <el-card shadow="never">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span>📋 调拨单列表</span>
          <el-form :inline="true" size="small">
            <el-form-item>
              <el-select v-model="queryParams.status" placeholder="状态" clearable style="width: 120px">
                <el-option label="待审核" value="0" />
                <el-option label="待出库" value="1" />
                <el-option label="待入库" value="2" />
                <el-option label="已完成" value="3" />
                <el-option label="已取消" value="4" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="getList">搜索</el-button>
            </el-form-item>
          </el-form>
        </div>
      </template>
      
      <el-table :data="transferList" border>
        <el-table-column label="调拨单号" align="center" prop="transferNo" width="150" />
        <el-table-column label="调出门店" align="center" prop="deptNameOut" width="120" />
        <el-table-column label="调入门店" align="center" prop="deptNameIn" width="120" />
        <el-table-column label="商品数" align="center" width="80">
          <template #default="scope">{{ scope.row.itemList?.length || 0 }}</template>
        </el-table-column>
        <el-table-column label="总数量" align="center" prop="totalQty" width="80" />
        <el-table-column label="状态" align="center" width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.status === '0'" type="info">待审核</el-tag>
            <el-tag v-else-if="scope.row.status === '1'" type="warning">待出库</el-tag>
            <el-tag v-else-if="scope.row.status === '2'" type="primary">待入库</el-tag>
            <el-tag v-else-if="scope.row.status === '3'" type="success">已完成</el-tag>
            <el-tag v-else-if="scope.row.status === '4'">已取消</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" align="center" prop="createTime" width="160" />
        <el-table-column label="操作" align="center" width="280">
          <template #default="scope">
            <el-button link type="primary" @click="viewDetail(scope.row)">详情</el-button>
            <el-button link type="success" @click="handleAudit(scope.row)" v-if="scope.row.status === '0'" v-hasPermi="['inventory:transfer:edit']">审核</el-button>
            <el-button link type="warning" @click="handleOut(scope.row)" v-if="scope.row.status === '1'" v-hasPermi="['inventory:transfer:edit']">出库</el-button>
            <el-button link type="success" @click="handleIn(scope.row)" v-if="scope.row.status === '2'" v-hasPermi="['inventory:transfer:edit']">入库</el-button>
            <el-button link type="info" @click="handleCancel(scope.row)" v-if="scope.row.status === '0'" v-hasPermi="['inventory:transfer:edit']">取消</el-button>
            <el-button link type="danger" @click="handleDelete(scope.row)" v-if="scope.row.status !== '2' && scope.row.status !== '3'" v-hasPermi="['inventory:transfer:remove']">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog title="调拨单详情" v-model="detailOpen" width="800px">
      <el-descriptions :column="2" border v-if="currentTransfer">
        <el-descriptions-item label="调拨单号">{{ currentTransfer.transferNo }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag v-if="currentTransfer.status === '0'" type="info">待审核</el-tag>
          <el-tag v-else-if="currentTransfer.status === '1'" type="warning">待出库</el-tag>
          <el-tag v-else-if="currentTransfer.status === '2'" type="primary">待入库</el-tag>
          <el-tag v-else-if="currentTransfer.status === '3'" type="success">已完成</el-tag>
          <el-tag v-else-if="currentTransfer.status === '4'">已取消</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="调出门店">{{ currentTransfer.deptNameOut }}</el-descriptions-item>
        <el-descriptions-item label="调入门店">{{ currentTransfer.deptNameIn }}</el-descriptions-item>
        <el-descriptions-item label="总数量">{{ currentTransfer.totalQty }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentTransfer.createTime }}</el-descriptions-item>
        <el-descriptions-item label="审核人" v-if="currentTransfer.auditBy">{{ currentTransfer.auditBy }}</el-descriptions-item>
        <el-descriptions-item label="审核时间" v-if="currentTransfer.auditTime">{{ currentTransfer.auditTime }}</el-descriptions-item>
        <el-descriptions-item label="出库人" v-if="currentTransfer.outBy">{{ currentTransfer.outBy }}</el-descriptions-item>
        <el-descriptions-item label="出库时间" v-if="currentTransfer.outTime">{{ currentTransfer.outTime }}</el-descriptions-item>
        <el-descriptions-item label="入库人" v-if="currentTransfer.inBy">{{ currentTransfer.inBy }}</el-descriptions-item>
        <el-descriptions-item label="入库时间" v-if="currentTransfer.inTime">{{ currentTransfer.inTime }}</el-descriptions-item>
      </el-descriptions>
      
      <el-table :data="currentTransfer?.itemList || []" border style="margin-top: 15px">
        <el-table-column label="SKU编码" align="center" prop="skuCode" width="130" />
        <el-table-column label="商品名称" align="center" prop="goodsName" />
        <el-table-column label="颜色" align="center" width="80">
          <template #default="scope">{{ getColorLabel(scope.row.color) }}</template>
        </el-table-column>
        <el-table-column label="尺码" align="center" width="60">
          <template #default="scope">{{ getSizeLabel(scope.row.size) }}</template>
        </el-table-column>
        <el-table-column label="调拨数量" align="center" width="80" prop="quantity" />
        <el-table-column label="已出库" align="center" width="70" prop="outQty" />
        <el-table-column label="已入库" align="center" width="70" prop="inQty" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup name="Transfer" lang="ts">
import { listTransfer, getTransfer, createTransfer, auditTransfer, outTransfer, inTransfer, cancelTransfer, delTransfer } from "@/api/inventory/transfer"
import { listGoods } from "@/api/inventory/goods"
import { getDicts } from "@/api/system/dict/data"
import { treeselect } from "@/api/system/dept"

const { proxy } = getCurrentInstance() as ComponentInternalInstance

const deptIdOut = ref<number | undefined>()
const deptIdIn = ref<number | undefined>()
const scanCode = ref("")
const transferItems = ref<any[]>([])
const transferList = ref<any[]>([])
const total = ref(0)
const detailOpen = ref(false)
const currentTransfer = ref<any>(null)
const deptOptions = ref<any[]>([])

const queryParams = ref({ pageNum: 1, pageSize: 10, status: undefined })

const colorOptions = ref<any[]>([])
const sizeOptions = ref<any[]>([])

const totalQuantity = computed(() => transferItems.value.reduce((sum, item) => sum + item.quantity, 0))

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

const loadDeptTree = async () => {
  try {
    const res = await treeselect()
    deptOptions.value = res.data || []
  } catch (e) {
    console.error('加载部门树失败', e)
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
        const existItem = transferItems.value.find(item => item.skuId === sku.skuId)
        if (existItem) {
          existItem.quantity++
        } else {
          transferItems.value.push({
            skuId: sku.skuId,
            skuCode: sku.skuCode,
            goodsName: goods.goodsName,
            color: sku.color,
            size: sku.size,
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
  transferItems.value.splice(index, 1)
}

const clearItems = () => {
  transferItems.value = []
}

const handleSubmit = async () => {
  if (transferItems.value.length === 0 || !deptIdOut.value || !deptIdIn.value) return
  
  if (deptIdOut.value === deptIdIn.value) {
    proxy?.$modal.msgWarning("调出门店和调入门店不能相同")
    return
  }
  
  try {
    const data = {
      deptIdOut: deptIdOut.value,
      deptIdIn: deptIdIn.value,
      itemList: transferItems.value.map(item => ({
        skuId: item.skuId,
        quantity: item.quantity
      }))
    }
    
    const res = await createTransfer(data)
    proxy?.$modal.msgSuccess("调拨单创建成功：" + res.data?.transferNo || "")
    transferItems.value = []
    deptIdOut.value = undefined
    deptIdIn.value = undefined
    getList()
  } catch (e: any) {
    proxy?.$modal.msgError(e.message || "创建失败")
  }
}

const getList = async () => {
  const res = await listTransfer(queryParams.value)
  transferList.value = res.rows || []
  total.value = res.total || 0
}

const viewDetail = async (row: any) => {
  const res = await getTransfer(row.transferId)
  currentTransfer.value = res.data
  detailOpen.value = true
}

const handleAudit = async (row: any) => {
  await proxy?.$modal.confirm('确认审核该调拨单？')
  await auditTransfer(row.transferId)
  proxy?.$modal.msgSuccess("审核成功")
  getList()
}

const handleOut = async (row: any) => {
  await proxy?.$modal.confirm('确认出库？出库后调出门店库存将减少。')
  await outTransfer(row.transferId)
  proxy?.$modal.msgSuccess("出库成功")
  getList()
}

const handleIn = async (row: any) => {
  await proxy?.$modal.confirm('确认入库？入库后调入门店库存将增加。')
  await inTransfer(row.transferId)
  proxy?.$modal.msgSuccess("入库成功")
  getList()
}

const handleCancel = async (row: any) => {
  await proxy?.$modal.confirm('确认取消该调拨单？')
  await cancelTransfer(row.transferId)
  proxy?.$modal.msgSuccess("已取消")
  getList()
}

const handleDelete = async (row: any) => {
  await proxy?.$modal.confirm('确认删除该调拨单？')
  await delTransfer(row.transferId)
  proxy?.$modal.msgSuccess("删除成功")
  getList()
}

onMounted(() => {
  loadDicts()
  loadDeptTree()
  getList()
})
</script>
