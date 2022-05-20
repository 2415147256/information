package com.hd123.baas.sop.remote.rsmas.shopsku;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class RsShopSkuTagBatchUpdate {
  @ApiModelProperty(value = "ID列表")
  private List<String> ids = new ArrayList<String>();
  @ApiModelProperty(value = "标签列表")
  private List<RsShopSkuTagUpdateLine> tags = new ArrayList<RsShopSkuTagUpdateLine>();
}
