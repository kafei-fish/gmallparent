package com.lxl.gmall.web.controller;

import com.lxl.gmail.list.client.ListFeginClient;
import com.lxl.gmall.comon.util.result.Result;
import com.lxl.gmall.model.list.SearchParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author LiXiaoLong
 * @Date 2022/6/24 16:35
 * @PackageName:com.lxl.gmall.web.controller
 * @ClassName: ListController
 * @Description: TODO
 * @Version 1.0
 */
@Controller
public class ListController {
    /**
     * 远程调用接口
     */
    @Autowired
    private ListFeginClient listFeginClient;

    @GetMapping("list.html")
    public String  search(SearchParam searchParam, Model model){
        Result<Map> result  = listFeginClient.list(searchParam);
        model.addAllAttributes(result.getData());
        //拼接搜索对象url
        String urlParam=this.makeUrlParam(searchParam);
        String trademarkParam=this.makeTrademarkParam(searchParam.getTrademark());
        List<Map<String,String>> propsParamList=this.makePropsParamList(searchParam.getProps());
        Map<String,Object> orderMap=this.makeOrderMap(searchParam.getOrder());
        model.addAttribute("searchParam",searchParam);
        model.addAttribute("trademarkParam",trademarkParam);
        model.addAttribute("propsParamList",propsParamList);
        model.addAttribute("orderMap",orderMap);

        model.addAttribute("urlParam",urlParam);

        return "list/index";
    }

    /**
     * 排序处理
     * @param order
     * @return
     */
    private Map<String, Object> makeOrderMap(String order) {
        Map<String,Object> orderMap=new HashMap<>();
        if(!StringUtils.isEmpty(order)){
            String[] split = order.split(":");
            orderMap.put("type",split[0]);
            orderMap.put("sort",split[1]);
        }else {
            orderMap.put("type","0");
            orderMap.put("sort","asc");
        }
        return orderMap;
    }

    /**
     * 平台属性处理，因为拼图属实是很多的组合的，所以使用嵌套list集合来进行管理
     * @param props
     * @return
     */
    private List<Map<String, String>> makePropsParamList(String[] props) {
        //声明list集合
        List<Map<String,String>> propsParamList=new ArrayList<>();
        if(props!=null && props.length>0){
            for (String prop : props) {

                String[] split = prop.split(":");
                if(split!=null && split.length==3){
                    Map<String ,String > map=new HashMap<>();
                    map.put("attrId",split[0]);
                    map.put("attrValue",split[1]);
                    map.put("attrName",split[2]);
                    propsParamList.add(map);
                }
            }
        }
        return propsParamList;
    }

    /**
     * 面包屑处理
     * @param trademark
     * @return
     */
    private String makeTrademarkParam(String trademark) {
        if(!StringUtils.isEmpty(trademark)){
            String[] split = trademark.split(":");
            if(split!=null && split.length==2){
                return "品牌"+split[1];
            }
        }
        return "";
    }

    /**
     * 拼接urlParam
     * @param searchParam  searchParam
     * @return urlParam
     */
    private String makeUrlParam(SearchParam searchParam) {
        StringBuffer urlParam=new StringBuffer();
        //拼接keyword
        if (!StringUtils.isEmpty(searchParam.getKeyword())){
            //如果不为空就进行拼接
            urlParam.append("keyword=").append(searchParam.getKeyword());
        }
        //拼接分类
        if(!StringUtils.isEmpty(searchParam.getCategory3Id())){
            urlParam.append("category3Id=").append(searchParam.getCategory3Id());
        }
        if(!StringUtils.isEmpty(searchParam.getCategory2Id())){
            urlParam.append("category2Id=").append(searchParam.getCategory2Id());
        }
        if(!StringUtils.isEmpty(searchParam.getCategory1Id())){
            urlParam.append("category1Id=").append(searchParam.getCategory1Id());
        }
        //拼接品牌
        if(!StringUtils.isEmpty(searchParam.getTrademark())){
            //这个是用来判断是用户是否已经进入搜索界面
            if(urlParam.length()>0){
                urlParam.append("&trademark").append(searchParam.getTrademark());
            }
        }
        //拼接属性
        if(!StringUtils.isEmpty(searchParam.getProps())){
            for (String prop : searchParam.getProps()) {
                //这个也是进行判断是否进入列表界面，如果没有就是那么url的参数就会为0
                if(urlParam.length()>0){
                    urlParam.append("&props=").append(prop);
                }
            }
        }
        return "list.html?"+urlParam.toString();
    }
}
