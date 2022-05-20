package com.hd123.baas.sop.service.api.price.config;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

/**
 * @author zhengzewang on 2020/11/17.
 */
@Getter
@Setter
public class BaseConfigParam {

  // 容差值，10表示10%
  private BigDecimal toleranceValue;
  // K值
  private BigDecimal kv;
  // B值
  private BigDecimal bv;
  // 后台加价率，10表示10%
  private BigDecimal increaseRate;
  // 是否计算尾差
  private Boolean calcTailDiff;

}
