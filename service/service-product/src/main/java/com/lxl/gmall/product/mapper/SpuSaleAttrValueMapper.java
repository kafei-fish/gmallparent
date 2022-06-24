package com.lxl.gmall.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lxl.gmall.model.product.SpuSaleAttrValue;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Author LiXiaoLong
 * @Date 2022/6/12 18:59
 * @PackageName:com.lxl.gmall.product.mapper
 * @ClassName: SpuSaleAttrValueMapper
 * @Description: TODO
 * @Version 1.0
 */
@Mapper
public interface SpuSaleAttrValueMapper extends BaseMapper<SpuSaleAttrValue> {
     SpuSaleAttrValue getSpuSaleAttrValue(@Param("spuId") Long skuId, @Param("spuId") Long spuId);
}
