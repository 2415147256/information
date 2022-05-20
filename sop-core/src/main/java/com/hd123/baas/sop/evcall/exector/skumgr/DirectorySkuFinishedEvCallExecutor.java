package com.hd123.baas.sop.evcall.exector.skumgr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.service.api.h6task.H6TaskService;
import com.hd123.baas.sop.service.api.h6task.H6TaskState;
import com.hd123.baas.sop.service.impl.shopsku.h6.H6DirectorySkuFileMgr;
import com.hd123.baas.sop.evcall.AbstractEvCallExecutor;
import com.hd123.baas.sop.utils.BaasJSONUtil;
import com.hd123.rumba.evcall.EvCallExecutionContext;
import com.qianfan123.baas.common.BaasException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zhengzewang on 2020/11/18.
 */
@Slf4j
@Component
public class DirectorySkuFinishedEvCallExecutor extends AbstractEvCallExecutor<ShopSkuFinishMsg> {

  public static final String SHOP_SKU_FINISHED_EXECUTOR_ID = DirectorySkuFinishedEvCallExecutor.class.getSimpleName();

  @Autowired
  private H6DirectorySkuFileMgr h6DirectorySkuFileMgr;
  @Autowired
  private H6TaskService h6TaskService;

  @Override
  @Tx
  protected void doExecute(ShopSkuFinishMsg msg, EvCallExecutionContext context) throws Exception {
    String tenant = msg.getTenant();
    String taskId = msg.getTaskId();
    try {
      h6DirectorySkuFileMgr.generateSkuFile(tenant, taskId, getSysOperateInfo());

      h6DirectorySkuFileMgr.uploadH6Task(tenant, taskId, getSysOperateInfo());
      // 更新状态
      h6TaskService.updateState(tenant, taskId, H6TaskState.FINISHED, getSysOperateInfo());
    } catch (Exception e) {
      log.error("ShopSkuFinishedEvCallExecutor错误", e);
      h6TaskService.logError(tenant, taskId, "门店商品生成csv文件", e, getSysOperateInfo());
      throw e;
    }
  }

  @Override
  protected ShopSkuFinishMsg decodeMessage(String msg) throws BaasException {
    log.info("收到ShopSkuFinishMsg:{}", msg);
    return BaasJSONUtil.safeToObject(msg, ShopSkuFinishMsg.class);
  }

}
