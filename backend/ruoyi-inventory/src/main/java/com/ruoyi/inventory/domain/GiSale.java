package com.ruoyi.inventory.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 销售单对象 gi_sale
 */
public class GiSale extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 销售单ID */
    private Long saleId;

    /** 销售单号 */
    @Excel(name = "销售单号")
    private String saleNo;

    /** 店铺ID */
    private Long deptId;

    /** 店铺名称 */
    @Excel(name = "店铺")
    private String deptName;

    /** 总金额 */
    @Excel(name = "总金额")
    private BigDecimal totalAmount;

    /** 实付金额 */
    @Excel(name = "实付金额")
    private BigDecimal payAmount;

    /** 优惠金额 */
    @Excel(name = "优惠金额")
    private BigDecimal discountAmount;

    /** 支付方式 */
    @Excel(name = "支付方式")
    private String payType;

    /** 状态 */
    @Excel(name = "状态", readConverterExp = "0=正常,1=作废")
    private String status;

    /** 销售明细列表 */
    private List<GiSaleItem> itemList;

    public Long getSaleId() {
        return saleId;
    }

    public void setSaleId(Long saleId) {
        this.saleId = saleId;
    }

    public String getSaleNo() {
        return saleNo;
    }

    public void setSaleNo(String saleNo) {
        this.saleNo = saleNo;
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

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(BigDecimal payAmount) {
        this.payAmount = payAmount;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<GiSaleItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<GiSaleItem> itemList) {
        this.itemList = itemList;
    }
}
