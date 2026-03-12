<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="品牌名称" prop="brandName">
        <el-input
          v-model="queryParams.brandName"
          placeholder="请输入品牌名称"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="品牌状态" clearable style="width: 200px">
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
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['inventory:brand:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['inventory:brand:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['inventory:brand:remove']">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="brandList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="品牌ID" align="center" prop="brandId" width="80" />
      <el-table-column label="品牌名称" align="center" prop="brandName" :show-overflow-tooltip="true" />
      <el-table-column label="品牌Logo" align="center" prop="brandLogo" width="100">
        <template #default="scope">
          <el-image
            v-if="scope.row.brandLogo"
            :src="scope.row.brandLogo"
            :preview-src-list="[scope.row.brandLogo]"
            fit="contain"
            style="width: 60px; height: 40px"
          />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="排序" align="center" prop="sort" width="80" />
      <el-table-column label="状态" align="center" prop="status" width="80">
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
            <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['inventory:brand:edit']"></el-button>
          </el-tooltip>
          <el-tooltip content="删除" placement="top">
            <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['inventory:brand:remove']"></el-button>
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

    <!-- 添加或修改品牌对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form :model="form" :rules="rules" ref="brandRef" label-width="80px">
        <el-form-item label="品牌名称" prop="brandName">
          <el-input v-model="form.brandName" placeholder="请输入品牌名称" maxlength="50" />
        </el-form-item>
        <el-form-item label="品牌Logo" prop="brandLogo">
          <el-input v-model="form.brandLogo" placeholder="请输入品牌Logo地址" maxlength="200" />
        </el-form-item>
        <el-form-item label="显示排序" prop="sort">
          <el-input-number v-model="form.sort" controls-position="right" :min="0" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="0">正常</el-radio>
            <el-radio label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
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

<script setup name="Brand" lang="ts">
import { listBrand, getBrand, delBrand, addBrand, updateBrand } from "@/api/inventory/brand";

const { proxy } = getCurrentInstance() as ComponentInternalInstance;

const brandList = ref<any[]>([]);
const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<number[]>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");

const data = reactive<{
  form: any;
  queryParams: any;
  rules: any;
}>({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    brandName: undefined,
    status: undefined,
  },
  rules: {
    brandName: [{ required: true, message: "品牌名称不能为空", trigger: "blur" }],
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询品牌列表 */
const getList = async () => {
  loading.value = true;
  const res = await listBrand(queryParams.value);
  brandList.value = res.rows;
  total.value = res.total;
  loading.value = false;
};

/** 取消按钮 */
const cancel = () => {
  open.value = false;
  reset();
};

/** 表单重置 */
const reset = () => {
  form.value = {
    brandId: undefined,
    brandName: undefined,
    brandLogo: undefined,
    sort: 0,
    status: "0",
  };
  proxy?.resetForm("brandRef");
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
  ids.value = selection.map(item => item.brandId);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
};

/** 新增按钮操作 */
const handleAdd = () => {
  reset();
  open.value = true;
  title.value = "添加品牌";
};

/** 修改按钮操作 */
const handleUpdate = async (row: any) => {
  reset();
  const brandId = row.brandId || ids.value[0];
  const res = await getBrand(brandId);
  form.value = res.data;
  open.value = true;
  title.value = "修改品牌";
};

/** 提交按钮 */
const submitForm = () => {
  (proxy?.$refs["brandRef"] as any).validate(async (valid: boolean) => {
    if (valid) {
      if (form.value.brandId != undefined) {
        await updateBrand(form.value);
        proxy?.$modal.msgSuccess("修改成功");
        open.value = false;
        getList();
      } else {
        await addBrand(form.value);
        proxy?.$modal.msgSuccess("新增成功");
        open.value = false;
        getList();
      }
    }
  });
};

/** 删除按钮操作 */
const handleDelete = async (row: any) => {
  const brandIds = row.brandId || ids.value;
  await proxy?.$modal.confirm('是否确认删除品牌编号为"' + brandIds + '"的数据项？');
  await delBrand(brandIds);
  getList();
  proxy?.$modal.msgSuccess("删除成功");
};

/** 品牌状态修改 */
const handleStatusChange = async (row: any) => {
  const text = row.status === "0" ? "启用" : "停用";
  try {
    await proxy?.$modal.confirm('确认要"' + text + '""' + row.brandName + '"品牌吗？');
    await updateBrand({ brandId: row.brandId, status: row.status });
    proxy?.$modal.msgSuccess(text + "成功");
  } catch {
    row.status = row.status === "0" ? "1" : "0";
  }
};

getList();
</script>
