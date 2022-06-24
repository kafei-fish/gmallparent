package com.lxl.gmall.item.controller;

import com.google.j2objc.annotations.AutoreleasePool;
import com.lxl.gmall.comon.util.result.Result;
import com.lxl.gmall.item.servie.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @Author LiXiaoLong
 * @Date 2022/6/16 11:02
 * @PackageName:com.lxl.gmall.item.controller
 * @ClassName: ItemApiController
 * @Description: TODO
 * @Version 1.0
 */
@RestController
@RequestMapping("api/item")
public class ItemApiController {
    @Autowired
    private ItemService itemService;
    /**
     * 获取sku详情
     * @param skuId
     * @return
     */
    @GetMapping("getItem/{skuId}")
    public Result getItem(@PathVariable Long skuId){
        Map<String,Object> reslut=itemService.getSkuInfoById(skuId);
        return Result.ok(reslut);
    }
}
