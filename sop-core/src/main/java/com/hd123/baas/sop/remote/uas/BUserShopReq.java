package com.hd123.baas.sop.remote.uas;

import java.util.List;

import com.qianfan123.baas.common.entity.BEntity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户门店的请求实体
 */
@Setter
@Getter
public class BUserShopReq extends BEntity {
  @ApiModelProperty(value = "门店id", required = true, example = "门店id")
  private List<String> shopIds;
}
