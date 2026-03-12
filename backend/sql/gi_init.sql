-- =============================================
-- 服装进销存系统 - 数据库初始化脚本
-- 基于若依框架，使用部门实现店铺数据隔离
-- =============================================

-- ----------------------------
-- 1. 商品分类表
-- ----------------------------
DROP TABLE IF EXISTS gi_category;
CREATE TABLE gi_category (
    category_id   BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
    parent_id     BIGINT DEFAULT 0 COMMENT '父分类ID',
    category_name VARCHAR(50) NOT NULL COMMENT '分类名称',
    sort          INT DEFAULT 0 COMMENT '排序',
    status        CHAR(1) DEFAULT '0' COMMENT '状态(0正常 1停用)',
    del_flag      CHAR(1) DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
    create_by     VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time   DATETIME COMMENT '创建时间',
    update_by     VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time   DATETIME COMMENT '更新时间',
    remark        VARCHAR(500) COMMENT '备注'
) ENGINE=InnoDB AUTO_INCREMENT=1000 COMMENT='商品分类表';

-- 初始化分类数据
INSERT INTO gi_category (category_id, parent_id, category_name, sort, status, create_time) VALUES
(1, 0, '女装', 1, '0', NOW()),
(2, 0, '男装', 2, '0', NOW()),
(3, 0, '童装', 3, '0', NOW()),
(4, 0, '配饰', 4, '0', NOW()),
(101, 1, 'T恤', 1, '0', NOW()),
(102, 1, '衬衫', 2, '0', NOW()),
(103, 1, '连衣裙', 3, '0', NOW()),
(104, 1, '外套', 4, '0', NOW()),
(105, 1, '裤子', 5, '0', NOW()),
(201, 2, 'T恤', 1, '0', NOW()),
(202, 2, '衬衫', 2, '0', NOW()),
(203, 2, '外套', 3, '0', NOW()),
(204, 2, '裤子', 4, '0', NOW());

-- ----------------------------
-- 2. 商品品牌表
-- ----------------------------
DROP TABLE IF EXISTS gi_brand;
CREATE TABLE gi_brand (
    brand_id      BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '品牌ID',
    brand_name    VARCHAR(50) NOT NULL COMMENT '品牌名称',
    brand_logo    VARCHAR(200) COMMENT '品牌Logo',
    sort          INT DEFAULT 0 COMMENT '排序',
    status        CHAR(1) DEFAULT '0' COMMENT '状态(0正常 1停用)',
    del_flag      CHAR(1) DEFAULT '0' COMMENT '删除标志',
    create_by     VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time   DATETIME COMMENT '创建时间',
    update_by     VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time   DATETIME COMMENT '更新时间'
) ENGINE=InnoDB AUTO_INCREMENT=1000 COMMENT='商品品牌表';

-- 初始化品牌数据
INSERT INTO gi_brand (brand_id, brand_name, sort, status, create_time) VALUES
(1, 'ZARA', 1, '0', NOW()),
(2, 'H&M', 2, '0', NOW()),
(3, '优衣库', 3, '0', NOW()),
(4, 'GAP', 4, '0', NOW()),
(5, '海澜之家', 5, '0', NOW());

