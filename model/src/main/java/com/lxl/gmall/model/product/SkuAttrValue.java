package com.lxl.gmall.model.product;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lxl.gmall.model.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * SkuAttrValue
 * </p>
 *
 */
@Data
@ApiModel(description = "Sku平台属性值")
@TableName("sku_attr_value")
public class SkuAttrValue extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "属性id（冗余)")
	@TableField("attr_id")
	private Long attrId;

	@ApiModelProperty(value = "属性值id")
	@TableField("value_id")
	private Long valueId;

	@ApiModelProperty(value = "skuid")
	@TableField("sku_id")
	private Long skuId;

	@ApiModelProperty(value = "平台属性名称")
	@TableField(exist = false)
	private String attrName;

	@ApiModelProperty(value = "平台属性值名称")
	@TableField(exist = false)
	private String valueName;

}

