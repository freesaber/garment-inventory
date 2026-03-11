# 服装进销存管理系统

基于若依(RuoYi)前后端分离架构的服装进销存管理系统，支持零售、多店铺调货。

## 项目结构

```
garment-inventory/
├── backend/          # 后端项目 (Spring Boot + Spring Security + JWT)
│   ├── ruoyi-admin/      # 后台管理入口
│   ├── ruoyi-common/     # 公共模块
│   ├── ruoyi-framework/  # 框架核心
│   ├── ruoyi-generator/  # 代码生成器
│   ├── ruoyi-quartz/     # 定时任务
│   ├── ruoyi-system/     # 系统模块
│   ├── sql/              # 数据库脚本
│   └── pom.xml
├── frontend/         # 前端项目 (Vue 3 + TypeScript + Vite + Element Plus)
│   ├── src/              # 源代码
│   ├── public/           # 静态资源
│   ├── package.json
│   └── vite.config.ts
└── README.md
```

## 技术栈

### 后端
- Spring Boot 2.x
- Spring Security + JWT
- MyBatis + MyBatis-Plus
- MySQL 8.0
- Redis

### 前端
- Vue 3 + TypeScript
- Vite
- Element Plus
- Pinia
- Vue Router

## 快速开始

### 后端启动

1. 创建数据库并导入SQL
```bash
mysql -u root -p < backend/sql/ry_xxx.sql
```

2. 修改数据库配置
```yaml
# backend/ruoyi-admin/src/main/resources/application-druid.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ry-vue?useUnicode=true&characterEncoding=utf8
    username: root
    password: your_password
```

3. 启动后端服务
```bash
cd backend
mvn spring-boot:run -pl ruoyi-admin
```

### 前端启动

1. 安装依赖
```bash
cd frontend
npm install
```

2. 启动开发服务器
```bash
npm run dev
```

3. 访问系统
- 地址：http://localhost:80
- 账号：admin
- 密码：admin123

## 功能模块

- [x] 用户管理
- [x] 角色管理
- [x] 菜单管理
- [x] 部门管理
- [x] 岗位管理
- [x] 字典管理
- [x] 参数配置
- [x] 通知公告
- [x] 日志管理
- [ ] 商品管理 (待开发)
- [ ] 库存管理 (待开发)
- [ ] 进货管理 (待开发)
- [ ] 销售管理 (待开发)
- [ ] 店铺管理 (待开发)
- [ ] 调货管理 (待开发)

## 开发计划

1. 基础架构搭建
2. 商品信息管理
3. 库存管理
4. 进销存业务模块
5. 多店铺调货
6. 报表统计

## 开源协议

MIT License

## 致谢

- [RuoYi-Vue](https://gitee.com/y_project/RuoYi-Vue) - 后端框架
- [RuoYi-Vue3](https://gitcode.com/yangzongzhuan/RuoYi-Vue3) - 前端框架
