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
        return giSkuMapper.selectGiSkuByGoodsId(goodsId);
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
    public int deleteGiSkuById(Long skuId) {
        return giSkuMapper.deleteGiSkuById(skuId);
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

    @Override
    public GiSku selectGiSkuByBarcode(String barcode) {
        return giSkuMapper.selectGiSkuByBarcode(barcode);
    }
}
