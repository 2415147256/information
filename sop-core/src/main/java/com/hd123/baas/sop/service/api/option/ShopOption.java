package com.hd123.baas.sop.service.api.option;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 门店侧写
 */
@Getter
@Setter
public class ShopOption {
  @ApiModelProperty(value = "营业执照", required = false)
  private List<String> businessLicenses = new ArrayList<>();
  @ApiModelProperty(value = "员工健康证", required = false)
  private List<String> empHealthyImages = new ArrayList<>();
  @ApiModelProperty(value = "食品经营许可证", required = false)
  private List<String> foodBusinessLicenses = new ArrayList<>();

}
