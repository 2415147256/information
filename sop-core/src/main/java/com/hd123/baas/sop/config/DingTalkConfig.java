package com.hd123.baas.sop.config;

import com.qianfan123.baas.config.api.annotation.BcGroup;
import com.qianfan123.baas.config.api.annotation.BcKey;
import com.qianfan123.baas.config.api.field.ConfigEditor;

import lombok.Data;

@Data
@BcGroup(appId = "sop-service", name = "钉钉配置")
public class DingTalkConfig {
  private static final String PREFIX = "dingTalk.";

  @BcKey(name = "钉订小程序appKey", editor = ConfigEditor.TEXT)
  private String appKey = "dingacgv9ybvfizjxv9s";
  @BcKey(name = "钉订小程序appSecret", editor = ConfigEditor.TEXT)
  private String appSecret = "k9EjKxGa4z_EBg2i7FccQetge7werVtl3OKHrFdHSgazNu_6B5mi7thBGht0gpBG";
  @BcKey(name = "钉订小程序agentId", editor = ConfigEditor.TEXT)
  private String agentId = "961184390";
  @BcKey(name = "钉订url:获取token", editor = ConfigEditor.TEXT)
  private String acquireTokenUrl = "https://oapi.dingtalk.com/gettoken";
  @BcKey(name = "钉订url:根据mobile获取userId", editor = ConfigEditor.TEXT)
  private String acquireUserIdByMobileUrl = "https://oapi.dingtalk.com/topapi/v2/user/getbymobile";
  @BcKey(name = "钉订url:发送消息url", editor = ConfigEditor.TEXT)
  private String sendMessageUrl = "https://oapi.dingtalk.com/topapi/message/corpconversation/asyncsend_v2";
}