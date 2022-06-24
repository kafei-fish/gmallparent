package com.lxl.gmall.web.controller;

import com.lxl.gmall.comon.util.result.Result;
import com.lxl.gmall.item.client.ItemFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * @Author LiXiaoLong
 * @Date 2022/6/16 20:10
 * @PackageName:com.lxl.gmall.web.controller
 * @ClassName: ItemController
 * @Description: TODO
 * @Version 1.0
 */
@Controller
public class ItemController {

    @Autowired
    private ItemFeignClient itemFeignClient;
    @RequestMapping("{skuId}.html")
    public String getItem(@PathVariable Long skuId, Model model){
        Result<Map> item = itemFeignClient.getItem(skuId);
        model.addAllAttributes(item.getData());
        return "item/item";
    }
    /**
     * 首页
     */

}
