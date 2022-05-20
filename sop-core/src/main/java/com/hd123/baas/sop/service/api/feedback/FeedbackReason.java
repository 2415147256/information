package com.hd123.baas.sop.service.api.feedback;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 原因
 * @author yu lilin
 * @since 1.0
 */
@Getter
@Setter
@ApiModel( description = "原因")
public class FeedbackReason implements Serializable {
    private final static long serialVersionUID = 1974315528674309668L;

    @ApiModelProperty(value = "原因类型,取值范围：apply-申请原因，approve-通过原因，reject-拒绝原因", required = true)
    private FeedbackReasonType type;
    @ApiModelProperty(value = "原因内容", example = "其他", required = true)
    private String content;
}
