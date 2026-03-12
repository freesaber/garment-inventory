<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="商品编码" prop="goodsCode">
        <el-input
          v-model="queryParams.goodsCode"
          placeholder="请输入商品编码"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="商品名称" prop="goodsName">
        <el-input
          v-model="queryParams.goodsName"
          placeholder="请输入商品名称"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="分类" prop="categoryId">
        <el-tree-select
          v-model="queryParams.categoryId"
          :data="categoryOptions"
          :props="{ value: 'id', label: 'label', children: 'children' }"
          value-key="id"
          placeholder="请选择分类"
          clearable
          style="width: 200px"
        />
      </el-form-item>
      <el-form-item label="品牌" prop="brandId">
        <el-select v-model="queryParams.brandId" placeholder="请选择品牌" clearable style="width: 200px">
          <el-option
            v-for="brand in brandOptions"
            :key="brand.brandId"
            :label="brand.brandName"
            :value="brand.brandId"
          />
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
        <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['inventory:goods:export']">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" :columns="columns"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="goodsList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="商品编码" align="center" prop="goodsCode" width="120" :show-overflow-tooltip="true" />
      <el-table-column label="商品名称" align="center" prop="goodsName" :show-overflow-tooltip="true" />
      <el-table-column label="分类" align="center" prop="categoryName" width="100" />
      <el-table-column label="品牌" align="center" prop="brandName" width="100" />
      <el-table-column label="季节" align="center" prop="season" width="80" />
      <el-table-column label="年份" align="center" prop="year" width="80" />
      <el-table-column label="进货价" align="center" prop="purchasePrice" width="100">
        <template #default="scope">
          <span>{{ scope.row.purchasePrice ? '¥' + scope.row.purchasePrice : '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="销售价" align="center" prop="salePrice" width="100">
        <template #default="scope">
          <span>{{ scope.row.salePrice ? '¥' + scope.row.salePrice : '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" width="80">
        <template #default="scope">
          <el-switch
            v-model="scope.row.status"
            active-value="0"
            inactive-value="1"
            @change="handleStatusChange(scope.row)"
          ></el-switch>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="150">
        <template #default="scope">
          <el-tooltip content="修改" placement="top">
            <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['inventory:goods:edit']"></el-button>
          </el-tooltip>
          <el-tooltip content="删除" placement="top">
            <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['inventory:goods:remove']"></el-button>
          </el-tooltip>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改商品对话框 -->
    <el-dialog :title="title" v-model="open" width="800px" append-to-body>
      <el-form :model="form" :rules="rules" ref="goodsRef" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="商品编码" prop="goodsCode">
              <el-input v-model="form.goodsCode" placeholder="请输入商品编码" maxlength="50" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="商品名称" prop="goodsName">
              <el-input v-model="form.goodsName" placeholder="请输入商品名称" maxlength="100" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="商品分类" prop="categoryId">
              <el-tree-select
                v-model="form.categoryId"
                :data="categoryOptions"
                :props="{ value: 'id', label: 'label', children: 'children' }"
                value-key="id"
                placeholder="请选择分类"
                clearable
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="品牌" prop="brandId">
              <el-select v-model="form.brandId" placeholder="请选择品牌" clearable>
                <el-option
                  v-for="brand in brandOptions"
                  :key="brand.brandId"
                  :label="brand.brandName"
                  :value="brand.brandId"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="季节" prop="season">
              <el-select v-model="form.season" placeholder="请选择季节">
                <el-option label="春" value="春" />
                <el-option label="夏" value="夏" />
                <el-option label="秋" value="秋" />
                <el-option label="冬" value="冬" />
                <el-option label="四季" value="四季" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="年份" prop="year">
              <el-date-picker
                v-model="form.year"
                type="year"
                placeholder="选择年份"
                value-format="YYYY"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <el-form-item label="单位" prop="unit">
              <el-input v-model="form.unit" placeholder="请输入单位" maxlength="10" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="进货价" prop="purchasePrice">
              <el-input-number v-model="form.purchasePrice" :precision="2" :min="0" placeholder="进货价" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="销售价" prop="salePrice">
              <el-input-number v-model="form.salePrice" :precision="2" :min="0" placeholder="销售价" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio label="0">正常</el-radio>
                <el-radio label="1">停用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" maxlength="500" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Goods" lang="ts">
import { listGoods, getGoods, delGoods, addGoods, updateGoods } from "@/api/inventory/goods";
import { treeselect as categoryTreeselect } from "@/api/inventory/category";
import { listBrandAll } from "@/api/inventory/brand";

const { proxy } = getCurrentInstance() as ComponentInternalInstance;

const goodsList = ref<any[]>([]);
const categoryOptions = ref<any[]>([]);
const brandOptions = ref<any[]>([]);
const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<number[]>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");

const columns = ref([
  { visible: true, key: 'goodsCode' },
  { visible: true, key: 'goodsName' },
  { visible: true, key: 'categoryName' },
  { visible: true, key: 'brandName' },
  { visible: true, key: 'season' },
  { visible: true, key: 'year' },
  { visible: true, key: 'purchasePrice' },
  { visible: true, key: 'salePrice' },
  { visible: true, key: 'status' },
  { visible: true, key: 'createTime' },
]);

const data = reactive<{
  form: any;
  queryParams: any;
  rules: any;
}>({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    goodsCode: undefined,
    goodsName: undefined,
    categoryId: undefined,
    brandId: undefined,
    status: undefined,
  },
  rules: {
    goodsCode: [{ required: true, message: "商品编码不能为空", trigger: "blur" }],
    goodsName: [{ required: true, message: "商品名称不能为空", trigger: "blur" }],
    categoryId: [{ required: true, message: "商品分类不能为空", trigger: "change" }],
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询商品列表 */
const getList = async () => {
  loading.value = true;
  const res = await listGoods(queryParams.value);
  goodsList.value = res.rows;
  total.value = res.total;
  loading.value = false;
};

/** 查询分类下拉树结构 */
const getCategoryTreeselect = async () => {
  const res = await categoryTreeselect();
  categoryOptions.value = res.data;
};

/** 查询品牌列表 */
const getBrandList = async () => {
  const res = await listBrandAll();
  brandOptions.value = res.data || [];
};

/** 取消按钮 */
const cancel = () => {
  open.value = false;
  reset();
};

/** 表单重置 */
const reset = () => {
  form.value = {
    goodsId: undefined,
    goodsCode: undefined,
    goodsName: undefined,
    categoryId: undefined,
    brandId: undefined,
    season: undefined,
    year: undefined,
    unit: "件",
    purchasePrice: undefined,
    salePrice: undefined,
    status: "0",
    remark: undefined,
  };
  proxy?.resetForm("goodsRef");
};

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.value.pageNum = 1;
  getList();
};

/** 重置按钮操作 */
const resetQuery = () => {
  proxy?.resetForm("queryRef");
  handleQuery();
};

/** 多选框选中数据 */
const handleSelectionChange = (selection: any[]) => {
  ids.value = selection.map(item => item.goodsId);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
};

/** 新增按钮操作 */
const handleAdd = () => {
  reset();
  open.value = true;
  title.value = "添加商品";
};

/** 修改按钮操作 */
const handleUpdate = async (row: any) => {
  reset();
  const goodsId = row.goodsId || ids.value[0];
  const res = await getGoods(goodsId);
  form.value = res.data;
  open.value = true;
  title.value = "修改商品";
};

/** 提交按钮 */
const submitForm = () => {
  (proxy?.$refs["goodsRef"] as any).validate(async (valid: boolean) => {
    if (valid) {
      if (form.value.goodsId != undefined) {
        await updateGoods(form.value);
        proxy?.$modal.msgSuccess("修改成功");
        open.value = false;
        getList();
      } else {
        await addGoods(form.value);
        proxy?.$modal.msgSuccess("新增成功");
        open.value = false;
        getList();
      }
    }
  });
};

/** 删除按钮操作 */
const handleDelete = async (row: any) => {
  const goodsIds = row.goodsId || ids.value;
  await proxy?.$modal.confirm('是否确认删除商品编号为"' + goodsIds + '"的数据项？');
  await delGoods(goodsIds);
  getList();
  proxy?.$modal.msgSuccess("删除成功");
};

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.$download.name(
    "goods",
    queryParams.value
  );
};

/** 商品状态修改 */
const handleStatusChange = async (row: any) => {
  const text = row.status === "0" ? "启用" : "停用";
  try {
    await proxy?.$modal.confirm('确认要"' + text + '""' + row.goodsName + '"商品吗？');
    await updateGoods({ goodsId: row.goodsId, status: row.status });
    proxy?.$modal.msgSuccess(text + "成功");
  } catch {
    row.status = row.status === "0" ? "1" : "0";
  }
};

getCategoryTreeselect();
getBrandList();
getList();
</script>
