package com.hd123.baas.sop.service.api.basedata.collocation;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ApiModel("加料组和商品的关系解除请求")
public class CollocationGruopSKUUnbind {

  @ApiModelProperty(value = "SKU的ID列表")
  private List<String> skuIds = new ArrayList<String>();
}
