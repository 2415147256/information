package com.hd123.baas.sop.service.api.task;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.hd123.baas.sop.service.api.TenantEntity;
import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryFactors;
import com.hd123.rumba.commons.biz.query.QueryField;
import com.hd123.rumba.commons.biz.query.QueryOperation;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author maodapeng
 * @Since
 */
@Setter
@Getter
public class ShopTaskSummary extends TenantEntity {

  // 计划ID
  private String plan;
  private String planCode;
  private String planName;
  private Date planStartTime;
  private Date planEndTime;
  // 计划周期与计划一致 冗余字段
  private String planPeriodCode;
  private String planPeriod;

  private String shop;
  private String shopCode;
  private String shopName;

  private ShopTaskState state = ShopTaskState.UNFINISHED;

  private BigDecimal point = BigDecimal.ZERO;
  private BigDecimal score = BigDecimal.ZERO;

  private Date finishTime;

  private BigDecimal rank = BigDecimal.ZERO;

  // 门店任务
  private List<ShopTask> tasks = new ArrayList<>();

  private int finishedLogCount;

  private int logTotalCount;

  private String operatorId;
  private String operatorName;

  @QueryEntity(ShopTaskSummary.class)
  public static class Queries extends QueryFactors.Entity {
    private static final String PREFIX = ShopTaskSummary.class.getName() + "::";
    @QueryField
    public static final String TENANT = PREFIX + "tenant";
    @QueryField
    public static final String STATE = PREFIX + "state";
    @QueryField
    public static final String PLAN = PREFIX + "plan";
    @QueryField
    public static final String PLAN_CODE = PREFIX + "planCode";
    @QueryField
    public static final String PLAN_NAME = PREFIX + "planName";
    @QueryField
    public static final String PLAN_PERIOD_CODE = PREFIX + "planPeriodCode";
    @QueryField
    public static final String PLAN_END_TIME = PREFIX + "planEndTime";
    @QueryField
    public static final String SHOP = PREFIX + "shop";
    @QueryField
    public static final String SHOP_CODE = PREFIX + "shopCode";
    @QueryField
    public static final String SHOP_NAME = PREFIX + "shopName";
    @QueryField
    public static final String OPERATOR_ID = PREFIX + "operatorId";

    @QueryOperation
    public static final String SHOP_KEYWORD_LIKE = PREFIX + "shopKeyword like";

    @QueryOperation
    public static final String PLAN_KEYWORD_LIKE = PREFIX + "planKeyword like";

    @QueryOperation
    public static final String HANDLE_IN = PREFIX + "handler in";
    @QueryOperation
    public static final String PLAN_START_TIME_BTW = PREFIX + "planStartTime btw";
  }

}
