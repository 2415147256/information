package com.hd123.baas.sop.remote.rsmas.spu;

import com.hd123.baas.sop.remote.rsmas.RsMasFilter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@ApiModel("Spu查询条件")
public class RsSpuFilter extends RsMasFilter {

  private static final long serialVersionUID = 1L;
  @ApiModelProperty(value = "orgId等于", required = false)
  private String orgIdEq;
  @ApiModelProperty(value = "orgType等于", required = false)
  private String orgTypeEq;
  @ApiModelProperty(value = "名称类似于", required = false)
  private String nameLike;
  @ApiModelProperty(value = "Id类似于", required = false)
  private String idLike;
  @ApiModelProperty(value = "包含skuId", required = false)
  private String skuContains;
  @ApiModelProperty(value = "包含skuId列表", required = false)
  private List<String> skuIdIn;
}