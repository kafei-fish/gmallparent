package com.lxl.gmall.model.cart;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lxl.gmall.model.activity.CouponInfo;
import com.lxl.gmall.model.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Data
@ApiModel(description = "购物车")
@TableName("cart_info")
public class CartInfo extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "skuid")
    private Long skuId;

    @ApiModelProperty(value = "放入购物车时价格")
    private BigDecimal cartPrice;  // 1999

    @ApiModelProperty(value = "数量")
    private Integer skuNum;

    @ApiModelProperty(value = "图片文件")
    private String imgUrl;

    @ApiModelProperty(value = "sku名称 (冗余)")
    private String skuName;

    @ApiModelProperty(value = "isChecked")
    private Integer isChecked = 1;

    // 实时价格 skuInfo.price
    BigDecimal skuPrice;  // 端午  1888 | 提示 比加入时，降价了，还是涨价了

    //  优惠券信息列表
    @ApiModelProperty(value = "购物项对应的优惠券信息")
    @TableField(exist = false)
    private List<CouponInfo> couponInfoList;

}
