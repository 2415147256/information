package com.hd123.baas.sop.config;

import com.qianfan123.baas.config.api.annotation.BcGroup;
import com.qianfan123.baas.config.api.annotation.BcKey;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@BcGroup(appId = "sop-service", name = "门店任务")
public class TaskGroupJobConfig {
  private static final String PREFIX = "shop_task.";

  public static final String CORN_EXPRESSION = PREFIX + "cronExpression";

  @BcKey(name = "执行cron表达式")
  public String cronExpression = "0 * 1 * * ?";

  @BcKey(name = "一次更新job数")
  public int size = 50;
}
