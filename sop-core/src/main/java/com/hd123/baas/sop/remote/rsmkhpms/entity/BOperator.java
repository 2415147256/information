package com.hd123.baas.sop.remote.rsmkhpms.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author W.J.H.7
 * @since 1.0.0
 **/
@Getter
@Setter
public class BOperator {

    @ApiModelProperty(value = "创建日期")
    private Date created;
    @ApiModelProperty(value = "创建人")
    private String creatorId;
    @ApiModelProperty(value = "创建人名称")
    private String creatorName;
    @ApiModelProperty(value = "最后修改时间", required = false)
    private Date lastModified;
    @ApiModelProperty(value = "最后修改人信息", required = true)
    private String lastModifierId;
    @ApiModelProperty(value = "最后修改人名称", required = true)
    private String lastModifierName;
}
