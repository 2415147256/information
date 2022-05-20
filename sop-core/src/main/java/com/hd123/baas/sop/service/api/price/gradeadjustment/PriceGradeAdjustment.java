package com.hd123.baas.sop.service.api.price.gradeadjustment;

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
 * @author zhengzewang on 2020/11/10.
 * 
 *         价格级调整单
 * 
 */
@Getter
@Setter
public class PriceGradeAdjustment extends TenantStandardEntity implements PriceBill {

  public static final String FETCH_SHOP = "part_shop";
  public static final String[] FETCH_ALL = new String[] {
      FETCH_SHOP };
  private String orgId;

  // 1 + 年月日 + 四位顺序
  private String flowNo;
  // 生效开始时间
  private Date effectiveStartDate;
  // 状态
  private PriceGradeAdjustmentState state;
  /**
   * 状态变更原因
   */
  private String reason;

  private List<PriceGradeAdjustmentShop> shops = new ArrayList<>();
  private List<PriceGradeAdjustmentLine> lines = new ArrayList<>();

  @QueryEntity(PriceGradeAdjustment.class)
  public static class Queries extends QueryFactors.StandardEntity {

    private static final String PREFIX = PriceGradeAdjustment.class.getName() + "::";

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
    public static final String SHOP_EQUALS = PREFIX + "shopEq";
    @QueryOperation
    public static final String SHOP_IN = PREFIX + "shopIn";
    @QueryOperation
    public static final String NOT_INT_SHOP_PRICE_GRADE = PREFIX + "notInShopPriceGrade";
  }

}
