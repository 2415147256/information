package com.hd123.baas.sop.service.api.sysconfig;

import java.math.BigDecimal;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author maodapeng
 * @Since
 */
@Setter
@Getter
public class FeedbackAutoAuditConfig {
  @ApiModelProperty(value = "是否开启自动审核")
  private Boolean enable;
  @ApiModelProperty(value = "来源规则")
  private List<SourceRule> sourceRules;

  @Setter
  @Getter
  public static class SourceRule{
    @ApiModelProperty(value = "订单来源")
    private String source;
    @ApiModelProperty(value = "自动审核规则")
    private List<Rule> rules;
  }

  @Setter
  @Getter
  public static class Rule {
    @ApiModelProperty(value = "等级ID")
    private String gradeId;
    @ApiModelProperty(value = "等级名称")
    private String gradeName;
    @ApiModelProperty(value = "自动审批最高申请金额")
    private BigDecimal applyAmount;
    @ApiModelProperty(value = "赔付比例")
    private BigDecimal rate;
    @ApiModelProperty(value = "同意原因")
    private String reason;
    @ApiModelProperty(value = "承担部门")
    private List<RuleDepartmentLine> lines;
  }

  @Getter
  @Setter
  public static class RuleDepartmentLine {
    @ApiModelProperty(value = "分类ID")
    public String categoryId;
    @ApiModelProperty(value = "分类代码")
    public String categoryCode;
    @ApiModelProperty(value = "分类名称")
    public String categoryName;
    @ApiModelProperty(value = "承担部门代码")
    private String depCode;
    @ApiModelProperty(value = "承担部门名称")
    private String depName;
  }
}
