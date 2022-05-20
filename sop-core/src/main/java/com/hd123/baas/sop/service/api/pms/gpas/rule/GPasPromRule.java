package com.hd123.baas.sop.service.api.pms.gpas.rule;

import com.hd123.baas.sop.service.api.promotion.FavorSharing;
import com.hd123.baas.sop.service.api.promotion.Promotion;
import com.hd123.baas.sop.service.api.promotion.PromotionJoinUnits;
import com.hd123.baas.sop.service.api.promotion.condition.DateRangeCondition;
import com.hd123.baas.sop.service.api.promotion.condition.TimePeriodCondition;
import com.hd123.rumba.commons.biz.entity.StandardEntity;
import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryFactors;
import com.hd123.rumba.commons.biz.query.QueryField;
import com.hd123.rumba.commons.biz.query.QueryOperation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Data
@ApiModel("批发订货促销规则")
@EqualsAndHashCode(callSuper = true)
public class GPasPromRule extends StandardEntity {

  public static final String FILTER_ACTIVITY_KEYWORD_LIKES = "activityKeyWord:%=%";
  public static final String FILTER_DATE_RANGE_BETWEEN = "dateRange:[,]";
  public static final String FILTER_PROMOTION_LIKES = "product:%=%";
  public static final String FILTER_JOIN_UNITS_LIKES = "joinUnits:%=%";
  public static final String FILTER_FRONT_STATE_EQUAL = "frontState:=";
  public static final String FILTER_PROMOTION_TYPE_EQUAL = "promotionType:=";


  public static final String FRONT_STATE_EFFECT = "effect";
  public static final String FRONT_STATE_EXPIRED = "expired";

  public static final String FAVOR_SHARINGS = "favorSharings";
  public static final String PARTS_JOIN_UNITS = "joinUnits";
  public static final String PARTS_PROMOTION = "promotion";
  public static final List<String> FILTER_ITEM = Arrays.asList(
          FILTER_ACTIVITY_KEYWORD_LIKES, FILTER_DATE_RANGE_BETWEEN, FILTER_PROMOTION_LIKES
                                                              );
  public static final String[] ALL_PARTS = new String[]{
          PARTS_PROMOTION, PARTS_JOIN_UNITS, FAVOR_SHARINGS
  };

  @ApiModelProperty("租户")
  private String tenant;
  @ApiModelProperty("活动编号")
  private String billNumber;
  @ApiModelProperty("名称")
  private String name;
  @ApiModelProperty("状态")
  private State state;
  @ApiModelProperty("前端状态")
  private String frontState;
  @ApiModelProperty("适用门店")
  private PromotionJoinUnits joinUnits;

  @ApiModelProperty("会员专享")
  private Boolean onlyMember;
  @ApiModelProperty("促销渠道")
  private List<String> promChannels;
  @ApiModelProperty("促销费用承担")
  private List<FavorSharing> favorSharings;
  @ApiModelProperty("营销物料费")
  private BigDecimal materielAmount;
  @ApiModelProperty("促销日期")
  private DateRangeCondition dateRangeCondition;
  @ApiModelProperty("时段促销")
  private TimePeriodCondition timePeriodCondition;
  @ApiModelProperty("促销设置")
  private Promotion promotion;
  @ApiModelProperty("促销说明")
  private String promNote;

  public enum State {
    @ApiModelProperty("未审核")
    initial,
    @ApiModelProperty("已审核")
    audited,
    @ApiModelProperty("已终止")
    stopped,
    @ApiModelProperty("已作废")
    canceled,
  }

  @QueryEntity(GPasPromRule.class)
  public static class Queries extends QueryFactors.StandardEntity {
    private static final String PREFIX = GPasPromRule.class.getName() + "::";
    @QueryField
    public static final String NAME = PREFIX + "name";
    @QueryField
    public static final String TENANT = PREFIX + "tenant";
    @QueryField
    public static final String STATE = PREFIX + "state";
    @QueryField
    public static final String BILL_NUMBER = PREFIX + "billNumber";
    @QueryOperation
    public static final String JOIN_UNITS = PREFIX + "joinUnits";
    @QueryOperation
    public static final String PROMOTION = PREFIX + "promotion";

    @QueryField
    public static final String PROMOTION_TYPE = PREFIX + "promotion.promotionType";
    @QueryField
    public static final String PROMOTION_MODE = PREFIX + "promotion.promotionMode";
    @QueryField
    public static final String PROMOTION_DESCRIPTION = PREFIX + "promotion.description";
    @QueryField
    public static final String PROM_NOTE = PREFIX + "promNote";
    @QueryField
    public static final String BEGIN_DATE = PREFIX + "dateRangeCondition.dateRange.beginDate";
    @QueryField
    public static final String END_DATE = PREFIX + "dateRangeCondition.dateRange.endDate";
    @QueryField
    public static final String ALL_UNIT = PREFIX + "joinUnits.allUnit";
    @QueryField
    public static final String CREATED = PREFIX + "createInfo.time";
  }

}
