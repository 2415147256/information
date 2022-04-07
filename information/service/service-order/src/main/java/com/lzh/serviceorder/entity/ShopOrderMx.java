package com.lzh.serviceorder.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author lzh
 * @since 2022-04-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ShopOrderMx对象", description="")
public class ShopOrderMx implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "订单的id")
    @TableId(value = "id", type = IdType.ID_WORKER)
    private String id;

    @ApiModelProperty(value = "商品的名称")
    private String shopName;

    @ApiModelProperty(value = "商品的总价")
    private BigDecimal shopAllPrice;

    @ApiModelProperty(value = "店铺的名称")
    private String storeName;

    @ApiModelProperty(value = "商品的数量")
    private String shopNum;

    @ApiModelProperty(value = "商品的单价")
    private String shopPrice;

    @ApiModelProperty(value = "购买人的id")
    private String shopUserId;

    @ApiModelProperty(value = "是否支付  0表示未支付  1 表示已支付")
    private Boolean isPay;

    @ApiModelProperty(value = "商家的id")
    private String merchantId;


}
