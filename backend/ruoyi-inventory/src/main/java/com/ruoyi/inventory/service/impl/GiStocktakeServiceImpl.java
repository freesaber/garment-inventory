package com.ruoyi.inventory.service.impl;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.inventory.domain.GiStocktake;
import com.ruoyi.inventory.domain.GiStocktakeItem;
import com.ruoyi.inventory.domain.GiStock;
import com.ruoyi.inventory.mapper.GiStocktakeMapper;
import com.ruoyi.inventory.mapper.GiStocktakeItemMapper;
import com.ruoyi.inventory.mapper.GiStockMapper;
import com.ruoyi.inventory.service.IGiStocktakeService;

@Service
public class GiStocktakeServiceImpl implements IGiStocktakeService {

    @Autowired
    private GiStocktakeMapper giStocktakeMapper;

    @Autowired
    private GiStocktakeItemMapper giStocktakeItemMapper;

    @Autowired
    private GiStockMapper giStockMapper;

    @Override
    public List<GiStocktake> selectGiStocktakeList(GiStocktake giStocktake) {
        List<GiStocktake> list = giStocktakeMapper.selectGiStocktakeList(giStocktake);
        for (GiStocktake stocktake : list) {
            stocktake.setItemList(giStocktakeItemMapper.selectByStocktakeId(stocktake.getStocktakeId()));
        }
        return list;
    }

    @Override
    public GiStocktake selectGiStocktakeById(Long stocktakeId) {
        GiStocktake stocktake = giStocktakeMapper.selectGiStocktakeById(stocktakeId);
        if (stocktake != null) {
            stocktake.setItemList(giStocktakeItemMapper.selectByStocktakeId(stocktakeId));
        }
        return stocktake;
    }

    @Override
    @Transactional
    public int insertGiStocktake(GiStocktake giStocktake) {
        // 生成盘点单号
        String stocktakeNo = giStocktakeMapper.generateStocktakeNo();
        giStocktake.setStocktakeNo(stocktakeNo);
        giStocktake.setStatus("0"); // 待审核
        giStocktake.setDeptId(SecurityUtils.getLoginUser().getDeptId());
        giStocktake.setCreateBy(SecurityUtils.getUsername());
        
        // 计算汇总数据
        int itemCount = giStocktake.getItemList().size();
        BigDecimal diffAmount = BigDecimal.ZERO;
        
        for (GiStocktakeItem item : giStocktake.getItemList()) {
            item.setDiffQty(item.getActualQty() - item.getSystemQty());
            if (item.getCostPrice() != null) {
                item.setDiffAmount(item.getCostPrice().multiply(new BigDecimal(item.getDiffQty())));
                diffAmount = diffAmount.add(item.getDiffAmount());
            }
        }
        
        giStocktake.setItemCount(itemCount);
        giStocktake.setDiffAmount(diffAmount);
        
        // 插入盘点单
        int rows = giStocktakeMapper.insertGiStocktake(giStocktake);
        
        // 设置盘点单ID并插入明细
        for (GiStocktakeItem item : giStocktake.getItemList()) {
            item.setStocktakeId(giStocktake.getStocktakeId());
        }
        giStocktakeItemMapper.insertBatch(giStocktake.getItemList());
        
        return rows;
    }

    @Override
    @Transactional
    public int updateItem(Long itemId, Integer actualQty) {
        // 这里简化处理，实际应该重新计算盈亏
        GiStocktakeItem item = new GiStocktakeItem();
        item.setItemId(itemId);
        item.setActualQty(actualQty);
        // 需要查询原数据计算 diffQty 和 diffAmount
        return giStocktakeItemMapper.updateItem(item);
    }

    @Override
    @Transactional
    public int auditStocktake(Long stocktakeId) {
        GiStocktake stocktake = giStocktakeMapper.selectGiStocktakeById(stocktakeId);
        if (stocktake == null || !"0".equals(stocktake.getStatus())) {
            throw new RuntimeException("盘点单不存在或状态不正确");
        }
        
        // 调整库存
        List<GiStocktakeItem> items = giStocktakeItemMapper.selectByStocktakeId(stocktakeId);
        Long deptId = stocktake.getDeptId();
        
        for (GiStocktakeItem item : items) {
            if (item.getDiffQty() == null || item.getDiffQty() == 0) {
                continue;
            }
            
            GiStock stock = giStockMapper.selectGiStockByDeptAndSku(deptId, item.getSkuId());
            if (stock != null) {
                int newQty = stock.getQuantity() + item.getDiffQty();
                giStockMapper.updateStockQuantity(stock.getStockId(), newQty);
            } else if (item.getActualQty() > 0) {
                // 新增库存记录
                GiStock newStock = new GiStock();
                newStock.setDeptId(deptId);
                newStock.setSkuId(item.getSkuId());
                newStock.setQuantity(item.getActualQty());
                newStock.setLockedQty(0);
                newStock.setWarningQty(10);
                giStockMapper.insertGiStock(newStock);
            }
        }
        
        // 更新状态
        stocktake.setStatus("1"); // 已完成
        stocktake.setAuditBy(SecurityUtils.getUsername());
        stocktake.setAuditTime(new java.util.Date());
        return giStocktakeMapper.updateGiStocktake(stocktake);
    }

    @Override
    @Transactional
    public int cancelStocktake(Long stocktakeId) {
        GiStocktake stocktake = giStocktakeMapper.selectGiStocktakeById(stocktakeId);
        if (stocktake == null || !"0".equals(stocktake.getStatus())) {
            throw new RuntimeException("盘点单不存在或只有待审核状态可取消");
        }
        
        stocktake.setStatus("2"); // 已取消
        return giStocktakeMapper.updateGiStocktake(stocktake);
    }

    @Override
    @Transactional
    public int deleteGiStocktakeByIds(Long[] stocktakeIds) {
        for (Long stocktakeId : stocktakeIds) {
            GiStocktake stocktake = giStocktakeMapper.selectGiStocktakeById(stocktakeId);
            if ("1".equals(stocktake.getStatus())) {
                throw new RuntimeException("盘点单 " + stocktake.getStocktakeNo() + " 已完成，无法删除");
            }
            giStocktakeItemMapper.deleteByStocktakeId(stocktakeId);
        }
        return giStocktakeMapper.deleteGiStocktakeByIds(stocktakeIds);
    }
}
