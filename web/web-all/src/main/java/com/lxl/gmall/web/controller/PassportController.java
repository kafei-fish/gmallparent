package com.lxl.gmall.web.controller;

import com.lxl.gmall.model.user.UserInfo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author LiXiaoLong
 * @Date 2022/6/24 20:04
 * @PackageName:com.lxl.gmall.web.controller
 * @ClassName: PassportController
 * @Description: TODO
 * @Version 1.0
 */
@Controller
public class PassportController {
    @GetMapping("login.html")
    public String login(HttpServletRequest request){
        String originUrl = request.getParameter("originUrl");
        request.setAttribute("originUrl",originUrl);
        return "login";
    }
}
