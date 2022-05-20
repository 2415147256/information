package com.hd123.baas.sop.job.timed;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShopTaskGroupRemindJobParam {
  private String tenant;
  private String shopTaskGroupId;
}
