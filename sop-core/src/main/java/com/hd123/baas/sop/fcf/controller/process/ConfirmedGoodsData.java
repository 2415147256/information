package com.hd123.baas.sop.fcf.controller.process;

import com.hd123.baas.sop.fcf.service.api.process.FreshMealTime;
import com.hd123.rumba.commons.biz.entity.UCN;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ConfirmedGoodsData {
  @ApiModelProperty
  private List<ProcessPlanGoods> goods = new ArrayList<>();

  @ApiModelProperty
  private FreshMealTime mealTime;
  @ApiModelProperty
  private UCN categoryInfo;
}
