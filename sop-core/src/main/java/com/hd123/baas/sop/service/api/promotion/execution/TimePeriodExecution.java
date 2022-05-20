package com.hd123.baas.sop.service.api.promotion.execution;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@ApiModel(value = "时段促销优惠")
public class TimePeriodExecution {
  protected List<Item> items = new ArrayList<>();

  @Data
  @ApiModel("时段促销优惠Item")
  public static class Item {
    protected TimeRange period;
    @ApiModelProperty("计算方法")
    private GeneralExecution.Form form = GeneralExecution.Form.price;
    @ApiModelProperty("取值")
    private BigDecimal value;
  }

  @Data
  public static class TimeRange {
    private Date start;
    private Date finish;
  }
}
