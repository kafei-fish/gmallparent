package com.lxl.gmall.product.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lxl.gmall.comon.util.result.Result;
import com.lxl.gmall.model.product.BaseTrademark;
import com.lxl.gmall.product.service.ManageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author LiXiaoLong
 * @Date 2022/6/12 21:01
 * @PackageName:com.lxl.gmall.product.controller
 * @ClassName: BaseTrademarkController
 * @Description: TODO
 * @Version 1.0
 */
@Api("品牌管理")
@RestController
@RequestMapping("/admin/product/baseTrademark")
public class BaseTrademarkController {
    @Autowired
    private ManageService manageService;
    //品牌列表集合
    // /admin/product/baseTrademark/{page}/{limit}
    @ApiOperation("品牌列表集合")
    @GetMapping("{page}/{limit}")
    public Result baseTrademark(@PathVariable Integer page, @PathVariable Integer limit){
        Page pagePram =new Page(page,limit);
        IPage<BaseTrademark> baseTrademarkIPage=manageService.baseTrademarkPageList(pagePram);
        return Result.ok(baseTrademarkIPage);
    }
    //保存品牌
    ///admin/product/baseTrademark/save
    @PostMapping("save")
    public Result save(@RequestBody BaseTrademark baseTrademark){
        manageService.saveBaseTrademark(baseTrademark);
        return Result.ok();
    }
    //修改品牌
    // /admin/product/baseTrademark/update
    @PutMapping("")
    public Result updateById(@RequestBody BaseTrademark baseTrademark){
        manageService.updateById(baseTrademark);
        return Result.ok();
    }
    //获取详情
    ///admin/product/baseTrademark/get/{id}
    @GetMapping("get/{id}")
    public Result getBaseTrademarkById(@PathVariable Long id ){
        BaseTrademark baseTrademark=manageService.getBaseTrademarkById(id);
        return Result.ok(baseTrademark);
    }
    //根据id删除品牌
    ///admin/product/baseTrademark/remove/{id}
    @DeleteMapping("remove/{id}")
    public Result removeById(@PathVariable Long id){
        manageService.removeById(id);
        return Result.ok();
    }
    //更具品牌关键字检索品牌
    ///admin/product/baseTrademark/findBaseTrademarkByKeyword/{keyword}
    //更具品牌id获取对应品牌集合数据
    ///admin/product/baseTrademark/inner/findBaseTrademarkByTrademarkIdList

}
