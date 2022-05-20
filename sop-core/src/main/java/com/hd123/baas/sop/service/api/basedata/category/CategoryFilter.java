package com.hd123.baas.sop.service.api.basedata.category;

import com.hd123.baas.sop.service.api.basedata.Filter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author Silent
 **/
@Getter
@Setter
@ApiModel(description = "分类查询条件")
public class CategoryFilter extends Filter {

  private static final long serialVersionUID = 1L;

  @ApiModelProperty(value = "组织类型等于...")
  private String orgTypeEq;
  @ApiModelProperty(value = "组织ID等于")
  private String orgIdEq;
  @ApiModelProperty(value = "所属组织类型在....范围内")
  private List<String> orgTypeIn;
  @ApiModelProperty(value = "所属组织ID在....范围内")
  private List<String> orgIdIn;
  @ApiModelProperty(value = "上级分类等于")
  private String upperEq;
  @ApiModelProperty(value = "ID在...范围内")
  private List<String> idIn;
  @ApiModelProperty(value = "代码名称类似于")
  private String keyword;
  @ApiModelProperty(value = "层级码等于")
  private Integer levelEq;

}
