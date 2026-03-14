package com.ruoyi.inventory.mapper;

import java.util.List;
import com.ruoyi.inventory.domain.GiStocktake;

/**
 * 库存盘点单Mapper接口
 */
public interface GiStocktakeMapper {
    
    /**
     * 查询盘点单列表
     */
    public List<GiStocktake> selectGiStocktakeList(GiStocktake giStocktake);

    /**
     * 查询盘点单详情
     */
    public GiStocktake selectGiStocktakeById(Long stocktakeId);

    /**
     * 新增盘点单
     */
    public int insertGiStocktake(GiStocktake giStocktake);

    /**
     * 修改盘点单
     */
    public int updateGiStocktake(GiStocktake giStocktake);

    /**
     * 删除盘点单
     */
    public int deleteGiStocktakeByIds(Long[] stocktakeIds);

    /**
     * 生成盘点单号
     */
    public String generateStocktakeNo();
}