-- ----------------------------
-- 3. 商品SPU表
-- ----------------------------
DROP TABLE IF EXISTS gi_goods;
CREATE TABLE gi_goods (
    goods_id      BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '商品ID',
    goods_code    VARCHAR(50) NOT NULL COMMENT '商品编码',
    goods_name    VARCHAR(100) NOT NULL COMMENT '商品名称',
    category_id   BIGINT COMMENT '分类ID',
    brand_id      BIGINT COMMENT '品牌ID',
    season        VARCHAR(20) COMMENT '季节(春/夏/秋/冬/四季)',
    year          INT COMMENT '年份',
    unit          VARCHAR(10) DEFAULT '件' COMMENT '单位',
    purchase_price DECIMAL(10,2) COMMENT '进货价',
    sale_price    DECIMAL(10,2) COMMENT '销售价',
    status        CHAR(1) DEFAULT '0' COMMENT '状态(0正常 1停用)',
    del_flag      CHAR(1) DEFAULT '0' COMMENT '删除标志',
    main_image    VARCHAR(200) COMMENT '主图',
    images        TEXT COMMENT '商品图片(JSON)',
    create_by     VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time   DATETIME COMMENT '创建时间',
    update_by     VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time   DATETIME COMMENT '更新时间',
    remark        VARCHAR(500) COMMENT '备注',
    UNIQUE KEY uk_goods_code (goods_code)
) ENGINE=InnoDB AUTO_INCREMENT=1000 COMMENT='商品SPU表';

-- ----------------------------
-- 4. 商品SKU表
-- ----------------------------
DROP TABLE IF EXISTS gi_sku;
CREATE TABLE gi_sku (
    sku_id        BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'SKUID',
    goods_id      BIGINT NOT NULL COMMENT '商品ID',
    sku_code      VARCHAR(50) NOT NULL COMMENT 'SKU编码',
    color         VARCHAR(20) COMMENT '颜色',
    size          VARCHAR(20) COMMENT '尺码',
    barcode       VARCHAR(50) COMMENT '条码',
    price         DECIMAL(10,2) COMMENT '售价',
    status        CHAR(1) DEFAULT '0' COMMENT '状态(0正常 1停用)',
    del_flag      CHAR(1) DEFAULT '0' COMMENT '删除标志',
    image         VARCHAR(200) COMMENT 'SKU图片',
    create_by     VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time   DATETIME COMMENT '创建时间',
    update_by     VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time   DATETIME COMMENT '更新时间',
    UNIQUE KEY uk_sku_code (sku_code),
    KEY idx_goods_id (goods_id),
    KEY idx_barcode (barcode)
) ENGINE=InnoDB AUTO_INCREMENT=1000 COMMENT='商品SKU表';

-- ----------------------------
-- 5. 店铺库存表
-- ----------------------------
DROP TABLE IF EXISTS gi_stock;
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
) ENGINE=InnoDB AUTO_INCREMENT=1000 COMMENT='店铺库存表';

-- ----------------------------
-- 6. 库存流水表
-- ----------------------------
DROP TABLE IF EXISTS gi_stock_log;
CREATE TABLE gi_stock_log (
    log_id        BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '流水ID',
    dept_id       BIGINT NOT NULL COMMENT '店铺ID',
    sku_id        BIGINT NOT NULL COMMENT 'SKUID',
    type          VARCHAR(20) NOT NULL COMMENT '类型(purchase进货/sale销售/transfer_out调出/transfer_in调入/adjust调整)',
    quantity      INT NOT NULL COMMENT '变动数量(正负)',
    before_qty    INT COMMENT '变动前数量',
    after_qty     INT COMMENT '变动后数量',
    biz_id        BIGINT COMMENT '业务ID',
    biz_no        VARCHAR(50) COMMENT '业务单号',
    create_by     VARCHAR(64) COMMENT '操作人',
    create_time   DATETIME COMMENT '创建时间',
    remark        VARCHAR(500) COMMENT '备注',
    KEY idx_dept_sku (dept_id, sku_id),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB AUTO_INCREMENT=1000 COMMENT='库存流水表';

-- ----------------------------
-- 7. 供应商表
-- ----------------------------
DROP TABLE IF EXISTS gi_supplier;
CREATE TABLE gi_supplier (
    supplier_id   BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '供应商ID',
    supplier_name VARCHAR(100) NOT NULL COMMENT '供应商名称',
    contact       VARCHAR(50) COMMENT '联系人',
    phone         VARCHAR(20) COMMENT '电话',
    address       VARCHAR(200) COMMENT '地址',
    status        CHAR(1) DEFAULT '0' COMMENT '状态(0正常 1停用)',
    del_flag      CHAR(1) DEFAULT '0' COMMENT '删除标志',
    create_by     VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time   DATETIME COMMENT '创建时间',
    update_by     VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time   DATETIME COMMENT '更新时间',
    remark        VARCHAR(500) COMMENT '备注'
) ENGINE=InnoDB AUTO_INCREMENT=1000 COMMENT='供应商表';

-- 初始化供应商
INSERT INTO gi_supplier (supplier_id, supplier_name, contact, phone, status, create_time) VALUES
(1, '广州服装批发商', '张经理', '13800138001', '0', NOW()),
(2, '杭州服饰有限公司', '李总', '13800138002', '0', NOW()),
(3, '上海时尚供应链', '王经理', '13800138003', '0', NOW());

-- ----------------------------
-- 8. 进货单表
-- ----------------------------
DROP TABLE IF EXISTS gi_purchase;
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
    del_flag      CHAR(1) DEFAULT '0' COMMENT '删除标志',
    create_by     VARCHAR(64) DEFAULT '' COMMENT '创建人',
    create_time   DATETIME COMMENT '创建时间',
    update_by     VARCHAR(64) DEFAULT '' COMMENT '更新人',
    update_time   DATETIME COMMENT '更新时间',
    remark        VARCHAR(500) COMMENT '备注',
    UNIQUE KEY uk_purchase_no (purchase_no),
    KEY idx_dept_id (dept_id)
) ENGINE=InnoDB AUTO_INCREMENT=1000 COMMENT='进货单表';

