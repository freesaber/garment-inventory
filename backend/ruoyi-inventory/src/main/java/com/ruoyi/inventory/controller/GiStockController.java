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
import com.ruoyi.inventory.domain.GiStock;
import com.ruoyi.inventory.service.IGiStockService;

@RestController
@RequestMapping("/inventory/stock")
public class GiStockController extends BaseController {

    @Autowired
    private IGiStockService giStockService;

    @PreAuthorize("@ss.hasPermi('inventory:stock:query')")
    @GetMapping("/list")
    public TableDataInfo list(GiStock giStock) {
        // 数据权限：根据当前用户部门过滤
        Long deptId = SecurityUtils.getLoginUser().getDeptId();
        if (giStock.getDeptId() == null) {
            giStock.setDeptId(deptId);
        }
        startPage();
        List<GiStock> list = giStockService.selectGiStockList(giStock);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('inventory:stock:warning')")
    @GetMapping("/warning")
    public TableDataInfo warningList(GiStock giStock) {
        Long deptId = SecurityUtils.getLoginUser().getDeptId();
        if (giStock.getDeptId() == null) {
            giStock.setDeptId(deptId);
        }
        startPage();
        List<GiStock> list = giStockService.selectWarningStockList(giStock);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('inventory:stock:query')")
    @GetMapping(value = "/{stockId}")
    public AjaxResult getInfo(@PathVariable("stockId") Long stockId) {
        return success(giStockService.selectGiStockById(stockId));
    }

    @PreAuthorize("@ss.hasPermi('inventory:stock:edit')")
    @Log(title = "库存调整", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody GiStock giStock) {
        return toAjax(giStockService.updateGiStock(giStock));
    }
}
