package com.ruoyi.inventory.service;

import java.util.List;
import com.ruoyi.inventory.domain.GiSpec;
import com.ruoyi.inventory.domain.GiSpecValue;

/**
 * 规格类型Service接口
 */
public interface IGiSpecService {
    
    public GiSpec selectGiSpecById(Long specId);

    public List<GiSpec> selectGiSpecList(GiSpec giSpec);

    public List<GiSpec> selectAllGiSpecList();

    public int insertGiSpec(GiSpec giSpec);

    public int updateGiSpec(GiSpec giSpec);

    public int deleteGiSpecByIds(Long[] specId);

    public boolean checkSpecCodeUnique(GiSpec giSpec);

    public List<GiSpecValue> selectSpecValuesBySpecId(Long specId);

    public List<GiSpecValue> selectSpecValuesBySpecCode(String specCode);
}
