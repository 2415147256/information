package com.hd123.baas.sop.config;

import com.qianfan123.baas.config.api.annotation.BcGroup;
import com.qianfan123.baas.config.api.annotation.BcKey;

import lombok.Getter;
import lombok.Setter;

/**
 * @author zhengzewang on 2020/11/9.
 * 
 */
@Getter
@Setter
@BcGroup(name = "任务消息配置")
public class TaskMessageConfig {

  @BcKey(name = "日结任务页面path")
  private String dailyTaskPage = "/pages/dailyTask/DailyTask";
  @BcKey(name = "日常任务页面path")
  private String usualTaskPage = "/pages/usualTask/UsualTask";
  @BcKey(name = "门店巡检页面path")
  private String checkTaskPage = "/pagesSupervision/supervise/SelectShop";
  @BcKey(name = "抢单任务页面path")
  private String grabOrderPage = "/pagesShopManageSub/missioncenter/GrabSingle";
  @BcKey(name = "指派任务页面path")
  private String assignableShopTaskPage = "/pagesShopManageSub/missioncenter/MissioncenterDetail";
  @BcKey(name = "普通任务到期提醒跳转路径path")
  private String assignableShopTaskRemindPage = "/pagesShopManageSub/missioncenter/MissioncenterDetail";

}
