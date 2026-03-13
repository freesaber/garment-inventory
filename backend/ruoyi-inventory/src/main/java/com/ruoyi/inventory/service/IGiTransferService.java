package com.ruoyi.inventory.service;

import java.util.List;
import com.ruoyi.inventory.domain.GiTransfer;

/**
 * 库存调拨Service接口
 */
public interface IGiTransferService {
    
    /**
     * 查询调拨单列表
     */
    public List<GiTransfer> selectGiTransferList(GiTransfer giTransfer);

    /**
     * 查询调拨单详情
     */
    public GiTransfer selectGiTransferById(Long transferId);

    /**
     * 创建调拨单
     */
    public int insertGiTransfer(GiTransfer giTransfer);

    /**
     * 审核调拨单
     */
    public int auditTransfer(Long transferId);

    /**
     * 出库
     */
    public int outTransfer(Long transferId);

    /**
     * 入库
     */
    public int inTransfer(Long transferId);

    /**
     * 取消调拨单
     */
    public int cancelTransfer(Long transferId);

    /**
     * 删除调拨单
     */
    public int deleteGiTransferByIds(Long[] transferIds);
}