-- ----------------------------
-- 9. 进货单明细表
-- ----------------------------
DROP TABLE IF EXISTS gi_purchase_item;
CREATE TABLE gi_purchase_item (
    item_id       BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '明细ID',
    purchase_id   BIGINT NOT NULL COMMENT '进货单ID',
    sku_id        BIGINT NOT NULL COMMENT 'SKUID',
    quantity      INT NOT NULL COMMENT '数量',
    price         DECIMAL(10,2) COMMENT '进货价',
    amount        DECIMAL(12,2) COMMENT '金额',
    create_time   DATETIME COMMENT '创建时间',
    KEY idx_purchase_id (purchase_id)
) ENGINE=InnoDB AUTO_INCREMENT=1000 COMMENT='进货单明细表';

-- ----------------------------
-- 10. 会员表
-- ----------------------------
DROP TABLE IF EXISTS gi_member;
CREATE TABLE gi_member (
    member_id     BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '会员ID',
    member_no     VARCHAR(50) NOT NULL COMMENT '会员卡号',
    member_name   VARCHAR(50) COMMENT '会员姓名',
    phone         VARCHAR(20) COMMENT '手机号',
    gender        CHAR(1) COMMENT '性别(0男 1女 2未知)',
    birthday      DATE COMMENT '生日',
    points        INT DEFAULT 0 COMMENT '积分',
    balance       DECIMAL(10,2) DEFAULT 0 COMMENT '余额',
    level         VARCHAR(20) DEFAULT '普通会员' COMMENT '等级',
    status        CHAR(1) DEFAULT '0' COMMENT '状态(0正常 1停用)',
    del_flag      CHAR(1) DEFAULT '0' COMMENT '删除标志',
    create_time   DATETIME COMMENT '创建时间',
    update_time   DATETIME COMMENT '更新时间',
    remark        VARCHAR(500) COMMENT '备注',
    UNIQUE KEY uk_member_no (member_no),
    UNIQUE KEY uk_phone (phone)
) ENGINE=InnoDB AUTO_INCREMENT=1000 COMMENT='会员表';

