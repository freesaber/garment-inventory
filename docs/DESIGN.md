# 服装进销存系统 - 设计文档

## 一、数据隔离方案

### 部门 → 店铺映射
```
若依部门结构 → 业务结构
├── 总部 (dept_id=100)      → 总部/总仓
│   ├── 华北区 (dept_id=200) → 区域
│   │   ├── 北京店 (dept_id=201) → 门店
│   │   └── 天津店 (dept_id=202) → 门店
│   ├── 华东区 (dept_id=300) → 区域
│   │   ├── 上海店 (dept_id=301) → 门店
│   │   └── 南京店 (dept_id=302) → 门店
│   └── ...
```

### 数据权限规则
- 总部用户：查看所有数据
- 区域用户：查看本区域及下级店铺数据
- 店铺用户：仅查看本店铺数据

## 二、数据库设计

### 2.1 商品管理

```sql
-- 商品分类
CREATE TABLE gi_category (
    category_id   BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
    parent_id     BIGINT DEFAULT 0 COMMENT '父分类ID',
    category_name VARCHAR(50) NOT NULL COMMENT '分类名称',
    sort          INT DEFAULT 0 COMMENT '排序',
    status        CHAR(1) DEFAULT '0' COMMENT '状态(0正常 1停用)',
    create_by     VARCHAR(64) COMMENT '创建者',
    create_time   DATETIME COMMENT '创建时间',
    update_by     VARCHAR(64) COMMENT '更新者',
    update_time   DATETIME COMMENT '更新时间',
    remark        VARCHAR(500) COMMENT '备注'
) COMMENT '商品分类表';

-- 商品品牌
CREATE TABLE gi_brand (
    brand_id      BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '品牌ID',
    brand_name    VARCHAR(50) NOT NULL COMMENT '品牌名称',
    brand_logo    VARCHAR(200) COMMENT '品牌Logo',
    sort          INT DEFAULT 0 COMMENT '排序',
    status        CHAR(1) DEFAULT '0' COMMENT '状态',
    create_by     VARCHAR(64) COMMENT '创建者',
    create_time   DATETIME COMMENT '创建时间',
    update_by     VARCHAR(64) COMMENT '更新者',
    update_time   DATETIME COMMENT '更新时间'
) COMMENT '商品品牌表';

-- 商品SPU（标准化产品单元）
CREATE TABLE gi_goods (
    goods_id      BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '商品ID',
    goods_code    VARCHAR(50) NOT NULL COMMENT '商品编码',
    goods_name    VARCHAR(100) NOT NULL COMMENT '商品名称',
    category_id   BIGINT COMMENT '分类ID',
    brand_id      BIGINT COMMENT '品牌ID',
    season        VARCHAR(20) COMMENT '季节(春夏秋冬)',
    year          INT COMMENT '年份',
    unit          VARCHAR(10) DEFAULT '件' COMMENT '单位',
    purchase_price DECIMAL(10,2) COMMENT '进货价',
    sale_price    DECIMAL(10,2) COMMENT '销售价',
    status        CHAR(1) DEFAULT '0' COMMENT '状态',
    main_image    VARCHAR(200) COMMENT '主图',
    images        TEXT COMMENT '商品图片(JSON)',
    create_by     VARCHAR(64) COMMENT '创建者',
    create_time   DATETIME COMMENT '创建时间',
    update_by     VARCHAR(64) COMMENT '更新者',
    update_time   DATETIME COMMENT '更新时间',
    remark        VARCHAR(500) COMMENT '备注',
    UNIQUE KEY uk_goods_code (goods_code)
) COMMENT '商品SPU表';

-- 商品SKU（库存单元，按颜色尺码）
CREATE TABLE gi_sku (
    sku_id        BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'SKUID',
    goods_id      BIGINT NOT NULL COMMENT '商品ID',
    sku_code      VARCHAR(50) NOT NULL COMMENT 'SKU编码',
    color         VARCHAR(20) COMMENT '颜色',
    size          VARCHAR(20) COMMENT '尺码',
    barcode       VARCHAR(50) COMMENT '条码',
    price         DECIMAL(10,2) COMMENT '售价',
    status        CHAR(1) DEFAULT '0' COMMENT '状态',
    image         VARCHAR(200) COMMENT 'SKU图片',
    create_by     VARCHAR(64) COMMENT '创建者',
    create_time   DATETIME COMMENT '创建时间',
    update_by     VARCHAR(64) COMMENT '更新者',
    update_time   DATETIME COMMENT '更新时间',
    UNIQUE KEY uk_sku_code (sku_code),
    KEY idx_goods_id (goods_id)
) COMMENT '商品SKU表';
```

### 2.2 库存管理

