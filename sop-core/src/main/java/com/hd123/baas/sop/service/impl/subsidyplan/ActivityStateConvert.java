package com.hd123.baas.sop.service.impl.subsidyplan;

import com.hd123.baas.sop.service.api.price.pricepromotion.PricePromotionState;
import com.hd123.baas.sop.service.api.subsidyplan.ActivityState;
import com.hd123.baas.sop.service.api.subsidyplan.ActivityType;
import com.hd123.baas.sop.service.api.pms.activity.PromActivity;
import com.hd123.baas.sop.remote.rsmkhpms.entity.BasePricePromotionState;

/**
 * 三种活动状态转换：营销活动，价格促销，价格促销模型
 *
 * @author liuhaoxin
 */
public class ActivityStateConvert {

  public static ActivityState getActivityState(ActivityType activityType, String state) {
    if (ActivityType.PROMOTE_ACTIVITY.equals(activityType)) {
      return getPromoteActivityState(state);
    }
    if (ActivityType.PRICE_PROMOTION.equals(activityType)) {
      return getPriceProMotionState(state);
    }
    if (ActivityType.PRICE_PROMOTION_MODEL.equals(activityType)) {
      return getPricePromModeState(state);
    }
    return null;
  }

  private static ActivityState getPricePromModeState(String state) {
    if (BasePricePromotionState.INIT.name().equals(state)) {
      return ActivityState.INIT;
    }
    if (BasePricePromotionState.AUDITED.name().equals(state)) {
      return ActivityState.AUDITED;
    }
    if (BasePricePromotionState.TERMINATED.name().equals(state)) {
      return ActivityState.TERMINATED;
    }
    if (BasePricePromotionState.CANCELED.name().equals(state)) {
      return ActivityState.CANCELED;
    }
    if (BasePricePromotionState.SUBMITTED.name().equals(state)) {
      return ActivityState.CONFIRMED;
    }
    if (BasePricePromotionState.EFFECT.name().equals(state)) {
      return ActivityState.PUBLISHED;
    }
    if (BasePricePromotionState.EXPIRED.name().equals(state)) {
      return ActivityState.EXPIRED;
    }
    return null;
  }

  private static ActivityState getPriceProMotionState(String state) {
    if (PricePromotionState.INIT.name().equals(state)) {
      return ActivityState.INIT;
    }
    if (ActivityState.AUDITED.name().equals(state)) {
      return ActivityState.AUDITED;
    }
    if (PricePromotionState.TERMINATE.name().equals(state)) {
      return ActivityState.TERMINATED;
    }
    if (PricePromotionState.CANCELED.name().equals(state)) {
      return ActivityState.CANCELED;
    }
    if (PricePromotionState.CONFIRMED.name().equals(state)) {
      return ActivityState.CONFIRMED;
    }
    if (PricePromotionState.PUBLISHED.name().equals(state)) {
      return ActivityState.PUBLISHED;
    }
    if (PricePromotionState.EXPIRED.name().equals(state)) {
      return ActivityState.EXPIRED;
    }
    return null;
  }

  private static ActivityState getPromoteActivityState(String state) {
    /*
     * initial --> CONFIRMED; audited --> AUDITED,PUBLISHED,CONFIRMED; stopped -->
     * TERMINATED; canceled --> CANCELED;
     */
    if (PromActivity.State.initial.name().equals(state)) {
      return ActivityState.CONFIRMED;
    }
    if (PromActivity.State.audited.name().equals(state)) {
      return ActivityState.AUDITED;
    }
    if (PromActivity.FRONT_STATE_EFFECT.equals(state)) {
      return ActivityState.PUBLISHED;
    }
    if (PromActivity.FRONT_STATE_EXPIRED.equals(state)) {
      return ActivityState.EXPIRED;
    }
    if (PromActivity.State.stopped.name().equals(state)) {
      return ActivityState.TERMINATED;
    }
    if (PromActivity.State.canceled.name().equals(state)) {
      return ActivityState.CANCELED;
    }
    return null;
  }

}
