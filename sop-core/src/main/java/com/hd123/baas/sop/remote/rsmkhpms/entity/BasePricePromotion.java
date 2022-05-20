package com.hd123.baas.sop.remote.rsmkhpms.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.hd123.rumba.commons.biz.entity.StandardEntity;
import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryFactors;
import com.hd123.rumba.commons.biz.query.QueryField;
import com.hd123.rumba.commons.biz.query.QueryOperation;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BasePricePromotion extends StandardEntity {
  public static final String FETCH_LINES = "fetch_lines";
  public static final String FETCH_SHOPS = "fetch_shops";

  public static final String[] FETCH_ALL = new String[] {
      FETCH_LINES, FETCH_SHOPS };

  private String tenant;
  /**
   * 模板名称
   */
  private String name;
  /**
   * 模板说明
   */
  private String remark;

  /**
   * 促销类型/模板
   */
  private BasePricePromotionCode code;

  /**
   * 流水号
   */
  private String flowNo;
  /**
   * 开始生效日期
   */
  private Date effectiveStartDate;
  /**
   * 结束生效日期
   */
  private Date effectiveEndDate;
  /**
   * 状态
   */
  private String state;

  /**
   * 是否全部门店
   */
  private boolean allShop;

  /**
   * 审核日期
   */
  private Date auditTime;

  /**
   * 说明 作废原因等
   */
  private String reason;
  /**
   * 督导承担比例
   */
  private BigDecimal supervisorSharingRate;

  /**
   * 总部承担比例
   */
  private BigDecimal headSharingRate;

  /**
   * 最小起订额
   */
  private BigDecimal orderLimitAmount;
  /**
   * 最小起订量
   */
  private BigDecimal orderLimitQty;

  /**
   * 相关门店信息
   */
  List<BasePricePromotionShop> shops;

  List<BasePricePromotionLine> lines;

  @QueryEntity(BasePricePromotion.class)
  public static class Queries extends QueryFactors.StandardEntity {
    private static final String PREFIX = BasePricePromotion.class.getSimpleName() + "::";
    @QueryField
    public static final String TENANT = PREFIX + "tenant";
    @QueryField
    public static final String NAME = PREFIX + "name";
    @QueryField
    public static final String FLOW_NO = PREFIX + "flowNo";
    @QueryField
    public static final String STATE = PREFIX + "state";
    @QueryOperation
    public static final String SHOP_KEY_WORD = PREFIX + "shopKeyWord";
    @QueryOperation
    public static final String SKU_KEY_WORD = PREFIX + "skuKeyWord";
    @QueryOperation
    public static final String CREATOR_KEY_WORD = PREFIX + "creatorKeyword";

  }
}
