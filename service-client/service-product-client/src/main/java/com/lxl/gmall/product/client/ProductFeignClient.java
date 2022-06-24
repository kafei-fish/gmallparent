package com.lxl.gmall.product.client;

import com.lxl.gmall.comon.util.result.Result;
import com.lxl.gmall.model.product.*;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Author LiXiaoLong
 * @Date 2022/6/16 10:41
 * @PackageName:com.lxl.gmall.product.client
 * @ClassName: ProductFeignClient
 * @Description:  Product远程调用接口
 * @Version 1.0
 */
@FeignClient(value = "service-product",fallback =ProductDegradeFeignClient.class )
public interface ProductFeignClient {
    /**
     * 获取Sku基本信息与图片信息
     * @param skuId
     * @return
     */
    @ApiOperation(value = "获取Sku基本信息与图片信息")
    @GetMapping("/api/product/inner/getSpuInfo/{skuId}")
    public SkuInfo getSpuInfo(@PathVariable Long skuId);
    /**
     * 获取SKu时时更新价格
     * @param skuId
     * @return
     */
    @ApiOperation(value ="获取SKu时时更新价格" )
    @GetMapping("/api/product/inner/getSpuInfoPrice/{skuId}")
    public BigDecimal getPrice(@PathVariable Long skuId);

    /**
     * 获取分类信息
     * @param skuId
     * @return
     */
    @ApiOperation(value = "获取分类信息")
    @GetMapping("/api/product/inner/getBaseCategory/{skuId}")
    public BaseCategoryView getBaseCategory(@PathVariable Long skuId);

    /**
     * 获取销售属性-销售属性值-锁定
     * @param skuId
     * @return
     */
    @ApiOperation("获取销售属性-销售属性值-锁定")
    @GetMapping("/api/product/inner/getSpuSaleAttrValue/{skuId}")
    public List<SpuSaleAttr> getSpuSaleAttrValue(@PathVariable Long skuId);

    /**
     * 获取海报
     * @param spuId
     * @return
     */
    @ApiOperation(value = "获取海报")
    @GetMapping("/api/product/inner/getSpuPosterList/{spuId}")
    public List<SpuPoster> getSpuPosterList(@PathVariable Long spuId);

    /**
     * 获取Sku对应的平台属性
     * @return
     */
    @ApiOperation(value = "获取Sku对应的平台属性")
    @GetMapping("/api/product/inner/getBaseAttrInfo/{skuId}")
    public List<BaseAttrInfo> getBaseAttrInfo(@PathVariable Long skuId);

    @ApiOperation(value = "通过SpuID获取全部的sku对应销售属性与属性值")
    @GetMapping("/api/product/inner/getSkuValueIdsMap/{spuId}")
    public Map getSkuValueIdsMap(@PathVariable Long spuId);

    @GetMapping("/api/product/inner/getBaseCategoryList")
    public Result getBaseCategoryList();

    /**
     * 通过品牌Id 集合来查询数据
     * @param tmId
     * @return
     */
    @GetMapping("/api/product/inner/getTrademark/{tmId}")
    public BaseTrademark getTrademark(@PathVariable Long tmId);
}
