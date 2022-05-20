package com.hd123.baas.sop.remote.rsmas.sku;


import com.hd123.baas.sop.remote.rsmas.tastegroup.RsTasteGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel("商品口味组")
public class RsSkuTasteGroup {
  @ApiModelProperty(value = "口味组")
  private RsTasteGroup tasteGroup;
  @ApiModelProperty(value = "显示序号")
  private Integer showIndex;
}
