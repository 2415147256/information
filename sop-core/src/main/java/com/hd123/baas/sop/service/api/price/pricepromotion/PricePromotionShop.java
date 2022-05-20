package com.hd123.baas.sop.service.api.price.pricepromotion;

import com.hd123.baas.sop.service.api.TenantEntity;
import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryFactors;
import com.hd123.rumba.commons.biz.query.QueryField;

import lombok.Getter;
import lombok.Setter;

/**
 * @author zhengzewang on 2020/11/13.
 * 
 *         关联门店
 * 
 */
@Getter
@Setter
public class PricePromotionShop extends TenantEntity {

  private String owner;
  private String shop;
  private String shopCode;
  private String shopName;

  @QueryEntity(PricePromotionShop.class)
  public static class Queries extends QueryFactors.Entity {

    private static final String PREFIX = PricePromotionShop.class.getName() + "::";

    @QueryField
    public static final String TENANT = PREFIX + "tenant";
    @QueryField
    public static final String OWNER = PREFIX + "owner";

  }

}
