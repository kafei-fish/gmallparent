package com.lxl.gmall.product.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lxl.gmall.common.cache.GmallCache;
import com.lxl.gmall.common.constant.RedisConst;
import com.lxl.gmall.comon.util.execption.GmallException;
import com.lxl.gmall.model.product.*;
import com.lxl.gmall.product.mapper.*;
import com.lxl.gmall.product.service.BaseCategoryTrademarkServcer;
import com.lxl.gmall.product.service.ManageService;
import com.lxl.gmall.product.service.SpuImageServer;
import com.lxl.gmall.product.service.SpuPosterServer;
import org.checkerframework.checker.units.qual.A;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.data.domain.Example;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.rmi.server.UID;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
    @Resource
    private SpuInfoMapper spuInfoMapper;
    @Resource
    private SpuImageMapper spuImageMapper;
    @Resource
    private SpuImageServer spuImageServer;
    @Resource
    private SpuPosterMapper spuPosterMapper;
    @Resource
    private SpuPosterServer spuPosterServer;
    @Resource
    private SpuSaleAttrMapper spuSaleAttrMapper;
    @Resource
    private SpuSaleAttrValueMapper spuSaleAttrValueMapper;
    @Resource
    private BaseTrademarkMapper baseTrademarkMapper;
    @Resource
    private BaseCategoryTrademarkMapper baseCategoryTrademarkMapper;
    @Resource
    private BaseCategoryTrademarkServcer baseCategoryTrademarkServcer;
    @Resource
    private BaseSaleAttrMapper baseSaleAttrMapper;
    @Resource
    private SkuInfoMapper skuInfoMapper;
    @Resource
    private SkuImageMapper skuImageMapper;
    @Resource
    private SkuAttrValueMapper skuAttrValueMapper;

    @Resource
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;
    @Resource
    private BaseCategoryViewMapper baseCategoryViewMapper;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private RedissonClient redissonClient;
    /**
     * ??????????????????
     * @return ??????????????????
     */
    @Override
    public List<BaseCategory1> getCategory1() {

        return  baseCategory1Mapper.selectList(null);
    }

    /**
     *
     * @param category1Id ????????????Id
     * @return ??????????????????
     */
    @Override
    public List<BaseCategory2> getCategory2(Long category1Id) {
        return baseCategory2Mapper.selectList(new QueryWrapper<BaseCategory2>().eq("category1_id", category1Id));
    }

    /**
     *
     * @param category2Id ????????????Id
     * @return ??????????????????
     */
    @Override
    public List<BaseCategory3> getCategory3(Long category2Id) {
        return baseCategory3Mapper.selectList(new QueryWrapper<BaseCategory3>().eq("category2_id",category2Id));
    }

    /**
     * ????????????id????????????????????????
     * @param category1Id ????????????
     * @param category2Id ????????????
     * @param category3Id ????????????
     * @return ????????????????????????
     */
    @Override
    public List<BaseAttrInfo> getAttrInfoList(Long category1Id, Long category2Id, Long category3Id) {
       List<BaseAttrInfo> attrInfoList= attrInfoMapper.getByCategoryIdFindAttrInfoList(category1Id,category2Id,category3Id);
        return attrInfoList;
    }

    /**
     * ??????????????????Id ??????????????????????????????
     * @param attrId ????????????Id
     * @return ??????????????????
     */
    @Override
    public  List<BaseAttrValue> getAttrValueList(Long attrId) {
        List<BaseAttrValue> baseAttrValueList = baseAttrValueMapper.selectList(new QueryWrapper<BaseAttrValue>().eq("attr_id", attrId));

        return baseAttrValueList;

    }

    /**
     * ??????????????????????????????
     * @param baseAttrInfo ????????????
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdateAttrInfo(BaseAttrInfo baseAttrInfo) {


        if(baseAttrInfo.getId()==null){
            //??????
            attrInfoMapper.insert(baseAttrInfo);
        }else {
            //??????
            attrInfoMapper.updateById(baseAttrInfo);
            //???????????????????????????????????????????????????????????????????????????????????????????????????
            QueryWrapper<BaseAttrValue> baseAttrValueQueryWrapper=new QueryWrapper<>();
            baseAttrValueQueryWrapper.eq("attr_id",baseAttrInfo.getId());
            baseAttrValueMapper.delete(baseAttrValueQueryWrapper);
        }
        //??????BaseAttrVlaue????????????????????????????????????
        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
        attrValueList.forEach((baseAttrValue)->{
            //  base_attr_value.attr_id ??????????????????????????????????????????????????????.
            baseAttrValue.setAttrId(baseAttrInfo.getId());
            baseAttrValueMapper.insert(baseAttrValue);
        });

    }

    /**
     * ??????????????????Id????????????????????????????????????????????????
     * @param attrId ????????????Id
     * @return
     */
    @Override
    public BaseAttrInfo getBaseAttrInfo(Long attrId) {
        BaseAttrInfo baseAttrInfo = attrInfoMapper.selectById(attrId);
        if(baseAttrInfo!=null){
            baseAttrInfo.setAttrValueList(this.getAttrValueList(attrId));
        }

        return baseAttrInfo;
    }

    /**
     *
     * @param pagePram ??????????????????
     * @return
     */
    @Override
    public Page<SpuInfo> getProductPageList(Page pagePram,SpuInfo spu) {
        QueryWrapper<SpuInfo> queryWrapper=new QueryWrapper();
        queryWrapper.eq("is_deleted",0);
        queryWrapper.eq("category3_id",spu.getCategory3Id());
        Page<SpuInfo> page = spuInfoMapper.selectPage(pagePram, queryWrapper);
        List<SpuInfo> spuInfoList=page.getRecords();
        spuInfoList.forEach((spuInfo)->{
            this.packSpuInfo(spuInfo);
        });

        return page;
    }

    @Override
    public IPage<BaseTrademark> baseTrademarkPageList(Page pagePram) {
        QueryWrapper<BaseTrademark> queryWrapper=new QueryWrapper<>();
        queryWrapper.orderByAsc("id");
        IPage<BaseTrademark> page = baseTrademarkMapper.selectPage(pagePram, queryWrapper);
        return page;
    }

    /**
     * ??????3?????????Id??????BaseTrademark??????
     * @param category3Id 3?????????ID
     * @return
     */
    @Override
    public List<BaseTrademark> findTrademarkList(Long category3Id) {
        //????????????category3Id ?????????tm_id
        List<BaseCategoryTrademark> baseCategoryTrademarkList =
                baseCategoryTrademarkMapper.
                        selectList(new QueryWrapper<BaseCategoryTrademark>().eq("category3_id", category3Id));

        //??????foreach??????
//        List<BaseTrademark> baseTrademarksList=new ArrayList<>();
//        if(baseCategoryTrademarkList!=null){
//            for (BaseCategoryTrademark baseCategoryTrademark : baseCategoryTrademarkList) {
//                BaseTrademark baseTrademark = baseTrademarkMapper.selectById(baseCategoryTrademark.getId());
//                baseTrademarksList.add(baseTrademark);
//            }
//        }
        //??????Stream?????????tradMarkId
        if(!CollectionUtils.isEmpty(baseCategoryTrademarkList)){
            List<Long> tradeMarkIdList  = baseCategoryTrademarkList.stream().map(baseCategoryTrademark -> {
                return baseCategoryTrademark.getTrademarkId();
            }).collect(Collectors.toList());
            return baseTrademarkMapper.selectBatchIds(tradeMarkIdList);
        }

        return null;
    }

    /**
     *
     * @param baseTrademark
     */
    @Override
    public void saveBaseTrademark(BaseTrademark baseTrademark) {
        if(baseTrademark!=null){
            baseTrademarkMapper.insert(baseTrademark);
        }else {
            throw new GmallException("????????????",20001);
        }
    }

    /**
     * ??????ID????????????
     * @param Trademark
     */
    @Override
    public void updateById(BaseTrademark Trademark) {
        Long id = Trademark.getId();
        BaseTrademark baseTrademark = baseTrademarkMapper.selectById(id);
        baseTrademark.setTmName(Trademark.getTmName());
        baseTrademark.setLogoUrl(Trademark.getLogoUrl());
        baseTrademark.setUpdateTime(new Date());
        baseTrademarkMapper.updateById(baseTrademark);
    }

    /**
     * ??????ID????????????
     * @param id
     * @return
     */
    @Override
    public BaseTrademark getBaseTrademarkById(Long id) {
        BaseTrademark baseTrademark = baseTrademarkMapper.selectById(id);
        if(baseTrademark!=null){
            return baseTrademark;
        }
        return null;
    }

    /**
     * ??????
     * @param id
     */
    @Override
    public void removeById(Long id) {
        baseTrademarkMapper.deleteById(id);
    }

    @Override
    public List<BaseTrademark> getCurrentTrademarkList(Long category3Id) {
        //????????????category3Id ?????????tm_id
        List<BaseCategoryTrademark> baseCategoryTrademarkList =
                baseCategoryTrademarkMapper.
                        selectList(new QueryWrapper<BaseCategoryTrademark>().eq("category3_id", category3Id));
        if(!CollectionUtils.isEmpty(baseCategoryTrademarkList)){
            List<Long> trademarkIds = baseCategoryTrademarkList.stream().map((baseCategoryTrademark -> {
                return baseCategoryTrademark.getTrademarkId();
            })).collect(Collectors.toList());

            //??????????????????
            List<BaseTrademark> baseTrademarkList = baseTrademarkMapper.selectList(null).stream().filter(baseTrademark -> {
                return !trademarkIds.contains(baseTrademark.getId());
            }).collect(Collectors.toList());
            return baseTrademarkList;
        }else {
            return baseTrademarkMapper.selectList(null);
        }

    }

    @Override
    public void saveBaseCategoryTrademark(CategoryTrademarkVo categoryTrademarkVo) {
        //????????????
        //?????????
        List<Long> trademarkIdList = categoryTrademarkVo.getTrademarkIdList();
//        if(!CollectionUtils.isEmpty(trademarkIdList)){
//            trademarkIdList.forEach((ids)->{
//                BaseCategoryTrademark baseCategoryTrademark =new BaseCategoryTrademark();
//                baseCategoryTrademark.setCategory3Id(categoryTrademarkVo.getCategory3Id());
//                baseCategoryTrademark.setTrademarkId(ids);
//                baseCategoryTrademarkMapper.insert(baseCategoryTrademark);
//            });
         //??????Stream???
            if(!CollectionUtils.isEmpty(trademarkIdList)){
                List<BaseCategoryTrademark> baseCategoryTrademarkList = trademarkIdList.stream().map(trademarkId -> {
                    BaseCategoryTrademark baseCategoryTrademark = new BaseCategoryTrademark();
                    baseCategoryTrademark.setCategory3Id(categoryTrademarkVo.getCategory3Id());
                    baseCategoryTrademark.setTrademarkId(trademarkId);
                    return baseCategoryTrademark;
                }).collect(Collectors.toList());
                baseCategoryTrademarkServcer.saveBatch(baseCategoryTrademarkList);
            }

    }

    @Override
    public List<BaseSaleAttr> baseSaleAttrList() {

        return  baseSaleAttrMapper.selectList(null);
    }

    /**
     * ??????SpuInfo
     * @param spuInfo
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveSpuInfo(SpuInfo spuInfo) {
        //1.??????SpuInfo
        spuInfoMapper.insert(spuInfo);
        this.saveList(spuInfo);
    }

    @Override
    public List<SpuImage> spuImageList(Long spuId) {
        return spuImageMapper.selectList(new QueryWrapper<SpuImage>().eq("spu_id",spuId));
    }

    @Override
    public List<SpuSaleAttr> spuSaleAttrListBySpuId(Long spuId) {
        return   spuSaleAttrMapper.spuSaleAttrListBySpuId(spuId);

    }

    @Override
    public void saveSkuInfo(SkuInfo skuInfo) {
        /**
         * ??????????????????
         * 1.sku_info
         * 2.sku_image
         * 3.sku_attr_value
         * 4.sku_sale_attr_value
         */
        //??????Sku_info
        skuInfoMapper.insert(skuInfo);

        this.saveSkuList(skuInfo);
        //??????????????????
        RBloomFilter<Long> rbloomFilter = redissonClient.getBloomFilter(RedisConst.SKU_BLOOM_FILTER);
        rbloomFilter.add(skuInfo.getId());
    }

    @Override
    public IPage<SkuInfo> skuList( Page<SkuInfo> pageParm, SkuInfo skuInfo) {
        QueryWrapper queryWrapper=new QueryWrapper();
        queryWrapper.eq("category3_id",skuInfo.getCategory3Id());
        return skuInfoMapper.selectPage(pageParm, queryWrapper);

    }

    @Override
    public SpuInfo getSpuInfo(Long spuId) {
        SpuInfo spuInfo = spuInfoMapper.selectById(spuId);
        //??????????????????
        List<SpuImage> spuImageList = spuImageMapper.selectList(new QueryWrapper<SpuImage>().eq("spu_id", spuInfo.getId()));
        spuInfo.setSpuImageList(spuImageList);
        //??????????????????
        List<SpuPoster> posterList = spuPosterMapper.selectList(new QueryWrapper<SpuPoster>().eq("spu_id", spuInfo.getId()));
        spuInfo.setSpuPosterList(posterList);
        //????????????
        List<SpuSaleAttr> spuSaleAttrList =
                spuSaleAttrMapper.selectList(new QueryWrapper<SpuSaleAttr>().eq("spu_id", spuInfo.getId()));
        spuSaleAttrList.forEach(spuSaleAttr -> {

            List<SpuSaleAttrValue> spuSaleAttrValues = spuSaleAttrValueMapper.
                    selectList(new QueryWrapper<SpuSaleAttrValue>().
                            eq("spu_id", spuInfo.getId()).
                            eq("base_sale_attr_id", spuSaleAttr.getBaseSaleAttrId()));
            spuSaleAttr.setSpuSaleAttrValueList(spuSaleAttrValues);
        });
        spuInfo.setSpuSaleAttrList(spuSaleAttrList);
        return spuInfo;
    }

    @Override
    public List<SpuImage> getSpuImageList(Long spuId) {
        return spuImageMapper.selectList(new QueryWrapper<SpuImage>().eq("spu_id",spuId));
    }

    @Override
    @GmallCache(prefix = "SpuPosterList:")
    public List<SpuPoster> getSpuPosterList(Long spuId) {
        return spuPosterMapper.selectList(new QueryWrapper<SpuPoster>().eq("spu_id",spuId));
    }

    /**
     * ??????Spuinfo
     * @param spuInfo
     */
    @Override
    public void updateSpuInfo(SpuInfo spuInfo) {


       spuImageMapper.delete(new QueryWrapper<SpuImage>().eq("spu_id",spuInfo.getId()));
       spuPosterMapper.delete(new QueryWrapper<SpuPoster>().eq("spu_id",spuInfo.getId()));

        //?????????????????? ??????????????? ???????????????????????????????????????
        List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();
        spuSaleAttrMapper.delete(new QueryWrapper<SpuSaleAttr>().eq("spu_id",spuInfo.getId()));
        spuSaleAttrValueMapper.delete(new QueryWrapper<SpuSaleAttrValue>().eq("spu_id",spuInfo.getId()));
        //????????????????????????
        spuInfoMapper.updateById(spuInfo);
        //??????
        this.saveList(spuInfo);
    }

    @Override
    public SkuInfo getSkuInfo(Long skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);

        //??????SkuId??????  skuAttrValueList
        List<SkuAttrValue> skuAttrValueList
                = skuAttrValueMapper.selectList(new QueryWrapper<SkuAttrValue>().eq("sku_id", skuId));
        skuInfo.setSkuAttrValueList(skuAttrValueList);
        if(!CollectionUtils.isEmpty(skuAttrValueList)){
            //?????? SkuAttrValue???attrName
            skuAttrValueList.forEach(skuAttrValue -> {
                //ValueName
                BaseAttrValue baseAttrValue =
                        baseAttrValueMapper.selectOne(new QueryWrapper<BaseAttrValue>().eq("id", skuAttrValue.getValueId()));
                skuAttrValue.setValueName(baseAttrValue.getValueName());
                BaseAttrInfo baseAttrInfo=
                        attrInfoMapper.selectOne(
                                new QueryWrapper<BaseAttrInfo>().eq("id",skuAttrValue.getAttrId()));
                skuAttrValue.setAttrName(baseAttrInfo.getAttrName());
            });
        }
        //??????skuSaleAttrValueListTemp
        List<SkuSaleAttrValue> saleAttrValueList =
                skuSaleAttrValueMapper.selectList(
                        new QueryWrapper<SkuSaleAttrValue>().eq("sku_id", skuId).eq("spu_id", skuInfo.getSpuId()));
        if (!CollectionUtils.isEmpty(saleAttrValueList)) {
            saleAttrValueList.forEach(skuSaleAttrValue -> {
                //????????????????????????
                SpuSaleAttrValue spuSaleAttrValue = spuSaleAttrValueMapper.selectById(skuSaleAttrValue.getSaleAttrValueId());
                //????????????????????????
                skuSaleAttrValue.setSaleAttrValueName(spuSaleAttrValue.getSaleAttrValueName());
                //?????????????????????
                skuSaleAttrValue.setSaleAttrName(spuSaleAttrValue.getSaleAttrName());
                //??????????????????ID
                skuSaleAttrValue.setBaseSaleAttrId(spuSaleAttrValue.getBaseSaleAttrId());
            });
        }
        skuInfo.setSkuSaleAttrValueList(saleAttrValueList);
        List<SkuImage> skuImageList = skuImageMapper.selectList(new QueryWrapper<SkuImage>().eq("sku_id",skuId ));
        skuInfo.setSkuImageList(skuImageList);
        return skuInfo;
    }

    @Override
    public void updateSkuInfo(SkuInfo skuInfo) {
        //?????????????????????
        skuImageMapper.delete(new QueryWrapper<SkuImage>().eq("sku_id",skuInfo.getId()));
        //???????????????????????????
        skuAttrValueMapper.delete(new QueryWrapper<SkuAttrValue>().eq("sku_id",skuInfo.getId()));
        //???????????????????????????
        skuSaleAttrValueMapper.delete(new QueryWrapper<SkuSaleAttrValue>().eq("sku_id",skuInfo.getId()));
        //??????????????????????????????
        skuInfoMapper.updateById(skuInfo);
        this.saveSkuList(skuInfo);
    }

    @Override
    public void cancelSaleSku(Long skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        if(!StringUtils.isEmpty(skuInfo)&&skuInfo.getIsSale()==1){
            //1 ??? 0???
            skuInfo.setIsSale(0);
            skuInfoMapper.updateById(skuInfo);
        }
    }

    @Override
    public void onSaleSku(Long skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        if(!StringUtils.isEmpty(skuInfo) && skuInfo.getIsSale()==0){
            skuInfo.setIsSale(1);
            //??????
            skuInfoMapper.updateById(skuInfo);
        }
    }

    @Override
    public SkuInfo getApiSpuInfo(Long skuId) {
        //??????uuid???lua?????????????????????
        SkuInfo skuInfo =null;
        try {
            //????????????
            //??????key???
            String skuKey= RedisConst.SKUKEY_PREFIX+skuId+RedisConst.SKUKEY_SUFFIX;
            //??????????????????
            skuInfo  = (SkuInfo) redisTemplate.opsForValue().get(skuKey);
            //???????????????????????????????????????????????????????????????????????????????????????????????????????????????
            if(StringUtils.isEmpty(skuInfo)){
                //?????????????????????????????????????????????????????????
                String lockKey=RedisConst.SKUKEY_PREFIX+skuId+RedisConst.SKULOCK_SUFFIX;
                //???????????????
                String uuid = UUID.randomUUID().toString().replace("-", "");
                Boolean isExist = redisTemplate.opsForValue().setIfAbsent(lockKey, uuid, RedisConst.SKUKEY_TIMEOUT, TimeUnit.SECONDS);
                if(isExist){
                    //?????????????????????
                    System.out.println("?????????????????????");
                    skuInfo=this.getSkuInfoDB(skuId);
                    //???????????????????????????????????????
                    if(StringUtils.isEmpty(skuInfo)){
                        skuInfo=new SkuInfo();
                        //???????????????????????????
                        redisTemplate.opsForValue().set(skuKey,skuInfo, RedisConst.SKUKEY_TEMPORARY_TIMEOUT,TimeUnit.SECONDS);
                        return skuInfo;
                    }
                    //???????????????????????????
                    redisTemplate.opsForValue().set(skuKey,skuInfo, RedisConst.SKUKEY_TEMPORARY_TIMEOUT,TimeUnit.SECONDS);
                    // ???????????????lua ????????????
                    String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                    // ??????lua???????????????????????????
                    DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
                    // ??????lua?????????????????????Long
                    redisScript.setResultType(Long.class);
                    redisScript.setScriptText(script);
                    // ??????key ???????????? value
                    redisTemplate.execute(redisScript, Arrays.asList(lockKey),uuid);
                    return skuInfo;
                }else {
                    //????????????
                    Thread.sleep(1000);
                    return this.getSkuInfo(skuId);
                }
            }else {
                //??????????????????
                return skuInfo;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        //??????????????????????????????????????????????????????????????????????????????
        return this.getSkuInfoDB(skuId);
    }
    @Override
    @GmallCache(prefix = RedisConst.SKUKEY_PREFIX)
    public SkuInfo getSkuInfoDB(Long skuId){
        SkuInfo skuInfo=skuInfoMapper.selectById(skuId);
        if(skuInfo==null){
            return null;
        }
        skuInfo.setSkuImageList(skuImageMapper.selectList(new QueryWrapper<SkuImage>().eq("sku_id",skuId)));
        return skuInfo;
    }

    @Override
    @GmallCache(prefix="getBaseCategoryList")
    public List<JSONObject> getBaseCategoryList() {
        /*
         * {
         *     "index": 1,
         *     "categoryChild": [
         *       {
         *         "categoryChild": [
         *           {
         *             "categoryName": "?????????", # ???????????????name
         *             "categoryId": 1
         *           }
         *           ...
         *         ],
         *         "categoryName": "????????????", #???????????????name
         *         "categoryId": 1
         *       },
         *      ...
         *     ],
         *     "categoryName": "??????????????????????????????", # ???????????????name
         *     "categoryId": 1
         *   },
         */
        // ????????????json ??????
        ArrayList<JSONObject> list = new ArrayList<>();
        //1.????????????
        List<BaseCategoryView> baseCategoryViews = baseCategoryViewMapper.selectList(null);
        //2.??????????????????????????????ma?????????<id,list> , Collectors.groupingBy??????1?????????id????????????
        Map<Long, List<BaseCategoryView>> category1Map  =
                baseCategoryViews.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory1Id));
        //3.??????iterator??????map??????
        Iterator<Map.Entry<Long, List<BaseCategoryView>>> iterator = category1Map.entrySet().iterator();
        int index=1;
        while(iterator.hasNext()){
            JSONObject category1 = new JSONObject();
            //key ?????? BaseCategoryView ???getCategory1Id ????????????id
            //value ?????? BaseCategoryView ?????????????????????
            Map.Entry<Long, List<BaseCategoryView>> entry = iterator.next();
            //  ????????????????????????Id
            Long category1Id = entry.getKey();
            List<BaseCategoryView> baseCategoryViews1=entry.getValue();
            String category1Name = baseCategoryViews1.get(0).getCategory1Name();
            //??????index
            category1.put("index",index);
            //??????????????????id
            category1.put("categoryId",category1Id);
            //????????????????????????
            category1.put("categoryName",category1Name);

            index++;
            //???????????????????????????????????????????????????map??????
            Map<Long, List<BaseCategoryView>> category2Map =
                    baseCategoryViews1.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory2Id));
            Iterator<Map.Entry<Long, List<BaseCategoryView>>> iterator1 = category2Map.entrySet().iterator();
            List<JSONObject>  category2List=new ArrayList<>();
            while(iterator1.hasNext()){
                JSONObject category2 = new JSONObject();
                Map.Entry<Long, List<BaseCategoryView>> entry1 = iterator1.next();
                Long category2Id = entry1.getKey();
                List<BaseCategoryView> baseCategoryViews2 = entry1.getValue();
                String category2Name=baseCategoryViews2.get(0).getCategory2Name();
                category2.put("categoryName",category2Name);
                category2.put("categoryId",category2Id);
                //?????????????????????????????????
                category2List.add(category2);
                List<JSONObject> category3List = new ArrayList<>();
                baseCategoryViews2.forEach(baseCategoryView -> {
                    JSONObject category3 = new JSONObject();
                    category3.put("categoryId",baseCategoryView.getCategory3Id());
                    category3.put("categoryName",baseCategoryView.getCategory3Name());
                    category3List.add(category3);
                });
                category2.put("categoryChild",category3List);
            }
            //???????????????
            category1.put("categoryChild",category2List);
            list.add(category1);
        }
        return list;
    }

    @Override
    public BaseTrademark getTrademark(Long tmId) {
        return baseTrademarkMapper.selectById(tmId);
    }

    @Override
    public BigDecimal getPrice(Long skuId) {
        //????????????????????????????????????

        //?????????
        RLock lock = redissonClient.getLock(skuId + ":lock");
        //??????
        lock.lock();
        SkuInfo skuInfo=null;
        BigDecimal price = new BigDecimal(0);
        try {
            QueryWrapper<SkuInfo> queryWrapper=new QueryWrapper<>();
            queryWrapper.select("price");
            queryWrapper.eq("id",skuId);
            skuInfo = skuInfoMapper.selectOne(queryWrapper);
            if(skuInfo!=null){
                price = skuInfo.getPrice();
            }
        }catch (Exception e){
            e.printStackTrace();;
        }finally {
            //??????
            lock.unlock();
        }


        if(skuInfo!=null){
            return skuInfo.getPrice();
        }
        return price;
    }

    @Override
    @GmallCache(prefix = "categoryViewByCategory3Id")
    public BaseCategoryView getBaseCategory(Long skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        Long category3Id = skuInfo.getCategory3Id();
        return baseCategoryViewMapper.selectById(category3Id);

    }

    @Override
    @GmallCache(prefix = "spuSaleAttrListCheckBySku:")
    public   List<SpuSaleAttr> getSpuSaleAttrValue(Long skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        return spuSaleAttrMapper.getSpuSaleAttrValue(skuInfo.getId(),skuInfo.getSpuId());

    }

    @Override
    @GmallCache(prefix = "BaseAttrInfoList:")
    public List<BaseAttrInfo> getBaseAttrInfoBySkuId(Long skuId) {
        return attrInfoMapper.getBaseAttrInfoBySkuId(skuId);
    }

    @Override
    @GmallCache(prefix = "saleAttrValuesBySpu:")
    public Map getSkuValueIdsMap(Long spuId) {
        Map<Object,Object>  map=new HashMap<>();
        // key = 125|123 ,value = 37
        List<Map> skuValueIdsMap = skuSaleAttrValueMapper.getSkuValueIdsMap(spuId);
        skuValueIdsMap.forEach(skuMap -> {
            map.put(skuMap.get("value_ids"),skuMap.get("sku_id"));
        });
        return map;
    }

    @Override
    public SkuInfo getApiSpuInfoRedisSeesion(Long skuId) {
        SkuInfo skuInfo = null;
        try {
            // ?????????????????????key-value
            // ??????key sku:skuId:info
            String skuKey = RedisConst.SKUKEY_PREFIX+skuId+RedisConst.SKUKEY_SUFFIX;
            // ???????????????????????? redis ????????????????????? ?????????????????????????????? ???????????????????????????
            // ??????????????????
            skuInfo = (SkuInfo) redisTemplate.opsForValue().get(skuKey);
            // ???????????????????????????????????????
            if (skuInfo==null){
                // ????????????????????????????????????????????????????????????????????????????????????????????????????????????
                // ????????????redisson
                // ????????????key sku:skuId:lock  set k1 v1 px 10000 nx
                String lockKey = RedisConst.SKUKEY_PREFIX+skuId+RedisConst.SKULOCK_SUFFIX;
                RLock lock = redissonClient.getLock(lockKey);
            /*
            ???????????? lock.lock();
            ?????????:  lock.lock(10,TimeUnit.SECONDS);
            ???????????? lock.tryLock(100,10,TimeUnit.SECONDS);
             */
                // ????????????
                boolean res = lock.tryLock(RedisConst.SKULOCK_EXPIRE_PX1, RedisConst.SKULOCK_EXPIRE_PX2, TimeUnit.SECONDS);
                if (res){
                    try {
                        // ?????????????????? ???????????????????????????
                        // ????????????????????????????????? {??????????????????????????????????????? = ??????????????????}
                        skuInfo = getSkuInfoDB(skuId);
                        // ???????????????????????????????????????
                        if (skuInfo==null){
                            // ???????????????????????? ?????????????????????????????????
                            SkuInfo skuInfo1 = new SkuInfo(); //???????????????
                            redisTemplate.opsForValue().set(skuKey,skuInfo1,RedisConst.SKUKEY_TEMPORARY_TIMEOUT,TimeUnit.SECONDS);
                            return skuInfo1;
                        }
                        // ?????????????????????????????????
                        redisTemplate.opsForValue().set(skuKey,skuInfo,RedisConst.SKUKEY_TIMEOUT,TimeUnit.SECONDS);

                        // ??????redis ?????????lua ???????????? ???????????????????????? lock.unlock
                        return skuInfo;

                    }catch (Exception e){
                        e.printStackTrace();
                    }finally {
                        // ?????????
                        lock.unlock();
                    }
                }else {
                    // ??????????????????
                    Thread.sleep(1000);
                    return getSkuInfo(skuId);
                }
            }else {

                return skuInfo;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // ??????????????????????????????????????????????????????
        return getSkuInfoDB(skuId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveSkuList(SkuInfo skuInfo){
        //??????Sku_image
        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        if(!CollectionUtils.isEmpty(skuImageList)){
            skuImageList.forEach(skuImage -> {
                skuImage.setSkuId(skuInfo.getId());
                skuImageMapper.insert(skuImage);
            });
        }
        //??????sku_attr_value
        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        if(!CollectionUtils.isEmpty(skuAttrValueList)){
            skuAttrValueList.forEach(skuAttrValue -> {
                skuAttrValue.setSkuId(skuInfo.getId());
                skuAttrValueMapper.insert(skuAttrValue);
            });
        }
        //??????sku_sale_attr_value
        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        if(!CollectionUtils.isEmpty(skuSaleAttrValueList)){
            skuSaleAttrValueList.forEach(skuSaleAttrValue -> {
                skuSaleAttrValue.setSpuId(skuInfo.getSpuId());
                skuSaleAttrValue.setSkuId(skuInfo.getId());
                skuSaleAttrValueMapper.insert(skuSaleAttrValue);
            });
        }
    }

    public SpuInfo packSpuInfo(SpuInfo spuInfo){
        Long spuId = spuInfo.getId();
        //????????????
        List<SpuImage> spuImageList = spuImageMapper.selectList(new QueryWrapper<SpuImage>().eq("spu_id", spuId));
        spuInfo.setSpuImageList(spuImageList);
        //????????????
        List<SpuPoster> spuPosterList = spuPosterMapper.selectList(new QueryWrapper<SpuPoster>().eq("spu_id", spuId));
        spuInfo.setSpuPosterList(spuPosterList);
        //??????????????????
        List<SpuSaleAttr> spuSaleAttrList = spuSaleAttrMapper.selectList(new QueryWrapper<SpuSaleAttr>().eq("spu_id", spuId));
        spuSaleAttrList.forEach((spuSaleAttr -> {
            Long id = spuSaleAttr.getId();
            List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttrValueMapper.selectList(new QueryWrapper<SpuSaleAttrValue>().eq("spu_id", spuId));
            spuSaleAttr.setSpuSaleAttrValueList(spuSaleAttrValueList);
        }));
        spuInfo.setSpuSaleAttrList(spuSaleAttrList);
        return spuInfo;
    }
    @Transactional(rollbackFor = Exception.class)
    public void saveList(SpuInfo spuInfo){
        //2.??????puImage ??????
        List<SpuImage> spuImageList = spuInfo.getSpuImageList();
        if(!CollectionUtils.isEmpty(spuImageList)){
            spuImageList.forEach(spuImage -> {
                //??????SpuId
                spuImage.setSpuId(spuInfo.getId());
                spuImageMapper.insert(spuImage);
            });
        }
        //3.??????puPoster ??????
        List<SpuPoster> spuPosterList = spuInfo.getSpuPosterList();
        if(!CollectionUtils.isEmpty(spuPosterList)){
            spuPosterList.forEach(spuPoster -> {
                //??????SpuId
                spuPoster.setSpuId(spuInfo.getId());
                spuPosterMapper.insert(spuPoster);
            });
        }
        //4.??????Spu??????
        List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();
        if(!CollectionUtils.isEmpty(spuSaleAttrList)){
            spuSaleAttrList.forEach(spuSaleAttr -> {
                //??????SpuId
                spuSaleAttr.setSpuId(spuInfo.getId());
                spuSaleAttrMapper.insert(spuSaleAttr);
                //4.1??????Spu?????????
                List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttr.getSpuSaleAttrValueList();
                if(!CollectionUtils.isEmpty(spuSaleAttrValueList)){
                    spuSaleAttrValueList.forEach(spuSaleAttrValue->{
                        //??????SpuId
                        spuSaleAttrValue.setSpuId(spuInfo.getId());
                        Long id= spuSaleAttrValue.getId();
                        //???spuSaleAttr??????
                        spuSaleAttrValue.setSaleAttrName(spuSaleAttr.getSaleAttrName());
                        List<SkuSaleAttrValue> skuSaleAttrValues = skuSaleAttrValueMapper.selectList(new QueryWrapper<SkuSaleAttrValue>().eq("spu_id", spuInfo.getId()));
                        skuSaleAttrValues.forEach(skuSaleAttrValue -> {

                            skuSaleAttrValue.setSaleAttrValueId(id);
                            skuSaleAttrValueMapper.updateById(skuSaleAttrValue);
                        });
                        spuSaleAttrValueMapper.insert(spuSaleAttrValue);
                    });
                }
            });
        }
    }
}
