package com.hd123.baas.sop.config;

import com.qianfan123.baas.config.api.annotation.BcGroup;
import com.qianfan123.baas.config.api.annotation.BcKey;
import com.qianfan123.baas.config.api.annotation.BcOption;
import com.qianfan123.baas.config.api.field.ConfigEditor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@BcGroup(name = "消息服务配置")
public class MessageConfig {

  public static final String SOP = "SOP";
  public static final String FMS = "FMS";

  private static final String PREFIX = "msg.";

  @BcKey(
      name = "APP消息服务提供方（SOP|FMS，默认SOP）",
      description = "配置应用内消息的存储方，切换后历史消息需要迁移，如果需要改变，请和开发确认历史消息迁移脚本。",
      editor = ConfigEditor.OPTION, options = {
        @BcOption(value = SOP, name = SOP),
        @BcOption(value = FMS, name = FMS)
      })
  private String appMessageVendor = SOP;
}