package com.lxl.gmall.list.controller;

import com.lxl.gmall.common.cache.GmallCache;
import com.lxl.gmall.comon.util.result.Result;
import com.lxl.gmall.list.service.SearchService;
import com.lxl.gmall.model.list.Goods;
import com.lxl.gmall.model.list.SearchParam;
import com.lxl.gmall.model.list.SearchResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * @Author LiXiaoLong
 * @Date 2022/6/21 19:38
 * @PackageName:com.lxl.gmall.list.controller
 * @ClassName: ListApiController
 * @Description: TODO
 * @Version 1.0
 */
@RestController
@RequestMapping("api/list")
public class ListApiController {
    @Autowired
    private SearchService searchService;

    @Autowired
    private ElasticsearchRestTemplate restTemplate;

    @GetMapping("inner/createIndex")
    public Result createIndex(){
        restTemplate.createIndex(Goods.class);
        restTemplate.putMapping(Goods.class);
        return Result.ok();
    }

    /**
     * 上架商品
     * @param skuId
     * @return
     */
    @GetMapping("inner/upperGoods/{skuId}")
    public Result upperGoods(@PathVariable Long skuId){
        searchService.upperGoods(skuId);
        return Result.ok();
    }

    /**
     * 下架商品
     * @param skuId
     * @return
     */
    @GetMapping("inner/lowerGoods/{skuId}")
    public Result lowerGoods(@PathVariable Long skuId){
        searchService.lowerGoods(skuId);
        return Result.ok();
    }

    /**
     * 更新热度
     * @param skuId
     * @return
     */
    @GetMapping("inner/incrHotScore/{skuId}")
    public   Result incrHotScore(@PathVariable Long skuId){
        searchService.incrHotScore(skuId);
        return Result.ok();
    }
    /**
     * 搜索商品
     * @param searchParam
     * @return
     * @throws IOException
     */
    @PostMapping
    public Result list(@RequestBody  SearchParam searchParam) throws IOException {
        SearchResponseVo response=searchService.search(searchParam);
        return Result.ok(response);
    }
}
