package com.lxl.gmail.list.client;

import com.lxl.gmall.comon.util.result.Result;
import com.lxl.gmall.model.list.SearchParam;
import org.checkerframework.checker.units.qual.C;
import org.springframework.stereotype.Component;

/**
 * @Author LiXiaoLong
 * @Date 2022/6/22 20:25
 * @PackageName:com.lxl.gmail.list.client
 * @ClassName: ListFeginClientImpl
 * @Description: TODO
 * @Version 1.0
 */
@Component
public class ListFeginClientImpl implements ListFeginClient {
    @Override
    public Result incrHotScore(Long skuId) {
        System.out.println("走熔断了");
        return null;
    }

    @Override
    public Result list(SearchParam searchParam) {
        System.out.println("走熔断了");
        return null;
    }

    @Override
    public Result lowerGoods(Long skuId) {
        System.out.println("走熔断了");
        return null;
    }

    @Override
    public Result upperGoods(Long skuId) {
        System.out.println("走熔断了");
        return null;
    }
}
