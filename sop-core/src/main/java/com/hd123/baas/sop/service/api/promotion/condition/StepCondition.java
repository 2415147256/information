package com.hd123.baas.sop.service.api.promotion.condition;

import com.hd123.baas.sop.service.api.promotion.execution.ExecutionSet;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel("阶梯条件")
public class StepCondition {
  @ApiModelProperty(value = "级别条件取值类型")
  private ValueType valueType = ValueType.amount;
  @ApiModelProperty(value = "级别条件运算符")
  private Operator operator = Operator.range;
  @ApiModelProperty(value = "取价类型，目前只支持actualPrice")
  private PriceType priceType = PriceType.actualPrice;
  private List<StepCase> stepCases = new ArrayList<>();

  @Data
  @ApiModel("阶梯优惠")
  public static class StepCase {
    @ApiModelProperty(value = "阶梯值")
    private BigDecimal value;
    @ApiModelProperty("优惠集合")
    private ExecutionSet executionSet;
  }

  public enum ValueType {
    @ApiModelProperty("数量")
    quantity,
    @ApiModelProperty("金额")
    amount,
    @ApiModelProperty("份数")
    portion,
  }

  public enum Operator {
    @ApiModelProperty("区间")
    range,
    @ApiModelProperty("整除")
    equal,
  }

  public enum PriceType {
    none,
    @ApiModelProperty("实际单价")
    actualPrice,
    @ApiModelProperty("零售价")
    retailPrice,
    @ApiModelProperty("会员价")
    memberPrice,
  }

}
