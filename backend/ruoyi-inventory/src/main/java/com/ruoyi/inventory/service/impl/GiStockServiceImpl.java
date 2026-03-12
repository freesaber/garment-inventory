package com.ruoyi.inventory.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.inventory.domain.GiStock;
import com.ruoyi.inventory.mapper.GiStockMapper;
import com.ruoyi.inventory.service.IGiStockService;

@Service
public class GiStockServiceImpl implements IGiStockService {

    @Autowired
    private GiStockMapper giStockMapper;

    @Override
    public GiStock selectGiStockById(Long stockId) {
        return giStockMapper.selectGiStockById(stockId);
    }

    @Override
    public GiStock selectGiStockByDeptAndSku(Long deptId, Long skuId) {
        return giStockMapper.selectGiStockByDeptAndSku(deptId, skuId);
    }

    @Override
    public List<GiStock> selectGiStockList(GiStock giStock) {
        return giStockMapper.selectGiStockList(giStock);
    }

    @Override
    public List<GiStock> selectWarningStockList(GiStock giStock) {
        return giStockMapper.selectWarningStockList(giStock);
    }

    @Override
    public int insertGiStock(GiStock giStock) {
        return giStockMapper.insertGiStock(giStock);
    }

    @Override
    public int updateGiStock(GiStock giStock) {
        return giStockMapper.updateGiStock(giStock);
    }

    @Override
    public int updateStockQuantity(Long stockId, Integer quantity) {
        return giStockMapper.updateStockQuantity(stockId, quantity);
    }

    @Override
    public int deleteGiStockById(Long stockId) {
        return giStockMapper.deleteGiStockById(stockId);
    }
}
