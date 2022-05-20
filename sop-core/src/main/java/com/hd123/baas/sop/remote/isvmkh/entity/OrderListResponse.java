package com.hd123.baas.sop.remote.isvmkh.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@ApiModel("明康汇查询订单结果")
public class OrderListResponse {
  @ApiModelProperty("当前页数")
  private int currPage;
  @ApiModelProperty("订单信息")
  private List<MkhOrder> list;
  @ApiModelProperty("每页记录数")
  private int pageSize;
  @ApiModelProperty("总记录数")
  private int totalCount;
  @ApiModelProperty("总页数")
  private int totalPage;
}
