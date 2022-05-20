package com.hd123.baas.sop.service.api.price.priceadjustment;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * @author zhengzewang on 2020/11/11.
 * 
 *         对应价格级加价规则
 * 
 */
@Getter
@Setter
public class PriceIncreaseRule {

  private List<PriceGrade> grades;
  private String value;

}
