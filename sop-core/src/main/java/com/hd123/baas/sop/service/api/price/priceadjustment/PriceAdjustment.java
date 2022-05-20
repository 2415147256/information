package com.hd123.baas.sop.service.api.price.priceadjustment;

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
 * @author zhengzewang on 2020/11/10.
 * 
 *         价格调整单
 * 
 */
@Getter
@Setter
public class PriceAdjustment extends TenantStandardEntity implements PriceBill {

  public static final int MIN_EFFECTIVE_DAYS = 2;

  private String orgId;

  // 9 + 年月日 + 四位顺序
  private String flowNo;
  // 生效开始时间
  private Date effectiveStartDate;
  // 状态
  private PriceAdjustmentState state = PriceAdjustmentState.INIT;
  /**
   * 状态变更原因
   */
  private String reason;

  private List<PriceAdjustmentLine> lines;

  @QueryEntity(PriceAdjustment.class)
  public static class Queries extends QueryFactors.StandardEntity {

    private static final String PREFIX = PriceAdjustment.class.getName() + "::";

    @QueryField
    public static final String TENANT = PREFIX + "tenant";
    @QueryField
    public static final String ORG_ID = PREFIX + "orgId";
    @QueryField
    public static final String FLOW_NO = PREFIX + "flowNo";
    @QueryField
    public static final String EFFECTIVE_START_DATE = PREFIX + "effectiveStartDate";
    @QueryField
    public static final String STATE = PREFIX + "state";
    @QueryOperation
    public static final String SKU_KEYWORD = PREFIX + "skuKeyword";
    @QueryOperation
    public static final String SKU_BASE_PRICE_IS_NULL = PREFIX + "skuBasePriceIsNull";
  }

}
