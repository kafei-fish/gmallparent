package com.lxl.gmall.model.product;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lxl.gmall.model.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * BaseSaleAttr
 * </p>
 *
 */
@Data
@ApiModel(description = "销售属性")
@TableName("base_sale_attr")
public class BaseSaleAttr extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "销售属性名称")
	@TableField("name")
	private String name;

}

