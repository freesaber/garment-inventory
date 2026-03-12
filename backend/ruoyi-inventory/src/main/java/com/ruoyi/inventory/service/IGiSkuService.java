package com.ruoyi.inventory.service;

import java.util.List;
import com.ruoyi.inventory.domain.GiSku;

public interface IGiSkuService {
    
    public GiSku selectGiSkuById(Long skuId);

    public List<GiSku> selectGiSkuList(GiSku giSku);

    public List<GiSku> selectGiSkuByGoodsId(Long goodsId);

    public int insertGiSku(GiSku giSku);

    public int updateGiSku(GiSku giSku);

    public int deleteGiSkuById(Long skuId);

    public int deleteGiSkuByIds(Long[] skuIds);

    public boolean checkSkuCodeUnique(GiSku giSku);

    public GiSku selectGiSkuByBarcode(String barcode);
}
