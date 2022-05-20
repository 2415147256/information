package com.hd123.baas.sop.service.api.basedata.store;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class YcShopUpdate {

  @ApiModelProperty("门店营业时间")
  private List<String> businessHours;
  @ApiModelProperty("价格组")
  private String priceGroupId;
  @ApiModelProperty("经度")
  private String longitude;
  @ApiModelProperty("纬度")
  private String latitude;
}
