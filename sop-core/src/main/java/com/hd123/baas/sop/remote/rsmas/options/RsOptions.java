package com.hd123.baas.sop.remote.rsmas.options;

import com.hd123.baas.sop.remote.rsmas.RsMasEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RsOptions extends RsMasEntity {
  private static final long serialVersionUID = -1909006380691607440L;

  @ApiModelProperty(value = "组织类型")
  private String orgType;
  @ApiModelProperty(value = "组织id")
  private String orgId;
  @ApiModelProperty(value = "类型", required = true)
  private String type;
  @ApiModelProperty(value = "选项名", required = true)
  private String key;
  @ApiModelProperty(value = "选项值", required = true)
  private String value;
  @ApiModelProperty(value = "参数", required = true)
  private List<RsParameter> parameters = new ArrayList<>();

}
