package com.hd123.baas.sop.service.api.taskplan.template;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Author maodapeng
 * @Since
 */
@Service
@Slf4j
public class ShopAttendanceCheckTaskPlan implements TemplateClsTaskPlan{
  @Override
  public String getName() {
    return "门店考勤情况12：00前确认";
  }

  @Override
  public String getDescription() {
    return "此项任务需要店员在盖雅系统上完成";
  }

  @Override
  public boolean wordNeeded() {
    return false;
  }

  @Override
  public boolean imageNeeded() {
    return false;
  }

  @Override
  public void check(String tenant, Object checkInfo) throws Exception {

  }
}
