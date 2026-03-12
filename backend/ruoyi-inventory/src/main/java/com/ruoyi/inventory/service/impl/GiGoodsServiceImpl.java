package com.ruoyi.inventory.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.inventory.domain.GiGoods;
import com.ruoyi.inventory.mapper.GiGoodsMapper;
import com.ruoyi.inventory.service.IGiGoodsService;

@Service
public class GiGoodsServiceImpl implements IGiGoodsService {

    @Autowired
    private GiGoodsMapper giGoodsMapper;

    @Override
    public GiGoods selectGiGoodsById(Long goodsId) {
        return giGoodsMapper.selectGiGoodsById(goodsId);
    }

    @Override
    public List<GiGoods> selectGiGoodsList(GiGoods giGoods) {
        return giGoodsMapper.selectGiGoodsList(giGoods);
    }

    @Override
    public int insertGiGoods(GiGoods giGoods) {
        return giGoodsMapper.insertGiGoods(giGoods);
    }

    @Override
    public int updateGiGoods(GiGoods giGoods) {
        return giGoodsMapper.updateGiGoods(giGoods);
    }

    @Override
    public int deleteGiGoodsById(Long goodsId) {
        return giGoodsMapper.deleteGiGoodsById(goodsId);
    }

    @Override
    public int deleteGiGoodsByIds(Long[] goodsIds) {
        return giGoodsMapper.deleteGiGoodsByIds(goodsIds);
    }

    @Override
    public boolean checkGoodsCodeUnique(GiGoods giGoods) {
        Long goodsId = giGoods.getGoodsId() == null ? -1L : giGoods.getGoodsId();
        GiGoods info = giGoodsMapper.checkGoodsCodeUnique(giGoods.getGoodsCode());
        if (info != null && !info.getGoodsId().equals(goodsId)) {
            return false;
        }
        return true;
    }
}
