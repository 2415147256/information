package com.hd123.baas.sop.service.api.pms.explosive;

import com.hd123.baas.sop.service.api.promotion.PomEntity;
import com.hd123.rumba.commons.biz.entity.Entity;
import com.hd123.rumba.commons.biz.entity.UCN;
import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryFactors;
import com.hd123.rumba.commons.biz.query.QueryField;
import com.hd123.rumba.commons.biz.query.QueryOperation;
import com.hd123.spms.commons.calendar.DateRange;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@ApiModel("爆品预定记录")
@EqualsAndHashCode(callSuper = true)
public class ExplosiveActivityDetail extends Entity {
  public static final String FILTER_SKU_KEYWORD_LIKES = "skuKeyword:%=%";
  public static final String FILTER_STORE_UUID_EQUAL = "joinUnitUuid:=";
  public static final String FILTER_STORE_KEYWORD_LIKE = "storeKeyword:%=%";
  public static final String FILTER_ACTIVITY_UUID_EQUAL = "activityUuid:=";
  public static final String FILTER_ACTIVITY_FONT_STATE_EQUAL = "activityFontState:=";
  public static final String FILTER_ALC_QPC_LIKE = "alcQpc:%=%";
  public static final String FILTER_ORG_ID_IN = "orgIdIn";
  public static final String FILTER_ACTIVITY_RANGE_BETWEEN = "activityRange:[,]";

  public static final String PARTS_JOIN = "detailJoin";


  @ApiModelProperty("租户")
  private String tenant;
  @ApiModelProperty("组织ID")
  private String orgId;
  @ApiModelProperty("商品")
  private PomEntity entity;
  @ApiModelProperty("所属活动")
  private UCN activity;
  @ApiModelProperty("活动状态")
  private String activityState;
  @ApiModelProperty("活动前端状态")
  private String activityFrontState;
  @ApiModelProperty("活动日期")
  private DateRange activityRange;
  @ApiModelProperty("订货量")
  private BigDecimal totalSignQty;
  @ApiModelProperty("门店示例")
  private UCN storeExample;
  @ApiModelProperty("门店数量")
  private Long storeCount;

  @QueryEntity(ExplosiveActivityDetail.class)
  public static class Queries extends QueryFactors.Entity {
    private static final String PREFIX = ExplosiveActivityDetail.class.getName() + "::";
    @QueryField
    public static final String TENANT = PREFIX + "tenant";
    @QueryField
    public static final String ORG_ID = PREFIX + "orgId";
    @QueryField
    public static final String ENTITY_NAME = PREFIX + "entity.name";
    @QueryField
    public static final String ENTITY_CODE = PREFIX + "entity.code";
    @QueryField
    public static final String ACTIVITY_STATE = PREFIX + "activityState";
    @QueryField
    public static final String ACTIVITY_UUID = PREFIX + "activity.uuid";
    @QueryOperation
    public static final String JOIN_UNIT_UUID = PREFIX + "storeExample.uuid";
    @QueryField
    public static final String JOIN_UNIT_CODE = PREFIX + "storeExample.code";
    @QueryOperation
    public static final String STORE_KEYWORD = PREFIX + "storeKeyword";
    @QueryField
    public static final String JOIN_UNIT_NAME = PREFIX + "storeExample.name";
    @QueryOperation
    public static final String STORE_KEYWORD_LIKE = PREFIX + "storeKeyword like";
    @QueryField
    public static final String ACTIVITY_BEGIN_DATE = PREFIX + "activityRange.beginDate";
    @QueryField
    public static final String ACTIVITY_END_DATE = PREFIX + "activityRange.endDate";
    @QueryOperation
    public static final String ACTIVITY_RANGE_BTW = PREFIX + "activityRange btw";
  }
}