-- ----------------------------
-- 11. 销售单表
-- ----------------------------
DROP TABLE IF EXISTS gi_sale;
CREATE TABLE gi_sale (
    sale_id       BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '销售单ID',
    sale_no       VARCHAR(50) NOT NULL COMMENT '销售单号',
    dept_id       BIGINT NOT NULL COMMENT '店铺ID',
    member_id     BIGINT COMMENT '会员ID',
    total_qty     INT DEFAULT 0 COMMENT '总数量',
    total_amount  DECIMAL(12,2) DEFAULT 0 COMMENT '总金额',
    discount_amount DECIMAL(12,2) DEFAULT 0 COMMENT '优惠金额',
    pay_amount    DECIMAL(12,2) DEFAULT 0 COMMENT '实付金额',
    pay_type      VARCHAR(20) COMMENT '支付方式(cash/wechat/alipay/card)',
    status        CHAR(1) DEFAULT '0' COMMENT '状态(0待付款 1已完成 2已退款)',
    del_flag      CHAR(1) DEFAULT '0' COMMENT '删除标志',
    create_by     VARCHAR(64) COMMENT '收银员',
    create_time   DATETIME COMMENT '销售时间',
    remark        VARCHAR(500) COMMENT '备注',
    UNIQUE KEY uk_sale_no (sale_no),
    KEY idx_dept_time (dept_id, create_time)
) ENGINE=InnoDB AUTO_INCREMENT=1000 COMMENT='销售单表';

-- ----------------------------
-- 12. 销售单明细表
-- ----------------------------
DROP TABLE IF EXISTS gi_sale_item;
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
) ENGINE=InnoDB AUTO_INCREMENT=1000 COMMENT='销售单明细表';

-- ----------------------------
-- 13. 调货单表
-- ----------------------------
DROP TABLE IF EXISTS gi_transfer;
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
    del_flag      CHAR(1) DEFAULT '0' COMMENT '删除标志',
    create_by     VARCHAR(64) DEFAULT '' COMMENT '申请人',
    create_time   DATETIME COMMENT '申请时间',
    remark        VARCHAR(500) COMMENT '备注',
    UNIQUE KEY uk_transfer_no (transfer_no),
    KEY idx_out_dept (out_dept_id),
    KEY idx_in_dept (in_dept_id)
) ENGINE=InnoDB AUTO_INCREMENT=1000 COMMENT='调货单表';

-- ----------------------------
-- 14. 调货单明细表
-- ----------------------------
DROP TABLE IF EXISTS gi_transfer_item;
CREATE TABLE gi_transfer_item (
    item_id       BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '明细ID',
    transfer_id   BIGINT NOT NULL COMMENT '调货单ID',
    sku_id        BIGINT NOT NULL COMMENT 'SKUID',
    quantity      INT NOT NULL COMMENT '调货数量',
    out_qty       INT DEFAULT 0 COMMENT '出库数量',
    in_qty        INT DEFAULT 0 COMMENT '入库数量',
    create_time   DATETIME COMMENT '创建时间',
    KEY idx_transfer_id (transfer_id)
) ENGINE=InnoDB AUTO_INCREMENT=1000 COMMENT='调货单明细表';

-- ----------------------------
-- 菜单配置
-- ----------------------------
-- 进销存一级菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) VALUES
('进销存管理', 0, 5, 'inventory', NULL, 1, 0, 'M', '0', '0', '', 'shopping', 'admin', NOW(), '进销存管理目录');

-- 获取进销存菜单ID
SET @inventory_menu_id = LAST_INSERT_ID();

-- 商品管理菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) VALUES
('商品管理', @inventory_menu_id, 1, 'goods', NULL, 1, 0, 'M', '0', '0', '', 'component', 'admin', NOW(), '商品管理目录');

SET @goods_menu_id = LAST_INSERT_ID();

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) VALUES
('商品分类', @goods_menu_id, 1, 'category', 'inventory/goods/category/index', 1, 0, 'C', '0', '0', 'inventory:category:list', 'tree', 'admin', NOW()),
('商品品牌', @goods_menu_id, 2, 'brand', 'inventory/goods/brand/index', 1, 0, 'C', '0', '0', 'inventory:brand:list', 'skill', 'admin', NOW()),
('商品管理', @goods_menu_id, 3, 'list', 'inventory/goods/list/index', 1, 0, 'C', '0', '0', 'inventory:goods:list', 'documentation', 'admin', NOW());

