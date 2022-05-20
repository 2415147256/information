package com.hd123.baas.sop.remote.rsaccount.store;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@ApiModel("日清")
public class RSDailyClear {
  @ApiModelProperty("日清日期 yyyy-mm-dd")
  private String clearDate;
  @ApiModelProperty("备注")
  private String remark;
  @ApiModelProperty("操作来源")
  private String sourceAction;
  @ApiModelProperty("门店代码列表")
  private List<String> stores;
}
