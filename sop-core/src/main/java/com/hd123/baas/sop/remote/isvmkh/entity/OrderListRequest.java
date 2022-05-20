package com.hd123.baas.sop.remote.isvmkh.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel("明康汇查询订单请求参数")
public class OrderListRequest {
  @ApiModelProperty(value = "当前页码", required = true)
  private int current;
  @ApiModelProperty(value = "每页条数，不传默认20")
  private int size;
  @ApiModelProperty("订单状态: 不传查全部。1=待付款，2=待发货，3=待核销，4=已完成，11=超时未支付自动取消，12=已支付用户主动取消")
  private int orderState;
  @ApiModelProperty("提货人手机号")
  private String phone;
  @ApiModelProperty(value = "门店编码", required = true)
  private String shopCode;
  @ApiModelProperty(value = "提货日期", required = true)
  private String takeDate;
}
