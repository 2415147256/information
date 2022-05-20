/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2021，所有权利保留。
 * <p>
 * 项目名： workspace_mkh
 * 文件名： PromActivity.java
 * 模块说明：
 * 修改历史：
 * 2021年01月29日 - huangjunxian - 创建。
 */
package com.hd123.baas.sop.remote.rsh6sop.activity;

/**
 * @author huangjunxian
 * @since 1.0
 */

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel(description = "运营平台活动")
public class H6SopActivity implements Serializable {
  @ApiModelProperty("活动ID")
  private String uuid;
  @NotBlank
  @Length(max = 32)
  @ApiModelProperty(value = "活动代码", example = "0001", required = true)
  private String activityCode;
  @NotBlank
  @Length(max = 64)
  @ApiModelProperty(value = "活动名称", example = "活动", required = true)
  private String activityName;
  @NotBlank
  @Length(max = 20)
  @ApiModelProperty(value = "活动类型，取值范围：promotion-促销活动，explosive-爆品预订活动", required = true)
  private H6ActivityType type;
  @NotNull
  @ApiModelProperty(value = "活动开始时间", required = true)
  private Date beginTime;
  @NotNull
  @ApiModelProperty(value = "活动截止时间", required = true)
  private Date endTime;
  @ApiModelProperty(value = "报名开始时间", required = false)
  private Date signBeginTime;
  @ApiModelProperty(value = "报名截止时间", required = false)
  private Date signEndTime;
  @NotNull
  @ApiModelProperty(value = "是否会员专享", example = "false", required = true)
  private Boolean memberExclusive = Boolean.FALSE;
  @NotNull
  @ApiModelProperty(value = "是否全场", example = "false", required = true)
  private Boolean overall;
  @Length(max = 38)
  @ApiModelProperty(value = "所属组织id", required = false)
  private String starterOrgUuid;
  @NotNull
  @ApiModelProperty(value = "营销物料费", required = true)
  private BigDecimal materialAmount;
  @Length(max = 100)
  @ApiModelProperty(value = "促销渠道", required = false)
  private String promChannels;
  @ApiModelProperty(value = "审核时间", example = "2020-11-30 11:00:00", required = false)
  private Date auditTime;
  @Length(max = 512)
  @ApiModelProperty(value = "活动说明", example = "", required = false)
  private String note;

}
