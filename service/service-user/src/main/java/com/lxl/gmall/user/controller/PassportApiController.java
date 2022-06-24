package com.lxl.gmall.user.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.api.R;
import com.lxl.gmall.common.constant.RedisConst;
import com.lxl.gmall.comon.util.result.Result;
import com.lxl.gmall.comon.util.util.IpUtil;
import com.lxl.gmall.model.user.UserInfo;
import com.lxl.gmall.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Author LiXiaoLong
 * @Date 2022/6/24 19:22
 * @PackageName:com.lxl.gmall.user.controller
 * @ClassName: PassportApiController
 * @Description: 用户认证接口
 * @Version 1.0
 */
@RestController
@RequestMapping("api/user/passport")
public class PassportApiController {
    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate redisTemplate;
    @PostMapping("login")
    public Result login(@RequestBody UserInfo userInfo, HttpServletRequest request){
        //获取登录数据
        UserInfo info=userService.login(userInfo);
        //如果有数据，就说明登录成功，将用户存储在reiuds中，并将用户信息存储为token
        if(info!=null){
            String token = UUID.randomUUID().toString();
            //声明map集合，将token与用户名一并返回
            Map<String,Object> map=new HashMap<>();
            map.put("nickName",userInfo.getLoginName());
            //token
            map.put("token",token);
            //将用户信息放入redis中，为空放置恶意盗用token进行等，我们将当前登录成功的ip地址进行添加搭配redis中
            String userKey= RedisConst.USER_KEY_PREFIX+token;
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("userId",userInfo.getId());
            //获取Ip，并存入redis中
            jsonObject.put("ip", IpUtil.getIpAddress(request));
            redisTemplate.opsForValue().set(userKey,jsonObject.toJSONString(),RedisConst.SKUKEY_TEMPORARY_TIMEOUT, TimeUnit.SECONDS);
            return Result.ok(map);
        }else {
            return Result.fail().message("登录失败");
        }
    }
    @GetMapping("logout")
    public Result logout(HttpServletRequest request){
        redisTemplate.delete(RedisConst.USER_KEY_PREFIX+request.getHeader("token"));
        return Result.ok();
    }
}
