
package com.hd123.baas.sop.remote.rssos.feedback;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 质量反馈图片明细
 * @author yu lilin
 * @since 1.0
 */
@Data
@ApiModel(description = "质量反馈图片明细")
public class RsFeedbackImage implements Serializable  {
    private static final long serialVersionUID = -5018350837059028668L;
    @ApiModelProperty(value = "图片id", required = true)
    private String id;
    @ApiModelProperty(value = "图片url", required = true)
    private String url;

}
