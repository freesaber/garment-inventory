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
import com.ruoyi.inventory.domain.GiPurchase;
import com.ruoyi.inventory.service.IGiPurchaseService;

@RestController
@RequestMapping("/inventory/purchase")
public class GiPurchaseController extends BaseController {

    @Autowired
    private IGiPurchaseService giPurchaseService;

    @PreAuthorize("@ss.hasPermi('inventory:purchase:query')")
    @GetMapping("/list")
    public TableDataInfo list(GiPurchase giPurchase) {
        startPage();
        List<GiPurchase> list = giPurchaseService.selectGiPurchaseList(giPurchase);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('inventory:purchase:query')")
    @GetMapping(value = "/{purchaseId}")
    public AjaxResult getInfo(@PathVariable("purchaseId") Long purchaseId) {
        return success(giPurchaseService.selectGiPurchaseById(purchaseId));
    }

    @PreAuthorize("@ss.hasPermi('inventory:purchase:add')")
    @Log(title = "采购开单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody GiPurchase giPurchase) {
        return success(giPurchaseService.insertGiPurchase(giPurchase));
    }

    @PreAuthorize("@ss.hasPermi('inventory:purchase:edit')")
    @Log(title = "确认入库", businessType = BusinessType.UPDATE)
    @PutMapping("/confirm/{purchaseId}")
    public AjaxResult confirm(@PathVariable Long purchaseId) {
        try {
            return toAjax(giPurchaseService.confirmPurchase(purchaseId));
        } catch (RuntimeException e) {
            return error(e.getMessage());
        }
    }

    @PreAuthorize("@ss.hasPermi('inventory:purchase:edit')")
    @Log(title = "取消采购单", businessType = BusinessType.UPDATE)
    @PutMapping("/cancel/{purchaseId}")
    public AjaxResult cancel(@PathVariable Long purchaseId) {
        try {
            return toAjax(giPurchaseService.cancelPurchase(purchaseId));
        } catch (RuntimeException e) {
            return error(e.getMessage());
        }
    }

    @PreAuthorize("@ss.hasPermi('inventory:purchase:remove')")
    @Log(title = "删除采购单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{purchaseIds}")
    public AjaxResult remove(@PathVariable Long[] purchaseIds) {
        return toAjax(giPurchaseService.deleteGiPurchaseByIds(purchaseIds));
    }
}
