package com.hd123.baas.sop.service.api.basedata.pos;

import com.hd123.baas.sop.service.api.basedata.Filter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@ApiModel(description = "收银机查询定义")
public class PosFilter extends Filter {

  private static final long serialVersionUID = 1L;

  @ApiModelProperty(value = "组织类型等于...")
  private String orgTypeEq;
  @ApiModelProperty(value = "组织ID等于")
  private String orgIdEq;
  @ApiModelProperty(value = "所属组织类型在...范围内")
  private List<String> orgTypeIn;
  @ApiModelProperty(value = "所属组织ID在...范围内")
  private List<String> orgIdIn;

  @ApiModelProperty(value = "ID等于")
  private String idEq;
  @ApiModelProperty(value = "id类似于")
  private String idLike;
  @ApiModelProperty(value = "门店ID等于")
  private String storeIdEq;
  @ApiModelProperty(value = "门店ID在...范围内")
  private List<String> storeIdIn;
  @ApiModelProperty(value = "出品部门ID在...范围内")
  private List<String> stallIdIn;
  @ApiModelProperty(value = "出品部门ID等于")
  private String stallIdEq;
  @ApiModelProperty(value = "起禁用状态等于")
  private Boolean enabledEq;
  @ApiModelProperty(value = "名称、代码类似于")
  private String keyword;
  @ApiModelProperty(value = "代码在...范围内")
  private List<String> codeIn;
  @ApiModelProperty(value = "是否默认等于")
  private Boolean isDefaultEq;
}
