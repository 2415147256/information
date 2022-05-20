package com.hd123.baas.sop.service.api.explosivev2;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author shenmin
 */
@Getter
@Setter
public class LineLimitIncrInfo {
  private String skuId;
  private BigDecimal qty;
}
