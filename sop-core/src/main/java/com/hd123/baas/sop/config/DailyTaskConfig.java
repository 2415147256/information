package com.hd123.baas.sop.config;

import java.util.Arrays;
import java.util.stream.Collectors;

import com.hd123.baas.sop.service.api.taskplan.template.TemplateCls;
import com.qianfan123.baas.config.api.annotation.BcGroup;
import com.qianfan123.baas.config.api.annotation.BcKey;

import lombok.Getter;
import lombok.Setter;


/**
 * @author zhengzewang on 2020/11/16.
 */
@Getter
@Setter
@BcGroup(name = "日结任务")
public class DailyTaskConfig {

  public static final String PREFIX = "dailyTask.";

  public static final String EARLIEST_FINISH_TIME = PREFIX + "earliestFinishTime";

  public static final String ENABLE_DAILY_CLEAR = PREFIX + "enableDailyClear";

  public static final String TIPS = PREFIX +"tips";

  @BcKey(name = "固定模板")
  private String templateCls = Arrays.stream(TemplateCls.values()).map(t -> t.name()).collect(Collectors.joining(","));

  @BcKey(name = "任务组完成最早时间 时分秒")
  private String earliestFinishTime = "20:30:30";

  @BcKey(name = "当前租户是否开启日清功能")
  private boolean enableDailyClear = false;

  @BcKey(name = "是否忽略检查门店任务组状态，默认false")
  public boolean ignoreShopTaskGroupState = false;

  @BcKey(name = "是否忽略检查门店任务组状态，默认(请在规定时间内（21：00-23：30）完成日结，否则第二天无法查看BI报表等)")
  private String tips = "请在规定时间内（21：00-23：30）完成日结，否则第二天无法查看BI报表等";
}
