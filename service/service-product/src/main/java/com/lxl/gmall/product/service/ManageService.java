package com.lxl.gmall.product.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lxl.gmall.model.product.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Author LiXiaoLong
 * @Date 2022/6/10 14:58
 * @PackageName:com.lxl.gmall.product.service
 * @ClassName: ManageService
 * @Description: 后台商品关联接口
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

    /**
     * 添加或者修改商品属性
     * @param baseAttrInfo 商品属性
     */
    void saveOrUpdateAttrInfo(BaseAttrInfo baseAttrInfo);

    /**
     * 根据平台属性Id，找到平台属性以及平台属性的集合
     * @param attrId 平台属性Id
     * @return 平台属性
     */
    BaseAttrInfo getBaseAttrInfo(Long attrId);

    /**
     * <p>SPU分页</p>
     * @param pagePram 分页条件参数
     * @return Page<SpuInfo></>
     */
    Page<SpuInfo> getProductPageList(Page pagePram,SpuInfo spuInfo);

    /**
     * <p>分页查询品牌</p>
     * @return
     */
    IPage<BaseTrademark> baseTrademarkPageList(Page pagePram);

    /**
     * 根据分类Id查询品牌信息
     * @param category3Id 3级分类ID
     * @return baseCategoryTrademarkList
     */
    List<BaseTrademark> findTrademarkList(Long category3Id);

    /**
     * 添加品牌
     * @param baseTrademark
     */
    void saveBaseTrademark(BaseTrademark baseTrademark);

    /**
     * 根据Id修改
     * @param baseTrademark
     */
    void updateById(BaseTrademark baseTrademark);

    /**
     * 通过Id获取详情
     * @param id
     * @return
     */
    BaseTrademark getBaseTrademarkById(Long id);

    /**
     * 更具ID删除
     * @param id
     */
    void removeById(Long id);

    /**
     * 根据category3Id获取可选品牌列表
     * @param category3Id 三级ID
     * @return  BaseTrademarkList
     */
    List<BaseTrademark> getCurrentTrademarkList(Long category3Id);

    /**
     * 添加
     * @param categoryTrademarkVo
     */
    void saveBaseCategoryTrademark(CategoryTrademarkVo categoryTrademarkVo);

    /**
     * 获取销售首先列表
     * @return baseSaleAttrList
     */
    List<BaseSaleAttr> baseSaleAttrList();

    /**
     * 添加SpuInfo
     * @param spuInfo
     */
    void saveSpuInfo(SpuInfo spuInfo);

    /**
     * 根据Spuid查询spu的图片
     * @param spuId spuId
     * @return spuImageList
     */
    List<SpuImage> spuImageList(Long spuId);

    /**
     * Spu销售属性更具Spuid查询
     * @param spuId spuId
     * @return spuSaleAttrListBySpuId
     */
    List<SpuSaleAttr> spuSaleAttrListBySpuId(Long spuId);

    /**
     * 保存Sku
     * @param
     */
    void saveSkuInfo(SkuInfo skuInfo);

    /**
     * 分页查询SkuInfo
     * @param pageParm page
     * @param skuInfo skuInfo
     * @return skuList
     */
    IPage<SkuInfo> skuList( Page<SkuInfo> pageParm, SkuInfo skuInfo);

    /**
     * 根据SpuId获取SpuInfo
     * @param spuId
     * @return
     */
    SpuInfo getSpuInfo(Long spuId);

    /**
     * 根据SpuId获取图片列表
     * @param spuId
     * @return
     */
    List<SpuImage> getSpuImageList(Long spuId);

    /**
     * 根据spuId获取海报
     * @param spuId
     * @return
     */
    List<SpuPoster> getSpuPosterList(Long spuId);

    /**
     * 跟新SpuInfo
     * @param spuInfo
     */
    void updateSpuInfo(SpuInfo spuInfo);

    /**
     * 获取SkyInfo
     * @param skuId
     * @return
     */
    SkuInfo getSkuInfo(Long skuId);

    /**
     * 修改SkuInfo
     * @param skuInfo
     */
    void updateSkuInfo(SkuInfo skuInfo);

    /**
     * 下架
     * @param skuId
     */
    void cancelSaleSku(Long skuId);

    /**
     * 上架
     * @param skuId
     */
    void onSaleSku(Long skuId);

    /**
     * 获取Api需要的SpuInfo
     * @param spuId spuId
     * @return
     */
    SkuInfo getApiSpuInfo(Long spuId);

    /**
     * 通过SkuID获取实时价格
     * @param skuId
     * @return
     */
    BigDecimal getPrice(Long skuId);

    /**
     * 根据SkuId查询出分类
     * @param skuId
     * @return
     */
    BaseCategoryView getBaseCategory(Long skuId);

    /**
     * 根据skuId查询出销售属性和以及是否被锁定
     * @param skuId
     * @return
     */
    List<SpuSaleAttr> getSpuSaleAttrValue(Long skuId);

    /**
     * 根据SkuId获取平台属性
     * @param skuId
     * @return
     */
    List<BaseAttrInfo> getBaseAttrInfoBySkuId(Long skuId);

    /**
     * 通过SpuID获取全部的sku对应销售属性与属性值
     * @param spuId
     * @return
     */
    Map getSkuValueIdsMap(Long spuId);

    /**
     * 对Skuinfo加缓存
     * @param skuId
     * @return
     */
    SkuInfo getApiSpuInfoRedisSeesion(Long skuId);

    SkuInfo getSkuInfoDB(Long skuId);

    /**
     * 获取分类
     * @return
     */
    List<JSONObject> getBaseCategoryList();

    /**
     * 获取品牌信息
     * @param tmId
     * @return
     */
    BaseTrademark getTrademark(Long tmId);
}
