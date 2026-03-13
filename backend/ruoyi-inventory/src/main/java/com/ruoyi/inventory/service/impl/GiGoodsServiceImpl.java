package com.ruoyi.inventory.service.impl;

import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.inventory.domain.GiCategory;
import com.ruoyi.inventory.domain.GiGoods;
import com.ruoyi.inventory.domain.GiSku;
import com.ruoyi.inventory.mapper.GiCategoryMapper;
import com.ruoyi.inventory.mapper.GiGoodsMapper;
import com.ruoyi.inventory.mapper.GiSkuMapper;
import com.ruoyi.inventory.service.IGiGoodsService;
import com.ruoyi.system.service.ISysDictDataService;

/**
 * 商品Service实现
 */
@Service
public class GiGoodsServiceImpl implements IGiGoodsService {

    @Autowired
    private GiGoodsMapper giGoodsMapper;

    @Autowired
    private GiSkuMapper giSkuMapper;

    @Autowired
    private GiCategoryMapper giCategoryMapper;

    @Autowired
    private ISysDictDataService sysDictDataService;

    @Override
    public GiGoods selectGiGoodsById(Long goodsId) {
        GiGoods goods = giGoodsMapper.selectGiGoodsById(goodsId);
        if (goods != null) {
            GiSku skuQuery = new GiSku();
            skuQuery.setGoodsId(goodsId);
            goods.setSkuList(giSkuMapper.selectGiSkuList(skuQuery));
        }
        return goods;
    }

    @Override
    public List<GiGoods> selectGiGoodsList(GiGoods giGoods) {
        return giGoodsMapper.selectGiGoodsList(giGoods);
    }

    @Override
    @Transactional
    public int insertGiGoods(GiGoods giGoods) {
        // 自动生成款号（如果未提供）
        if (StringUtils.isEmpty(giGoods.getGoodsCode())) {
            giGoods.setGoodsCode(generateGoodsNumber(giGoods));
        }
        int rows = giGoodsMapper.insertGiGoods(giGoods);
        insertGiSku(giGoods);
        return rows;
    }

    @Override
    @Transactional
    public int updateGiGoods(GiGoods giGoods) {
        giSkuMapper.deleteGiSkuByGoodsId(giGoods.getGoodsId());
        insertGiSku(giGoods);
        return giGoodsMapper.updateGiGoods(giGoods);
    }

    @Override
    @Transactional
    public int deleteGiGoodsByIds(Long[] goodsIds) {
        for (Long goodsId : goodsIds) {
            giSkuMapper.deleteGiSkuByGoodsId(goodsId);
        }
        return giGoodsMapper.deleteGiGoodsByIds(goodsIds);
    }

    @Override
    public boolean checkGoodsCodeUnique(GiGoods giGoods) {
        Long goodsId = giGoods.getGoodsId() == null ? -1L : giGoods.getGoodsId();
        GiGoods info = giGoodsMapper.checkGoodsCodeUnique(giGoods.getGoodsCode());
        return info == null || info.getGoodsId().equals(goodsId);
    }

    /**
     * 生成款号
     * 格式：年份(2位) + 季度(1位) + 品类编码(1位) + 序号(3位)
     * 示例：261T001 = 2026年 + 第1季度 + T恤(T) + 001号
     */
    private String generateGoodsNumber(GiGoods giGoods) {
        // 年份后2位
        String year = String.valueOf(LocalDate.now().getYear()).substring(2);
        
        // 季度（1-4）
        String quarter = giGoods.getSeason() != null ? giGoods.getSeason() : "1";
        
        // 品类编码（默认T）
        String kindCode = "T";
        if (giGoods.getCategoryId() != null) {
            // 这里可以查询分类的kind_code，简化处理用默认值
            kindCode = getKindCode(giGoods.getCategoryId());
        }
        
        // 生成前缀
        String prefix = year + quarter + kindCode;
        
        // 查询当前最大序号
        int maxSeq = giGoodsMapper.getMaxGoodsSeq(prefix);
        String seq = String.format("%03d", maxSeq + 1);
        
        return prefix + seq;
    }

