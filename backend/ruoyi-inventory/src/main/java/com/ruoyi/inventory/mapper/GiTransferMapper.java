package com.ruoyi.inventory.mapper;

import java.util.List;
import com.ruoyi.inventory.domain.GiTransfer;

/**
 * 库存调拨单Mapper接口
 */
public interface GiTransferMapper {
    
    /**
     * 查询调拨单列表
     */
    public List<GiTransfer> selectGiTransferList(GiTransfer giTransfer);

    /**
     * 查询调拨单详情
     */
    public GiTransfer selectGiTransferById(Long transferId);

    /**
     * 新增调拨单
     */
    public int insertGiTransfer(GiTransfer giTransfer);

    /**
     * 修改调拨单
     */
    public int updateGiTransfer(GiTransfer giTransfer);

    /**
     * 删除调拨单
     */
    public int deleteGiTransferByIds(Long[] transferIds);

    /**
     * 生成调拨单号
     */
    public String generateTransferNo();
}
