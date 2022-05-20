package com.hd123.baas.sop.fcf.controller.unfreeze;

import java.util.ArrayList;
import java.util.List;

import com.hd123.baas.sop.fcf.controller.BaseAppRequest;

import com.hd123.baas.sop.fcf.service.api.unfreeze.UnFreezeGoods;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author zhangweigang
 */
@Getter
@Setter
@ApiModel
public class UnFreezeGoodsRequest extends BaseAppRequest {
  private static final long serialVersionUID = 3518535664734759195L;
  @ApiModelProperty(value = "门店代码", example = "444")
  private String shopCode;

  @ApiModelProperty(value = "解冻计划主键", example = "b78465d9-f38e-41eb-b862-feb5f9c69992")
  private String planUuid;

  @ApiModelProperty("解冻的商品")
  private List<UnFreezeGoods> lines = new ArrayList<>();
}
