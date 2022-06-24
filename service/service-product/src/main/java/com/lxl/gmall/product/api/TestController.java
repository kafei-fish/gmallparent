package com.lxl.gmall.product.api;

import com.lxl.gmall.comon.util.result.Result;
import com.lxl.gmall.product.service.TestService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author LiXiaoLong
 * @Date 2022/6/18 16:29
 * @PackageName:com.lxl.gmall.product.api
 * @ClassName: TestController
 * @Description: TODO
 * @Version 1.0
 */
@Api(tags = "测试接口")
@RestController
@RequestMapping("admin/product/test")
public class TestController {
    @Autowired
    private TestService testService;
    @GetMapping("testLock")
    public Result test(){
        testService.testLock();
        return Result.ok();
    }
}
