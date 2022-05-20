package com.hd123.baas.sop.evcall.exector.fcf;

import com.hd123.baas.sop.fcf.service.api.unfreeze.UnFreezeGoodsState;
import com.hd123.baas.sop.fcf.service.api.unfreeze.UnFreezePlanOrderLine;
import com.hd123.baas.sop.fcf.service.api.unfreeze.UnFreezePlanOrderService;
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
public class UnfreezeEvCallExecutor extends AbstractEvCallExecutor<UnfreezeMsg> {
  public static final String UNFREEZE_EXECUTOR_ID = UnfreezeEvCallExecutor.class.getSimpleName();

  @Resource
  private UnFreezePlanOrderService unFreezePlanOrderService;
  @Resource
  private EvCallEventPublisher publisher;

  @Override
  protected void doExecute(UnfreezeMsg message, EvCallExecutionContext context) throws Exception {
    AliYunPush.doPush(message.getTenant(), message.getMessage(), message.getStoreCode());
    if (Type.cycle.equals(message.getType()) && message.getEndTime().after(new Date())
        && CollectionUtils.isNotEmpty(getLines(message))) {
      log.info("进入自旋解冻消息推送,当前时间={}，将在十分钟后推送，参数：message={},planId={},startTime={},endTime={},storeCode={}", new Date(),
          message.getMessage(), message.getPlanId(), message.getStartTime(), message.getEndTime(),
          message.getStoreCode());
      publisher.publishForNormal(UNFREEZE_EXECUTOR_ID, message, 600000L);
    }
  }

  @Override
  protected UnfreezeMsg decodeMessage(String msg) throws BaasException {
    log.info("收到UnfreezeMsg:{}", msg);
    return BaasJSONUtil.safeToObject(msg, UnfreezeMsg.class);
  }

  private List<UnFreezePlanOrderLine> getLines(UnfreezeMsg message) {
    return unFreezePlanOrderService.getProcessOrderLine(message.getTenant(), message.getPlanId(),
        UnFreezeGoodsState.unfreeze.name());
  }
}
