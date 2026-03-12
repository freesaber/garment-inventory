package com.ruoyi.inventory.domain;

import java.io.Serializable;
import java.util.Date;
import com.ruoyi.common.annotation.Excel;

/**
 * 店铺库存对象 gi_stock
 */
public class GiStock implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 库存ID */
    private Long stockId;

    /** 店铺ID(部门ID) */
    @Excel(name = "店铺ID")
    private Long deptId;

    /** 店铺名称 */
    @Excel(name = "店铺名称")
    private String deptName;

    /** SKUID */
    private Long skuId;

    /** SKU编码 */
    @Excel(name = "SKU编码")
    private String skuCode;

    /** 商品名称 */
    @Excel(name = "商品名称")
    private String goodsName;

    /** 颜色 */
    @Excel(name = "颜色")
    private String color;

    /** 尺码 */
    @Excel(name = "尺码")
    private String size;

    /** 库存数量 */
    @Excel(name = "库存数量")
    private Integer quantity;

    /** 锁定数量 */
    private Integer lockedQty;

    /** 预警数量 */
    @Excel(name = "预警数量")
    private Integer warningQty;

    /** 创建时间 */
    private Date createTime;

    /** 更新时间 */
    private Date updateTime;

    public Long getStockId() {
        return stockId;
    }

    public void setStockId(Long stockId) {
        this.stockId = stockId;
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

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getLockedQty() {
        return lockedQty;
    }

    public void setLockedQty(Integer lockedQty) {
        this.lockedQty = lockedQty;
    }

    public Integer getWarningQty() {
        return warningQty;
    }

    public void setWarningQty(Integer warningQty) {
        this.warningQty = warningQty;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
