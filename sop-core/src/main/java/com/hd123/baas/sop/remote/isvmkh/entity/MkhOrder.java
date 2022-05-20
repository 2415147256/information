package com.hd123.baas.sop.remote.isvmkh.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@ApiModel("明康汇订单主体信息")
public class MkhOrder {
  @ApiModelProperty("下单时间")
  private String buyTime;
  @ApiModelProperty("商品列表概览(json字符串，不含商品状态)")
  private String goodsListJson;
  @ApiModelProperty("订单编号")
  private long id;
  @ApiModelProperty("订单状态:1待付款，2待发货，3待取货，4已完成，取消订单（11超时未支付自动取消，12已支付用户主动取消）")
  private Integer orderState;
  @ApiModelProperty("实际支付金额")
  private BigDecimal payPrice;
  @ApiModelProperty("提货日期")
  private String takeDate;
  @ApiModelProperty("提货人姓名")
  private String takeName;
  @ApiModelProperty("提货人联系方式")
  private String takePhone;
}
