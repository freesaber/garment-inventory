package com.ruoyi.inventory.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 规格类型对象 gi_spec
 */
public class GiSpec extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 规格ID */
    private Long specId;

    /** 规格名称 */
    @Excel(name = "规格名称")
    private String specName;

    /** 规格编码 */
    @Excel(name = "规格编码")
    private String specCode;

    /** 排序 */
    @Excel(name = "排序")
    private Integer sort;

    /** 状态 */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /** 删除标志 */
    private String delFlag;

    /** 规格值列表 */
    private List<GiSpecValue> values;

    public Long getSpecId() {
        return specId;
    }

    public void setSpecId(Long specId) {
        this.specId = specId;
    }

    public String getSpecName() {
        return specName;
    }

    public void setSpecName(String specName) {
        this.specName = specName;
    }

    public String getSpecCode() {
        return specCode;
    }

    public void setSpecCode(String specCode) {
        this.specCode = specCode;
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

    public List<GiSpecValue> getValues() {
        return values;
    }

    public void setValues(List<GiSpecValue> values) {
        this.values = values;
    }
}
