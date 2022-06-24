package com.lxl.gmall.product.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lxl.gmall.model.product.SpuImage;
import com.lxl.gmall.product.mapper.SpuImageMapper;
import com.lxl.gmall.product.service.SpuImageServer;
import org.springframework.stereotype.Service;

/**
 * @Author LiXiaoLong
 * @Date 2022/6/14 20:31
 * @PackageName:com.lxl.gmall.product.service.impl
 * @ClassName: SpuImageServerImpl
 * @Description: TODO
 * @Version 1.0
 */
@Service
public class SpuImageServerImpl  extends ServiceImpl<SpuImageMapper, SpuImage> implements SpuImageServer {
}
