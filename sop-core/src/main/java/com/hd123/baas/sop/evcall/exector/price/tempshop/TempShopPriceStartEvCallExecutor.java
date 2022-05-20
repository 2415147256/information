package com.hd123.baas.sop.evcall.exector.price.tempshop;

import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.service.api.h6task.H6Task;
import com.hd123.baas.sop.service.api.h6task.H6TaskService;
import com.hd123.baas.sop.service.api.h6task.H6TaskState;
import com.hd123.baas.sop.service.api.price.tempshoppriceadjustment.TempShopPriceAdjustment;
import com.hd123.baas.sop.service.api.price.tempshoppriceadjustment.TempShopPriceManagerService;
import com.hd123.baas.sop.service.impl.price.shopprice.ShopPriceCalculateMgr;
import com.hd123.baas.sop.evcall.AbstractEvCallExecutor;
import com.hd123.baas.sop.utils.BaasJSONUtil;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.evcall.EvCallExecutionContext;
import com.qianfan123.baas.common.BaasException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author maodapeng
 * @Since
 */
@Service
@Slf4j
public class TempShopPriceStartEvCallExecutor extends AbstractEvCallExecutor<TempShopPriceAdjustmentStartMsg> {

  public static final String TEMP_SHOP_PRICE_START_EV_CALL = TempShopPriceStartEvCallExecutor.class.getSimpleName();
  @Autowired
  private H6TaskService h6TaskService;
  @Autowired
  private TempShopPriceManagerService tempShopPriceManagerService;
  @Autowired
  private ShopPriceCalculateMgr calculateMgr;

  @Override
  @Tx
  protected void doExecute(TempShopPriceAdjustmentStartMsg message, EvCallExecutionContext context) throws Exception {
    H6Task h6Tasks = h6TaskService.get(message.getTenant(), message.getTaskId());
    String tenant = message.getTenant();
    String taskId = message.getTaskId();
    if (h6Tasks == null) {
      log.info("任务不存在。");
      return;
    }
    if (h6Tasks.getState().equals(H6TaskState.FINISHED)) {
      log.info("任务已完成。");
      return;
    }
    TempShopPriceAdjustment tempShopPriceAdjustment = calculateMgr.tempShopPriceAdjustment(tenant, h6Tasks.getOrgId(),
        h6Tasks.getExecuteDate());
    if (tempShopPriceAdjustment == null) {
      log.info("没有有效的临时改价单，本次计算忽略");
      h6TaskService.updateState(tenant, taskId, H6TaskState.FINISHED, getSysOperateInfo());
      return;
    }
    String title = "";
    OperateInfo operateInfo = getSysOperateInfo();
    try {
      // 更新状态
      h6TaskService.updateState(tenant, taskId, H6TaskState.CONFIRMED, getSysOperateInfo());
      // 清理缓存
      tempShopPriceManagerService.deleteBefore(tenant, h6Tasks.getOrgId(), h6Tasks.getExecuteDate());
      //
      calculateMgr.calculateTempShopPriceAdjustment(tenant, h6Tasks.getExecuteDate(), h6Tasks.getUuid(),
          tempShopPriceAdjustment.getUuid(), operateInfo);
      // 过期无效的试算单
      calculateMgr.cancelTempShopAdjustment(tenant, h6Tasks.getOrgId(), h6Tasks.getExecuteDate(), getSysOperateInfo());

    } catch (Exception e) {
      String msg = title + " 发生异常";
      log.error(msg, e);
      h6TaskService.logError(tenant, taskId, title, e, operateInfo);
      throw e;
    }

  }

  @Override
  protected TempShopPriceAdjustmentStartMsg decodeMessage(String msg) throws BaasException {
    log.info("收到TempShopPriceAdjustmentStartMsg:{}", msg);
    return BaasJSONUtil.safeToObject(msg, TempShopPriceAdjustmentStartMsg.class);
  }
}
