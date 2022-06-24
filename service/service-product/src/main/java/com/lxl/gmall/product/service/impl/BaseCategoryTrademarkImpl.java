package com.lxl.gmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lxl.gmall.model.product.BaseCategoryTrademark;
import com.lxl.gmall.product.mapper.BaseCategoryTrademarkMapper;
import com.lxl.gmall.product.service.BaseCategoryTrademarkServcer;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author LiXiaoLong
 * @Date 2022/6/13 15:07
 * @PackageName:com.lxl.gmall.product.service.impl
 * @ClassName: BaseCategoryTrademarkImpl
 * @Description: TODO
 * @Version 1.0
 */
@Service
public class BaseCategoryTrademarkImpl extends ServiceImpl<BaseCategoryTrademarkMapper, BaseCategoryTrademark> implements BaseCategoryTrademarkServcer {
    @Resource
    private BaseCategoryTrademarkMapper baseCategoryTrademarkMapper;
    @Override
    public void removeBaseCategoryTrademark(Long category3Id, Long trademarkId) {
        QueryWrapper<BaseCategoryTrademark> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("category3_id",category3Id).eq("trademark_id",trademarkId);
        baseCategoryTrademarkMapper.delete(queryWrapper);
    }
}
