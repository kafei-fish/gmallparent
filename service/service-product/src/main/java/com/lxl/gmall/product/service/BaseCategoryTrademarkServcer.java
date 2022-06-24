package com.lxl.gmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lxl.gmall.model.product.BaseCategoryTrademark;

/**
 * @Author LiXiaoLong
 * @Date 2022/6/13 15:06
 * @PackageName:com.lxl.gmall.product.service
 * @ClassName: BaseCategoryTrademark
 * @Description: TODO
 * @Version 1.0
 */
public interface BaseCategoryTrademarkServcer extends IService<BaseCategoryTrademark> {
    /**
     * 删除
     * @param category3Id
     * @param trademarkId
     */
    void removeBaseCategoryTrademark(Long category3Id, Long trademarkId);
}
