package com.hd123.baas.sop.config;

import com.qianfan123.baas.config.api.annotation.BcGroup;
import com.qianfan123.baas.config.api.annotation.BcKey;

import lombok.Getter;
import lombok.Setter;

/**
 * @author guyahui
 * @date 2021/5/21 13:41
 */
@Getter
@Setter
@BcGroup(name = "任务交接消息配置")
public class ShopTaskTransferMessageConfig {

  @BcKey(name = "巡检任务交接跳转path")
  private String shopTaskTransferPath = "/pagesSupervision/supervise/ConnectDetail";
  @BcKey(name = "普通任务交接跳转path")
  private String assignableShopTaskTransferPath = "/pagesShopManageSub/missioncenter/ExecuteTask";
  @BcKey(name = "普通任务交接同意或拒绝后的跳转路径")
  private String assignableAcceptOrRefusePath = "/pagesShopManageSub/missioncenter/MissioncenterDetail";
}
