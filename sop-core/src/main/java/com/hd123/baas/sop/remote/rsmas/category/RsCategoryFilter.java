package com.hd123.baas.sop.remote.rsmas.category;

import com.hd123.baas.sop.remote.rsmas.RsMasFilter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author lina
 */
@Getter
@Setter
@ApiModel("分类查询条件")
public class RsCategoryFilter extends RsMasFilter {

  private static final long serialVersionUID = 1L;

  @ApiModelProperty(value = "组织类型等于...")
  private String orgTypeEq;
  @ApiModelProperty(value = "组织ID等于")
  private String orgIdEq;
  @ApiModelProperty(value = "上级分类等于")
  private String upperEq;
  @ApiModelProperty(value = "上级分类在....范围内")
  private List<String> upperIn;
  @ApiModelProperty(value = "代码名称类似于")
  private String keyword;
  @ApiModelProperty(value = "代码等于")
  private String codeEq;
  @ApiModelProperty(value = "代码类似于")
  private String codeLike;
  @ApiModelProperty(value = "代码在...范围内")
  private List<String> codeIn;
  @ApiModelProperty(value = "名称类似于")
  private String nameLike;
  @ApiModelProperty(value = "路径开始于")
  private String pathStartWidth;
  @ApiModelProperty(value = "层级码等于")
  private Integer levelEq;
  @ApiModelProperty("所属组织类型在....范围内")
  private List<String> orgTypeIn;
  @ApiModelProperty("所属组织ID在....范围内")
  private List<String> orgIdIn;
}