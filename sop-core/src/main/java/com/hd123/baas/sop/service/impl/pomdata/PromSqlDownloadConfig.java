package com.hd123.baas.sop.service.impl.pomdata;

import com.qianfan123.baas.config.api.annotation.BcGroup;
import com.qianfan123.baas.config.api.annotation.BcKey;
import lombok.Data;

@Data
@BcGroup(name = "促销数据下发任务设置")
public class PromSqlDownloadConfig {
  private static final String PREFIX = "promSql.download.";

  @BcKey(name = "启用")
  private boolean enabled = true;
}
