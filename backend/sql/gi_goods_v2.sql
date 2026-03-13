-- ============================================
-- 服装进销存系统 - 商品管理模块数据库设计 V2
-- 执行顺序：按表顺序执行
-- ============================================

-- 1. 品牌表（恢复独立表）
DROP TABLE IF EXISTS gi_brand;
CREATE TABLE gi_brand (
    brand_id       BIGINT          NOT NULL AUTO_INCREMENT COMMENT '品牌ID',
    brand_name     VARCHAR(100)    NOT NULL COMMENT '品牌名称',
    brand_code     VARCHAR(50)     DEFAULT NULL COMMENT '品牌编码',
    logo           VARCHAR(500)    DEFAULT NULL COMMENT '品牌Logo',
    sort           INT             DEFAULT 0 COMMENT '排序',
    status         CHAR(1)         DEFAULT '0' COMMENT '状态（0正常 1停用）',
    del_flag       CHAR(1)         DEFAULT '0' COMMENT '删除标志（0存在 1删除）',
    create_by      VARCHAR(64)     DEFAULT '' COMMENT '创建者',
    create_time    DATETIME        DEFAULT NULL COMMENT '创建时间',
    update_by      VARCHAR(64)     DEFAULT '' COMMENT '更新者',
    update_time    DATETIME        DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (brand_id),
    UNIQUE KEY uk_brand_name (brand_name)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='品牌表';

-- 2. 规格类型表
DROP TABLE IF EXISTS gi_spec;
CREATE TABLE gi_spec (
    spec_id        BIGINT          NOT NULL AUTO_INCREMENT COMMENT '规格ID',
    spec_name      VARCHAR(50)     NOT NULL COMMENT '规格名称（如：颜色、尺码）',
    spec_code      VARCHAR(30)     NOT NULL COMMENT '规格编码（如：color、size）',
    sort           INT             DEFAULT 0 COMMENT '排序',
    status         CHAR(1)         DEFAULT '0' COMMENT '状态（0正常 1停用）',
    del_flag       CHAR(1)         DEFAULT '0' COMMENT '删除标志（0存在 1删除）',
    create_by      VARCHAR(64)     DEFAULT '' COMMENT '创建者',
    create_time    DATETIME        DEFAULT NULL COMMENT '创建时间',
    update_by      VARCHAR(64)     DEFAULT '' COMMENT '更新者',
    update_time    DATETIME        DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (spec_id),
    UNIQUE KEY uk_spec_code (spec_code)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='规格类型表';

-- 3. 规格值表
DROP TABLE IF EXISTS gi_spec_value;
CREATE TABLE gi_spec_value (
    value_id       BIGINT          NOT NULL AUTO_INCREMENT COMMENT '规格值ID',
    spec_id        BIGINT          NOT NULL COMMENT '规格ID',
    value_name     VARCHAR(50)     NOT NULL COMMENT '规格值名称',
    value_code     VARCHAR(30)     DEFAULT NULL COMMENT '规格值编码',
    color_hex      VARCHAR(10)     DEFAULT NULL COMMENT '颜色十六进制值',
    sort           INT             DEFAULT 0 COMMENT '排序',
    status         CHAR(1)         DEFAULT '0' COMMENT '状态（0正常 1停用）',
    del_flag       CHAR(1)         DEFAULT '0' COMMENT '删除标志（0存在 1删除）',
    create_by      VARCHAR(64)     DEFAULT '' COMMENT '创建者',
    create_time    DATETIME        DEFAULT NULL COMMENT '创建时间',
    update_by      VARCHAR(64)     DEFAULT '' COMMENT '更新者',
    update_time    DATETIME        DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (value_id),
    KEY idx_spec_id (spec_id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='规格值表';

-- 4. 重建商品SPU表
DROP TABLE IF EXISTS gi_goods;
CREATE TABLE gi_goods (
    goods_id       BIGINT          NOT NULL AUTO_INCREMENT COMMENT '商品ID',
    goods_code     VARCHAR(50)     NOT NULL COMMENT '商品编码',
    goods_name     VARCHAR(200)    NOT NULL COMMENT '商品名称',
    category_id    BIGINT          DEFAULT NULL COMMENT '分类ID',
    brand_id       BIGINT          DEFAULT NULL COMMENT '品牌ID',
    season         CHAR(1)         DEFAULT NULL COMMENT '季节（1春 2夏 3秋 4冬 5四季）',
    year           INT             DEFAULT NULL COMMENT '年份',
    unit           VARCHAR(10)     DEFAULT '件' COMMENT '单位',
    purchase_price DECIMAL(10,2)   DEFAULT 0.00 COMMENT '进货价（参考）',
    sale_price     DECIMAL(10,2)   DEFAULT 0.00 COMMENT '销售价（参考）',
    main_image     VARCHAR(500)    DEFAULT NULL COMMENT '主图URL',
    images         TEXT            DEFAULT NULL COMMENT '商品图片列表（JSON）',
    description    TEXT            DEFAULT NULL COMMENT '商品描述',
    status         CHAR(1)         DEFAULT '0' COMMENT '状态（0正常 1停用）',
    del_flag       CHAR(1)         DEFAULT '0' COMMENT '删除标志（0存在 1删除）',
    create_by      VARCHAR(64)     DEFAULT '' COMMENT '创建者',
    create_time    DATETIME        DEFAULT NULL COMMENT '创建时间',
    update_by      VARCHAR(64)     DEFAULT '' COMMENT '更新者',
    update_time    DATETIME        DEFAULT NULL COMMENT '更新时间',
    remark         VARCHAR(500)    DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (goods_id),
    UNIQUE KEY uk_goods_code (goods_code),
    KEY idx_category_id (category_id),
    KEY idx_brand_id (brand_id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='商品SPU表';

-- 5. 重建SKU表
DROP TABLE IF EXISTS gi_sku;
CREATE TABLE gi_sku (
    sku_id         BIGINT          NOT NULL AUTO_INCREMENT COMMENT 'SKUID',
    goods_id       BIGINT          NOT NULL COMMENT '商品ID',
    sku_code       VARCHAR(80)     NOT NULL COMMENT 'SKU编码',
    color          VARCHAR(50)     DEFAULT NULL COMMENT '颜色',
    size           VARCHAR(50)     DEFAULT NULL COMMENT '尺码',
    barcode        VARCHAR(50)     DEFAULT NULL COMMENT '条形码',
    purchase_price DECIMAL(10,2)   DEFAULT 0.00 COMMENT '进货价',
    sale_price     DECIMAL(10,2)   DEFAULT 0.00 COMMENT '销售价',
    image          VARCHAR(500)    DEFAULT NULL COMMENT 'SKU图片',
    status         CHAR(1)         DEFAULT '0' COMMENT '状态（0正常 1停用）',
    del_flag       CHAR(1)         DEFAULT '0' COMMENT '删除标志（0存在 1删除）',
    create_by      VARCHAR(64)     DEFAULT '' COMMENT '创建者',
    create_time    DATETIME        DEFAULT NULL COMMENT '创建时间',
    update_by      VARCHAR(64)     DEFAULT '' COMMENT '更新者',
    update_time    DATETIME        DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (sku_id),
    UNIQUE KEY uk_sku_code (sku_code),
    KEY idx_goods_id (goods_id),
    KEY idx_barcode (barcode)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='商品SKU表';

-- ============================================
-- 初始化数据
-- ============================================

-- 初始化品牌
INSERT INTO gi_brand (brand_name, brand_code, sort, create_time) VALUES
('自有品牌', 'SELF', 0, NOW()),
('优衣库', 'UNIQLO', 1, NOW()),
('ZARA', 'ZARA', 2, NOW()),
('H&M', 'HM', 3, NOW()),
('耐克', 'NIKE', 4, NOW()),
('阿迪达斯', 'ADIDAS', 5, NOW());

-- 初始化规格类型
INSERT INTO gi_spec (spec_id, spec_name, spec_code, sort, create_time) VALUES
(1, '颜色', 'color', 1, NOW()),
(2, '尺码', 'size', 2, NOW());

-- 初始化颜色规格值
INSERT INTO gi_spec_value (spec_id, value_name, value_code, color_hex, sort, create_time) VALUES
(1, '白色', 'white', '#FFFFFF', 1, NOW()),
(1, '黑色', 'black', '#000000', 2, NOW()),
(1, '红色', 'red', '#FF0000', 3, NOW()),
(1, '蓝色', 'blue', '#0000FF', 4, NOW()),
(1, '绿色', 'green', '#008000', 5, NOW()),
(1, '黄色', 'yellow', '#FFFF00', 6, NOW()),
(1, '粉色', 'pink', '#FFC0CB', 7, NOW()),
(1, '灰色', 'gray', '#808080', 8, NOW()),
(1, '米色', 'beige', '#F5F5DC', 9, NOW()),
(1, '藏青色', 'navy', '#000080', 10, NOW());

-- 初始化尺码规格值
INSERT INTO gi_spec_value (spec_id, value_name, value_code, sort, create_time) VALUES
(2, 'XS', 'XS', 1, NOW()),
(2, 'S', 'S', 2, NOW()),
(2, 'M', 'M', 3, NOW()),
(2, 'L', 'L', 4, NOW()),
(2, 'XL', 'XL', 5, NOW()),
(2, 'XXL', 'XXL', 6, NOW()),
(2, 'XXXL', 'XXXL', 7, NOW()),
(2, '均码', 'F', 8, NOW());
