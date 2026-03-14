package com.ruoyi.inventory.mapper;

import java.util.List;
import com.ruoyi.inventory.domain.GiStocktakeItem;

/**
 * 盘点单明细Mapper接口
 */
public interface GiStocktakeItemMapper {
    
    /**
     * 根据盘点单ID查询明细
     */
    public List<GiStocktakeItem> selectByStocktakeId(Long stocktakeId);

    /**
     * 批量插入明细
     */
    public int insertBatch(List<GiStocktakeItem> list);

    /**
     * 更新明细
     */
    public int updateItem(GiStocktakeItem item);

    /**
     * 删除盘点单明细
     */
    public int deleteByStocktakeId(Long stocktakeId);
}
