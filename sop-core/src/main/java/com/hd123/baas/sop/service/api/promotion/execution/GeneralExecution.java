package com.hd123.baas.sop.service.api.promotion.execution;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value = "一般促销优惠", description = "其中提供了最常用的促销价、折扣率以及折扣额。")
public class GeneralExecution {
  @ApiModelProperty("计算方法")
  private Form form = Form.price;
  @ApiModelProperty("取值")
  private BigDecimal value;

  @ApiModelProperty(value = "计算的促销价精度。：BigDecimal.setScale。", notes = "定义了priceScale之后，表示先用商品价格×折扣率得出（符合设置精度的）促销价，再用促销价计算优惠。")
  private BigDecimal priceScale;
  @ApiModelProperty("计算的促销价精度舍入方式[四舍五入 / 去尾 / 进一]")
  private String priceRoundingMode;

  /**
   * 计算方法
   *
   * @author lxm
   */
  public enum Form {
    /** 促销价 */
    price,
    /** 促销总价 */
    totalPrice,
    /** 零售价折扣率 */
    retailPriceDiscount,
    /** 零售价折扣额 */
    retailPriceAmount,
    /** 会员价折扣率 */
    memberPriceDiscount,
    /** 会员价折扣额 */
    memberPriceAmount,
    // 基于非会员优惠后金额的折扣率
    nonMemberDiscount,
    // 基于非会员优惠后金额的折扣额
    nonMemberAmount,
    /** 折扣率 */
    discount,
    /** 折扣额 */
    amount,
    /** 赠送金额 参加任务单：http://jira.app.hd123.cn/jira/browse/JPOS-16298 */
    freeAmount,
    /** 条件价格，配合MultiProductCondition使用 */
    conditionPrice,
  }
}
