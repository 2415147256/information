package com.hd123.baas.sop.evcall.exector.h6task;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import com.hd123.baas.sop.evcall.exector.price.tempshop.TempShopPriceAdjustmentStartMsg;
import com.hd123.baas.sop.evcall.exector.price.tempshop.TempShopPriceStartEvCallExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.service.api.h6task.H6Task;
import com.hd123.baas.sop.service.api.h6task.H6TaskService;
import com.hd123.baas.sop.service.api.h6task.H6TaskState;
import com.hd123.baas.sop.service.api.h6task.H6TaskType;
import com.hd123.baas.sop.service.impl.price.shopprice.h6.H6PriceFileMgr;
import com.hd123.baas.sop.evcall.AbstractEvCallExecutor;
import com.hd123.baas.sop.evcall.EvCallEventPublisher;
import com.hd123.baas.sop.evcall.exector.price.ShopPriceEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.price.ShopPriceStartEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.price.ShopPriceStartMsg;
import com.hd123.baas.sop.evcall.exector.price.ShopPriceTaskMsg;
import com.hd123.baas.sop.utils.BaasJSONUtil;
import com.hd123.baas.sop.utils.JsonUtil;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.entity.Operator;
import com.hd123.rumba.evcall.EvCallExecutionContext;
import com.qianfan123.baas.common.BaasException;

import lombok.extern.slf4j.Slf4j;

/**
 * 第（五）步
 *
 * @author zhengzewang on 2020/11/24.
 */
@Slf4j
@Component
public class H6TaskDeliveredEvCallExecutor extends AbstractEvCallExecutor<H6TaskDeliveredMsg> {

  public static final String EXECUTOR_ID = H6TaskDeliveredEvCallExecutor.class.getSimpleName();

  @Autowired
  private H6PriceFileMgr h6PriceFileMgr;
  @Autowired
  private H6TaskService h6TaskService;
  @Autowired
  private EvCallEventPublisher publisher;

  @Override
  @Tx
  protected void doExecute(H6TaskDeliveredMsg msg, EvCallExecutionContext context) throws Exception {
    String tenant = msg.getTenant();
    String taskId = msg.getPk();

    log.info("第五步：收到租户 {} h6任务 {} 已生成文件事件", tenant, taskId);
    H6Task h6Task = h6TaskService.get(tenant, taskId);
    h6PriceFileMgr.uploadH6Task(tenant, taskId, getOperateInfo());
    // 更新状态
    h6TaskService.updateState(tenant, taskId, H6TaskState.FINISHED, getOperateInfo());

    // 查询当天是否还有其他的问题，如果有的话，唤醒
    List<H6Task> unFinishTasks = h6TaskService.getByDate(tenant, h6Task.getOrgId(), h6Task.getType(),
        msg.getExecuteDate());
    if (CollectionUtils.isEmpty(unFinishTasks)) {
      log.info("无待完成的任务");
      if (h6Task.getType() == H6TaskType.PRICE) {
        publishTempShopPriceEvCall(tenant, h6Task.getOrgId(), h6Task.getExecuteDate());
      }
      return;
    }
    for (H6Task task : unFinishTasks) {
      if (H6TaskState.INIT.equals(task.getState())) {
        log.info("准备唤醒下个任务，taskId={},type={}", task.getUuid(), task.getType());
        if (task.getType() == H6TaskType.PRICE) {
          publishShopPriceEvCall(tenant, task.getUuid(), task.getExecuteDate());
        } else if (task.getType() == H6TaskType.TEMP_SHOP) {
          TempShopPriceAdjustmentStartMsg startMsg = new TempShopPriceAdjustmentStartMsg();
          startMsg.setTenant(tenant);
          startMsg.setTaskId(h6Task.getUuid());
          publisher.publishForNormal(TempShopPriceStartEvCallExecutor.TEMP_SHOP_PRICE_START_EV_CALL, startMsg);
        }
      }
    }
  }

  private void publishShopPriceEvCall(String tenant, String taskId, Date executeDate) {
    ShopPriceStartMsg startMsg = new ShopPriceStartMsg();
    startMsg.setTenant(tenant);
    startMsg.setExecuteDate(executeDate);
    startMsg.setTaskId(taskId);
    publisher.publishForNormal(ShopPriceStartEvCallExecutor.EXECUTOR_ID, startMsg);
  }

  private void publishTempShopPriceEvCall(String tenant, String orgId, Date executeDate) {
    ShopPriceTaskMsg msg = new ShopPriceTaskMsg();
    msg.setTenant(tenant);
    msg.setOrgId(orgId);
    msg.setExecuteDate(executeDate);
    msg.setTaskType(H6TaskType.TEMP_SHOP);
    log.info("试算模型完成后触发到店价调整模型：{}", JsonUtil.objectToJson(msg));
    publisher.publishForNormal(ShopPriceEvCallExecutor.SHOP_PRICE_CREATE_EXECUTOR_ID, msg);
  }

  private OperateInfo getOperateInfo() {
    OperateInfo operateInfo = new OperateInfo();
    Operator operator = new Operator();
    operateInfo.setTime(new Date());
    operator.setId("system");
    operator.setFullName("系统用户");
    operator.setNamespace("system");
    operateInfo.setOperator(operator);
    return operateInfo;
  }

  @Override
  protected H6TaskDeliveredMsg decodeMessage(String msg) throws BaasException {
    log.info("收到H6TaskDeliveredMsg:{}", msg);
    return BaasJSONUtil.safeToObject(msg, H6TaskDeliveredMsg.class);
  }
}
