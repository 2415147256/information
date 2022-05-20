package com.hd123.baas.sop.service.api.pms.rule;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hd123.baas.sop.service.api.pms.activity.PromActivityType;
import com.hd123.baas.sop.service.api.promotion.FavorSharing;
import com.hd123.baas.sop.service.api.promotion.PromFieldControl;
import com.hd123.baas.sop.service.api.promotion.Promotion;
import com.hd123.baas.sop.service.api.promotion.PromotionJoinUnits;
import com.hd123.baas.sop.service.api.promotion.condition.DateRangeCondition;
import com.hd123.baas.sop.service.api.promotion.condition.TimePeriodCondition;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.entity.StandardEntity;
import com.hd123.rumba.commons.biz.entity.UCN;
import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryFactors;
import com.hd123.rumba.commons.biz.query.QueryField;
import com.hd123.rumba.commons.biz.query.QueryOperation;
import com.qianfan123.baas.common.util.Converter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@ApiModel("促销规则")
@EqualsAndHashCode(callSuper = true)
public class PromRule extends StandardEntity {
  public static final String DEFAULT_STARTER_ORG_UUID = "-";

  public static final String FILTER_KEYWORD_LIKES = "keyword:%=%";
  public static final String FILTER_DATE_RANGE_BETWEEN = "dateRange:[,]";
  public static final String FILTER_FRONT_STATE_EQUAL = "frontState:=";
  public static final String FILTER_ACTIVITY_UUID_EQUAL = "activityUuid:=";
  public static final String FILTER_JOIN_UNIT_UUID_EQUAL = "joinUnitUuid:=";
  public static final String FILTER_ORG_ID_IN = "orgIdIn";

  public static final String FRONT_STATE_INITIAL = "initial";
  public static final String FRONT_STATE_EFFECT = "effect";
  public static final String FRONT_STATE_EXPIRED = "expired";
  public static final String FRONT_STATE_STOPPED = "stopped";
  public static final String FRONT_STATE_EXPIRED_AND_STOPPED = "expired&stopped";

  public static final String PART_JOIN_UNITS = "joinUnits";
  public static final String PART_PROMOTION = "promotion";
  public static final String PART_FAVOR_SHARINGS_PARTS = "favorSharings";
  public static final String[] ALL_PARTS = new String[]{
          PART_JOIN_UNITS, PART_FAVOR_SHARINGS_PARTS, PART_PROMOTION
  };
  @ApiModelProperty("租户")
  private String tenant;
  @ApiModelProperty("组织ID")
  private String orgId;
  @ApiModelProperty("发起组织id")
  private String starterOrgUuid;
  @ApiModelProperty("名称")
  private String name;
  @ApiModelProperty("状态")
  private State state;
  @ApiModelProperty("前端状态")
  private String frontState;
  @ApiModelProperty("规则代码")
  private String billNumber;
  @ApiModelProperty("促销模板")
  private UCN template;
  @ApiModelProperty("所属活动")
  private UCN activity;
  @ApiModelProperty("所属活动类型")
  private PromActivityType activityType;
  @ApiModelProperty("适用门店")
  private PromotionJoinUnits joinUnits;
  @ApiModelProperty("会员专享")
  private Boolean onlyMember;
  @ApiModelProperty("促销渠道")
  private List<String> promChannels;

  @ApiModelProperty("促销日期")
  private DateRangeCondition dateRangeCondition;
  @ApiModelProperty("时段促销")
  private TimePeriodCondition timePeriodCondition;
  @ApiModelProperty("促销内容")
  private Promotion promotion;
  @ApiModelProperty("促销说明")
  private String promNote;

  @ApiModelProperty("终止信息")
  private OperateInfo stopInfo;

  @ApiModelProperty("促销费用承担")
  private List<FavorSharing> favorSharings;
  @ApiModelProperty("字段控制")
  private Map<String, PromFieldControl> fieldControls = new HashMap<>();

  @ApiModelProperty("促销原因")
  private String marketReason;


  @JsonIgnore
  public boolean effectiveToday() {
    if (this.dateRangeCondition == null) {
      return false;
    }
    if (this.dateRangeCondition.getDateRange() == null) {
      return false;
    }
    if (this.dateRangeCondition.getDateRange().getBeginDate() == null) {
      return false;
    }
    String beginDay =  Converter.toString_yyyyMMdd(dateRangeCondition.getDateRange().getBeginDate());
    String today = Converter.toString_yyyyMMdd(new Date());
    return beginDay.equals(today);
  }

  public enum State {
    @ApiModelProperty("进行中")
    effect,
    @ApiModelProperty("已终止")
    stopped,
  }

  @QueryEntity(PromRule.class)
  public static class Queries extends QueryFactors.Entity {
    private static final String PREFIX = PromRule.class.getName() + "::";
    @QueryField
    public static final String TENANT = PREFIX + "tenant";
    @QueryField
    public static final String STARTER_ORG_UUID = PREFIX + "starterOrgUuid";
    @QueryField
    public static final String ORG_ID = PREFIX + "orgId";
    @QueryField
    public static final String NAME = PREFIX + "name";
    @QueryField
    public static final String STATE = PREFIX + "state";
    @QueryField
    public static final String ACTIVITY_TYPE = PREFIX + "activityType";
    @QueryOperation
    public static final String TIME_PERIOD_CONDITION = PREFIX + "timePeriodCondition";
    @QueryField
    public static final String BILL_NUMBER = PREFIX + "billNumber";
    @QueryField
    public static final String BEGIN_DATE = PREFIX + "dateRangeCondition.dateRange.beginDate";
    @QueryField
    public static final String END_DATE = PREFIX + "dateRangeCondition.dateRange.endDate";
    @QueryField
    public static final String CREATED = PREFIX + "createInfo.time";
    @QueryField
    public static final String LAST_MODIFIED = PREFIX + "lastModifyInfo.time";

    @QueryField
    public static final String PROMOTION_TYPE = PREFIX + "promotion.promotionType";
    @QueryField
    public static final String TEMPLATE_NAME = PREFIX + "template.name";
    @QueryField
    public static final String TEMPLATE_UUID = PREFIX + "template.uuid";
    @QueryField
    public static final String ACTIVITY_UUID = PREFIX + "activity.uuid";
    @QueryOperation
    public static final String JOIN_UNIT_UUID = PREFIX + "joinUnitUuid";
    @QueryField
    public static final String MARKET_REASON = PREFIX + "marketReason";
  }
}
