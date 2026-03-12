package com.ruoyi.inventory.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.core.domain.TreeSelect;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.inventory.domain.GiCategory;
import com.ruoyi.inventory.mapper.GiCategoryMapper;
import com.ruoyi.inventory.service.IGiCategoryService;

@Service
public class GiCategoryServiceImpl implements IGiCategoryService {

    @Autowired
    private GiCategoryMapper giCategoryMapper;

    @Override
    public GiCategory selectGiCategoryById(Long categoryId) {
        return giCategoryMapper.selectGiCategoryById(categoryId);
    }

    @Override
    public List<GiCategory> selectGiCategoryList(GiCategory giCategory) {
        return giCategoryMapper.selectGiCategoryList(giCategory);
    }

    @Override
    public int insertGiCategory(GiCategory giCategory) {
        return giCategoryMapper.insertGiCategory(giCategory);
    }

    @Override
    public int updateGiCategory(GiCategory giCategory) {
        return giCategoryMapper.updateGiCategory(giCategory);
    }

    @Override
    public int deleteGiCategoryById(Long categoryId) {
        return giCategoryMapper.deleteGiCategoryById(categoryId);
    }

    @Override
    public int deleteGiCategoryByIds(Long[] categoryIds) {
        return giCategoryMapper.deleteGiCategoryByIds(categoryIds);
    }

    @Override
    public boolean checkCategoryNameUnique(GiCategory giCategory) {
        Long categoryId = giCategory.getCategoryId() == null ? -1L : giCategory.getCategoryId();
        GiCategory info = giCategoryMapper.checkCategoryNameUnique(giCategory.getCategoryName(), giCategory.getParentId());
        if (info != null && !info.getCategoryId().equals(categoryId)) {
            return false;
        }
        return true;
    }

    @Override
    public List<TreeSelect> buildCategoryTreeSelect() {
        List<GiCategory> categories = giCategoryMapper.selectGiCategoryList(new GiCategory());
        List<GiCategory> categoryTree = buildCategoryTree(categories, 0L);
        return categoryTree.stream().map(this::convertToTreeSelect).collect(Collectors.toList());
    }

    /**
     * 构建分类树
     */
    private List<GiCategory> buildCategoryTree(List<GiCategory> categories, Long parentId) {
        List<GiCategory> result = new ArrayList<>();
        for (GiCategory category : categories) {
            if (parentId.equals(category.getParentId())) {
                category.setChildren(buildCategoryTree(categories, category.getCategoryId()));
                result.add(category);
            }
        }
        return result;
    }

    /**
     * 转换为 TreeSelect
     */
    private TreeSelect convertToTreeSelect(GiCategory category) {
        TreeSelect treeSelect = new TreeSelect();
        treeSelect.setId(category.getCategoryId());
        treeSelect.setLabel(category.getCategoryName());
        if (category.getChildren() != null && !category.getChildren().isEmpty()) {
            treeSelect.setChildren(category.getChildren().stream().map(this::convertToTreeSelect).collect(Collectors.toList()));
        }
        return treeSelect;
    }
}
