package com.lxl.gmall.list.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.lxl.gmall.comon.util.result.Result;
import com.lxl.gmall.list.repository.GoodsRepository;
import com.lxl.gmall.list.service.SearchService;
import com.lxl.gmall.model.list.*;
import com.lxl.gmall.model.product.BaseAttrInfo;
import com.lxl.gmall.model.product.BaseCategoryView;
import com.lxl.gmall.model.product.BaseTrademark;
import com.lxl.gmall.model.product.SkuInfo;
import com.lxl.gmall.product.client.ProductFeignClient;
import jdk.nashorn.internal.parser.JSONParser;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author LiXiaoLong
 * @Date 2022/6/21 19:44
 * @PackageName:com.lxl.gmall.list.service.impl
 * @ClassName: SearchServiceImpl
 * @Description: TODO
 * @Version 1.0
 */
@Service
public class SearchServiceImpl implements SearchService {
    @Resource
    private GoodsRepository goodsRepository;
    @Resource
    private RedisTemplate redisTemplate;

    //es?????????
    @Resource
    private RestHighLevelClient restHighLevelClient;
    @Resource
    private ProductFeignClient productFeignClient;

    @Override
    public void incrHotScore(Long skuId) {
        String hotKey="hoyKey";
        Double hotScore  = redisTemplate.opsForZSet().incrementScore(hotKey, "skuId:" + skuId, 1);
        if(hotScore%10==0){
            //??????skuid
            Optional<Goods> optionalGoods = goodsRepository.findById(skuId);
            Goods goods = optionalGoods.get();
            goods.setHotScore(Math.round(hotScore));
            goodsRepository.save(goods);
        }
    }

    @Override
    public void upperGoods(Long skuId) {
        //??????????????????
        Goods goods=new Goods();
        //??????skuInfo
        SkuInfo skuInfo = productFeignClient.getSpuInfo(skuId);

        //????????????Id
        BaseCategoryView baseCategory = productFeignClient.getBaseCategory(skuId);
        goods.setId(skuInfo.getId());
        //????????????Id
        goods.setCategory1Id(baseCategory.getCategory1Id());
        goods.setCategory1Name(baseCategory.getCategory1Name());
        goods.setCategory2Id(baseCategory.getCategory2Id());
        goods.setCategory2Name(baseCategory.getCategory2Name());
        goods.setCategory3Id(baseCategory.getCategory3Id());
        goods.setCategory3Name(baseCategory.getCategory3Name());

        //??????skuInfp
        goods.setTitle(skuInfo.getSkuName());
        goods.setDefaultImg(skuInfo.getSkuDefaultImg());
        //????????????
        goods.setPrice(productFeignClient.getPrice(skuId).doubleValue()); // ??????????????????
        //????????????
        goods.setCreateTime(new Date());

        //????????????
        BaseTrademark trademark = productFeignClient.getTrademark(skuInfo.getTmId());

        goods.setTmId(trademark.getId());
        goods.setTmName(trademark.getTmName());
        goods.setTmLogoUrl(trademark.getLogoUrl());
        //??????????????????
        List<BaseAttrInfo> baseAttrInfoList = productFeignClient.getBaseAttrInfo(skuId);
        List<SearchAttr> collect = baseAttrInfoList.stream().map(baseAttrInfo -> {
            SearchAttr searchAttr = new SearchAttr();
            searchAttr.setAttrId(baseAttrInfo.getId());
            searchAttr.setAttrName(baseAttrInfo.getAttrName());
            searchAttr.setAttrValue(baseAttrInfo.getAttrValueList().get(0).getValueName());
            return searchAttr;
        }).collect(Collectors.toList());
        goods.setAttrs(collect);
        goodsRepository.save(goods);
    }

    @Override
    public void lowerGoods(Long skuId) {
        goodsRepository.deleteById(skuId);
    }

    /**
     * es????????????
     * @param searchParam
     * @return
     */
    @Override
    public SearchResponseVo search(SearchParam searchParam) throws IOException {
        // ??????dsl??????
        SearchRequest searchRequest=this.buildQueryDsl(searchParam);
        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        //????????????
        SearchResponseVo responseVo=this.parseSearchResult(response);
        long totalPages = (responseVo.getTotal()+searchParam.getPageSize()-1)/searchParam.getPageSize();
        responseVo.setTotalPages(totalPages);
        responseVo.setPageNo(searchParam.getPageNo());
        responseVo.setPageSize(searchParam.getPageSize());
        return responseVo;
    }

