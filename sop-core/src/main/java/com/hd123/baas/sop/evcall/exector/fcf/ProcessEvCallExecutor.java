package com.hd123.baas.sop.evcall.exector.fcf;

import com.hd123.baas.sop.fcf.controller.process.ProcessPlanGoodsState;
import com.hd123.baas.sop.fcf.service.api.process.ProcessPlanOrderLine;
import com.hd123.baas.sop.fcf.service.api.process.ProcessPlanOrderService;
import com.hd123.baas.sop.evcall.AbstractEvCallExecutor;
import com.hd123.baas.sop.evcall.EvCallEventPublisher;
import com.hd123.baas.sop.utils.BaasJSONUtil;
import com.hd123.rumba.evcall.EvCallExecutionContext;
import com.qianfan123.baas.common.BaasException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class ProcessEvCallExecutor extends AbstractEvCallExecutor<ProcessMsg> {
  public static final String PROCESS_EXECUTOR_ID = ProcessEvCallExecutor.class.getSimpleName();
  @Resource
  private ProcessPlanOrderService processPlanOrderService;
  @Resource
  private EvCallEventPublisher publisher;

  @Override
  protected void doExecute(ProcessMsg message, EvCallExecutionContext context) throws Exception {
    AliYunPush.doPush(message.getTenant(), message.getMessage(), message.getStoreCode());
    if (Type.cycle.equals(message.getType()) && new Date().before(message.getEndTime())
        && CollectionUtils.isNotEmpty(getLines(message))) {
      log.info(
          "进入自旋制作消息推送,当前时间={}，将在十分钟后推送，参数：message={},planUuid={},mealTimeId={},startTime={},endTime={},storeCode={}",
          new Date(), message.getMessage(), message.getPlanUuid(), message.getMealTimeId(), message.getStartTime(),
          message.getEndTime(), message.getStoreCode());
      publisher.publishForNormal(PROCESS_EXECUTOR_ID, message, 600000L);
    }
  }

  @Override
  protected ProcessMsg decodeMessage(String msg) throws BaasException {
    log.info("收到ProcessMsg:{}", msg);
    return BaasJSONUtil.safeToObject(msg, ProcessMsg.class);
  }

  private List<ProcessPlanOrderLine> getLines(ProcessMsg message) {
    return processPlanOrderService.getProcessOrderLine(message.getTenant(), message.getPlanUuid(),
        message.getMealTimeId(), ProcessPlanGoodsState.todo.name());
  }
}
