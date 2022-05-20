package com.hd123.baas.sop.service.api.explosivev2.sign;

/**
 * 爆品活动报名状态
 *
 * @author liuhaoxin
 * @since 2021-11-24
 */
public enum ExplosiveSignV2State {
  /** 待报名 */
  INIT,
  /** 已提交 */
  SUBMITTED,
  /** 已取消 */
  CANCELED,
  /** 已确认 */
  CONFIRMED
}
