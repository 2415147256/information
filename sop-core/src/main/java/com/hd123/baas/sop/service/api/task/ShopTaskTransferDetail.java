package com.hd123.baas.sop.service.api.task;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author guyahui
 * @date 2021/5/21 11:06
 */
@Setter
@Getter
@ApiModel(value = "门店任务交接详情")
public class ShopTaskTransferDetail {

  @ApiModelProperty("门店任务详情")
  ShopTask shopTask;

  @ApiModelProperty("交接记录列表")
  List<ShopTaskTransfer> shopTaskTransferList;
}
