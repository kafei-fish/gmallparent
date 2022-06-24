package com.lxl.gmall.product.api;

import com.alibaba.fastjson.JSONObject;
import com.lxl.gmall.comon.util.result.Result;
import com.lxl.gmall.model.product.*;
import com.lxl.gmall.product.service.ManageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Author LiXiaoLong
 * @Date 2022/6/15 13:25
 * @PackageName:com.lxl.gmall.product.controller.api
 * @ClassName: ProductApiController
 * @Description: TODO
 * @Version 1.0
 */
@Api("远程调用Api接口")
@RestController
@RequestMapping("/api/product/inner")
public class ProductApiController {
    @Autowired
    private ManageService manageService;

    /**
     * 获取Sku基本信息与图片信息
     * @param skuId
     * @return
     */
    @ApiOperation(value = "获取Sku基本信息与图片信息")
    @GetMapping("getSpuInfo/{skuId}")
    public SkuInfo getSpuInfo(@PathVariable Long skuId){
//        return manageService.getApiSpuInfo(skuId);
        return manageService.getSkuInfoDB(skuId);
    }
    /**
     * 获取SKu时时更新价格
     * @param skuId
     * @return
     */
    @ApiOperation(value ="获取SKu时时更新价格" )
    @GetMapping("getSpuInfoPrice/{skuId}")
    public BigDecimal getPrice(@PathVariable Long skuId){
        return manageService.getPrice(skuId);
    }
    /**
     * 获取分类信息
     * @param skuId
     * @return
     */
    @ApiOperation(value = "获取分类信息")
    @GetMapping("getBaseCategory/{skuId}")
    public BaseCategoryView getBaseCategory(@PathVariable Long skuId){
        return manageService.getBaseCategory(skuId);
    }

    /**
     * 获取销售属性-销售属性值-锁定
     * @param skuId
     * @return
     */
    @ApiOperation("获取销售属性-销售属性值-锁定")
    @GetMapping("getSpuSaleAttrValue/{skuId}")
    public   List<SpuSaleAttr> getSpuSaleAttrValue(@PathVariable Long skuId){
        return manageService.getSpuSaleAttrValue(skuId);
    }

    /**
     * 获取海报
     * @param spuId
     * @return
     */
    @ApiOperation(value = "获取海报")
    @GetMapping("getSpuPosterList/{spuId}")
    public List<SpuPoster> getSpuPosterList(@PathVariable Long spuId){
        return manageService.getSpuPosterList(spuId);
    }

    /**
     * 获取Sku对应的平台属性
     * @return
     */
    @ApiOperation(value = "获取Sku对应的平台属性")
    @GetMapping("getBaseAttrInfo/{skuId}")
    public List<BaseAttrInfo> getBaseAttrInfo(@PathVariable Long skuId){
        return manageService.getBaseAttrInfoBySkuId(skuId);
    }
    @ApiOperation(value = "通过SpuID获取全部的sku对应销售属性与属性值")
    @GetMapping("getSkuValueIdsMap/{spuId}")
    public Map getSkuValueIdsMap(@PathVariable Long spuId){
        return manageService.getSkuValueIdsMap(spuId);
    }

    @GetMapping("getBaseCategoryList")
    public Result getBaseCategoryList(){
        List<JSONObject> list=manageService.getBaseCategoryList();
        return Result.ok(list);
    }
    /**
     * 通过品牌Id 集合来查询数据
     * @param tmId
     * @return
     */
    @GetMapping("getTrademark/{tmId}")
    public BaseTrademark getTrademark(@PathVariable Long tmId){
        BaseTrademark baseTrademark=manageService.getTrademark(tmId);
        return baseTrademark;
    }
}
