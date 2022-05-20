package com.hd123.baas.sop.service.api.feedback;
import com.hd123.rumba.commons.biz.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 质量反馈图片明细
 * @author yu lilin
 * @since 1.0
 */
@Getter
@Setter
@ApiModel(description = "质量反馈图片明细")
public class FeedbackImage extends Entity {
    private static final long serialVersionUID = 7556908853285448124L;
    @ApiModelProperty(value = "图片id", required = true)
    private String id;
    @ApiModelProperty(value = "图片url", required = true)
    private String url;

}
