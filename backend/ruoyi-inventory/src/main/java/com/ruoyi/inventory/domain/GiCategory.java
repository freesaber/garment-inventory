package com.ruoyi.inventory.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 商品分类对象 gi_category
 * 
 * @author ruoyi
 */
public class GiCategory extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 分类ID */
    private Long categoryId;

    /** 父分类ID */
    @Excel(name = "父分类ID")
    private Long parentId;

    /** 分类名称 */
    @Excel(name = "分类名称")
    private String categoryName;

    /** 排序 */
    @Excel(name = "排序")
    private Integer sort;

    /** 品类编码 */
    @Excel(name = "品类编码")
    private String kindCode;

    /** 状态(0正常 1停用) */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /** 删除标志(0存在 1删除) */
    private String delFlag;

    /** 子分类 */
    private List<GiCategory> children = new ArrayList<GiCategory>();

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getKindCode() {
        return kindCode;
    }

    public void setKindCode(String kindCode) {
        this.kindCode = kindCode;
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

    public List<GiCategory> getChildren() {
        return children;
    }

    public void setChildren(List<GiCategory> children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return "GiCategory [categoryId=" + categoryId + ", parentId=" + parentId + ", categoryName=" + categoryName + "]";
    }
}
