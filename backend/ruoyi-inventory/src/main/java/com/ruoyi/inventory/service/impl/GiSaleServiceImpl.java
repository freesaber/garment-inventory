package com.ruoyi.inventory.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.inventory.domain.GiSale;
import com.ruoyi.inventory.domain.GiSaleItem;
import com.ruoyi.inventory.mapper.GiSaleMapper;
import com.ruoyi.inventory.mapper.GiSaleItemMapper;
import com.ruoyi.inventory.mapper.GiStockMapper;
import com.ruoyi.inventory.mapper.GiSkuMapper;
import com.ruoyi.inventory.domain.GiStock;
import com.ruoyi.inventory.domain.GiSku;
import com.ruoyi.inventory.service.IGiSaleService;

@Service
public class GiSaleServiceImpl implements IGiSaleService {

    @Autowired
    private GiSaleMapper giSaleMapper;

    @Autowired
    private GiSaleItemMapper giSaleItemMapper;

    @Autowired
    private GiStockMapper giStockMapper;

    @Autowired
    private GiSkuMapper giSkuMapper;

    @Override
    public List<GiSale> selectGiSaleList(GiSale giSale) {
        List<GiSale> list = giSaleMapper.selectGiSaleList(giSale);
        for (GiSale sale : list) {
            sale.setItemList(giSaleItemMapper.selectBySaleId(sale.getSaleId()));
        }
        return list;
    }

    @Override
    public GiSale selectGiSaleById(Long saleId) {
        GiSale sale = giSaleMapper.selectGiSaleById(saleId);
        if (sale != null) {
            sale.setItemList(giSaleItemMapper.selectBySaleId(saleId));
        }
        return sale;
    }

    @Override
    @Transactional
    public int insertGiSale(GiSale giSale) {
        // 生成销售单号
        String saleNo = giSaleMapper.generateSaleNo();
        giSale.setSaleNo(saleNo);
        giSale.setDeptId(SecurityUtils.getLoginUser().getDeptId());
        giSale.setStatus("0");
        
        // 计算总金额
        java.math.BigDecimal totalAmount = java.math.BigDecimal.ZERO;
        for (GiSaleItem item : giSale.getItemList()) {
            item.setAmount(item.getPrice().multiply(new java.math.BigDecimal(item.getQuantity())));
            totalAmount = totalAmount.add(item.getAmount());
        }
        giSale.setTotalAmount(totalAmount);
        if (giSale.getPayAmount() == null) {
            giSale.setPayAmount(totalAmount);
        }
        if (giSale.getDiscountAmount() == null) {
            giSale.setDiscountAmount(java.math.BigDecimal.ZERO);
        }
        
        // 插入销售单
        int rows = giSaleMapper.insertGiSale(giSale);
        
        // 插入明细并扣减库存
        for (GiSaleItem item : giSale.getItemList()) {
            item.setSaleId(giSale.getSaleId());
            
            // 查询SKU信息补充商品名称等
            GiSku sku = giSkuMapper.selectGiSkuById(item.getSkuId());
            if (sku != null) {
                item.setSkuCode(sku.getSkuCode());
                item.setGoodsName(sku.getGoodsName());
                item.setColor(sku.getColor());
                item.setSize(sku.getSize());
            }
            
            // 扣减库存
            Long deptId = giSale.getDeptId();
            GiStock stock = giStockMapper.selectGiStockByDeptAndSku(deptId, item.getSkuId());
            if (stock != null) {
                int newQty = stock.getQuantity() - item.getQuantity();
                if (newQty < 0) {
                    throw new RuntimeException("库存不足: " + item.getSkuCode());
                }
                stock.setQuantity(newQty);
                giStockMapper.updateStockQuantity(stock.getStockId(), newQty);
            }
        }
        
        // 批量插入明细
        giSaleItemMapper.insertBatch(giSale.getItemList());
        
        return rows;
    }

    @Override
    public int updateGiSale(GiSale giSale) {
        return giSaleMapper.updateGiSale(giSale);
    }

    @Override
    @Transactional
    public int deleteGiSaleByIds(Long[] saleIds) {
        for (Long saleId : saleIds) {
            giSaleItemMapper.deleteBySaleId(saleId);
        }
        return giSaleMapper.deleteGiSaleByIds(saleIds);
    }
}
