package com.lxl.gmall.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lxl.gmall.model.product.SpuSaleAttr;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author LiXiaoLong
 * @Date 2022/6/12 18:50
 * @PackageName:com.lxl.gmall.product.mapper
 * @ClassName: SpuSaleAttrMapper
 * @Description: TODO
 * @Version 1.0
 */
@Mapper
public interface SpuSaleAttrMapper extends BaseMapper<SpuSaleAttr> {
    /**
     * Spu销售属性更具Spuid查询
     * @param spuId spuId
     * @return spuSaleAttrListBySpuId
     */
    List<SpuSaleAttr> spuSaleAttrListBySpuId(Long spuId);

    /**
     *
     * @param id
     * @param spuId
     * @return
     */
    List<SpuSaleAttr> getSpuSaleAttrValue(@Param("skuId") Long skuId, @Param("spuId") Long spuId);
}
