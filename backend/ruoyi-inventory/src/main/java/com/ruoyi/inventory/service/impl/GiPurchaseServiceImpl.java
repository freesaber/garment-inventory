package com.ruoyi.inventory.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.inventory.domain.GiPurchase;
import com.ruoyi.inventory.domain.GiPurchaseItem;
import com.ruoyi.inventory.domain.GiStock;
import com.ruoyi.inventory.domain.GiSku;
import com.ruoyi.inventory.mapper.GiPurchaseMapper;
import com.ruoyi.inventory.mapper.GiPurchaseItemMapper;
import com.ruoyi.inventory.mapper.GiStockMapper;
import com.ruoyi.inventory.mapper.GiSkuMapper;
import com.ruoyi.inventory.service.IGiPurchaseService;

@Service
public class GiPurchaseServiceImpl implements IGiPurchaseService {

    @Autowired
    private GiPurchaseMapper giPurchaseMapper;

    @Autowired
    private GiPurchaseItemMapper giPurchaseItemMapper;

    @Autowired
    private GiStockMapper giStockMapper;

    @Autowired
    private GiSkuMapper giSkuMapper;

    @Override
    public List<GiPurchase> selectGiPurchaseList(GiPurchase giPurchase) {
        List<GiPurchase> list = giPurchaseMapper.selectGiPurchaseList(giPurchase);
        for (GiPurchase purchase : list) {
            purchase.setItemList(giPurchaseItemMapper.selectByPurchaseId(purchase.getPurchaseId()));
        }
        return list;
    }

    @Override
    public GiPurchase selectGiPurchaseById(Long purchaseId) {
        GiPurchase purchase = giPurchaseMapper.selectGiPurchaseById(purchaseId);
        if (purchase != null) {
            purchase.setItemList(giPurchaseItemMapper.selectByPurchaseId(purchaseId));
        }
        return purchase;
    }

    @Override
    @Transactional
    public int insertGiPurchase(GiPurchase giPurchase) {
        // 生成采购单号
        String purchaseNo = giPurchaseMapper.generatePurchaseNo();
        giPurchase.setPurchaseNo(purchaseNo);
        giPurchase.setStatus("0"); // 待入库
        giPurchase.setDeptId(SecurityUtils.getLoginUser().getDeptId());
        
        // 计算总金额
        java.math.BigDecimal totalAmount = java.math.BigDecimal.ZERO;
        for (GiPurchaseItem item : giPurchase.getItemList()) {
            item.setAmount(item.getPrice().multiply(new java.math.BigDecimal(item.getQuantity())));
            totalAmount = totalAmount.add(item.getAmount());
        }
        giPurchase.setTotalAmount(totalAmount);
        
        // 插入采购单
        int rows = giPurchaseMapper.insertGiPurchase(giPurchase);
        
        // 补充SKU信息并插入明细
        for (GiPurchaseItem item : giPurchase.getItemList()) {
            item.setPurchaseId(giPurchase.getPurchaseId());
            
            GiSku sku = giSkuMapper.selectGiSkuById(item.getSkuId());
            if (sku != null) {
                item.setSkuCode(sku.getSkuCode());
                item.setGoodsName(sku.getGoodsName());
                item.setColor(sku.getColor());
                item.setSize(sku.getSize());
            }
        }
        
        giPurchaseItemMapper.insertBatch(giPurchase.getItemList());
        return rows;
    }

    @Override
    @Transactional
    public int confirmPurchase(Long purchaseId) {
        GiPurchase purchase = giPurchaseMapper.selectGiPurchaseById(purchaseId);
        if (purchase == null || !"0".equals(purchase.getStatus())) {
            throw new RuntimeException("采购单不存在或状态不正确");
        }
        
        // 更新状态为已入库
        purchase.setStatus("1");
        giPurchaseMapper.updateGiPurchase(purchase);
        
        // 增加库存
        List<GiPurchaseItem> items = giPurchaseItemMapper.selectByPurchaseId(purchaseId);
        Long deptId = purchase.getDeptId();
        
        for (GiPurchaseItem item : items) {
            GiStock stock = giStockMapper.selectGiStockByDeptAndSku(deptId, item.getSkuId());
            if (stock != null) {
                int newQty = stock.getQuantity() + item.getQuantity();
                giStockMapper.updateStockQuantity(stock.getStockId(), newQty);
            } else {
                // 新增库存记录
                GiStock newStock = new GiStock();
                newStock.setDeptId(deptId);
                newStock.setSkuId(item.getSkuId());
                newStock.setQuantity(item.getQuantity());
                newStock.setLockedQty(0);
                newStock.setWarningQty(10);
                giStockMapper.insertGiStock(newStock);
            }
        }
        
        return 1;
    }

    @Override
    @Transactional
    public int cancelPurchase(Long purchaseId) {
        GiPurchase purchase = giPurchaseMapper.selectGiPurchaseById(purchaseId);
        if (purchase == null || !"0".equals(purchase.getStatus())) {
            throw new RuntimeException("采购单不存在或状态不正确");
        }
        
        purchase.setStatus("2"); // 已取消
        return giPurchaseMapper.updateGiPurchase(purchase);
    }

    @Override
    @Transactional
    public int deleteGiPurchaseByIds(Long[] purchaseIds) {
        for (Long purchaseId : purchaseIds) {
            giPurchaseItemMapper.deleteByPurchaseId(purchaseId);
        }
        return giPurchaseMapper.deleteGiPurchaseByIds(purchaseIds);
    }
}
