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
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.inventory.domain.GiStocktake;
import com.ruoyi.inventory.service.IGiStocktakeService;

@RestController
@RequestMapping("/inventory/stocktake")
public class GiStocktakeController extends BaseController {

    @Autowired
    private IGiStocktakeService giStocktakeService;

    @PreAuthorize("@ss.hasPermi('inventory:stocktake:query')")
    @GetMapping("/list")
    public TableDataInfo list(GiStocktake giStocktake) {
        // 数据权限：根据当前用户部门过滤
        Long deptId = SecurityUtils.getLoginUser().getDeptId();
        if (giStocktake.getDeptId() == null) {
            giStocktake.setDeptId(deptId);
        }
        startPage();
        List<GiStocktake> list = giStocktakeService.selectGiStocktakeList(giStocktake);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('inventory:stocktake:query')")
    @GetMapping(value = "/{stocktakeId}")
    public AjaxResult getInfo(@PathVariable("stocktakeId") Long stocktakeId) {
        return success(giStocktakeService.selectGiStocktakeById(stocktakeId));
    }

    @PreAuthorize("@ss.hasPermi('inventory:stocktake:add')")
    @Log(title = "创建盘点单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody GiStocktake giStocktake) {
        try {
            return success(giStocktakeService.insertGiStocktake(giStocktake));
        } catch (RuntimeException e) {
            return error(e.getMessage());
        }
    }

    @PreAuthorize("@ss.hasPermi('inventory:stocktake:edit')")
    @Log(title = "审核盘点单", businessType = BusinessType.UPDATE)
    @PutMapping("/audit/{stocktakeId}")
    public AjaxResult audit(@PathVariable Long stocktakeId) {
        try {
            return toAjax(giStocktakeService.auditStocktake(stocktakeId));
        } catch (RuntimeException e) {
            return error(e.getMessage());
        }
    }

    @PreAuthorize("@ss.hasPermi('inventory:stocktake:edit')")
    @Log(title = "取消盘点单", businessType = BusinessType.UPDATE)
    @PutMapping("/cancel/{stocktakeId}")
    public AjaxResult cancel(@PathVariable Long stocktakeId) {
        try {
            return toAjax(giStocktakeService.cancelStocktake(stocktakeId));
        } catch (RuntimeException e) {
            return error(e.getMessage());
        }
    }

    @PreAuthorize("@ss.hasPermi('inventory:stocktake:remove')")
    @Log(title = "删除盘点单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{stocktakeIds}")
    public AjaxResult remove(@PathVariable Long[] stocktakeIds) {
        try {
            return toAjax(giStocktakeService.deleteGiStocktakeByIds(stocktakeIds));
        } catch (RuntimeException e) {
            return error(e.getMessage());
        }
    }
}
