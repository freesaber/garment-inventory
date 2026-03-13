package com.garment.print.handler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.garment.print.print.HtmlPrintService;
import com.garment.print.print.PrintResult;
import org.java_websocket.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 打印消息处理器
 * 
 * 处理来自管理后台的打印请求
 * 
 * 支持的消息类型：
 * 1. print - 打印单个吊牌
 * 2. batch - 批量打印吊牌
 * 3. preview - 预览吊牌
 * 4. status - 查询状态
 * 5. printers - 获取打印机列表
 * 
 * @author garment
 */
public class PrintMessageHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(PrintMessageHandler.class);
    
    private final HtmlPrintService printService;
    
    public PrintMessageHandler(HtmlPrintService printService) {
        this.printService = printService;
    }
    
    public String handle(WebSocket conn, String message) {
        JSONObject json = JSON.parseObject(message);
        String type = json.getString("type");
        
        if (type == null) {
            return error("消息类型不能为空");
        }
        
        switch (type) {
            case "print":
                return handlePrint(json);
            case "batch":
                return handleBatch(json);
            case "preview":
                return handlePreview(json);
            case "status":
                return handleStatus();
            case "printers":
                return handlePrinters();
            default:
                return error("未知的消息类型: " + type);
        }
    }
    
    /**
     * 处理单个打印请求
     */
    private String handlePrint(JSONObject json) {
        JSONObject data = json.getJSONObject("data");
        if (data == null) {
            return error("打印数据不能为空");
        }
        
        String html = data.getString("html");
        Integer count = data.getInteger("count");
        String printer = data.getString("printer");
        
        if (html == null || html.isEmpty()) {
            return error("HTML内容不能为空");
        }
        
        if (count == null || count < 1) {
            count = 1;
        }
        
        logger.info("收到打印请求，份数: {}, 打印机: {}", count, printer);
        
        PrintResult result = printService.printHtml(html, count, printer);
        
        if (result.isSuccess()) {
            return success("打印成功，共打印 " + result.getPrintedCount() + " 份");
        } else {
            return error("打印失败: " + result.getErrorMessage());
        }
    }
    
    /**
     * 处理批量打印请求
     */
    private String handleBatch(JSONObject json) {
        JSONObject data = json.getJSONObject("data");
        if (data == null) {
            return error("打印数据不能为空");
        }
        
        List<String> htmlList = data.getList("items", String.class);
        String printer = data.getString("printer");
        
        if (htmlList == null || htmlList.isEmpty()) {
            return error("打印项不能为空");
        }
        
        logger.info("收到批量打印请求，数量: {}, 打印机: {}", htmlList.size(), printer);
        
        int successCount = 0;
        int failCount = 0;
        
        for (String html : htmlList) {
            PrintResult result = printService.printHtml(html, 1, printer);
            if (result.isSuccess()) {
                successCount++;
            } else {
                failCount++;
                logger.warn("打印失败: {}", result.getErrorMessage());
            }
        }
        
        return success("批量打印完成，成功: " + successCount + ", 失败: " + failCount);
    }
    
    /**
     * 处理预览请求
     */
    private String handlePreview(JSONObject json) {
        JSONObject data = json.getJSONObject("data");
        if (data == null) {
            return error("预览数据不能为空");
        }
        
        String html = data.getString("html");
        if (html == null || html.isEmpty()) {
            return error("HTML内容不能为空");
        }
        
        // 预览模式下，生成临时文件并返回路径
        String previewPath = printService.generatePreview(html);
        
        return success("预览已生成").fluentPut("previewPath", previewPath).toJSONString();
    }
    
    /**
     * 处理状态查询
     */
    private String handleStatus() {
        JSONObject result = new JSONObject();
        result.put("type", "status");
        result.put("status", "running");
        result.put("connections", 1);
        result.put("defaultPrinter", printService.getDefaultPrinter());
        return result.toJSONString();
    }
    
    /**
     * 处理打印机列表查询
     */
    private String handlePrinters() {
        JSONObject result = new JSONObject();
        result.put("type", "printers");
        result.put("printers", printService.getPrinterList());
        result.put("default", printService.getDefaultPrinter());
        return result.toJSONString();
    }
    
    private String error(String message) {
        JSONObject result = new JSONObject();
        result.put("type", "error");
        result.put("message", message);
        return result.toJSONString();
    }
    
    private JSONObject success(String message) {
        JSONObject result = new JSONObject();
        result.put("type", "success");
        result.put("message", message);
        return result;
    }
}
