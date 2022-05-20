package com.hd123.baas.sop.service.api.electricscale;

import com.hd123.baas.sop.service.api.TenantEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShopElecScaleTemplate extends TenantEntity {
  /**
   * 电子秤模板id
   */
  private String elecScaleTemplate;
  /**
   * 是否全部门店
   */
  private Boolean isAllShop;
  /**
   * 门店id
   */
  private String shop;
  /**
   * 门店code
   */
  private String shopName;
  /**
   * 门店名称
   */
  private String shopCode;
}
