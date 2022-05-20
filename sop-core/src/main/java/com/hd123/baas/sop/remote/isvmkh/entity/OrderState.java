package com.hd123.baas.sop.remote.isvmkh.entity;

public class OrderState {
  /**
   * 1待付款
   */
  public static final int CONFIRMED = 1;
  /**
   * 2待发货
   */
  public static final int PAID = 2;
  /**
   * 3待取货
   */
  public static final int DELIVERED = 3;
  /**
   * 4已完成
   */
  public static final int FINISH = 4;
  /**
   * 11超时未支付自动取消
   */
  public static final int AUTO_CANCEL = 11;
  /**
   * 12已支付用户主动取消
   */
  public static final int INIT_CANCEL = 12;
}
