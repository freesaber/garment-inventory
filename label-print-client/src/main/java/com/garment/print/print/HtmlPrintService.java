package com.garment.print.print;

import com.garment.print.config.PrintConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.*;
import java.awt.print.PrinterJob;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * HTML 打印服务
 * 
 * 将 HTML 内容渲染后发送到打印机打印吊牌
 * 
 * @author garment
 */
public class HtmlPrintService {
    
    private static final Logger logger = LoggerFactory.getLogger(HtmlPrintService.class);
    
    private final PrintConfig config;
    
    public HtmlPrintService() {
        this.config = PrintConfig.load();
    }
    
    /**
     * 打印 HTML 内容
     * 
     * @param html HTML 内容
     * @param count 打印份数
     * @param printerName 指定打印机（可选）
     * @return 打印结果
     */
    public PrintResult printHtml(String html, int count, String printerName) {
        PrintResult result = new PrintResult();
        
        try {
            // 获取打印机
            PrintService printer = getPrinter(printerName);
            if (printer == null) {
                result.setSuccess(false);
                result.setErrorMessage("未找到可用的打印机");
                return result;
            }
            
            logger.info("使用打印机: {}", printer.getName());
            
            // 生成 PDF 并打印
            for (int i = 0; i < count; i++) {
                boolean printed = printToPrinter(html, printer);
                if (printed) {
                    result.incrementPrintedCount();
                }
            }
            
            result.setSuccess(result.getPrintedCount() > 0);
            
        } catch (Exception e) {
            logger.error("打印失败", e);
            result.setSuccess(false);
            result.setErrorMessage(e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 调用打印机打印
     */
    private boolean printToPrinter(String html, PrintService printer) throws Exception {
        // 使用 JavaFX WebView 渲染 HTML 并打印
        // 这里简化实现，使用系统默认浏览器打开 HTML 并调用打印
        
        // 创建临时 HTML 文件
        File tempFile = File.createTempFile("label_", ".html");
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write(html);
        }
        
        // 使用系统命令调用打印
        String os = System.getProperty("os.name").toLowerCase();
        ProcessBuilder pb;
        
        if (os.contains("win")) {
            // Windows: 使用 mshta 或 rundll32 打印
            pb = new ProcessBuilder("rundll32.exe", "mshtml.dll,PrintHTML", tempFile.getAbsolutePath());
        } else if (os.contains("mac")) {
            // macOS: 使用 lpr 命令
            pb = new ProcessBuilder("lpr", "-P", printer.getName(), tempFile.getAbsolutePath());
        } else {
            // Linux: 使用 lpr 命令
            pb = new ProcessBuilder("lpr", "-P", printer.getName(), tempFile.getAbsolutePath());
        }
        
        Process process = pb.start();
        int exitCode = process.waitFor();
        
        // 延迟删除临时文件
        tempFile.deleteOnExit();
        
        return exitCode == 0;
    }
    
    /**
     * 获取打印机
     */
    private PrintService getPrinter(String printerName) {
        PrintService[] services = PrinterJob.lookupPrintServices();
        
        if (printerName != null && !printerName.isEmpty()) {
            for (PrintService service : services) {
                if (service.getName().contains(printerName)) {
                    return service;
                }
            }
        }
        
        // 使用配置的默认打印机
        String defaultPrinter = config.getPrinterName();
        if (defaultPrinter != null && !defaultPrinter.isEmpty()) {
            for (PrintService service : services) {
                if (service.getName().contains(defaultPrinter)) {
                    return service;
                }
            }
        }
        
        // 使用系统默认打印机
        return PrintServiceLookup.lookupDefaultPrintService();
    }
    
    /**
     * 获取打印机列表
     */
    public List<String> getPrinterList() {
        List<String> printers = new ArrayList<>();
        PrintService[] services = PrinterJob.lookupPrintServices();
        for (PrintService service : services) {
            printers.add(service.getName());
        }
        return printers;
    }
    
    /**
     * 获取默认打印机
     */
    public String getDefaultPrinter() {
        PrintService defaultPrinter = PrintServiceLookup.lookupDefaultPrintService();
        return defaultPrinter != null ? defaultPrinter.getName() : null;
    }
    
    /**
     * 生成预览文件
     */
    public String generatePreview(String html) {
        try {
            File previewFile = new File(System.getProperty("java.io.tmpdir"), "label_preview.html");
            try (FileWriter writer = new FileWriter(previewFile)) {
                writer.write("<!DOCTYPE html>");
                writer.write("<html><head>");
                writer.write("<meta charset=\"UTF-8\">");
                writer.write("<title>吊牌预览</title>");
                writer.write("<style>body { margin: 0; padding: 20px; }</style>");
                writer.write("</head><body>");
                writer.write(html);
                writer.write("</body></html>");
            }
            return previewFile.getAbsolutePath();
        } catch (IOException e) {
            logger.error("生成预览失败", e);
            return null;
        }
    }
}
