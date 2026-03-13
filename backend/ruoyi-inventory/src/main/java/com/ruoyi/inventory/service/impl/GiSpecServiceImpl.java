package com.ruoyi.inventory.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.inventory.domain.GiSpec;
import com.ruoyi.inventory.domain.GiSpecValue;
import com.ruoyi.inventory.mapper.GiSpecMapper;
import com.ruoyi.inventory.service.IGiSpecService;

@Service
public class GiSpecServiceImpl implements IGiSpecService {

    @Autowired
    private GiSpecMapper giSpecMapper;

    @Override
    public GiSpec selectGiSpecById(Long specId) {
        return giSpecMapper.selectGiSpecById(specId);
    }

    @Override
    public List<GiSpec> selectGiSpecList(GiSpec giSpec) {
        return giSpecMapper.selectGiSpecList(giSpec);
    }

    @Override
    public List<GiSpec> selectAllGiSpecList() {
        return giSpecMapper.selectAllGiSpecList();
    }

    @Override
    public int insertGiSpec(GiSpec giSpec) {
        return giSpecMapper.insertGiSpec(giSpec);
    }

    @Override
    public int updateGiSpec(GiSpec giSpec) {
        return giSpecMapper.updateGiSpec(giSpec);
    }

    @Override
    public int deleteGiSpecByIds(Long[] specIds) {
        return giSpecMapper.deleteGiSpecByIds(specIds);
    }

    @Override
    public boolean checkSpecCodeUnique(GiSpec giSpec) {
        Long specId = giSpec.getSpecId() == null ? -1L : giSpec.getSpecId();
        GiSpec info = giSpecMapper.checkSpecCodeUnique(giSpec.getSpecCode());
        if (info != null && !info.getSpecId().equals(specId)) {
            return false;
        }
        return true;
    }

    @Override
    public List<GiSpecValue> selectSpecValuesBySpecId(Long specId) {
        return giSpecMapper.selectGiSpecValueBySpecId(specId);
    }

    @Override
    public List<GiSpecValue> selectSpecValuesBySpecCode(String specCode) {
        return giSpecMapper.selectGiSpecValueBySpecCode(specCode);
    }
}
