package com.ruoyi.inventory.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 库存盘点单对象 gi_stocktake
 */
public class GiStocktake extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 盘点单ID */
    private Long stocktakeId;

    /** 盘点单号 */
    @Excel(name = "盘点单号")
    private String stocktakeNo;

    /** 门店ID */
    private Long deptId;

    /** 门店名称 */
    @Excel(name = "门店")
    private String deptName;

    /** 盘点商品数 */
    @Excel(name = "商品数")
    private Integer itemCount;

    /** 盈亏金额 */
    @Excel(name = "盈亏金额")
    private BigDecimal diffAmount;

    /** 状态（0待审核 1已完成 2已取消） */
    @Excel(name = "状态", readConverterExp = "0=待审核,1=已完成,2=已取消")
    private String status;

    /** 审核人 */
    private String auditBy;

    /** 审核时间 */
    private Date auditTime;

    /** 盘点明细列表 */
    private List<GiStocktakeItem> itemList;

    public Long getStocktakeId() {
        return stocktakeId;
    }

    public void setStocktakeId(Long stocktakeId) {
        this.stocktakeId = stocktakeId;
    }

    public String getStocktakeNo() {
        return stocktakeNo;
    }

    public void setStocktakeNo(String stocktakeNo) {
        this.stocktakeNo = stocktakeNo;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public Integer getItemCount() {
        return itemCount;
    }

    public void setItemCount(Integer itemCount) {
        this.itemCount = itemCount;
    }

    public BigDecimal getDiffAmount() {
        return diffAmount;
    }

    public void setDiffAmount(BigDecimal diffAmount) {
        this.diffAmount = diffAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAuditBy() {
        return auditBy;
    }

    public void setAuditBy(String auditBy) {
        this.auditBy = auditBy;
    }

    public Date getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(Date auditTime) {
        this.auditTime = auditTime;
    }

    public List<GiStocktakeItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<GiStocktakeItem> itemList) {
        this.itemList = itemList;
    }
}
