package com.lxl.gmall.item.servie;

import java.util.Map;

/**
 * @Author LiXiaoLong
 * @Date 2022/6/16 11:02
 * @PackageName:com.lxl.gmall.item.servie
 * @ClassName: ItemService
 * @Description: TODO
 * @Version 1.0
 */
public interface ItemService {

    /**
     * 获取Sku详情
     * @param skuId
     * @return
     */
    Map<String, Object> getSkuInfoById(Long skuId);
}