    /**
     * ????????????
     * @param response
     * @return
     */
    private SearchResponseVo parseSearchResult(SearchResponse response) {
        //????????????????????????????????????????????????SearchResponseVo??????
        SearchHits searchHits = response.getHits();
        SearchHit[] hits = searchHits.getHits();
        SearchResponseVo searchResponseVo=new SearchResponseVo();
        //????????????
        //???????????? ?????????????????????????????????
        List<Goods> goodsList=new ArrayList<>();
        for (SearchHit hit : hits) {
            Goods goods = JSONObject.parseObject(hit.getSourceAsString(), Goods.class);
            //????????????
            if(hit.getHighlightFields().get("title")!=null){
                Text title = hit.getHighlightFields().get("title").getFragments()[0];
                goods.setTitle(title.toString());
            }
            goodsList.add(goods);
        }
        //???GoodsList????????????searchResponseVo
        searchResponseVo.setGoodsList(goodsList);
        //????????????
        /*
         * //?????? ??????vo????????????id??????????????????????????? name?????????????????? value: [{id:100,name:??????,logo:xxx},{id:101,name:??????,log:yyy}]
         *     private List<SearchResponseTmVo> trademarkList;
         */
        //??????????????????
        Map<String, Aggregation> stringAggregationMap = response.getAggregations().asMap();
        //?????????????????????????????????
        ParsedLongTerms tmIdAgg = (ParsedLongTerms) stringAggregationMap.get("tmIdAgg");
        //??????
        List<SearchResponseTmVo> trademarkList  = tmIdAgg.getBuckets().stream().map(bucket -> {
            SearchResponseTmVo trademark = new SearchResponseTmVo();
            trademark.setTmId(Long.parseLong(bucket.getKeyAsString()));
            //??????????????????
            Map<String, Aggregation> tmIdSubMap = bucket.getAggregations().asMap();
            ParsedStringTerms tmNameAgg = (ParsedStringTerms) tmIdSubMap.get("tmNameAgg");
            String tmName = tmNameAgg.getBuckets().get(0).getKeyAsString();
            trademark.setTmName(tmName);
            //????????????url
            ParsedStringTerms tmLogoUrlAgg = (ParsedStringTerms) tmIdSubMap.get("tmLogoUrlAgg");
            String tmLogoUrl = tmLogoUrlAgg.getBuckets().get(0).getKeyAsString();
            trademark.setTmLogoUrl(tmLogoUrl);
            return trademark;
        }).collect(Collectors.toList());
        searchResponseVo.setTrademarkList(trademarkList);
        //????????????????????????
        ParsedNested attrAgg = (ParsedNested ) stringAggregationMap.get("attrAgg");
        ParsedLongTerms  attrIdAgg = attrAgg.getAggregations().get("attrIdAgg");
        List<? extends Terms.Bucket> buckets = attrIdAgg.getBuckets();
        if(!CollectionUtils.isEmpty(buckets)){
            List<SearchResponseAttrVo> searchResponseAttrVOS  = buckets.stream().map(bucket -> {

                SearchResponseAttrVo searchResponseAttrVo = new SearchResponseAttrVo();
                //??????????????????Id
                searchResponseAttrVo.setAttrId(((Terms.Bucket) bucket).getKeyAsNumber().longValue());
                //????????????
                //??????value
                ParsedStringTerms attrNameAgg = bucket.getAggregations().get("attrNameAgg");
                List<? extends Terms.Bucket> bucketsAttrName = attrNameAgg.getBuckets();
                searchResponseAttrVo.setAttrName(bucketsAttrName.get(0).getKeyAsString());
                //??????????????????
                ParsedStringTerms attrValueAgg = bucket.getAggregations().get("attrValueAgg");
                List<? extends Terms.Bucket> buckets1 = attrValueAgg.getBuckets();
                List<String> values = buckets1.stream().map(Terms.Bucket::getKeyAsString).collect(Collectors.toList());
                searchResponseAttrVo.setAttrValueList(values);
                return searchResponseAttrVo;
            }).collect(Collectors.toList());
            searchResponseVo.setAttrsList(searchResponseAttrVOS);
            //??????????????????
            searchResponseVo.setTotal(searchHits.getTotalHits().value);
        }
        //????????????????????????
        //??????????????????
        return searchResponseVo;

    }

