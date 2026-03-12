package com.ruoyi.inventory.mapper;

import java.util.List;
import com.ruoyi.inventory.domain.GiSku;

/**
 * 商品SKUMapper接口
 */
public interface GiSkuMapper {
    
    public GiSku selectGiSkuById(Long skuId);

    public List<GiSku> selectGiSkuList(GiSku giSku);

    public List<GiSku> selectGiSkuByGoodsId(Long goodsId);

    public int insertGiSku(GiSku giSku);

    public int updateGiSku(GiSku giSku);

    public int deleteGiSkuById(Long skuId);

    public int deleteGiSkuByIds(Long[] skuIds);

    public int deleteGiSkuByGoodsId(Long goodsId);

    public GiSku checkSkuCodeUnique(String skuCode);

    public GiSku selectGiSkuByBarcode(String barcode);
}
