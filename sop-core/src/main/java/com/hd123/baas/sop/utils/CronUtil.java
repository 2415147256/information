package com.hd123.baas.sop.utils;

import com.hd123.baas.sop.utils.entity.CronModel;
import com.hd123.baas.sop.utils.entity.CronType;
import com.qianfan123.baas.common.BaasException;
import org.apache.commons.lang3.StringUtils;

/**
 * 游佳威
 */
public class CronUtil {

  public static final String CONNECTOR = ":";
  public static final String SINGLE = "单次任务";
  public static final String DAILY = "每天";
  public static final String TASK_CYCLE = "任务周期";

  public static String MOUTH = "从%s号开始，每%s月执行一次";

  public static String EVERY_MOUTH = "每年%s月执行一次";
  public static String WEEKLY = "每周%s执行一次";
  public static String EVERY_DAILY = "每月从第%s天开始,每%s天执行一次";
  public static String ASSIGN_DAILY = "每月第%s天执行一次";

  /**
   *
   * 方法摘要：构建Cron表达式
   *
   * @param cronModel
   * @return String
   */
  public static String createCronExpression(CronModel cronModel) throws BaasException {
    StringBuffer cronExp = new StringBuffer("");

    if (null == cronModel.getCronType()) {
      System.out.println("执行周期未配置");// 执行周期未配置
    }

    if (cronModel.getCronType().name().equals(CronType.single.name())) {
      return CronType.single.name();
    }

    // 秒
    cronExp.append("0").append(" ");
    // 分
    cronExp.append("0").append(" ");
    // 小时
    cronExp.append("0").append(" ");

    // 每天
    if (CronType.daily.name().equals(cronModel.getCronType().name())) {
      cronExp.append("* ");// 日
      cronExp.append("* ");// 月
      cronExp.append("?");// 周
    }

    // 按每周
    else if (CronType.weekly.name().equals(cronModel.getCronType().name())) {
      // 一个月中第几天
      cronExp.append("? ");
      // 月份
      cronExp.append("* ");
      // 周
      Integer[] weeks = cronModel.getDayOf();
      for (int i = 0; i < weeks.length; i++) {
        if (i == 0) {
          cronExp.append(weeks[i]);
        } else {
          Integer week = weeks[i];
          if (week > 7) {
            throw new BaasException("日期选择错误，周数不可大于7");
          }
          cronExp.append(",").append(weeks[i]);
        }
      }

    }

    // 按每月
    else if (CronType.monthly.name().equals(cronModel.getCronType().name())) {
      // 一个月中的哪几天
      Integer[] days = cronModel.getDayOf();
      for (int i = 0; i < days.length; i++) {
        if (i == 0) {
          cronExp.append(days[i]);
        } else {
          cronExp.append(",").append(days[i]);
        }
      }
      // 月份
      cronExp.append(" * ");
      // 周
      cronExp.append("?");
    }

    return cronExp.toString();
  }

  /**
   * cron 表达式翻译成CronModel
   */
  public static CronModel translateToCronModel(String cronStr) {
    CronModel cronModel = new CronModel();
    if (StringUtils.isBlank(cronStr)) {
      throw new IllegalArgumentException("cron表达式为空");
    }
    if (cronStr.equals(CronType.single.name())) {
      cronModel.setCronType(CronType.single);
      return cronModel;
    }
    if (cronStr.equals("0 0 0 * * ?")) {
      cronModel.setCronType(CronType.daily);
      return cronModel;
    }
    String[] cronArray = cronStr.split(" ");
    // 表达式到年会有7段， 至少6段
    if (cronArray.length != 6 && cronArray.length != 7) {
      throw new IllegalArgumentException("cron表达式格式错误");
    }
    String dayCron = cronArray[3];
    String monthCron = cronArray[4];
    String weekCron = cronArray[5];

    // 解析周
    boolean hasWeekCron = false;
    if (!weekCron.equals("*") && !weekCron.equals("?")) {
      hasWeekCron = true;
      cronModel.setCronType(CronType.weekly);
      Integer[] dayOf = dayOfList(weekCron);
      cronModel.setDayOf(dayOf);
      return cronModel;

    }

    // 解析日
    if (!dayCron.equals("?") && !"*".equals(dayCron)) {
      if (hasWeekCron) {
        throw new IllegalArgumentException("表达式错误，不允许同时存在指定日和指定星期");
      }
      cronModel.setCronType(CronType.monthly);
      Integer[] dayOfList = dayOfList(dayCron);
      cronModel.setDayOf(dayOfList);
      return cronModel;
    }
    return cronModel;
  }

