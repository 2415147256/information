package com.hd123.baas.sop.remote.uas;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@ApiModel("UserStatement")
public class BUserStatement {

  @ApiModelProperty(value = "应用id")
  private String appId;

  @ApiModelProperty(value = "类型", example = "AREA")
  private UserStatementType type = UserStatementType.AREA;

  @ApiModelProperty(value = "用户id")
  private String owner;

  @ApiModelProperty(value = "影响/目的", example = "ALLOW")
  private UserStatementEffect effect;

  @ApiModelProperty(value = "操作动作", example = "QUERY")
  private UserStatementAction action;

  @ApiModelProperty(value = "资源", example = "AREA_DATA")
  private UserStatementResource resource;

  @ApiModelProperty(value = "限制条件")
  private List<String> conditions;
}
