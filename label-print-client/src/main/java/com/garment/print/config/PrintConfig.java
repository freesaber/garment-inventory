package com.garment.print.config;

import java.io.*;
import java.util.Properties;

/**
 * 打印配置
 * 
 * @author garment
 */
public class PrintConfig {
    
    private String printerName;
    private int paperWidth = 60;  // 默认60mm
    private int paperHeight = 40;  // 默认40mm
    private int marginTop = 2;    // 上边距 2mm
    private int marginRight = 2;  // 右边距 2mm
    private int marginLeft = 2;   // 左边距 2mm
    private int marginBottom = 2; // 下边距 2mm
    
    private static final String CONFIG_FILE = "print-config.properties";
    
    public static PrintConfig load() {
        PrintConfig config = new PrintConfig();
        File file = new File(CONFIG_FILE);
        if (file.exists()) {
            try (Properties props = new Properties();
                props.load(new FileInputStream(file));
                config.setPrinterName(props.getProperty("printer.name"));
                config.setPaperWidth(parseInt(props.getProperty("paper.width", "60")));
                config.setPaperHeight(parseInt(props.getProperty("paper.height", "40")));
            } catch (IOException e) {
                System.err.println("加载配置文件失败: " + e.getMessage());
            }
        }
        return config;
    }
    
    public void save() {
        try (Properties props = new Properties();
        if (printerName != null) {
            props.setProperty("printer.name", printerName);
            props.setProperty("paper.width", String.valueOf(paperWidth));
            props.setProperty("paper.height", String.valueOf(paperHeight));
            props.store(new FileOutputStream(CONFIG_FILE), "UTF-8");
        } catch (IOException e) {
            System.err.println("保存配置文件失败: " + e.getMessage());
        }
    }
    
    // Getters and Setters
    public String getPrinterName() {
        return printerName;
    }
    
    public void setPrinterName(String printerName) {
        this.printerName = printerName;
    }
    
    public int getPaperWidth() {
        return paperWidth;
    }
    
    public void setPaperWidth(int paperWidth) {
        this.paperWidth = paperWidth;
    }
    
    public int getPaperHeight() {
        return paperHeight;
    }
    
    public void setPaperHeight(int paperHeight) {
        this.paperHeight = paperHeight;
    }
    
    public int getMarginTop() {
        return marginTop;
    }
    
    public int getMarginRight() {
        return marginRight;
    }
    
    public int getMarginLeft() {
        return marginLeft;
    }
    
    public int getMarginBottom() {
        return marginBottom;
    }
}
