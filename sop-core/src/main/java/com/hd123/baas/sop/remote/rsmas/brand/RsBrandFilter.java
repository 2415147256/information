package com.hd123.baas.sop.remote.rsmas.brand;

import com.hd123.baas.sop.remote.rsmas.RsMasFilter;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel("品牌查询条件")
public class RsBrandFilter extends RsMasFilter {

  private static final long serialVersionUID = -4981838750275595690L;
  
  @ApiModelProperty(value = "所属组织类型等于")
  private String orgTypeEq;
  @ApiModelProperty(value = "所属组织ID等于")
  private String orgIdEq;
  @ApiModelProperty(value = "父品牌等于")
  private String upperEq;
  @ApiModelProperty(value = "代码等于")
  private String codeEq;
  @ApiModelProperty(value = "代码起始于")
  private String codeStartWith;
  @ApiModelProperty(value = "名称类似于")
  private String nameLike;

}