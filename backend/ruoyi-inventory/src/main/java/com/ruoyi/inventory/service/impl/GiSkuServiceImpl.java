package com.ruoyi.inventory.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.inventory.domain.GiSku;
import com.ruoyi.inventory.mapper.GiSkuMapper;
import com.ruoyi.inventory.service.IGiSkuService;

@Service
public class GiSkuServiceImpl implements IGiSkuService {

    @Autowired
    private GiSkuMapper giSkuMapper;

    @Override
    public GiSku selectGiSkuById(Long skuId) {
        return giSkuMapper.selectGiSkuById(skuId);
    }

    @Override
    public List<GiSku> selectGiSkuList(GiSku giSku) {
        return giSkuMapper.selectGiSkuList(giSku);
    }

    @Override
    public List<GiSku> selectGiSkuByGoodsId(Long goodsId) {
        GiSku query = new GiSku();
        query.setGoodsId(goodsId);
        return giSkuMapper.selectGiSkuList(query);
    }

    @Override
    public GiSku selectGiSkuByBarcode(String barcode) {
        GiSku query = new GiSku();
        query.setBarcode(barcode);
        List<GiSku> list = giSkuMapper.selectGiSkuList(query);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public int insertGiSku(GiSku giSku) {
        return giSkuMapper.insertGiSku(giSku);
    }

    @Override
    public int updateGiSku(GiSku giSku) {
        return giSkuMapper.updateGiSku(giSku);
    }

    @Override
    public int deleteGiSkuByIds(Long[] skuIds) {
        return giSkuMapper.deleteGiSkuByIds(skuIds);
    }

    @Override
    public boolean checkSkuCodeUnique(GiSku giSku) {
        Long skuId = giSku.getSkuId() == null ? -1L : giSku.getSkuId();
        GiSku info = giSkuMapper.checkSkuCodeUnique(giSku.getSkuCode());
        if (info != null && !info.getSkuId().equals(skuId)) {
            return false;
        }
        return true;
    }
}
