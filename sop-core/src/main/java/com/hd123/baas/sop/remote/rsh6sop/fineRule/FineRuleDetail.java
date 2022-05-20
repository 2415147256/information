package com.hd123.baas.sop.remote.rsh6sop.fineRule;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author maodapeng
 * @Since
 */
@Setter
@Getter
public class FineRuleDetail {
  //逾期天数
  private int overdueDay;
  //截止时间(hh24:mi)
  private String endTime;
  //罚款金额
  private BigDecimal total;
}
