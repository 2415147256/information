package com.hd123.baas.sop.remote.rsh6sop.feedback;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @Author maodapeng
 * @Since
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RsH6FeedbackGrade {
  @ApiModelProperty(value = "等级标识", example = "d30756e55e2ec2c5e053060a110a01fe")
  private String id;
  @ApiModelProperty(value = "名称", example = "一般")
  private String name;
}
