package com.hd123.baas.sop.service.api.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SkuPositionAssoc {
  /**
   * uuid
   */
  private int uuid;
  /**
   * 租户id
   */
  private String tenant;
  /**
   * 商品定位id
   */
  private int skuPositionId;
  /**
   * 商品ID
   */
  private String skuId;
}
