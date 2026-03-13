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
import com.ruoyi.inventory.domain.GiSale;
import com.ruoyi.inventory.service.IGiSaleService;

@RestController
@RequestMapping("/inventory/sale")
public class GiSaleController extends BaseController {

    @Autowired
    private IGiSaleService giSaleService;

    @PreAuthorize("@ss.hasPermi('inventory:sale:query')")
    @GetMapping("/list")
    public TableDataInfo list(GiSale giSale) {
        startPage();
        List<GiSale> list = giSaleService.selectGiSaleList(giSale);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('inventory:sale:query')")
    @GetMapping(value = "/{saleId}")
    public AjaxResult getInfo(@PathVariable("saleId") Long saleId) {
        return success(giSaleService.selectGiSaleById(saleId));
    }

    @PreAuthorize("@ss.hasPermi('inventory:sale:add')")
    @Log(title = "销售开单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody GiSale giSale) {
        try {
            return success(giSaleService.insertGiSale(giSale));
        } catch (RuntimeException e) {
            return error(e.getMessage());
        }
    }

    @PreAuthorize("@ss.hasPermi('inventory:sale:remove')")
    @Log(title = "删除销售单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{saleIds}")
    public AjaxResult remove(@PathVariable Long[] saleIds) {
        return toAjax(giSaleService.deleteGiSaleByIds(saleIds));
    }
}
