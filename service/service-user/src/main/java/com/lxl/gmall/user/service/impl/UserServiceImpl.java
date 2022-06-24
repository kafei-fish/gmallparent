package com.lxl.gmall.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lxl.gmall.comon.util.util.MD5;
import com.lxl.gmall.model.user.UserInfo;
import com.lxl.gmall.user.mapper.UserMapper;
import com.lxl.gmall.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;

/**
 * @Author LiXiaoLong
 * @Date 2022/6/24 19:33
 * @PackageName:com.lxl.gmall.user.service.impl
 * @ClassName: UserServiceImpl
 * @Description: 登录
 * @Version 1.0
 */
@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserMapper userMapper;
    @Override
    public UserInfo login(UserInfo userInfo) {
        //查询用户是否存在
        //需要加上MD5加密
        //进行查询
        String passwd = userInfo.getPasswd();
        String loginName = userInfo.getLoginName();
        String newPwd = DigestUtils.md5DigestAsHex(passwd.getBytes());
        QueryWrapper<UserInfo> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("login_name",loginName);
        queryWrapper.eq("passwd",newPwd);
        UserInfo info = userMapper.selectOne(queryWrapper);
        if(info!=null){
            return info;
        }
        return null;
    }
}
