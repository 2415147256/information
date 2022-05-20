package com.hd123.baas.sop.service.api.basedata.options;

import com.hd123.rumba.commons.biz.entity.Entity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author lina
 */
@Data
public class Options extends Entity {
  private static final long serialVersionUID = -1909006380691607440L;

  @ApiModelProperty(value = "选项名", required = true)
  private String key;
  @ApiModelProperty(value = "选项值", required = true)
  private String value;
  @ApiModelProperty(value = "组织类型")
  private String orgType;
  @ApiModelProperty(value = "组织id")
  private String orgId;

}
