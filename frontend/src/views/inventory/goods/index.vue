<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="商品编码" prop="goodsCode">
        <el-input v-model="queryParams.goodsCode" placeholder="请输入商品编码" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="商品名称" prop="goodsName">
        <el-input v-model="queryParams.goodsName" placeholder="请输入商品名称" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="分类" prop="categoryId">
        <el-select v-model="queryParams.categoryId" placeholder="请选择分类" clearable style="width: 200px">
          <el-option v-for="cat in categoryOptions" :key="cat.categoryId" :label="cat.categoryName" :value="cat.categoryId" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="商品状态" clearable style="width: 200px">
          <el-option label="正常" value="0" />
          <el-option label="停用" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['inventory:goods:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['inventory:goods:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['inventory:goods:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Printer" @click="handlePrint" v-hasPermi="['inventory:goods:print']">打印吊牌</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="goodsList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="商品编码" align="center" prop="goodsCode" width="120" />
      <el-table-column label="商品名称" align="center" prop="goodsName" :show-overflow-tooltip="true" />
      <el-table-column label="分类" align="center" prop="categoryName" width="100" />
      <el-table-column label="品牌" align="center" prop="brandName" width="100" />
      <el-table-column label="季节" align="center" prop="season" width="80">
        <template #default="scope">
          <span v-if="scope.row.season === '1'">春</span>
          <span v-else-if="scope.row.season === '2'">夏</span>
          <span v-else-if="scope.row.season === '3'">秋</span>
          <span v-else-if="scope.row.season === '4'">冬</span>
          <span v-else-if="scope.row.season === '5'">四季</span>
        </template>
      </el-table-column>
      <el-table-column label="年份" align="center" prop="year" width="80" />
      <el-table-column label="进货价" align="center" prop="purchasePrice" width="100">
        <template #default="scope">¥{{ scope.row.purchasePrice || 0 }}</template>
      </el-table-column>
      <el-table-column label="销售价" align="center" prop="salePrice" width="100">
        <template #default="scope">¥{{ scope.row.salePrice || 0 }}</template>
      </el-table-column>
      <el-table-column label="SKU数" align="center" width="80">
        <template #default="scope">
          <el-tag size="small">{{ scope.row.skuList?.length || 0 }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" width="80">
        <template #default="scope">
          <el-switch v-model="scope.row.status" active-value="0" inactive-value="1" @change="handleStatusChange(scope.row)"></el-switch>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="150">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['inventory:goods:edit']"></el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['inventory:goods:remove']"></el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 添加或修改商品对话框 -->
    <el-dialog :title="title" v-model="open" width="900px" append-to-body>
      <el-form :model="form" :rules="rules" ref="goodsRef" label-width="100px">
        <el-row>
          <!-- 商品编码：编辑时显示，新增时隐藏（由后端自动生成） -->
          <el-col :span="12" v-if="form.goodsId">
            <el-form-item label="商品编码">
              <el-input v-model="form.goodsCode" disabled placeholder="自动生成" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="商品名称" prop="goodsName">
              <el-input v-model="form.goodsName" placeholder="请输入商品名称" maxlength="200" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="商品分类" prop="categoryId">
              <el-select v-model="form.categoryId" placeholder="请选择分类" clearable>
                <el-option v-for="cat in categoryOptions" :key="cat.categoryId" :label="cat.categoryName" :value="cat.categoryId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="品牌" prop="brandId">
              <el-select v-model="form.brandId" placeholder="请选择品牌" clearable>
                <el-option v-for="brand in brandOptions" :key="brand.brandId" :label="brand.brandName" :value="brand.brandId" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="季节" prop="season">
              <el-select v-model="form.season" placeholder="请选择季节">
                <el-option label="春" value="1" />
                <el-option label="夏" value="2" />
                <el-option label="秋" value="3" />
                <el-option label="冬" value="4" />
                <el-option label="四季" value="5" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="年份" prop="year">
              <el-date-picker v-model="form.year" type="year" placeholder="选择年份" value-format="YYYY" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <el-form-item label="单位" prop="unit">
              <el-input v-model="form.unit" placeholder="单位" maxlength="10" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="参考进价" prop="purchasePrice">
              <el-input-number v-model="form.purchasePrice" :precision="2" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="参考售价" prop="salePrice">
              <el-input-number v-model="form.salePrice" :precision="2" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <!-- SKU 规格选择 -->
        <el-divider content-position="left">SKU 规格</el-divider>
        <el-row>
          <el-col :span="12">
            <el-form-item label="颜色">
              <el-checkbox-group v-model="selectedColorCodes">
                <el-checkbox v-for="color in colorOptions" :key="color.specCode" :label="color.specCode">
                  {{ color.valueName }}
                </el-checkbox>
              </el-checkbox-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="尺码">
              <el-checkbox-group v-model="selectedSizeCodes">
                <el-checkbox v-for="size in sizeOptions" :key="size.specCode" :label="size.specCode">{{ size.valueName }}</el-checkbox>
              </el-checkbox-group>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- SKU 表格 -->
        <el-divider content-position="left">SKU 列表 ({{ skuTable.length }} 个)</el-divider>
        <el-table :data="skuTable" border size="small" max-height="300">
          <el-table-column label="颜色" align="center" prop="colorName" width="100" />
          <el-table-column label="尺码" align="center" prop="sizeName" width="80" />
          <el-table-column label="SKU编码" align="center" prop="skuCode" width="180" />
          <el-table-column label="条码" align="center" width="150">
            <template #default="scope">
              <el-input v-model="scope.row.barcode" size="small" placeholder="自动生成" disabled />
            </template>
          </el-table-column>
          <el-table-column label="进货价" align="center" width="160">
            <template #default="scope">
              <el-input-number v-model="scope.row.purchasePrice" :precision="2" :min="0" size="small" />
            </template>
          </el-table-column>
          <el-table-column label="销售价" align="center" width="160">
            <template #default="scope">
              <el-input-number v-model="scope.row.salePrice" :precision="2" :min="0" size="small" />
            </template>
          </el-table-column>
        </el-table>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 打印吊牌对话框 -->
    <el-dialog title="打印吊牌" v-model="printOpen" width="900px" append-to-body>
      <el-alert title="提示：点击确认后，将按设置的数量批量打印吊牌，打印服务需启动在 ws://localhost:8765" type="info" :closable="false" style="margin-bottom: 15px" />
      
      <el-form :inline="true" size="small" style="margin-bottom: 15px">
        <el-form-item label="批量设置打印数量">
          <el-input-number v-model="batchPrintCount" :min="1" :max="100" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="applyBatchPrintCount">应用到所有</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="printList" border max-height="400">
        <el-table-column label="商品编码" align="center" prop="goodsCode" width="120" />
        <el-table-column label="商品名称" align="center" prop="goodsName" :show-overflow-tooltip="true" />
        <el-table-column label="SKU编码" align="center" prop="skuCode" width="150" />
        <el-table-column label="颜色" align="center" prop="colorName" width="80" />
        <el-table-column label="尺码" align="center" prop="sizeName" width="80" />
        <el-table-column label="销售价" align="center" width="100">
          <template #default="scope">¥{{ scope.row.salePrice || 0 }}</template>
        </el-table-column>
        <el-table-column label="打印数量" align="center" width="120">
          <template #default="scope">
            <el-input-number v-model="scope.row.printCount" :min="0" :max="100" size="small" style="width: 90px" />
          </template>
        </el-table-column>
      </el-table>

      <div style="margin-top: 15px; color: #666">
        <span>共 {{ printList.length }} 个 SKU，总计打印 {{ totalPrintCount }} 张吊牌</span>
        <span v-if="printStatus" style="margin-left: 20px">
          <el-tag :type="printStatus === 'printing' ? 'warning' : 'success'">
            {{ printStatus === 'printing' ? '打印中...' : '打印完成' }}
          </el-tag>
          <span style="margin-left: 10px">{{ printProgress }} / {{ totalPrintCount }}</span>
        </span>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="executePrint" :loading="printStatus === 'printing'">确认打印</el-button>
          <el-button @click="printOpen = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Goods" lang="ts">
import { listGoods, getGoods, delGoods, addGoods, updateGoods } from "@/api/inventory/goods"
import { listCategoryAll } from "@/api/inventory/category"
import { listBrandAll } from "@/api/inventory/brand"
import { listSpecValuesByCode } from "@/api/inventory/spec"
import { io, Socket } from "socket.io-client"

const { proxy } = getCurrentInstance() as ComponentInternalInstance

// ==================== 打印相关配置 ====================
const PRINTER_WS_URL = "ws://localhost:8765"  // 打印服务 WebSocket 地址
let printSocket: Socket | null = null

// 打印相关状态
const printOpen = ref(false)
const printList = ref<any[]>([])
const batchPrintCount = ref(1)
const printStatus = ref<"idle" | "printing" | "done">("idle")
const printProgress = ref(0)

// 计算总打印数量
const totalPrintCount = computed(() => {
  return printList.value.reduce((sum, item) => sum + (item.printCount || 0), 0)
})

// 规格选项（从数据库加载，包含编码）
const colorOptions = ref<any[]>([])  // 颜色选项（含 specCode: 01,02...）
const sizeOptions = ref<any[]>([])   // 尺码选项（含 specCode: XS,S,M...）
const colorCodeMap = ref<Map<string, string>>(new Map())  // specCode -> valueName
const sizeCodeMap = ref<Map<string, string>>(new Map())   // specCode -> valueName

const goodsList = ref<any[]>([])
const categoryOptions = ref<any[]>([])
const brandOptions = ref<any[]>([])
const selectedColorCodes = ref<string[]>([])  // 选中的颜色编码
const selectedSizeCodes = ref<string[]>([])   // 选中的尺码编码
const skuTable = ref<any[]>([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref<number[]>([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")

const data = reactive<any>({
  form: {},
  queryParams: { pageNum: 1, pageSize: 10, goodsCode: undefined, goodsName: undefined, categoryId: undefined, status: undefined },
  rules: {
    goodsName: [{ required: true, message: "商品名称不能为空", trigger: "blur" }],
    categoryId: [{ required: true, message: "商品分类不能为空", trigger: "change" }]
  }
})

const { queryParams, form, rules } = toRefs(data)

// 加载颜色规格值（含编码）
const loadColorOptions = async () => {
  try {
    const res = await listSpecValuesByCode('color')
    colorOptions.value = res.data || []
    // 构建编码->名称映射
    colorCodeMap.value.clear()
    colorOptions.value.forEach((item: any) => {
      colorCodeMap.value.set(item.specCode, item.valueName)
    })
  } catch (e) {
    console.error('加载颜色选项失败', e)
  }
}

// 加载尺码规格值（含编码）
const loadSizeOptions = async () => {
  try {
    const res = await listSpecValuesByCode('size')
    sizeOptions.value = res.data || []
    // 构建编码->名称映射
    sizeCodeMap.value.clear()
    sizeOptions.value.forEach((item: any) => {
      sizeCodeMap.value.set(item.specCode, item.valueName)
    })
  } catch (e) {
    console.error('加载尺码选项失败', e)
  }
}

// 监听颜色和尺码变化，自动生成SKU表格
watch([selectedColorCodes, selectedSizeCodes], () => {
  generateSkuTable()
})

// 生成SKU表格 - 使用编码
const generateSkuTable = () => {
  skuTable.value = []
  if (selectedColorCodes.value.length === 0 && selectedSizeCodes.value.length === 0) return

  const colorCodes = selectedColorCodes.value.length > 0 ? selectedColorCodes.value : ['']
  const sizeCodes = selectedSizeCodes.value.length > 0 ? selectedSizeCodes.value : ['']

  for (const colorCode of colorCodes) {
    for (const sizeCode of sizeCodes) {
      // SKU编码 = 款号 + 颜色编码(2位) + 尺码编码
      // 例如：261T001 + 01 + S = 261T00101S
      const skuCode = `${form.value.goodsCode || 'NEW'}${colorCode}${sizeCode}`
      // 条码同SKU编码
      const barcode = skuCode
      
      skuTable.value.push({
        color: colorCode,           // 存储编码
        colorName: colorCodeMap.value.get(colorCode) || colorCode,  // 显示名称
        size: sizeCode,             // 存储编码
        sizeName: sizeCodeMap.value.get(sizeCode) || sizeCode,      // 显示名称
        skuCode,
        barcode,
        purchasePrice: form.value.purchasePrice || 0,
        salePrice: form.value.salePrice || 0
      })
    }
  }
}

const getList = async () => {
  loading.value = true
  const res = await listGoods(queryParams.value)
  goodsList.value = res.rows
  total.value = res.total
  loading.value = false
}

const getCategoryList = async () => {
  const res = await listCategoryAll()
  categoryOptions.value = res.data || []
}

const getBrandList = async () => {
  const res = await listBrandAll()
  brandOptions.value = res.data || []
}

const cancel = () => { open.value = false; reset() }

const reset = () => {
  form.value = { 
    goodsId: undefined, 
    goodsCode: undefined, 
    goodsName: undefined, 
    categoryId: undefined, 
    brandId: undefined, 
    season: undefined, 
    year: new Date().getFullYear().toString(),
    unit: '件', 
    purchasePrice: undefined, 
    salePrice: undefined, 
    status: '0', 
    remark: undefined 
  }
  selectedColorCodes.value = []
  selectedSizeCodes.value = []
  skuTable.value = []
  proxy?.resetForm("goodsRef")
}

const handleQuery = () => { queryParams.value.pageNum = 1; getList() }
const resetQuery = () => { proxy?.resetForm("queryRef"); handleQuery() }
const handleSelectionChange = (selection: any[]) => { ids.value = selection.map(item => item.goodsId); single.value = selection.length !== 1; multiple.value = !selection.length }

const handleAdd = () => { reset(); open.value = true; title.value = "添加商品" }

const handleUpdate = async (row: any) => {
  reset()
  const goodsId = row.goodsId || ids.value[0]
  const res = await getGoods(goodsId)
  form.value = res.data
  if (res.data.skuList && res.data.skuList.length > 0) {
    // 从SKU中提取已选的颜色编码和尺码编码
    const colorCodes = [...new Set(res.data.skuList.map((s: any) => s.color).filter(Boolean))]
    const sizeCodes = [...new Set(res.data.skuList.map((s: any) => s.size).filter(Boolean))]
    selectedColorCodes.value = colorCodes
    selectedSizeCodes.value = sizeCodes
    // 转换SKU列表，添加显示名称
    skuTable.value = res.data.skuList.map((s: any) => ({
      ...s,
      colorName: colorCodeMap.value.get(s.color) || s.color,
      sizeName: sizeCodeMap.value.get(s.size) || s.size
    }))
  }
  open.value = true
  title.value = "修改商品"
}

const submitForm = () => {
  (proxy?.$refs["goodsRef"] as any).validate(async (valid: boolean) => {
    if (valid) {
      // 提交时只发送编码，不发送名称
      form.value.skuList = skuTable.value.map((s: any) => ({
        skuId: s.skuId,
        color: s.color,
        size: s.size,
        skuCode: s.skuCode,
        barcode: s.barcode,
        purchasePrice: s.purchasePrice,
        salePrice: s.salePrice
      }))
      if (form.value.goodsId != undefined) {
        await updateGoods(form.value)
        proxy?.$modal.msgSuccess("修改成功")
      } else {
        const res = await addGoods(form.value)
        proxy?.$modal.msgSuccess("新增成功，商品编码：" + (res.data?.goodsCode || "已自动生成"))
      }
      open.value = false
      getList()
    }
  })
}

const handleDelete = async (row: any) => {
  const goodsIds = row.goodsId || ids.value
  await proxy?.$modal.confirm('是否确认删除商品编号为"' + goodsIds + '"的数据项？')
  await delGoods(goodsIds)
  getList()
  proxy?.$modal.msgSuccess("删除成功")
}

const handleStatusChange = async (row: any) => {
  const text = row.status === "0" ? "启用" : "停用"
  try {
    await proxy?.$modal.confirm(`确认要"${text}""${row.goodsName}"商品吗？`)
    await updateGoods({ goodsId: row.goodsId, status: row.status })
    proxy?.$modal.msgSuccess(text + "成功")
  } catch { row.status = row.status === "0" ? "1" : "0" }
}

// ==================== 打印吊牌功能 ====================

// 连接打印服务
const connectPrinter = (): Promise<boolean> => {
  return new Promise((resolve) => {
    if (printSocket && printSocket.connected) {
      resolve(true)
      return
    }

    printSocket = io(PRINTER_WS_URL, {
      transports: ['websocket'],
      reconnection: false,
      timeout: 5000
    })

    printSocket.on('connect', () => {
      resolve(true)
    })

    printSocket.on('connect_error', () => {
      proxy?.$modal.msgError("无法连接打印服务，请确保打印服务已启动（ws://localhost:8765）")
      resolve(false)
    })
  })
}

// 打开打印对话框
const handlePrint = async () => {
  if (ids.value.length === 0) {
    proxy?.$modal.msgWarning("请先选择要打印吊牌的商品")
    return
  }

  // 获取选中商品的详细信息和SKU列表
  printList.value = []
  for (const goodsId of ids.value) {
    const res = await getGoods(goodsId)
    if (res.data && res.data.skuList) {
      res.data.skuList.forEach((sku: any) => {
        printList.value.push({
          goodsId: res.data.goodsId,
          goodsCode: res.data.goodsCode,
          goodsName: res.data.goodsName,
          brandName: res.data.brandName,
          skuId: sku.skuId,
          skuCode: sku.skuCode,
          barcode: sku.barcode,
          color: sku.color,
          colorName: colorCodeMap.value.get(sku.color) || sku.color,
          size: sku.size,
          sizeName: sizeCodeMap.value.get(sku.size) || sku.size,
          salePrice: sku.salePrice || res.data.salePrice,
          printCount: 1
        })
      })
    }
  }

  if (printList.value.length === 0) {
    proxy?.$modal.msgWarning("选中的商品没有SKU数据")
    return
  }

  printStatus.value = "idle"
  printProgress.value = 0
  printOpen.value = true
}

// 批量设置打印数量
const applyBatchPrintCount = () => {
  printList.value.forEach(item => {
    item.printCount = batchPrintCount.value
  })
}

// 生成吊牌 HTML
const generateTagHTML = (item: any): string => {
  return `
    <div style="width: 80mm; padding: 3mm; font-family: 'Microsoft YaHei', sans-serif; text-align: center;">
      <div style="font-size: 14px; font-weight: bold; margin-bottom: 2mm;">${item.brandName || ''}</div>
      <div style="font-size: 12px; margin-bottom: 2mm;">${item.goodsName}</div>
      <div style="font-size: 10px; margin-bottom: 2mm;">
        <span>颜色: ${item.colorName || '-'}</span>
        <span style="margin-left: 3mm;">尺码: ${item.sizeName || '-'}</span>
      </div>
      <div style="font-size: 10px; font-family: monospace; margin-bottom: 2mm;">${item.barcode || item.skuCode}</div>
      <div style="font-size: 16px; font-weight: bold; color: #c00;">¥${item.salePrice || 0}</div>
    </div>
  `
}

// 执行打印
const executePrint = async () => {
  const toPrintItems = printList.value.filter(item => item.printCount > 0)
  
  if (toPrintItems.length === 0) {
    proxy?.$modal.msgWarning("请设置打印数量")
    return
  }

  // 连接打印服务
  const connected = await connectPrinter()
  if (!connected) return

  printStatus.value = "printing"
  printProgress.value = 0

  let currentPrintIndex = 0
  let successCount = 0
  let failCount = 0

  // 递归打印函数
  const printNext = () => {
    if (currentPrintIndex >= toPrintItems.length) {
      // 打印完成
      printStatus.value = "done"
      printSocket?.disconnect()
      proxy?.$modal.msgSuccess(`打印完成！成功 ${successCount} 张，失败 ${failCount} 张`)
      return
    }

    const item = toPrintItems[currentPrintIndex]
    const printCount = item.printCount
    let itemPrinted = 0

    // 打印单个SKU的多个吊牌
    const printItem = () => {
      if (itemPrinted >= printCount) {
        currentPrintIndex++
        setTimeout(printNext, 500)  // 每个SKU之间间隔500ms
        return
      }

      const html = generateTagHTML(item)
      
      printSocket?.emit('print', {
        id: `${item.skuCode}-${Date.now()}-${itemPrinted}`,
        content: html,
        options: { width: 80, height: 50 }
      })

      itemPrinted++
      printProgress.value++
      successCount++

      // 每张吊牌之间间隔300ms
      setTimeout(printItem, 300)
    }

    printItem()
  }

  printNext()
}

// 初始化
loadColorOptions()
loadSizeOptions()
getCategoryList()
getBrandList()
getList()
</script>
