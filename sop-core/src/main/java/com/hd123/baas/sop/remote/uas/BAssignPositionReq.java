package com.hd123.baas.sop.remote.uas;

import java.util.List;

import com.qianfan123.baas.common.entity.BEntity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 设置某个岗位的用户的请求实体
 */
@Setter
@Getter
public class BAssignPositionReq extends BEntity {
  @ApiModelProperty(value = "用户id", required = true, example = "102223")
  private List<String> userId;
}
