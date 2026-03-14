package com.ruoyi.inventory.service;

import java.util.List;
import com.ruoyi.inventory.domain.GiStocktake;

/**
 * 库存盘点Service接口
 */
public interface IGiStocktakeService {
    
    /**
     * 查询盘点单列表
     */
    public List<GiStocktake> selectGiStocktakeList(GiStocktake giStocktake);

    /**
     * 查询盘点单详情
     */
    public GiStocktake selectGiStocktakeById(Long stocktakeId);

    /**
     * 创建盘点单
     */
    public int insertGiStocktake(GiStocktake giStocktake);

    /**
     * 更新盘点明细
     */
    public int updateItem(Long itemId, Integer actualQty);

    /**
     * 审核盘点单（调整库存）
     */
    public int auditStocktake(Long stocktakeId);

    /**
     * 取消盘点单
     */
    public int cancelStocktake(Long stocktakeId);

    /**
     * 删除盘点单
     */
    public int deleteGiStocktakeByIds(Long[] stocktakeIds);
}
