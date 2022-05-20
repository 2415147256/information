package com.hd123.baas.sop.service.api.subsidyplan;

/**
 * @author liuhaoxin
 */

public enum TerminateType {
  SUBSIDY, // 仅终止补贴
  SUBSIDY_ACTIVITY, // 终止补贴并立即终止相关活动
  SUBSIDY_NEXT_DAY_ACTIVITY // 终止补贴并次日终止相关活动
}
