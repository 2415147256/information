package com.hd123.baas.sop.service.impl.message;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.hd123.baas.config.core.BaasConfigClient;
import com.hd123.baas.sop.remote.fms.FmsClient;
import com.hd123.baas.sop.remote.fms.bean.FmsMsg;
import com.hd123.rumba.commons.lang.Assert;
import com.qianfan123.baas.common.BaasException;
import com.qianfan123.baas.common.http2.BaasResponse;
import com.qianfan123.baas.common.util.JSONUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zhengzewang on 2020/11/23.
 */
@Component
@Slf4j
public class FmsMgr {

  @Autowired
  private BaasConfigClient configClient;
  @Autowired
  private FmsClient fmsClient;
  @Value("${baas-config.currentAppId:${spring.application.name}}")
  private String appId;

  public void sendAndSave(String tenant, String topic, FmsMsg msg) {
    Assert.notNull(msg, "msg");
    if (StringUtils.isBlank(msg.getTemplateId())) {
      log.warn("未配置模板ID，忽略发送");
      return;
    }
    msg.setAppId(appId);
    try {
      BaasResponse rsp = fmsClient.sendAndSave(tenant, topic, msg);
      if (!rsp.success) {
        log.error("调用FMS发送消息失败,内容: <{}>, 错误原因: <{}>", JSONUtil.safeToJson(msg), rsp.getMsg());
        return;
      }
      log.info("调用FMS发送消息成功,内容: <{}>", JSONUtil.safeToJson(msg));
    } catch (Exception e) {
      try {
        log.error("调用FMS发送消息失败,内容: <{}>", JSONUtil.safeToJson(msg), e);
      } catch (BaasException ex) {
        //
      }
    }
  }

}
