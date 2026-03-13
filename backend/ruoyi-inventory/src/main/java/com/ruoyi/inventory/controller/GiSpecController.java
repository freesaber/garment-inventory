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
import com.ruoyi.inventory.domain.GiSpec;
import com.ruoyi.inventory.domain.GiSpecValue;
import com.ruoyi.inventory.service.IGiSpecService;

@RestController
@RequestMapping("/inventory/spec")
public class GiSpecController extends BaseController {

    @Autowired
    private IGiSpecService giSpecService;

    @PreAuthorize("@ss.hasPermi('inventory:spec:list')")
    @GetMapping("/list")
    public TableDataInfo list(GiSpec giSpec) {
        startPage();
        List<GiSpec> list = giSpecService.selectGiSpecList(giSpec);
        return getDataTable(list);
    }

    @GetMapping("/listAll")
    public AjaxResult listAll() {
        List<GiSpec> list = giSpecService.selectAllGiSpecList();
        return success(list);
    }

    @PreAuthorize("@ss.hasPermi('inventory:spec:query')")
    @GetMapping("/{specId}")
    public AjaxResult getInfo(@PathVariable Long specId) {
        return success(giSpecService.selectGiSpecById(specId));
    }

    @PreAuthorize("@ss.hasPermi('inventory:spec:add')")
    @Log(title = "规格类型", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody GiSpec giSpec) {
        if (!giSpecService.checkSpecCodeUnique(giSpec)) {
            return error("新增规格'" + giSpec.getSpecName() + "'失败，规格编码已存在");
        }
        return toAjax(giSpecService.insertGiSpec(giSpec));
    }

    @PreAuthorize("@ss.hasPermi('inventory:spec:edit')")
    @Log(title = "规格类型", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody GiSpec giSpec) {
        if (!giSpecService.checkSpecCodeUnique(giSpec)) {
            return error("修改规格'" + giSpec.getSpecName() + "'失败，规格编码已存在");
        }
        return toAjax(giSpecService.updateGiSpec(giSpec));
    }

    @PreAuthorize("@ss.hasPermi('inventory:spec:remove')")
    @Log(title = "规格类型", businessType = BusinessType.DELETE)
    @DeleteMapping("/{specIds}")
    public AjaxResult remove(@PathVariable Long[] specIds) {
        return toAjax(giSpecService.deleteGiSpecByIds(specIds));
    }

    @GetMapping("/value/list")
    public AjaxResult listValues(@RequestParam Long specId) {
        List<GiSpecValue> list = giSpecService.selectSpecValuesBySpecId(specId);
        return success(list);
    }

    @GetMapping("/value/listByCode")
    public AjaxResult listValuesByCode(@RequestParam String specCode) {
        List<GiSpecValue> list = giSpecService.selectSpecValuesBySpecCode(specCode);
        return success(list);
    }
}
