package com.hd123.baas.sop.remote.rsmas.area;

import com.hd123.baas.sop.remote.rsmas.RsMasFilter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@ApiModel("区域查询条件")
public class RsAreaFilter extends RsMasFilter {

  private static final long serialVersionUID = 5230590461752410363L;

  @ApiModelProperty(value = "名称等于")
  private String nameEq;
  @ApiModelProperty(value = "名称类似于")
  private String nameLike;
  @ApiModelProperty(value = "上级等于")
  private String upperEq;
  @ApiModelProperty(value = "层级等于")
  private Integer levelEq;
  @ApiModelProperty(value = "代码等于")
  private String codeEq;
  @ApiModelProperty(value = "代码在范围")
  private List<String> codeIn;

}