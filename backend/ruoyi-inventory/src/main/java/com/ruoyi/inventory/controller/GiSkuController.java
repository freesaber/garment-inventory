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
import com.ruoyi.inventory.domain.GiSku;
import com.ruoyi.inventory.service.IGiSkuService;

@RestController
@RequestMapping("/inventory/sku")
public class GiSkuController extends BaseController {

    @Autowired
    private IGiSkuService giSkuService;

    @PreAuthorize("@ss.hasPermi('inventory:goods:list')")
    @GetMapping("/list")
    public TableDataInfo list(GiSku giSku) {
        startPage();
        List<GiSku> list = giSkuService.selectGiSkuList(giSku);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('inventory:goods:query')")
    @GetMapping(value = "/{skuId}")
    public AjaxResult getInfo(@PathVariable("skuId") Long skuId) {
        return success(giSkuService.selectGiSkuById(skuId));
    }

    @GetMapping("/barcode/{barcode}")
    public AjaxResult getByBarcode(@PathVariable("barcode") String barcode) {
        return success(giSkuService.selectGiSkuByBarcode(barcode));
    }

    @GetMapping("/goods/{goodsId}")
    public AjaxResult getByGoodsId(@PathVariable("goodsId") Long goodsId) {
        return success(giSkuService.selectGiSkuByGoodsId(goodsId));
    }

    @PreAuthorize("@ss.hasPermi('inventory:goods:add')")
    @Log(title = "商品SKU", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody GiSku giSku) {
        if (!giSkuService.checkSkuCodeUnique(giSku)) {
            return error("新增SKU'" + giSku.getSkuCode() + "'失败，SKU编码已存在");
        }
        giSku.setCreateBy(getUsername());
        return toAjax(giSkuService.insertGiSku(giSku));
    }

    @PreAuthorize("@ss.hasPermi('inventory:goods:edit')")
    @Log(title = "商品SKU", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody GiSku giSku) {
        if (!giSkuService.checkSkuCodeUnique(giSku)) {
            return error("修改SKU'" + giSku.getSkuCode() + "'失败，SKU编码已存在");
        }
        giSku.setUpdateBy(getUsername());
        return toAjax(giSkuService.updateGiSku(giSku));
    }

    @PreAuthorize("@ss.hasPermi('inventory:goods:remove')")
    @Log(title = "商品SKU", businessType = BusinessType.DELETE)
    @DeleteMapping("/{skuIds}")
    public AjaxResult remove(@PathVariable Long[] skuIds) {
        return toAjax(giSkuService.deleteGiSkuByIds(skuIds));
    }
}
