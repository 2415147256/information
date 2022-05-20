package com.hd123.baas.sop.service.api.price.temppriceadjustment;

import java.util.Date;

import com.hd123.baas.sop.service.api.TenantStandardEntity;
import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryFactors;
import com.hd123.rumba.commons.biz.query.QueryField;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author maodapeng
 * @Since
 */
@Setter
@Getter
public class TempPriceAdjustment extends TenantStandardEntity {
  private String orgId;
  private String flowNo;
  private TempPriceAdjustmentState state = TempPriceAdjustmentState.INIT;
  private Date effectiveStartDate;
  private String reason;

  // 不作存储
  private long shopCount;
  private TempShop shop;

  // 商品数量
  private long count;

  @QueryEntity(TempPriceAdjustment.class)
  public static class Queries extends QueryFactors.StandardEntity {

    private static final String PREFIX = TempPriceAdjustment.class.getName() + "::";

    @QueryField
    public static final String TENANT = PREFIX + "tenant";
    @QueryField
    public static final String ORG_ID = PREFIX + "orgId";
    @QueryField
    public static final String STATE = PREFIX + "state";
  }
}
