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
import com.ruoyi.inventory.domain.GiGoods;
import com.ruoyi.inventory.service.IGiGoodsService;
import com.ruoyi.inventory.service.IGiSkuService;

@RestController
@RequestMapping("/inventory/goods")
public class GiGoodsController extends BaseController {

    @Autowired
    private IGiGoodsService giGoodsService;

    @Autowired
    private IGiSkuService giSkuService;

    @PreAuthorize("@ss.hasPermi('inventory:goods:list')")
    @GetMapping("/list")
    public TableDataInfo list(GiGoods giGoods) {
        startPage();
        List<GiGoods> list = giGoodsService.selectGiGoodsList(giGoods);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('inventory:goods:query')")
    @GetMapping(value = "/{goodsId}")
    public AjaxResult getInfo(@PathVariable("goodsId") Long goodsId) {
        GiGoods goods = giGoodsService.selectGiGoodsById(goodsId);
        goods.setSkuList(giSkuService.selectGiSkuByGoodsId(goodsId));
        return success(goods);
    }

    @PreAuthorize("@ss.hasPermi('inventory:goods:add')")
    @Log(title = "商品管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody GiGoods giGoods) {
        if (!giGoodsService.checkGoodsCodeUnique(giGoods)) {
            return error("新增商品'" + giGoods.getGoodsCode() + "'失败，商品编码已存在");
        }
        giGoods.setCreateBy(getUsername());
        return toAjax(giGoodsService.insertGiGoods(giGoods));
    }

    @PreAuthorize("@ss.hasPermi('inventory:goods:edit')")
    @Log(title = "商品管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody GiGoods giGoods) {
        if (!giGoodsService.checkGoodsCodeUnique(giGoods)) {
            return error("修改商品'" + giGoods.getGoodsCode() + "'失败，商品编码已存在");
        }
        giGoods.setUpdateBy(getUsername());
        return toAjax(giGoodsService.updateGiGoods(giGoods));
    }

    @PreAuthorize("@ss.hasPermi('inventory:goods:remove')")
    @Log(title = "商品管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{goodsIds}")
    public AjaxResult remove(@PathVariable Long[] goodsIds) {
        return toAjax(giGoodsService.deleteGiGoodsByIds(goodsIds));
    }
}
