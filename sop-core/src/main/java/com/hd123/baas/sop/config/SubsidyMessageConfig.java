package com.hd123.baas.sop.config;

import com.qianfan123.baas.config.api.annotation.BcGroup;
import com.qianfan123.baas.config.api.annotation.BcKey;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author maodapeng
 * @Since
 */
@Getter
@Setter
@BcGroup(name = "补贴计划消息")
public class SubsidyMessageConfig {
  @BcKey(name = "创建补贴计划title")
  private String createTitle = "新到补贴计划通知";
  @BcKey("创建补贴计划Text")
  private String createText = "您新收到X补贴计划，快去看看吧";

  @BcKey(name = "调整补贴计划title")
  private String modifyTitle = "补贴计划调整通知";
  @BcKey(name = "调整补贴计划text")
  private String modifyText = "您的补贴计划X发生了调整，快去看看吧";

  @BcKey(name = "终止补贴计划title")
  private String terminateTitle = "补贴计划终止通知";
  @BcKey(name = "终止补贴计划text")
  private String terminateText = "您的补贴计划X被终止了，快去看看吧";

  @BcKey("跳转地址url")
  private String jumpUrl = "";
  @BcKey("消息图片url")
  private String picUrl = "";

}
