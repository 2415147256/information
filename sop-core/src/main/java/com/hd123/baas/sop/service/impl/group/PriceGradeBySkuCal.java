package com.hd123.baas.sop.service.impl.group;

import com.hd123.baas.sop.service.api.entity.PUnv;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author maodapeng
 * @Since
 */
@Getter
@Setter
public class PriceGradeBySkuCal {
  private String skuId;
  private PUnv firstPriceGrade;
  private PUnv secondPriceGrade;
}
