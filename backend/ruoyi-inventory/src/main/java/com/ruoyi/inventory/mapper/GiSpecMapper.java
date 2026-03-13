package com.ruoyi.inventory.mapper;

import java.util.List;
import com.ruoyi.inventory.domain.GiSpec;
import com.ruoyi.inventory.domain.GiSpecValue;

/**
 * 规格类型Mapper接口
 */
public interface GiSpecMapper {
    
    public GiSpec selectGiSpecById(Long specId);

    public List<GiSpec> selectGiSpecList(GiSpec giSpec);

    public List<GiSpec> selectAllGiSpecList();

    public int insertGiSpec(GiSpec giSpec);

    public int updateGiSpec(GiSpec giSpec);

    public int deleteGiSpecById(Long specId);

    public int deleteGiSpecByIds(Long[] specIds);

    public GiSpec checkSpecCodeUnique(String specCode);

    // 规格值相关
    public int insertGiSpecValue(GiSpecValue giSpecValue);

    public int updateGiSpecValue(GiSpecValue giSpecValue);

    public int deleteGiSpecValueById(Long valueId);

    public List<GiSpecValue> selectGiSpecValueList(GiSpecValue giSpecValue);

    public List<GiSpecValue> selectGiSpecValueBySpecId(Long specId);

    public List<GiSpecValue> selectGiSpecValueBySpecCode(String specCode);
}
