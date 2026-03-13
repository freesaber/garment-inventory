package com.ruoyi.inventory.mapper;

import java.util.List;
import com.ruoyi.inventory.domain.GiPurchase;

public interface GiPurchaseMapper {
    
    List<GiPurchase> selectGiPurchaseList(GiPurchase giPurchase);
    
    GiPurchase selectGiPurchaseById(Long purchaseId);
    
    int insertGiPurchase(GiPurchase giPurchase);
    
    int updateGiPurchase(GiPurchase giPurchase);
    
    int deleteGiPurchaseByIds(Long[] purchaseIds);
    
    String generatePurchaseNo();
}
