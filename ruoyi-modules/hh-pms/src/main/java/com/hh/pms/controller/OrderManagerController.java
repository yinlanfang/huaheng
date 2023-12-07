package com.hh.pms.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.hh.pms.domain.OrderMaterial;
import com.hh.pms.domain.SupplierDetails;
import com.hh.pms.mast.domain.Category;
import com.hh.pms.mast.domain.Currency;
import com.hh.pms.mast.domain.Material;
import com.hh.pms.mast.domain.TaxRate;
import com.hh.pms.model.MaterialClient;
import com.hh.pms.model.SupplierClient;
import com.hh.pms.service.IOrderMaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.security.annotation.RequiresPermissions;
import com.hh.pms.domain.OrderManager;
import com.hh.pms.service.IOrderManagerService;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.core.utils.poi.ExcelUtil;
import com.ruoyi.common.core.web.page.TableDataInfo;

/**
 * 采购订单管理Controller
 * 
 * @author ruoyi
 * @date 2023-11-21
 */
@RestController
@RequestMapping("/manager")
public class OrderManagerController extends BaseController
{
    @Autowired
    private IOrderManagerService orderManagerService;

    @Autowired
    private IOrderMaterialService orderMaterialService;


    /**
     * 根据执行状态Id查询采购订单列表
     * @param orId
     * @return
     */
    @GetMapping("/managerList")
    public List<OrderManager> managerList(@RequestParam("orId") Long orId){
        return orderManagerService.selectByOrderTypeRunning(orId);
    }
    /**
     * 查询执行状态个数
     * @return
     */
    @GetMapping("/runTypeNumber")
    public List<OrderManager> runTypeNumber(){
        return orderManagerService.findRunTypeNumber();
    }
    /**
     * 查询订单物料明细列表
     */
    @RequiresPermissions("pms:material:listOrderMaterial")
    @GetMapping("/listOrderMaterial")
    public TableDataInfo listOrderMaterial(OrderMaterial orderMaterial)
    {
        startPage();
        List<OrderMaterial> list = orderMaterialService.selectOrderMaterialList(orderMaterial);
        return getDataTable(list);
    }


    /**
     * 查询采购订单管理列表
     */
    @RequiresPermissions("pms:manager:list")
    @GetMapping("/list")
    public TableDataInfo list(OrderManager orderManager)
    {
        startPage();
        List<OrderManager> list = orderManagerService.selectOrderManagerList(orderManager);
        return getDataTable(list);
    }

    /**
     * 导出采购订单管理列表
     */
    @RequiresPermissions("pms:manager:export")
    @Log(title = "采购订单管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, OrderManager orderManager)
    {
        List<OrderManager> list = orderManagerService.selectOrderManagerList(orderManager);
        ExcelUtil<OrderManager> util = new ExcelUtil<OrderManager>(OrderManager.class);
        util.exportExcel(response, list, "采购订单管理数据");
    }

    /**
     * 获取采购订单管理详细信息
     */
    @RequiresPermissions("pms:manager:query")
    @GetMapping(value = "/{orderId}")
    public AjaxResult getInfo(@PathVariable("orderId") Long orderId)
    {
        return success(orderManagerService.selectOrderManagerByOrderId(orderId));
    }

    /**
     * 新增采购订单管理
     */
    @RequiresPermissions("pms:manager:add")
    @Log(title = "采购订单管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody OrderManager orderManager)
    {
        return toAjax(orderManagerService.insertOrderManager(orderManager));
    }

    /**
     * 修改采购订单管理
     */
    @RequiresPermissions("pms:manager:edit")
    @Log(title = "采购订单管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody OrderManager orderManager)
    {
        return toAjax(orderManagerService.updateOrderManager(orderManager));
    }

    /**
     * 删除采购订单管理
     */
    @RequiresPermissions("pms:manager:remove")
    @Log(title = "采购订单管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{orderIds}")
    public AjaxResult remove(@PathVariable Long[] orderIds)
    {
        return toAjax(orderManagerService.deleteOrderManagerByOrderIds(orderIds));
    }
}
