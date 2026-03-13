<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="SKU编码" prop="skuCode">
        <el-input v-model="queryParams.skuCode" placeholder="请输入SKU编码" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="商品名称" prop="goodsName">
        <el-input v-model="queryParams.goodsName" placeholder="请输入商品名称" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="颜色" prop="color">
        <el-select v-model="queryParams.color" placeholder="请选择颜色" clearable style="width: 150px">
          <el-option v-for="dict in colorOptions" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="尺码" prop="size">
        <el-select v-model="queryParams.size" placeholder="请选择尺码" clearable style="width: 120px">
          <el-option v-for="dict in sizeOptions" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="warning" plain icon="WarnTriangleFilled" @click="handleWarning" v-hasPermi="['inventory:stock:warning']">库存预警</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="stockList">
      <el-table-column label="SKU编码" align="center" prop="skuCode" width="150" />
      <el-table-column label="商品名称" align="center" prop="goodsName" :show-overflow-tooltip="true" />
      <el-table-column label="颜色" align="center" width="100">
        <template #default="scope">
          {{ getColorLabel(scope.row.color) }}
        </template>
      </el-table-column>
      <el-table-column label="尺码" align="center" width="80">
        <template #default="scope">
          {{ getSizeLabel(scope.row.size) }}
        </template>
      </el-table-column>
      <el-table-column label="库存数量" align="center" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.quantity <= scope.row.warningQty ? 'danger' : 'success'">
            {{ scope.row.quantity }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="锁定数量" align="center" prop="lockedQty" width="100" />
      <el-table-column label="可用数量" align="center" width="100">
        <template #default="scope">
          {{ (scope.row.quantity || 0) - (scope.row.lockedQty || 0) }}
        </template>
      </el-table-column>
      <el-table-column label="预警值" align="center" prop="warningQty" width="80" />
      <el-table-column label="更新时间" align="center" prop="updateTime" width="160" />
      <el-table-column label="操作" align="center" width="150">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleAdjust(scope.row)" v-hasPermi="['inventory:stock:edit']">调整</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 库存调整对话框 -->
    <el-dialog title="库存调整" v-model="adjustOpen" width="500px" append-to-body>
      <el-form :model="adjustForm" :rules="adjustRules" ref="adjustRef" label-width="100px">
        <el-form-item label="SKU编码">
          <el-input v-model="adjustForm.skuCode" disabled />
        </el-form-item>
        <el-form-item label="商品名称">
          <el-input v-model="adjustForm.goodsName" disabled />
        </el-form-item>
        <el-form-item label="当前库存">
          <el-input-number v-model="adjustForm.oldQuantity" disabled />
        </el-form-item>
        <el-form-item label="调整数量" prop="adjustQty">
          <el-input-number v-model="adjustForm.adjustQty" :min="-9999" :max="9999" />
          <span style="margin-left: 10px; color: #999">（正数入库，负数出库）</span>
        </el-form-item>
        <el-form-item label="调整后库存">
          <el-tag size="large">{{ (adjustForm.oldQuantity || 0) + (adjustForm.adjustQty || 0) }}</el-tag>
        </el-form-item>
        <el-form-item label="预警值" prop="warningQty">
          <el-input-number v-model="adjustForm.warningQty" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitAdjust">确 定</el-button>
          <el-button @click="adjustOpen = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 库存预警对话框 -->
    <el-dialog title="库存预警列表" v-model="warningOpen" width="900px" append-to-body>
      <el-table :data="warningList" border>
        <el-table-column label="SKU编码" align="center" prop="skuCode" width="150" />
        <el-table-column label="商品名称" align="center" prop="goodsName" :show-overflow-tooltip="true" />
        <el-table-column label="颜色" align="center" width="100">
          <template #default="scope">{{ getColorLabel(scope.row.color) }}</template>
        </el-table-column>
        <el-table-column label="尺码" align="center" width="80">
          <template #default="scope">{{ getSizeLabel(scope.row.size) }}</template>
        </el-table-column>
        <el-table-column label="当前库存" align="center" width="100">
          <template #default="scope">
            <el-tag type="danger">{{ scope.row.quantity }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="预警值" align="center" prop="warningQty" width="80" />
        <el-table-column label="缺口" align="center" width="80">
          <template #default="scope">
            <span style="color: red">{{ (scope.row.warningQty || 0) - (scope.row.quantity || 0) }}</span>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup name="Stock" lang="ts">
import { listStock, listWarningStock, updateStock } from "@/api/inventory/stock"
import { getDicts } from "@/api/system/dict/data"

const { proxy } = getCurrentInstance() as ComponentInternalInstance

const stockList = ref<any[]>([])
const warningList = ref<any[]>([])
const loading = ref(true)
const showSearch = ref(true)
const total = ref(0)
const adjustOpen = ref(false)
const warningOpen = ref(false)

const colorOptions = ref<any[]>([])
const sizeOptions = ref<any[]>([])

const data = reactive<any>({
  queryParams: { pageNum: 1, pageSize: 10, skuCode: undefined, goodsName: undefined, color: undefined, size: undefined },
  adjustForm: {},
  adjustRules: {
    adjustQty: [{ required: true, message: "调整数量不能为空", trigger: "blur" }]
  }
})

const { queryParams, adjustForm, adjustRules } = toRefs(data)

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

const getList = async () => {
  loading.value = true
  const res = await listStock(queryParams.value)
  stockList.value = res.rows
  total.value = res.total
  loading.value = false
}

const handleQuery = () => { queryParams.value.pageNum = 1; getList() }
const resetQuery = () => { proxy?.resetForm("queryRef"); handleQuery() }

// 库存调整
const handleAdjust = (row: any) => {
  adjustForm.value = {
    stockId: row.stockId,
    skuCode: row.skuCode,
    goodsName: row.goodsName,
    oldQuantity: row.quantity,
    adjustQty: 0,
    warningQty: row.warningQty || 10
  }
  adjustOpen.value = true
}

// 提交调整
const submitAdjust = () => {
  (proxy?.$refs["adjustRef"] as any).validate(async (valid: boolean) => {
    if (valid) {
      const newQty = (adjustForm.value.oldQuantity || 0) + (adjustForm.value.adjustQty || 0)
      if (newQty < 0) {
        proxy?.$modal.msgError("调整后库存不能为负数")
        return
      }
      await updateStock({
        stockId: adjustForm.value.stockId,
        quantity: newQty,
        warningQty: adjustForm.value.warningQty
      })
      proxy?.$modal.msgSuccess("调整成功")
      adjustOpen.value = false
      getList()
    }
  })
}

// 库存预警
const handleWarning = async () => {
  const res = await listWarningStock({})
  warningList.value = res.rows || []
  warningOpen.value = true
}

// 初始化
loadDicts()
getList()
</script>
