package com.hd123.baas.sop.remote.rsmkhpms.entity;

import com.hd123.rumba.commons.biz.entity.Entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BasePricePromotionShop extends Entity {
  private String tenant;
  private String owner;
  private String shopId;
  private String shopCode;
  private String shopName;
  private boolean allShop;
}
