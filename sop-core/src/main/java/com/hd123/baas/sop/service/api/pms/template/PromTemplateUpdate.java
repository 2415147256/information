package com.hd123.baas.sop.service.api.pms.template;

import com.hd123.baas.sop.service.api.promotion.FavorSharing;
import com.hd123.baas.sop.service.api.promotion.PromFieldControl;
import com.hd123.baas.sop.service.api.promotion.Promotion;
import com.hd123.baas.sop.service.api.promotion.PromotionJoinUnits;
import com.hd123.baas.sop.service.api.promotion.condition.DateRangeCondition;
import com.hd123.baas.sop.service.api.promotion.condition.TimePeriodCondition;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhuangwenting
 * @since 1.0
 */
@Data
public class PromTemplateUpdate {
  @ApiModelProperty("名称")
  @NotBlank
  private String name;

  @ApiModelProperty("授权门店")
  private PromotionJoinUnits grantUnits;
  @ApiModelProperty("会员专享")
  private Boolean onlyMember;
  @ApiModelProperty("促销渠道")
  private List<String> promChannels;
  @ApiModelProperty("模版说明")
  private String remark;

  @ApiModelProperty("促销日期")
  private DateRangeCondition dateRangeCondition;
  @ApiModelProperty("时段促销")
  private TimePeriodCondition timePeriodCondition;
  @ApiModelProperty("促销内容")
  private Promotion promotion;
  @ApiModelProperty("促销说明")
  private String promNote;

  @ApiModelProperty("促销费用承担")
  // @NotBlank todo
  private List<FavorSharing> favorSharings;
  @ApiModelProperty("字段控制")
  private Map<String, PromFieldControl> fieldControls = new HashMap<>();
}
