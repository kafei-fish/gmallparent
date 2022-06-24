package com.lxl.gmall.product.controller;

import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lxl.gmall.comon.util.result.Result;
import com.lxl.gmall.model.product.*;
import com.lxl.gmall.product.service.ManageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;

/**
 * @Author LiXiaoLong
 * @Date 2022/6/12 21:00
 * @PackageName:com.lxl.gmall.product.controller
 * @ClassName: SpuManageController
 * @Description: TODO
 * @Version 1.0
 */
@Api("Spu接口")
@RestController
@RequestMapping("/admin/product")
public class SpuManageController {
    @Autowired
    private ManageService manageService;

    @ApiOperation(value = "SPU分页 ")
    @GetMapping("{page}/{limit}")
    public Result getProduct(@PathVariable Integer page, @PathVariable Integer limit, SpuInfo spuInfo){
        Page pagePram=new Page(page,limit);
        Page<SpuInfo> pageList=manageService.getProductPageList(pagePram,spuInfo);
        return Result.ok(pageList);
    }
    //获取销售属性
    ///admin/product/baseSaleAttrList
    @GetMapping("baseSaleAttrList")
    public  Result baseSaleAttrList(){
        //获取销售属性
        List<BaseSaleAttr> baseSaleAttrList=manageService.baseSaleAttrList();
        return Result.ok(baseSaleAttrList);
    }
    //保存Spu
    ///admin/product/saveSpuInfo
    @PostMapping("saveSpuInfo")
    public Result saveSpuInfo(@RequestBody SpuInfo spuInfo){
        manageService.saveSpuInfo(spuInfo);
        return Result.ok();
    }

    //查询Spu图片
    ///admin/product/spuImageList/{spuId}
    @GetMapping("spuImageList/{spuId}")
    public Result spuImageList(@PathVariable Long spuId){
        List<SpuImage> spuImageList=manageService.spuImageList(spuId);
        return Result.ok(spuImageList);
    }
    //根据SPuid查询销售属性
    ///admin/product/spuSaleAttrList/{spuId}
    @GetMapping("spuSaleAttrList/{spuId}")
    public Result spuSaleAttrList(@PathVariable Long spuId){
        List<SpuSaleAttr> spuSaleAttrList=manageService.spuSaleAttrListBySpuId(spuId);
        return Result.ok(spuSaleAttrList);
    }
    //保存SkuInfo
    // /admin/product/saveSkuInfo
    @PostMapping("saveSkuInfo")
    public Result saveSkuInfo(@RequestBody SkuInfo skuInfo){
        manageService.saveSkuInfo(skuInfo);
        return Result.ok();
    }

    // 根据Spu获取全部信息
    ///getSpuInfo/${spuId}
    @GetMapping("getSpuInfo/{spuId}")
    public Result getSpuInfo(@PathVariable Long spuId){
        SpuInfo spuInfo=manageService.getSpuInfo(spuId);
        return Result.ok(spuInfo);
    }
    //根据ID获取Spu海报
    @GetMapping("getSpuPosterList/{spuId}")
    public Result getSpuPosterList(@PathVariable Long spuId){
        List<SpuPoster> posterList=manageService.getSpuPosterList(spuId);
        return Result.ok(posterList);
    }
    //更新Spu
    // /admin/product/updateSpuInfo
    @PostMapping("updateSpuInfo")
    public Result updateSpuInfo(@RequestBody SpuInfo spuInfo){
        manageService.updateSpuInfo(spuInfo);
        return Result.ok();
    }
}