```sql
-- 店铺库存（关联部门）
CREATE TABLE gi_stock (
    stock_id      BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '库存ID',
    dept_id       BIGINT NOT NULL COMMENT '店铺ID(部门ID)',
    sku_id        BIGINT NOT NULL COMMENT 'SKUID',
    quantity      INT DEFAULT 0 COMMENT '库存数量',
    locked_qty    INT DEFAULT 0 COMMENT '锁定数量',
    warning_qty   INT DEFAULT 10 COMMENT '预警数量',
    create_time   DATETIME COMMENT '创建时间',
    update_time   DATETIME COMMENT '更新时间',
    UNIQUE KEY uk_dept_sku (dept_id, sku_id),
    KEY idx_sku_id (sku_id)
) COMMENT '店铺库存表';

-- 库存流水
CREATE TABLE gi_stock_log (
    log_id        BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '流水ID',
    dept_id       BIGINT NOT NULL COMMENT '店铺ID',
    sku_id        BIGINT NOT NULL COMMENT 'SKUID',
    type          VARCHAR(20) NOT NULL COMMENT '类型(purchase进货/sale销售/transfer调货/adjust调整)',
    quantity      INT NOT NULL COMMENT '变动数量(正负)',
    before_qty    INT COMMENT '变动前数量',
    after_qty     INT COMMENT '变动后数量',
    biz_id        BIGINT COMMENT '业务ID(进货单/销售单/调货单ID)',
    biz_no        VARCHAR(50) COMMENT '业务单号',
    create_by     VARCHAR(64) COMMENT '操作人',
    create_time   DATETIME COMMENT '创建时间',
    remark        VARCHAR(500) COMMENT '备注',
    KEY idx_dept_sku (dept_id, sku_id),
    KEY idx_create_time (create_time)
) COMMENT '库存流水表';
```

### 2.3 进货管理

```sql
-- 进货单
CREATE TABLE gi_purchase (
    purchase_id   BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '进货单ID',
    purchase_no   VARCHAR(50) NOT NULL COMMENT '进货单号',
    dept_id       BIGINT NOT NULL COMMENT '店铺ID',
    supplier_id   BIGINT COMMENT '供应商ID',
    total_qty     INT DEFAULT 0 COMMENT '总数量',
    total_amount  DECIMAL(12,2) DEFAULT 0 COMMENT '总金额',
    status        CHAR(1) DEFAULT '0' COMMENT '状态(0待审核 1已入库 2已拒绝)',
    audit_by      VARCHAR(64) COMMENT '审核人',
    audit_time    DATETIME COMMENT '审核时间',
    create_by     VARCHAR(64) COMMENT '创建人',
    create_time   DATETIME COMMENT '创建时间',
    update_by     VARCHAR(64) COMMENT '更新人',
    update_time   DATETIME COMMENT '更新时间',
    remark        VARCHAR(500) COMMENT '备注',
    UNIQUE KEY uk_purchase_no (purchase_no)
) COMMENT '进货单表';

-- 进货单明细
CREATE TABLE gi_purchase_item (
    item_id       BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '明细ID',
    purchase_id   BIGINT NOT NULL COMMENT '进货单ID',
    sku_id        BIGINT NOT NULL COMMENT 'SKUID',
    quantity      INT NOT NULL COMMENT '数量',
    price         DECIMAL(10,2) COMMENT '进货价',
    amount        DECIMAL(12,2) COMMENT '金额',
    create_time   DATETIME COMMENT '创建时间',
    KEY idx_purchase_id (purchase_id)
) COMMENT '进货单明细表';

-- 供应商
CREATE TABLE gi_supplier (
    supplier_id   BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '供应商ID',
    supplier_name VARCHAR(100) NOT NULL COMMENT '供应商名称',
    contact       VARCHAR(50) COMMENT '联系人',
    phone         VARCHAR(20) COMMENT '电话',
    address       VARCHAR(200) COMMENT '地址',
    status        CHAR(1) DEFAULT '0' COMMENT '状态',
    create_by     VARCHAR(64) COMMENT '创建者',
    create_time   DATETIME COMMENT '创建时间',
    update_by     VARCHAR(64) COMMENT '更新者',
    update_time   DATETIME COMMENT '更新时间',
    remark        VARCHAR(500) COMMENT '备注'
) COMMENT '供应商表';
```

### 2.4 销售管理

