package com.ruoyi.inventory.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 库存调拨单对象 gi_transfer
 */
public class GiTransfer extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 调拨单ID */
    private Long transferId;

    /** 调拨单号 */
    @Excel(name = "调拨单号")
    private String transferNo;

    /** 调出门店ID */
    private Long deptIdOut;

    /** 调出门店名称 */
    @Excel(name = "调出门店")
    private String deptNameOut;

    /** 调入门店ID */
    private Long deptIdIn;

    /** 调入门店名称 */
    @Excel(name = "调入门店")
    private String deptNameIn;

    /** 总数量 */
    @Excel(name = "总数量")
    private Integer totalQty;

    /** 状态（0待审核 1待出库 2待入库 3已完成 4已取消） */
    @Excel(name = "状态", readConverterExp = "0=待审核,1=待出库,2=待入库,3=已完成,4=已取消")
    private String status;

    /** 审核人 */
    private String auditBy;

    /** 审核时间 */
    private Date auditTime;

    /** 出库人 */
    private String outBy;

    /** 出库时间 */
    private Date outTime;

    /** 入库人 */
    private String inBy;

    /** 入库时间 */
    private Date inTime;

    /** 调拨明细列表 */
    private List<GiTransferItem> itemList;

    public Long getTransferId() {
        return transferId;
    }

    public void setTransferId(Long transferId) {
        this.transferId = transferId;
    }

    public String getTransferNo() {
        return transferNo;
    }

    public void setTransferNo(String transferNo) {
        this.transferNo = transferNo;
    }

    public Long getDeptIdOut() {
        return deptIdOut;
    }

    public void setDeptIdOut(Long deptIdOut) {
        this.deptIdOut = deptIdOut;
    }

    public String getDeptNameOut() {
        return deptNameOut;
    }

    public void setDeptNameOut(String deptNameOut) {
        this.deptNameOut = deptNameOut;
    }

    public Long getDeptIdIn() {
        return deptIdIn;
    }

    public void setDeptIdIn(Long deptIdIn) {
        this.deptIdIn = deptIdIn;
    }

    public String getDeptNameIn() {
        return deptNameIn;
    }

    public void setDeptNameIn(String deptNameIn) {
        this.deptNameIn = deptNameIn;
    }

    public Integer getTotalQty() {
        return totalQty;
    }

    public void setTotalQty(Integer totalQty) {
        this.totalQty = totalQty;
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

    public String getOutBy() {
        return outBy;
    }

    public void setOutBy(String outBy) {
        this.outBy = outBy;
    }

    public Date getOutTime() {
        return outTime;
    }

    public void setOutTime(Date outTime) {
        this.outTime = outTime;
    }

    public String getInBy() {
        return inBy;
    }

    public void setInBy(String inBy) {
        this.inBy = inBy;
    }

    public Date getInTime() {
        return inTime;
    }

    public void setInTime(Date inTime) {
        this.inTime = inTime;
    }

    public List<GiTransferItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<GiTransferItem> itemList) {
        this.itemList = itemList;
    }
}
