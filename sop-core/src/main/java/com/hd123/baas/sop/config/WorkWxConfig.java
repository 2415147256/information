package com.hd123.baas.sop.config;

import com.qianfan123.baas.config.api.annotation.BcGroup;
import com.qianfan123.baas.config.api.annotation.BcKey;
import com.qianfan123.baas.config.api.annotation.BcOption;
import com.qianfan123.baas.config.api.field.ConfigEditor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author W.J.H.7
 */
@Getter
@Setter
@BcGroup(name = "企微配置")
public class WorkWxConfig {
  public static final String FIX = "fix";
  public static final String EXCHANGE = "exchange";

  private static final String PREFIX = "workwx.";
  @BcKey(name = "服务地址")
  private String serverUrl = "https://qyapi.weixin.qq.com";
  @BcKey(name = "企业Id")
  private String corpId;
  @BcKey(name = "应用Id")
  private String agentId;
  @BcKey(name = "应用秘钥")
  private String corpSecret;
  @BcKey(name = "审批应用-Token")
  private String approvalToken;
  @BcKey(name = "encodingAESKey", editor = ConfigEditor.TEXT, description = "配置企业微信应用接受消息服务器配置时输入的encodingAESKey")
  private String encodingAESKey;

  @BcKey(name = "审批应用申请-用户策略，固定=fix，转换=exchange", editor = ConfigEditor.OPTION, options = {
      @BcOption(value = FIX, name = FIX),
      @BcOption(value = EXCHANGE, name = EXCHANGE)
  })
  private String approvalUserIdStrategy = FIX;
  @BcKey(name = "审批应用申请-固定用户ID")
  private String approvalUserId;
  @BcKey(name = "审批应用-模板ID，格式：[{\"approvalType\":\"feedback\",\"approvalTemplateId\":\"XXX\"}]", editor = ConfigEditor.JSON)
  private String approvalTemplateJson;
  @BcKey(name = "企业微信拒绝reason")
  private String rejectReason = "企业微信拒绝";

}
