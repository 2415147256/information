package com.hd123.baas.sop.fcf.controller.process;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author zhangweigang
 */
@ApiModel("制作商品")
@Getter
@Setter
public class ProcessPlanGoods extends PlanGoods {
  private static final long serialVersionUID = 525035493718580220L;
  @ApiModelProperty(value = "是否计划内", example = "true")
  private Boolean inPlan;
  @ApiModelProperty(value = "所属餐段id", example = "b87187b5-cced-4113-ab37-e4f59ee98442")
  private String mealTimeId;
  @ApiModelProperty(value = "商品制作状态", example = "todo(待处理)/confirmed(已确认)")
  private String state = "todo";

  public ProcessPlanGoods() {
  }

  public ProcessPlanGoods(String uuid, String skuId, BigDecimal lastWeekSale, String productName, BigDecimal suggestQty,
      BigDecimal qty, String state, Boolean inPlan, String mealTimeId) {
    super(uuid, skuId, lastWeekSale, productName, suggestQty, qty);
    this.state = state;
    this.inPlan = inPlan;
    this.mealTimeId = mealTimeId;
  }
}
