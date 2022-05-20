package com.hd123.baas.sop.service.impl.group;

import java.io.Serializable;

import com.hd123.baas.sop.service.api.entity.PUnv;
import com.hd123.baas.sop.service.api.entity.PriceRange;
import lombok.Getter;
import lombok.Setter;

/**
 * @author W.J.H.7
 * @since 1.0.0
 */
@Getter
@Setter
public class PriceGradeByRangeCal implements Serializable {
  private PriceRange priceRange;
  private PUnv firstPriceGrade;
  private PUnv secondPriceGrade;
}
