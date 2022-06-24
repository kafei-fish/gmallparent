package com.lxl.gmall.product.controller;

import com.baomidou.mybatisplus.extension.api.R;
import com.lxl.gmall.comon.util.result.Result;
import com.lxl.gmall.model.product.BaseCategoryTrademark;
import com.lxl.gmall.model.product.BaseTrademark;
import com.lxl.gmall.model.product.CategoryTrademarkVo;
import com.lxl.gmall.product.service.BaseCategoryTrademarkServcer;
import com.lxl.gmall.product.service.ManageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author LiXiaoLong
 * @Date 2022/6/12 21:02
 * @PackageName:com.lxl.gmall.product.controller
 * @ClassName: BaseCategoryTrademarkController
 * @Description: TODO
 * @Version 1.0
 */
@Api("分类品牌接口")
@RestController
@RequestMapping("/admin/product/baseCategoryTrademark")
public class BaseCategoryTrademarkController {
    @Autowired
    private ManageService manageService;
    @Autowired
    private BaseCategoryTrademarkServcer baseCategoryTrademarkServcer;
    @ApiOperation("分类品牌列表")
    @GetMapping("findTrademarkList/{category3Id}")
    public Result findTrademarkList(@PathVariable Long category3Id){
        //查询当前分类下有多少品牌
            List<BaseTrademark> baseCategoryTrademarkList=manageService.findTrademarkList(category3Id);
            return Result.ok(baseCategoryTrademarkList);
    }
    //根据分类ID获取可选品牌列表
    // /admin/product/baseCategoryTrademark/findCurrentTrademarkList/{category3Id}
    @GetMapping("findCurrentTrademarkList/{category3Id}")
    public Result findCurrentTrademarkList(@PathVariable Long category3Id){
        List<BaseTrademark> baseCategoryTrademarkList=manageService.getCurrentTrademarkList(category3Id);
        return Result.ok(baseCategoryTrademarkList);

    }
    //保存分类品牌ID 关联添加 BaseCategoryTrademark
    //  /admin/product/baseCategoryTrademark/save
    @PostMapping("save")
    public Result save(@RequestBody CategoryTrademarkVo categoryTrademarkVo){
        manageService.saveBaseCategoryTrademark(categoryTrademarkVo);
        return Result.ok();
    }
    //删除品牌分类 关联的集合
    // /admin/product/baseCategoryTrademark/remove/{category3Id}/{trademarkId}
    @DeleteMapping("remove/{category3Id}/{trademarkId}")
    public Result removeBaseCategoryTrademark(@PathVariable Long  category3Id,@PathVariable Long trademarkId){
        baseCategoryTrademarkServcer.removeBaseCategoryTrademark(category3Id,trademarkId);
        return Result.ok();
    }
}
