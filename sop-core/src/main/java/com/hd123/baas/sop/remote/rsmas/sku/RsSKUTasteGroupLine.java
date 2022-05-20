package com.hd123.baas.sop.remote.rsmas.sku;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lina
 */
@Getter
@Setter
@ApiModel("修改SKU口味组明细")
public class RsSKUTasteGroupLine {

  @ApiModelProperty(value = "SKUID", required = true)
  private String skuId;
  @ApiModelProperty(value = "口味组id列表")
  private List<RsSkuTasteGroupSave> tasteGroups = new ArrayList<>();
}