package com.ruoyi.inventory.service;

import java.util.List;
import com.ruoyi.inventory.domain.GiBrand;

public interface IGiBrandService {
    
    public GiBrand selectGiBrandById(Long brandId);

    public List<GiBrand> selectGiBrandList(GiBrand giBrand);

    public int insertGiBrand(GiBrand giBrand);

    public int updateGiBrand(GiBrand giBrand);

    public int deleteGiBrandById(Long brandId);

    public int deleteGiBrandByIds(Long[] brandIds);

    public boolean checkBrandNameUnique(GiBrand giBrand);
}