    /**
     *  ??????dsl????????????
     * @param searchParam
     * @return
     */
    private SearchRequest buildQueryDsl(SearchParam searchParam) {
        //???????????????
        SearchSourceBuilder searchSourceBuilder=new SearchSourceBuilder();
        //bool
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        if(!StringUtils.isEmpty(searchParam.getKeyword())){
            boolQuery.must(
                    QueryBuilders.
                            matchQuery("title",searchParam.getKeyword()).
                            operator(Operator.AND));
            //  ???????????? { highlight }
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("title");
            highlightBuilder.preTags("<span style=color:red>");
            highlightBuilder.postTags("</span>");
            searchSourceBuilder.highlighter(highlightBuilder);
        }
        if(!StringUtils.isEmpty(searchParam.getCategory3Id())){
            boolQuery.
                    filter(QueryBuilders.
                            termQuery("category3Id",searchParam.getCategory3Id()));
        }
        if(!StringUtils.isEmpty(searchParam.getCategory2Id())){
            boolQuery.
                    filter(QueryBuilders.
                            termQuery("category2Id",searchParam.getCategory2Id()));
        }
        if(!StringUtils.isEmpty(searchParam.getCategory1Id())){
            boolQuery.
                    filter(QueryBuilders.
                            termQuery("category1Id",searchParam.getCategory1Id()));
        }
        if(!StringUtils.isEmpty(searchParam.getTrademark())){
            String[] split = searchParam.getTrademark().split(":");
            if(split!=null && split.length==2){
                // "tmId": "2" 2:??????
                boolQuery.filter(QueryBuilders.termQuery("tmId",split[0]));
            }
        }
        //??????????????????
        String[] props = searchParam.getProps();
        if(props!=null && props.length>0){
            for (String prop : props) {
                String[] split = prop.split(":");
                if(split!=null && split.length==3){
                    //??????dsl??????
                    BoolQueryBuilder boolQueryBuilder  = QueryBuilders.boolQuery();
                    BoolQueryBuilder subBoolBuilder = QueryBuilders.boolQuery();
                    subBoolBuilder.must(QueryBuilders.termQuery("attrs.attrId",split[0]));
                    subBoolBuilder.must(QueryBuilders.termQuery("attrs.attrValue",split[1]));
                    boolQueryBuilder.must(QueryBuilders.nestedQuery("attrs",subBoolBuilder, ScoreMode.None));
                    boolQuery.filter(boolQueryBuilder);
                }
            }
        }
        //{query}
        searchSourceBuilder.query(boolQuery);
        //??????  "from": 0, "size": 20,
        searchSourceBuilder.from((searchParam.getPageNo()-1)*searchParam.getPageSize());
        searchSourceBuilder.size(searchParam.getPageSize());
        //?????? "sort":    //  ????????????????????? order=2:desc ???????????? order=2:asc  ???????????? order=1:desc  ???????????? order=1:asc
        String order = searchParam.getOrder();
        String[] orders = order.split(":");
        if(orders!=null && orders.length==20){
            String  field=null;
            switch (orders[0]){
                case "0":
                    field="hotScore";
                    break;
                case "1":
                    field="price";
                    break;

            }
            searchSourceBuilder.sort(field,"asc".equals(orders[0])? SortOrder.ASC:SortOrder.DESC);
        }else {
            searchSourceBuilder.sort("hotScore",SortOrder.DESC);
        }
        //????????????
        searchSourceBuilder.aggregation(
                AggregationBuilders.terms("tmIdAgg").field("tmId").
                        subAggregation(AggregationBuilders.terms("tmNameAgg").field("tmName")).
                        subAggregation(AggregationBuilders.terms("tmLogoUrlAgg").field("tmLogoUrl"))
        );
        //??????????????????
        searchSourceBuilder.aggregation(
                AggregationBuilders.nested("attrAgg","attrs").
                        subAggregation(AggregationBuilders.terms("attrIdAgg").field("attrs.attrId").
                        subAggregation(AggregationBuilders.terms("attrNameAgg").field("attrs.attrName")).
                        subAggregation(AggregationBuilders.terms("attrValueAgg").field("attrs.attrValue"))
                        )
        );
        //  ??????????????????  GET /goods/_search
        SearchRequest searchRequest=new SearchRequest("goods");
        //  ????????????????????? id???defaultImg,title,price,createTime ??????????????????????????????: Goods
        //  ??????,????????????????????????????????? ???????????????????????????Goods ?????????????????????
        searchSourceBuilder.fetchSource(new String []{"id","defaultImg","title","price","createTime"},null);
        searchRequest.source(searchSourceBuilder);
        //  dsl ????????????????????? searchSourceBuilder ??????
        System.out.println("dsl:\t"+searchSourceBuilder.toString());



        return searchRequest;
    }
}
