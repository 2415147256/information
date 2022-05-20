package com.hd123.baas.sop.service.api.price.priceadjustment;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * @author zhengzewang on 2020/11/11.
 */
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class PriceGrade {

  private String id;
  private String name;
  private int seq;

}
