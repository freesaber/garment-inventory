package com.garment.print.print;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.awt.print.PrinterJob;
import java.util.Arrays;
import java.util.List;

/**
 * 打印机服务工具类
 * 
 * @author garment
 */
public class PrintServiceUtil {
    
    /**
     * 列出所有可用打印机
     */
    public static void listPrinters() {
        System.out.println();
        System.out.println("可用打印机列表:");
        System.out.println("----------------------------------------");
        
        PrintService[] services = PrinterJob.lookupPrintServices();
        PrintService defaultPrinter = PrintServiceLookup.lookupDefaultPrintService();
        
        if (services.length == 0) {
            System.out.println("  (无可用打印机)");
        } else {
            for (int i = 0; i < services.length; i++) {
                PrintService service = services[i];
                String defaultMark = service.equals(defaultPrinter) ? " [默认]" : "";
                System.out.println("  " + (i + 1) + ". " + service.getName() + defaultMark);
            }
        }
        
        System.out.println("----------------------------------------");
        System.out.println("共 " + services.length + " 台打印机");
        System.out.println();
    }
    
    /**
     * 获取打印机列表
     */
    public static List<String> getPrinterNames() {
        PrintService[] services = PrinterJob.lookupPrintServices();
        return Arrays.stream(services).map(PrintService::getName).toList();
    }
    
    /**
     * 查找打印机
     */
    public static PrintService findPrinter(String name) {
        if (name == null || name.isEmpty()) {
            return PrintServiceLookup.lookupDefaultPrintService();
        }
        
        PrintService[] services = PrinterJob.lookupPrintServices();
        for (PrintService service : services) {
            if (service.getName().contains(name)) {
                return service;
            }
        }
        
        return null;
    }
}
