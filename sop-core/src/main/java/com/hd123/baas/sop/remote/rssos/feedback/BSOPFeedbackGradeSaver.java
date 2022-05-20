package com.hd123.baas.sop.remote.rssos.feedback;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @Author maodapeng
 * @Since
 */
@Setter
@Getter
public class BSOPFeedbackGradeSaver {
  @ApiModelProperty(value = "质量反馈等级列表")
  private List<BSOPFeedbackGrade> grades;
}
