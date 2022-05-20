package com.hd123.baas.sop.qcy.service.api.sku;

import com.hd123.baas.sop.service.api.basedata.taste.TasteGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author: liuyang
 **/

@Getter
@Setter
@ApiModel("商品口味组")
public class SkuTasteGroup {
  @ApiModelProperty(value = "口味组")
  private TasteGroup tasteGroup;
  @ApiModelProperty(value = "显示序号")
  private Integer showIndex;
}

