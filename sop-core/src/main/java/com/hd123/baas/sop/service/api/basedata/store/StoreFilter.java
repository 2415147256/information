package com.hd123.baas.sop.service.api.basedata.store;

import com.hd123.baas.sop.service.api.basedata.Filter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@ApiModel(description = "门店查询定义")
public class StoreFilter extends Filter {

  private static final long serialVersionUID = 1L;

  @ApiModelProperty(value = "组织类型等于...")
  private String orgTypeEq;
  @ApiModelProperty(value = "组织ID等于")
  private String orgIdEq;
  @ApiModelProperty(value = "所属组织类型在...范围内")
  private List<String> orgTypeIn;
  @ApiModelProperty(value = "所属组织ID在...范围内")
  private List<String> orgIdIn;

  @ApiModelProperty(value = "代码/名称类似于")
  private String keyword;
  @ApiModelProperty(value = "营业状态等于")
  private String businessStateEq;
  @ApiModelProperty("营业状态在...之中")
  private List<String> businessStateIn;

  @ApiModelProperty(value = "CODE在...范围内")
  private List<String> codeIn;
  @ApiModelProperty(value = "ID在...范围内")
  private List<String> idIn;
  @ApiModelProperty("门店ID不在...之中")
  private List<String> idNotIn;
  @ApiModelProperty(value = "区域代码等于")
  private String areaCodeEq;

  @ApiModelProperty("规则： 经度和纬度用\",\"分割，经度在前，纬度在后，经纬度小数点后不得超过6位")
  private String location;
  @ApiModelProperty(value = "用户Id等于")
  private String userIdEq;
}
