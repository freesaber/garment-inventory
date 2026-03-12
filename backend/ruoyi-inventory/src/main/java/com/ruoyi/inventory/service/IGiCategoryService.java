package com.ruoyi.inventory.service;

import java.util.List;
import com.ruoyi.common.core.domain.TreeSelect;
import com.ruoyi.inventory.domain.GiCategory;

/**
 * 商品分类Service接口
 */
public interface IGiCategoryService {
    
    public GiCategory selectGiCategoryById(Long categoryId);

    public List<GiCategory> selectGiCategoryList(GiCategory giCategory);

    public int insertGiCategory(GiCategory giCategory);

    public int updateGiCategory(GiCategory giCategory);

    public int deleteGiCategoryById(Long categoryId);

    public int deleteGiCategoryByIds(Long[] categoryIds);

    public boolean checkCategoryNameUnique(GiCategory giCategory);

    /**
     * 构建前端所需要下拉树结构
     */
    public List<TreeSelect> buildCategoryTreeSelect();
}