-- 库存管理菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) VALUES
('库存管理', @inventory_menu_id, 2, 'stock', NULL, 1, 0, 'M', '0', '0', '', 'tree-table', 'admin', NOW(), '库存管理目录');

SET @stock_menu_id = LAST_INSERT_ID();

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) VALUES
('库存查询', @stock_menu_id, 1, 'query', 'inventory/stock/query/index', 1, 0, 'C', '0', '0', 'inventory:stock:query', 'search', 'admin', NOW()),
('库存预警', @stock_menu_id, 2, 'warning', 'inventory/stock/warning/index', 1, 0, 'C', '0', '0', 'inventory:stock:warning', 'message', 'admin', NOW()),
('库存流水', @stock_menu_id, 3, 'log', 'inventory/stock/log/index', 1, 0, 'C', '0', '0', 'inventory:stock:log', 'log', 'admin', NOW());

-- 进货管理菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) VALUES
('进货管理', @inventory_menu_id, 3, 'purchase', NULL, 1, 0, 'M', '0', '0', '', 'build', 'admin', NOW(), '进货管理目录');

SET @purchase_menu_id = LAST_INSERT_ID();

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) VALUES
('供应商管理', @purchase_menu_id, 1, 'supplier', 'inventory/purchase/supplier/index', 1, 0, 'C', '0', '0', 'inventory:supplier:list', 'peoples', 'admin', NOW()),
('进货单', @purchase_menu_id, 2, 'order', 'inventory/purchase/order/index', 1, 0, 'C', '0', '0', 'inventory:purchase:list', 'form', 'admin', NOW());

-- 销售管理菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) VALUES
('销售管理', @inventory_menu_id, 4, 'sale', NULL, 1, 0, 'M', '0', '0', '', 'money', 'admin', NOW(), '销售管理目录');

SET @sale_menu_id = LAST_INSERT_ID();

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) VALUES
('会员管理', @sale_menu_id, 1, 'member', 'inventory/sale/member/index', 1, 0, 'C', '0', '0', 'inventory:member:list', 'user', 'admin', NOW()),
('销售单', @sale_menu_id, 2, 'order', 'inventory/sale/order/index', 1, 0, 'C', '0', '0', 'inventory:sale:list', 'list', 'admin', NOW()),
('POS收银', @sale_menu_id, 3, 'pos', 'inventory/sale/pos/index', 1, 0, 'C', '0', '0', 'inventory:pos:query', 'monitor', 'admin', NOW());

-- 调货管理菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) VALUES
('调货管理', @inventory_menu_id, 5, 'transfer', NULL, 1, 0, 'M', '0', '0', '', 'checkbox', 'admin', NOW(), '调货管理目录');

SET @transfer_menu_id = LAST_INSERT_ID();

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) VALUES
('调货单', @transfer_menu_id, 1, 'order', 'inventory/transfer/order/index', 1, 0, 'C', '0', '0', 'inventory:transfer:list', 'excel', 'admin', NOW());

-- 报表管理菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) VALUES
('报表分析', @inventory_menu_id, 6, 'report', NULL, 1, 0, 'M', '0', '0', '', 'chart', 'admin', NOW(), '报表分析目录');

SET @report_menu_id = LAST_INSERT_ID();

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) VALUES
('销售报表', @report_menu_id, 1, 'sale', 'inventory/report/sale/index', 1, 0, 'C', '0', '0', 'inventory:report:sale', 'chart', 'admin', NOW()),
('库存报表', @report_menu_id, 2, 'stock', 'inventory/report/stock/index', 1, 0, 'C', '0', '0', 'inventory:report:stock', 'dashboard', 'admin', NOW());
