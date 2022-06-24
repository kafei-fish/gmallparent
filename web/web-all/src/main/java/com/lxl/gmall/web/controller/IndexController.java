package com.lxl.gmall.web.controller;

import com.lxl.gmall.comon.util.result.Result;
import com.lxl.gmall.product.client.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileWriter;

/**
 * @Author LiXiaoLong
 * @Date 2022/6/21 17:14
 * @PackageName:com.lxl.gmall.web.controller
 * @ClassName: IndexController
 * @Description: TODO
 * @Version 1.0
 */
@Controller
public class IndexController {
    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private ProductFeignClient productFeignClient;
    @GetMapping({"/","index.html"})
    public String index(HttpServletRequest request){
        Result result = productFeignClient.getBaseCategoryList();
        request.setAttribute("list",result.getData());
        return "index/index";
    }
    @GetMapping("createIndex")
    @ResponseBody
    public Result createIndex(){
        //获取后台数据
        Result result = productFeignClient.getBaseCategoryList();
        //  设置模板显示的内容
        Context context = new Context();
        context.setVariable("list",result.getData());

        //定义输入位置
        FileWriter fileWriter=null;
        try {
            fileWriter=new FileWriter("D:\\web\\index.html");

        }catch (Exception e){
            e.printStackTrace();
        }
        //  调用process();方法创建模板
        templateEngine.process("index/index.html",context,fileWriter);
        return Result.ok();
    }
}
