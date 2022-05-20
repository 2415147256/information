package com.hd123.baas.sop.remote.uas;

import com.qianfan123.baas.common.entity.BEntity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BPosition extends BEntity {

    @ApiModelProperty(value = "岗位代码", required = true, example = "102223")
    private String code;
    @ApiModelProperty(value = "岗位名称", example = "技术岗")
    private String name;
    @ApiModelProperty(value = "岗位类型id", required = true, example = "岗位类型id")
    private String positionTypeId;
    @ApiModelProperty(value = "岗位类型名称", required = true, example = "岗位类型名称")
    private String positionTypeName;
}