  /**
   * cron 表达式翻译成中文
   *
   * @param cronStr
   * @return
   */
  public static String translateToChinese(String cronStr) {

    if (StringUtils.isBlank(cronStr)) {
      throw new IllegalArgumentException("cron表达式为空");
    }
    if (cronStr.equals(CronType.single.name())) {
      return SINGLE;
    }
    StringBuilder result = new StringBuilder();
    result.append(TASK_CYCLE);
    if (cronStr.equals("0 0 0 * * ?")) {
      return result.append(CONNECTOR).append(DAILY).toString();
    }

    String[] cronArray = cronStr.split(" ");
    // 表达式到年会有7段， 至少6段
    if (cronArray.length != 6 && cronArray.length != 7) {
      throw new IllegalArgumentException("cron表达式格式错误");
    }
    String dayCron = cronArray[3];
    String monthCron = cronArray[4];
    String weekCron = cronArray[5];

    // 解析月
    if (!monthCron.equals("*") && !monthCron.equals("?")) {
      if (monthCron.contains("/")) {
        return result.append(CONNECTOR)
                .append(String.format(MOUTH, monthCron.split("/")[0], monthCron.split("/")[1]))
                .toString();
      } else {
        return result.append(CONNECTOR).append(String.format(EVERY_MOUTH, monthCron)).toString();
      }
    }

    // 解析周
    boolean hasWeekCron = false;
    if (!weekCron.equals("*") && !weekCron.equals("?")) {
      hasWeekCron = true;

      return result.append(CONNECTOR).append(String.format(WEEKLY, weeklyString(weekCron))).toString();

    }

    // 解析日
    if (!dayCron.equals("?") && !"*".equals(dayCron)) {
      if (hasWeekCron) {
        throw new IllegalArgumentException("表达式错误，不允许同时存在指定日和指定星期");
      }
      if (dayCron.contains("/")) {
        return result.append(CONNECTOR).append(String.format(EVERY_DAILY, dayCron.split("/")[0], dayCron.split("/")[1])).toString();

      } else {
        return result.append(CONNECTOR).append(String.format(ASSIGN_DAILY, dayCron)).toString();
      }
    }
    return result.toString();
  }

  public static Integer[] dayOfList(String weekCron) {
    String[] split = weekCron.split(",");
    Integer[] result = new Integer[split.length];
    for (int i = 0; i < split.length; i++) {
      result[i] = Integer.parseInt(split[i]);
    }
    return result;
  }

  public static String weeklyString(String weekCron) {
    StringBuffer result = new StringBuffer();
    char[] tmpArray = weekCron.toCharArray();
    for (char tmp : tmpArray) {
      switch (tmp) {
        case '1':
          result.append("日");
          break;
        case '2':
          result.append("一");
          break;
        case '3':
          result.append("二");
          break;
        case '4':
          result.append("三");
          break;
        case '5':
          result.append("四");
          break;
        case '6':
          result.append("五");
          break;
        case '7':
          result.append("六");
          break;
        default:
          result.append(tmp);
          break;
      }
    }
    return result.toString();
  }

  public static void main(String[] args) throws BaasException {
    CronModel cronModel = new CronModel();
    cronModel.setCronType(CronType.weekly);
    Integer[] dayOf = new Integer[] {
            3, 5, 7 };
    cronModel.setDayOf(dayOf);
    String cron = createCronExpression(cronModel);
    System.out.println("cron = " + cron);
    String s = translateToChinese(cron);
    System.out.println("s = " + s);
    CronModel cronModel1 = translateToCronModel(cron);
    System.out.println("cronModel1 = " + JsonUtil.objectToJson(cronModel1));

  }
}
