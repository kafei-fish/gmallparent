package com.lxl.gmall.item.servie.impl;

import com.alibaba.fastjson.JSON;
import com.lxl.gmail.list.client.ListFeginClient;
import com.lxl.gmall.common.constant.RedisConst;
import com.lxl.gmall.comon.util.result.Result;
import com.lxl.gmall.item.servie.ItemService;
import com.lxl.gmall.model.product.*;
import com.lxl.gmall.product.client.ProductFeignClient;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * @Author LiXiaoLong
 * @Date 2022/6/16 11:02
 * @PackageName:com.lxl.gmall.item.servie.impl
 * @ClassName: ItemServiceImpl
 * @Description: TODO
 * @Version 1.0
 */
@Service
public class ItemServiceImpl implements ItemService {

    @Resource
    private ProductFeignClient productFeignClient;
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;
    @Autowired
    private RedissonClient redissonClient;
    @Resource
    private ListFeginClient listFeginClient;
    @Override
    public Map<String, Object> getSkuInfoById(Long skuId) {
        //声明对象存储数据
        Map<String,Object> result=new HashMap<>();
        //不包含
        RBloomFilter<Long> rbloomFilter = redissonClient.getBloomFilter(RedisConst.SKU_BLOOM_FILTER);
        if(rbloomFilter.contains(skuId)){
            return result;
        }
        CompletableFuture<SkuInfo> skuInfoCompletableFuture = CompletableFuture.supplyAsync(() -> {
            //获取Sku基本信息
            SkuInfo skuInfo = productFeignClient.getSpuInfo(skuId);
            result.put("skuInfo",skuInfo);
            return skuInfo;
        },threadPoolExecutor);
        /**
         * runAsync 无返回值
         * supplyAsync 有返回值
         */
        CompletableFuture<Void> incrHotScoreCompletableFuture  = CompletableFuture.runAsync(() -> {
            listFeginClient.incrHotScore(skuId);
        } ,threadPoolExecutor);
        CompletableFuture<Void> valuesSkuJson = skuInfoCompletableFuture.thenAcceptAsync(skuInfo -> {

            //通过spu获取到每个sku用的销售属性与销售属性值，并封装为map集合
            Map skuValueIdsMap = productFeignClient.getSkuValueIdsMap(skuInfo.getSpuId());
            String jsonString = JSON.toJSONString(skuValueIdsMap);
            result.put("valuesSkuJson", jsonString);
        },threadPoolExecutor);
        CompletableFuture<Void> spuPosterList1 = skuInfoCompletableFuture.thenAcceptAsync(skuInfo -> {


            //获取spu的海报信息
            List<SpuPoster> spuPosterList = productFeignClient.getSpuPosterList(skuInfo.getSpuId());

            result.put("spuPosterList", spuPosterList);
        },threadPoolExecutor);
        CompletableFuture<Void> categoryView = skuInfoCompletableFuture.thenAcceptAsync(skuInfo -> {


            //获取分类信息
            BaseCategoryView baseCategory = productFeignClient.getBaseCategory(skuId);
            result.put("categoryView", baseCategory);
        },threadPoolExecutor);

        CompletableFuture<Void> spuSaleAttrList = CompletableFuture.runAsync(() -> {
            List<SpuSaleAttr> spuSaleAttrValue = productFeignClient.getSpuSaleAttrValue(skuId);
            result.put("spuSaleAttrList", spuSaleAttrValue);
        },threadPoolExecutor);
        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(() -> {
            //将调用数据存入对象中
            //获取销售规则
            List<BaseAttrInfo> baseAttrInfoList = productFeignClient.getBaseAttrInfo(skuId);
            List<Map<String, String>> skuAttrList = baseAttrInfoList.stream().map(baseAttrInfo -> {
                Map<String, String> attrMap = new HashMap<>();
                attrMap.put("attrName", baseAttrInfo.getAttrName());
                attrMap.put("attrValue", baseAttrInfo.getAttrValueList().get(0).getValueName());
                return attrMap;
            }).collect(Collectors.toList());

            result.put("skuAttrList", skuAttrList);
        },threadPoolExecutor);
        CompletableFuture<Void> priceCompletableFuture = CompletableFuture.runAsync(() -> {

            //获取实时价格
            BigDecimal price = productFeignClient.getPrice(skuId);
            result.put("price", price);

        },threadPoolExecutor);
      CompletableFuture.allOf(skuInfoCompletableFuture,
                      valuesSkuJson,
                      spuPosterList1,
                      categoryView,
                      spuSaleAttrList,
                      voidCompletableFuture,
                      priceCompletableFuture,
                      incrHotScoreCompletableFuture).join();

        return result;
    }
}
