package com.hd123.baas.sop.remote.rsh6sop.storeprom;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @Author maodapeng
 * @Since
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StorePromAlcPrcBillDtl {
  /**
   * 商品规格ID
   */
  private String skuId;
  /** 商品标识 */
  private Integer gdGid;
  /** 商品分类 */
  private String sort;
}
