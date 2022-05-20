package com.hd123.baas.sop.evcall;

import com.hd123.baas.sop.service.api.pms.activity.PromActivity;
import com.hd123.baas.sop.service.api.activity.event.PromActivityAutoAuditEvent;
import com.hd123.baas.sop.service.api.activity.PromActivityService;
import com.hd123.rumba.commons.biz.entity.Operator;
import com.hd123.rumba.evcall.EvCallExecutionContext;
import com.hd123.rumba.evcall.EvCallExecutor;
import com.hd123.rumba.evcall.EvCallManager;
import com.hd123.spms.commons.json.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Slf4j
@Component(PromActivityAutoAuditEvCall.BEAN_ID)
public class PromActivityAutoAuditEvCall implements EvCallExecutor {
  public static final String BEAN_ID = "sop-service.PromActivityAutoAuditEvCall";

  @Autowired
  private PromActivityService promActivityService;
  @Autowired
  private EvCallManager evCallManager;

  @PostConstruct
  public void init() {
    evCallManager.addExecutor(this, BEAN_ID);
  }

  @EventListener
  public void processPromRuleGeneralBillEvent(PromActivityAutoAuditEvent event) {
    PromActivity activity = event.getActivity();
    if (activity == null) {
      return;
    }
    PromActivityAutoAuditMsg msg = new PromActivityAutoAuditMsg();
    msg.setTenant(activity.getTenant());
    msg.setUuid(activity.getUuid());
    evCallManager.submit(BEAN_ID, JsonUtil.objectToJson(msg));
  }

  @Override
  public void execute(String json, @NotNull EvCallExecutionContext evCallExecutionContext) {
    PromActivityAutoAuditMsg msg = JsonUtil.jsonToObject(json, PromActivityAutoAuditMsg.class);
    promActivityService.autoAudit(msg.getTenant(), msg.getUuid(), getSysOperateInfo());
  }

  protected com.hd123.rumba.commons.biz.entity.OperateInfo getSysOperateInfo() {
    com.hd123.rumba.commons.biz.entity.OperateInfo operateInfo = new com.hd123.rumba.commons.biz.entity.OperateInfo();
    Operator operator = new Operator();
    operateInfo.setTime(new Date());
    operator.setId("system");
    operator.setFullName("系统用户");
    operator.setNamespace("system");
    operateInfo.setOperator(operator);
    return operateInfo;
  }

}
