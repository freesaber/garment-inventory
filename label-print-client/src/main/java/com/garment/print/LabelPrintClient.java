package com.garment.print;

import com.garment.print.server.WebSocketPrintServer;
import com.garment.print.config.PrintConfig;
import org.java_websocket.server.DefaultSSLWebSocketServerFactory;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.KeyStore;
import java.util.Scanner;

/**
 * 吊牌打印客户端主程序
 * 
 * 功能：
 * 1. 启动 WebSocket 服务， 监听管理后台的打印请求
 * 2. 接收 HTML 片段，调用本地打印机打印吊牌
 * 3. 支持批量打印、 打印预览
 * 
 * 使用方法：
 * java -jar label-print-client.jar [端口号]
 * 
 * 默认端口： 9 988
 * 
 * @author garment
 */
public class LabelPrintClient {
    
    private static final int DEFAULT_PORT = 9988;
    private static WebSocketPrintServer server;
    
    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("端口号格式错误，使用默认端口: " + DEFAULT_PORT);
            }
        }
        
        System.out.println("========================================");
        System.out.println("   服装吊牌打印客户端 v1.0.0");
        System.out.println("========================================");
        System.out.println();
        
        try {
            // 加载配置
            PrintConfig config = PrintConfig.load();
            System.out.println("[配置] 打印机: " + config.getPrinterName());
            System.out.println("[配置] 纸张宽度: " + config.getPaperWidth() + "mm");
            System.out.println("[配置] 纸张高度: " + config.getPaperHeight() + "mm");
            System.out.println();
            
            // 启动 WebSocket 服务
            server = new WebSocketPrintServer(new InetSocketAddress(port));
            server.start();
            
            System.out.println("[服务] WebSocket 服务已启动");
            System.out.println("[服务] 监听端口: ws://localhost:" + port);
            System.out.println("[服务] 等待管理后台连接...");
            System.out.println();
            System.out.println("输入 'help' 查看帮助，'quit' 退出程序");
            System.out.println();
            
            // 命令行交互
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.print("> ");
                String command = scanner.nextLine().trim();
                
                if ("quit".equalsIgnoreCase(command) || "exit".equalsIgnoreCase(command)) {
                    System.out.println("正在停止服务...");
                    server.stop();
                    System.out.println("服务已停止，再见！");
                    break;
                } else if ("help".equalsIgnoreCase(command)) {
                    printHelp();
                } else if ("status".equalsIgnoreCase(command)) {
                    printStatus();
                } else if ("printers".equalsIgnoreCase(command)) {
                    PrintServiceUtil.listPrinters();
                } else if (command.startsWith("set printer ")) {
                    String printerName = command.substring("set printer ".length());
                    config.setPrinterName(printerName);
                    config.save();
                    System.out.println("已设置默认打印机: " + printerName);
                } else if (!command.isEmpty()) {
                    System.out.println("未知命令: " + command);
                    System.out.println("输入 'help' 查看帮助");
                }
            }
            
        } catch (Exception e) {
            System.err.println("启动失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void printHelp() {
        System.out.println();
        System.out.println("可用命令:");
        System.out.println("  help          - 显示帮助信息");
        System.out.println("  status        - 显示服务状态");
        System.out.println("  printers      - 列出可用打印机");
        System.out.println("  set printer <名称> - 设置默认打印机");
        System.out.println("  quit          - 退出程序");
        System.out.println();
    }
    
    private static void printStatus() {
        System.out.println();
        System.out.println("服务状态:");
        System.out.println("  运行中: " + (server != null && server.isRunning()));
        System.out.println("  端口: " + (server != null ? server.getPort() : "N/A"));
        System.out.println("  连接数: " + (server != null ? server.getConnections().size() : 0));
        System.out.println();
    }
}
