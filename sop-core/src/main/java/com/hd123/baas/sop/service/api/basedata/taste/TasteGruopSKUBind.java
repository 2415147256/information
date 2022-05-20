package com.hd123.baas.sop.service.api.basedata.taste;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ApiModel("口味组和商品的关系建立请求")
public class TasteGruopSKUBind {

  @ApiModelProperty(value = "SKU的ID列表")
  private List<String> skuIds = new ArrayList<String>();
}
