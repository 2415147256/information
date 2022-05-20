package com.hd123.baas.sop.fcf.controller.process;

public enum ProcessPlanMealTimeState {
  /**
   * 该类别、该餐段未处理完毕
   */
  todo("待处理"),
  /**
   * 该类别、该餐段未处理完毕
   */
  timeout("超时未处理"),
  /**
   * 该类别、该餐段所有商品均已加工
   */
  finished("已完成"),
  /**
   * // 该类别、该餐段中没有商品
   */
  noGoods("没有商品"),
  /**
   * 餐段时间未到？
   */
  canNotProcess("不可制作"),;

  public final String description;

  ProcessPlanMealTimeState(String description) {
    this.description = description;
  }
}