package com.hd123.baas.sop.remote.rsh6sop.feedback;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 承担明细
 * @author yu lilin
 * @since 1.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ApiModel(description = "承担明细")
public class RsH6FeedbackDepLine {
  private static final long serialVersionUID = -3308095849018150027L;
  private String uuid;
  @ApiModelProperty(value = "承担部门代码", required = true)
  private String depCode;
  @ApiModelProperty(value = "承担部门名称", required = true)
  private String depName;
  @ApiModelProperty(value = "承担比例", example = "70", required = true)
  private BigDecimal rate;
  @ApiModelProperty(value = "承担金额", example = "10.0000", required = true)
  private BigDecimal total;
}
