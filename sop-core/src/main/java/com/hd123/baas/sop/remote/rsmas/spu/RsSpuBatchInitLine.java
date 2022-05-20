package com.hd123.baas.sop.remote.rsmas.spu;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author lina
 */
@Getter
@Setter
@ApiModel("商品Spu初始化明细")
public class RsSpuBatchInitLine implements Serializable {

  private static final long serialVersionUID = 1L;

  @ApiModelProperty(value = "SPU的ID")
  private String id;
  @ApiModelProperty(value = "名称")
  private String name;
  @ApiModelProperty(value = "skuId列表")
  private List<String> skuIds;
  @ApiModelProperty(value = "spu商品销售属性")
  private List<RsProductProperties> properties = new ArrayList<>();
}
