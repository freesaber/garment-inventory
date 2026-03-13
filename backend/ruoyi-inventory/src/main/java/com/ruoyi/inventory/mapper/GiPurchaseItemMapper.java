package com.ruoyi.inventory.mapper;

import java.util.List;
import com.ruoyi.inventory.domain.GiPurchaseItem;

public interface GiPurchaseItemMapper {
    
    List<GiPurchaseItem> selectByPurchaseId(Long purchaseId);
    
    int insertBatch(List<GiPurchaseItem> list);
    
    int deleteByPurchaseId(Long purchaseId);
}
