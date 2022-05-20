package com.hd123.baas.sop.remote.rsh6sop.feedback;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 质量反馈图片明细
 * @author yu lilin
 * @since 1.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ApiModel(description = "质量反馈图片明细")
public class RsH6FeedbackImage {
  private static final long serialVersionUID = 7556908853285448124L;
  private String uuid;
  @ApiModelProperty(value = "图片id", required = true)
  private String id;
  @ApiModelProperty(value = "图片url", required = true)
  private String url;

}
