package com.ruoyi.inventory.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.inventory.domain.GiBrand;
import com.ruoyi.inventory.mapper.GiBrandMapper;
import com.ruoyi.inventory.service.IGiBrandService;

@Service
public class GiBrandServiceImpl implements IGiBrandService {

    @Autowired
    private GiBrandMapper giBrandMapper;

    @Override
    public GiBrand selectGiBrandById(Long brandId) {
        return giBrandMapper.selectGiBrandById(brandId);
    }

    @Override
    public List<GiBrand> selectGiBrandList(GiBrand giBrand) {
        return giBrandMapper.selectGiBrandList(giBrand);
    }

    @Override
    public int insertGiBrand(GiBrand giBrand) {
        return giBrandMapper.insertGiBrand(giBrand);
    }

    @Override
    public int updateGiBrand(GiBrand giBrand) {
        return giBrandMapper.updateGiBrand(giBrand);
    }

    @Override
    public int deleteGiBrandById(Long brandId) {
        return giBrandMapper.deleteGiBrandById(brandId);
    }

    @Override
    public int deleteGiBrandByIds(Long[] brandIds) {
        return giBrandMapper.deleteGiBrandByIds(brandIds);
    }

    @Override
    public boolean checkBrandNameUnique(GiBrand giBrand) {
        Long brandId = giBrand.getBrandId() == null ? -1L : giBrand.getBrandId();
        GiBrand info = giBrandMapper.checkBrandNameUnique(giBrand.getBrandName());
        if (info != null && !info.getBrandId().equals(brandId)) {
            return false;
        }
        return true;
    }
}
