package com.ruoyi.inventory.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.inventory.domain.GiCategory;
import com.ruoyi.inventory.service.IGiCategoryService;

@RestController
@RequestMapping("/inventory/category")
public class GiCategoryController extends BaseController {

    @Autowired
    private IGiCategoryService giCategoryService;

    @PreAuthorize("@ss.hasPermi('inventory:category:list')")
    @GetMapping("/list")
    public TableDataInfo list(GiCategory giCategory) {
        startPage();
        List<GiCategory> list = giCategoryService.selectGiCategoryList(giCategory);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('inventory:category:query')")
    @GetMapping(value = "/{categoryId}")
    public AjaxResult getInfo(@PathVariable("categoryId") Long categoryId) {
        return success(giCategoryService.selectGiCategoryById(categoryId));
    }

    @PreAuthorize("@ss.hasPermi('inventory:category:add')")
    @Log(title = "商品分类", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody GiCategory giCategory) {
        if (!giCategoryService.checkCategoryNameUnique(giCategory)) {
            return error("新增分类'" + giCategory.getCategoryName() + "'失败，分类名称已存在");
        }
        giCategory.setCreateBy(getUsername());
        return toAjax(giCategoryService.insertGiCategory(giCategory));
    }

    @PreAuthorize("@ss.hasPermi('inventory:category:edit')")
    @Log(title = "商品分类", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody GiCategory giCategory) {
        if (!giCategoryService.checkCategoryNameUnique(giCategory)) {
            return error("修改分类'" + giCategory.getCategoryName() + "'失败，分类名称已存在");
        }
        giCategory.setUpdateBy(getUsername());
        return toAjax(giCategoryService.updateGiCategory(giCategory));
    }

    @PreAuthorize("@ss.hasPermi('inventory:category:remove')")
    @Log(title = "商品分类", businessType = BusinessType.DELETE)
    @DeleteMapping("/{categoryIds}")
    public AjaxResult remove(@PathVariable Long[] categoryIds) {
        return toAjax(giCategoryService.deleteGiCategoryByIds(categoryIds));
    }

    /**
     * 获取分类下拉树列表
     */
    @GetMapping("/treeselect")
    public AjaxResult treeselect(GiCategory giCategory) {
        return success(giCategoryService.buildCategoryTreeSelect());
    }

    /**
     * 获取分类列表（扁平，用于商品表单下拉选择）
     */
    @GetMapping("/listAll")
    public AjaxResult listAll() {
        return success(giCategoryService.selectGiCategoryList(new GiCategory()));
    }
}
