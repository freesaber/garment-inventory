package com.ruoyi.inventory.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 商品SPU对象 gi_goods
 */
public class GiGoods extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 商品ID */
    private Long goodsId;

    /** 商品编码 */
    @Excel(name = "商品编码")
    private String goodsCode;

    /** 商品名称 */
    @Excel(name = "商品名称")
    private String goodsName;

    /** 分类ID */
    @Excel(name = "分类ID")
    private Long categoryId;

    /** 分类名称 */
    private String categoryName;

    /** 品牌(字典值) */
    @Excel(name = "品牌")
    private String brand;

    /** 季节（1春 2夏 3秋 4冬 5四季） */
    @Excel(name = "季节", readConverterExp = "1=春,2=夏,3=秋,4=冬,5=四季")
    private String season;

    /** 年份 */
    @Excel(name = "年份")
    private Integer year;

    /** 单位 */
    @Excel(name = "单位")
    private String unit;

    /** 进货价 */
    @Excel(name = "进货价")
    private BigDecimal purchasePrice;

    /** 销售价 */
    @Excel(name = "销售价")
    private BigDecimal salePrice;

    /** 主图 */
    private String mainImage;

    /** 商品图片 */
    private String images;

    /** 商品描述 */
    private String description;

    /** 状态 */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /** 删除标志 */
    private String delFlag;

    /** SKU列表 */
    private List<GiSku> skuList;

    /** 选中的颜色（用于前端） */
    private String[] colors;

    /** 选中的尺码（用于前端） */
    private String[] sizes;

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public String getGoodsCode() {
        return goodsCode;
    }

    public void setGoodsCode(String goodsCode) {
        this.goodsCode = goodsCode;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public String getMainImage() {
        return mainImage;
    }

    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public List<GiSku> getSkuList() {
        return skuList;
    }

    public void setSkuList(List<GiSku> skuList) {
        this.skuList = skuList;
    }

    public String[] getColors() {
        return colors;
    }

    public void setColors(String[] colors) {
        this.colors = colors;
    }

    public String[] getSizes() {
        return sizes;
    }

    public void setSizes(String[] sizes) {
        this.sizes = sizes;
    }
}
