package com.hd123.baas.sop.service.api.electricscale;

import java.util.Date;

import com.hd123.baas.sop.service.api.TenantEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ElecScaleState extends TenantEntity {
  // 电子秤id
  private String electronicScaleUuid;
  // 电子秤状态
  private String state;
  // 传秤时间
  private Date createTime;
  // 备注
  private String remark;

  private ElecScaleStateType type;
}
