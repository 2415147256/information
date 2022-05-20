package com.hd123.baas.sop.fcf.controller.process;

public enum ProcessPlanOrderState {
  /**
   * 还未开始制作
   */
  todo("待处理"),
  /**
   * 正在制作
   */
  doing("已经开始制作，尚未制作完成"),
  /**
   * 已完成
   */
  confirmed("已完成"),;

  public final String description;

  ProcessPlanOrderState(String description) {
    this.description = description;
  }
}