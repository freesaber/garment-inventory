package com.ruoyi.inventory.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.inventory.domain.GiTransfer;
import com.ruoyi.inventory.domain.GiTransferItem;
import com.ruoyi.inventory.domain.GiStock;
import com.ruoyi.inventory.domain.GiSku;
import com.ruoyi.inventory.mapper.GiTransferMapper;
import com.ruoyi.inventory.mapper.GiTransferItemMapper;
import com.ruoyi.inventory.mapper.GiStockMapper;
import com.ruoyi.inventory.mapper.GiSkuMapper;
import com.ruoyi.inventory.service.IGiTransferService;

@Service
public class GiTransferServiceImpl implements IGiTransferService {

    @Autowired
    private GiTransferMapper giTransferMapper;

    @Autowired
    private GiTransferItemMapper giTransferItemMapper;

    @Autowired
    private GiStockMapper giStockMapper;

    @Autowired
    private GiSkuMapper giSkuMapper;

    @Override
    public List<GiTransfer> selectGiTransferList(GiTransfer giTransfer) {
        List<GiTransfer> list = giTransferMapper.selectGiTransferList(giTransfer);
        for (GiTransfer transfer : list) {
            transfer.setItemList(giTransferItemMapper.selectByTransferId(transfer.getTransferId()));
        }
        return list;
    }

    @Override
    public GiTransfer selectGiTransferById(Long transferId) {
        GiTransfer transfer = giTransferMapper.selectGiTransferById(transferId);
        if (transfer != null) {
            transfer.setItemList(giTransferItemMapper.selectByTransferId(transferId));
        }
        return transfer;
    }

    @Override
    @Transactional
    public int insertGiTransfer(GiTransfer giTransfer) {
        // 生成调拨单号
        String transferNo = giTransferMapper.generateTransferNo();
        giTransfer.setTransferNo(transferNo);
        giTransfer.setStatus("0"); // 待审核
        giTransfer.setCreateBy(SecurityUtils.getUsername());
        
        // 计算总数量
        int totalQty = 0;
        for (GiTransferItem item : giTransfer.getItemList()) {
            totalQty += item.getQuantity();
        }
        giTransfer.setTotalQty(totalQty);
        
        // 插入调拨单
        int rows = giTransferMapper.insertGiTransfer(giTransfer);
        
        // 补充SKU信息并插入明细
        for (GiTransferItem item : giTransfer.getItemList()) {
            item.setTransferId(giTransfer.getTransferId());
            
            GiSku sku = giSkuMapper.selectGiSkuById(item.getSkuId());
            if (sku != null) {
                item.setSkuCode(sku.getSkuCode());
                item.setGoodsName(sku.getGoodsName());
                item.setColor(sku.getColor());
                item.setSize(sku.getSize());
            }
        }
        
        giTransferItemMapper.insertBatch(giTransfer.getItemList());
        return rows;
    }

    @Override
    @Transactional
    public int auditTransfer(Long transferId) {
        GiTransfer transfer = giTransferMapper.selectGiTransferById(transferId);
        if (transfer == null || !"0".equals(transfer.getStatus())) {
            throw new RuntimeException("调拨单不存在或状态不正确");
        }
        
        transfer.setStatus("1"); // 待出库
        transfer.setOutBy(SecurityUtils.getUsername());
        transfer.setOutTime(new java.util.Date());
        return giTransferMapper.updateGiTransfer(transfer);
    }

    @Override
    @Transactional
    public int outTransfer(Long transferId) {
        GiTransfer transfer = giTransferMapper.selectGiTransferById(transferId);
        if (transfer == null || !"1".equals(transfer.getStatus())) {
            throw new RuntimeException("调拨单不存在或状态不正确");
        }
        
        Long deptIdOut = transfer.getDeptIdOut();
        List<GiTransferItem> items = giTransferItemMapper.selectByTransferId(transferId);
        
        // 检查库存并扣减
        for (GiTransferItem item : items) {
            GiStock stock = giStockMapper.selectGiStockByDeptAndSku(deptIdOut, item.getSkuId());
            if (stock == null || stock.getQuantity() < item.getQuantity()) {
                throw new RuntimeException("SKU " + item.getSkuCode() + " 库存不足");
            }
            int newQty = stock.getQuantity() - item.getQuantity();
            giStockMapper.updateStockQuantity(stock.getStockId(), newQty);
            item.setOutQty(item.getQuantity());
        }
        
        transfer.setStatus("2"); // 待入库
        transfer.setOutBy(SecurityUtils.getUsername());
        transfer.setOutTime(new java.util.Date());
        return giTransferMapper.updateGiTransfer(transfer);
    }

    @Override
    @Transactional
    public int inTransfer(Long transferId) {
        GiTransfer transfer = giTransferMapper.selectGiTransferById(transferId);
        if (transfer == null || !"2".equals(transfer.getStatus())) {
            throw new RuntimeException("调拨单不存在或状态不正确");
        }
        
        Long deptIdIn = transfer.getDeptIdIn();
        List<GiTransferItem> items = giTransferItemMapper.selectByTransferId(transferId);
        
        // 增加调入门店库存
        for (GiTransferItem item : items) {
            GiStock stock = giStockMapper.selectGiStockByDeptAndSku(deptIdIn, item.getSkuId());
            if (stock != null) {
                int newQty = stock.getQuantity() + item.getQuantity();
                giStockMapper.updateStockQuantity(stock.getStockId(), newQty);
            } else {
                // 新增库存记录
                GiStock newStock = new GiStock();
                newStock.setDeptId(deptIdIn);
                newStock.setSkuId(item.getSkuId());
                newStock.setQuantity(item.getQuantity());
                newStock.setLockedQty(0);
                newStock.setWarningQty(10);
                giStockMapper.insertGiStock(newStock);
            }
            item.setInQty(item.getQuantity());
        }
        
        transfer.setStatus("3"); // 已完成
        transfer.setInBy(SecurityUtils.getUsername());
        transfer.setInTime(new java.util.Date());
        return giTransferMapper.updateGiTransfer(transfer);
    }

    @Override
    @Transactional
    public int cancelTransfer(Long transferId) {
        GiTransfer transfer = giTransferMapper.selectGiTransferById(transferId);
        if (transfer == null || !"0".equals(transfer.getStatus())) {
            throw new RuntimeException("调拨单不存在或只有待审核状态可取消");
        }
        
        transfer.setStatus("4"); // 已取消
        return giTransferMapper.updateGiTransfer(transfer);
    }

    @Override
    @Transactional
    public int deleteGiTransferByIds(Long[] transferIds) {
        for (Long transferId : transferIds) {
            GiTransfer transfer = giTransferMapper.selectGiTransferById(transferId);
            if ("2".equals(transfer.getStatus()) || "3".equals(transfer.getStatus())) {
                throw new RuntimeException("调拨单 " + transfer.getTransferNo() + " 已出库，无法删除");
            }
            giTransferItemMapper.deleteByTransferId(transferId);
        }
        return giTransferMapper.deleteGiTransferByIds(transferIds);
    }
}
