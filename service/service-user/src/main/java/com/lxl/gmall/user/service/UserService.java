package com.lxl.gmall.user.service;

import com.lxl.gmall.model.user.UserInfo;

/**
 * @Author LiXiaoLong
 * @Date 2022/6/24 19:33
 * @PackageName:com.lxl.gmall.user.service
 * @ClassName: UserService
 * @Description: TODO
 * @Version 1.0
 */
public interface UserService {
    /**
     * 登录
     * @param userInfo
     * @return
     */
    UserInfo login(UserInfo userInfo);
}
