package com.hd123.baas.sop.remote.rsmas.department;

import com.hd123.baas.sop.remote.rsmas.RsMasEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class RsDepartment extends RsMasEntity {
  private static final long serialVersionUID = 5293360971124277715L;

  @ApiModelProperty(value = "ID")
  public String id;
  @ApiModelProperty(value = "代码", required = true)
  private String code;
  @ApiModelProperty(value = "名称", required = true)
  private String name;
  @ApiModelProperty(value = "组织类型")
  private String orgType;
  @ApiModelProperty(value = "组织id")
  private String orgId;
}
