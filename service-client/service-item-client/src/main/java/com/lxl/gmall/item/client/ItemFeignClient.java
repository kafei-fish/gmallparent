package com.lxl.gmall.item.client;

import com.lxl.gmall.comon.util.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Author LiXiaoLong
 * @Date 2022/6/16 14:00
 * @PackageName:com.lxl.gmall.item.client
 * @ClassName: ItemFeignClient
 * @Description: TODO
 * @Version 1.0
 */
@FeignClient(value = "service-item",fallback = ItemDegradeClient.class)
public interface ItemFeignClient {
    /**
     * 获取sku详情
     * @param skuId
     * @return
     */
    @GetMapping("api/item/getItem/{skuId}")
    public Result getItem(@PathVariable Long skuId);

}
