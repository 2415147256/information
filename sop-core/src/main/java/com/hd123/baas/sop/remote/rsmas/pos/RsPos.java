package com.hd123.baas.sop.remote.rsmas.pos;

import com.hd123.baas.sop.remote.rsmas.RsMasEntity;
import com.hd123.rumba.commons.biz.entity.UCN;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author lina
 */
@Data
public class RsPos extends RsMasEntity {

  private static final long serialVersionUID = 3735590422846305651L;
  @ApiModelProperty(value = "代码", required = true)
  private String code;
  @ApiModelProperty(value = "名称", required = true)
  private String name;
  @ApiModelProperty(value = "门店信息", required = true)
  private UCN store;
  @ApiModelProperty(value = "起禁用状态", required = true)
  private Boolean enabled = Boolean.TRUE;
  @ApiModelProperty(value = "仓位信息")
  public UCN warehouse;
  @ApiModelProperty(value = "是否默认", required = true)
  private Boolean isDefault = Boolean.FALSE;

  @ApiModelProperty(value = "收银机序列号")
  private String posSerialNum;
  @ApiModelProperty(value = "组织类型")
  private String orgType;
  @ApiModelProperty(value = "组织id")
  private String orgId;
}
