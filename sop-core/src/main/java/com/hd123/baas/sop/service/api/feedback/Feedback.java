package com.hd123.baas.sop.service.api.feedback;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

/**
 * 质量反馈单
 * @author yu lilin
 * @since 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@ApiModel(description = "质量反馈单")
public class Feedback extends FeedbackBase {
    /**
     * 扩展信息：反馈图片明细
     */
    public static final String FETCH_IMAGES = "images";
    /**
     * 扩展信息：承担明细
     */
    public static final String FETCH_DEP_LINES = "depLines";

    @ApiModelProperty(value = "反馈图片明细")
    @Valid
    @NotEmpty
    private List<FeedbackImage> images = new ArrayList<>();

    @ApiModelProperty(value = "承担明细")
    @Valid
    @NotEmpty
    private List<FeedbackDepLine> depLines = new ArrayList<>();

    @ApiModelProperty(value = "渠道，offline-线下，EC-电商平台")
    private String channel;

    @ApiModelProperty(value = "分类路径")
    private String categoryPath;

}
