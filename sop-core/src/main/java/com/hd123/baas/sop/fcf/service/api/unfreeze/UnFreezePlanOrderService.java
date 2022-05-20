package com.hd123.baas.sop.fcf.service.api.unfreeze;

import com.hd123.baas.sop.fcf.controller.unfreeze.BDailyUnFreezeTime;
import com.hd123.baas.sop.fcf.controller.unfreeze.UnFreezeGoodsRequest;
import com.qianfan123.baas.common.BaasException;

import java.util.Date;
import java.util.List;

/**
 * @author zhangweigang
 */
public interface UnFreezePlanOrderService {

  /**
   * 获取门店当日解冻计划单
   */
  UnFreezePlanOrder get(String tenant, Date date, String storeCode);

  UnFreezePlanOrder getByStoreGid(String tenant, String storeGid);

  /**
   * 根据uuid获取解冻计划
   */
  UnFreezePlanOrder getByUuid(String tenant, String uuid);

  /**
   * 解冻商品
   */
  UnFreezePlanOrder unFreezeGoods(String tenant, UnFreezeGoodsRequest request) throws BaasException;

  void batchSave(String tenant, List<UnFreezePlanOrder> planOrders, String operator) throws BaasException;

  DailyUnFreezeTime getUnFreezeTimes(String tenant);

  int updateUnFreezeTime(String tenant, BDailyUnFreezeTime dailyUnFreezeTime);

  List<UnFreezePlanOrderLine> getProcessOrderLine(String tenant, String planId, String state);
}
