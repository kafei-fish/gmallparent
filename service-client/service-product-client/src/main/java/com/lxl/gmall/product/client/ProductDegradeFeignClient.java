package com.lxl.gmall.product.client;

import com.lxl.gmall.comon.util.result.Result;
import com.lxl.gmall.model.product.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Author LiXiaoLong
 * @Date 2022/6/16 10:56
 * @PackageName:com.lxl.gmall.product.client
 * @ClassName: ProductDegradeFeignClient
 * @Description: 远程调用超时做的降级处理
 * @Version 1.0
 */
@Component
public class ProductDegradeFeignClient implements ProductFeignClient{
    @Override
    public SkuInfo getSpuInfo(Long skuId) {
        System.out.println("走熔断了");
        return new SkuInfo();
    }

    @Override
    public BigDecimal getPrice(Long skuId) {
        System.out.println("走熔断了");
        return null;
    }

    @Override
    public BaseCategoryView getBaseCategory(Long skuId) {
        System.out.println("走熔断了");
        return new BaseCategoryView();
    }

    @Override
    public List<SpuSaleAttr> getSpuSaleAttrValue(Long skuId) {
        System.out.println("走熔断了");
        return null;
    }

    @Override
    public List<SpuPoster> getSpuPosterList(Long spuId) {
        System.out.println("走熔断了");
        return null;
    }

    @Override
    public List<BaseAttrInfo> getBaseAttrInfo(Long skuId) {
        System.out.println("走熔断了");
        return null;
    }

    @Override
    public Map getSkuValueIdsMap(Long spuId) {
        System.out.println("走熔断了");
        return null;
    }

    @Override
    public Result getBaseCategoryList() {
        System.out.println("走熔断了");
        return null;
    }

    @Override
    public BaseTrademark getTrademark(Long tmId) {
        System.out.println("走熔断了");

        return null;
    }
}
