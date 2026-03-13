package com.ruoyi.inventory.mapper;

import java.util.List;
import com.ruoyi.inventory.domain.GiSaleItem;

public interface GiSaleItemMapper {
    
    List<GiSaleItem> selectBySaleId(Long saleId);
    
    int insertBatch(List<GiSaleItem> list);
    
    int deleteBySaleId(Long saleId);
}
