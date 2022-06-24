package com.lxl.gmall.product.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lxl.gmall.comon.util.result.Result;
import com.lxl.gmall.model.product.SkuInfo;
import com.lxl.gmall.product.service.ManageService;
import io.swagger.annotations.Api;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author LiXiaoLong
 * @Date 2022/6/14 16:22
 * @PackageName:com.lxl.gmall.product.controller
 * @ClassName: SkuManageController
 * @Description: TODO
 * @Version 1.0
 */
@Api("Sku接口")
@RestController
@RequestMapping("/admin/product")
public class SkuManageController {

    @Autowired
    private ManageService manageService;
    //Sku分页列表
    ///admin/product/list/{page}/{limit}
    @GetMapping("list/{page}/{limit}")
    public Result skuList(@PathVariable Long page, @PathVariable Long limit, SkuInfo skuInfo){
        Page<SkuInfo> pageParm=new Page<>(page,limit);
        IPage<SkuInfo> skuPageList=manageService.skuList(pageParm,skuInfo);
        return Result.ok(skuPageList);
    }
    //getSkuInfo
    @GetMapping("getSkuInfo/{skuId}")
    public Result getSkuInfo(@PathVariable Long skuId){
            SkuInfo skuInfo=manageService.getSkuInfo(skuId);
            return Result.ok(skuInfo);
    }
    //修改Sku
    //updateSkuInfo
    @PostMapping("updateSkuInfo")
    public Result updateSkuInfo(@RequestBody SkuInfo skuInfo){
        manageService.updateSkuInfo(skuInfo);
        return Result.ok();
    }
    //下架
    ///admin/product/cancelSale/{skuId}
    @GetMapping("cancelSale/{skuId}")
    public Result cancelSale(@PathVariable Long skuId){
        //下架
        manageService.cancelSaleSku(skuId);
        return Result.ok();
    }
    //上架
    ///admin/product/onSale/{skuId}
    @GetMapping("onSale/{skuId}")
    public Result onSale(@PathVariable Long skuId){
        //上架
        manageService.onSaleSku(skuId);
        return Result.ok();
    }
}
