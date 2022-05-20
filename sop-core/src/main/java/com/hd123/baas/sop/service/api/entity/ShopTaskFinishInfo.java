package com.hd123.baas.sop.service.api.entity;

import com.hd123.rumba.commons.biz.entity.OperateInfo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShopTaskFinishInfo {
  public String Feedback;
  public OperateInfo finishInfo;
  public String finishAppid;
}
