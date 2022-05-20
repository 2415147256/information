package com.hd123.baas.sop.evcall.exector.fms;

import com.hd123.baas.sop.evcall.AbstractEvCallExecutor;
import com.hd123.baas.sop.remote.fms.FmsClient;
import com.hd123.baas.sop.remote.fms.bean.FmsMsg;
import com.hd123.baas.sop.utils.BaasJSONUtil;
import com.hd123.rumba.commons.lang.Assert;
import com.hd123.rumba.evcall.EvCallExecutionContext;
import com.qianfan123.baas.common.BaasException;
import com.qianfan123.baas.common.http2.BaasResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FmsSendEvCallExecutor extends AbstractEvCallExecutor<FmsSendMsg> {
  public static final String FMS_SEND_EXECUTOR_ID = FmsSendEvCallExecutor.class.getSimpleName();

  @Autowired
  private FmsClient fmsClient;

  @Value("${baas-config.currentAppId:${spring.application.name}}")
  private String appId;

  @Override
  protected void doExecute(FmsSendMsg message, EvCallExecutionContext context) throws Exception {
    String templateId = message.getTemplateId();
    String tenant = message.getTenant();
    Assert.notNull(tenant,"租户");
    if(StringUtils.isBlank(templateId)){
      log.info("模板消息id为空,忽略处理");
      return;
    }

    FmsMsg fmsMsg = new FmsMsg();
    fmsMsg.setAppId(appId);
    fmsMsg.setTarget(message.getTarget());
    fmsMsg.setTemplateId(templateId);
    fmsMsg.setTemplateParams(message.getTemplateParams());
    try {
      BaasResponse rsp = fmsClient.send(tenant, fmsMsg);
      if (!rsp.success) {
        log.error("调用FMS发送消息失败,内容: <{}>, 错误原因: <{}>", BaasJSONUtil.safeToJson(fmsMsg), rsp.getMsg());
        return;
      }
      log.info("调用FMS发送消息成功,内容: <{}>", BaasJSONUtil.safeToJson(fmsMsg));
    } catch (Exception e) {
      try {
        log.error("调用FMS发送消息失败,内容: <{}>", BaasJSONUtil.safeToJson(fmsMsg), e);
      } catch (BaasException ex) {
        //
      }
    }
  }

  @Override
  protected FmsSendMsg decodeMessage(String arg) throws BaasException {
    log.info("收到FmsSendMsg:{}", arg);
    return BaasJSONUtil.safeToObject(arg, FmsSendMsg.class);
  }
}
