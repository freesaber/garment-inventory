package com.ruoyi.inventory.mapper;

import java.util.List;
import com.ruoyi.inventory.domain.GiStock;

/**
 * 店铺库存Mapper接口
 */
public interface GiStockMapper {
    
    public GiStock selectGiStockById(Long stockId);

    public GiStock selectGiStockByDeptAndSku(Long deptId, Long skuId);

    public List<GiStock> selectGiStockList(GiStock giStock);

    public List<GiStock> selectWarningStockList(GiStock giStock);

    public int insertGiStock(GiStock giStock);

    public int updateGiStock(GiStock giStock);

    public int updateStockQuantity(Long stockId, Integer quantity);

    public int deleteGiStockById(Long stockId);

    public int batchInsertGiStock(List<GiStock> list);
}
