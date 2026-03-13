package com.ruoyi.inventory.mapper;

import java.util.List;
import com.ruoyi.inventory.domain.GiSale;

public interface GiSaleMapper {
    
    List<GiSale> selectGiSaleList(GiSale giSale);
    
    GiSale selectGiSaleById(Long saleId);
    
    int insertGiSale(GiSale giSale);
    
    int updateGiSale(GiSale giSale);
    
    int deleteGiSaleById(Long saleId);
    
    int deleteGiSaleByIds(Long[] saleIds);
    
    String generateSaleNo();
}
