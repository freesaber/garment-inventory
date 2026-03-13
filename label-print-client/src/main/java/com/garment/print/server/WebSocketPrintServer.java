package com.garment.print.server;

import com.garment.print.handler.PrintMessageHandler;
import com.garment.print.print.HtmlPrintService;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * WebSocket 打印服务
 * 
 * 接收管理后台发送的打印请求，调用本地打印机打印
 * 
 * 消息格式：
 * {
 *   "type": "print",
 *   "data": {
 *     "html": "<div>...</div>",
 *     "count": 1,
 *     "printer": "打印机名称(可选)"
 *   }
 * }
 * 
 * @author garment
 */
public class WebSocketPrintServer extends WebSocketServer {
    
    private static final Logger logger = LoggerFactory.getLogger(WebSocketPrintServer.class);
    
    private final Set<WebSocket> connections = Collections.synchronizedSet(new HashSet<>());
    private final HtmlPrintService printService = new HtmlPrintService();
    private final PrintMessageHandler messageHandler = new PrintMessageHandler(printService);
    
    public WebSocketPrintServer(InetSocketAddress address) {
        super(address);
    }
    
    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        connections.add(conn);
        String clientIp = conn.getRemoteSocketAddress().getAddress().getHostAddress();
        logger.info("新连接: {} (当前连接数: {})", clientIp, connections.size());
        
        // 发送欢迎消息
        sendMessage(conn, "{\"type\":\"connected\",\"message\":\"已连接到打印服务\"}");
    }
    
    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        connections.remove(conn);
        logger.info("连接关闭: {} - {} (当前连接数: {})", 
            conn.getRemoteSocketAddress(), reason, connections.size());
    }
    
    @Override
    public void onMessage(WebSocket conn, String message) {
        logger.debug("收到消息: {}", message);
        
        try {
            String response = messageHandler.handle(conn, message);
            if (response != null) {
                sendMessage(conn, response);
            }
        } catch (Exception e) {
            logger.error("处理消息失败", e);
            sendMessage(conn, "{\"type\":\"error\",\"message\":\"" + e.getMessage() + "\"}");
        }
    }
    
    @Override
    public void onError(WebSocket conn, Exception ex) {
        logger.error("WebSocket 错误: ", ex);
    }
    
    @Override
    public void onStart() {
        logger.info("WebSocket 服务已启动，端口: {}", getPort());
    }
    
    public void sendMessage(WebSocket conn, String message) {
        if (conn != null && conn.isOpen()) {
            conn.send(message);
        }
    }
    
    public void broadcast(String message) {
        for (WebSocket conn : connections) {
                sendMessage(conn, message);
            }
        }
    }
    
    public Set<WebSocket> getConnections() {
        return Collections.unmodifiableSet(connections);
    }
}
