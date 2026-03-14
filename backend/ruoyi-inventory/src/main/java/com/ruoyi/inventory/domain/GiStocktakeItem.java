package com.ruoyi.inventory.domain;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 盘点单明细对象 gi_stocktake_item
 */
public class GiStocktakeItem implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 明细ID */
    private Long itemId;

    /** 盘点单ID */
    private Long stocktakeId;

    /** SKUID */
    private Long skuId;

    /** SKU编码 */
    private String skuCode;

    /** 商品名称 */
    private String goodsName;

    /** 颜色 */
    private String color;

    /** 尺码 */
    private String size;

    /** 系统库存 */
    private Integer systemQty;

    /** 实盘数量 */
    private Integer actualQty;

    /** 盈亏数量 */
    private Integer diffQty;

    /** 成本价 */
    private BigDecimal costPrice;

    /** 盈亏金额 */
    private BigDecimal diffAmount;

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Long getStocktakeId() {
        return stocktakeId;
    }

    public void setStocktakeId(Long stocktakeId) {
        this.stocktakeId = stocktakeId;
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

    public Integer getSystemQty() {
        return systemQty;
    }

    public void setSystemQty(Integer systemQty) {
        this.systemQty = systemQty;
    }

    public Integer getActualQty() {
        return actualQty;
    }

    public void setActualQty(Integer actualQty) {
        this.actualQty = actualQty;
    }

    public Integer getDiffQty() {
        return diffQty;
    }

    public void setDiffQty(Integer diffQty) {
        this.diffQty = diffQty;
    }

    public BigDecimal getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
    }

    public BigDecimal getDiffAmount() {
        return diffAmount;
    }

    public void setDiffAmount(BigDecimal diffAmount) {
        this.diffAmount = diffAmount;
    }
}
