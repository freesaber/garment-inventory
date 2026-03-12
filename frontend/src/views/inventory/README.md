# 商品管理模块 - 菜单配置说明

## 需要在后台添加的菜单

请在「系统管理 -> 菜单管理」中添加以下菜单：

### 1. 进销存管理（一级菜单）
- 菜单名称：进销存管理
- 菜单类型：目录
- 路由地址：inventory
- 组件路径：无
- 菜单图标：shopping-cart（或其他合适的图标）
- 排序：3

### 2. 商品管理（二级菜单）
- 菜单名称：商品管理
- 父菜单：进销存管理
- 菜单类型：菜单
- 路由地址：goods
- 组件路径：inventory/goods/index
- 权限标识：inventory:goods:list
- 排序：1

### 3. 商品管理按钮权限
| 按钮名称 | 权限标识 |
|---------|---------|
| 查询 | inventory:goods:query |
| 新增 | inventory:goods:add |
| 修改 | inventory:goods:edit |
| 删除 | inventory:goods:remove |
| 导出 | inventory:goods:export |

### 4. 分类管理（二级菜单）
- 菜单名称：分类管理
- 父菜单：进销存管理
- 菜单类型：菜单
- 路由地址：category
- 组件路径：inventory/category/index
- 权限标识：inventory:category:list
- 排序：2

### 5. 分类管理按钮权限
| 按钮名称 | 权限标识 |
|---------|---------|
| 查询 | inventory:category:query |
| 新增 | inventory:category:add |
| 修改 | inventory:category:edit |
| 删除 | inventory:category:remove |

### 6. 品牌管理（二级菜单）
- 菜单名称：品牌管理
- 父菜单：进销存管理
- 菜单类型：菜单
- 路由地址：brand
- 组件路径：inventory/brand/index
- 权限标识：inventory:brand:list
- 排序：3

### 7. 品牌管理按钮权限
| 按钮名称 | 权限标识 |
|---------|---------|
| 查询 | inventory:brand:query |
| 新增 | inventory:brand:add |
| 修改 | inventory:brand:edit |
| 删除 | inventory:brand:remove |

## SQL 快速配置

```sql
-- 进销存管理目录
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('进销存管理', 0, 3, 'inventory', NULL, 'M', '0', '0', '', 'shopping-cart', 'admin', NOW(), '进销存管理目录');

-- 获取进销存管理菜单ID（假设为2000）
SET @inventory_menu_id = LAST_INSERT_ID();

-- 商品管理菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time)
VALUES ('商品管理', @inventory_menu_id, 1, 'goods', 'inventory/goods/index', 'C', '0', '0', 'inventory:goods:list', 'goods', 'admin', NOW());

SET @goods_menu_id = LAST_INSERT_ID();

-- 商品管理按钮
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time) VALUES
('商品查询', @goods_menu_id, 1, '', NULL, 'F', '0', '0', 'inventory:goods:query', '#', 'admin', NOW()),
('商品新增', @goods_menu_id, 2, '', NULL, 'F', '0', '0', 'inventory:goods:add', '#', 'admin', NOW()),
('商品修改', @goods_menu_id, 3, '', NULL, 'F', '0', '0', 'inventory:goods:edit', '#', 'admin', NOW()),
('商品删除', @goods_menu_id, 4, '', NULL, 'F', '0', '0', 'inventory:goods:remove', '#', 'admin', NOW()),
('商品导出', @goods_menu_id, 5, '', NULL, 'F', '0', '0', 'inventory:goods:export', '#', 'admin', NOW());

-- 分类管理菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time)
VALUES ('分类管理', @inventory_menu_id, 2, 'category', 'inventory/category/index', 'C', '0', '0', 'inventory:category:list', 'tree', 'admin', NOW());

SET @category_menu_id = LAST_INSERT_ID();

-- 分类管理按钮
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time) VALUES
('分类查询', @category_menu_id, 1, '', NULL, 'F', '0', '0', 'inventory:category:query', '#', 'admin', NOW()),
('分类新增', @category_menu_id, 2, '', NULL, 'F', '0', '0', 'inventory:category:add', '#', 'admin', NOW()),
('分类修改', @category_menu_id, 3, '', NULL, 'F', '0', '0', 'inventory:category:edit', '#', 'admin', NOW()),
('分类删除', @category_menu_id, 4, '', NULL, 'F', '0', '0', 'inventory:category:remove', '#', 'admin', NOW());

-- 品牌管理菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time)
VALUES ('品牌管理', @inventory_menu_id, 3, 'brand', 'inventory/brand/index', 'C', '0', '0', 'inventory:brand:list', 'peoples', 'admin', NOW());

SET @brand_menu_id = LAST_INSERT_ID();

-- 品牌管理按钮
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time) VALUES
('品牌查询', @brand_menu_id, 1, '', NULL, 'F', '0', '0', 'inventory:brand:query', '#', 'admin', NOW()),
('品牌新增', @brand_menu_id, 2, '', NULL, 'F', '0', '0', 'inventory:brand:add', '#', 'admin', NOW()),
('品牌修改', @brand_menu_id, 3, '', NULL, 'F', '0', '0', 'inventory:brand:edit', '#', 'admin', NOW()),
('品牌删除', @brand_menu_id, 4, '', NULL, 'F', '0', '0', 'inventory:brand:remove', '#', 'admin', NOW());
```

## 已创建的文件

### API 文件
- `frontend/src/api/inventory/goods.ts` - 商品管理 API
- `frontend/src/api/inventory/category.ts` - 分类管理 API
- `frontend/src/api/inventory/brand.ts` - 品牌管理 API

### 页面文件
- `frontend/src/views/inventory/goods/index.vue` - 商品管理页面
- `frontend/src/views/inventory/category/index.vue` - 分类管理页面
- `frontend/src/views/inventory/brand/index.vue` - 品牌管理页面
