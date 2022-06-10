package com.lxl.gmall.product.controller;

import com.baomidou.mybatisplus.extension.api.R;
import com.lxl.gmall.comon.util.result.Result;
import com.lxl.gmall.model.product.*;
import com.lxl.gmall.product.service.ManageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @Author LiXiaoLong
 * @Date 2022/6/10 14:56
 * @PackageName:com.lxl.gmall.product.controller
 * @ClassName: BaseManageController
 * @Description: TODO
 * @Version 1.0
 */
@Api("商品基础信息接口")
@RestController
@RequestMapping("/admin/product")
public class BaseManageController {
    @Autowired
    private ManageService manageService;
    @ApiOperation(value = "获取一级分类")
    @GetMapping("getCategory1")
    public Result getCategory1(){
        List<BaseCategory1> baseCategory1List=manageService.getCategory1();

        return Result.ok(baseCategory1List);
    }
    @ApiOperation(value = "获取二级分类")
    @GetMapping("getCategory2/{category1Id}")
    public Result getCategory2(@PathVariable Long category1Id){
        List<BaseCategory2> baseCategory2List=manageService.getCategory2(category1Id);
        return Result.ok(baseCategory2List);
    }
    @ApiOperation(value = "获取三级分类")
    @GetMapping("getCategory3/{category2Id}")
    public Result getCategory3(@PathVariable Long category2Id){
        List<BaseCategory3> baseCategory3List=manageService.getCategory3(category2Id);
        return Result.ok(baseCategory3List);
    }
    @ApiOperation(value = "根据分类Id 获取平台属性集合")
    @GetMapping("attrInfoList/{category1Id}/{category2Id}/{category3Id}")
    public Result getAttrInfoList(@PathVariable Long category1Id,
                                  @PathVariable Long category2Id,
                                  @PathVariable Long category3Id){
        List<BaseAttrInfo> attrInfoList =manageService.getAttrInfoList(category1Id,category2Id,category3Id);
        return Result.ok(attrInfoList);
    }
    @ApiOperation(value = "根据平台属性Id 获取到平台属性值集合")
    @GetMapping("getAttrValueList/{attrId}")
    public Result getAttrValueList(@PathVariable Long attrId){
        List<BaseAttrValue> attrValueList=manageService.getAttrValueList(attrId);
        return Result.ok(attrValueList);
    }
}
