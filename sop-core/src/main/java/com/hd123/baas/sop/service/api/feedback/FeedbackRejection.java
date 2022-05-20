package com.hd123.baas.sop.service.api.feedback;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 质量反馈单处理拒绝对象
 *
 * @author yu lilin
 * @since 1.0
 */
@Getter
@Setter
@ApiModel(description = "质量反馈单处理拒绝对象")
public class FeedbackRejection implements Serializable {
  private static final long serialVersionUID = -4589460961103666532L;

  @ApiModelProperty(value = "质量反馈单标识", example = "c14c046939704cf1a0e93545a1f768bb", required = true)
  @NotNull
  private String billId;
  @ApiModelProperty(value = "门店标识", example = "8048", required = true)
  @NotBlank
  private String shop;
  @ApiModelProperty(value = "拒绝原因", example = "其他", required = true)
  @NotNull
  private String reason;
  @ApiModelProperty(value = "备注", required = false)
  private String note;

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
