package com.hd123.baas.sop.evcall.exector.explosivev2;

import com.hd123.baas.sop.service.api.explosivev2.ExplosiveActionV2;
import com.hd123.baas.sop.service.api.explosivev2.ExplosiveV2Service;
import com.hd123.baas.sop.evcall.AbstractEvCallExecutor;
import com.hd123.baas.sop.utils.BaasJSONUtil;
import com.hd123.rumba.evcall.EvCallExecutionContext;
import com.qianfan123.baas.common.BaasException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author shenmin
 */
@Slf4j
@Component
public class ExplosivePrepareOnOffEvCallExecutor extends AbstractEvCallExecutor<ExplosivePrepareOnMsg> {

  public static final String EXECUTOR_ID = ExplosivePrepareOnOffEvCallExecutor.class.getSimpleName();

  @Autowired
  private ExplosiveV2Service explosiveV2Service;

  @Override
  protected void doExecute(ExplosivePrepareOnMsg msg, EvCallExecutionContext context) throws Exception {
    String tenant = msg.getTenant();
    String uuid = msg.getUuid();

    if (StringUtils.isEmpty(tenant) || StringUtils.isEmpty(uuid) || msg.getOperateInfo() == null) {
      log.warn("关键数据为空，忽略");
      return;
    }

    // 获取action
    ExplosiveActionV2 action = msg.getAction();
    if (ExplosiveActionV2.ON.equals(action)) {
      explosiveV2Service.on(tenant, uuid, msg.getOperateInfo());
    }
    if (ExplosiveActionV2.OFF.equals(action)) {
      explosiveV2Service.off(tenant, uuid, msg.getOperateInfo());
    }
  }

  @Override
  protected ExplosivePrepareOnMsg decodeMessage(String msg) throws BaasException {
    log.info("收到Msg:{}", msg);
    return BaasJSONUtil.safeToObject(msg, ExplosivePrepareOnMsg.class);
  }
}
