package com.ruoyi.inventory.service;

import java.util.List;
import com.ruoyi.inventory.domain.GiSale;

public interface IGiSaleService {
    
    List<GiSale> selectGiSaleList(GiSale giSale);
    
    GiSale selectGiSaleById(Long saleId);
    
    int insertGiSale(GiSale giSale);
    
    int updateGiSale(GiSale giSale);
    
    int deleteGiSaleByIds(Long[] saleIds);
}
