package com.hd123.baas.sop.remote.uas;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class BUserPosition {
  @ApiModelProperty(value = "用户uuid", required = true, example = "uuid")
  private String uuid;
  @ApiModelProperty(value = "岗位类型id", required = true, example = "岗位类型id")
  private String positionTypeId;
  @ApiModelProperty(value = "岗位类型名称", required = true, example = "岗位类型名称")
  private String positionTypeName;
  @ApiModelProperty(value = "岗位信息", example = "岗位信息")
  private List<BPosition> bPositions;
}
