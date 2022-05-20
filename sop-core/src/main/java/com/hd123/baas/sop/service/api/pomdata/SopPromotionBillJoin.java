package com.hd123.baas.sop.service.api.pomdata;

import com.hd123.spms.service.bill.PromotionBillJoin;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SopPromotionBillJoin extends PromotionBillJoin {
  private String storeTag;
}
