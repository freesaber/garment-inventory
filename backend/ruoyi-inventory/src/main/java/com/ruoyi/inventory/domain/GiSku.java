package com.ruoyi.inventory.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 商品SKU对象 gi_sku
 */
public class GiSku extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /** SKUID */
    private Long skuId;

    /** 商品ID */
    private Long goodsId;

    /** SKU编码 */
    @Excel(name = "SKU编码")
    private String skuCode;

    /** 颜色 */
    @Excel(name = "颜色")
    private String color;

    /** 尺码 */
    @Excel(name = "尺码")
    private String size;

    /** 条码 */
    @Excel(name = "条码")
    private String barcode;

    /** 售价 */
    @Excel(name = "售价")
    private BigDecimal price;

    /** 状态 */
    private String status;

    /** 删除标志 */
    private String delFlag;

    /** SKU图片 */
    private String image;

    /** 商品名称(关联查询) */
    private String goodsName;

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
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

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }
}