    /**
     * 根据分类ID获取品类编码
     */
    private String getKindCode(Long categoryId) {
        if (categoryId == null) {
            return "T";
        }
        GiCategory category = giCategoryMapper.selectGiCategoryById(categoryId);
        if (category != null && StringUtils.isNotEmpty(category.getKindCode())) {
            return category.getKindCode();
        }
        return "T"; // 默认
    }

    /**
     * 批量插入SKU，自动生成条码
     * 条码格式：款号 + 颜色编码(2位) + 尺码编码(1-3位)
     * 示例：261T00101S = 款号261T001 + 颜色01(白色) + 尺码S
     */
    private void insertGiSku(GiGoods giGoods) {
        if (giGoods.getSkuList() != null && !giGoods.getSkuList().isEmpty()) {
            for (GiSku sku : giGoods.getSkuList()) {
                sku.setGoodsId(giGoods.getGoodsId());
                
                String goodsCode = giGoods.getGoodsCode();
                
                // 生成SKU编码：款号-颜色-尺码
                String skuCode = goodsCode;
                if (StringUtils.isNotEmpty(sku.getColor())) {
                    skuCode += "-" + sku.getColor();
                }
                if (StringUtils.isNotEmpty(sku.getSize())) {
                    skuCode += "-" + sku.getSize();
                }
                sku.setSkuCode(skuCode);
                
                // 自动生成条码（如果未提供）
                if (StringUtils.isEmpty(sku.getBarcode())) {
                    String barcode = generateBarcode(goodsCode, sku.getColor(), sku.getSize());
                    sku.setBarcode(barcode);
                }
                
                giSkuMapper.insertGiSku(sku);
            }
        }
    }

    /**
     * 生成条码
     * 格式：款号 + 颜色编码 + 尺码编码
     */
    private String generateBarcode(String goodsCode, String color, String size) {
        StringBuilder barcode = new StringBuilder(goodsCode);

        // 颜色编码（从字典查询，2位）
        if (StringUtils.isNotEmpty(color)) {
            String colorCode = getColorCode(color);
            barcode.append(colorCode);
        }

        // 尺码编码（从字典查询，1位数字）
        if (StringUtils.isNotEmpty(size)) {
            String sizeCode = getSizeCode(size);
            barcode.append(sizeCode);
        }

        return barcode.toString();
    }

    /**
     * 获取颜色编码（2位）- 从字典查询
     */
    private String getColorCode(String color) {
        if (StringUtils.isEmpty(color)) {
            return "99";
        }
        // 从字典 gi_color 中查询颜色编码
        SysDictData query = new SysDictData();
        query.setDictType("gi_color");
        List<SysDictData> dictList = sysDictDataService.selectDictDataList(query);
        for (SysDictData dict : dictList) {
            if (color.equals(dict.getDictLabel())) {
                return dict.getDictValue();
            }
        }
        return "99"; // 未找到返回99
    }

    /**
     * 获取尺码编码（1位数字）- 从字典查询
     * 1=XS(150cm), 2=S(155cm), 3=M(160cm), 4=L(165cm), 5=XL(170cm), 6=XXL(175cm), 7=XXXL(180cm), 8=F(均码)
     */
    private String getSizeCode(String size) {
        if (StringUtils.isEmpty(size)) {
            return "9";
        }
        // 从字典 gi_size 中查询尺码编码
        SysDictData query = new SysDictData();
        query.setDictType("gi_size");
        List<SysDictData> dictList = sysDictDataService.selectDictDataList(query);
        for (SysDictData dict : dictList) {
            if (size.equals(dict.getDictLabel())) {
                return dict.getDictValue();
            }
        }
        return "9"; // 未找到返回9
    }
}
