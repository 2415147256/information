package com.hd123.baas.sop.service.api.basedata.platformcategory;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Silent
 **/
@Getter
@Setter
@ApiModel(description = "元初分类ID列表对象")
public class PlatformCategoryIdList {
  @ApiModelProperty(value = "元初分类ID列表")
  public List<String> ids = new ArrayList<>();
}
