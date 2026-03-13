package com.ruoyi.inventory.mapper;

import java.util.List;
import com.ruoyi.inventory.domain.GiBrand;

/**
 * 品牌Mapper接口
 */
public interface GiBrandMapper {
    
    public GiBrand selectGiBrandById(Long brandId);

    public List<GiBrand> selectGiBrandList(GiBrand giBrand);

    public int insertGiBrand(GiBrand giBrand);

    public int updateGiBrand(GiBrand giBrand);

    public int deleteGiBrandById(Long brandId);

    public int deleteGiBrandByIds(Long[] brandIds);

    public GiBrand checkBrandNameUnique(String brandName);
}
