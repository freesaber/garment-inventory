package com.ruoyi.inventory.mapper;

import java.util.List;
import com.ruoyi.inventory.domain.GiCategory;

/**
 * 商品分类Mapper接口
 */
public interface GiCategoryMapper {
    
    /**
     * 查询商品分类
     */
    public GiCategory selectGiCategoryById(Long categoryId);

    /**
     * 查询商品分类列表
     */
    public List<GiCategory> selectGiCategoryList(GiCategory giCategory);

    /**
     * 新增商品分类
     */
    public int insertGiCategory(GiCategory giCategory);

    /**
     * 修改商品分类
     */
    public int updateGiCategory(GiCategory giCategory);

    /**
     * 删除商品分类
     */
    public int deleteGiCategoryById(Long categoryId);

    /**
     * 批量删除商品分类
     */
    public int deleteGiCategoryByIds(Long[] categoryIds);

    /**
     * 校验分类名称是否唯一
     */
    public GiCategory checkCategoryNameUnique(String categoryName, Long parentId);
}
