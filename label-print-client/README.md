# 服装吊牌打印客户端

一个独立的 WebSocket 打印服务，用于接收管理后台发送的 HTML 吊牌并调用本地打印机打印。

## 功能特点

- ✅ WebSocket 服务，支持实时通信
- ✅ 接收 HTML 片段打印吊牌
- ✅ 支持批量打印
- ✅ 支持指定打印机
- ✅ 支持打印预览
- ✅ 跨平台支持（Windows/macOS/Linux）

## 系统要求

- Java 8 或更高版本
- 本地打印机已安装并配置

## 快速开始

### 1. 编译打包

```bash
mvn clean package
```

### 2. 启动服务

```bash
java -jar target/label-print-client-1.0.0.jar
```

默认监听端口：`9988`

### 3. 指定端口

```bash
java -jar target/label-print-client-1.0.0.jar 8888
```

## 使用说明

### 命令行操作

启动后进入交互模式：

| 命令 | 说明 |
|------|------|
| `help` | 显示帮助信息 |
| `status` | 显示服务状态 |
| `printers` | 列出可用打印机 |
| `set printer <名称>` | 设置默认打印机 |
| `quit` | 退出程序 |

### WebSocket 消息格式

#### 打印单个吊牌

```json
{
  "type": "print",
  "data": {
    "html": "<div class=\"label\">...</div>",
    "count": 1,
    "printer": "打印机名称(可选)"
  }
}
```

#### 批量打印

```json
{
  "type": "batch",
  "data": {
    "items": [
      "<div class=\"label\">...</div>",
      "<div class=\"label\">...</div>"
    ],
    "printer": "打印机名称(可选)"
  }
}
```

#### 获取打印机列表

```json
{
  "type": "printers"
}
```

#### 查询状态

```json
{
  "type": "status"
}
```

### 返回消息格式

#### 成功

```json
{
  "type": "success",
  "message": "打印成功"
}
```

#### 错误

```json
{
  "type": "error",
  "message": "错误信息"
}
```

## 配置文件

配置文件位于用户目录下：`~/.label-print/config.json`

```json
{
  "printerName": "",
  "paperWidth": 70,
  "paperHeight": 50,
  "marginTop": 5,
  "marginRight": 5,
  "marginLeft": 5,
  "marginBottom": 5
}
```

## 与管理后台集成

### 前端示例代码

```javascript
// 连接打印服务
const ws = new WebSocket('ws://localhost:9988');

ws.onopen = () => {
  console.log('已连接到打印服务');
};

ws.onmessage = (event) => {
  const response = JSON.parse(event.data);
  if (response.type === 'success') {
    console.log('打印成功');
  } else if (response.type === 'error') {
    console.error('打印失败:', response.message);
  }
};

// 发送打印请求
function printLabel(html, count = 1, printer = null) {
  ws.send(JSON.stringify({
    type: 'print',
    data: { html, count, printer }
  }));
}
```

## 吊牌模板示例

```html
<div style="width: 70mm; height: 50mm; padding: 5mm; font-family: Arial; font-size: 12px; border: 1px solid #000;">
  <div style="text-align: center; font-size: 16px; font-weight: bold; margin-bottom: 5px;">
    品牌名称
  </div>
  <div style="font-size: 10px; margin-bottom: 3px;">
    款号: <span style="font-weight: bold;">261T001</span>
  </div>
  <div style="font-size: 10px; margin-bottom: 3px;">
    品名: <span>纯棉T恤</span>
  </div>
  <div style="font-size: 10px; margin-bottom: 3px;">
    颜色: <span>白色</span> 尺码: <span>M</span>
  </div>
  <div style="font-size: 10px; margin-bottom: 3px;">
    面料: <span>100%棉</span>
  </div>
  <div style="font-size: 10px; margin-bottom: 3px;">
    价格: <span style="font-weight: bold; font-size: 14px;">¥199.00</span>
  </div>
  <div style="text-align: center; margin-top: 5px;">
    <div style="display: inline-block; width: 80px; height: 20px; background: #000;"></div>
    <div style="font-size: 8px;">261T00101S</div>
  </div>
</div>
```

## 编译为可执行文件

使用 Launch4j 或 jpackage 将 JAR 打包为 EXE：

### 使用 jpackage (Java 14+)

```bash
jpackage --name LabelPrintClient \
  --input target \
  --main-jar label-print-client-1.0.0.jar \
  --main-class com.garment.print.LabelPrintClient \
  --type exe \
  --dest dist
```

### 使用 Launch4j

下载 Launch4j 并配置 `launch4j.xml`：

```xml
<launch4jConfig>
  <dontWrapJar>false</dontWrapJar>
  <headerType>console</headerType>
  <jar>target/label-print-client-1.0.0.jar</jar>
  <outfile>dist/LabelPrintClient.exe</outfile>
  <errTitle>Label Print Client</errTitle>
  <cmdLine></cmdLine>
  <chdir>.</chdir>
  <priority>normal</priority>
  <downloadUrl>http://java.com/download</downloadUrl>
  <supportUrl></supportUrl>
  <stayAlive>false</stayAlive>
  <manifest></manifest>
  <icon></icon>
  <jre>
    <path></path>
    <minVersion>1.8.0</minVersion>
    <maxVersion></maxVersion>
    <jdkPreference>preferJre</jdkPreference>
  </jre>
</launch4jConfig>
```

## 许可证

MIT License

## 作者

Garment Inventory System
