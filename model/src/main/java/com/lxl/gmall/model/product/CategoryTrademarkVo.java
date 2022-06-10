//
//
package com.lxl.gmall.model.product;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * <p>
 * BaseCategoryView
 * </p>
 *
 */
@Data
public class CategoryTrademarkVo {

	@ApiModelProperty(value = "三级分类编号")
	private Long category3Id;

	@ApiModelProperty(value = "品牌id")
	private List<Long> trademarkIdList;



}

