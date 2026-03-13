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
import com.ruoyi.inventory.domain.GiBrand;
import com.ruoyi.inventory.mapper.GiBrandMapper;

@RestController
@RequestMapping("/inventory/brand")
public class GiBrandController extends BaseController {

    @Autowired
    private GiBrandMapper giBrandMapper;

    @PreAuthorize("@ss.hasPermi('inventory:brand:list')")
    @GetMapping("/list")
    public TableDataInfo list(GiBrand giBrand) {
        startPage();
        List<GiBrand> list = giBrandMapper.selectGiBrandList(giBrand);
        return getDataTable(list);
    }

    @GetMapping("/listAll")
    public AjaxResult listAll() {
        List<GiBrand> list = giBrandMapper.selectGiBrandList(new GiBrand());
        return success(list);
    }

    @PreAuthorize("@ss.hasPermi('inventory:brand:query')")
    @GetMapping("/{brandId}")
    public AjaxResult getInfo(@PathVariable Long brandId) {
        return success(giBrandMapper.selectGiBrandById(brandId));
    }

    @PreAuthorize("@ss.hasPermi('inventory:brand:add')")
    @Log(title = "品牌管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody GiBrand giBrand) {
        return toAjax(giBrandMapper.insertGiBrand(giBrand));
    }

    @PreAuthorize("@ss.hasPermi('inventory:brand:edit')")
    @Log(title = "品牌管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody GiBrand giBrand) {
        return toAjax(giBrandMapper.updateGiBrand(giBrand));
    }

    @PreAuthorize("@ss.hasPermi('inventory:brand:remove')")
    @Log(title = "品牌管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{brandIds}")
    public AjaxResult remove(@PathVariable Long[] brandIds) {
        return toAjax(giBrandMapper.deleteGiBrandByIds(brandIds));
    }
}
