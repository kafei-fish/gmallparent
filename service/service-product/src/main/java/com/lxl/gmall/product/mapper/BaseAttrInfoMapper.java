package com.lxl.gmall.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lxl.gmall.model.product.BaseAttrInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author LiXiaoLong
 * @Date 2022/6/10 16:51
 * @PackageName:com.lxl.gmall.product.mapper
 * @ClassName: BaseAttrInfoMapper
 * @Description: TODO
 * @Version 1.0
 */
@Mapper
public interface BaseAttrInfoMapper extends BaseMapper<BaseAttrInfo> {
    /**
     * 通过分类id获取平台属性集合
     * @param category1Id 一级分类
     * @param category2Id 二级分类
     * @param category3Id 案件分类
     * @return 平台属性数据集合
     */
    List<BaseAttrInfo> getByCategoryIdFindAttrInfoList(
            @Param("category1Id") Long category1Id,
            @Param("category2Id") Long category2Id,
            @Param("category3Id") Long category3Id);
}
