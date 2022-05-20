package com.hd123.baas.sop.config;

import com.qianfan123.baas.config.api.annotation.BcGroup;
import com.qianfan123.baas.config.api.annotation.BcKey;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author guyahui
 * @date 2021/5/17 13:22
 */
@Slf4j
@Data
@BcGroup(appId = "sop-service", name = "门店任务")
public class TaskPlanJobConfig {
  private static final String PREFIX = "task_plan_job.";

  @BcKey(name = "任务下发时间范围，默认30天")
  public int days = 30;
}
