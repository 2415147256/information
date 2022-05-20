package com.hd123.baas.sop.remote.uas;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @Author maodapeng
 * @Since
 */
@Getter
@Setter
@ApiModel("用户可登录组织")
public class BUserOrg {
  @ApiModelProperty(value = "用户ID", example = "10002")
  private String userId;
  @ApiModelProperty(value = "可登录组织集合")
  private List<BOrg> orgs;
}
