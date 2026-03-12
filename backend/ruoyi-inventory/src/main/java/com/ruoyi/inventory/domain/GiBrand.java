package com.ruoyi.inventory.domain;

import java.io.Serializable;
import java.util.Date;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 商品品牌对象 gi_brand
 */
public class GiBrand extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 品牌ID */
    private Long brandId;

    /** 品牌名称 */
    @Excel(name = "品牌名称")
    private String brandName;

    /** 品牌Logo */
    @Excel(name = "品牌Logo")
    private String brandLogo;

    /** 排序 */
    @Excel(name = "排序")
    private Integer sort;

    /** 状态(0正常 1停用) */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /** 删除标志 */
    private String delFlag;

    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getBrandLogo() {
        return brandLogo;
    }

    public void setBrandLogo(String brandLogo) {
        this.brandLogo = brandLogo;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
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
}
