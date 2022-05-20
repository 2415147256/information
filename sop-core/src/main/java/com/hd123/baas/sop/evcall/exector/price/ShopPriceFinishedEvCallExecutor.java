package com.hd123.baas.sop.evcall.exector.price;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.service.api.h6task.H6TaskService;
import com.hd123.baas.sop.service.impl.price.shopprice.h6.H6PriceFileMgr;
import com.hd123.baas.sop.evcall.AbstractEvCallExecutor;
import com.hd123.baas.sop.utils.BaasJSONUtil;
import com.hd123.pms.util.ElapsedTimer;
import com.hd123.rumba.evcall.EvCallExecutionContext;
import com.qianfan123.baas.common.BaasException;

import lombok.extern.slf4j.Slf4j;

/**
 * 第（四）步
 *
 * @author zhengzewang on 2020/11/18.
 */
@Slf4j
@Component
public class ShopPriceFinishedEvCallExecutor extends AbstractEvCallExecutor<ShopPriceFinishedMsg> {

  public static final String SHOP_PRICE_FINISHED_CREATE_EXECUTOR_ID = ShopPriceFinishedEvCallExecutor.class
      .getSimpleName();

  @Autowired
  private H6PriceFileMgr h6PriceFileMgr;
  @Autowired
  private H6TaskService h6TaskService;

  @Override
  @Tx
  protected void doExecute(ShopPriceFinishedMsg msg, EvCallExecutionContext context) throws Exception {
    String tenant = msg.getTenant();
    String shop = msg.getShop();
    String taskId = msg.getTaskId();
    log.info("第四步：门店任务已完成，准备生成并打包文件");
    try {
      ElapsedTimer timer = ElapsedTimer.getThreadInstance();
      timer.clear();
      h6PriceFileMgr.generateFile(tenant, taskId, shop, getSysOperateInfo());
      this.timer.accumulate(timer);
    } catch (Exception e) {
      log.error("ShopPriceFinishedEvCallExecutor错误", e);
      h6TaskService.logError(tenant, taskId, "门店价格计算完毕，生成csv文件", e, getSysOperateInfo());
      throw e;
    }
  }

  @Override
  protected ShopPriceFinishedMsg decodeMessage(String msg) throws BaasException {
    log.info("收到ShopPriceFinishedMsg:{}", msg);
    return BaasJSONUtil.safeToObject(msg, ShopPriceFinishedMsg.class);
  }

}
