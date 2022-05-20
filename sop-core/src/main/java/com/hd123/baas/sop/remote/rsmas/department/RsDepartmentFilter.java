package com.hd123.baas.sop.remote.rsmas.department;

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
@ApiModel("部门查询条件")
public class RsDepartmentFilter extends RsMasFilter {

  private static final long serialVersionUID = 1L;
  @ApiModelProperty(value = "名称、代码类似于")
  private String keyword;
  @ApiModelProperty(value = "代码等于")
  private String codeEq;
  @ApiModelProperty(value = "代码类似于")
  private String codeLike;
  @ApiModelProperty(value = "代码在...范围内")
  private List<String> codeIn;
  @ApiModelProperty(value = "名称类似于")
  private String nameLike;
  @ApiModelProperty(value = "名称类等于")
  private String nameEq;
  @ApiModelProperty(value = "id在...范围内")
  private List<String>  idIn;
  @ApiModelProperty(value = "所属组织类型等于")
  private String orgTypeEq;
  @ApiModelProperty(value = "所属组织ID等于")
  private String orgIdEq;
}