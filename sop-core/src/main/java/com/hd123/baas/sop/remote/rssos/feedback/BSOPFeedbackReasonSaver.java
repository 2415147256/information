package com.hd123.baas.sop.remote.rssos.feedback;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * @Author maodapeng
 * @Since
 */
@Setter
@Getter
public class BSOPFeedbackReasonSaver implements Serializable {
  @ApiModelProperty(value = "0-申请原因 1-同意原因 2-拒绝原因", example = "0")
  private Integer type;
  @ApiModelProperty(value = "类别代码", example = "01")
  private String sortCode;
  @ApiModelProperty(value = "类别名称", example = "蔬菜")
  private String sortName;
  @ApiModelProperty(value = "原因列表", example = "原因列表", required = true)
  private List<String> reasons;
}
