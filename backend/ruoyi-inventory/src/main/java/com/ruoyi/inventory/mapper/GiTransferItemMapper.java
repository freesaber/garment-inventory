package com.ruoyi.inventory.mapper;

import java.util.List;
import com.ruoyi.inventory.domain.GiTransferItem;

/**
 * 调拨单明细Mapper接口
 */
public interface GiTransferItemMapper {
    
    /**
     * 根据调拨单ID查询明细
     */
    public List<GiTransferItem> selectByTransferId(Long transferId);

    /**
     * 批量插入明细
     */
    public int insertBatch(List<GiTransferItem> list);

    /**
     * 删除调拨单明细
     */
    public int deleteByTransferId(Long transferId);
}
