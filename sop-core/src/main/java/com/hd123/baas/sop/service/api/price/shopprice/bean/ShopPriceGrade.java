package com.hd123.baas.sop.service.api.price.shopprice.bean;

import com.hd123.baas.sop.service.api.TenantEntity;
import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryFactors;
import com.hd123.rumba.commons.biz.query.QueryField;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author zhengzewang on 2020/11/12.
 * 
 *         门店价格级。用于记录当前生效的门店价格级
 * 
 */
@Getter
@Setter
public class ShopPriceGrade extends TenantEntity {

  // 组织ID
  private String orgId;
  // 门店
  private String shop;
  // 类别
  private String skuGroup;
  // 定位
  private String skuPosition;
  // 价格级
  private String priceGrade;
  // 来源单据
  private String source;
  // 来源单据创建时间
  private Date sourceCreateTime;

  // 以下不持久化
  private String skuGroupName;
  private String skuPositionName;
  private String priceGradeName;

  @QueryEntity(ShopPriceGrade.class)
  public static class Queries extends QueryFactors.Entity {

    private static final String PREFIX = ShopPriceGrade.class.getName() + "::";

    @QueryField
    public static final String TENANT = PREFIX + "tenant";
    @QueryField
    public static final String ORG_ID = PREFIX + "orgId";
    @QueryField
    public static final String SHOP = PREFIX + "shop";
    @QueryField
    public static final String SOURCE_CREATE_TIME = PREFIX + "sourceCreateTime";
    @QueryField
    public static final String SKU_POSITION = PREFIX + "skuPosition";
    @QueryField
    public static final String SKU_GROUP = PREFIX + "skuGroup";
    @QueryField
    public static final String PRICE_GRADE = PREFIX + "priceGrade";

  }

}
