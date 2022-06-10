package com.lxl.gmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lxl.gmall.model.product.*;
import com.lxl.gmall.product.mapper.*;
import com.lxl.gmall.product.service.ManageService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @Author LiXiaoLong
 * @Date 2022/6/10 14:59
 * @PackageName:com.lxl.gmall.product.service.impl
 * @ClassName: ManagerServiceImpl
 * @Description: TODO
 * @Version 1.0
 */
@Service
public class ManagerServiceImpl implements ManageService {

    @Resource
    private BaseCategory1Mapper baseCategory1Mapper;
    @Resource
    private BaseCategory2Mapper baseCategory2Mapper;
    @Resource
    private BaseCategory3Mapper baseCategory3Mapper;
    @Resource
    private BaseAttrInfoMapper attrInfoMapper;
    @Resource
    private BaseAttrValueMapper baseAttrValueMapper;
    @Override
    public List<BaseCategory1> getCategory1() {

        return  baseCategory1Mapper.selectList(null);
    }

    @Override
    public List<BaseCategory2> getCategory2(Long category1Id) {
        return baseCategory2Mapper.selectList(new QueryWrapper<BaseCategory2>().eq("category1_id", category1Id));
    }

    @Override
    public List<BaseCategory3> getCategory3(Long category2Id) {
        return baseCategory3Mapper.selectList(new QueryWrapper<BaseCategory3>().eq("category2_id",category2Id));
    }

    @Override
    public List<BaseAttrInfo> getAttrInfoList(Long category1Id, Long category2Id, Long category3Id) {
       List<BaseAttrInfo> attrInfoList= attrInfoMapper.getByCategoryIdFindAttrInfoList(category1Id,category2Id,category3Id);
        return attrInfoList;
    }

    @Override
    public List<BaseAttrValue> getAttrValueList(Long attrId) {

        return baseAttrValueMapper.selectList(new QueryWrapper<BaseAttrValue>().eq("attrId",attrId));
    }
}
