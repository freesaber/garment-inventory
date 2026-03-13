package com.garment.print.print;

/**
 * 打印结果
 * 
 * @author garment
 */
public class PrintResult {
    
    private boolean success;
    private String errorMessage;
    private int printedCount;
    
    public PrintResult() {
        this.printedCount = 0;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public int getPrintedCount() {
        return printedCount;
    }
    
    public void incrementPrintedCount() {
        this.printedCount++;
    }
    
    public void setPrintedCount(int printedCount) {
        this.printedCount = printedCount;
    }
}
