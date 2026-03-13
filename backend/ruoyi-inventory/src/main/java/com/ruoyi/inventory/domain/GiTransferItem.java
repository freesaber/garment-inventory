package com.ruoyi.inventory.domain;

import java.io.Serializable;

/**
 * 调拨单明细对象 gi_transfer_item
 */
public class GiTransferItem implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 明细ID */
    private Long itemId;

    /** 调拨单ID */
    private Long transferId;

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

    /** 调拨数量 */
    private Integer quantity;

    /** 已出库数量 */
    private Integer outQty;

    /** 已入库数量 */
    private Integer inQty;

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Long getTransferId() {
        return transferId;
    }

    public void setTransferId(Long transferId) {
        this.transferId = transferId;
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

    public Integer getOutQty() {
        return outQty;
    }

    public void setOutQty(Integer outQty) {
        this.outQty = outQty;
    }

    public Integer getInQty() {
        return inQty;
    }

    public void setInQty(Integer inQty) {
        this.inQty = inQty;
    }
}
