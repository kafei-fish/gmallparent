package com.lxl.gmall.list.service;

import com.lxl.gmall.model.list.SearchParam;
import com.lxl.gmall.model.list.SearchResponseVo;

import java.io.IOException;

/**
 * @Author LiXiaoLong
 * @Date 2022/6/21 19:43
 * @PackageName:com.lxl.gmall.list.service
 * @ClassName: SearchService
 * @Description: TODO
 * @Version 1.0
 */
public interface SearchService {

    /**
     * 热度排名
     * @param skuId
     */
     void incrHotScore(Long skuId) ;

    /**
     * 上架
     * @param skuId
     */
    void upperGoods(Long skuId);

    /**
     * 下架
     * @param skuId
     */
    void lowerGoods(Long skuId);

    /**
     * es搜索商品
     * @param searchParam
     * @return
     */
    SearchResponseVo search(SearchParam searchParam) throws IOException;
}
