package com.hd123.baas.sop.fcf.controller.process;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangweigang
 */
@Getter
@Setter
@ApiModel("计划外商品数据")
public class BUnPlanGoodsData implements Serializable {
  private static final long serialVersionUID = 4187840278145250083L;
  @ApiModelProperty("计划外商品")
  private List<BUnPlanGoods> goods = new ArrayList<>();
}
