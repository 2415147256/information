package com.hd123.baas.sop.fcf.service.api.process;

import com.hd123.rumba.commons.biz.entity.Entity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author zhangweigang
 */
@Getter
@Setter
public class ProcessPlanOrderLine extends Entity {

  private static final long serialVersionUID = -3026998818324969497L;
  @ApiModelProperty(value = "租户", example = "fcf")
  private String tenant;
  @ApiModelProperty(value = "序号", example = "1")
  private String lineNo;
  @ApiModelProperty(value = "所属计划的uuid", example = "a8cf4fa9-0e4c-4b03-950e-69ea56f1df44")
  private String ownerUuid;
  @ApiModelProperty(value = "规格商品主键", example = "20210331160659123")
  private String skuId;
  @ApiModelProperty(value = "商品名称", example = "咸肉粽")
  private String productName;
  @ApiModelProperty(value = "是否计划内商品,1->是,0->false", example = "1")
  private boolean inPlan;
  @ApiModelProperty(value = "建议数", example = "12")
  private BigDecimal suggestQty;
  @ApiModelProperty(value = "实际数", example = "12")
  private BigDecimal qty;
  @ApiModelProperty(value = "所属类目uuid", example = "a8cf4fa9-0e4c-4b03-950e-69ea56f1df44")
  private String categoryUuid;
  @ApiModelProperty(value = "所属类目代码", example = "45321")
  private String categoryCode;
  @ApiModelProperty(value = "所属类目名称", example = "包子")
  private String categoryName;
  @ApiModelProperty(value = "所属餐段uuid", example = "a8cf4fa9-0e4c-4b03-950e-69ea56f1df44")
  private String mealTimeUuid;
  @ApiModelProperty(value = "上周同期销量", example = "12")
  private BigDecimal lastWeekSale;
  @ApiModelProperty(value = "状态，是否完成 执行过制作即是完成 可选 todo，confirmed", example = "todo")
  private String state;
}
