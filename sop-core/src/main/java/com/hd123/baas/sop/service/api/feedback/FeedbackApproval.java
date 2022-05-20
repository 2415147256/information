package com.hd123.baas.sop.service.api.feedback;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 质量反馈单处理同意对象
 *
 * @author yu lilin
 * @since 1.0
 */
@Getter
@Setter
@ApiModel(description = "质量反馈单处理同意对象")
public class FeedbackApproval implements Serializable {
  private static final long serialVersionUID = 8160917370505941952L;

  @ApiModelProperty(value = "质量反馈单标识", example = "c14c046939704cf1a0e93545a1f768bb", required = true)
  @NotNull
  private String billId;
  @ApiModelProperty(value = "门店标识", example = "8048", required = true)
  @NotBlank
  private String shop;
  @ApiModelProperty(value = "同意原因", example = "其他", required = true)
  @NotNull
  private String reason;
  @ApiModelProperty(value = "备注", required = false)
  private String note;
  @ApiModelProperty(value = "赔付比例", example = "70", required = true)
  @NotNull
  private BigDecimal payRate;
  @ApiModelProperty(value = "赔付金额", example = "10.0000", required = true)
  @NotNull
  private BigDecimal payTotal;
  @ApiModelProperty(value = "承担明细")
  @Valid
  @NotEmpty
  private List<FeedbackDepLine> feedbackDepLines = new ArrayList<>();

  @ApiModelProperty(value = "审核时间", required = true)
  @NotNull
  private Date auditTime;
  @ApiModelProperty(value = "审核人代码", example = "zhangsan", required = true)
  @NotBlank
  private String auditorId;
  @ApiModelProperty(value = "审核人名称", example = "张三", required = true)
  @NotBlank
  private String auditorName;
}
