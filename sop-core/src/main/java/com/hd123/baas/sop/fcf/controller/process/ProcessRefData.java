package com.hd123.baas.sop.fcf.controller.process;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author zhangweigang
 */
@Getter
@Setter
@ApiModel("智能制作参考信息")
public class ProcessRefData {

  @ApiModelProperty(value = "门店gid", example = "100068")
  private Integer storeGid;

  @ApiModelProperty(value = "商品gid", example = "3005149")
  private String gid;

  @ApiModelProperty(value = "商品规格", example = "1")
  private BigDecimal qpc;

  @ApiModelProperty(value = "餐段uuid", example = "5db633f6-14e5-472b-b238-123c29e2687a")
  private String mealTimeId;

  @ApiModelProperty(value = "建议制作数量", example = "12")
  private BigDecimal qty;

  @ApiModelProperty(value = "上周同期销量", example = "100")
  private BigDecimal lastWeekSale;
}
