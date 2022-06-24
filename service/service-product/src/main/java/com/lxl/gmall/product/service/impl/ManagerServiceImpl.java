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
     * 获取一级分类
     * @return 一级分类列表
     */
    @Override
    public List<BaseCategory1> getCategory1() {

        return  baseCategory1Mapper.selectList(null);
    }

    /**
     *
     * @param category1Id 一级分类Id
     * @return 二级分类列表
     */
    @Override
    public List<BaseCategory2> getCategory2(Long category1Id) {
        return baseCategory2Mapper.selectList(new QueryWrapper<BaseCategory2>().eq("category1_id", category1Id));
    }

    /**
     *
     * @param category2Id 二级分类Id
     * @return 三级分类列表
     */
    @Override
    public List<BaseCategory3> getCategory3(Long category2Id) {
        return baseCategory3Mapper.selectList(new QueryWrapper<BaseCategory3>().eq("category2_id",category2Id));
    }

    /**
     * 通过分类id获取平台属性集合
     * @param category1Id 一级分类
     * @param category2Id 二级分类
     * @param category3Id 案件分类
     * @return 平台属性数据集合
     */
    @Override
    public List<BaseAttrInfo> getAttrInfoList(Long category1Id, Long category2Id, Long category3Id) {
       List<BaseAttrInfo> attrInfoList= attrInfoMapper.getByCategoryIdFindAttrInfoList(category1Id,category2Id,category3Id);
        return attrInfoList;
    }

    /**
     * 根据平台属性Id 获取到平台属性值集合
     * @param attrId 平台属性Id
     * @return 平台数据集合
     */
    @Override
    public  List<BaseAttrValue> getAttrValueList(Long attrId) {
        List<BaseAttrValue> baseAttrValueList = baseAttrValueMapper.selectList(new QueryWrapper<BaseAttrValue>().eq("attr_id", attrId));

        return baseAttrValueList;

    }

    /**
     * 添加或者修改商品属性
     * @param baseAttrInfo 商品属性
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdateAttrInfo(BaseAttrInfo baseAttrInfo) {


        if(baseAttrInfo.getId()==null){
            //添加
            attrInfoMapper.insert(baseAttrInfo);
        }else {
            //修改
            attrInfoMapper.updateById(baseAttrInfo);
            //添加属性值策略，首先将属性值全部删除，然后在将拿到的属性值全部添加
            QueryWrapper<BaseAttrValue> baseAttrValueQueryWrapper=new QueryWrapper<>();
            baseAttrValueQueryWrapper.eq("attr_id",baseAttrInfo.getId());
            baseAttrValueMapper.delete(baseAttrValueQueryWrapper);
        }
        //获取BaseAttrVlaue的集合，变量集合进行添加
        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
        attrValueList.forEach((baseAttrValue)->{
            //  base_attr_value.attr_id 页面提交的时候，不会携带这个字段数据.
            baseAttrValue.setAttrId(baseAttrInfo.getId());
            baseAttrValueMapper.insert(baseAttrValue);
        });

    }

    /**
     * 根据平台属性Id，找到平台属性以及平台属性的集合
     * @param attrId 平台属性Id
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
     * @param pagePram 分页条件参数
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
     * 通过3级分类Id查询BaseTrademark集合
     * @param category3Id 3级分类ID
     * @return
     */
    @Override
    public List<BaseTrademark> findTrademarkList(Long category3Id) {
        //首先通过category3Id 查询出tm_id
        List<BaseCategoryTrademark> baseCategoryTrademarkList =
                baseCategoryTrademarkMapper.
                        selectList(new QueryWrapper<BaseCategoryTrademark>().eq("category3_id", category3Id));

        //使用foreach循环
//        List<BaseTrademark> baseTrademarksList=new ArrayList<>();
//        if(baseCategoryTrademarkList!=null){
//            for (BaseCategoryTrademark baseCategoryTrademark : baseCategoryTrademarkList) {
//                BaseTrademark baseTrademark = baseTrademarkMapper.selectById(baseCategoryTrademark.getId());
//                baseTrademarksList.add(baseTrademark);
//            }
//        }
        //使用Stream流获取tradMarkId
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
            throw new GmallException("不能为空",20001);
        }
    }

    /**
     * 根据ID修改数据
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
     * 通过ID获取数据
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
     * 删除
     * @param id
     */
    @Override
    public void removeById(Long id) {
        baseTrademarkMapper.deleteById(id);
    }

    @Override
    public List<BaseTrademark> getCurrentTrademarkList(Long category3Id) {
        //首先通过category3Id 查询出tm_id
        List<BaseCategoryTrademark> baseCategoryTrademarkList =
                baseCategoryTrademarkMapper.
                        selectList(new QueryWrapper<BaseCategoryTrademark>().eq("category3_id", category3Id));
        if(!CollectionUtils.isEmpty(baseCategoryTrademarkList)){
            List<Long> trademarkIds = baseCategoryTrademarkList.stream().map((baseCategoryTrademark -> {
                return baseCategoryTrademark.getTrademarkId();
            })).collect(Collectors.toList());

            //查询不包含得
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
        //两种方式
        //第一种
        List<Long> trademarkIdList = categoryTrademarkVo.getTrademarkIdList();
//        if(!CollectionUtils.isEmpty(trademarkIdList)){
//            trademarkIdList.forEach((ids)->{
//                BaseCategoryTrademark baseCategoryTrademark =new BaseCategoryTrademark();
//                baseCategoryTrademark.setCategory3Id(categoryTrademarkVo.getCategory3Id());
//                baseCategoryTrademark.setTrademarkId(ids);
//                baseCategoryTrademarkMapper.insert(baseCategoryTrademark);
//            });
         //使用Stream流
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
     * 添加SpuInfo
     * @param spuInfo
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveSpuInfo(SpuInfo spuInfo) {
        //1.保存SpuInfo
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
         * 需要保存的表
         * 1.sku_info
         * 2.sku_image
         * 3.sku_attr_value
         * 4.sku_sale_attr_value
         */
        //保存Sku_info
        skuInfoMapper.insert(skuInfo);

        this.saveSkuList(skuInfo);
        //添加布隆过滤
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
        //查询图片列表
        List<SpuImage> spuImageList = spuImageMapper.selectList(new QueryWrapper<SpuImage>().eq("spu_id", spuInfo.getId()));
        spuInfo.setSpuImageList(spuImageList);
        //查询海报列表
        List<SpuPoster> posterList = spuPosterMapper.selectList(new QueryWrapper<SpuPoster>().eq("spu_id", spuInfo.getId()));
        spuInfo.setSpuPosterList(posterList);
        //销售属性
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
     * 更新Spuinfo
     * @param spuInfo
     */
    @Override
    public void updateSpuInfo(SpuInfo spuInfo) {


       spuImageMapper.delete(new QueryWrapper<SpuImage>().eq("spu_id",spuInfo.getId()));
       spuPosterMapper.delete(new QueryWrapper<SpuPoster>().eq("spu_id",spuInfo.getId()));

        //销售属性也是 把销售属性 与销售属性值删除之后在添加
        List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();
        spuSaleAttrMapper.delete(new QueryWrapper<SpuSaleAttr>().eq("spu_id",spuInfo.getId()));
        spuSaleAttrValueMapper.delete(new QueryWrapper<SpuSaleAttrValue>().eq("spu_id",spuInfo.getId()));
        //删除完毕进行更新
        spuInfoMapper.updateById(spuInfo);
        //添加
        this.saveList(spuInfo);
    }

    @Override
    public SkuInfo getSkuInfo(Long skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);

        //根据SkuId查询  skuAttrValueList
        List<SkuAttrValue> skuAttrValueList
                = skuAttrValueMapper.selectList(new QueryWrapper<SkuAttrValue>().eq("sku_id", skuId));
        skuInfo.setSkuAttrValueList(skuAttrValueList);
        if(!CollectionUtils.isEmpty(skuAttrValueList)){
            //补全 SkuAttrValue的attrName
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
        //查询skuSaleAttrValueListTemp
        List<SkuSaleAttrValue> saleAttrValueList =
                skuSaleAttrValueMapper.selectList(
                        new QueryWrapper<SkuSaleAttrValue>().eq("sku_id", skuId).eq("spu_id", skuInfo.getSpuId()));
        if (!CollectionUtils.isEmpty(saleAttrValueList)) {
            saleAttrValueList.forEach(skuSaleAttrValue -> {
                //查询销售属性值表
                SpuSaleAttrValue spuSaleAttrValue = spuSaleAttrValueMapper.selectById(skuSaleAttrValue.getSaleAttrValueId());
                //设置销售属性值名
                skuSaleAttrValue.setSaleAttrValueName(spuSaleAttrValue.getSaleAttrValueName());
                //设置销售属性名
                skuSaleAttrValue.setSaleAttrName(spuSaleAttrValue.getSaleAttrName());
                //设置销售属性ID
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
        //首先是删除图片
        skuImageMapper.delete(new QueryWrapper<SkuImage>().eq("sku_id",skuInfo.getId()));
        //删除平台属性关联表
        skuAttrValueMapper.delete(new QueryWrapper<SkuAttrValue>().eq("sku_id",skuInfo.getId()));
        //删除销售属性关联表
        skuSaleAttrValueMapper.delete(new QueryWrapper<SkuSaleAttrValue>().eq("sku_id",skuInfo.getId()));
        //完成删除直接进行添加
        skuInfoMapper.updateById(skuInfo);
        this.saveSkuList(skuInfo);
    }

    @Override
    public void cancelSaleSku(Long skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        if(!StringUtils.isEmpty(skuInfo)&&skuInfo.getIsSale()==1){
            //1 是 0否
            skuInfo.setIsSale(0);
            skuInfoMapper.updateById(skuInfo);
        }
    }

    @Override
    public void onSaleSku(Long skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        if(!StringUtils.isEmpty(skuInfo) && skuInfo.getIsSale()==0){
            skuInfo.setIsSale(1);
            //上架
            skuInfoMapper.updateById(skuInfo);
        }
    }

    @Override
    public SkuInfo getApiSpuInfo(Long skuId) {
        //使用uuid与lua脚本来是是实现
        SkuInfo skuInfo =null;
        try {
            //缓存数据
            //定义key和
            String skuKey= RedisConst.SKUKEY_PREFIX+skuId+RedisConst.SKUKEY_SUFFIX;
            //获取缓存数据
            skuInfo  = (SkuInfo) redisTemplate.opsForValue().get(skuKey);
            //如果从缓存中读取的数据未空，那么就查询数据库，查询直接再将数据放入到缓存中
            if(StringUtils.isEmpty(skuInfo)){
                //在这个位置会发生缓存击穿，所以要加上锁
                String lockKey=RedisConst.SKUKEY_PREFIX+skuId+RedisConst.SKULOCK_SUFFIX;
                //定义锁的值
                String uuid = UUID.randomUUID().toString().replace("-", "");
                Boolean isExist = redisTemplate.opsForValue().setIfAbsent(lockKey, uuid, RedisConst.SKUKEY_TIMEOUT, TimeUnit.SECONDS);
                if(isExist){
                    //获取到分布式锁
                    System.out.println("获取到分布式锁");
                    skuInfo=this.getSkuInfoDB(skuId);
                    //如果从数据库获取的数据是空
                    if(StringUtils.isEmpty(skuInfo)){
                        skuInfo=new SkuInfo();
                        //将空数据放入缓存中
                        redisTemplate.opsForValue().set(skuKey,skuInfo, RedisConst.SKUKEY_TEMPORARY_TIMEOUT,TimeUnit.SECONDS);
                        return skuInfo;
                    }
                    //将空数据放入缓存中
                    redisTemplate.opsForValue().set(skuKey,skuInfo, RedisConst.SKUKEY_TEMPORARY_TIMEOUT,TimeUnit.SECONDS);
                    // 解锁：使用lua 脚本解锁
                    String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                    // 设置lua脚本返回的数据类型
                    DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
                    // 设置lua脚本返回类型为Long
                    redisScript.setResultType(Long.class);
                    redisScript.setScriptText(script);
                    // 删除key 所对应的 value
                    redisTemplate.execute(redisScript, Arrays.asList(lockKey),uuid);
                    return skuInfo;
                }else {
                    //尝试调用
                    Thread.sleep(1000);
                    return this.getSkuInfo(skuId);
                }
            }else {
                //缓存中有数据
                return skuInfo;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        //如果发生缓存在执行异常，会执行改方法，直接查询数据库
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
         *             "categoryName": "电子书", # 三级分类的name
         *             "categoryId": 1
         *           }
         *           ...
         *         ],
         *         "categoryName": "电子书刊", #二级分类的name
         *         "categoryId": 1
         *       },
         *      ...
         *     ],
         *     "categoryName": "图书、音像、电子书刊", # 一级分类的name
         *     "categoryId": 1
         *   },
         */
        // 声明几个json 集合
        ArrayList<JSONObject> list = new ArrayList<>();
        //1.查询全部
        List<BaseCategoryView> baseCategoryViews = baseCategoryViewMapper.selectList(null);
        //2.通过拉姆达表达式转为ma集合，<id,list> , Collectors.groupingBy根据1级分类id进行排序
        Map<Long, List<BaseCategoryView>> category1Map  =
                baseCategoryViews.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory1Id));
        //3.使用iterator遍历map集合
        Iterator<Map.Entry<Long, List<BaseCategoryView>>> iterator = category1Map.entrySet().iterator();
        int index=1;
        while(iterator.hasNext()){
            JSONObject category1 = new JSONObject();
            //key 就是 BaseCategoryView 的getCategory1Id 一级分类id
            //value 就是 BaseCategoryView 对应的二级分类
            Map.Entry<Long, List<BaseCategoryView>> entry = iterator.next();
            //  获取到了一级分类Id
            Long category1Id = entry.getKey();
            List<BaseCategoryView> baseCategoryViews1=entry.getValue();
            String category1Name = baseCategoryViews1.get(0).getCategory1Name();
            //设置index
            category1.put("index",index);
            //设置一级分类id
            category1.put("categoryId",category1Id);
            //设置一级分类名称
            category1.put("categoryName",category1Name);

            index++;
            //通过一级分类我们继续遍历二级分类的map集合
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
                //将二级分类添加到集合中
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
            //设置子节点
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
        //不需要将价格放入到缓存中

        //准备锁
        RLock lock = redissonClient.getLock(skuId + ":lock");
        //上锁
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
            //解锁
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
            // 缓存存储数据：key-value
            // 定义key sku:skuId:info
            String skuKey = RedisConst.SKUKEY_PREFIX+skuId+RedisConst.SKUKEY_SUFFIX;
            // 获取里面的数据？ redis 有五种数据类型 那么我们存储商品详情 使用哪种数据类型？
            // 获取缓存数据
            skuInfo = (SkuInfo) redisTemplate.opsForValue().get(skuKey);
            // 如果从缓存中获取的数据是空
            if (skuInfo==null){
                // 直接获取数据库中的数据，可能会造成缓存击穿。所以在这个位置，应该添加锁。
                // 第二种：redisson
                // 定义锁的key sku:skuId:lock  set k1 v1 px 10000 nx
                String lockKey = RedisConst.SKUKEY_PREFIX+skuId+RedisConst.SKULOCK_SUFFIX;
                RLock lock = redissonClient.getLock(lockKey);
            /*
            第一种： lock.lock();
            第二种:  lock.lock(10,TimeUnit.SECONDS);
            第三种： lock.tryLock(100,10,TimeUnit.SECONDS);
             */
                // 尝试加锁
                boolean res = lock.tryLock(RedisConst.SKULOCK_EXPIRE_PX1, RedisConst.SKULOCK_EXPIRE_PX2, TimeUnit.SECONDS);
                if (res){
                    try {
                        // 处理业务逻辑 获取数据库中的数据
                        // 真正获取数据库中的数据 {数据库中到底有没有这个数据 = 防止缓存穿透}
                        skuInfo = getSkuInfoDB(skuId);
                        // 从数据库中获取的数据就是空
                        if (skuInfo==null){
                            // 为了避免缓存穿透 应该给空的对象放入缓存
                            SkuInfo skuInfo1 = new SkuInfo(); //对象的地址
                            redisTemplate.opsForValue().set(skuKey,skuInfo1,RedisConst.SKUKEY_TEMPORARY_TIMEOUT,TimeUnit.SECONDS);
                            return skuInfo1;
                        }
                        // 查询数据库的时候，有值
                        redisTemplate.opsForValue().set(skuKey,skuInfo,RedisConst.SKUKEY_TIMEOUT,TimeUnit.SECONDS);

                        // 使用redis 用的是lua 脚本删除 ，但是现在用么？ lock.unlock
                        return skuInfo;

                    }catch (Exception e){
                        e.printStackTrace();
                    }finally {
                        // 解锁：
                        lock.unlock();
                    }
                }else {
                    // 其他线程等待
                    Thread.sleep(1000);
                    return getSkuInfo(skuId);
                }
            }else {

                return skuInfo;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 为了防止缓存宕机：从数据库中获取数据
        return getSkuInfoDB(skuId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveSkuList(SkuInfo skuInfo){
        //保存Sku_image
        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        if(!CollectionUtils.isEmpty(skuImageList)){
            skuImageList.forEach(skuImage -> {
                skuImage.setSkuId(skuInfo.getId());
                skuImageMapper.insert(skuImage);
            });
        }
        //保存sku_attr_value
        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        if(!CollectionUtils.isEmpty(skuAttrValueList)){
            skuAttrValueList.forEach(skuAttrValue -> {
                skuAttrValue.setSkuId(skuInfo.getId());
                skuAttrValueMapper.insert(skuAttrValue);
            });
        }
        //保存sku_sale_attr_value
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
        //查询图片
        List<SpuImage> spuImageList = spuImageMapper.selectList(new QueryWrapper<SpuImage>().eq("spu_id", spuId));
        spuInfo.setSpuImageList(spuImageList);
        //查询海报
        List<SpuPoster> spuPosterList = spuPosterMapper.selectList(new QueryWrapper<SpuPoster>().eq("spu_id", spuId));
        spuInfo.setSpuPosterList(spuPosterList);
        //查询销售属性
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
        //2.添加puImage 图片
        List<SpuImage> spuImageList = spuInfo.getSpuImageList();
        if(!CollectionUtils.isEmpty(spuImageList)){
            spuImageList.forEach(spuImage -> {
                //设置SpuId
                spuImage.setSpuId(spuInfo.getId());
                spuImageMapper.insert(spuImage);
            });
        }
        //3.添加puPoster 海报
        List<SpuPoster> spuPosterList = spuInfo.getSpuPosterList();
        if(!CollectionUtils.isEmpty(spuPosterList)){
            spuPosterList.forEach(spuPoster -> {
                //设置SpuId
                spuPoster.setSpuId(spuInfo.getId());
                spuPosterMapper.insert(spuPoster);
            });
        }
        //4.添加Spu属性
        List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();
        if(!CollectionUtils.isEmpty(spuSaleAttrList)){
            spuSaleAttrList.forEach(spuSaleAttr -> {
                //设置SpuId
                spuSaleAttr.setSpuId(spuInfo.getId());
                spuSaleAttrMapper.insert(spuSaleAttr);
                //4.1添加Spu属性值
                List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttr.getSpuSaleAttrValueList();
                if(!CollectionUtils.isEmpty(spuSaleAttrValueList)){
                    spuSaleAttrValueList.forEach(spuSaleAttrValue->{
                        //设置SpuId
                        spuSaleAttrValue.setSpuId(spuInfo.getId());
                        Long id= spuSaleAttrValue.getId();
                        //将spuSaleAttr放入
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
