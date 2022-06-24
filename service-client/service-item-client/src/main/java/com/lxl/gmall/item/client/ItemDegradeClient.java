package com.lxl.gmall.item.client;

import com.lxl.gmall.comon.util.result.Result;
import org.springframework.stereotype.Component;

/**
 * @Author LiXiaoLong
 * @Date 2022/6/16 14:02
 * @PackageName:com.lxl.gmall.item.client
 * @ClassName: ItemDegradeClient
 * @Description: TODO
 * @Version 1.0
 */
@Component
public class ItemDegradeClient implements ItemFeignClient {
    @Override
    public Result getItem(Long skuId) {
        System.out.println("走熔断了");
        return Result.fail();
    }
}
