package com.lxl.gmall.product.service;

import com.lxl.gmall.model.product.*;

import java.util.List;
import java.util.Map;

/**
 * @Author LiXiaoLong
 * @Date 2022/6/10 14:58
 * @PackageName:com.lxl.gmall.product.service
 * @ClassName: ManageService
 * @Description: TODO
 * @Version 1.0
 */
public interface ManageService {
    /**
     * 获取一级分类接口
     * @return 一级分类
     */
    List<BaseCategory1> getCategory1();

    /**
     * 获取二级分类接口
     * @param category1Id 一级分类Id
     * @return 二级分类列表
     */
    List<BaseCategory2> getCategory2(Long category1Id);

    /**
     * 获取三级分类接口
     * @param category2Id 二级分类Id
     * @return 三级分类列表
     */
    List<BaseCategory3> getCategory3(Long category2Id);

    /**
     * 通过分类id获取平台属性集合
     * @param category1Id 一级分类
     * @param category2Id 二级分类
     * @param category3Id 案件分类
     * @return 平台属性数据集合
     */
    List<BaseAttrInfo> getAttrInfoList(Long category1Id, Long category2Id, Long category3Id);

    /**
     * 根据平台属性Id 获取到平台属性值集合
     * @param attrId 平台属性Id
     * @return 平台数据集合
     */
    List<BaseAttrValue> getAttrValueList(Long attrId);
}
