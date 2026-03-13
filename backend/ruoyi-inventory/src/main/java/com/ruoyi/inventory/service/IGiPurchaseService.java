package com.ruoyi.inventory.service;

import java.util.List;
import com.ruoyi.inventory.domain.GiPurchase;

public interface IGiPurchaseService {
    
    List<GiPurchase> selectGiPurchaseList(GiPurchase giPurchase);
    
    GiPurchase selectGiPurchaseById(Long purchaseId);
    
    int insertGiPurchase(GiPurchase giPurchase);
    
    int confirmPurchase(Long purchaseId);
    
    int cancelPurchase(Long purchaseId);
    
    int deleteGiPurchaseByIds(Long[] purchaseIds);
}
