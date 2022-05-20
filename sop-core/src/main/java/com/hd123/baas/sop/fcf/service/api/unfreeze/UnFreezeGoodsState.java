package com.hd123.baas.sop.fcf.service.api.unfreeze;

/**
 * @author zhangweigang
 */
public enum UnFreezeGoodsState {

  /**
   * 待处理
   */
  unfreeze("待处理"),
  /**
   * 已确认
   */
  confirmed("已确认"),;

  public final String description;

  UnFreezeGoodsState(String description) {
    this.description = description;
  }
}
