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
import com.ruoyi.inventory.domain.GiTransfer;
import com.ruoyi.inventory.service.IGiTransferService;

@RestController
@RequestMapping("/inventory/transfer")
public class GiTransferController extends BaseController {

    @Autowired
    private IGiTransferService giTransferService;

    @PreAuthorize("@ss.hasPermi('inventory:transfer:query')")
    @GetMapping("/list")
    public TableDataInfo list(GiTransfer giTransfer) {
        // 数据权限：查询与当前用户部门相关的调拨单（调出或调入）
        Long deptId = SecurityUtils.getLoginUser().getDeptId();
        if (giTransfer.getDeptIdOut() == null && giTransfer.getDeptIdIn() == null) {
            // 如果没有指定门店，则查询与当前用户部门相关的调拨单
            giTransfer.setDeptIdOut(deptId);
            giTransfer.setDeptIdIn(deptId);
        }
        startPage();
        List<GiTransfer> list = giTransferService.selectGiTransferList(giTransfer);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('inventory:transfer:query')")
    @GetMapping(value = "/{transferId}")
    public AjaxResult getInfo(@PathVariable("transferId") Long transferId) {
        return success(giTransferService.selectGiTransferById(transferId));
    }

    @PreAuthorize("@ss.hasPermi('inventory:transfer:add')")
    @Log(title = "创建调拨单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody GiTransfer giTransfer) {
        return success(giTransferService.insertGiTransfer(giTransfer));
    }

    @PreAuthorize("@ss.hasPermi('inventory:transfer:edit')")
    @Log(title = "审核调拨单", businessType = BusinessType.UPDATE)
    @PutMapping("/audit/{transferId}")
    public AjaxResult audit(@PathVariable Long transferId) {
        try {
            return toAjax(giTransferService.auditTransfer(transferId));
        } catch (RuntimeException e) {
            return error(e.getMessage());
        }
    }

    @PreAuthorize("@ss.hasPermi('inventory:transfer:edit')")
    @Log(title = "调拨出库", businessType = BusinessType.UPDATE)
    @PutMapping("/out/{transferId}")
    public AjaxResult out(@PathVariable Long transferId) {
        try {
            return toAjax(giTransferService.outTransfer(transferId));
        } catch (RuntimeException e) {
            return error(e.getMessage());
        }
    }

    @PreAuthorize("@ss.hasPermi('inventory:transfer:edit')")
    @Log(title = "调拨入库", businessType = BusinessType.UPDATE)
    @PutMapping("/in/{transferId}")
    public AjaxResult in(@PathVariable Long transferId) {
        try {
            return toAjax(giTransferService.inTransfer(transferId));
        } catch (RuntimeException e) {
            return error(e.getMessage());
        }
    }

    @PreAuthorize("@ss.hasPermi('inventory:transfer:edit')")
    @Log(title = "取消调拨单", businessType = BusinessType.UPDATE)
    @PutMapping("/cancel/{transferId}")
    public AjaxResult cancel(@PathVariable Long transferId) {
        try {
            return toAjax(giTransferService.cancelTransfer(transferId));
        } catch (RuntimeException e) {
            return error(e.getMessage());
        }
    }

    @PreAuthorize("@ss.hasPermi('inventory:transfer:remove')")
    @Log(title = "删除调拨单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{transferIds}")
    public AjaxResult remove(@PathVariable Long[] transferIds) {
        try {
            return toAjax(giTransferService.deleteGiTransferByIds(transferIds));
        } catch (RuntimeException e) {
            return error(e.getMessage());
        }
    }
}
