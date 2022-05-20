package com.hd123.baas.sop.service.api.basedata.area;

import com.hd123.baas.sop.service.api.basedata.Filter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@ApiModel(description = "区域查询定义")
public class AreaFilter extends Filter {

  private static final long serialVersionUID = 5636913535241055335L;

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
