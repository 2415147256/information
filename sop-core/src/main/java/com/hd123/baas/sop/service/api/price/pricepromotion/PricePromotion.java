package com.hd123.baas.sop.service.api.price.pricepromotion;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.hd123.baas.sop.service.api.TenantStandardEntity;
import com.hd123.baas.sop.service.api.price.PriceBill;
import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryFactors;
import com.hd123.rumba.commons.biz.query.QueryField;
import com.hd123.rumba.commons.biz.query.QueryOperation;

import lombok.Getter;
import lombok.Setter;

/**
 * @author zhengzewang on 2020/11/13.
 */
@Getter
@Setter
public class PricePromotion extends TenantStandardEntity implements PriceBill {

  public static final String FETCH_SHOP = "part_shop";
  public static final String FETCH_LINE = "part_line";
  public static final String[] FETCH_ALL = new String[] {
      FETCH_SHOP, FETCH_LINE };

  // 2 + 年月日 + 四位顺序
  private String flowNo;
  private Date effectiveStartDate;
  private Date effectiveEndDate;
  private PricePromotionState state;
  private BigDecimal ordLimitAmount;
  private BigDecimal ordLimitQty;
  private BigDecimal headSharingRate;
  private BigDecimal supervisorSharingRate;
  private String promotionTargets;
  private String note;
  private String orgId;
  /**
   * 促销类型
   */
  private PricePromotionType type;
  /**
   * 作废原因
   */
  private String reason;
  private boolean allShops;

  private List<PricePromotionLine> lines;
  private List<PricePromotionShop> shops = new ArrayList<>();

  @QueryEntity(PricePromotion.class)
  public static class Queries extends QueryFactors.StandardEntity {

    private static final String PREFIX = PricePromotion.class.getName() + "::";

    @QueryField
    public static final String TENANT = PREFIX + "tenant";
    @QueryField
    public static final String ORG_ID = PREFIX + "orgId";
    @QueryField
    public static final String FLOW_NO = PREFIX + "flowNo";
    @QueryField
    public static final String EFFECTIVE_START_DATE = PREFIX + "effectiveStartDate";
    @QueryField
    public static final String EFFECTIVE_END_DATE = PREFIX + "effectiveEndDate";
    @QueryField
    public static final String STATE = PREFIX + "state";
    @QueryOperation
    public static final String CREATOR_KEYWORD = "creatorKeyword";
    @QueryField
    public static final String ALL_SHOPS = PREFIX + "allShops";
    @QueryOperation
    public static final String SHOP_EQUALS = PREFIX + "shopEq";
    @QueryOperation
    public static final String SHOP_IN = PREFIX + "shopIn";
    @QueryOperation
    public static final String SKU_KEYWORD = PREFIX + "skuKeyword";
    @QueryOperation
    public static final String NOT_INT_SHOP_PRICE_PROMOTION = PREFIX + "notInShopPricePromotion";
    @QueryField
    public static final String TYPE = PREFIX + "type";
  }

}
