package com.hd123.baas.sop.fcf.service.api.process;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author zhangweigang
 */
@Getter
@Setter
@ApiModel("各餐段状态")
public class ProcessPlanGoodsSummaryState implements Serializable {
  private static final long serialVersionUID = -5627203648460275490L;
  @ApiModelProperty("指向餐段的主键")
  private String mealTimeUuid;

  @ApiModelProperty(value = "已完成件数", example = "2")
  private BigDecimal confirmedQty;

  @ApiModelProperty(value = "总件数 = 已完成的实际件数+未完成的建议件数。当state为finished时，confirmedQty==totalQty", example = "9")
  private BigDecimal totalQty;

  @ApiModelProperty(value = "该餐段下此分类完成状态", example = "todo(待处理)/finished(已完成)/noGoods(没有商品)/canNotProcess(不可制作)")
  private String state = "todo";

}
