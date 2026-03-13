package com.ruoyi.inventory.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 采购单对象 gi_purchase
 */
public class GiPurchase extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 采购单ID */
    private Long purchaseId;

    /** 采购单号 */
    @Excel(name = "采购单号")
    private String purchaseNo;

    /** 供应商ID */
    private Long supplierId;

    /** 供应商名称 */
    @Excel(name = "供应商")
    private String supplierName;

    /** 入库店铺ID */
    private Long deptId;

    /** 店铺名称 */
    private String deptName;

    /** 总金额 */
    @Excel(name = "总金额")
    private BigDecimal totalAmount;

    /** 状态 */
    @Excel(name = "状态", readConverterExp = "0=待入库,1=已入库,2=已取消")
    private String status;

    /** 采购明细列表 */
    private List<GiPurchaseItem> itemList;

    public Long getPurchaseId() { return purchaseId; }
    public void setPurchaseId(Long purchaseId) { this.purchaseId = purchaseId; }
    public String getPurchaseNo() { return purchaseNo; }
    public void setPurchaseNo(String purchaseNo) { this.purchaseNo = purchaseNo; }
    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }
    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
    public Long getDeptId() { return deptId; }
    public void setDeptId(Long deptId) { this.deptId = deptId; }
    public String getDeptName() { return deptName; }
    public void setDeptName(String deptName) { this.deptName = deptName; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public List<GiPurchaseItem> getItemList() { return itemList; }
    public void setItemList(List<GiPurchaseItem> itemList) { this.itemList = itemList; }
}
