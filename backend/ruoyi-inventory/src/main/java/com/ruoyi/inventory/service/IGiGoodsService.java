package com.ruoyi.inventory.service;

import java.util.List;
import com.ruoyi.inventory.domain.GiGoods;

/**
 * 商品Service接口
 */
public interface IGiGoodsService {
    
    public GiGoods selectGiGoodsById(Long goodsId);

    public List<GiGoods> selectGiGoodsList(GiGoods giGoods);

    public int insertGiGoods(GiGoods giGoods);

    public int updateGiGoods(GiGoods giGoods);

    public int deleteGiGoodsByIds(Long[] goodsIds);

    public boolean checkGoodsCodeUnique(GiGoods giGoods);
}
