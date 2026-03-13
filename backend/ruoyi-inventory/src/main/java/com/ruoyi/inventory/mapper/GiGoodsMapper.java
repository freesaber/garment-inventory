package com.ruoyi.inventory.mapper;

import java.util.List;
import com.ruoyi.inventory.domain.GiGoods;

/**
 * 商品Mapper接口
 */
public interface GiGoodsMapper {
    
    public GiGoods selectGiGoodsById(Long goodsId);

    public List<GiGoods> selectGiGoodsList(GiGoods giGoods);

    public int insertGiGoods(GiGoods giGoods);

    public int updateGiGoods(GiGoods giGoods);

    public int deleteGiGoodsById(Long goodsId);

    public int deleteGiGoodsByIds(Long[] goodsIds);

    public GiGoods checkGoodsCodeUnique(String goodsCode);

    /**
     * 查询指定前缀的最大序号
     */
    public int getMaxGoodsSeq(String prefix);
}
