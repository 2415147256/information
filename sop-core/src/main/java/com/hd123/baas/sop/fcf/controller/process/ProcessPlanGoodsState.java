package com.hd123.baas.sop.fcf.controller.process;

/**
 * @author zhangweigang
 */
public enum ProcessPlanGoodsState {
  todo("待处理"), confirmed("已确认"),;

  public final String description;

  ProcessPlanGoodsState(String description) {
    this.description = description;
  }
}
