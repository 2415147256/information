package com.hd123.baas.sop.service.api.pms.template;

import com.hd123.baas.sop.service.api.promotion.FavorSharing;
import com.hd123.baas.sop.service.api.promotion.PromFieldControl;
import com.hd123.baas.sop.service.api.promotion.Promotion;
import com.hd123.baas.sop.service.api.promotion.PromotionJoinUnits;
import com.hd123.baas.sop.service.api.promotion.condition.DateRangeCondition;
import com.hd123.baas.sop.service.api.promotion.condition.TimePeriodCondition;
import com.hd123.rumba.commons.biz.entity.HasUCN;
import com.hd123.rumba.commons.biz.entity.StandardEntity;
import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryFactors;
import com.hd123.rumba.commons.biz.query.QueryField;
import com.hd123.rumba.commons.biz.query.QueryOperation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@ApiModel("促销模板")
@EqualsAndHashCode(callSuper = true)
public class PromTemplate extends StandardEntity implements HasUCN {

  public static final String FILTER_KEYWORD_LIKES = "keyword:%=%";
  public static final String FILTER_PROMOTION_TYPE_EQUAL = "promotionType:=";
  public static final String FILTER_PREDEFINE_EQUAL = "predefine:=";
  public static final String FILTER_DATE_RANGE_BETWEEN = "dateRange:[,]";
  public static final String FILTER_GRANT_UNIT_UUID_EQUAL = "grantUnitUuid:=";
  public static final String FILTER_ORG_ID_IN="orgIdIn";

  public static final String PARTS_GRANT_UNITS = "grantUnits";
  public static final String PARTS_PROMOTION = "promotion";
  public static final String PARTS_FAVOR_SHARING = "favorSharings";
  public static final String[] ALL_PARTS = new String[]{
          PARTS_GRANT_UNITS, PARTS_PROMOTION, PARTS_FAVOR_SHARING
  };

  private String tenant;
  @ApiModelProperty("组织id")
  private String orgId;
  @ApiModelProperty("模板代码")
  private String code;
  @ApiModelProperty("名称")
  private String name;
  @ApiModelProperty("系统预定义")
  private boolean predefine;
  @ApiModelProperty("模板说明")
  private String remark;

  @ApiModelProperty("授权门店")
  private PromotionJoinUnits grantUnits;
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

  @ApiModelProperty("促销费用承担")
  private List<FavorSharing> favorSharings;
  @ApiModelProperty("字段控制")
  private Map<String, PromFieldControl> fieldControls = new HashMap<>();

  @QueryEntity(PromTemplate.class)
  public static class Queries extends QueryFactors.Entity {
    private static final String PREFIX = PromTemplate.class.getName() + "::";
    @QueryOperation
    public static final String KEYWORD = PREFIX + "keyword";
    @QueryField
    public static final String TENANT = PREFIX + "tenant";
    @QueryField
    public static final String ORG_ID = PREFIX + "orgId";
    @QueryField
    public static final String CODE = PREFIX + "code";
    @QueryField
    public static final String NAME = PREFIX + "name";
    @QueryField
    public static final String PREDEFINE = PREFIX + "predefine";
    @QueryField
    public static final String PROMOTION_TYPE = PREFIX + "promotion.promotionType";
    @QueryField
    public static final String BEGIN_DATE = PREFIX + "dateRangeCondition.dateRange.beginDate";
    @QueryField
    public static final String END_DATE = PREFIX + "dateRangeCondition.dateRange.endDate";
    @QueryField
    public static final String CREATED = PREFIX + "createInfo.time";
    @QueryOperation
    public static final String GRANT_UNIT_UUID = PREFIX + "grantUnitUuid";
  }
}
