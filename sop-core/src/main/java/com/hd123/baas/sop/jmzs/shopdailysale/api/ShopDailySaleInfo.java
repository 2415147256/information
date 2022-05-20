package com.hd123.baas.sop.jmzs.shopdailysale.api;

import com.hd123.baas.sop.service.api.TenantStandardEntity;
import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryFactorName;
import com.hd123.rumba.commons.biz.query.QueryFactors;
import com.hd123.rumba.commons.biz.query.QueryField;
import com.hd123.rumba.commons.biz.query.QueryOperation;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Setter
@Getter
public class ShopDailySaleInfo extends TenantStandardEntity {
  /**
   * 组织Id
   */
  private String orgId;
  /**
   * 编号
   */
  private int code;

  /**
   * 门店代码
   */
  private String shopCode;
  /**
   * 门店名称
   */
  private String shopName;
  /**
   * 门店ID
   */
  private String shopId;

  /**
   * 时间
   */
  private Date dailySaleDate;

  /**
   * 金额
   */
  private BigDecimal amount;
  /**
   * 状态
   */
  private ShopDailySaleInfoState state = ShopDailySaleInfoState.INIT;
  /**
   * 持有人
   */
  private ShopDailySaleInfoHolder holder = ShopDailySaleInfoHolder.JM;

  /**
   * 持有人Id
   */
  private String holderId;

  /**
   * 持有人code
   */
  private String holderCode;

  /**
   * 持有人名称
   */
  private String holderName;

  /**
   * 明细
   */
  private List<ShopDailySaleInfoLine> lines = new ArrayList<>();


  @QueryEntity(ShopDailySaleInfo.class)
  public static class Queries extends QueryFactors.StandardEntity {
    private static final QueryFactorName PREFIX = QueryFactorName.prefix(ShopDailySaleInfo.class);
    @QueryField
    public static final String TENANT = PREFIX.nameOf("tenant");
    @QueryField
    public static final String ORG_ID = PREFIX.nameOf("orgId");
    @QueryField
    public static final String DAILY_SALE_DATE = PREFIX.nameOf("dailySaleDate");
    @QueryField
    public static final String amount = PREFIX.nameOf("amount");
    @QueryField
    public static final String CODE = PREFIX.nameOf("code");
    @QueryField
    public static final String SHOP_CODE = PREFIX.nameOf("shopCode");
    @QueryField
    public static final String SHOP_ID = PREFIX.nameOf("shopId");
    @QueryField
    public static final String SHOP_NAME = PREFIX.nameOf("shopName");

    @QueryOperation
    public static final String SHOP_KEYWORD_LIKE = PREFIX.nameOf("shopKeyword like");
    @QueryOperation
    public static final String AMT_BTW = PREFIX.nameOf("amt btw");

  }
}