```sql
-- 销售单
CREATE TABLE gi_sale (
    sale_id       BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '销售单ID',
    sale_no       VARCHAR(50) NOT NULL COMMENT '销售单号',
    dept_id       BIGINT NOT NULL COMMENT '店铺ID',
    member_id     BIGINT COMMENT '会员ID',
    total_qty     INT DEFAULT 0 COMMENT '总数量',
    total_amount  DECIMAL(12,2) DEFAULT 0 COMMENT '总金额',
    discount_amount DECIMAL(12,2) DEFAULT 0 COMMENT '优惠金额',
    pay_amount    DECIMAL(12,2) DEFAULT 0 COMMENT '实付金额',
    pay_type      VARCHAR(20) COMMENT '支付方式(cash现金/wechat微信/alipay支付宝)',
    status        CHAR(1) DEFAULT '0' COMMENT '状态(0待付款 1已完成 2已退款)',
    create_by     VARCHAR(64) COMMENT '收银员',
    create_time   DATETIME COMMENT '销售时间',
    remark        VARCHAR(500) COMMENT '备注',
    UNIQUE KEY uk_sale_no (sale_no),
    KEY idx_dept_time (dept_id, create_time)
) COMMENT '销售单表';

-- 销售单明细
CREATE TABLE gi_sale_item (
    item_id       BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '明细ID',
    sale_id       BIGINT NOT NULL COMMENT '销售单ID',
    sku_id        BIGINT NOT NULL COMMENT 'SKUID',
    goods_name    VARCHAR(100) COMMENT '商品名称',
    color         VARCHAR(20) COMMENT '颜色',
    size          VARCHAR(20) COMMENT '尺码',
    quantity      INT NOT NULL COMMENT '数量',
    price         DECIMAL(10,2) COMMENT '单价',
    discount_rate DECIMAL(5,2) DEFAULT 100 COMMENT '折扣率',
    amount        DECIMAL(12,2) COMMENT '金额',
    create_time   DATETIME COMMENT '创建时间',
    KEY idx_sale_id (sale_id)
) COMMENT '销售单明细表';

-- 会员
CREATE TABLE gi_member (
    member_id     BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '会员ID',
    member_no     VARCHAR(50) NOT NULL COMMENT '会员卡号',
    member_name   VARCHAR(50) COMMENT '会员姓名',
    phone         VARCHAR(20) COMMENT '手机号',
    gender        CHAR(1) COMMENT '性别',
    birthday      DATE COMMENT '生日',
    points        INT DEFAULT 0 COMMENT '积分',
    balance       DECIMAL(10,2) DEFAULT 0 COMMENT '余额',
    level         VARCHAR(20) COMMENT '等级',
    status        CHAR(1) DEFAULT '0' COMMENT '状态',
    create_time   DATETIME COMMENT '创建时间',
    update_time   DATETIME COMMENT '更新时间',
    remark        VARCHAR(500) COMMENT '备注',
    UNIQUE KEY uk_member_no (member_no),
    UNIQUE KEY uk_phone (phone)
) COMMENT '会员表';
```

### 2.5 调货管理

```sql
-- 调货单
CREATE TABLE gi_transfer (
    transfer_id   BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '调货单ID',
    transfer_no   VARCHAR(50) NOT NULL COMMENT '调货单号',
    out_dept_id   BIGINT NOT NULL COMMENT '调出店铺ID',
    in_dept_id    BIGINT NOT NULL COMMENT '调入店铺ID',
    total_qty     INT DEFAULT 0 COMMENT '总数量',
    status        CHAR(1) DEFAULT '0' COMMENT '状态(0待审核 1待出库 2在途 3已入库 4已拒绝)',
    audit_by      VARCHAR(64) COMMENT '审核人',
    audit_time    DATETIME COMMENT '审核时间',
    out_by        VARCHAR(64) COMMENT '出库人',
    out_time      DATETIME COMMENT '出库时间',
    in_by         VARCHAR(64) COMMENT '入库人',
    in_time       DATETIME COMMENT '入库时间',
    create_by     VARCHAR(64) COMMENT '申请人',
    create_time   DATETIME COMMENT '申请时间',
    remark        VARCHAR(500) COMMENT '备注',
    UNIQUE KEY uk_transfer_no (transfer_no)
) COMMENT '调货单表';

-- 调货单明细
CREATE TABLE gi_transfer_item (
    item_id       BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '明细ID',
    transfer_id   BIGINT NOT NULL COMMENT '调货单ID',
    sku_id        BIGINT NOT NULL COMMENT 'SKUID',
    quantity      INT NOT NULL COMMENT '调货数量',
    out_qty       INT DEFAULT 0 COMMENT '出库数量',
    in_qty        INT DEFAULT 0 COMMENT '入库数量',
    create_time   DATETIME COMMENT '创建时间',
    KEY idx_transfer_id (transfer_id)
) COMMENT '调货单明细表';
```

## 三、开发计划

### Phase 1: 基础模块（1周）
- [x] 若依框架部署
- [ ] 商品分类管理
- [ ] 商品品牌管理
- [ ] 商品SPU/SKU管理

### Phase 2: 库存模块（1周）
- [ ] 店铺库存管理
- [ ] 库存查询
- [ ] 库存预警
- [ ] 库存盘点

### Phase 3: 进货模块（1周）
- [ ] 供应商管理
- [ ] 进货单管理
- [ ] 进货审核
- [ ] 入库处理

### Phase 4: 销售模块（1周）
- [ ] 会员管理
- [ ] POS收银
- [ ] 销售查询
- [ ] 销售统计

### Phase 5: 调货模块（1周）
- [ ] 调货申请
- [ ] 调货审核
- [ ] 调货出库
- [ ] 调货入库

### Phase 6: 报表模块（1周）
- [ ] 销售报表
- [ ] 库存报表
- [ ] 进货报表
- [ ] 利润分析
