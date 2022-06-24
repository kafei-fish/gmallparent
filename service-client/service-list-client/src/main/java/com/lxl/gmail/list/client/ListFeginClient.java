package com.lxl.gmail.list.client;

import com.lxl.gmall.comon.util.result.Result;
import com.lxl.gmall.model.list.SearchParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author LiXiaoLong
 * @Date 2022/6/22 20:24
 * @PackageName:com.lxl.gmail.list.client
 * @ClassName: ListFeginClient
 * @Description: TODO
 * @Version 1.0
 */
@FeignClient(value = "service-list",fallback = ListFeginClientImpl.class)
public interface ListFeginClient {
    @GetMapping("api/list/inner/incrHotScore/{skuId}")
    public Result incrHotScore(@PathVariable Long skuId);

    /**
     * 搜索商品
     * @param searchParam
     */
    @PostMapping("api/list")
    public Result list(@RequestBody SearchParam searchParam);

    /**
     * 下架商品
     * @param skuId
     * @return
     */
    @GetMapping("api/listinner/lowerGoods/{skuId}")
    public Result lowerGoods(@PathVariable Long skuId);
    /**
     * 上架商品
     * @param skuId
     * @return
     */
    @GetMapping("api/list/inner/upperGoods/{skuId}")
    public Result upperGoods(@PathVariable Long skuId);
}
