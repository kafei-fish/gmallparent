package com.lxl.gmall.list.repository;

import com.lxl.gmall.model.list.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @Author LiXiaoLong
 * @Date 2022/6/21 19:54
 * @PackageName:com.lxl.gmall.list.repository
 * @ClassName: GoodsRepository
 * @Description: TODO
 * @Version 1.0
 */
public interface GoodsRepository  extends ElasticsearchRepository<Goods,Long> {
}
